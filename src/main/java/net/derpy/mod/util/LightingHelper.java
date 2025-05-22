package net.derpy.mod.util;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Lighting Helper class - there is a version of this in the Thematic mod just not the latest version
 */
public final class LightingHelper {
    // Map to track fake light blocks with their expiration time
    private static final Map<BlockPos, Long> fakeBlocks = new HashMap<>();
    // Track the last position where we sent a light block update
    private static BlockPos lastLightPos = null;
    // How long (in ticks) fake light blocks should remain visible before refresh
    private static final int LIGHT_BLOCK_DURATION = 90;
    // How often to refresh existing light blocks (in ticks)
    private static final int REFRESH_INTERVAL = 20;
    // Maximum number of fake light blocks to keep at once
    private static final int MAX_LIGHT_BLOCKS = 6;
    // The block state to simulate (light block at maximum level)
    private static final BlockState LIGHT_BLOCK_STATE = Blocks.LIGHT.getDefaultState().with(Properties.LEVEL_15, 15);
    // Maximum distance to search for an air block
    private static final int MAX_SEARCH_DISTANCE = 3;
    // Tick counter for refreshing lights
    private static int refreshTicks = 0;

    public static void tick(Entity entity, boolean shouldGlow) {
        // Skip client-side processing
        if (entity == null || entity.getWorld() == null || entity.getWorld().isClient) {
            return;
        }

        ServerWorld world = (ServerWorld) entity.getWorld();

        if (shouldGlow) {
            // Get current position
            BlockPos currentPos = entity.getBlockPos();

            // Check if we need to place a new light block
            boolean needNewLight = lastLightPos == null;

            // Or if the player has moved too far from the last light
            if (!needNewLight && lastLightPos.getManhattanDistance(currentPos) > 1) {
                needNewLight = true;
            }

            if (needNewLight) {
                // Find a suitable position for the light block
                BlockPos lightPos = findValidLightPosition(world, currentPos);

                // Only send packet if we found a valid position
                if (lightPos != null) {
                    if (entity instanceof PlayerEntity playerEntity)
                        sendFakeLightPacket(playerEntity, lightPos, world);
                }
            }
            // Even if we don't need a new light, periodically refresh existing lights
            else if (++refreshTicks >= REFRESH_INTERVAL) {
                refreshTicks = 0;

                // Refresh the current light to ensure it stays visible
                if (lastLightPos != null) {

                    if (entity instanceof PlayerEntity playerEntity)
                        sendFakeLightPacket(playerEntity, lastLightPos, world, true);
                }
            }

            // Clean up expired fake light blocks
            cleanupExpiredFakeBlocks(world);

        } else {
            // Ability is inactive, clean up ALL fake light blocks
            cleanupAllFakeBlocks(world);
            lastLightPos = null;
            refreshTicks = 0;
        }
    }

    /**
     * Checks if the specified position is valid for placing a light block.
     * Valid positions are those where the block is air.
     */
    private static boolean isValidLightPosition(ServerWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir();
    }

    /**
     * Finds a valid position for placing a light block near the specified position.
     * Starts at the specified position and then searches nearby if necessary.
     * Returns null if no valid position is found.
     */
    private static BlockPos findValidLightPosition(ServerWorld world, BlockPos centerPos) {
        // First, check the center position
        if (isValidLightPosition(world, centerPos)) {
            return centerPos;
        }

        // If the center position is not valid, search nearby positions
        // Start with positions at the same Y level
        for (int distance = 1; distance <= MAX_SEARCH_DISTANCE; distance++) {
            for (int xOffset = -distance; xOffset <= distance; xOffset++) {
                for (int zOffset = -distance; zOffset <= distance; zOffset++) {
                    // Skip checking the center again
                    if (xOffset == 0 && zOffset == 0) continue;

                    // Check all three possible Y positions at each X/Z coordinate
                    for (int yOffset = -1; yOffset <= 1; yOffset++) {
                        BlockPos pos = centerPos.add(xOffset, yOffset, zOffset);
                        if (isValidLightPosition(world, pos)) {
                            return pos;
                        }
                    }
                }
            }
        }

        // If no valid position is found, return null
        return null;
    }

    /**
     * Sends a packet to create a fake light block at the specified position.
     */
    private static void sendFakeLightPacket(PlayerEntity playerEntity, BlockPos pos, ServerWorld world) {
        sendFakeLightPacket(playerEntity, pos, world, false);
    }

    /**
     * Sends a packet to create a fake light block at the specified position.
     * @param isRefresh If true, this is just refreshing an existing light block
     */
    private static void sendFakeLightPacket(PlayerEntity playerEntity, BlockPos pos, ServerWorld world, boolean isRefresh) {
        // Create a block update packet for this position
        BlockUpdateS2CPacket packet = new BlockUpdateS2CPacket(pos, LIGHT_BLOCK_STATE);

        // Send the packet to all players tracking this chunk
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            player.networkHandler.sendPacket(packet);
        }

        // Also send to the player themselves if they're in a different chunk
        if (playerEntity instanceof ServerPlayerEntity serverPlayer && !PlayerLookup.tracking(world, pos).contains(serverPlayer)) {
            serverPlayer.networkHandler.sendPacket(packet);
        }

        // Only update tracking if this isn't just a refresh
        if (!isRefresh) {
            // Record the position and when it should expire
            long expirationTime = world.getTime() + LIGHT_BLOCK_DURATION;
            fakeBlocks.put(pos, expirationTime);

            // Update the last position
            lastLightPos = pos;

            // If we have too many fake blocks, remove the oldest ones
            if (fakeBlocks.size() > MAX_LIGHT_BLOCKS) {
                BlockPos oldestPos = null;
                long oldestTime = Long.MAX_VALUE;

                for (Map.Entry<BlockPos, Long> entry : fakeBlocks.entrySet()) {
                    if (entry.getValue() < oldestTime) {
                        oldestTime = entry.getValue();
                        oldestPos = entry.getKey();
                    }
                }

                if (oldestPos != null && !oldestPos.equals(pos)) {
                    sendRemoveFakeLightPacket(world, oldestPos);
                    fakeBlocks.remove(oldestPos);
                }
            }
        } else {
            // If this is a refresh, update the expiration time
            if (fakeBlocks.containsKey(pos)) {
                fakeBlocks.put(pos, world.getTime() + LIGHT_BLOCK_DURATION);
            }
        }
    }

    /**
     * Cleans up expired fake light blocks.
     */
    private static void cleanupExpiredFakeBlocks(ServerWorld world) {
        long currentTime = world.getTime();
        Iterator<Map.Entry<BlockPos, Long>> iterator = fakeBlocks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Long> entry = iterator.next();
            if (currentTime >= entry.getValue()) {
                sendRemoveFakeLightPacket(world, entry.getKey());
                iterator.remove();

                // If this was the last light position, null it out
                if (entry.getKey().equals(lastLightPos)) {
                    lastLightPos = null;
                }
            }
        }
    }

    /**
     * Sends a packet to remove a fake light block.
     */
    private static void sendRemoveFakeLightPacket(ServerWorld world, BlockPos pos) {
        // Get the real block at this position
        BlockState realState = world.getBlockState(pos);

        // Create a block update packet to restore the real block
        BlockUpdateS2CPacket packet = new BlockUpdateS2CPacket(pos, realState);

        // Send the packet to all players tracking this chunk
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            player.networkHandler.sendPacket(packet);
        }
    }

    /**
     * Cleans up all fake light blocks.
     */
    private static void cleanupAllFakeBlocks(ServerWorld world) {
        for (BlockPos pos : new ArrayList<>(fakeBlocks.keySet())) {
            sendRemoveFakeLightPacket(world, pos);
        }
        fakeBlocks.clear();
    }

    public static void cleanupLightBlocks(LivingEntity livingEntity) {
        if (livingEntity == null || livingEntity.getWorld() == null || livingEntity.getWorld().isClient) {
            return;
        }

        // Clean up any remaining fake light blocks
        cleanupAllFakeBlocks((ServerWorld) livingEntity.getWorld());
        lastLightPos = null;
        refreshTicks = 0;
    }
}

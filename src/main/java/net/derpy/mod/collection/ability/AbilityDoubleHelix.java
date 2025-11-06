package net.derpy.mod.collection.ability;

import bond.thematic.api.callbacks.LivingTickCallback;
import bond.thematic.api.registries.armors.ability.ThematicAbility;
import bond.thematic.api.registries.armors.ability.DefaultOptions;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityDoubleHelix extends ThematicAbility {

    private static final double RADIUS = 1.5;
    private static final double HEIGHT = 2.5;
    private static final int ROTATIONS = 3;
    private static final double SPEED = 0.1;
    private static final double Y_OFFSET = 0.0;

    private final Map<UUID, Boolean> activeStates = new HashMap<>();
    private boolean wasLeftClicking = false;

    public AbilityDoubleHelix(String doubleHelix) {
        super("double_helix", AbilityType.PRESS);
    }

    @Override
    public void serverEvents() {
        super.serverEvents();

        LivingTickCallback.EVENT.register(living -> {
            if (!(living instanceof PlayerEntity player)) return;
            if (!player.getWorld().isClient) return;
            if (!activeStates.getOrDefault(player.getUuid(), false)) return;

            spawnHelixParticles(player);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerEntity player = client.player;
            if (player == null) return;
            if (!activeStates.getOrDefault(player.getUuid(), false)) return;

            KeyBinding rightClick = client.options.useKey;
            KeyBinding leftClick = client.options.attackKey;

            if (rightClick.isPressed()) {
                spawnPunchFire(player);
            }

            boolean isLeftClicking = leftClick.isPressed();
            if (isLeftClicking && !wasLeftClicking) {
                spawnPunchFire(player);
            }
            wasLeftClicking = isLeftClicking;
        });
    }

    private void spawnHelixParticles(PlayerEntity player) {
        Vec3d center = player.getPos().add(0, Y_OFFSET, 0);
        int ticks = player.age;
        double progress = (ticks % 200) / 200.0;
        double yUp = center.y + progress * HEIGHT;
        double yDown = center.y + HEIGHT - progress * HEIGHT;
        World world = player.getWorld();

        double angle1 = progress * ROTATIONS * 2 * Math.PI + ticks * SPEED;
        double x1 = center.x + Math.cos(angle1) * RADIUS;
        double z1 = center.z + Math.sin(angle1) * RADIUS;
        world.addParticle(ParticleTypes.FLAME, x1, yUp, z1, 0, 0, 0);

        double angle2 = angle1 + Math.PI;
        double x2 = center.x + Math.cos(angle2) * RADIUS;
        double z2 = center.z + Math.sin(angle2) * RADIUS;
        world.addParticle(ParticleTypes.FLAME, x2, yUp, z2, 0, 0, 0);

        double angle3 = angle1 + Math.PI / 2;
        double x3 = center.x + Math.cos(angle3) * RADIUS;
        double z3 = center.z + Math.sin(angle3) * RADIUS;
        world.addParticle(ParticleTypes.FLAME, x3, yDown, z3, 0, 0, 0);

        double angle4 = angle3 + Math.PI;
        double x4 = center.x + Math.cos(angle4) * RADIUS;
        double z4 = center.z + Math.sin(angle4) * RADIUS;
        world.addParticle(ParticleTypes.FLAME, x4, yDown, z4, 0, 0, 0);
    }

    private void spawnPunchFire(PlayerEntity player) {
        World world = player.getWorld();
        Vec3d look = player.getRotationVecClient();
        Vec3d origin = player.getCameraPosVec(1.0F).add(look.multiply(0.8));

        double step = 0.5;
        double maxDistance = 40.0;
        int particleCount = (int)(maxDistance / step);

        for (int i = 0; i < particleCount; i++) {
            Vec3d pos = origin.add(look.multiply(i * step));
            if (!world.isAir(BlockPos.ofFloored(pos))) {
                break;
            }
            if (!world.getOtherEntities(player, player.getBoundingBox().expand(look.multiply(i * step).length())).isEmpty()) {
                break;
            }
            world.addParticle(ParticleTypes.FLAME,
                    pos.x, pos.y, pos.z,
                    look.x * 0.15, look.y * 0.15, look.z * 0.15);
        }
    }

    @Override
    public void press(PlayerEntity player, net.minecraft.item.ItemStack stack) {
        super.press(player, stack);
        if (isBlocked(player)) return;
        if (getCooldown(player) > 0) return;

        boolean currentlyActive = activeStates.getOrDefault(player.getUuid(), false);
        activeStates.put(player.getUuid(), !currentlyActive);
        setActive(player, this.getId(), cooldown(player), !currentlyActive);
    }

    @Override
    public DefaultOptions getDefaultData() {
        return new DefaultOptions.Builder()
                .cooldown(10)
                .build();
    }
}

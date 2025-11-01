package net.derpy.mod.collection.ability;

import bond.thematic.api.abilities.ultimate.AbilityBatSwarm;
import net.derpy.mod.entity.custom.DroneEntity;
import net.derpy.mod.entity.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import bond.thematic.mod.Thematic;
import net.minecraft.entity.Entity;

public class AbilityDroneSwarm extends AbilityBatSwarm {

    public AbilityDroneSwarm(String id) {
        super(id); // Call the parent constructor
    }

    protected void spawnMob(PlayerEntity playerEntity) {
        if (!playerEntity.getWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) playerEntity.getWorld();

            // Spawn 5 drones
            for (int i = 0; i < 5; i++) {
                DroneEntity drone = new DroneEntity(ModEntities.DRONE, playerEntity.getWorld());
                drone.updatePosition(
                        playerEntity.getX() + (Thematic.random.nextFloat() * 2.0F - 1.0F),
                        playerEntity.getY() + Thematic.random.nextFloat() + 1.0,
                        playerEntity.getZ() + (Thematic.random.nextFloat() * 2.0F - 1.0F)
                );
                serverWorld.spawnEntity(drone);
            }
        }
    }
}

package net.derpy.mod.entity;

import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.entity.custom.AstromechEntity;
import net.derpy.mod.entity.custom.C3POEntity;
import net.derpy.mod.entity.custom.DroneEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<AstromechEntity> ASTROMECH = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Derpyslightsabers.MOD_ID, "astromech"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, AstromechEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.2f))
                    .build()
    );

    public static final EntityType<C3POEntity> C3PO = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Derpyslightsabers.MOD_ID, "c3po"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, C3POEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
                    .build()
    );

    public static final EntityType<DroneEntity> DRONE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Derpyslightsabers.MOD_ID, "drone"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DroneEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 0.8f))
                    .build()
    );

    public static void registerModEntities() {
        Derpyslightsabers.LOGGER.info("Registering Mod Entities for " + Derpyslightsabers.MOD_ID);

        FabricDefaultAttributeRegistry.register(ASTROMECH, AstromechEntity.createAstromechAttributes());
        FabricDefaultAttributeRegistry.register(C3PO, C3POEntity.createC3POAttributes());
        FabricDefaultAttributeRegistry.register(DRONE, DroneEntity.createDroneAttributes());
    }
}

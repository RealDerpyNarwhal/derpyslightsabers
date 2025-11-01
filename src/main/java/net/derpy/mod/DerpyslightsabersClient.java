package net.derpy.mod;

import net.derpy.mod.item.ModItems;
import net.derpy.mod.entity.ModEntities;
import net.derpy.mod.entity.client.AstromechRenderer;
import net.derpy.mod.entity.client.C3PORenderer;
import net.derpy.mod.entity.client.DroneRenderer; // <-- import this
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import software.bernie.geckolib.GeckoLib;

public class DerpyslightsabersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Initialize GeckoLib
        GeckoLib.initialize();

        // Register custom item renderer for Anakin's Lightsaber
        ModItems.ANAKINS_LIGHTSABER.getRenderProvider().get();

        // Register custom entity renderers
        EntityRendererRegistry.register(ModEntities.ASTROMECH, AstromechRenderer::new);
        EntityRendererRegistry.register(ModEntities.C3PO, C3PORenderer::new);
        EntityRendererRegistry.register(ModEntities.DRONE, DroneRenderer::new); // <-- Drone renderer
    }
}

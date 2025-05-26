package net.derpy.mod;

import net.derpy.mod.item.ModItems;
import net.derpy.mod.entity.ModEntities;
import net.derpy.mod.entity.client.AstromechRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import software.bernie.geckolib.GeckoLib;

public class DerpyslightsabersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Initialize GeckoLib
        GeckoLib.initialize();

        // Register custom item render provider
        ModItems.ANAKINS_LIGHTSABER.getRenderProvider().get();

        // Register custom entity renderers
        EntityRendererRegistry.register(ModEntities.ASTROMECH, AstromechRenderer::new);
    }
}

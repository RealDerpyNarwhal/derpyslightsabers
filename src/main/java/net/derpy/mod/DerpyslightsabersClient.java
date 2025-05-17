package net.derpy.mod;

import net.derpy.mod.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import software.bernie.geckolib.GeckoLib;

public class DerpyslightsabersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GeckoLib.initialize();
        // Optional: manually force render provider creation
        ModItems.ANAKINSLIGHTSABER.getRenderProvider().get();
    }
}

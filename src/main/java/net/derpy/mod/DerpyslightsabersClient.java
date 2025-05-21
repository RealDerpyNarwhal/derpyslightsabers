package net.derpy.mod;

import net.derpy.mod.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import software.bernie.geckolib.GeckoLib;

public class DerpyslightsabersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GeckoLib.initialize();
        ModItems.ANAKINS_LIGHTSABER.getRenderProvider().get();
    }
}

package net.derpy.mod.mixin;


import bond.thematic.mod.item.Constructs;
import net.derpy.mod.api.compat.pointblank.DerpyGunRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = com.vicmatskiv.pointblank.registry.GunRegistry.class, remap = false)
public class ExtensionRegistryMixin {
    @Inject(at = @At("RETURN"), method = "init", remap = false)
    private static void init(CallbackInfo info) {
        DerpyGunRegistry.init();
    }
}
package net.derpy.mod.mixin;

import bond.thematic.api.util.ThematicHelper;
import com.vicmatskiv.pointblank.item.FireModeInstance;
import com.vicmatskiv.pointblank.item.GunItem;
import com.vicmatskiv.pointblank.item.HurtingItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.animatable.GeoItem;

@Mixin(
        value = {GunItem.class},
        remap = false
)
public abstract class GunItemMixin extends HurtingItem implements GeoItem {
    public GunItemMixin(Item.Settings properties, HurtingItem.Builder<?> builder) {
        super(properties, builder);
    }

    @Inject(
            at = {@At("RETURN")},
            method = {"requestReloadFromServer"},
            remap = false,
            cancellable = true
    )
    public void thematic$overrideRequest(PlayerEntity player, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        ThematicHelper.overrideRequestReloadFromServer(player, itemStack, cir);
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"canReloadGun"},
            remap = false,
            cancellable = true
    )
    public void thematic$overrideReload(ItemStack gunStack, PlayerEntity player, FireModeInstance fireModeInstance, CallbackInfoReturnable<Integer> cir) {
        Integer overrideValue = ThematicHelper.overrideCanReloadGun(gunStack, fireModeInstance);
        if (overrideValue != null) {
            cir.setReturnValue(overrideValue);
        }

    }

    @Inject(
            at = {@At("HEAD")},
            method = {"reloadGun"},
            remap = false,
            cancellable = true
    )
    public void thematic$overrideAmmo(ItemStack gunStack, PlayerEntity player, FireModeInstance fireModeInstance, CallbackInfoReturnable<Integer> cir) {
        Integer overrideValue = ThematicHelper.overrideReloadGun(gunStack, player, fireModeInstance);
        if (overrideValue != null) {
            cir.setReturnValue(overrideValue);
        }

    }
}

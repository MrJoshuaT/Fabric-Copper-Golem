package com.mrjoshuat.coppergolem.mixin;

import com.mrjoshuat.coppergolem.handler.HoneycombItemHandler;

import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {
    @Inject(
        method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void useOnButtonBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        HoneycombItemHandler.useOnButtonBlock(context, info);
    }
}

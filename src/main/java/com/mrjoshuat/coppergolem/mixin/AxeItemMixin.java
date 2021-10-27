package com.mrjoshuat.coppergolem.mixin;

import com.mrjoshuat.coppergolem.handler.AxeItemHandler;

import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class AxeItemMixin {
    @Inject(
        method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void useOnButtonBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        AxeItemHandler.useOnButtonBlock(context, info);
    }
}

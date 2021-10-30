package com.mrjoshuat.coppergolem.mixin;

import com.mrjoshuat.coppergolem.handler.HoneycombItemHandler;

import net.minecraft.block.BlockState;
import net.minecraft.item.HoneycombItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {
    private static BlockState lastBlockState;
    @ModifyVariable(
            method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;",
            at = @At("STORE"),
            ordinal = 0
    )
    private BlockState storeLastBlockState(BlockState blockState) { return lastBlockState = blockState; }

    @Inject(
        method = "getWaxedState(Lnet/minecraft/block/BlockState;)Ljava/util/Optional;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void getWaxedButtonState(CallbackInfoReturnable<Optional<BlockState> > info) {
        var updatedState = HoneycombItemHandler.getButtonWaxedState(lastBlockState);
        if (updatedState.isPresent()) {
            info.setReturnValue(updatedState);
            info.cancel();
        }
     }
}

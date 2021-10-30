package com.mrjoshuat.coppergolem.mixin;

import com.google.common.collect.BiMap;
import com.mrjoshuat.coppergolem.block.OxidizableButton;

import com.mrjoshuat.coppergolem.handler.HoneycombItemHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {
    private BlockState lastBlockState;
    @ModifyVariable(
            method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;",
            at = @At("STORE"),
            ordinal = 0
    )
    private BlockState storeLastBlockState(BlockState blockState) { return lastBlockState = blockState; }

    @ModifyVariable(
        method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;",
        at = @At("STORE"),
        ordinal = 1
    )
    private Optional<BlockState> getButtonStateIfBlockNotPresent(Optional<BlockState> blockState) {
        if (blockState.isPresent())
            return blockState;
        return OxidizableButton.getDecreasedOxidationState(lastBlockState);
    }

    @ModifyVariable(
            method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;",
            at = @At("STORE"),
            ordinal = 2
    )
    private Optional<BlockState> getButtonUnwaxedStateIfBlockNotPresent(Optional<BlockState> blockState) {
        if (blockState.isPresent())
            return blockState;
        return Optional.ofNullable((Block)((BiMap) HoneycombItemHandler.WAXED_TO_UNWAXED_BUTTON_BLOCKS.get())
            .get(lastBlockState.getBlock())).map((block) -> block.getStateWithProperties(lastBlockState));
    }
}

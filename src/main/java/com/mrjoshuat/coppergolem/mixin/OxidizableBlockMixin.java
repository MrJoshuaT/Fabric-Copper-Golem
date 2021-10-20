package com.mrjoshuat.coppergolem.mixin;

import com.mrjoshuat.coppergolem.ModInit;
import com.mrjoshuat.coppergolem.OxidizableBlockCallback;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.OxidizableBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(OxidizableBlock.class)
public class OxidizableBlockMixin {
    @Inject(
            method = "randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V",
            at = @At(value = "INVOKE"),
            cancellable = true
    )
    private void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo info) {
        //ModInit.LOGGER.info("Random tick happened ...");
        OxidizableBlockCallback.EVENT.invoker().randomTick();
    }
}

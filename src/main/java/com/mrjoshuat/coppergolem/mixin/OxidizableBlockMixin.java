package com.mrjoshuat.coppergolem.mixin;

import com.mrjoshuat.coppergolem.OxidizableBlockCallback;

import net.minecraft.block.BlockState;
import net.minecraft.block.OxidizableBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OxidizableBlock.class)
public class OxidizableBlockMixin {
    @Inject(
        method = "randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)V",
        at = @At(value = "INVOKE"),
        cancellable = true
    )
    private void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo info) {
        // TODO: this should be removed, need a better way
        OxidizableBlockCallback.EVENT.invoker().randomTick(state, world, pos, random);
    }
}

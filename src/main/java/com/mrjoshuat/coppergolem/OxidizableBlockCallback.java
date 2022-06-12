package com.mrjoshuat.coppergolem;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public interface OxidizableBlockCallback {
    Event<OxidizableBlockCallback> EVENT = EventFactory.createArrayBacked(OxidizableBlockCallback.class,
        (listeners) -> (BlockState state, ServerWorld world, BlockPos pos, Random random) -> {
            for (OxidizableBlockCallback listener : listeners) {
                ActionResult result = listener.randomTick(state, world, pos, random);

                if(result != ActionResult.PASS) {
                    return result;
                }
            }

            return ActionResult.PASS;
        });

    ActionResult randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
}

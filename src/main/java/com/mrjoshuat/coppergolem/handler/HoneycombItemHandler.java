package com.mrjoshuat.coppergolem.handler;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mrjoshuat.coppergolem.ModInit;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Supplier;

public class HoneycombItemHandler {
    public static final Supplier<BiMap<Block, Block>> UNWAXED_TO_WAXED_BUTTON_BLOCKS = Suppliers.memoize(() -> {
        BiMap<Block, Block> map = HashBiMap.create();
        map.put(ModInit.COPPER_BUTTON, ModInit.WAXED_COPPER_BUTTON);
        map.put(ModInit.EXPOSED_COPPER_BUTTON, ModInit.WAXED_EXPOSED_COPPER_BUTTON);
        map.put(ModInit.WEATHERED_COPPER_BUTTON, ModInit.WAXED_WEATHERED_COPPER_BUTTON);
        map.put(ModInit.OXIDIZED_COPPER_BUTTON, ModInit.WAXED_OXIDIZED_COPPER_BUTTON);
        return map;
    });
    public static final Supplier<BiMap<Block, Block>> WAXED_TO_UNWAXED_BUTTON_BLOCKS = Suppliers.memoize(() -> ((BiMap)UNWAXED_TO_WAXED_BUTTON_BLOCKS.get()).inverse());

    public static void useOnButtonBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        /* NOTE: This is mostly taken from AxeItem with minor changes.
         * Required due to needing to call getButtonWaxedState with our new buttons
         */

        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        Optional<BlockState> updatedState = getButtonWaxedState(blockState);
        if (!updatedState.isPresent())
            return;

        PlayerEntity playerEntity = context.getPlayer();
        ItemStack itemStack = context.getStack();
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
        }

        itemStack.decrement(1);
        world.setBlockState(blockPos, updatedState.get(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
        world.syncWorldEvent(playerEntity, WorldEvents.BLOCK_WAXED, blockPos, 0);

        info.setReturnValue(ActionResult.success(world.isClient));
    }


    private static Optional<BlockState> getButtonWaxedState(BlockState state) {
        return Optional.ofNullable((Block)((BiMap) HoneycombItemHandler.UNWAXED_TO_WAXED_BUTTON_BLOCKS.get()).get(state.getBlock())).map((block) -> {
            return block.getStateWithProperties(state);
        });
    }
}

package com.mrjoshuat.coppergolem.handler;

import com.google.common.collect.BiMap;
import com.mrjoshuat.coppergolem.block.OxidizableButton;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Consumer;

public class AxeItemHandler {
    public static void useOnButtonBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        /* NOTE: This is mostly taken from AxeItem with minor changes.
         * Required due to needing to call OxidizableButton.getDecreasedOxidationState
         */
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity playerEntity = context.getPlayer();
        BlockState blockState = world.getBlockState(blockPos);
        Optional<BlockState> buttonState = OxidizableButton.getDecreasedOxidationState(blockState);
        Optional<BlockState> unwaxedState = Optional.ofNullable((Block)((BiMap) HoneycombItemHandler.WAXED_TO_UNWAXED_BUTTON_BLOCKS.get())
                .get(blockState.getBlock())).map((block) -> block.getStateWithProperties(blockState));

        // Check we can do something before attempting more
        if (!buttonState.isPresent() && !unwaxedState.isPresent()) {
            return;
        }

        Optional<BlockState> finalState = Optional.empty();
        if (buttonState.isPresent()) {
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.syncWorldEvent(playerEntity, WorldEvents.BLOCK_SCRAPED, blockPos, 0);
            finalState = buttonState;
        } else if (unwaxedState.isPresent()) {
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.syncWorldEvent(playerEntity, WorldEvents.WAX_REMOVED, blockPos, 0);
            finalState = unwaxedState;
        }

        ItemStack itemStack = context.getStack();
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
        }

        world.setBlockState(blockPos, (BlockState)finalState.get(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
        if (playerEntity != null) {
            itemStack.damage(1, (LivingEntity)playerEntity, (Consumer)((p) -> {
                ((PlayerEntity)p).sendToolBreakStatus(context.getHand());
            }));
        }

        info.setReturnValue(ActionResult.success(world.isClient));
    }
}

package com.mrjoshuat.coppergolem.block;

import com.mrjoshuat.coppergolem.handler.RedstonePowerHandler;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class WaxedOxidizableButtonBlock extends AbstractButtonBlock {
    private Oxidizable.OxidizationLevel level;

    public WaxedOxidizableButtonBlock(Oxidizable.OxidizationLevel level, Settings settings) {
        super(false, settings);
        this.level = level;
    }

    @Override
    protected SoundEvent getClickSound(boolean powered) {
        // or to use BLOCK_COPPER_STEP?
        return powered ? SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON : SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF;
    }



    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? RedstonePowerHandler.getRedstonePower(level) : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) && getDirection(state) == direction ? RedstonePowerHandler.getRedstonePower(level) : 0;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (player.isCreative()) {
            return;
        }
        var itemStack = new ItemStack(state.getBlock(), 1);
        var itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, itemStack);
        world.spawnEntity(itemEntity);
    }
}

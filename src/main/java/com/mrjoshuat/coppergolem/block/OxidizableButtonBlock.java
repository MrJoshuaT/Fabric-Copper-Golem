package com.mrjoshuat.coppergolem.block;

import net.minecraft.block.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class OxidizableButtonBlock extends AbstractButtonBlock implements OxidizableButton {
    private final Oxidizable.OxidizationLevel oxidizationLevel;

    public OxidizableButtonBlock(Oxidizable.OxidizationLevel oxidizationLevel, AbstractBlock.Settings settings) {
        super(false, settings);
        this.oxidizationLevel = oxidizationLevel;
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    public boolean hasRandomTicks(BlockState state) {
        return OxidizableButton.getIncreasedOxidationBlock(state.getBlock()).isPresent();
    }

    @Override
    public Oxidizable.OxidizationLevel getDegradationLevel() {
        return this.oxidizationLevel;
    }

    @Override
    protected SoundEvent getClickSound(boolean powered) {
        // or to use BLOCK_COPPER_STEP?
        return powered ? SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON : SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? getRedstonePower(getDegradationLevel()) : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) && getDirection(state) == direction ? getRedstonePower(getDegradationLevel()) : 0;
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

    public static int getRedstonePower(Oxidizable.OxidizationLevel level) {
        return 15 - (3 * switch (level) {
            case UNAFFECTED -> 0;
            case EXPOSED -> 1;
            case WEATHERED -> 2;
            case OXIDIZED -> 3;
        });
    }
}

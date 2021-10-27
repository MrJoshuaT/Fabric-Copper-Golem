package com.mrjoshuat.coppergolem.block;

import com.mrjoshuat.coppergolem.OxidizableBlockCallback;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

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

    protected SoundEvent getClickSound(boolean powered) {
        // or to use BLOCK_COPPER_STEP?
        return powered ? SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON : SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF;
    }
}

package com.mrjoshuat.coppergolem.block;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class WaxedOxidizableButtonBlock extends AbstractButtonBlock {
    public WaxedOxidizableButtonBlock(Settings settings) {
        super(false, settings);
    }

    @Override
    protected SoundEvent getClickSound(boolean powered) {
        // or to use BLOCK_COPPER_STEP?
        return powered ? SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON : SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF;
    }
}

package com.mrjoshuat.coppergolem.handler;

import net.minecraft.block.Oxidizable;

public class RedstonePowerHandler {
    public static int getRedstonePower(Oxidizable.OxidizationLevel level) {
        return 15 - (3 * switch (level) {
            case UNAFFECTED -> 0;
            case EXPOSED -> 1;
            case WEATHERED -> 2;
            case OXIDIZED -> 3;
        });
    }
}

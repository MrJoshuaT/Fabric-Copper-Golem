package com.mrjoshuat.coppergolem.entity.goals;

import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;

import net.minecraft.entity.ai.goal.Goal;

public class SpinHeadGoal extends Goal {
    private CopperGolemEntity entity;

    public SpinHeadGoal(CopperGolemEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        return entity.getRandom().nextFloat() > 0.995F;
    }

    @Override
    public boolean shouldContinue() {
        return this.entity.getLastHeadSpinTicks() > 0;
    }

    @Override
    public void tick() {
        var ticks  = this.entity.getLastHeadSpinTicks();
        if (ticks > 0) {
            this.entity.setLastButtonPressTicks(ticks--);
        } else {
            this.entity.setLastButtonPressTicks(100);
        }
    }
}

package com.mrjoshuat.coppergolem.entity.goals;

import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;

import net.minecraft.entity.ai.goal.Goal;

public class RodWiggleGoal extends Goal {
    private CopperGolemEntity entity;

    public RodWiggleGoal(CopperGolemEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        if (entity.getBlockTarget() != null)
            return false;
        return entity.getRandom().nextFloat() > 0.995F;
    }

    @Override
    public boolean shouldContinue() {
        return this.entity.getLastRodWiggleTicks() > 0;
    }

    @Override
    public void tick() {
        var ticks  = this.entity.getLastRodWiggleTicks();
        if (ticks > 0) {
            this.entity.setLastRodWiggleTicksTicks(--ticks);
        } else {
            this.entity.setLastRodWiggleTicksTicks(20);
        }
    }
}

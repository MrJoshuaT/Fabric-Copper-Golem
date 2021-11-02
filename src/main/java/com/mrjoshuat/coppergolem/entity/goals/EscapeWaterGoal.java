package com.mrjoshuat.coppergolem.entity.goals;

import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;

import net.minecraft.entity.ai.NoWaterTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class EscapeWaterGoal extends Goal {
    private CopperGolemEntity entity;

    public EscapeWaterGoal(CopperGolemEntity entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    public boolean canStart() {
        return entity.isTouchingWater();
    }

    public boolean shouldContinue() {
        return entity.getNavigation().isFollowingPath();
    }

    public void start() {
        Vec3d vec3d = this.getRandomLocation();
        if (vec3d != null) {
            entity.getNavigation().startMovingAlong(entity.getNavigation().findPathTo((new BlockPos(vec3d)), 1), 0.3D);
        }
    }

    @Nullable
    private Vec3d getRandomLocation() {
        return NoWaterTargeting.find(entity, 8, 8, -1, entity.getRotationVec(0.0F), 1.5707963705062866D);
    }
}

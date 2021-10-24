package com.mrjoshuat.coppergolem.entity.goals;

import com.mrjoshuat.coppergolem.ModInit;
import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class PressButtonGoal extends Goal {
    private final CopperGolemEntity entity;
    public float lastTick = 0;

    public PressButtonGoal(CopperGolemEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() { return this.entity.getBlockTarget() != null && this.entity.getRandom().nextFloat() > 0.6f; }

    public boolean shouldContinue() { return !this.canStart(); }

    public void start() {
        var target = this.entity.getBlockTarget();
        entity.getNavigation().startMovingTo(target.getX(), target.getY(), target.getZ(), 0.4D);
    }

    public void tick() {
        if (this.lastTick > 0) {
            --this.lastTick;
        }

        var target = entity.getBlockTarget();
        if (target == null || !canClickTarget(target) || lastTick >= 20)
            return;

        // TODO: if a user breaks a block the golem will still attempt to press it, causing issues
        try {
            BlockState state = this.entity.world.getBlockState(target);
            Block block = state.getBlock();
            if (!(block instanceof AbstractButtonBlock))
                return;
            AbstractButtonBlock button = (AbstractButtonBlock) block;
            if (state.get(AbstractButtonBlock.FACE) == WallMountLocation.FLOOR) {
                this.entity.setBendOverTicks(10);
            } else if (state.get(AbstractButtonBlock.FACE) == WallMountLocation.CEILING) {
                this.entity.setBendOverTicks(-10);
            }
            button.onUse(state, entity.world, target, null, Hand.MAIN_HAND, null);
            this.lastTick = 20 * 5;
            this.entity.setButtonTicksLeft(10); // 10 = 1 rotation, as max deviation is set to 10 in model logic

            entity.clearBlockTarget();
        }
        catch (Exception ignored) {
            ModInit.LOGGER.error("Failed to press targeted button. Error: " +  ignored.getMessage());
            // Could be an invalid target now, so clear it out
            entity.clearBlockTarget();
        }
    }

    private boolean canClickTarget(BlockPos pos) {
        var distance = this.entity.getBlockPos().getManhattanDistance(pos);
        return distance <= 1;
    }
}

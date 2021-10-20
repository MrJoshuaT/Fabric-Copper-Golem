package com.mrjoshuat.coppergolem.entity.goals;

import com.mrjoshuat.coppergolem.ModInit;
import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class FindButtonsToPressGoal extends Goal {
    private CopperGolemEntity golem;

    public FindButtonsToPressGoal(CopperGolemEntity entity) {
        this.golem = entity;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    public boolean canStart() {
        return !getButtons().isEmpty();
    }

    public List<BlockPos> getButtons() {
        ArrayList<BlockPos> blocks = new ArrayList<>();

        BlockPos pos = this.golem.getBlockPos();
        BlockPos start = pos.add(-10, -10, -10);
        BlockPos end = pos.add(10, 10, 10);
        for (BlockPos blockpos$mutableblockpos : BlockPos.iterate(start, end))
        {
            Block block = this.golem.world.getBlockState(blockpos$mutableblockpos).getBlock();
            if (isValidBlock(block))
            {
                blocks.add(blockpos$mutableblockpos.toImmutable());
            }
        }

        return blocks;
    }

    // TODO: what button, so all the buttons?
    protected boolean isValidBlock(Block block) {
        return block == Blocks.ACACIA_BUTTON || block == Blocks.BIRCH_BUTTON || block == Blocks.CRIMSON_BUTTON ||
                block == Blocks.DARK_OAK_BUTTON || block == Blocks.JUNGLE_BUTTON  || block == Blocks.OAK_BUTTON ||
                block == Blocks.SPRUCE_BUTTON || block == Blocks.STONE_BUTTON || block == Blocks.POLISHED_BLACKSTONE_BUTTON ||
                block == Blocks.WARPED_BUTTON;
    }

    public void tick() {
        pressTargetButton();
    }

    public void start() {
        targetButton();
    }

    private BlockPos targetPos = null;
    private void targetButton() {
        List<BlockPos> blocks = getButtons();
        if (blocks.isEmpty())
            return;

        int randomBlockToPress = this.golem.getRandom().nextInt((int) blocks.stream().count());
        this.targetPos = blocks.get(randomBlockToPress);

        golem.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 0.4D);
        //ModInit.LOGGER.info("Navigating to (" + targetPos.getX() + "," + targetPos.getY() + "," + targetPos.getZ() + ")");
    }

    private void pressTargetButton() {
        if (this.targetPos == null) {
            return;
        }

        BlockPos golemPos = golem.getBlockPos();
        if (golemPos.getManhattanDistance(targetPos) > 1) {
            return;
        }

        // interact with block
        // TODO: if a user breaks a block the golem will still attempt to press it, causing issues
        try {
            BlockState state = this.golem.world.getBlockState(targetPos);
            Block block = state.getBlock();
            AbstractButtonBlock button = (AbstractButtonBlock) block;
            button.onUse(state, golem.world, targetPos, null, Hand.MAIN_HAND, null);
        }
        catch (Exception ignored) { }
    }
}

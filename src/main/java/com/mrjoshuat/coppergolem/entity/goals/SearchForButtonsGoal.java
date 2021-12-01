package com.mrjoshuat.coppergolem.entity.goals;

import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SearchForButtonsGoal extends Goal {
    private final CopperGolemEntity entity;

    public SearchForButtonsGoal(CopperGolemEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        return this.entity.getBlockTarget() == null; // && this.entity.getRandom().nextFloat() < 0.1f;
    }

    @Override
    public boolean shouldContinue() { return this.entity.getBlockTarget() == null; }

    public void tick() {
        searchForButtons();
    }

    private void searchForButtons() {
        var buttons = getButtons();
        if (buttons.size() == 0)
            return;

        var pos = getRandomNavigableBlockPos(buttons);
        // Check we could find a button path
        if (pos == null)
            return;

        entity.setBlockTarget(pos);
    }

    @Nullable
    private BlockPos getRandomNavigableBlockPos(List<BlockPos> buttons) {
        while (buttons.size() > 0) {
            var index = entity.getRandom().nextInt(buttons.size());
            var pos = buttons.get(index);
            var path = this.entity.getNavigation().findPathTo(pos, 1);
            if (path != null && path.reachesTarget()) {
                return pos;
            } else {
                buttons.remove(index);
            }
        }
        return null;
    }

    private List<BlockPos> getButtons() {
        ArrayList<BlockPos> blocks = new ArrayList<>();

        BlockPos pos = this.entity.getBlockPos();
        BlockPos start = pos.add(-10, -10, -10);
        BlockPos end = pos.add(10, 10, 10);
        for (BlockPos blockpos$mutableblockpos : BlockPos.iterate(start, end)) {
            Block block = this.entity.world.getBlockState(blockpos$mutableblockpos).getBlock();
            if (isValidBlock(block)) {
                blocks.add(blockpos$mutableblockpos.toImmutable());
            }
        }

        return blocks;
    }

    // TODO: what button, so all the buttons?
    private boolean isValidBlock(Block block) { return block instanceof AbstractButtonBlock; }
}

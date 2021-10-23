package com.mrjoshuat.coppergolem.entity.target;

import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SearchForButtonsGoal extends Goal {
    private final CopperGolemEntity entity;
    public float lastTick = 0;

    public SearchForButtonsGoal(CopperGolemEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() { return true; }

    public void start() {
        this.searchForButtons();
    }

    public void tick() {
        if (this.lastTick > 0)
            --this.lastTick;

        if (this.lastTick <= 0)
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

        // Set tick cooldown
        this.lastTick = 20 * (10 * MathHelper.clamp(entity.getRandom().nextFloat(), 0.5f, 1f));
    }

    @Nullable
    private BlockPos getRandomNavigableBlockPos(List<BlockPos> buttons) {
        while (buttons.size() > 0)
        {
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
        for (BlockPos blockpos$mutableblockpos : BlockPos.iterate(start, end))
        {
            Block block = this.entity.world.getBlockState(blockpos$mutableblockpos).getBlock();
            if (isValidBlock(block))
            {
                blocks.add(blockpos$mutableblockpos.toImmutable());
            }
        }

        return blocks;
    }

    // TODO: what button, so all the buttons?
    private boolean isValidBlock(Block block) {
        return block == Blocks.ACACIA_BUTTON || block == Blocks.BIRCH_BUTTON || block == Blocks.CRIMSON_BUTTON ||
                block == Blocks.DARK_OAK_BUTTON || block == Blocks.JUNGLE_BUTTON  || block == Blocks.OAK_BUTTON ||
                block == Blocks.SPRUCE_BUTTON || block == Blocks.STONE_BUTTON || block == Blocks.POLISHED_BLACKSTONE_BUTTON ||
                block == Blocks.WARPED_BUTTON;
    }
}

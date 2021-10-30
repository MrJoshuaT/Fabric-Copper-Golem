package com.mrjoshuat.coppergolem.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mrjoshuat.coppergolem.ModInit;
import net.minecraft.block.*;

import java.util.Optional;
import java.util.function.Supplier;

public interface OxidizableButton extends Degradable<Oxidizable.OxidizationLevel> {
    Supplier<BiMap<Block, Block>> BUTTON_OXIDATION_LEVEL_INCREASES = Suppliers.memoize(() -> {
        BiMap<Block, Block> map = HashBiMap.create();
        map.put(ModInit.COPPER_BUTTON, ModInit.EXPOSED_COPPER_BUTTON);
        map.put(ModInit.EXPOSED_COPPER_BUTTON, ModInit.WEATHERED_COPPER_BUTTON);
        map.put(ModInit.WEATHERED_COPPER_BUTTON, ModInit.OXIDIZED_COPPER_BUTTON);
        return map;
    });
    Supplier<BiMap<Block, Block>> BUTTON_OXIDATION_LEVEL_DECREASES = Suppliers.memoize(() -> {
        return ((BiMap)BUTTON_OXIDATION_LEVEL_INCREASES.get()).inverse();
    });

    static Optional<Block> getDecreasedOxidationBlock(Block block) {
        return Optional.ofNullable((Block)((BiMap) BUTTON_OXIDATION_LEVEL_DECREASES.get()).get(block));
    }

    static Block getUnaffectedOxidationBlock(Block block) {
        Block block2 = block;

        for(Block block3 = (Block)((BiMap) BUTTON_OXIDATION_LEVEL_DECREASES.get()).get(block);
            block3 != null;
            block3 = (Block)((BiMap) BUTTON_OXIDATION_LEVEL_DECREASES.get()).get(block3)) {
            block2 = block3;
        }

        return block2;
    }

    static Optional<BlockState> getDecreasedOxidationState(BlockState state) {
        return getDecreasedOxidationBlock(state.getBlock()).map((block) -> {
            return block.getStateWithProperties(state);
        });
    }

    static Optional<Block> getIncreasedOxidationBlock(Block block) {
        var x = (BiMap)BUTTON_OXIDATION_LEVEL_INCREASES.get();
        var b = (Block)(x).get(block);
        return Optional.ofNullable(b);
    }

    static BlockState getUnaffectedOxidationState(BlockState state) {
        return getUnaffectedOxidationBlock(state.getBlock()).getStateWithProperties(state);
    }

    default Optional<BlockState> getDegradationResult(BlockState state) {
        return getIncreasedOxidationBlock(state.getBlock()).map((block) -> {
            return block.getStateWithProperties(state);
        });
    }

    default float getDegradationChanceMultiplier() {
        return this.getDegradationLevel() == Oxidizable.OxidizationLevel.UNAFFECTED ? 0.75F : 1.0F;
    }
}

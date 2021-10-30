package com.mrjoshuat.coppergolem.handler;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mrjoshuat.coppergolem.ModInit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import java.util.Optional;
import java.util.function.Supplier;

public class HoneycombItemHandler {
    public static final Supplier<BiMap<Block, Block>> UNWAXED_TO_WAXED_BUTTON_BLOCKS = Suppliers.memoize(() -> {
        BiMap<Block, Block> map = HashBiMap.create();
        map.put(ModInit.COPPER_BUTTON, ModInit.WAXED_COPPER_BUTTON);
        map.put(ModInit.EXPOSED_COPPER_BUTTON, ModInit.WAXED_EXPOSED_COPPER_BUTTON);
        map.put(ModInit.WEATHERED_COPPER_BUTTON, ModInit.WAXED_WEATHERED_COPPER_BUTTON);
        map.put(ModInit.OXIDIZED_COPPER_BUTTON, ModInit.WAXED_OXIDIZED_COPPER_BUTTON);
        return map;
    });
    public static final Supplier<BiMap<Block, Block>> WAXED_TO_UNWAXED_BUTTON_BLOCKS = Suppliers.memoize(() -> ((BiMap)UNWAXED_TO_WAXED_BUTTON_BLOCKS.get()).inverse());

    public static Optional<BlockState> getButtonWaxedState(BlockState state) {
        return Optional.ofNullable((Block)((BiMap) HoneycombItemHandler.UNWAXED_TO_WAXED_BUTTON_BLOCKS.get()).get(state.getBlock())).map((block) -> block.getStateWithProperties(state));
    }
}

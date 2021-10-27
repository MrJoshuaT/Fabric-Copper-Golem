package com.mrjoshuat.coppergolem;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mrjoshuat.coppergolem.block.OxidizableButtonBlock;
import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ModInit implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger(ClientMod.ID);

	public static final EntityType<CopperGolemEntity> COPPER_GOLEM_ENTITY_TYPE = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("minecraft", "copper_golem"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, CopperGolemEntity::new).dimensions(EntityDimensions.fixed(0.75f, 1.15f)).build()
	);

	public static final Item IRON_GOLEM_SPAWN_EGG = new SpawnEggItem(COPPER_GOLEM_ENTITY_TYPE,13136982, 	5805694, new Item.Settings().group(ItemGroup.MISC));

	public static final Identifier COPPER_BUTTON_ID = new Identifier("minecraft", "copper_button");
	public static final Identifier EXPOSED_COPPER_BUTTON_ID = new Identifier("minecraft", "exposed_copper_button");
	public static final Identifier WEATHERED_COPPER_BUTTON_ID = new Identifier("minecraft", "weathered_copper_button");
	public static final Identifier OXIDIZED_COPPER_BUTTON_ID = new Identifier("minecraft", "oxidized_copper_button");

	public static final Block COPPER_BUTTON = new OxidizableButtonBlock(Oxidizable.OxidizationLevel.UNAFFECTED, buildButtonSettings(0, COPPER_BUTTON_ID));
	public static final Block EXPOSED_COPPER_BUTTON = new OxidizableButtonBlock(Oxidizable.OxidizationLevel.EXPOSED, buildButtonSettings(1, EXPOSED_COPPER_BUTTON_ID));
	public static final Block WEATHERED_COPPER_BUTTON = new OxidizableButtonBlock(Oxidizable.OxidizationLevel.WEATHERED, buildButtonSettings(2, WEATHERED_COPPER_BUTTON_ID));
	public static final Block OXIDIZED_COPPER_BUTTON = new OxidizableButtonBlock(Oxidizable.OxidizationLevel.OXIDIZED, buildButtonSettings(3, OXIDIZED_COPPER_BUTTON_ID));

	@Override
	public void onInitialize() {
		FabricDefaultAttributeRegistry.register(COPPER_GOLEM_ENTITY_TYPE, CopperGolemEntity.createMobAttributes());
		Registry.register(Registry.ITEM, new Identifier("minecraft", "copper_golem_spawn_egg"), IRON_GOLEM_SPAWN_EGG);

		registerCopperButton(COPPER_BUTTON_ID, COPPER_BUTTON);
		registerCopperButton(EXPOSED_COPPER_BUTTON_ID, EXPOSED_COPPER_BUTTON);
		registerCopperButton(WEATHERED_COPPER_BUTTON_ID, WEATHERED_COPPER_BUTTON);
		registerCopperButton(OXIDIZED_COPPER_BUTTON_ID, OXIDIZED_COPPER_BUTTON);
	}

	private static void registerCopperButton(Identifier identifier, Block block) {
		Registry.register(Registry.BLOCK, identifier, block);
		Registry.register(Registry.ITEM, identifier, new BlockItem(block, new FabricItemSettings().group(ItemGroup.REDSTONE)));
	}

	private static final float buttonStrengthBase = 4f;
	private static FabricBlockSettings buildButtonSettings(int val, Identifier identifier) {
		return FabricBlockSettings.of(Material.DECORATION)
			.noCollision()
			.requiresTool()
			.breakByTool(FabricToolTags.PICKAXES)
			.sounds(BlockSoundGroup.COPPER)
			.strength(buttonStrengthBase - (buttonStrengthBase * (val * 0.1f)))
			.drops(identifier);
	}
}

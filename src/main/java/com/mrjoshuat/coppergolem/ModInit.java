package com.mrjoshuat.coppergolem;

import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModInit implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger(ClientMod.ID);

	public static final EntityType<CopperGolemEntity> COPPER_GOLEM_ENTITY_TYPE = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("minecraft", "copper_golem"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CopperGolemEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 1f)).build()
	);

	public static final Item IRON_GOLEM_SPAWN_EGG = new SpawnEggItem(COPPER_GOLEM_ENTITY_TYPE,
			13136982, 	5805694, new Item.Settings().group(ItemGroup.MISC));

	@Override
	public void onInitialize() {
		FabricDefaultAttributeRegistry.register(COPPER_GOLEM_ENTITY_TYPE, CopperGolemEntity.createMobAttributes());
		Registry.register(Registry.ITEM, new Identifier("minecraft", "copper_golem_spawn_egg"), IRON_GOLEM_SPAWN_EGG);
	}
}

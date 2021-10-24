package com.mrjoshuat.coppergolem;

import com.mrjoshuat.coppergolem.model.CopperGolemModel;
import com.mrjoshuat.coppergolem.renderer.CopperGolemRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ClientMod implements ClientModInitializer {
    public static final String ID = "fabric-copper-golem";
    public static final EntityModelLayer MODEL_GOLEM_LAYER = new EntityModelLayer(new Identifier("minecraft", "copper_golem"), "copper_golem");

    @Override
    public void onInitializeClient() {
        this.registerCopperGolem();
        this.registerResourcePack();
    }

    private void registerCopperGolem() {
        EntityRendererRegistry.register(ModInit.COPPER_GOLEM_ENTITY_TYPE, (context) -> new CopperGolemRenderer(context,
            new CopperGolemModel(context.getPart(ClientMod.MODEL_GOLEM_LAYER)), 0.5f));

        EntityModelLayerRegistry.registerModelLayer(MODEL_GOLEM_LAYER, CopperGolemModel::getTexturedModelData);
    }

    private void registerResourcePack() {
        FabricLoader.getInstance().getModContainer(ID).ifPresent(container -> {
            var added = ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(ID, "copper_golem"), container, ResourcePackActivationType.ALWAYS_ENABLED);
            if (!added) {
                ModInit.LOGGER.error("Failed to add default 'copper_golem' resource pack!");
            }
        });
    }
}

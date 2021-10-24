package com.mrjoshuat.coppergolem.renderer;

import com.google.common.collect.ImmutableMap;
import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;
import com.mrjoshuat.coppergolem.model.CopperGolemModel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class CopperGolemRenderer extends MobEntityRenderer<CopperGolemEntity, CopperGolemModel<CopperGolemEntity>> {
    private static final Map<CopperGolemEntity.Oxidisation, Identifier> DAMAGE_TO_TEXTURE;

    static {
        DAMAGE_TO_TEXTURE = ImmutableMap.of(
                CopperGolemEntity.Oxidisation.UNAFFECTED, new Identifier("textures/entity/copper_golem/copper_golem_0.png"),
                CopperGolemEntity.Oxidisation.EXPOSED, new Identifier("textures/entity/copper_golem/copper_golem_1.png"),
                CopperGolemEntity.Oxidisation.WEATHERED, new Identifier("textures/entity/copper_golem/copper_golem_2.png"),
                CopperGolemEntity.Oxidisation.OXIDIZED, new Identifier("textures/entity/copper_golem/copper_golem_3.png"));
    }

    public CopperGolemRenderer(EntityRendererFactory.Context context, CopperGolemModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Override
    public Identifier getTexture(CopperGolemEntity entity) { return DAMAGE_TO_TEXTURE.get(entity.getOxidisation()); }

    protected void setupTransforms(CopperGolemEntity copperGolemEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(copperGolemEntity, matrixStack, f, g, h);

        // TODO: if button is pressed then set arm pitch here
    }
}

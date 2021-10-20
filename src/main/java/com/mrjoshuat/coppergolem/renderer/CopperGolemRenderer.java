package com.mrjoshuat.coppergolem.renderer;

import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;
import com.mrjoshuat.coppergolem.model.CopperGolemModel;
import com.mrjoshuat.coppergolem.renderer.feature.CopperGolemOxidizableFeatureRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CopperGolemRenderer extends MobEntityRenderer<CopperGolemEntity, CopperGolemModel<CopperGolemEntity>> {
    public CopperGolemRenderer(EntityRendererFactory.Context context, CopperGolemModel entityModel, float f) {
        super(context, entityModel, f);
        this.addFeature(new CopperGolemOxidizableFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(CopperGolemEntity entity) {
        return new Identifier("minecraft", "textures/entity/copper_golem/copper_golem_0.png");
    }

    protected void setupTransforms(CopperGolemEntity copperGolemEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(copperGolemEntity, matrixStack, f, g, h);

        // TODO: if button is pressed then set arm pitch here
    }
}

package com.mrjoshuat.coppergolem.renderer.feature;

import com.google.common.collect.ImmutableMap;
import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;
import com.mrjoshuat.coppergolem.model.CopperGolemModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Map;

public class CopperGolemOxidizableFeatureRenderer extends FeatureRenderer<CopperGolemEntity, CopperGolemModel<CopperGolemEntity>> {
    private static final Map<CopperGolemEntity.Oxidisation, Identifier> DAMAGE_TO_TEXTURE;

    public CopperGolemOxidizableFeatureRenderer(FeatureRendererContext<CopperGolemEntity, CopperGolemModel<CopperGolemEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CopperGolemEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!entity.isInvisible()) {
            CopperGolemEntity.Oxidisation crack = entity.getOxidisation();
            if (crack != CopperGolemEntity.Oxidisation.UNAFFECTED) {
                var identifier = (Identifier)DAMAGE_TO_TEXTURE.get(crack);
                renderModel(this.getContextModel(), identifier, matrices, vertexConsumers, light, entity, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    static {
        DAMAGE_TO_TEXTURE = ImmutableMap.of(
            CopperGolemEntity.Oxidisation.EXPOSED, new Identifier("textures/entity/copper_golem/copper_golem_1.png"),
            CopperGolemEntity.Oxidisation.WEATHERED, new Identifier("textures/entity/copper_golem/copper_golem_2.png"),
            CopperGolemEntity.Oxidisation.OXIDIZED, new Identifier("textures/entity/copper_golem/copper_golem_3.png"));
    }
}

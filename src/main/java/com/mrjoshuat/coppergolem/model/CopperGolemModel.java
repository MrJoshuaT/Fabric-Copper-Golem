package com.mrjoshuat.coppergolem.model;

import com.mrjoshuat.coppergolem.entity.CopperGolemEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CopperGolemModel<T extends CopperGolemEntity> extends SinglePartEntityModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    private float lastAnimationProcess = 0;

    public CopperGolemModel(ModelPart modelPart) {
        root = modelPart;

        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.leftLeg = modelPart.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightLeg = modelPart.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftArm = modelPart.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightArm = modelPart.getChild(EntityModelPartNames.RIGHT_ARM);
    }

    @Override
    public void setAngles(CopperGolemEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // Generic angels
        //this.head.yaw = headYaw * 0.017453292F;
		this.head.pitch = headPitch * 0.005453292F;
		this.rightLeg.pitch = -1.5F * MathHelper.wrap(limbAngle, 13.0F) * limbDistance;
		this.leftLeg.pitch = 1.5F * MathHelper.wrap(limbAngle, 13.0F) * limbDistance;
		this.rightLeg.yaw = 0.0F;
		this.leftLeg.yaw = 0.0F;

        // Head spinning
        var process = entity.getHeadSpinProgress();
        if (process <= 0) {
            this.head.yaw = headYaw * 0.017453292F;
        } else {
            float l = (0.5F + process) * 3.1415927F;
            float m = -1.0F + MathHelper.sin(l);
            this.head.yaw = m * m * m * m * 3.1415927F * 0.125F;
        }

        this.leftArm.pitch = -1.5F * MathHelper.wrap(limbAngle, 13.0F) * limbDistance;
        this.rightArm.pitch = 1.5F * MathHelper.wrap(limbAngle, 13.0F) * limbDistance;

        // Arm moving
        var i = entity.getButtonTicksLeft();
        if (i > 0) {
            var tickDelta = animationProgress - this.lastAnimationProcess;
            this.rightArm.pitch = -2.0F + 1.5F * MathHelper.wrap((float)i - tickDelta, 10.0F);
            this.leftArm.pitch = -2.0F + 1.5F * MathHelper.wrap((float)i - tickDelta, 10.0F);
        }
        this.lastAnimationProcess = animationProgress;
    }

    @Override
    public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
    }

    @Override
    public ModelPart getPart() { return this.root; }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        var head = modelPartData.addChild(EntityModelPartNames.HEAD,
                ModelPartBuilder.create().uv(0, 0).cuboid(-5, 12, -4, 10, 6, 8),
                ModelTransform.pivot(0F, -6F, 0F));
        head.addChild(EntityModelPartNames.NOSE,
                ModelPartBuilder.create().uv(44, 5).cuboid(-1, 11, -6, 2, 4, 2),
                ModelTransform.pivot(0F, 5F, 0F));
        head.addChild("Lightning_rod_top",
                ModelPartBuilder.create().uv(36, 17).cuboid(-2, 20, -2, 4, 3, 4),
                ModelTransform.pivot(0F, -13F, 0F));
        head.addChild("Lightning_rod_pole",
                ModelPartBuilder.create().uv(44, 12).cuboid(-1, 18, -1, 2, 2, 2),
                ModelTransform.pivot(0F, -8F, 0F));

        // TODO: should the models be attached? Probably, tackle that soon ...
        modelPartData.addChild(EntityModelPartNames.BODY,
                ModelPartBuilder.create().uv(0, 15).cuboid(-5, 4, -3, 10, 8, 6),
                ModelTransform.pivot(0F, 8F, 0F));
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM,
                ModelPartBuilder.create().uv(38, 25).cuboid(-8, 3, -2, 3, 9, 4),
                ModelTransform.pivot(0F, 9F, 0F));
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM,
                ModelPartBuilder.create().mirrored().uv(38, 25).cuboid(5, 3, -2, 3, 9, 4),
                ModelTransform.pivot(0F, 9F, 0F));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG,
                ModelPartBuilder.create().mirrored().uv(0, 30).cuboid(-4, 0, -2, 4, 4, 4),
                ModelTransform.pivot(0F, 20F, 0F));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG,
                ModelPartBuilder.create().uv(0, 30).cuboid(0, 0, -2, 4, 4, 4),
                ModelTransform.pivot(0F, 20F, 0F));

        return TexturedModelData.of(modelData, 64, 64);
    }
}

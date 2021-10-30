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
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart rod;

    public CopperGolemModel(ModelPart modelPart) {
        root = modelPart;

        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        this.head = this.body.getChild(EntityModelPartNames.HEAD);
        this.nose = this.head.getChild(EntityModelPartNames.NOSE);
        this.leftLeg = modelPart.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightLeg = modelPart.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftArm = this.body.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightArm = this.body.getChild(EntityModelPartNames.RIGHT_ARM);
        this.rod = this.head.getChild("Lightning_rod_pole");
    }

    @Override
    public void setAngles(CopperGolemEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // Generic angles
		this.head.pitch = headPitch * 0.017453292F;
		this.rightLeg.pitch = -1.5F * MathHelper.wrap(limbAngle, 13.0F) * limbDistance;
		this.leftLeg.pitch = 1.5F * MathHelper.wrap(limbAngle, 13.0F) * limbDistance;
		this.rightLeg.yaw = 0.0F;
		this.leftLeg.yaw = 0.0F;

        var process = entity.getHeadSpinProgress();
        if (process > 0) {
            float l = (0.5F + process) * 3.1415927F;
            float m = -1.0F + MathHelper.sin(l);
            this.head.yaw = m * m * m * m * 3.1415927F * 0.125F;
        } else {
            this.head.yaw = headYaw * 0.017453292F;
        }

        var armTicks = entity.getButtonTicksLeft();
        if (armTicks > 0) {
            this.rightArm.pitch = -2.0F + 1.5F * MathHelper.wrap(armTicks, 10.0F);
            this.leftArm.pitch = -2.0F + 1.5F * MathHelper.wrap(armTicks, 10.0F);

            this.body.roll = 0F;
        } else {
            this.leftArm.pitch = -1.5F * MathHelper.wrap(limbAngle, 13.0F) * limbDistance;
            this.rightArm.pitch = 1.5F * MathHelper.wrap(limbAngle, 13.0F) * limbDistance;

            this.body.roll = 0.15F * MathHelper.wrap(limbAngle, 6.0F) * limbDistance;
        }

        var bendOverValue = entity.getBendOverTicks();
        this.body.pitch = (bendOverValue / 100) * 3.1415927F;

        var rodWiggleTicks = entity.getLastRodWiggleTicks();
        if (rodWiggleTicks > 0) {
            this.rod.roll = MathHelper.wrap(animationProgress, 20.0F) / 5.0F;
        } else {
            this.rod.roll = 0F;
        }
    }

    @Override
    public ModelPart getPart() { return this.root; }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Body has the limbs attached
        var body = modelPartData.addChild(EntityModelPartNames.BODY,
            ModelPartBuilder.create().uv(0, 15).cuboid(-5, -6, -3, 10, 8, 6),
            ModelTransform.pivot(0, 18, 0));

        body.addChild(EntityModelPartNames.LEFT_ARM,
            ModelPartBuilder.create().uv(38, 25).cuboid(-2, 0, -2, 3, 9, 4),
            ModelTransform.pivot(-6, -6, 0));
        body.addChild(EntityModelPartNames.RIGHT_ARM,
            ModelPartBuilder.create().mirrored().uv(38, 25).cuboid(-1, 0, -2, 3, 9, 4),
            ModelTransform.pivot(6, -6, 0));

        var head = body.addChild(EntityModelPartNames.HEAD,
                ModelPartBuilder.create().uv(0, 0).cuboid(-5, -3, -4, 10, 6, 8),
                ModelTransform.pivot(0, -9, 0));

        head.addChild(EntityModelPartNames.NOSE,
                ModelPartBuilder.create().uv(44, 5).cuboid(-1, 0, -1, 2, 4, 2),
                ModelTransform.pivot(0, 0, -5));
        head.addChild("Lightning_rod_pole",
                ModelPartBuilder.create().uv(44, 12).cuboid(-1, -2, -1, 2, 2, 2),
                ModelTransform.pivot(0, -3, 0))
            .addChild("Lightning_rod_top",
                ModelPartBuilder.create().uv(36, 17).cuboid(-2, -5, -2, 4, 3, 4),
                ModelTransform.pivot(0, 0, 0));

        // Legs are not attached to the body, sounds like a horror movie
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG,
            ModelPartBuilder.create().mirrored().uv(0, 30).cuboid(-2, 0, -2, 4, 4, 4),
            ModelTransform.pivot(-2, 20, 0));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG,
            ModelPartBuilder.create().uv(0, 30).cuboid(-2, 0, -2, 4, 4, 4),
            ModelTransform.pivot(2, 20, 0));

        return TexturedModelData.of(modelData, 64, 64);
    }
}

package ttv.migami.mdf.entity.client.buster;// Made with Blockbench 4.10.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class PianoModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart bb_main;

	public PianoModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-20.0F, 0.0F, -13.35F, 40.0F, 10.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(0, 26).addBox(-20.0F, 0.0F, 2.65F, 32.0F, 10.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(0, 52).addBox(-20.0F, 0.0F, -18.35F, 40.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 58).addBox(-17.0F, -10.0F, -11.35F, 0.0F, 10.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(48, 48).addBox(17.0F, -10.0F, -11.35F, 0.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 48).addBox(0.0F, -5.0F, -6.0F, 0.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -5.0F, 15.65F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r2 = bb_main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 48).addBox(0.0F, -5.0F, -6.0F, 0.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, -5.35F, 0.0F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
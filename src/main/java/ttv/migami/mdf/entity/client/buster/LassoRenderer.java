package ttv.migami.mdf.entity.client.buster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.client.ModModelLayers;
import ttv.migami.mdf.entity.fruit.buster.Lasso;

@OnlyIn(Dist.CLIENT)
public class LassoRenderer extends EntityRenderer<Lasso> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Reference.MOD_ID, "textures/fruit/buster/lasso.png");

   private final LassoModel<Lasso> model;

   public LassoRenderer(EntityRendererProvider.Context pContext) {
      super(pContext);
      this.model = new LassoModel<>(pContext.bakeLayer(ModModelLayers.LASSO_LAYER));
   }

   public void render(Lasso pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
      VertexConsumer vertexconsumer = pBuffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
      this.model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getTextureLocation(Lasso pEntity) {
      return TEXTURE_LOCATION;
   }
}
package ttv.migami.spas.entity.client.spider;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ttv.migami.spas.Reference;
import ttv.migami.spas.entity.fruit.spider.StringRing;

@OnlyIn(Dist.CLIENT)
public class StringRingRenderer extends EntityRenderer<StringRing> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Reference.MOD_ID, "textures/fruit/spider/string_ring.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

   public StringRingRenderer(EntityRendererProvider.Context pContext) {
      super(pContext);
   }

   protected int getBlockLightLevel(StringRing pEntity, BlockPos pPos) {
      return 15;
   }

   public void render(StringRing pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
      super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getTextureLocation(StringRing pEntity) {
      return TEXTURE_LOCATION;
   }
}
package ttv.migami.mdf.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class StringRingParticle extends TextureSheetParticle {
   private static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0F, -1.0F, 0.0F);
   private int delay;
    private final Quaternionf randomRotation;
   private final int growthDuration;
   private final float maxSize = 16.0F;
   private final int shrinkStartTime = 50;
   private final int shrinkDuration = 5;

   StringRingParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
      super(pLevel, pX, pY, pZ, 0.0D, 0.0D, 0.0D);
      this.quadSize = 0.85F;
      this.lifetime = 80;
      this.gravity = 0.0F;
      this.xd = 0.0D;
      this.yd = 0.1D;
      this.zd = 0.0D;

      float randomXRotation = (float) (Math.random() * Math.PI * 2);
      float randomYRotation = (float) (Math.random() * Math.PI * 2);
      float randomZRotation = (float) (Math.random() * Math.PI * 2);
      this.randomRotation = new Quaternionf().rotateXYZ(randomXRotation, randomYRotation, randomZRotation);

      this.growthDuration = (int) (Math.random() * 31);
   }

   @Override
   public float getQuadSize(float pScaleFactor) {
      float ageRatio = ((float) this.age + pScaleFactor) / (float) this.lifetime;

      if (this.age < this.growthDuration) {
         return Mth.lerp((float) this.age / (float) this.growthDuration, 0.1F, this.maxSize);
      }

      if (this.age >= shrinkStartTime) {
         float shrinkRatio = (float) (this.age - shrinkStartTime + pScaleFactor) / (float) shrinkDuration;
         return Mth.lerp(shrinkRatio, this.maxSize, 0.0F);
      }

      return this.maxSize;
   }

   @Override
   public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
      if (this.delay <= 0) {
         this.alpha = 1.0F - Mth.clamp(((float) this.age + pPartialTicks) / (float) this.lifetime, 0.0F, 1.0F);
         this.renderRotatedParticle(pBuffer, pRenderInfo, pPartialTicks, quaternion -> quaternion.mul(this.randomRotation));
      }
   }

   private void renderRotatedParticle(VertexConsumer pConsumer, Camera pCamera, float pPartialTick, Consumer<Quaternionf> pQuaternion) {
      Vec3 vec3 = pCamera.getPosition();
      float f = (float) (Mth.lerp((double) pPartialTick, this.xo, this.x) - vec3.x());
      float f1 = (float) (Mth.lerp((double) pPartialTick, this.yo, this.y) - vec3.y());
      float f2 = (float) (Mth.lerp((double) pPartialTick, this.zo, this.z) - vec3.z());
      Quaternionf quaternionf = new Quaternionf();
      pQuaternion.accept(quaternionf);
      quaternionf.transform(TRANSFORM_VECTOR);
      Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
      float f3 = this.getQuadSize(pPartialTick);

      for (int i = 0; i < 4; ++i) {
         Vector3f vector3f = avector3f[i];
         vector3f.rotate(quaternionf);
         vector3f.mul(f3);
         vector3f.add(f, f1, f2);
      }

      int j = this.getLightColor(pPartialTick);
      
      // Render front side
      this.makeCornerVertex(pConsumer, avector3f[0], this.getU1(), this.getV1(), j);
      this.makeCornerVertex(pConsumer, avector3f[1], this.getU1(), this.getV0(), j);
      this.makeCornerVertex(pConsumer, avector3f[2], this.getU0(), this.getV0(), j);
      this.makeCornerVertex(pConsumer, avector3f[3], this.getU0(), this.getV1(), j);

      // Render back side (reverse order)
      this.makeCornerVertex(pConsumer, avector3f[0], this.getU1(), this.getV1(), j);
      this.makeCornerVertex(pConsumer, avector3f[3], this.getU0(), this.getV1(), j);
      this.makeCornerVertex(pConsumer, avector3f[2], this.getU0(), this.getV0(), j);
      this.makeCornerVertex(pConsumer, avector3f[1], this.getU1(), this.getV0(), j);
   }

   private void makeCornerVertex(VertexConsumer pConsumer, Vector3f pVertex, float pU, float pV, int pPackedLight) {
      pConsumer.vertex((double)pVertex.x(), (double)pVertex.y(), (double)pVertex.z()).uv(pU, pV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(pPackedLight).endVertex();
   }

   public int getLightColor(float pPartialTick) {
      return 240;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      if (this.delay > 0) {
         --this.delay;
      } else {
         super.tick();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet pSprite) {
         this.sprite = pSprite;
      }

      public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
         StringRingParticle StringRingParticle = new StringRingParticle(pLevel, pX, pY, pZ);
         StringRingParticle.pickSprite(this.sprite);
         StringRingParticle.setAlpha(1.0F);
         return StringRingParticle;
      }
   }
}
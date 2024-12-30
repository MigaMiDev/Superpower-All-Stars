package ttv.migami.mdf.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TeaParticle extends SimpleAnimatedParticle {
    TeaParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, int pPackedColor, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pSprites, 0.0F);
        this.friction = 0.92F;
        this.quadSize = 0.5F;
        this.setAlpha(1.0F);
        this.setColor((float) FastColor.ARGB32.red(pPackedColor), (float) FastColor.ARGB32.green(pPackedColor), (float) FastColor.ARGB32.blue(pPackedColor));
        this.lifetime = (int)((double)(this.quadSize * 12.0F) / (Math.random() * 0.800000011920929 + 0.20000000298023224));
        this.setSpriteFromAge(pSprites);
        this.hasPhysics = false;
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
    }

    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSpriteFromAge(this.sprites);
            if (this.age > this.lifetime / 2) {
                this.setAlpha(1.0F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
            }

            if (this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
                this.yd -= 0.007400000002235174;
            }
        }

    }

    public int getLightColor(float pPartialTick) {
        return 240;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new TeaParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, FastColor.ARGB32.color(255, 60, 200, 10), this.sprites);
        }
    }
}
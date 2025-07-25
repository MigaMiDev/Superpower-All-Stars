package ttv.migami.spas.effect.fruit;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import ttv.migami.spas.effect.FruitEffect;

/**
 * Author: MigaMi
 */
public class MagmaFruitEffect extends FruitEffect {

    public MagmaFruitEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        AABB box = pLivingEntity.getBoundingBox();
        RandomSource random = pLivingEntity.getRandom();

        /*int dripCount = 1;
        for (int i = 0; i < dripCount; i++) {
            double x = Mth.nextDouble(random, box.minX, box.maxX);
            double y = Mth.nextDouble(random, box.minY, box.maxY);
            double z = Mth.nextDouble(random, box.minZ, box.maxZ);
            pLivingEntity.level().addParticle(ParticleTypes.FALLING_LAVA, x, y, z, 0, 0, 0);
        }*/

        if (pLivingEntity.isSprinting()) {
            pLivingEntity.level().addParticle(ParticleTypes.LAVA, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), 0, 0, 0);
        }
    }
}

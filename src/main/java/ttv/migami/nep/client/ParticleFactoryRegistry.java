package ttv.migami.nep.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.nep.Reference;
import ttv.migami.nep.client.particle.*;
import ttv.migami.nep.init.ModParticleTypes;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleFactoryRegistry
{
    @SubscribeEvent
    public static void onRegisterParticleFactory(RegisterParticleProvidersEvent event)
    {
        event.registerSpriteSet(ModParticleTypes.GASTER_BLASTER_BEAM.get(), GasterBlasterBeamParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SKELETON_CONTROL_PARTICLE.get(), SkeletonControlParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.INK_STROKE.get(), InkStrokeParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.INK_STROKE_RIGHT.get(), InkStrokeRightParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.INK_STROKE_DUAL.get(), InkStrokeDualParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.FIRE_BIG_EXPLOSION.get(), FireBigExplosion.Provider::new);
        event.registerSpriteSet(ModParticleTypes.FIRE_RING.get(), FireRingParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.STRING_RING.get(), StringRingParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.TEA.get(), TeaParticle.Provider::new);

        event.registerSpriteSet(ModParticleTypes.GENERIC_HIT.get(), GenericHitParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SMOKE.get(), SmokeParticle.Provider::new);
    }
}

package ttv.migami.nep.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ttv.migami.nep.Reference;

/**
 * Author: MigaMi
 */
public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Reference.MOD_ID);

    public static final RegistryObject<SimpleParticleType> GASTER_BLASTER_BEAM = REGISTER.register("gaster_blaster_beam", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SKELETON_CONTROL_PARTICLE = REGISTER.register("skeleton_control", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> INK_STROKE = REGISTER.register("ink_stroke", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> INK_STROKE_RIGHT = REGISTER.register("ink_stroke_right", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> INK_STROKE_DUAL = REGISTER.register("ink_stroke_dual", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> FIRE_BIG_EXPLOSION = REGISTER.register("fire_big_explosion", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> FIRE_RING = REGISTER.register("fire_ring", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> STRING_RING = REGISTER.register("string_ring", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> TEA = REGISTER.register("tea", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> GENERIC_HIT = REGISTER.register("generic_hit", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SMOKE = REGISTER.register("smoke", () -> new SimpleParticleType(true));
}

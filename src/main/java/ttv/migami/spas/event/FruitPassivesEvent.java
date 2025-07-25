package ttv.migami.spas.event;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.spas.Config;
import ttv.migami.spas.Reference;
import ttv.migami.spas.SuperpowerAllStars;
import ttv.migami.spas.common.Fruit;
import ttv.migami.spas.common.FruitDataHandler;
import ttv.migami.spas.common.network.ServerPlayHandler;
import ttv.migami.spas.effect.FruitEffect;
import ttv.migami.spas.entity.fruit.TimedBlockDisplayEntity;
import ttv.migami.spas.init.ModEffects;
import ttv.migami.spas.init.ModParticleTypes;
import ttv.migami.spas.init.ModSounds;

import java.util.List;

import static ttv.migami.spas.common.network.ServerPlayHandler.throwPlayerDownward;
import static ttv.migami.spas.common.network.ServerPlayHandler.throwPlayerForward;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FruitPassivesEvent {

    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event) {

        Player player = event.player;
        Level level = player.level();

        if (level instanceof ServerLevel serverLevel)
        {
            ServerPlayHandler.syncToClient((ServerPlayer) player);
            MobEffect currentEffect = FruitDataHandler.getCurrentEffect(player);

            if (currentEffect instanceof FruitEffect) {
                if (player.getEffect(currentEffect) != null && player.getEffect(currentEffect).getDuration() == -1) {
                    player.addEffect(new MobEffectInstance(currentEffect, -1, 0, false, false));
                }
                if (!player.hasEffect(currentEffect)) {
                    FruitDataHandler.clearCurrentEffect(player);
                }

                if (((FruitEffect) currentEffect).getFruit().getGeneral().isSwimDisabled()) {
                    if (player.isInWater() && !Config.COMMON.gameplay.noSwimming.get() &&
                            ((serverLevel.getBlockState(player.getOnPos().below()).is(Blocks.WATER) && player.isInWater()) || player.isUnderWater())) {
                        if (player.getRandom().nextDouble() < 0.06) {
                            throwPlayerDownward(player, 0.2);
                            serverLevel.sendParticles(ParticleTypes.BUBBLE, player.getX(), player.getY(), player.getZ(), 10, player.getBbWidth(), player.getBbHeight(), player.getBbWidth(), 0.1);
                            serverLevel.playSound(null, player.getOnPos(), SoundEvents.PLAYER_SWIM, SoundSource.PLAYERS, 0.5F, 1);
                            serverLevel.playSound(null, player.getOnPos(), SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.PLAYERS, 1, 1);
                            //serverLevel.playSound(null, player.getOnPos(), SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, SoundSource.PLAYERS, 1, 1);
                            ServerPlayHandler.mediumFoodExhaustion(player);
                        }
                        player.displayClientMessage(Component.translatable("chat.spas.no_swimming").withStyle(ChatFormatting.RED), true);
                        ServerPlayHandler.applyHunger(player);
                        player.addEffect(new MobEffectInstance(ModEffects.STUNNED.get(), 10, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10, 0));
                    }
                }
            }

            if (player.hasEffect(ModEffects.FEATHER_FALLING.get())) {
                player.resetFallDistance();
            }

            if (player.hasEffect(ModEffects.POWER.get()) && !player.hasEffect(MobEffects.INVISIBILITY)) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        player.getX(), player.getY() + 1, player.getZ(), 2, 0.3, 0.4, 0.3, 0);
            }

            if (player.hasEffect(ModEffects.SQUID_FRUIT.get())) {
                if (player.hasEffect(MobEffects.BLINDNESS)) {
                    player.removeEffect(MobEffects.BLINDNESS);
                }
                if (player.isInWater()) {
                    player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 20, 0, false, false));
                }
                if (player.isUnderWater()) {
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20, 0, false, false));
                }
                if (player.hasEffect(MobEffects.LEVITATION) && player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                    serverLevel.sendParticles(ParticleTypes.SQUID_INK,
                            player.getX(), player.getY(), player.getZ(), 2, 0.3, 0.4, 0.3, 0);
                }
            }

            if (player.hasEffect(ModEffects.FIRE_FRUIT.get())) {
                if (player.isInWater()) {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0, false, true));
                }
                if (player.isUnderWater()) {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 1, false, true));
                }
                if (player.hasEffect(MobEffects.LEVITATION) && player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                    serverLevel.sendParticles(ModParticleTypes.FIRE_RING.get(),
                            player.getX(), player.getY(), player.getZ(), 1, 0.3, 0.4, 0.3, 0);
                    serverLevel.sendParticles(ModParticleTypes.SMOKE.get(), player.getX(), player.getY(), player.getZ(), 1, 0, 0, 0, 2);
                    serverLevel.sendParticles(ParticleTypes.LAVA, player.getX(), player.getY() + 1, player.getZ(), 3, 2.0, 0.0, 2.0, 0.2);
                }
                if (player.hasEffect(MobEffects.FIRE_RESISTANCE) && player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                    serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, player.getX(), player.getY(), player.getZ(), 1, 0, 0, 0, 0.05);
                    serverLevel.sendParticles(ParticleTypes.LAVA, player.getX(), player.getY() + 1, player.getZ(), 3, 0.0, 0.1, 0.0, 0.2);
                }
            }

            if (player.hasEffect(ModEffects.RUBBER_FRUIT.get())) {
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 0, false, false));

                if (player.hasEffect(ModEffects.SLINGSHOT.get())) {
                    if (player.getEffect(ModEffects.SLINGSHOT.get()).getDuration() == 27) {
                        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 2, 64, false, false));
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 27, 3, false, false));
                    }

                    if (player.getEffect(ModEffects.SLINGSHOT.get()).getDuration() == 25) {
                        double force = 3;
                        Vec3 lookVec = player.getLookAngle();
                        Vec3 motion = lookVec.scale(force);

                        throwPlayerForward(player, motion);
                        player.level().playSound(null, player, ModSounds.GOMU_NO_THROW.get(), SoundSource.AMBIENT, 0.75F, 1F);
                        ((ServerLevel) player.level()).sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.3, player.getZ(), 5, 0.0D, 0.0D, 0.0D, 0.1D);
                    }

                    if (player.getEffect(ModEffects.SLINGSHOT.get()).getDuration() <= 25) {
                        double radius = 3; // This will cover a 4x4 area centered on the player
                        AABB areaOfEffect = new AABB(player.getX() - radius, player.getY() - radius /2 , player.getZ() - radius, player.getX() + radius, player.getY() + radius / 2, player.getZ() + radius);
                        List<Entity> entitiesArea = serverLevel.getEntities(player, areaOfEffect);

                        MobEffect effect = FruitDataHandler.getCurrentEffect(player);
                        ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(effect);

                        if (effect instanceof FruitEffect fruitEffect) {
                            if (effectId != null) {
                                Fruit fruit = fruitEffect.getFruit();

                                if (fruit != null) {
                                    for (Entity entity : entitiesArea) {
                                        if (entity instanceof LivingEntity livingEntity && entity != player && entity.invulnerableTime == 0) {
                                            entity.invulnerableTime = 6;
                                            if (!entity.level().isClientSide) {
                                                ((ServerLevel) entity.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(), entity.getZ(), 3, 0.3, entity.getBbHeight(), 0.3, 0.2);
                                            }
                                            livingEntity.hurt(player.damageSources().playerAttack(player), ServerPlayHandler.calculateCustomDamage(player, fruit.getMobility().getDamage()));
                                        }
                                    }
                                }
                            }
                        }


                        if ((player.getEffect(ModEffects.SLINGSHOT.get()).getDuration() % 2) == 0) {
                            ((ServerLevel) player.level()).sendParticles(ModParticleTypes.GENERIC_HIT.get(), player.getX(), player.getY() + 1, player.getZ(), 1, 0, 0, 0, 0.0);
                        }
                        if (player.onGround()) {
                            player.removeEffect(ModEffects.SLINGSHOT.get());
                        }
                    }
                }

                if (player.isInWater()) {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0, false, true));
                }
                if (player.isUnderWater()) {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 1, false, true));
                }
            }

            if (player.hasEffect(ModEffects.SPIDER_FRUIT.get())) {
                if (player.isInWater()) {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0, false, true));
                }
                if (player.isUnderWater()) {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 1, false, true));
                }
                player.resetFallDistance();
            }
        }
    }

    @SubscribeEvent
    public static void onServerStartup(ServerStoppingEvent event) {
        MinecraftServer server = event.getServer();

        SuperpowerAllStars.LOGGER.atInfo().log("Cleaning debri!");

        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof TimedBlockDisplayEntity) {
                    entity.remove(Entity.RemovalReason.KILLED);
                    entity.discard();
                    entity.kill();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerStartup(ServerStoppedEvent event) {
        MinecraftServer server = event.getServer();

        SuperpowerAllStars.LOGGER.atInfo().log("Making sure debri is no more!");

        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof TimedBlockDisplayEntity) {
                    entity.remove(Entity.RemovalReason.KILLED);
                    entity.discard();
                    entity.kill();
                }
            }
        }
    }
}

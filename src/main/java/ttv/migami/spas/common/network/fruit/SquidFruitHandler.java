package ttv.migami.spas.common.network.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import ttv.migami.spas.common.Fruit;
import ttv.migami.spas.common.network.ServerPlayHandler;
import ttv.migami.spas.entity.fruit.squid.InkSplat;
import ttv.migami.spas.init.ModEffects;
import ttv.migami.spas.init.ModParticleTypes;
import ttv.migami.spas.init.ModSounds;

import java.util.List;

import static ttv.migami.spas.common.network.ServerPlayHandler.*;

/**
 * Author: MigaMi
 */
public class SquidFruitHandler
{
    public static void moveHandler(Player pPlayer, Fruit fruit, int move, int amount) {
        Level pLevel = pPlayer.level();

        if (!pLevel.isClientSide()) {

            BlockPos blockPos;
            Vec3 playerPos;
            EntityHitResult entityHitResult;

            Vec3 lookVec = pPlayer.getLookAngle();
            Vec3 rightVec = new Vec3(-lookVec.z, 0, lookVec.x).normalize();
            Vec3 forwardVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

            double sideOffset = 2.5;
            double randomChance = Math.random();

            if (randomChance < 0.5) {
                sideOffset *= -1;
            }

            double offsetX;
            double offsetY;
            double offsetZ;
            InkSplat inkSplat;

            switch (move) {
                case 1:
                    blockPos = rayTrace(pPlayer, 128.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    sideOffset = 1.0;
                    if (randomChance < 0.5) {
                        sideOffset *= -1;
                    }

                    offsetX = rightVec.x * (sideOffset / 2) + forwardVec.x * -2;
                    offsetY = pPlayer.getEyeHeight() - 0.4;
                    offsetZ = rightVec.z * (sideOffset / 2) + forwardVec.z * -2;

                    playerPos = pPlayer.getPosition(1F).add(offsetX, offsetY, offsetZ);

                    actionSlowdown(pPlayer);
                    if (entityHitResult != null) {
                        inkSplat = new InkSplat(pLevel, pPlayer, playerPos, entityHitResult.getEntity().getEyePosition().add(0, 1, 0), fruit.getMoveA().getDamage(), false);
                    }
                    else {
                        inkSplat = new InkSplat(pLevel, pPlayer, playerPos, blockPos.getCenter(), fruit.getMoveA().getDamage(), false);
                    }
                    pLevel.addFreshEntity(inkSplat);
                    pPlayer.removeEffect(MobEffects.BLINDNESS);

                    pLevel.playSound(null, pPlayer.getOnPos(), ModSounds.SQUID_SPLAT.get(), SoundSource.PLAYERS, 2F, 1F);

                    ServerPlayHandler.smallFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);
                    break;
                case 2:
                    double attackRange = 4.0;
                    double sweepAngle = Math.toRadians(100);

                    playerPos = pPlayer.position();
                    lookVec = pPlayer.getLookAngle();

                    List<LivingEntity> entities = pLevel.getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(attackRange));

                    pLevel.playSound(null, pPlayer.getOnPos(), ModSounds.SQUID_SPLAT.get(), SoundSource.PLAYERS, 2F, 1F);
                    for (LivingEntity entity : entities) {
                        Vec3 entityPos = entity.position().subtract(playerPos);
                        double angle = Math.acos(entityPos.normalize().dot(lookVec.normalize()));

                        if (angle < sweepAngle / 2 && entity != pPlayer) {
                            float customDamage = 3.0F;
                            //entity.hurt(pPlayer.damageSources().playerAttack(pPlayer), ServerPlayHandler.calculateCustomDamage(pPlayer, customDamage) / 1.5F);
                            entity.hurt(pPlayer.damageSources().playerAttack(pPlayer), fruit.getMoveB().getDamage());
                            entity.invulnerableTime = 0;
                            if (!entity.level().isClientSide) {
                                ((ServerLevel) entity.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(), entity.getZ(), 4, 0.3, entity.getBbHeight(), 0.3, 0.2);
                            }

                            if (!pPlayer.level().isClientSide) {
                                ServerLevel serverLevel = (ServerLevel) pPlayer.level();

                                serverLevel.sendParticles(ParticleTypes.SQUID_INK, entity.getX(), entity.getBbHeight() * 0.5, entity.getZ(), 12, 0.2, 0.0, 0.3, 0.2);

                            }
                        }
                    }

                    offsetX = lookVec.x * 1.8;
                    offsetY = lookVec.y * 1.8 + pPlayer.getEyeHeight();
                    offsetZ = lookVec.z * 1.8;
                    playerPos = pPlayer.getPosition(1F).add(offsetX, offsetY, offsetZ);


                    if (!pLevel.isClientSide) {
                        if (amount == 3) {
                            ((ServerLevel) pLevel).sendParticles(ModParticleTypes.INK_STROKE.get(), playerPos.x, playerPos.y, playerPos.z, 1, 0, 0, 0, 0.0);
                        } else if (amount == 2) {
                            ((ServerLevel) pLevel).sendParticles(ModParticleTypes.INK_STROKE_RIGHT.get(), playerPos.x, playerPos.y, playerPos.z, 1, 0, 0, 0, 0.0);
                        } else if (amount == 1) {
                            ((ServerLevel) pLevel).sendParticles(ModParticleTypes.INK_STROKE_DUAL.get(), playerPos.x, playerPos.y, playerPos.z, 1, 0, 0, 0, 0.0);
                        }
                    }
                    pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 2F, 1F);
                    pLevel.playSound(null, pPlayer.getOnPos(), ModSounds.SQUID_SPLAT.get(), SoundSource.PLAYERS, 2F, 1F);

                    actionHeavySlowdown(pPlayer);
                    ServerPlayHandler.bigFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);
                    break;
                case 3:
                    blockPos = rayTrace(pPlayer, 128.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    sideOffset = 1.0;
                    if (randomChance < 0.5) {
                        sideOffset *= -1;
                    }

                    offsetX = rightVec.x * (sideOffset / 2) + forwardVec.x * -2;
                    offsetY = pPlayer.getEyeHeight() - 0.4;
                    offsetZ = rightVec.z * (sideOffset / 2) + forwardVec.z * -2;

                    playerPos = pPlayer.getPosition(1F).add(offsetX, offsetY, offsetZ);

                    actionSlowdown(pPlayer);
                    if (entityHitResult != null) {
                        inkSplat = new InkSplat(pLevel, pPlayer, playerPos, entityHitResult.getEntity().getEyePosition(), fruit.getSpecial().getDamage(), true);
                    }
                    else {
                        inkSplat = new InkSplat(pLevel, pPlayer, playerPos, blockPos.getCenter(), fruit.getSpecial().getDamage(), true);
                    }
                    pLevel.addFreshEntity(inkSplat);
                    pPlayer.removeEffect(MobEffects.BLINDNESS);
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 5, false, true));

                    pLevel.playSound(null, pPlayer.getOnPos(), ModSounds.SQUID_SPLAT.get(), SoundSource.PLAYERS, 2F, 1F);

                    ServerPlayHandler.largeFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                case 4:
                    //if (pPlayer.onGround()) {
                        if (!pLevel.isClientSide) {
                            ((ServerLevel) pLevel).sendParticles(ParticleTypes.SQUID_INK, pPlayer.getX(), pPlayer.getY() + 0.0, pPlayer.getZ(), 256, 4, 0, 4, 0.2);
                        }

                        double radius = 4; // This will cover a 5x5 area centered on the player
                        AABB areaOfEffect = new AABB(pPlayer.getX() - radius, pPlayer.getY() - radius /2 , pPlayer.getZ() - radius, pPlayer.getX() + radius, pPlayer.getY() + radius / 2, pPlayer.getZ() + radius);
                        List<Entity> entitiesArea = pLevel.getEntities(pPlayer, areaOfEffect);
                        for (Entity entity : entitiesArea) {
                            if (entity instanceof LivingEntity livingEntity && entity != pPlayer) {
                                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3, false, false));
                                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, true));
                                livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0, false, false));
                                livingEntity.hurt(pPlayer.damageSources().playerAttack(pPlayer), fruit.getUltimate().getDamage());
                            }
                        }
                    pLevel.playSound(null, pPlayer.getOnPos(), ModSounds.SQUID_SPLAT.get(), SoundSource.PLAYERS, 2F, 1F);
                    //}

                    break;
                case 5:
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 5, false, false));
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 21, false, false));
                    pPlayer.addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 100, 0, false, false));

                    if (pPlayer.isUnderWater()) {
                        double force = 2;
                        lookVec = pPlayer.getLookAngle();
                        Vec3 motion = lookVec.scale(force);

                        throwPlayerForward(pPlayer, motion);
                    }

                    pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.SQUID_SQUIRT, SoundSource.PLAYERS, 2F, 1F);
                    pLevel.playSound(null, pPlayer.getOnPos(), ModSounds.SQUID_SPLAT.get(), SoundSource.PLAYERS, 2F, 1F);

                    ServerPlayHandler.largeFoodExhaustion(pPlayer);
                    break;
                default:
                    break;
            }
        }

    }
}

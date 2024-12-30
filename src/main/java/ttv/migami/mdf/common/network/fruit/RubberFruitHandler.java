package ttv.migami.mdf.common.network.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import ttv.migami.mdf.common.Fruit;
import ttv.migami.mdf.common.network.ServerPlayHandler;
import ttv.migami.mdf.init.ModEffects;
import ttv.migami.mdf.init.ModParticleTypes;
import ttv.migami.mdf.init.ModSounds;

import java.util.List;

import static ttv.migami.mdf.common.network.ServerPlayHandler.actionHeavySlowdown;
import static ttv.migami.mdf.common.network.ServerPlayHandler.throwPlayerForward;
import static ttv.migami.mdf.entity.fx.CracksMarkEntity.summonCracksMark;
import static ttv.migami.mdf.world.CraterCreator.createCrater;

/**
 * Author: MigaMi
 */
public class RubberFruitHandler
{
    private static Fruit fruit;
    private static Fruit.ZMove zMove;
    private static Fruit.XMove xMove;
    private static Fruit.CMove cMove;
    private static Fruit.VMove vMove;
    private static Fruit.FMove fMove;

    public static void moveHandler(Player pPlayer, int move) {
        Level pLevel = pPlayer.level();

        if (!pLevel.isClientSide()) {

            Vec3 look = pPlayer.getViewVector(1F);
            FireworkRocketEntity firework;
            BlockPos blockPos;
            EntityHitResult entityHitResult;
            RandomSource rand = RandomSource.create();

            double xOffset;
            double zOffset;
            double speed;

            int type = 0;
            if(pPlayer.experienceLevel >= 15 || pPlayer.isCreative()) {
                type = 1;
            }

            Vec3 playerPos;

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
            double force;
            Vec3 motion;

            HitResult hitResult;
            double range;

            switch (move) {
                case 1:
                    HitResult result = pPlayer.pick(16, 0, false);
                    Vec3 eyePos = pPlayer.getEyePosition().add(0, -0.75, 0);
                    Vec3 targetPos = result.getLocation();
                    Vec3 distanceTo = targetPos.subtract(eyePos);
                    Vec3 normal = distanceTo.normalize();

                    for(int i = 1; i < Mth.floor(distanceTo.length()) + 7; ++i) {
                        Vec3 eyeVec3 = eyePos.add(normal.scale((double)i));
                        //createCrater(pLevel, BlockPos.containing(targetPos), 2);
                        ((ServerLevel) pLevel).sendParticles(ModParticleTypes.GENERIC_HIT.get(), eyeVec3.x, eyeVec3.y, eyeVec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        ((ServerLevel) pLevel).sendParticles(ParticleTypes.CLOUD, eyeVec3.x, eyeVec3.y, eyeVec3.z, 5, 0.0D, 0.0D, 0.0D, 0.1D);
                    }

                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, BlockPos.containing(targetPos));

                    if(entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity entity && entity != pPlayer) {
                        entity.hurt(pPlayer.damageSources().playerAttack((Player) pPlayer), 5);
                        double d1 = 0.5D * (1.0D - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                        double d0 = 2.5D * (1.0D - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                        entity.push(normal.x() * d0, normal.y() * d1, normal.z() * d0);
                        entity.invulnerableTime = 0;

                    }
                    pLevel.playSound(null, pPlayer.blockPosition(), ModSounds.GUM_GUM_BLAST.get(), SoundSource.PLAYERS, 1.33F, 1F);

                    actionHeavySlowdown(pPlayer);
                    ServerPlayHandler.mediumFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                    
                case 2:
                    range = 48;
                    hitResult = pPlayer.pick(range, 0.0F, false);

                    pLevel.playSound(null, pPlayer, ModSounds.GOMU_NO_THROW.get(), SoundSource.AMBIENT, 0.75F, 1F);
                    //pPlayer.teleportTo(hitResult.getLocation().x, hitResult.getLocation().y + 10, hitResult.getLocation().z);
                    //pPlayer.lookAt(EntityAnchorArgument.Anchor.FEET, hitResult.getLocation());

                    force = 3;
                    lookVec = pPlayer.getLookAngle();
                    motion = lookVec.scale(force);

                    throwPlayerForward(pPlayer, motion);

                    pPlayer.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 5, 48, false, false));
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false));
                    pPlayer.addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 80, 0, false, false));

                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, BlockPos.containing(hitResult.getLocation()));

                    if (entityHitResult == null) {
                        summonCracksMark(pLevel, BlockPos.containing(hitResult.getLocation().add(0, 1, 0)), 200, 8);
                        createCrater(pLevel, BlockPos.containing(hitResult.getLocation().add(0, 1, 0)), 5);
                        explode(pPlayer, BlockPos.containing(hitResult.getLocation().add(0, 2, 0)));
                    } else {
                        summonCracksMark(pLevel, BlockPos.containing(entityHitResult.getLocation().add(0, 1, 0)), 200, 8);
                        createCrater(pLevel, BlockPos.containing(entityHitResult.getLocation().add(0, 1, 0)), 5);
                        explode(pPlayer, BlockPos.containing(entityHitResult.getLocation().add(0, 2, 0)));
                    }

                    ServerPlayHandler.bigFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;

                case 3:
                    double attackRange = 7.0;
                    double sweepAngle = Math.toRadians(75);

                    playerPos = pPlayer.position();
                    lookVec = pPlayer.getLookAngle();

                    List<LivingEntity> entities = pLevel.getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(attackRange));

                    for (LivingEntity entity : entities) {
                        Vec3 entityPos = entity.position().subtract(playerPos);
                        double angle = Math.acos(entityPos.normalize().dot(lookVec.normalize()));

                        if (angle < sweepAngle / 2 && entity != pPlayer) {
                            float customDamage = 0.2F;
                            entity.hurt(pPlayer.damageSources().generic(), ServerPlayHandler.calculateCustomDamage(pPlayer, customDamage) / 1.5F);
                            entity.invulnerableTime = 2;
                            entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 5, 0, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 4, false, false));
                            entity.addEffect(new MobEffectInstance(ModEffects.STUNNED.get(), 5, 4, false, false));

                            pPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, 4, false, false));

                            if (!entity.level().isClientSide) {
                                ((ServerLevel) entity.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(), entity.getZ(), 1, 0.3, entity.getBbHeight(), 0.3, 0.2);
                            }
                            if (!pPlayer.level().isClientSide) {
                                ServerLevel serverLevel = (ServerLevel) pPlayer.level();
                                serverLevel.sendParticles(ParticleTypes.CLOUD, entity.getX(), entity.getBbHeight() * 0.5, entity.getZ(), 12, 0.2, 0.0, 0.3, 0.2);
                            }
                        }
                    }

                    offsetX = lookVec.x * 4.6;
                    offsetY = lookVec.y * 1.5 + pPlayer.getEyeHeight();
                    offsetZ = lookVec.z * 4.6;
                    playerPos = pPlayer.getPosition(1F).add(offsetX, offsetY, offsetZ);

                    if (!pLevel.isClientSide) {
                        ((ServerLevel) pLevel).sendParticles(ModParticleTypes.GENERIC_HIT.get(), playerPos.x, playerPos.y, playerPos.z, 2, 2.5, 2.5, 2.5, 0.0);
                    }

                    pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.5F, 1F);

                    actionHeavySlowdown(pPlayer);
                    ServerPlayHandler.smallFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);
                    break;

                case 4:
                    break;

                case 5:
                    pPlayer.addEffect(new MobEffectInstance(ModEffects.SLINGSHOT.get(), 45, 0, false, false));
                    pPlayer.addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 120, 0, false, false));
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0, false, false));
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 3, false, false));

                    ServerPlayHandler.bigFoodExhaustion(pPlayer);

                    pLevel.playSound(null, pPlayer, ModSounds.GOMU_NO_SLINGSHOT.get(), SoundSource.AMBIENT, 0.75F, 1F);
                    ((ServerLevel) pPlayer.level()).sendParticles(ParticleTypes.CLOUD, pPlayer.getX(), pPlayer.getY() + 0.3, pPlayer.getZ(), 15, 0.0D, 0.0D, 0.0D, 0.1D);

                    break;
                default:
                    break;
            }
        }

    }

    private static void explode(Player pPlayer, BlockPos blockPos) {
        if (!pPlayer.level().isClientSide) {
            pPlayer.level().explode(pPlayer, blockPos.getX(), blockPos.getY() + 0.5, blockPos.getZ(), 3, Level.ExplosionInteraction.NONE);
        }
    }
}

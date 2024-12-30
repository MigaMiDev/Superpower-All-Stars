package ttv.migami.mdf.common.network.fruit;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import ttv.migami.mdf.common.network.ServerPlayHandler;
import ttv.migami.mdf.entity.SummonEntity;
import ttv.migami.mdf.entity.fruit.skeleton.Bone;
import ttv.migami.mdf.entity.fruit.skeleton.BoneZone;
import ttv.migami.mdf.entity.fruit.skeleton.GasterBlaster;
import ttv.migami.mdf.init.ModEffects;
import ttv.migami.mdf.init.ModParticleTypes;
import ttv.migami.mdf.init.ModSounds;

import java.util.List;

import static ttv.migami.mdf.common.network.ServerPlayHandler.actionSlowdown;
import static ttv.migami.mdf.common.network.ServerPlayHandler.rayTrace;

/**
 * Author: MigaMi
 */
public class SkeletonFruitHandler
{

    public static void moveHandler(Player pPlayer, int move, int amount) {
        Level pLevel = pPlayer.level();

        if (!pLevel.isClientSide()) {

            Vec3 look = pPlayer.getViewVector(1F);
            GasterBlaster gasterBlaster;
            BlockPos blockPos;
            Vec3 playerPos;
            EntityHitResult entityHitResult;
            LivingEntity pTarget;

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

            switch (move) {
                case 1:
                    blockPos = rayTrace(pPlayer, 128.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    if (amount % 2 == 0) {
                        sideOffset = 2.5;
                    } else {
                        sideOffset = -2.5;
                    }
                    offsetX = rightVec.x * (sideOffset / 2) + forwardVec.x * -2; //Move the bone 1.25 blocks to the side and 1 block forward
                    offsetY = pPlayer.getEyeHeight(); //Move the blaster slightly above the player's head
                    offsetZ = rightVec.z * (sideOffset / 2) + forwardVec.z * -2; //Move the bone 1.25 blocks to the side and 1 block forward

                    playerPos = pPlayer.getPosition(1F).add(offsetX, offsetY, offsetZ);

                    actionSlowdown(pPlayer);
                    Bone bone;
                    if (entityHitResult != null) {
                        bone = new Bone(pLevel, pPlayer, playerPos.add(0, 1 ,0), entityHitResult.getEntity().getEyePosition().add(0, 1, 0));
                    }
                    else {
                        bone = new Bone(pLevel, pPlayer, playerPos, blockPos.getCenter());
                    }
                    pLevel.addFreshEntity(bone);
                    ServerPlayHandler.smallFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                case 2:
                    blockPos = rayTrace(pPlayer, 48.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    if (amount == 2) {
                        sideOffset = 2.5;
                    } else {
                        sideOffset = -2.5;
                    }
                    offsetX = rightVec.x * sideOffset + forwardVec.x * 2; //Move the blaster 2.5 blocks to the side and 2 blocks forward
                    offsetY = pPlayer.getEyeHeight() + 0.4; //Move the blaster slightly above the player's head
                    offsetZ = rightVec.z * sideOffset + forwardVec.z * 2; //Move the blaster 2.5 blocks to the side and 2 blocks forward

                    playerPos = pPlayer.getPosition(1F).add(offsetX, offsetY, offsetZ);

                    actionSlowdown(pPlayer);
                    if (entityHitResult != null && !pPlayer.isCrouching() && !(entityHitResult.getEntity() instanceof SummonEntity)) {
                        gasterBlaster = new GasterBlaster(pLevel, pPlayer, playerPos, entityHitResult.getEntity());
                    }
                    else {
                        gasterBlaster = new GasterBlaster(pLevel, pPlayer, playerPos, blockPos.getCenter());
                    }
                    pLevel.addFreshEntity(gasterBlaster);

                    ServerPlayHandler.bigFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                case 3:
                    double attackRange = 32.0;
                    double coneAngle = Math.toRadians(25);

                    blockPos = rayTrace(pPlayer, 48.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    playerPos = pPlayer.position();
                    lookVec = pPlayer.getLookAngle();

                    List<LivingEntity> entities = pLevel.getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(attackRange));

                    int levitationCount = 0;
                    int levitationMax = 1;

                    if (pPlayer.experienceLevel >= 30 || pPlayer.isCreative()) {
                        levitationMax = 5;
                    }
                    else if (pPlayer.experienceLevel >= 20) {
                        levitationMax = 4;
                    }
                    else if (pPlayer.experienceLevel >= 12) {
                        levitationMax = 3;
                    }
                    else if (pPlayer.experienceLevel >= 7) {
                        levitationMax = 2;
                    }

                    if (entityHitResult.getEntity() instanceof LivingEntity controlEntity) {
                        ((ServerLevel) pLevel).sendParticles(ModParticleTypes.SKELETON_CONTROL_PARTICLE.get(), controlEntity.getX(), controlEntity.getY(), controlEntity.getZ(), 32, 1, 0.4, 1, 1);
                        pLevel.playSound(controlEntity, controlEntity.blockPosition(), ModSounds.GASTER_BLASTER_PRIME.get(), SoundSource.PLAYERS, 1F, 1F);

                        controlEntity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 10, 24, false, false));
                        controlEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false));
                        controlEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false));
                        controlEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 6, false, false));
                        controlEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 6, false, false));
                        controlEntity.addEffect(new MobEffectInstance(ModEffects.STUNNED.get(), 40, 4, false, false));
                    }

                    for (LivingEntity entity : entities) {
                        if (levitationCount >= levitationMax) {
                            break;
                        }

                        Vec3 entityPos = entity.position().subtract(playerPos);
                        double angle = Math.acos(entityPos.normalize().dot(lookVec.normalize()));

                        if (angle < coneAngle / 2 && entity != pPlayer) {
                            ((ServerLevel) pLevel).sendParticles(ModParticleTypes.SKELETON_CONTROL_PARTICLE.get(), entity.getX(), entity.getY(), entity.getZ(), 32, 1, 0.4, 1, 1);
                            pLevel.playSound(entity, entity.blockPosition(), ModSounds.GASTER_BLASTER_PRIME.get(), SoundSource.PLAYERS, 1F, 1F);

                            entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 10, 24, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 6, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 6, false, false));

                            levitationCount++;
                        }
                    }

                    ServerPlayHandler.bigFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                case 4:
                    blockPos = rayTrace(pPlayer, 32.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    int gasterAmount = 1;
                    if (pPlayer.experienceLevel >= 30 || pPlayer.isCreative()) {
                        gasterAmount = 4;
                    }
                    else if (pPlayer.experienceLevel >= 20) {
                        gasterAmount = 3;
                    }
                    else if (pPlayer.experienceLevel >= 10) {
                        gasterAmount = 2;
                    }

                    if (entityHitResult != null) {
                        pTarget = (LivingEntity) entityHitResult.getEntity();
                        Vec3 pGasterPos = pTarget.getPosition(1F);
                        if (pTarget.onGround()) {
                            pLevel.addFreshEntity(new BoneZone(pLevel, pPlayer, BlockPos.containing(entityHitResult.getLocation()), (float) look.x));
                        }
                        else {
                            // Spawn Gaster Blasters in a triangle formation
                            double angleIncrement = Math.PI * 2 / 4;
                            double radius = 4.0;
                            for (int i = 0; i < gasterAmount; i++) {
                                double angle = angleIncrement * i;
                                offsetX = Math.cos(angle) * radius;
                                offsetZ = Math.sin(angle) * radius;
                                Vec3 blasterPos = pGasterPos.add(offsetX, 3, offsetZ);

                                gasterBlaster = new GasterBlaster(pLevel, pPlayer, blasterPos, entityHitResult.getEntity());
                                pLevel.addFreshEntity(gasterBlaster);
                            }
                        }
                    }
                    else {
                        pLevel.addFreshEntity(new BoneZone(pLevel, pPlayer, blockPos, (float) look.x));
                    }

                    ServerPlayHandler.largeFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                case 5:
                    blockPos = rayTrace(pPlayer, 14.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    ((ServerLevel) pLevel).sendParticles(ModParticleTypes.SKELETON_CONTROL_PARTICLE.get(), pPlayer.getX(), pPlayer.getY() + 0.5, pPlayer.getZ(), 32, 0.3, 0.4, 0.3, 1);
                    pLevel.playSound(null, blockPos, ModSounds.BLINK.get(), SoundSource.PLAYERS, 2F, 1F);

                    if (entityHitResult != null) {
                        pTarget = (LivingEntity) entityHitResult.getEntity();
                        pPlayer.teleportTo(pTarget.getX() + 0.5, pTarget.getY() + 3.5, pTarget.getZ() + 0.5);
                        pTarget.removeEffect(MobEffects.LEVITATION);
                        pTarget.removeEffect(MobEffects.SLOW_FALLING);
                        pTarget.removeEffect(MobEffects.GLOWING);
                        pTarget.hurt(pPlayer.damageSources().mobAttack(pPlayer), 0.1F);
                        pTarget.push(0, -2, 0);
                        pLevel.playSound(null, blockPos, ModSounds.GASTER_BLASTER_PRIME.get(), SoundSource.PLAYERS, 2F, 1F);
                        pPlayer.lookAt(EntityAnchorArgument.Anchor.FEET, pTarget.getEyePosition());
                    }
                    else {
                        pPlayer.teleportTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
                    }
                    ((ServerLevel) pLevel).sendParticles(ModParticleTypes.SKELETON_CONTROL_PARTICLE.get(), pPlayer.getX(), pPlayer.getY() + 0.5, pPlayer.getZ(), 32, 0.3, 0.4, 0.3, 1);
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false));

                    ServerPlayHandler.mediumFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                default:
                    break;
            }
        }

    }

}

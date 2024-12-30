package ttv.migami.mdf.common.network.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import ttv.migami.mdf.common.Fruit;
import ttv.migami.mdf.common.network.ServerPlayHandler;
import ttv.migami.mdf.entity.fruit.fire.Fireball;
import ttv.migami.mdf.entity.fruit.fire.LargeFireball;
import ttv.migami.mdf.init.ModEffects;

import java.util.List;

import static ttv.migami.mdf.common.network.ServerPlayHandler.rayTrace;
import static ttv.migami.mdf.common.network.ServerPlayHandler.throwPlayerForward;
import static ttv.migami.mdf.entity.fx.ScorchMarkEntity.summonScorchMark;

/**
 * Author: MigaMi
 */
public class FireFruitHandler
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
            Fireball fireball;
            LargeFireball largeFireball;

            switch (move) {
                case 1:
                    blockPos = rayTrace(pPlayer, 128.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    sideOffset = 1.0;
                    if (randomChance < 0.5) {
                        sideOffset *= -1;
                    }

                    offsetX = rightVec.x * (sideOffset / 2) + forwardVec.x * -2;
                    offsetY = pPlayer.getEyeHeight() - 1.25;
                    offsetZ = rightVec.z * (sideOffset / 2) + forwardVec.z * -2;

                    playerPos = pPlayer.getPosition(1F).add(offsetX, offsetY, offsetZ);

                    if (entityHitResult != null) {
                        fireball = new Fireball(pLevel, pPlayer, playerPos, entityHitResult.getEntity().getEyePosition().add(0, 0.8, 0), false);
                    }
                    else {
                        fireball = new Fireball(pLevel, pPlayer, playerPos, blockPos.getCenter(), false);
                    }
                    pLevel.addFreshEntity(fireball);

                    pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.7F, 1F);
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, false));

                    ServerPlayHandler.actionSlowdown(pPlayer);
                    ServerPlayHandler.smallFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                    
                case 2:
                    blockPos = rayTrace(pPlayer, 128.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    sideOffset = 1.0;
                    if (randomChance < 0.5) {
                        sideOffset *= -1;
                    }

                    offsetX = rightVec.x * (sideOffset / 2) + forwardVec.x * -2;
                    offsetY = pPlayer.getEyeHeight() - 1.25;
                    offsetZ = rightVec.z * (sideOffset / 2) + forwardVec.z * -2;

                    playerPos = pPlayer.getPosition(1F).add(offsetX, offsetY, offsetZ);

                    if (entityHitResult != null) {
                        fireball = new Fireball(pLevel, pPlayer, playerPos, entityHitResult.getEntity().getEyePosition().add(0, 0.8, 0), true);
                    }
                    else {
                        fireball = new Fireball(pLevel, pPlayer, playerPos, blockPos.getCenter(), true);
                    }
                    pLevel.addFreshEntity(fireball);

                    pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 2F, 1F);
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, false));

                    ServerPlayHandler.actionSlowdown(pPlayer);
                    ServerPlayHandler.bigFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;

                case 3:
                    if (!pLevel.isClientSide) {
                        ((ServerLevel) pLevel).sendParticles(ParticleTypes.LAVA, pPlayer.getX(), pPlayer.getY() + 0.0, pPlayer.getZ(), 32, 6, 0, 6, 0.2);
                    }

                    double radius = 7; // This will cover a 7x7 area centered on the player
                    AABB areaOfEffect = new AABB(pPlayer.getX() - radius, pPlayer.getY() - radius /2 , pPlayer.getZ() - radius, pPlayer.getX() + radius, pPlayer.getY() + radius / 2, pPlayer.getZ() + radius);
                    List<Entity> entitiesArea = pLevel.getEntities(pPlayer, areaOfEffect);
                    for (Entity entity : entitiesArea) {
                        if (entity instanceof LivingEntity livingEntity && entity != pPlayer) {
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 10, 32, false, false));
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 50, 0, false, false));
                            livingEntity.addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 80, 0, false, false));
                            livingEntity.setSecondsOnFire(5);
                            if (livingEntity.hasEffect(ModEffects.FLOWER_FRUIT.get())) {
                                livingEntity.setSecondsOnFire(9);
                            }
                            livingEntity.hurt(pPlayer.damageSources().playerAttack(pPlayer), ServerPlayHandler.calculateCustomDamage(pPlayer, 1.0F));
                        }
                    }
                    //if(pPlayer.isCrouching()) {
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 13, 32, false, false));
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 13, 4, false, false));
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false));
                        pPlayer.addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 100, 0, false, false));
                    //}
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, false));

                    summonScorchMark(pLevel, pPlayer.getOnPos().above(), 200, 10);
                    //createCrater(pLevel, pPlayer.getOnPos(), 6);

                    ServerPlayHandler.actionHeavySlowdown(pPlayer);
                    ServerPlayHandler.largeFoodExhaustion(pPlayer);

                    pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.8F, 1F);

                    break;

                case 4:
                    blockPos = rayTrace(pPlayer, 128.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    // Get the player's pitch to determine if they are looking downwards
                    float pitch = pPlayer.getXRot(); // pitch angle (positive is downwards)

                    Vec3 spawnPosition;
                    if (pitch > 45.0F) { // If the player is looking more than 45 degrees downwards
                        // Get the player's look vector and adjust the spawn position 2 blocks in front
                        Vec3 lookDirection = pPlayer.getLookAngle().normalize();
                        spawnPosition = pPlayer.position().add(lookDirection.scale(2)); // 2 blocks in front of the player
                    } else {
                        // Spawn above the player if they are not looking downwards
                        offsetY = pPlayer.getEyeHeight() + 2;
                        spawnPosition = pPlayer.position().add(0, offsetY, 0); // 2 blocks above the player
                    }

                    if (entityHitResult != null) {
                        largeFireball = new LargeFireball(pLevel, pPlayer, spawnPosition, entityHitResult.getEntity().getEyePosition().add(0, 0.8, 0), true);
                    } else {
                        largeFireball = new LargeFireball(pLevel, pPlayer, spawnPosition, blockPos.getCenter(), true);
                    }

                    pLevel.addFreshEntity(largeFireball);

                    pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 2F, 1F);
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, false));

                    ServerPlayHandler.actionHeavySlowdown(pPlayer);
                    ServerPlayHandler.bigFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;


                case 5:
                    radius = 4; // This will cover a 4x4 area centered on the player
                    areaOfEffect = new AABB(pPlayer.getX() - radius, pPlayer.getY() - radius /2 , pPlayer.getZ() - radius, pPlayer.getX() + radius, pPlayer.getY() + radius / 2, pPlayer.getZ() + radius);
                    entitiesArea = pLevel.getEntities(pPlayer, areaOfEffect);

                    double force = 1;
                    lookVec = pPlayer.getLookAngle();
                    Vec3 motion = lookVec.scale(force);

                    for (Entity entity : entitiesArea) {
                        if (entity instanceof LivingEntity livingEntity && entity != pPlayer) {
                            if (livingEntity.hasEffect(ModEffects.FLOWER_FRUIT.get())) {
                                livingEntity.setSecondsOnFire(9);
                            }
                            livingEntity.setSecondsOnFire(3);
                            livingEntity.hurt(pPlayer.damageSources().playerAttack(pPlayer), ServerPlayHandler.calculateCustomDamage(pPlayer, 1.0F));
                            throwPlayerForward(livingEntity, motion);
                        }
                    }

                    throwPlayerForward(pPlayer, motion);
                    pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 2F, 1F);
                    pPlayer.addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 100, 0, false, false));
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 80, 0, false, false));
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 2, false, false));

                    ServerPlayHandler.bigFoodExhaustion(pPlayer);

                    break;
                default:
                    break;
            }
        }

    }
}

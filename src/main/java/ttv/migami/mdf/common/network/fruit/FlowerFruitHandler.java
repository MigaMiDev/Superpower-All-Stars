package ttv.migami.mdf.common.network.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import ttv.migami.mdf.common.network.ServerPlayHandler;
import ttv.migami.mdf.entity.SummonEntity;
import ttv.migami.mdf.entity.fruit.flower.FlowerSpear;
import ttv.migami.mdf.entity.fruit.flower.Petal;
import ttv.migami.mdf.entity.fruit.flower.PiranhaPlant;
import ttv.migami.mdf.entity.fruit.flower.Vine;
import ttv.migami.mdf.init.ModEffects;

import java.util.List;

import static ttv.migami.mdf.common.network.ServerPlayHandler.*;

/**
 * Author: MigaMi
 */
public class FlowerFruitHandler
{

    public static void moveHandler(Player pPlayer, int move, int amount) {
        Level pLevel = pPlayer.level();

        FlowerSpear flowerSpear;
        BlockPos blockPos;
        Vec3 playerPos;
        EntityHitResult entityHitResult;
        PiranhaPlant piranhaPlant;
        Vine vine;
        Petal petal;

        Vec3 lookVec = pPlayer.getLookAngle();
        Vec3 rightVec = new Vec3(-lookVec.z, 0, lookVec.x).normalize();
        Vec3 forwardVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

        double sideOffset = 2.5;
        double randomChance = Math.random();

        RandomSource rand = RandomSource.create();

        double xOffset;
        double zOffset;

        if (randomChance < 0.5) {
            sideOffset *= -1;
        }

        double offsetX;
        double offsetY;
        double offsetZ;
        boolean right;

        if (!pLevel.isClientSide()) {
            
            switch (move) {
                case 1:
                    blockPos = rayTrace(pPlayer, 6.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    if (amount == 2) {
                        sideOffset = 1.0;
                        right = true;
                    } else {
                        sideOffset = -1.0;
                        right = false;
                    }
                    offsetX = rightVec.x * sideOffset + forwardVec.x * 1;
                    offsetZ = rightVec.z * sideOffset + forwardVec.z * 1;

                    playerPos = pPlayer.getPosition(1F).add(offsetX, 0.5, offsetZ);

                    actionSlowdown(pPlayer);
                    if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity && !(entityHitResult.getEntity() instanceof FlowerSpear)) {
                        LivingEntity target = (LivingEntity) entityHitResult.getEntity();
                        flowerSpear = new FlowerSpear(pPlayer, pLevel, playerPos, target, right);
                    }
                    else {
                        flowerSpear = new FlowerSpear(pPlayer, pLevel, playerPos, blockPos, right);
                    }
                    pLevel.addFreshEntity(flowerSpear);

                    ServerPlayHandler.mediumFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);
                    break;
                case 2:
                    double attackRange = 24.0;
                    double coneAngle = Math.toRadians(45);

                    blockPos = rayTrace(pPlayer, 24.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    playerPos = pPlayer.position();
                    lookVec = pPlayer.getLookAngle();

                    List<LivingEntity> entities = pLevel.getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(attackRange));

                    int grappleCount = 0;
                    int grappleMax = 1;

                    if (pPlayer.experienceLevel >= 30 || pPlayer.isCreative()) {
                        grappleMax = 5;
                    }
                    else if (pPlayer.experienceLevel >= 20) {
                        grappleMax = 4;
                    }
                    else if (pPlayer.experienceLevel >= 12) {
                        grappleMax = 3;
                    }
                    else if (pPlayer.experienceLevel >= 7) {
                        grappleMax = 2;
                    }

                    //if (entityHitResult  != null) {
                    PiranhaPlant piranhaPlant2 = new PiranhaPlant(pLevel, pPlayer, blockPos, 100);
                        pLevel.addFreshEntity(piranhaPlant2);
                    //}

                    for (LivingEntity entity : entities) {
                        if (grappleCount >= grappleMax) {
                            break;
                        }

                        Vec3 entityPos = entity.position().subtract(playerPos);
                        double angle = Math.acos(entityPos.normalize().dot(lookVec.normalize()));

                        if (angle < coneAngle / 2 && entity != pPlayer && !(entity instanceof SummonEntity)) {
                            piranhaPlant = new PiranhaPlant(pLevel, pPlayer, entity.getOnPos(), 100);
                            pLevel.addFreshEntity(piranhaPlant);

                            grappleCount++;
                        }
                    }

                    ServerPlayHandler.bigFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                case 3:
                    Vec3 lookDirection = pPlayer.getLookAngle().normalize();
                    Vec3 startPosition = pPlayer.position();

                    for (int i = 0; i < 6; i++) {
                        if (i != 0) {
                            Vec3 spawnPosition = startPosition.add(lookDirection.scale(6 * i));
                            spawnPosition = new Vec3(spawnPosition.x, pPlayer.getY(), spawnPosition.z);
                            vine = new Vine(pPlayer, pLevel, spawnPosition);
                            pLevel.addFreshEntity(vine);
                        }
                    }

                    actionHeavySlowdown(pPlayer);
                    ServerPlayHandler.largeFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                case 4:
                    blockPos = rayTrace(pPlayer, 48.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    xOffset = rand.nextDouble() * 8 - 4;
                    zOffset = rand.nextDouble() * 8 - 4;

                    actionSlowdown(pPlayer);

                    if (entityHitResult != null) {
                        if (rand.nextDouble() > 0.7) {
                            petal = new Petal(pLevel, pPlayer, entityHitResult.getLocation());
                            pLevel.addFreshEntity(petal);
                        }
                        petal = new Petal(pLevel, pPlayer, entityHitResult.getLocation().add(xOffset, 0, zOffset));
                        pLevel.addFreshEntity(petal);
                    }
                    else {
                        petal = new Petal(pLevel, pPlayer, blockPos.getCenter().add(xOffset, 0, zOffset));
                        pLevel.addFreshEntity(petal);
                    }
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 1, false, false));
                    pPlayer.addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 60, 0, false, false));

                    ServerPlayHandler.smallFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);
                    break;
                default:
                    break;
            }
        }

    }

}

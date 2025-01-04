package ttv.migami.nep.common.network.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import ttv.migami.nep.common.Fruit;
import ttv.migami.nep.common.network.ServerPlayHandler;
import ttv.migami.nep.entity.fruit.spider.*;
import ttv.migami.nep.init.ModEffects;

import static ttv.migami.nep.common.network.ServerPlayHandler.*;

/**
 * Author: MigaMi
 */
public class SpiderFruitHandler
{
    private static Fruit fruit;
    private static Fruit.ZAction zMove;
    private static Fruit.XAction xMove;
    private static Fruit.CAction cMove;
    private static Fruit.VAction vMove;
    private static Fruit.RAction rMove;

    public static void moveHandler(Player pPlayer, int move, int amount) {
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
                    blockPos = rayTrace(pPlayer, 128.0D);

                    if (amount % 2 == 0) {
                        sideOffset = 2.5;
                    } else {
                        sideOffset = -2.5;
                    }
                    offsetX = rightVec.x * (sideOffset / 2) + forwardVec.x * -2;
                    offsetY = pPlayer.getEyeHeight();
                    offsetZ = rightVec.z * (sideOffset / 2) + forwardVec.z * -2;

                    //playerPos = pPlayer.getPosition(1F).add(offsetX, offsetY, offsetZ);
                    playerPos = pPlayer.getPosition(1F);

                    if (amount == 3) {
                        LeftSpiderString string;
                        string = new LeftSpiderString(pLevel, pPlayer, playerPos, blockPos.getCenter());
                        pLevel.addFreshEntity(string);
                    }
                    if (amount == 2) {
                        RightSpiderString string;
                        string = new RightSpiderString(pLevel, pPlayer, playerPos, blockPos.getCenter());
                        pLevel.addFreshEntity(string);
                    }
                    if (amount == 1) {
                        FireSpiderString string;
                        string = new FireSpiderString(pLevel, pPlayer, playerPos, blockPos.getCenter());
                        pLevel.addFreshEntity(string);
                    }

                    actionSlowdown(pPlayer);
                    ServerPlayHandler.mediumFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                case 2:
                    blockPos = rayTrace(pPlayer, 128.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    TeaCup teaCup;
                    TeaKettle teaKettle;
                    Croissant croissant;

                    sideOffset = 1.0;
                    if (randomChance < 0.5) {
                        sideOffset *= -1;
                    }

                    offsetX = rightVec.x * (sideOffset / 2) + forwardVec.x * 2;
                    offsetY = pPlayer.getEyeHeight() - 0.4;
                    offsetZ = rightVec.z * (sideOffset / 2) + forwardVec.z * 2;

                    playerPos = pPlayer.getPosition(1F).add(offsetX, offsetY, offsetZ);

                    actionSlowdown(pPlayer);

                    double randomValue = Math.random();

                    if (randomValue < 1.0 / 3.0) {
                        if (entityHitResult != null) {
                            teaKettle = new TeaKettle(pLevel, pPlayer, playerPos, entityHitResult.getEntity().getEyePosition());
                        } else {
                            teaKettle = new TeaKettle(pLevel, pPlayer, playerPos, blockPos.getCenter());
                        }
                        pLevel.addFreshEntity(teaKettle);

                    } else if (randomValue < 2.0 / 3.0) {
                        if (entityHitResult != null) {
                            teaCup = new TeaCup(pLevel, pPlayer, playerPos, entityHitResult.getEntity().getEyePosition());
                        } else {
                            teaCup = new TeaCup(pLevel, pPlayer, playerPos, blockPos.getCenter());
                        }
                        pLevel.addFreshEntity(teaCup);
                    } else {
                        if (entityHitResult != null) {
                            croissant = new Croissant(pLevel, pPlayer, playerPos, entityHitResult.getEntity().getEyePosition());
                        } else {
                            croissant = new Croissant(pLevel, pPlayer, playerPos, blockPos.getCenter());
                        }
                        pLevel.addFreshEntity(croissant);
                    }

                    pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 2F, 1F);

                    ServerPlayHandler.smallFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);
                    break;
                case 3:
                    blockPos = rayTrace(pPlayer, 64.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    SpiderFang spiderFang;
                    actionSlowdown(pPlayer);

                    double radius = 8.5;

                    int spawnCount = 1;

                    for (int i = 0; i < spawnCount; i++) {
                        double randomAngle = Math.toRadians(pLevel.random.nextDouble() * 360);

                        offsetX = Math.cos(randomAngle) * radius;
                        offsetZ = Math.sin(randomAngle) * radius;

                        BlockPos spawnPos;
                        if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity && !(entityHitResult.getEntity() instanceof SpiderFang)) {
                            LivingEntity target = (LivingEntity) entityHitResult.getEntity();
                            spawnPos = target.blockPosition().offset((int) offsetX, 0, (int) offsetZ);
                            spiderFang = new SpiderFang(pPlayer, pLevel, Vec3.atLowerCornerOf(spawnPos), BlockPos.containing(entityHitResult.getLocation()));
                        } else {
                            spawnPos = blockPos.offset((int) offsetX, 0, (int) offsetZ);
                            spiderFang = new SpiderFang(pPlayer, pLevel, Vec3.atLowerCornerOf(spawnPos), blockPos);
                        }

                        spiderFang.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                        pLevel.addFreshEntity(spiderFang);
                    }

                    actionHeavySlowdown(pPlayer);
                    ServerPlayHandler.mediumFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);
                    break;
                case 4:
                    blockPos = rayTrace(pPlayer, 128.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    actionSlowdown(pPlayer);

                    //for (int i = 0; i < 10; i++) {
                    if (entityHitResult != null) {
                        StringRing ring = new StringRing(pPlayer, pLevel, entityHitResult.getLocation(), 70);
                        pLevel.addFreshEntity(ring);
                    } else {
                        StringRing ring = new StringRing(pPlayer, pLevel, blockPos, 70);
                        pLevel.addFreshEntity(ring);
                    }
                    //}

                    ServerPlayHandler.largeFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                case 5:
                    pPlayer.addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 120, 0, false, false));
                    pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 2F, 1F);

                    force = 2.5;
                    lookVec = pPlayer.getLookAngle();
                    motion = lookVec.scale(force);

                    throwPlayerForward(pPlayer, motion);
                    pPlayer.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 15, 15, false, false));

                    ServerPlayHandler.smallFoodExhaustion(pPlayer);
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

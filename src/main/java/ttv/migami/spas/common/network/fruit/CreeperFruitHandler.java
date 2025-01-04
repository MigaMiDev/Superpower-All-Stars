package ttv.migami.spas.common.network.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import ttv.migami.spas.common.network.ServerPlayHandler;
import ttv.migami.spas.entity.CustomLightningBolt;
import ttv.migami.spas.init.ModEffects;

import static ttv.migami.spas.common.network.ServerPlayHandler.*;
import static ttv.migami.spas.entity.fx.ScorchMarkEntity.summonScorchMark;
import static ttv.migami.spas.world.CraterCreator.createCrater;

/**
 * Author: MigaMi
 */
public class CreeperFruitHandler
{

    public static void moveHandler(Player pPlayer, int move, int amount) {
        Level pLevel = pPlayer.level();

        if (!pLevel.isClientSide()) {

            BlockPos blockPos;

            switch (move) {
                case 1:
                    actionHeavySlowdown(pPlayer);
                    removeCamouflage(pPlayer);
                    explodeSelf(pPlayer);
                    weakness(pPlayer);

                    ServerPlayHandler.largeFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    pPlayer.hurt(pPlayer.damageSources().generic(), 4);
                    break;
                case 2:
                    blockPos = rayTrace(pPlayer, 16.0D);
                    actionSlowdown(pPlayer);
                    explode(pPlayer, blockPos);

                    ServerPlayHandler.bigFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                case 3:
                    camouflage(pPlayer);

                    ServerPlayHandler.bigFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);

                    break;
                case 4:
                    pLevel.addFreshEntity(new CustomLightningBolt(pLevel, (int) pPlayer.getX(), (int) pPlayer.getY(), (int) pPlayer.getZ()));
                    summonScorchMark(pPlayer.level(), pPlayer.getOnPos().above(), 200, 6);
                    powerUser(pPlayer);

                    ServerPlayHandler.largeFoodExhaustion(pPlayer);
                    break;
                default:
                    break;
            }
        }

    }

    private static void explodeSelf(Player pPlayer) {
        if (!pPlayer.level().isClientSide) {
            float explosionPower = calculateExplosionPower(pPlayer);

            if (isPoweredUser(pPlayer)) {
                summonScorchMark(pPlayer.level(), pPlayer.getOnPos().above(), 200, 6);
                createCrater(pPlayer.level(), pPlayer.getOnPos(), 4);
            }

            pPlayer.level().explode(pPlayer, pPlayer.getX(), pPlayer.getY() + 0.5, pPlayer.getZ(), explosionPower, Level.ExplosionInteraction.NONE);
        }
    }

    private static void explode(Player pPlayer, BlockPos blockPos) {
        if (!pPlayer.level().isClientSide) {
            float explosionPower = calculateExplosionPower(pPlayer) / 2;

            EntityHitResult entityHitResult = ServerPlayHandler.hitEntity(pPlayer.level(), pPlayer, blockPos);
            if (entityHitResult != null) {
                pPlayer.level().explode(pPlayer, entityHitResult.getLocation().x, entityHitResult.getLocation().y + 0.5, entityHitResult.getLocation().z, explosionPower, Level.ExplosionInteraction.NONE);
            }
            else {
                pPlayer.level().explode(pPlayer, blockPos.getX(), blockPos.getY() + 0.5, blockPos.getZ(), explosionPower, Level.ExplosionInteraction.NONE);
            }
        }
    }

    private static void weakness(Player pPlayer) {
        pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2, false, true));
        pPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, true));
    }

    private static void camouflage(Player pPlayer) {
        pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1, false, true));
        pPlayer.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0, false, true));
    }

    private static void removeCamouflage(Player pPlayer) {
        pPlayer.removeEffect(MobEffects.MOVEMENT_SPEED);
        pPlayer.removeEffect(MobEffects.INVISIBILITY);
    }

    private static void powerUser(Player pPlayer) {
        pPlayer.addEffect(new MobEffectInstance(ModEffects.POWER.get(), 100, 0, false, false));
    }

    private static boolean isPoweredUser(Player pPlayer) {
        return pPlayer.hasEffect(ModEffects.POWER.get());
    }

    private static float calculateExplosionPower(Player pPlayer) {
        if (isPoweredUser(pPlayer)) {
            if (pPlayer.experienceLevel > 20 || pPlayer.isCreative()) {
                return 4F;
            }
            return 2F;
        }
        else if (pPlayer.experienceLevel > 20 || pPlayer.isCreative()) {
            return 2F;
        }
        else return 1.5F;
    }

}

package ttv.migami.mdf.common.network.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import ttv.migami.mdf.common.network.ServerPlayHandler;
import ttv.migami.mdf.entity.fruit.buster.*;
import ttv.migami.mdf.init.ModSounds;

import static ttv.migami.mdf.common.network.ServerPlayHandler.*;

/**
 * Author: MigaMi
 */
public class BusterFruitHandler
{

    public static void moveHandler(Player pPlayer, int move) {
        Level pLevel = pPlayer.level();

        if (!pLevel.isClientSide()) {

            Vec3 look = pPlayer.getViewVector(1F);
            Vec3 playerPos;
            Piano piano;
            Dynamite dynamite;
            BlockPos blockPos;
            EntityHitResult entityHitResult;

            switch (move) {
                case 1:
                    blockPos = rayTrace(pPlayer, 64.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    actionSlowdown(pPlayer);
                    if (entityHitResult != null) {
                        Entity target = entityHitResult.getEntity();
                        target.hurt(pPlayer.damageSources().mobProjectile(target, pPlayer), calculateCustomDamage(pPlayer, 1.5F));
                        if (!pPlayer.level().isClientSide) {
                            ((ServerLevel) pPlayer.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(), target.getZ(), 2, 0.3, target.getBbHeight(), 0.3, 0.2);
                        }
                        target.invulnerableTime = 0;
                    }
                    pLevel.playSound(null, pPlayer.getOnPos(), ModSounds.BUSTER_FIRE.get(), SoundSource.PLAYERS, 2F, 1F);

                    ServerPlayHandler.smallFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);
                    break;
                case 2:
                    actionSlowdown(pPlayer);

                    dynamite = new Dynamite(pLevel, pPlayer, 40);
                    dynamite.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 2F, 1.0F);
                    pLevel.addFreshEntity(dynamite);
                    pPlayer.removeEffect(MobEffects.BLINDNESS);

                    pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 2F, 1F);

                    ServerPlayHandler.bigFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);
                    break;
                case 3:
                    blockPos = rayTrace(pPlayer, 64.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    actionSlowdown(pPlayer);
                    if (entityHitResult != null) {
                        piano = new Piano(pPlayer, pLevel, entityHitResult.getLocation());
                    }
                    else {
                        piano = new Piano(pPlayer, pLevel, blockPos);
                    }
                    pLevel.addFreshEntity(piano);

                    ServerPlayHandler.largeFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);
                    break;
                case 4:
                    blockPos = rayTrace(pPlayer, 24.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pPlayer, blockPos);

                    actionHeavySlowdown(pPlayer);

                    Cactus stun;
                    Lasso lasso;
                    if (entityHitResult != null && !pPlayer.onGround()) {
                        lasso = new Lasso(pPlayer, pLevel, entityHitResult.getLocation(), 200, entityHitResult.getEntity());
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2, false, false));
                        pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0, false, false));
                        pLevel.addFreshEntity(lasso);
                        pLevel.playSound(null, pPlayer.getOnPos(), ModSounds.BUSTER_FIRE_LASSO.get(), SoundSource.PLAYERS, 2F, 1F);
                    } else if (entityHitResult != null) {
                        stun = new Cactus(pPlayer, pLevel, entityHitResult.getLocation(), 60);
                        pLevel.addFreshEntity(stun);
                        pLevel.playSound(null, stun.getOnPos(), ModSounds.BUSTER_FIRE_CACTUS_GROW.get(), SoundSource.PLAYERS, 2F, 1F);
                    } else {
                        stun = new Cactus(pPlayer, pLevel, blockPos, 60);
                        pLevel.addFreshEntity(stun);
                        pLevel.playSound(null, stun.getOnPos(), ModSounds.BUSTER_FIRE_CACTUS_GROW.get(), SoundSource.PLAYERS, 2F, 1F);
                    }

                    ServerPlayHandler.largeFoodExhaustion(pPlayer);
                    ServerPlayHandler.applyHunger(pPlayer);
                    break;
                case 5:
                    pLevel.addFreshEntity(new Buster(pPlayer, pLevel, pPlayer.getOnPos()));

                    ServerPlayHandler.largeFoodExhaustion(pPlayer);
                    break;
                default:
                    break;

            }
        }

    }

}

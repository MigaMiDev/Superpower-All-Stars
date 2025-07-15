package ttv.migami.spas.common.network.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import ttv.migami.spas.common.Fruit;
import ttv.migami.spas.common.network.ServerPlayHandler;
import ttv.migami.spas.entity.fruit.firework.CustomFireworkRocketEntity;
import ttv.migami.spas.entity.fruit.firework.DragFireworkRocketEntity;
import ttv.migami.spas.entity.fruit.firework.FirecrackerEntity;

import static ttv.migami.spas.common.network.ServerPlayHandler.*;
import static ttv.migami.spas.entity.fruit.firework.CustomFireworkRocketEntity.getColoredFireworkStack;
import static ttv.migami.spas.entity.fruit.firework.CustomFireworkRocketEntity.getFireworkStack;

/**
 * Author: MigaMi
 */
public class FireworkFruitHandler
{
    public static void moveHandler(Player pShooter, Fruit fruit, int move, int amount) {
        Level pLevel = pShooter.level();

        if (!pLevel.isClientSide()) {

            Vec3 look = pShooter.getViewVector(1F);
            FireworkRocketEntity firework;
            BlockPos blockPos;
            EntityHitResult entityHitResult;
            RandomSource rand = RandomSource.create();

            double xOffset;
            double zOffset;
            double speed;

            int type = 0;
            if(pShooter.experienceLevel >= 15 || pShooter.isCreative()) {
                type = 1;
            }

            switch (move) {
                case 1:
                    actionSlowdown(pShooter);

                    firework = new CustomFireworkRocketEntity(pLevel, getFireworkStack(false, false, type, 1), pShooter, pShooter.getX(), pShooter.getEyeY() - 0.15, pShooter.getZ(), true, ServerPlayHandler.calculateCustomDamage(pShooter, fruit.getMoveA().getDamage()));
                    speed = 1.5;
                    firework.lerpMotion(look.x * speed, look.y * speed, look.z * speed);
                    pLevel.addFreshEntity(firework);

                    ServerPlayHandler.bigFoodExhaustion(pShooter);
                    ServerPlayHandler.applyHunger(pShooter);

                    break;

                case 2:
                    blockPos = rayTrace(pShooter, 32.0D);
                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pShooter, blockPos);

                    actionSlowdown(pShooter);
                    if (entityHitResult != null) {
                        firework = new DragFireworkRocketEntity(pLevel, getFireworkStack(false, false, type, 1), pShooter, entityHitResult.getLocation().x + 0.5, entityHitResult.getLocation().y + 1, entityHitResult.getLocation().z + 0.5, ServerPlayHandler.calculateCustomDamage(pShooter, fruit.getMoveB().getDamage()));
                    }
                    else {
                        firework = new DragFireworkRocketEntity(pLevel, getFireworkStack(false, false, type, 1), pShooter, blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, ServerPlayHandler.calculateCustomDamage(pShooter, fruit.getMoveB().getDamage()));
                    }
                    pLevel.addFreshEntity(firework);

                    ServerPlayHandler.bigFoodExhaustion(pShooter);
                    ServerPlayHandler.applyHunger(pShooter);

                    break;

                case 3:
                    blockPos = rayTrace(pShooter, 24.0D);
                    xOffset = rand.nextDouble() * 2 - 1;
                    zOffset = rand.nextDouble() * 2 - 1;

                    entityHitResult = ServerPlayHandler.hitEntity(pLevel, pShooter, blockPos);

                    actionSlowdown(pShooter);
                    if (entityHitResult != null) {
                        firework = new FirecrackerEntity(pLevel, pShooter, (entityHitResult.getLocation().x + 0.5) + xOffset, entityHitResult.getLocation().y + 1, (entityHitResult.getLocation().z + 0.5) + zOffset, getColoredFireworkStack(true, true, 4, 1, 0xFFFF00, 0xFF9700), ServerPlayHandler.calculateCustomDamage(pShooter, fruit.getSpecial().getDamage()));
                    }
                    else {
                        firework = new FirecrackerEntity(pLevel, pShooter, (blockPos.getX() + 0.5) + xOffset, blockPos.getY() + 1, (blockPos.getZ() + 0.5) + zOffset, getColoredFireworkStack(true, true, 4, 1, 0xFFFF00, 0xFF9700), ServerPlayHandler.calculateCustomDamage(pShooter, fruit.getSpecial().getDamage()));
                    }
                    pLevel.addFreshEntity(firework);

                    ServerPlayHandler.mediumFoodExhaustion(pShooter);
                    ServerPlayHandler.applyHunger(pShooter);

                    break;

                case 4:
                    xOffset = rand.nextDouble() * 16 - 8;
                    zOffset = rand.nextDouble() * 16 - 8;

                    actionHeavySlowdown(pShooter);

                    firework = new DragFireworkRocketEntity(pLevel, false, getFireworkStack(false, false, 1, 1), pShooter, (pShooter.getX() + 0.5) + xOffset, pShooter.getY() + 1, (pShooter.getZ() + 0.5) + zOffset, ServerPlayHandler.calculateCustomDamage(pShooter, fruit.getUltimate().getDamage()));
                    pLevel.addFreshEntity(firework);

                    ServerPlayHandler.smallFoodExhaustion(pShooter);
                    ServerPlayHandler.applyHunger(pShooter);

                    break;


                case 5:
                    actionSlowdown(pShooter);

                    firework = new DragFireworkRocketEntity(pLevel, getFireworkStack(false, false, 0, 1), pShooter, pShooter.getX(), pShooter.getEyeY() - 0.15, pShooter.getZ(), true, ServerPlayHandler.calculateCustomDamage(pShooter, fruit.getMobility().getDamage()));
                    speed = 1.5;
                    firework.lerpMotion(look.x * speed, look.y * speed, look.z * speed);
                    pLevel.addFreshEntity(firework);

                    ServerPlayHandler.mediumFoodExhaustion(pShooter);
                    ServerPlayHandler.applyHunger(pShooter);

                    break;
                default:
                    break;
            }
        }
    }
}

package ttv.migami.mdf.common.network.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import ttv.migami.mdf.common.Fruit;
import ttv.migami.mdf.common.network.ServerPlayHandler;
import ttv.migami.mdf.entity.fruit.firework.CustomFireworkRocketEntity;
import ttv.migami.mdf.entity.fruit.firework.DragFireworkRocketEntity;
import ttv.migami.mdf.entity.fruit.firework.FirecrackerEntity;

import static ttv.migami.mdf.common.network.ServerPlayHandler.*;

/**
 * Author: MigaMi
 */
public class FireworkFruitHandler
{
    private static Fruit fruit;
    private static Fruit.ZMove zMove;
    private static Fruit.XMove xMove;
    private static Fruit.CMove cMove;
    private static Fruit.VMove vMove;
    private static Fruit.FMove fMove;

    public static void moveHandler(Player pShooter, int move) {
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

                    firework = new CustomFireworkRocketEntity(pLevel, getFireworkStack(false, false, type, 1), pShooter, pShooter.getX(), pShooter.getEyeY() - 0.15, pShooter.getZ(), true);
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
                        firework = new DragFireworkRocketEntity(pLevel, getFireworkStack(false, false, type, 1), pShooter, entityHitResult.getLocation().x + 0.5, entityHitResult.getLocation().y + 1, entityHitResult.getLocation().z + 0.5);
                    }
                    else {
                        firework = new DragFireworkRocketEntity(pLevel, getFireworkStack(false, false, type, 1), pShooter, blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5);
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
                        firework = new FirecrackerEntity(pLevel, pShooter, (entityHitResult.getLocation().x + 0.5) + xOffset, entityHitResult.getLocation().y + 1, (entityHitResult.getLocation().z + 0.5) + zOffset, getColoredFireworkStack(true, true, 4, 1, 0xFFFF00, 0xFF9700));
                    }
                    else {
                        firework = new FirecrackerEntity(pLevel, pShooter, (blockPos.getX() + 0.5) + xOffset, blockPos.getY() + 1, (blockPos.getZ() + 0.5) + zOffset, getColoredFireworkStack(true, true, 4, 1, 0xFFFF00, 0xFF9700));
                    }
                    pLevel.addFreshEntity(firework);

                    ServerPlayHandler.mediumFoodExhaustion(pShooter);
                    ServerPlayHandler.applyHunger(pShooter);

                    break;

                case 4:
                    xOffset = rand.nextDouble() * 16 - 8;
                    zOffset = rand.nextDouble() * 16 - 8;

                    actionHeavySlowdown(pShooter);

                    firework = new DragFireworkRocketEntity(pLevel, false, getFireworkStack(false, false, 1, 1), pShooter, (pShooter.getX() + 0.5) + xOffset, pShooter.getY() + 1, (pShooter.getZ() + 0.5) + zOffset);
                    pLevel.addFreshEntity(firework);

                    ServerPlayHandler.smallFoodExhaustion(pShooter);
                    ServerPlayHandler.applyHunger(pShooter);

                    break;


                case 5:
                    actionSlowdown(pShooter);

                    firework = new DragFireworkRocketEntity(pLevel, getFireworkStack(false, false, 0, 1), pShooter, pShooter.getX(), pShooter.getEyeY() - 0.15, pShooter.getZ(), true);
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

    private static ItemStack getFireworkStack(Boolean pFlicker, Boolean pTrail, int pType, int pFlight) {
        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fireworkTag = new CompoundTag();

        ListTag explosionList = new ListTag();
        CompoundTag explosion = new CompoundTag();

        explosion.putBoolean("Flicker", pFlicker);
        explosion.putBoolean("Trail", pTrail);
        /*
         * Set the type of explosion (0-4 for different shapes)
         * 0 - Small
         * 1 - Large
         * 2 - Star
         * 3 - Creeper
         * 4 - Burst
         */
        explosion.putByte("Type", (byte) pType);
        explosion.putIntArray("Colors", new int[]{getRandomColor(), getRandomColor()});
        explosionList.add(explosion);
        fireworkTag.putByte("Flight", (byte) pFlight);
        fireworkTag.put("Explosions", explosionList);

        CompoundTag fireworkItemTag = new CompoundTag();
        fireworkItemTag.put("Fireworks", fireworkTag);
        fireworkStack.setTag(fireworkItemTag);

        return fireworkStack;
    }

    private static ItemStack getColoredFireworkStack(Boolean pFlicker, Boolean pTrail, int pType, int pFlight, int pColor1, int pColor2) {
        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fireworkTag = new CompoundTag();

        ListTag explosionList = new ListTag();
        CompoundTag explosion = new CompoundTag();

        explosion.putBoolean("Flicker", pFlicker);
        explosion.putBoolean("Trail", pTrail);
        /*
         * Set the type of explosion (0-4 for different shapes)
         * 0 - Small
         * 1 - Large
         * 2 - Star
         * 3 - Creeper
         * 4 - Burst
         */
        explosion.putByte("Type", (byte) pType);
        explosion.putIntArray("Colors", new int[]{pColor1, pColor2});
        explosionList.add(explosion);
        fireworkTag.putByte("Flight", (byte) pFlight);
        fireworkTag.put("Explosions", explosionList);

        CompoundTag fireworkItemTag = new CompoundTag();
        fireworkItemTag.put("Fireworks", fireworkTag);
        fireworkStack.setTag(fireworkItemTag);

        return fireworkStack;
    }

    private static int getRandomColor() {
        RandomSource rand = RandomSource.create();
        return rand.nextInt(0xFFFFFF);
    }

}

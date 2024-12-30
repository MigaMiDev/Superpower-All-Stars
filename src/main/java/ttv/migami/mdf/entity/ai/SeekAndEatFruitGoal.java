package ttv.migami.mdf.entity.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import ttv.migami.mdf.effect.FruitEffect;
import ttv.migami.mdf.item.FruitItem;

import java.util.List;

public class SeekAndEatFruitGoal extends Goal {
    private final PathfinderMob mob;
    private final double speed;
    private ItemEntity targetFruit;

    public SeekAndEatFruitGoal(PathfinderMob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        List<ItemEntity> nearbyItems = mob.level().getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(10.0D),
            item -> item.getItem().getItem() instanceof FruitItem);

        if (!nearbyItems.isEmpty()) {
            this.targetFruit = nearbyItems.get(0);
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        boolean hasFruitEffect = this.mob.getActiveEffects().stream().anyMatch(effect -> effect.getEffect() instanceof FruitEffect);
        if (this.targetFruit != null && !hasFruitEffect) {
            this.mob.getNavigation().moveTo(this.targetFruit, this.speed);

            if (this.mob.distanceToSqr(this.targetFruit) < 2.0D) {
                applyFruitEffects(this.targetFruit.getItem());
                if (this.targetFruit.getItem().isEmpty()) {
                    this.targetFruit.discard();
                }
                this.mob.level().playSound(null, this.mob.getOnPos(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 0 , 1);
                stop();
                //this.mob.goalSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof SeekAndEatFruitGoal);
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetFruit != null && this.targetFruit.isAlive();
    }

    private void applyFruitEffects(ItemStack fruitStack) {
        if (fruitStack.getItem() instanceof FruitItem fruitItem) {
            for (MobEffectInstance effect : fruitItem.getEffects()) {
                this.mob.addEffect(new MobEffectInstance(effect.getEffect(), -1, 0 , false, false));
            }
            this.mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0 , false, false));
        }

        fruitStack.shrink(1);
    }
}
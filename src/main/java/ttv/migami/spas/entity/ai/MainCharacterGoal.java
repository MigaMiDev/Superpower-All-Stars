package ttv.migami.spas.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import ttv.migami.spas.effect.FruitEffect;

import java.util.List;

public class MainCharacterGoal extends Goal {
    private final Villager villager;
    private LivingEntity targetEnemy;

    public MainCharacterGoal(Villager villager) {
        this.villager = villager;
    }

    @Override
    public boolean canUse() {
        boolean hasFruitEffect = this.villager.getActiveEffects().stream().anyMatch(effect ->
                effect.getEffect() instanceof FruitEffect);
        
        if (hasFruitEffect) {
            List<LivingEntity> nearbyHostiles = this.villager.level().getEntitiesOfClass(LivingEntity.class, this.villager.getBoundingBox().inflate(10.0D),
                    entity -> entity instanceof Zombie);

            if (!nearbyHostiles.isEmpty()) {
                this.targetEnemy = nearbyHostiles.get(0);
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.villager.setTarget(this.targetEnemy);
    }

    @Override
    public void stop() {
        super.stop();
        this.villager.setAggressive(false);
        this.villager.stopUsingItem();
    }

    @Override
    public void tick() {
        if (this.targetEnemy == null || !this.targetEnemy.isAlive()) {
            this.villager.setTarget(null);
            stop();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetEnemy != null && this.targetEnemy.isAlive() && this.villager.distanceToSqr(this.targetEnemy) < 100.0D;
    }
}
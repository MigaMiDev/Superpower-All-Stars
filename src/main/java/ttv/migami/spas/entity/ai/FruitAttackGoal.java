package ttv.migami.spas.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class FruitAttackGoal<PathfinderMob extends Mob> extends Goal {
    protected final PathfinderMob mob;
    protected int attackTime;
    protected int attackIntervalMin;
    protected final float attackRadiusSqr;
    protected double distanceToTarget;
    protected int seeTime;

    protected final float ATTACK_COMMON = 0.5f;         // 50% chance
    protected final float ATTACK_UNCOMMON = 0.75f;      // 25% chance
    protected final float ATTACK_RARE = 0.9f;           // 15% chance
    protected final float ATTACK_VERY_RARE = 1.0f;      // 10% chance

    public FruitAttackGoal(PathfinderMob mob, int attackIntervalMin, float attackRadius) {
        this.attackTime = -1;
        this.mob = mob;
        this.attackIntervalMin = attackIntervalMin;
        this.attackRadiusSqr = attackRadius * attackRadius;
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.attackTime = -1;
        this.mob.stopUsingItem();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.distanceToTarget = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            if (--this.attackTime <=  0 && this.seeTime >= -20 && this.seeTime >= 10) {
                chooseAttack(target);
                this.attackTime = this.attackIntervalMin;
            }

            escape(this.distanceToTarget);
            chase(this.distanceToTarget, target);
        }
    }

    /*
     * The random int goes from 0 to the number specified. Meaning that the number specified has to be 1+ than the number of attacks!
     */
    protected void chooseAttack(LivingEntity target) {
        float roll = this.mob.getRandom().nextFloat();
        if (roll < this.ATTACK_COMMON) {
            this.commonAttack(target);
        } else if (roll < this.ATTACK_UNCOMMON) {
            this.uncommonAttack(target);
        } else if (roll < this.ATTACK_RARE) {
            this.rareAttack(target);
        } else if (roll < this.ATTACK_VERY_RARE) {
            this.veryRareAttack(target);
        }
    }

    protected void commonAttack(LivingEntity target) {

    }

    protected void uncommonAttack(LivingEntity target) {

    }

    protected void rareAttack(LivingEntity target) {

    }

    protected void veryRareAttack(LivingEntity target) {

    }

    protected void escape(double distanceToTarget) {
    }

    protected void chase(double distanceToTarget, LivingEntity target) {
    }
}
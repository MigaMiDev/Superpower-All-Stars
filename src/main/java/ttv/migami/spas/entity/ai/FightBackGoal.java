package ttv.migami.spas.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

public class FightBackGoal extends Goal {
    private final PathfinderMob mob;
    private LivingEntity attacker;

    public FightBackGoal(PathfinderMob mob, LivingEntity attacker) {
        this.mob = mob;
        this.attacker = attacker;
    }

    @Override
    public boolean canUse() {
        return this.attacker != null && this.attacker.isAlive() && mob.distanceToSqr(this.attacker) < 100.0D;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacker);
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.mob.stopUsingItem();
    }

    @Override
    public void tick() {
        if (this.attacker == null || !this.attacker.isAlive()) {
            this.mob.setTarget(null);
            stop();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.attacker != null && this.attacker.isAlive() && mob.distanceToSqr(this.attacker) < 100.0D;
    }
}
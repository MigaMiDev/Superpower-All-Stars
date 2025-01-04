package ttv.migami.spas.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.spas.Config;
import ttv.migami.spas.Reference;
import ttv.migami.spas.effect.FruitEffect;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FruitAI {

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof PathfinderMob mob) {

            if (mob.getTags().contains("HungryBoi")) {
                boolean hasFruitEffect = mob.getActiveEffects().stream().anyMatch(effect -> effect.getEffect() instanceof FruitEffect);

                if (hasFruitEffect) {
                    mob.goalSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof AvoidEntityGoal<?>);

                    boolean hasHostileGoal = mob.goalSelector.getAvailableGoals().stream()
                            .anyMatch(goal -> goal.getGoal() instanceof MainCharacterGoal);

                    if (!hasHostileGoal) {
                        if (event.getEntity() instanceof Villager villager) {
                            mob.goalSelector.addGoal(1, new MainCharacterGoal(villager));
                        }
                    }
                }

                boolean fruitSeeker = mob.goalSelector.getAvailableGoals().stream()
                        .anyMatch(goal -> goal.getGoal() instanceof SeekAndEatFruitGoal);

                if (!fruitSeeker && !hasFruitEffect && Config.COMMON.world.mobsEatFruits.get()) {
                    mob.goalSelector.addGoal(3, new SeekAndEatFruitGoal(mob, 1.2D));
                }
            }
        }
    }
}
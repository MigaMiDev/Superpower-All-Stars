package ttv.migami.spas.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.spas.Reference;
import ttv.migami.spas.effect.FruitEffect;
import ttv.migami.spas.entity.ai.FightBackGoal;
import ttv.migami.spas.entity.ai.MainCharacterGoal;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobFruitEffectEvent {

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (event.getEntity() instanceof Villager villager) {
            
            if (effectInstance.getEffect() instanceof FruitEffect) {
                villager.goalSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof AvoidEntityGoal<?>);
                
                boolean hasHostileGoal = villager.goalSelector.getAvailableGoals().stream()
                        .anyMatch(goal -> goal.getGoal() instanceof MainCharacterGoal);
                
                if (!hasHostileGoal) {
                    villager.goalSelector.addGoal(1, new MainCharacterGoal(villager));
                }
            }
        }

        /*if (effectInstance.getEffect() instanceof FruitEffect && event.getEntity() instanceof PathfinderMob pathfinderMob) {
            pathfinderMob.goalSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof SeekAndEatFruitGoal);
        }*/
    }

    @SubscribeEvent
    public static void onSpecialSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getEntity() instanceof PathfinderMob mob) {
            // 5% chance to equip the trumpet instead of a bow
            if (mob.getRandom().nextFloat() < 1) {
                mob.addTag("HungryBoi");
            }
        }
    }

    /*@SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof PathfinderMob mob) {
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

            if (!fruitSeeker && !hasFruitEffect) {
                mob.goalSelector.addGoal(1, new SeekAndEatFruitGoal(mob, 1.2D));
            }
        }
    }*/

    @SubscribeEvent
    public static void onMobAttacked(LivingAttackEvent event) {
        if (event.getEntity() instanceof PathfinderMob mob && event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (!(event.getEntity() instanceof Villager)) {
                boolean hasFruitEffect = mob.getActiveEffects().stream().anyMatch(effect -> effect.getEffect() instanceof FruitEffect);

                if (hasFruitEffect) {
                    mob.setTarget(attacker);

                    boolean hasRetaliationGoal = mob.goalSelector.getAvailableGoals().stream()
                            .anyMatch(goal -> goal.getGoal() instanceof FightBackGoal);

                    if (!hasRetaliationGoal) {
                        mob.goalSelector.addGoal(1, new FightBackGoal(mob, attacker));
                    }
                }
            }
        }
    }
}
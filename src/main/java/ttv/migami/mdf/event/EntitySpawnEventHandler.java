package ttv.migami.mdf.event;

import net.minecraftforge.fml.common.Mod;
import ttv.migami.mdf.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntitySpawnEventHandler {

    /*@SubscribeEvent
    public static void onSpecialSpawn(MobSpawnEvent.FinalizeSpawn event) {
        boolean hasFruitEffect = event.getEntity().getActiveEffects().stream().anyMatch(effect ->
                effect.getEffect() instanceof FruitEffect);

        if (event.getEntity() instanceof PathfinderMob mob && !hasFruitEffect && Config.COMMON.world.mobsEatFruits.get()) {
            if (mob.level().random.nextFloat() < 1 && mob instanceof Monster) {
            // 20% for Mobs to chase after Fruits
            //if (mob.level().random.nextFloat() < 0.2) {
                mob.goalSelector.addGoal(1, new SeekAndEatFruitGoal(mob, 1.2D));
            }
        }

        //if (event.getEntity() instanceof PathfinderMob skeleton) {
        if (event.getEntity() instanceof Skeleton skeleton) {
            // 5% chance to have Skeleton Fruit
            if (skeleton.level().random.nextFloat() < 0.05) {
                skeleton.addTag("SkellyBoi");
            }
        }
    }*/
}
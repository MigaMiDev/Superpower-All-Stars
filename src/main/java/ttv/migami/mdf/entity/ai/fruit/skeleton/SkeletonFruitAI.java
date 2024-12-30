package ttv.migami.mdf.entity.ai.fruit.skeleton;

import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.init.ModEffects;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SkeletonFruitAI {

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof PathfinderMob skeleton) {

            if (skeleton.getTags().contains("SkellyBoi") && !(skeleton.hasEffect(ModEffects.SKELETON_FRUIT.get()))) {
                skeleton.addEffect(new MobEffectInstance(ModEffects.SKELETON_FRUIT.get(), -1, 0, false, true));
            }

            if (skeleton.hasEffect(ModEffects.SKELETON_FRUIT.get())) {

                boolean hasTrumpetGoal = skeleton.goalSelector.getAvailableGoals().stream()
                        .anyMatch(prioritizedGoal -> prioritizedGoal.getGoal() instanceof SkeletonFruitAttackGoal);

                if (skeleton.tickCount < 2) {
                    reassessWeaponGoal(skeleton);
                }

                if (!hasTrumpetGoal) {
                    /*skeleton.goalSelector.getAvailableGoals().removeIf(prioritizedGoal -> {
                        Goal goal = prioritizedGoal.getGoal();
                        return goal instanceof RangedBowAttackGoal || goal instanceof MeleeAttackGoal;
                    });*/

                    skeleton.goalSelector.addGoal(2, new SkeletonFruitAttackGoal(skeleton, 1.0D, 60, 12F));
                    if (skeleton.getAttribute(Attributes.FOLLOW_RANGE) != null) {
                        skeleton.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(32);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof PathfinderMob skeleton) {
            if (skeleton.hasEffect(ModEffects.SKELETON_FRUIT.get())) {
                boolean hasTrumpetGoal = skeleton.goalSelector.getAvailableGoals().stream()
                        .anyMatch(prioritizedGoal -> prioritizedGoal.getGoal() instanceof SkeletonFruitAttackGoal);

                reassessWeaponGoal(skeleton);

                if (!hasTrumpetGoal) {
                    /*skeleton.goalSelector.getAvailableGoals().removeIf(prioritizedGoal -> {
                        Goal goal = prioritizedGoal.getGoal();
                        return goal instanceof RangedBowAttackGoal || goal instanceof MeleeAttackGoal;
                    });*/

                    skeleton.goalSelector.addGoal(2, new SkeletonFruitAttackGoal(skeleton, 1.0D, 60, 12F));
                    if (skeleton.getAttribute(Attributes.FOLLOW_RANGE) != null) {
                        skeleton.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(32);
                    }
                }
            }
        }
    }

    public static void reassessWeaponGoal(PathfinderMob skeleton) {
        if (skeleton.level() != null && !skeleton.level().isClientSide) {
            //skeleton.goalSelector.removeGoal(skeleton.meleeGoal);
            //skeleton.goalSelector.removeGoal(skeleton.bowGoal);

            ItemStack itemstack = skeleton.getItemInHand(ProjectileUtil.getWeaponHoldingHand(skeleton, (item) -> {
                return item instanceof BowItem;
            }));

            if (skeleton.hasEffect(ModEffects.SKELETON_FRUIT.get())) {
                skeleton.goalSelector.addGoal(2, new SkeletonFruitAttackGoal(skeleton, 1.0D, 60, 12F));
                if (skeleton.getAttribute(Attributes.FOLLOW_RANGE) != null) {
                    skeleton.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(32);
                }
            } else if (itemstack.is(Items.BOW) && skeleton instanceof AbstractSkeleton abstractSkeleton) {
                int i = 20;
                if (abstractSkeleton.level().getDifficulty() != Difficulty.HARD) {
                    i = 40;
                }

                abstractSkeleton.bowGoal.setMinAttackInterval(i);
                abstractSkeleton.goalSelector.addGoal(4, abstractSkeleton.bowGoal);
            } else if (skeleton instanceof AbstractSkeleton abstractSkeleton) {
                skeleton.goalSelector.addGoal(4, abstractSkeleton.meleeGoal);
            }
        }
    }
}
package ttv.migami.spas.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import ttv.migami.spas.Config;
import ttv.migami.spas.Reference;
import ttv.migami.spas.SuperpowerAllStars;
import ttv.migami.spas.client.handler.ActionHandler;
import ttv.migami.spas.client.handler.MovesetHandler;
import ttv.migami.spas.common.Fruit;
import ttv.migami.spas.common.FruitDataHandler;
import ttv.migami.spas.common.FruitHandler;
import ttv.migami.spas.common.container.FruitMenu;
import ttv.migami.spas.common.container.MoveSelectionMenu;
import ttv.migami.spas.common.container.PermanentFruitsMenu;
import ttv.migami.spas.effect.FruitEffect;
import ttv.migami.spas.event.FruitFireEvent;
import ttv.migami.spas.init.ModPowers;
import ttv.migami.spas.network.message.C2SMessageAction;
import ttv.migami.spas.network.persistent.ModNetworking;
import ttv.migami.spas.network.persistent.SyncFruitsPacket;
import ttv.migami.spas.util.EffectUtils;

import java.util.ArrayList;
import java.util.List;

public class ServerPlayHandler
{
    public static void syncToClient(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();

        int currentEffectId = persistentData.getInt(FruitDataHandler.CURRENT_EFFECT_KEY);

        ListTag listTag = persistentData.getList(FruitDataHandler.PREVIOUS_EFFECTS_KEY, Tag.TAG_INT);
        List<Integer> previousEffectIds = new ArrayList<>();
        for (Tag tag : listTag) {
            previousEffectIds.add(((IntTag) tag).getAsInt());
        }

        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncFruitsPacket(currentEffectId, previousEffectIds));
    }

    /**
     * Fires the pressed action from the current fruit.
     * This is only intended for use on the logical server.
     *
     * @param player the player for who's action is to fire
     */
    public static void handleShoot(C2SMessageAction message, ServerPlayer player, int effectID, int move, int amount) {
        if (player.isSpectator())
            return;

        if (player.getUseItem().getItem() instanceof ShieldItem)
            return;

        if (FruitDataHandler.getCurrentEffect(player) != null) {
            if (MinecraftForge.EVENT_BUS.post(new FruitFireEvent.Pre(player, MobEffect.byId(effectID), move)))
                return;

            player.setYRot(Mth.wrapDegrees(message.getRotationYaw()));
            player.setXRot(Mth.clamp(message.getRotationPitch(), -90F, 90F));

            MobEffect effect = FruitDataHandler.getCurrentEffect(player);
            ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(effect);

            if (effect instanceof FruitEffect fruitEffect) {
                if (effectId != null) {
                    FruitHandler handler = ModPowers.getHandler(effectId);
                    Fruit fruit = fruitEffect.getFruit();

                    if (handler != null) {
                        handler.handle(player, fruit, move, amount);
                    } else {
                        SuperpowerAllStars.LOGGER.atWarn().log("No handler registered for effect: {}", effectId);
                    }
                } else {
                    SuperpowerAllStars.LOGGER.atWarn().log("Effect ID could not be found for the effect: {}", effect.getDescriptionId());
                }
            }

            MinecraftForge.EVENT_BUS.post(new FruitFireEvent.Post(player, MobEffect.byId(effectID), move));
        }
    }

    public static void swapPermanentFruit(Player player, int effect) {
        removeOtherFruitEffects(player);
        if (MobEffect.byId(effect) != null) {
            player.addEffect(new MobEffectInstance(MobEffect.byId(effect), -1, 0, false, false));
            MovesetHandler.get().resetCooldownsAndAmounts();
            ActionHandler.get().updateMove(0);
            FruitDataHandler.setCurrentEffect(player, MobEffect.byId(effect));
        }
    }

    public static void removeOtherFruitEffects(Player player) {
        List<MobEffect> allFruitEffects = EffectUtils.getCustomPotionEffects(Reference.MOD_ID);

        for (MobEffect fruitEffect : allFruitEffects) {
            if (player.hasEffect(fruitEffect)) {
                player.removeEffect(fruitEffect);
            }
        }
    }

    public static void messageToFruit(Player player, int effect, int move, int amount) {
        /*switch (effect) {
            case 1:
                FireworkFruitHandler.moveHandler(player, move, amount);
                break;
            case 2:
                CreeperFruitHandler.moveHandler(player, move, amount);
                break;
            case 3:
                SkeletonFruitHandler.moveHandler(player, move, amount);
                break;
            case 4:
                SquidFruitHandler.moveHandler(player, move, amount);
                break;
            case 5:
                BusterFruitHandler.moveHandler(player, move, amount);
                break;
            case 6:
                FlowerFruitHandler.moveHandler(player, move, amount);
                break;
            case 7:
                FireFruitHandler.moveHandler(player, move, amount);
                break;
            case 8:
                RubberFruitHandler.moveHandler(player, move, amount);
                break;
            case 9:
                SpiderFruitHandler.moveHandler(player, move, amount);
                break;
            default:
                break;
        }*/
    }

    public static boolean canDamage(Entity entity) {
        return entity instanceof LivingEntity;
    }

    public static BlockPos rayTrace(Player pPlayer, double distance) {
        HitResult rayTraceResult = pPlayer.pick(distance, 1.0F, false);
        return BlockPos.containing(rayTraceResult.getLocation());
    }

    public static BlockPos rayTrace(LivingEntity entity, double distance) {
        HitResult rayTraceResult = entity.pick(distance, 1.0F, false);
        return BlockPos.containing(rayTraceResult.getLocation());
    }

    public static void throwPlayerForward(LivingEntity player, Vec3 motion) {
        /*double force = 1;
        Vec3 lookVec = player.getLookAngle();
        Vec3 motion = lookVec.scale(force);*/
        player.setDeltaMovement(
                player.getDeltaMovement().add(motion.x, motion.y, motion.z)
        );
        player.hurtMarked = true;
    }

    public static void throwPlayerDownward(LivingEntity player, double force) {
        Vec3 motion = new Vec3(0, -force, 0);

        player.setDeltaMovement(
                player.getDeltaMovement().add(motion.x, motion.y, motion.z)
        );
        player.hurtMarked = true;
    }

    public static EntityHitResult hitEntity(Level pLevel, Player pPlayer, BlockPos blockPos) {
        return ProjectileUtil.getEntityHitResult(pLevel, pPlayer, pPlayer.getEyePosition(), blockPos.getCenter(), new AABB(pPlayer.getEyePosition(), blockPos.getCenter()), ServerPlayHandler::canDamage);
    }

    public static EntityHitResult hitEntity(Level pLevel, LivingEntity entity, BlockPos blockPos) {
        return ProjectileUtil.getEntityHitResult(pLevel, entity, entity.getEyePosition(), blockPos.getCenter(), new AABB(entity.getEyePosition(), blockPos.getCenter()), ServerPlayHandler::canDamage);
    }

    public static void actionSlowdown(LivingEntity pPlayer) {
        pPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 10, 0, false, false));
        pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 0, false, false));
        pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10, 4, false, false));
    }

    public static void actionHeavySlowdown(LivingEntity pPlayer) {
        pPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 10, 0, false, false));
        pPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 4, false, false));
        pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10, 4, false, false));
    }

    public static float calculateCustomDamage(Player player, float baseDamage) {
        if (FruitDataHandler.getCurrentEffect(player) instanceof FruitEffect fruitEffect) {
            double maxScaling = Config.COMMON.gameplay.maxScaling.get();
            double maxDamage = baseDamage * maxScaling;

            ResourceLocation effectLocation = ForgeRegistries.MOB_EFFECTS.getKey(fruitEffect);

            int masteryLevel = player.getPersistentData().getInt(effectLocation + "_MasteryLevel");
            float bonusDamage = masteryLevel * 0.1F;
            float damage = baseDamage + bonusDamage;

            damage = (float) Math.max(baseDamage, Math.min(damage, maxDamage));

            return damage * Config.COMMON.gameplay.damageMultiplier.get();
        }

        return baseDamage;
    }

    /*
     * Applies Food Exhaustion to the user
     * 25F drains 3 icons in 1 tick
     */
    public static void applyFoodExhaustion(Player pPlayer, float amount) {
        pPlayer.causeFoodExhaustion(amount);
    }

    public static void smallFoodExhaustion(Player pPlayer) {
        if (pPlayer.getFoodData().getFoodLevel() > 6 && Config.COMMON.gameplay.applyHunger.get()) {
            pPlayer.causeFoodExhaustion(1F);
        }
    }

    public static void mediumFoodExhaustion(Player pPlayer) {
        if (pPlayer.getFoodData().getFoodLevel() > 6 && Config.COMMON.gameplay.applyHunger.get()) {
            pPlayer.causeFoodExhaustion(3F);
        }
    }

    public static void bigFoodExhaustion(Player pPlayer) {
        if (pPlayer.getFoodData().getFoodLevel() > 6 && Config.COMMON.gameplay.applyHunger.get()) {
            pPlayer.causeFoodExhaustion(5F);
        }
    }

    public static void largeFoodExhaustion(Player pPlayer) {
        if (pPlayer.getFoodData().getFoodLevel() > 6 && Config.COMMON.gameplay.applyHunger.get()) {
            pPlayer.causeFoodExhaustion(10F);
        }
    }

    public static void applyHunger(Player pPlayer) {
        if (pPlayer.getFoodData().getFoodLevel() > 6 && Config.COMMON.gameplay.applyHunger.get()) {
            pPlayer.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20, 0, false, false));
        }
    }

    public static void handleMoveSelectionScreen(ServerPlayer player) {
        NetworkHooks.openScreen(player, new SimpleMenuProvider((windowId, playerInventory, player1) -> new MoveSelectionMenu(windowId, playerInventory), Component.translatable("container.spas.move_selection")));
    }

    public static void handlePermanentFruitsScreen(ServerPlayer player) {
        NetworkHooks.openScreen(player, new SimpleMenuProvider((windowId, playerInventory, player1) -> new PermanentFruitsMenu(windowId, playerInventory), Component.translatable("container.spas.permanent_fruits")));
    }

    public static void handleFruitScreen(ServerPlayer player) {
        NetworkHooks.openScreen(player, new SimpleMenuProvider((windowId, playerInventory, player1) -> new FruitMenu(windowId, playerInventory), Component.translatable("container.spas.fruit_menu")));
    }
}


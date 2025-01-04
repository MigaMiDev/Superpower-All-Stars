package ttv.migami.nep.common.network;

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
import ttv.migami.nep.Config;
import ttv.migami.nep.NotEnoughPowers;
import ttv.migami.nep.Reference;
import ttv.migami.nep.common.FruitDataHandler;
import ttv.migami.nep.common.FruitHandler;
import ttv.migami.nep.common.container.BlessingMenu;
import ttv.migami.nep.common.network.fruit.*;
import ttv.migami.nep.event.FruitFireEvent;
import ttv.migami.nep.init.ModPowers;
import ttv.migami.nep.network.message.C2SMessageAction;
import ttv.migami.nep.network.persistent.ModNetworking;
import ttv.migami.nep.network.persistent.SyncFruitsPacket;
import ttv.migami.nep.util.EffectUtils;

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

            if (effectId != null) {
                FruitHandler handler = ModPowers.getHandler(effectId);
                if (handler != null) {
                    handler.handle(player, move, amount);
                } else {
                    NotEnoughPowers.LOGGER.atWarn().log("No handler registered for effect: {}", effectId);
                }
            } else {
                NotEnoughPowers.LOGGER.atWarn().log("Effect ID could not be found for the effect: {}", effect.getDescriptionId());
            }

            MinecraftForge.EVENT_BUS.post(new FruitFireEvent.Post(player, MobEffect.byId(effectID), move));
        }
    }

    public static void swapBlessing(Player player, int effect) {
        removeOtherFruitEffects(player);
        if (MobEffect.byId(effect) != null) {
            player.addEffect(new MobEffectInstance(MobEffect.byId(effect), -1, 0, false, false));
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
        switch (effect) {
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
        }
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

    // Damage that scales with XP level, caps at 3 times the Base Damage or experience level 30
    public static float calculateCustomDamage(Player player, float baseDamage) {
        double maxScaling = Config.COMMON.gameplay.maxScaling.get();
        double maxDamage = baseDamage * maxScaling;
        int maxLevel = Config.COMMON.gameplay.maxLevel.get();
        // XP divisor to control scaling
        int xpDivisor = Config.COMMON.gameplay.xpDivisor.get();

        float damage;
        if (player.experienceLevel < maxLevel && !player.isCreative()) {
            damage = (float) (baseDamage + ((float) player.experienceLevel / xpDivisor) * (maxDamage - baseDamage) / (maxLevel / xpDivisor));
        } else {
            damage = (float) maxDamage;
        }

        damage = (float) Math.max(baseDamage, Math.min(damage, maxDamage));

        return damage * Config.COMMON.gameplay.damageMultiplier.get();
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

    /**
     * @param player
     */
    public static void handleBlessings(ServerPlayer player) {
        NetworkHooks.openScreen(player, new SimpleMenuProvider((windowId, playerInventory, player1) -> new BlessingMenu(windowId, playerInventory), Component.translatable("container.nep.blessings")));

        /*boolean hasFruitEffect = player.getActiveEffects().stream().anyMatch(effect -> effect.getEffect() instanceof FruitEffect);

        if (hasFruitEffect) {
            NetworkHooks.openScreen(player, new SimpleMenuProvider((windowId, playerInventory, player1) -> new BlessingMenu(windowId, playerInventory), Component.translatable("container.nep.blessings")));
        }*/
    }
}


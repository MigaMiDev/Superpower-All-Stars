package ttv.migami.spas.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import ttv.migami.spas.Config;
import ttv.migami.spas.Reference;
import ttv.migami.spas.common.FruitDataHandler;
import ttv.migami.spas.effect.FruitEffect;
import ttv.migami.spas.init.ModItems;
import ttv.migami.spas.network.PacketHandler;
import ttv.migami.spas.network.message.C2SMessageExplodePlayer;
import ttv.migami.spas.util.EffectUtils;

import javax.annotation.Nullable;
import java.util.List;

public class FruitItem extends Item {
    private final List<MobEffectInstance> effects;
    private String fruitEffect;

    public FruitItem(Properties properties, List<MobEffectInstance> effects, String fruitEffect) {
        super(properties.food(createFoodProperties(effects)));
        this.effects = effects;
        this.fruitEffect = fruitEffect;
    }

    public String getFruitEffect() {
        return this.fruitEffect;
    }

    public List<MobEffectInstance> getEffects() {
        return effects;
    }

    private static FoodProperties createFoodProperties(List<MobEffectInstance> effects) {
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(4)
                .saturationMod(0.3F)
                .alwaysEat();

        for (MobEffectInstance effect : effects) {
            builder.effect(() -> effect, 1.0F);
        }

        return builder.build();
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (Config.COMMON.gameplay.onlyOneFruitPerLife.get() && level.isClientSide &&  user instanceof Player player) {
            for (MobEffectInstance effect : this.getEffects()) {
                if (effect.getEffect() instanceof FruitEffect) {
                    if (FruitDataHandler.getCurrentEffect(player) != null && effect.getEffect() != FruitDataHandler.getCurrentEffect(player)) {
                        removeOtherFruitEffects(player);
                        PacketHandler.getPlayChannel().sendToServer(new C2SMessageExplodePlayer());

                        return super.finishUsingItem(stack, level, user);
                    }
                }
            }
        }

        if (level.isClientSide && user instanceof Player player) {
            for (MobEffectInstance effect : this.getEffects()) {
                if (effect.getEffect() instanceof FruitEffect) {
                    FruitDataHandler.clearCurrentEffect(player);
                    removeOtherFruitEffects(player);
                    FruitDataHandler.setCurrentEffect(player, effect.getEffect());
                }
            }
        }
        if (user instanceof ServerPlayer player) {
            for (MobEffectInstance effect : this.getEffects()) {
                if (effect.getEffect() instanceof FruitEffect) {
                    FruitDataHandler.clearCurrentEffect(player);
                    removeOtherFruitEffects(player);
                    FruitDataHandler.setCurrentEffect(player, effect.getEffect());
                }
            }
        }

        return super.finishUsingItem(stack, level, user);
    }

    public void removeOtherFruitEffects(Player player) {
        List<MobEffect> allFruitEffects = EffectUtils.getCustomPotionEffects(Reference.MOD_ID);

        for (MobEffect fruitEffect : allFruitEffects) {
            if (player.hasEffect(fruitEffect)) {
                player.removeEffect(fruitEffect);
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 64;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
        if (!stack.is(ModItems.BROCCOLI_VEGGIE.get())) tooltip.add(Component.translatable("info.spas.fruit_info").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("info.spas.tooltip_item" + "." + this.asItem()).withStyle(ChatFormatting.GRAY));
    }

}

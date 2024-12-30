package ttv.migami.mdf.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.util.EffectUtils;

import javax.annotation.Nullable;
import java.util.List;

public class VeggieItem extends Item {

    public VeggieItem(Properties properties, List<MobEffectInstance> effects) {
        super(properties.food(createFoodProperties(effects)));
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
        if (!level.isClientSide) {
            removeOtherFruitEffects((ServerPlayer) user);
        }
        return super.finishUsingItem(stack, level, user);
    }

    private void removeOtherFruitEffects(ServerPlayer player) {
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
        tooltip.add(Component.translatable("info.mdf.fruit_info").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("info.mdf.tooltip_item" + "." + this.asItem()).withStyle(ChatFormatting.GRAY));
    }

}

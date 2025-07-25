package ttv.migami.spas.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.spas.init.ModEffects;

@Mod.EventBusSubscriber
public class EntityHurtEvent {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingAttack(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        ResourceKey<DamageType> type = source.typeHolder().unwrapKey().orElse(null);

        if (entity.hasEffect(ModEffects.MAGMA_FRUIT.get())) {
            if (type == DamageTypes.HOT_FLOOR || type == DamageTypes.LAVA || type == DamageTypes.IN_FIRE || type == DamageTypes.ON_FIRE) {
                event.setCanceled(true);
            }
        }

        if (entity.hasEffect(ModEffects.FIRE_FRUIT.get())) {
            if (type == DamageTypes.IN_FIRE || type == DamageTypes.ON_FIRE) {
                event.setCanceled(true);
            }
        }
        if (source.getEntity() instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.MAGMA_FRUIT.get())) {
            if (type == DamageTypes.PLAYER_ATTACK || type == DamageTypes.MOB_ATTACK) entity.setSecondsOnFire(3);
        }

        if (entity.hasEffect(ModEffects.ICE_FRUIT.get())) {
            if (type == DamageTypes.FREEZE) {
                event.setCanceled(true);
            }

            if (type == DamageTypes.IN_FIRE || type == DamageTypes.ON_FIRE) {
                event.setAmount(event.getAmount() * 1.5F);
            }
        }

        if (entity.hasEffect(ModEffects.SQUID_FRUIT.get())) {
            if (type == DamageTypes.DROWN) {
                event.setCanceled(true);
            }
        }
    }
}
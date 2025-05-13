package ttv.migami.spas.entity.fruit.firework;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import ttv.migami.spas.init.ModEffects;

import java.util.*;

public class DragFireworkRocketEntity extends CustomFireworkRocketEntity {

    private List<Entity> draggedEntities = new ArrayList<>();

    public boolean dragsUser = true;

    public DragFireworkRocketEntity(Level pLevel, ItemStack pStack, Entity pShooter, double pX, double pY, double pZ, float damage) {
        super(pLevel, pX, pY, pZ, pStack);
        this.setOwner(pShooter);
        this.damage = damage;
    }

    public DragFireworkRocketEntity(Level pLevel, Boolean pDragsUser, ItemStack pStack, Entity pShooter, double pX, double pY, double pZ, float damage) {
        super(pLevel, pX, pY, pZ, pStack);
        this.setDragsUser(pDragsUser);
        this.setOwner(pShooter);
        this.damage = damage;
    }

    public DragFireworkRocketEntity(Level pLevel, ItemStack pStack, Entity pShooter, double pX, double pY, double pZ, Boolean pShotAtAngle, float damage) {
        super(pLevel, pStack, pX, pY, pZ, pShotAtAngle);
        this.setOwner(pShooter);
        this.damage = damage;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.isRemoved()) {
            List<Entity> nearbyEntities = this.level().getEntities(this, new AABB(
                    this.getX() - 2, this.getY() - 2, this.getZ() - 2,
                    this.getX() + 2, this.getY() + 2, this.getZ() + 2),
                    EntitySelector.NO_SPECTATORS);


            draggedEntities.addAll(nearbyEntities);

            for (Entity entity : draggedEntities) {
                if (entity instanceof CustomFireworkRocketEntity) {
                    return;
                }
                if (entity instanceof Player) {
                    if (entity == this.getOwner() && this.dragsUser) {
                        entity.startRiding(this, true);
                        ((Player) entity).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0, false, false));
                    }
                    else if (entity != this.getOwner()) {
                        entity.startRiding(this, true);
                        ((Player) entity).addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 6, false, false));
                        ((Player) entity).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0, false, false));
                        ((Player) entity).addEffect(new MobEffectInstance(ModEffects.STUNNED.get(), 40, 4, false, false));
                    }
                    ((Player) entity).addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 100, 0, false, false));
                }
                else {
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 100, 0, false, false));
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0, false, false));
                    }
                    entity.setDeltaMovement(this.getDeltaMovement());
                }
            }
        }
    }

    private void setDragsUser(Boolean pDragsUser) {
        this.dragsUser = pDragsUser;
    }
}
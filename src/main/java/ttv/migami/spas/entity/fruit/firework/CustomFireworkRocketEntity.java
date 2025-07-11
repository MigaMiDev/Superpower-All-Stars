package ttv.migami.spas.entity.fruit.firework;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import ttv.migami.spas.SuperpowerAllStars;
import ttv.migami.spas.common.network.ServerPlayHandler;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.OptionalInt;

public class CustomFireworkRocketEntity extends FireworkRocketEntity {

    protected float damage = 5.0F;

    public CustomFireworkRocketEntity(EntityType<? extends FireworkRocketEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public CustomFireworkRocketEntity(Level pLevel, double pX, double pY, double pZ, ItemStack pStack) {
        super(EntityType.FIREWORK_ROCKET, pLevel);
        this.life = 0;
        this.setPos(pX, pY, pZ);
        int i = 1;
        if (!pStack.isEmpty() && pStack.hasTag()) {
            this.entityData.set(DATA_ID_FIREWORKS_ITEM, pStack.copy());
            i += pStack.getOrCreateTagElement("Fireworks").getByte("Flight");
        }

        this.setDeltaMovement(this.random.triangle(0.0, 0.002297), 0.05, this.random.triangle(0.0, 0.002297));
        this.lifetime = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
    }

    public CustomFireworkRocketEntity(Level pLevel, @Nullable Entity pShooter, double pX, double pY, double pZ, ItemStack pStack) {
        this(pLevel, pX, pY, pZ, pStack);
        this.setOwner(pShooter);
    }

    public CustomFireworkRocketEntity(Level pLevel, ItemStack pStack, LivingEntity pShooter) {
        this(pLevel, pShooter, pShooter.getX(), pShooter.getY(), pShooter.getZ(), pStack);
        this.entityData.set(DATA_ATTACHED_TO_TARGET, OptionalInt.of(pShooter.getId()));
        this.attachedToEntity = pShooter;
    }

    public CustomFireworkRocketEntity(Level pLevel, ItemStack pStack, double pX, double pY, double pZ, boolean pShotAtAngle) {
        this(pLevel, pX, pY, pZ, pStack);
        this.entityData.set(DATA_SHOT_AT_ANGLE, pShotAtAngle);
    }

    public CustomFireworkRocketEntity(Level pLevel, ItemStack pStack, Entity pShooter, double pX, double pY, double pZ, boolean pShotAtAngle, float damage) {
        this(pLevel, pStack, pX, pY, pZ, pShotAtAngle);
        this.setOwner(pShooter);
        this.damage = damage;
    }

    public CustomFireworkRocketEntity(Level pLevel, ItemStack pStack, Entity pShooter, float damage, double pX, double pY, double pZ, boolean pShotAtAngle) {
        this(pLevel, pStack, pX, pY, pZ, pShotAtAngle);
        this.setOwner(pShooter);
        this.damage = damage;
    }

    @Override
    public void explode() {
        this.level().broadcastEntityEvent(this, (byte)17);
        this.gameEvent(GameEvent.EXPLODE, this.getOwner());

        float customDamage = this.damage;

        if (this.getOwner() instanceof Player) {
            Player owner = (Player) this.getOwner();
            customDamage = ServerPlayHandler.calculateCustomDamage(owner, this.damage);
        }

        this.dealExplosionDamage(customDamage);
        SuperpowerAllStars.LOGGER.atInfo().log(customDamage);
        this.discard();
    }

    public void dealExplosionDamage(float damage) {
        float explosionDamage = 0.0F;
        ItemStack itemstack = (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        CompoundTag compoundtag = itemstack.isEmpty() ? null : itemstack.getTagElement("Fireworks");
        ListTag listtag = compoundtag != null ? compoundtag.getList("Explosions", 10) : null;
        if (listtag != null && !listtag.isEmpty()) {
            explosionDamage = damage;
        }

        if (explosionDamage > 0.0F) {
            if (this.attachedToEntity != null) {
                this.attachedToEntity.hurt(this.damageSources().fireworks(this, this.getOwner()), damage);
            }


            Vec3 vec3 = this.position();
            Iterator var8 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0)).iterator();

            while(true) {
                LivingEntity livingentity;
                do {
                    do {
                        if (!var8.hasNext()) {
                            return;
                        }

                        livingentity = (LivingEntity)var8.next();
                    } while(livingentity == this.attachedToEntity);
                } while(this.distanceToSqr(livingentity) > 25.0);

                boolean flag = false;

                for(int i = 0; i < 2; ++i) {
                    Vec3 vec31 = new Vec3(livingentity.getX(), livingentity.getY(0.5 * (double)i), livingentity.getZ());
                    HitResult hitresult = this.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                    if (hitresult.getType() == HitResult.Type.MISS) {
                        flag = true;
                        break;
                    }
                }

                if (flag && livingentity != this.getOwner()) {
                    float f1 = explosionDamage * (float)Math.sqrt((5.0D - (double)this.distanceTo(livingentity)) / 5.0D);
                    livingentity.hurt(this.damageSources().fireworks(this, this.getOwner()), f1);
                    livingentity.invulnerableTime = 0;
                }
            }
        }
    }

    public static ItemStack getFireworkStack(Boolean pFlicker, Boolean pTrail, int pType, int pFlight) {
        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fireworkTag = new CompoundTag();

        ListTag explosionList = new ListTag();
        CompoundTag explosion = new CompoundTag();

        explosion.putBoolean("Flicker", pFlicker);
        explosion.putBoolean("Trail", pTrail);
        /*
         * Set the type of explosion (0-4 for different shapes)
         * 0 - Small
         * 1 - Large
         * 2 - Star
         * 3 - Creeper
         * 4 - Burst
         */
        explosion.putByte("Type", (byte) pType);
        explosion.putIntArray("Colors", new int[]{getRandomColor(), getRandomColor()});
        explosionList.add(explosion);
        fireworkTag.putByte("Flight", (byte) pFlight);
        fireworkTag.put("Explosions", explosionList);

        CompoundTag fireworkItemTag = new CompoundTag();
        fireworkItemTag.put("Fireworks", fireworkTag);
        fireworkStack.setTag(fireworkItemTag);

        return fireworkStack;
    }

    public static ItemStack getColoredFireworkStack(Boolean pFlicker, Boolean pTrail, int pType, int pFlight, int pColor1, int pColor2) {
        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fireworkTag = new CompoundTag();

        ListTag explosionList = new ListTag();
        CompoundTag explosion = new CompoundTag();

        explosion.putBoolean("Flicker", pFlicker);
        explosion.putBoolean("Trail", pTrail);
        /*
         * Set the type of explosion (0-4 for different shapes)
         * 0 - Small
         * 1 - Large
         * 2 - Star
         * 3 - Creeper
         * 4 - Burst
         */
        explosion.putByte("Type", (byte) pType);
        explosion.putIntArray("Colors", new int[]{pColor1, pColor2});
        explosionList.add(explosion);
        fireworkTag.putByte("Flight", (byte) pFlight);
        fireworkTag.put("Explosions", explosionList);

        CompoundTag fireworkItemTag = new CompoundTag();
        fireworkItemTag.put("Fireworks", fireworkTag);
        fireworkStack.setTag(fireworkItemTag);

        return fireworkStack;
    }

    private static int getRandomColor() {
        RandomSource rand = RandomSource.create();
        return rand.nextInt(0xFFFFFF);
    }

}

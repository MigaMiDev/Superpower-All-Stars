package ttv.migami.mdf.entity.fruit.buster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import ttv.migami.mdf.init.ModEntities;

import javax.annotation.Nullable;
import java.util.UUID;

public class Lasso extends Entity {
    private static final double ROTATION_SPEED = 1;
    private static final double DISTANCE = 5.0;
    private static final double OFFSET_Y = 2.5;
    public int life = 200;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    private Entity target;
    private boolean launched = false;
    private int ticksBeforeLaunch = 60;
    private Vec3 launchDirection = Vec3.ZERO;
    private BlockPos ownerPos;
    public double strength = 1;

    private static final EntityDataAccessor<Vector3f> OWNER_POS =
            SynchedEntityData.defineId(Lasso.class, EntityDataSerializers.VECTOR3);

    //change
    private static final EntityDataAccessor<Boolean> LAUNCHED =
            SynchedEntityData.defineId(Lasso.class, EntityDataSerializers.BOOLEAN);

    public Lasso(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Lasso(LivingEntity owner, Level pLevel, Vec3 targetPos, int life, Entity target) {
        super(ModEntities.LASSO.get(), pLevel);
        this.setPos(targetPos);
        this.owner = owner;
        this.life = life;
        this.target = target;

        this.setOwner(owner);
        this.setTarget(target);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();

        if (this.tickCount >= this.life || (this.getDeltaMovement() == Vec3.ZERO && launched)) {
            this.ejectPassengers();
            this.remove(RemovalReason.KILLED);
            return;
        }

        if (target != null) {
        //if (target != null && this.tickCount < ticksBeforeLaunch) {
            target.startRiding(this, true);
        }

        if (this.tickCount < ticksBeforeLaunch) {
            double angle = this.tickCount * ROTATION_SPEED;
            double offsetX = Math.cos(angle) * DISTANCE;
            double offsetZ = Math.sin(angle) * DISTANCE;

            if (owner != null) {
                this.setPos(owner.getX() + offsetX, owner.getY() + OFFSET_Y, owner.getZ() + offsetZ);
                // Track the direction the owner is looking at
                this.launchDirection = owner.getLookAngle();
                setOwnerPos(owner.getPosition(1F).toVector3f());
            }
        } else if (!launched) {
            // Launch the lasso in the tracked direction
            if (owner instanceof Player player) {
                if (player.experienceLevel == 69 && player.isCreative()) {
                    strength = 3;
                }
                else if (player.experienceLevel >= 30 || player.isCreative()) {
                    strength = 2.5D;
                }
                else if (player.experienceLevel >= 15) {
                    strength = 2;
                }
                else {
                    strength = 1;
                }
            }
            this.setDeltaMovement(launchDirection.normalize().scale(strength));
            if (this.owner != null) {
                this.setPos(owner.getX(), owner.getY() + 2, owner.getZ());
            }
            setLaunched(true);
            this.launched = true;
        }

        if (launched) {
            double nextPosX = this.getX() + this.getDeltaMovement().x();
            double nextPosY = this.getY() + this.getDeltaMovement().y();
            double nextPosZ = this.getZ() + this.getDeltaMovement().z();
            this.setPos(nextPosX, nextPosY, nextPosZ);
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04, 0));

            Vec3 nextPos = this.position().add(this.getDeltaMovement());

            BlockPos posBelow = new BlockPos((int) nextPos.x(), (int) (nextPos.y() - 0), (int) nextPos.z());
            if (!level.isEmptyBlock(posBelow)) {
                this.getPassengers().get(0).hurt(this.damageSources().fall(), 7);
                this.launched = false;
                this.setDeltaMovement(0, 0 ,0);
                this.ejectPassengers();
                this.remove(RemovalReason.KILLED);
            }
        }

    }

    protected boolean canAddPassenger(Entity pPassenger) {
        return this.getPassengers().size() <= 5;
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        this.owner = pOwner;
        this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
    }

    public void setTarget(@Nullable Entity target) {
        this.target = target;
    }

    /**
     * Returns null or the entityliving it was ignited by
     */
    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    public Vector3f getOwnerPos() {
        return this.entityData.get(OWNER_POS);
    }

    public void setOwnerPos(Vector3f pos) {
        this.entityData.set(OWNER_POS, pos);
    }

    public boolean isLaunched() {
        return this.entityData.get(LAUNCHED);
    }

    public void setLaunched(boolean launched) {
        this.entityData.set(LAUNCHED, launched);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_POS, new Vector3f(0,0,0));
        this.entityData.define(LAUNCHED, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

}

package ttv.migami.mdf.entity.fruit.buster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import ttv.migami.mdf.init.ModEntities;

import javax.annotation.Nullable;
import java.util.UUID;

public class Buster extends Horse {
    public int life = 200;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public Buster(EntityType<? extends Horse> entityType, Level world) {
        super(entityType, world);
    }

    public Buster(LivingEntity owner, Level pLevel, BlockPos blockPos) {
        super(ModEntities.BUSTER.get(), pLevel);
        this.setPos(blockPos.getCenter().add(0, 0.5, 0));
        this.owner = owner;
        super.setOwnerUUID(owner.getUUID());
        this.isTamed();
        this.isSaddled();
        this.setInvulnerable(true);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();
        this.setTamed(true);
        this.setInvulnerable(true);

        if (this.tickCount == 1) {
            this.equipSaddle(SoundSource.PLAYERS);
            if (!level.isClientSide)
            {
                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(), 64, 3.25, 1.0, 3.25, 0.05);
            }
        }

        if (this.tickCount < 10 && owner != null) {
            this.owner.startRiding(this);
        }

        if (this.tickCount >= this.life) {
            this.remove(RemovalReason.KILLED);

            if (!level.isClientSide)
            {
                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.playSound(null, this.getOnPos(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 4F, 2F);
                serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(), 64, 1.25, 1.0, 1.25, 0.05);
            }
        }
    }

    @Override
    public void openCustomInventoryScreen(Player pPlayer) {
    }

    @Override
    protected void dropEquipment() {
    }

    @Override
    protected void dropExperience() {
    }

    @Nullable
    public Buster getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public boolean canMate(Animal pOtherAnimal) {
        return false;
    }

    @Override
    protected void randomizeAttributes(RandomSource pRandom) {
        super.randomizeAttributes(pRandom);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(6.0);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.36);
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(1.0);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setVariantAndMarkings(Variant.WHITE, Markings.WHITE);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        this.owner = pOwner;
        this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
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

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.36D)
                .add(Attributes.JUMP_STRENGTH, 1.0D)
                .add(Attributes.ARMOR, 4.0D);
    }
}

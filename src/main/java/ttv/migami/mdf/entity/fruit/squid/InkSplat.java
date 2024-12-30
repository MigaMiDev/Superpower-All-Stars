package ttv.migami.mdf.entity.fruit.squid;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import ttv.migami.mdf.Config;
import ttv.migami.mdf.common.network.ServerPlayHandler;
import ttv.migami.mdf.entity.CustomProjectileEntityOld;
import ttv.migami.mdf.init.ModEntities;
import ttv.migami.mdf.init.ModSounds;

import java.util.List;
import java.util.UUID;

import static ttv.migami.mdf.entity.fx.InkMarkEntity.summonInkMark;
import static ttv.migami.mdf.entity.fx.LargeInkMarkEntity.summonLargeInkMark;

public class InkSplat extends CustomProjectileEntityOld {
    private int warmupDelayTicks;
    public int life = 100;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    public float damage = 3;
    public float customDamage = damage;
    public boolean affectedByGravity;
    public boolean explosive;

    public InkSplat(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public InkSplat(Level pLevel, Player pPlayer, Vec3 pPos, Vec3 targetPos) {
        super(ModEntities.INK_SPLAT.get(), pLevel);
        this.setPos(pPos.add(0, 1, 0));
        this.setOwner(pPlayer);
        this.owner = pPlayer;

        Vec3 dir = this.getDirection(pPlayer);
        this.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
        this.getLookAngle();
        this.affectedByGravity = true;
        double speed = 1.5F;
        this.setDeltaMovement(dir.x * speed, dir.y * speed, dir.z * speed);
        this.updateHeading();
    }

    public InkSplat(Level pLevel, Player pPlayer, Vec3 pPos, Vec3 targetPos, boolean explosive) {
        super(ModEntities.INK_SPLAT.get(), pLevel);
        this.setPos(pPos.add(0, 1, 0));
        this.setOwner(pPlayer);
        this.owner = pPlayer;

        Vec3 dir = this.getDirection(pPlayer);
        this.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
        this.getLookAngle();
        this.affectedByGravity = false;
        this.explosive = explosive;
        double speed = 3.5F;
        this.setDeltaMovement(this.getLookAngle().x * speed, this.getLookAngle().y * speed, this.getLookAngle().z * speed);
        this.updateHeading();
    }

    @Override
    public void tick()
    {
        super.tick();
        if(this.affectedByGravity)
        {
            this.setDeltaMovement(this.getDeltaMovement().add(0, this.modifiedGravity, 0));
        }
    }

    @Override
    protected void onProjectileTick()
    {
        if(this.level().isClientSide && this.tickCount < this.life) {
            if (this.tickCount > 2)
            {
                this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }
        }
    }

    @Override
    public float calculateDamage() {
        this.customDamage = this.damage;
        if (this.getOwner() instanceof Player) {
            Player owner = (Player) this.getOwner();
            this.customDamage = ServerPlayHandler.calculateCustomDamage(owner, this.damage);
        }
        return this.customDamage;
    }

    @Override
    protected void onHitEntity(Entity entity, Vec3 hitVec, Vec3 startVec, Vec3 endVec)
    {
        if (entity instanceof Display.BlockDisplay) {
            return;
        }
        int explosionRadius = 1;
        if (this.explosive) {
            Level pLevel = this.level();

            if (this.owner instanceof Player) {
                Player playerOwner = (Player) this.owner;
                if (playerOwner.experienceLevel > 20 || playerOwner.isCreative()) {
                    explosionRadius = 2;
                }
            }
            CustomProjectileEntityOld.createExplosion(this, explosionRadius, true);
            summonLargeInkMark(this.level(), this.getOnPos(), 400, 13);

            double radius = 4;
            AABB areaOfEffect = new AABB(this.getX() - radius, this.getY() - radius / 2, this.getZ() - radius, this.getX() + radius, this.getY() + radius / 2, this.getZ() + radius);
            List<Entity> entitiesArea = pLevel.getEntities(this, areaOfEffect);
            for (Entity entityAttack : entitiesArea) {
                if (entityAttack instanceof LivingEntity livingEntity) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3, false, false));
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0, false, false));
                }
            }

            if (!pLevel.isClientSide) {
                for (int i = 0; i < 4; i++) {
                    double angle = Math.random() * 2 * Math.PI;
                    double distance = 2 + Math.random() * 2; // Random distance between 2 and 4 blocks
                    double xOffset = Math.cos(angle) * distance;
                    double zOffset = Math.sin(angle) * distance;
                    double yOffset = 1 + Math.random() * 2;

                    InkSplat inkSplat = new InkSplat(ModEntities.INK_SPLAT.get(), this.level());
                    Vec3 spawnPos = new Vec3(this.getX() + xOffset, this.getY(), this.getZ() + zOffset);
                    inkSplat.setPos(spawnPos.x, spawnPos.y + yOffset, spawnPos.z);
                    inkSplat.affectedByGravity = true;
                    // Apply velocity to shoot away from the explosion center
                    Vec3 direction = new Vec3(xOffset, 0, zOffset).normalize().scale(0.5);
                    inkSplat.setDeltaMovement(direction);

                    this.level().addFreshEntity(inkSplat);
                }

                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY(), this.getZ(), 16, explosionRadius, explosionRadius, explosionRadius, 0.7);
                serverLevel.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY(), this.getZ(), 128, explosionRadius, explosionRadius, explosionRadius, 0.2);
                serverLevel.playSound(null, this.getOnPos(), ModSounds.SQUID_EXPLOSION.get(), SoundSource.PLAYERS, 8F, 1F);
            }
        } else {
            summonInkMark(this.level(), this.getOnPos(), 400, 4);
        }

        if (entity != this.owner && this.tickCount > 2) {
            entity.hurt(this.damageSources().playerAttack((Player) owner), this.customDamage);
            entity.invulnerableTime = 0;

            if (entity instanceof LivingEntity) {
                LivingEntity pTarget = (LivingEntity) entity;

                pTarget.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30, 0, false, true));
                pTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3, false, false));
                pTarget.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 6, false, false));
            }

            if (!this.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                if (this.explosive) {
                    serverLevel.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY(), this.getZ(), 16, explosionRadius, explosionRadius, explosionRadius, 0.7);
                    serverLevel.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY(), this.getZ(), 128, explosionRadius, explosionRadius, explosionRadius, 0.2);
                    serverLevel.playSound(null, this.getOnPos(), ModSounds.SQUID_EXPLOSION.get(), SoundSource.PLAYERS, 8F, 1F);
                }
                serverLevel.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY() + 1, this.getZ(), 12, 0.2, 0.0, 0.3, 0.2);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z)
    {
        int explosionRadius = 1;
        if (this.explosive) {
            Level pLevel = this.level();

            if (this.owner instanceof Player) {
                Player playerOwner = (Player) this.owner;
                if (playerOwner.experienceLevel > 20 || playerOwner.isCreative()) {
                    explosionRadius = 2;
                }
            }
            CustomProjectileEntityOld.createExplosion(this, explosionRadius, true);
            summonLargeInkMark(this.level(), this.getOnPos(), 400, 13);
            this.stainBlock(4);

            double radius = 4;
            AABB areaOfEffect = new AABB(this.getX() - radius, this.getY() - radius / 2, this.getZ() - radius, this.getX() + radius, this.getY() + radius / 2, this.getZ() + radius);
            List<Entity> entitiesArea = pLevel.getEntities(this, areaOfEffect);
            for (Entity entityAttack : entitiesArea) {
                if (entityAttack instanceof LivingEntity livingEntity) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3, false, false));
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0, false, false));
                }
            }

            if (!pLevel.isClientSide) {
                for (int i = 0; i < 4; i++) {
                    double angle = Math.random() * 2 * Math.PI;
                    double distance = 2 + Math.random() * 2; // Random distance between 2 and 4 blocks
                    double xOffset = Math.cos(angle) * distance;
                    double zOffset = Math.sin(angle) * distance;
                    double yOffset = 1 + Math.random() * 2;

                    InkSplat inkSplat = new InkSplat(ModEntities.INK_SPLAT.get(), this.level());
                    Vec3 spawnPos = new Vec3(this.getX() + xOffset, this.getY(), this.getZ() + zOffset);
                    inkSplat.setPos(spawnPos.x, spawnPos.y + yOffset, spawnPos.z);
                    inkSplat.affectedByGravity = true;
                    // Apply velocity to shoot away from the explosion center
                    Vec3 direction = new Vec3(xOffset, 0, zOffset).normalize().scale(0.5);
                    inkSplat.setDeltaMovement(direction);

                    this.level().addFreshEntity(inkSplat);
                }

                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY(), this.getZ(), 16, explosionRadius, explosionRadius, explosionRadius, 0.7);
                serverLevel.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY(), this.getZ(), 128, explosionRadius, explosionRadius, explosionRadius, 0.2);
                serverLevel.playSound(null, this.getOnPos(), ModSounds.SQUID_EXPLOSION.get(), SoundSource.PLAYERS, 8F, 1F);
            }
        } else {
            summonInkMark(this.level(), this.getOnPos(), 400, 4);
            this.stainBlock(2);
        }

        if (!this.level().isClientSide) {
            BlockPos entityPos = this.blockPosition();
            BlockPos belowPos = entityPos.below();

            ServerLevel serverLevel = (ServerLevel) this.level();

            if (this.explosive) {
                serverLevel.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY() + 2, this.getZ(), 16, 0, 0, 0, 0.7);
                serverLevel.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY(), this.getZ(), 128, explosionRadius, explosionRadius, explosionRadius, 0.2);
                serverLevel.playSound(null, this.getOnPos(), ModSounds.SQUID_EXPLOSION.get(), SoundSource.PLAYERS, 2F, 1F);
            }

            serverLevel.sendParticles(ParticleTypes.SQUID_INK, this.getX(), belowPos.getY() + 1, this.getZ(), 12, 0.2, 0.0, 0.3, 0.2);
            serverLevel.playSound(null, this.getOnPos(), ModSounds.SQUID_SPLAT.get(), SoundSource.PLAYERS, 2F, 1F);

            this.remove(RemovalReason.KILLED);
        }
    }

    public void stainBlock(int radius) {
        Level level = this.level();
        BlockPos centerPos = this.blockPosition();

        if (level instanceof ServerLevel serverLevel && Config.COMMON.gameplay.griefing.stainBlocks.get()) {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos targetPos = centerPos.offset(x, y, z);

                        if (centerPos.distSqr(targetPos) <= radius * radius) {
                            BlockState state = level.getBlockState(targetPos);

                            if (isDyeableBlock(state)) {
                                BlockState dyedState = getBlackDyedBlock(state);
                                if (dyedState != null) {
                                    serverLevel.setBlock(targetPos, dyedState, 3);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isDyeableBlock(BlockState state) {
        return state.is(Blocks.WHITE_WOOL) || state.is(Blocks.ORANGE_WOOL) || state.is(Blocks.MAGENTA_WOOL) ||
                state.is(Blocks.LIGHT_BLUE_WOOL) || state.is(Blocks.YELLOW_WOOL) || state.is(Blocks.LIME_WOOL) ||
                state.is(Blocks.PINK_WOOL) || state.is(Blocks.GRAY_WOOL) || state.is(Blocks.LIGHT_GRAY_WOOL) ||
                state.is(Blocks.CYAN_WOOL) || state.is(Blocks.PURPLE_WOOL) || state.is(Blocks.BLUE_WOOL) ||
                state.is(Blocks.BROWN_WOOL) || state.is(Blocks.GREEN_WOOL) || state.is(Blocks.RED_WOOL) ||
                state.is(Blocks.BLACK_WOOL) ||
                state.is(Blocks.WHITE_TERRACOTTA) || state.is(Blocks.ORANGE_TERRACOTTA) || state.is(Blocks.MAGENTA_TERRACOTTA) ||
                state.is(Blocks.LIGHT_BLUE_TERRACOTTA) || state.is(Blocks.YELLOW_TERRACOTTA) || state.is(Blocks.LIME_TERRACOTTA) ||
                state.is(Blocks.PINK_TERRACOTTA) || state.is(Blocks.GRAY_TERRACOTTA) || state.is(Blocks.LIGHT_GRAY_TERRACOTTA) ||
                state.is(Blocks.CYAN_TERRACOTTA) || state.is(Blocks.PURPLE_TERRACOTTA) || state.is(Blocks.BLUE_TERRACOTTA) ||
                state.is(Blocks.BROWN_TERRACOTTA) || state.is(Blocks.GREEN_TERRACOTTA) || state.is(Blocks.RED_TERRACOTTA) ||
                state.is(Blocks.BLACK_TERRACOTTA) || state.is(Blocks.TERRACOTTA) ||
                state.is(Blocks.WHITE_CONCRETE) || state.is(Blocks.ORANGE_CONCRETE) || state.is(Blocks.MAGENTA_CONCRETE) ||
                state.is(Blocks.LIGHT_BLUE_CONCRETE) || state.is(Blocks.YELLOW_CONCRETE) || state.is(Blocks.LIME_CONCRETE) ||
                state.is(Blocks.PINK_CONCRETE) || state.is(Blocks.GRAY_CONCRETE) || state.is(Blocks.LIGHT_GRAY_CONCRETE) ||
                state.is(Blocks.CYAN_CONCRETE) || state.is(Blocks.PURPLE_CONCRETE) || state.is(Blocks.BLUE_CONCRETE) ||
                state.is(Blocks.BROWN_CONCRETE) || state.is(Blocks.GREEN_CONCRETE) || state.is(Blocks.RED_CONCRETE) ||
                state.is(Blocks.BLACK_CONCRETE) ||
                state.is(Blocks.WHITE_CONCRETE_POWDER) || state.is(Blocks.ORANGE_CONCRETE_POWDER) || state.is(Blocks.MAGENTA_CONCRETE_POWDER) ||
                state.is(Blocks.LIGHT_BLUE_CONCRETE_POWDER) || state.is(Blocks.YELLOW_CONCRETE_POWDER) || state.is(Blocks.LIME_CONCRETE_POWDER) ||
                state.is(Blocks.PINK_CONCRETE_POWDER) || state.is(Blocks.GRAY_CONCRETE_POWDER) || state.is(Blocks.LIGHT_GRAY_CONCRETE_POWDER) ||
                state.is(Blocks.CYAN_CONCRETE_POWDER) || state.is(Blocks.PURPLE_CONCRETE_POWDER) || state.is(Blocks.BLUE_CONCRETE_POWDER) ||
                state.is(Blocks.BROWN_CONCRETE_POWDER) || state.is(Blocks.GREEN_CONCRETE_POWDER) || state.is(Blocks.RED_CONCRETE_POWDER) ||
                state.is(Blocks.BLACK_CONCRETE_POWDER) ||
                state.is(Blocks.WHITE_STAINED_GLASS) || state.is(Blocks.ORANGE_STAINED_GLASS) || state.is(Blocks.MAGENTA_STAINED_GLASS) ||
                state.is(Blocks.LIGHT_BLUE_STAINED_GLASS) || state.is(Blocks.YELLOW_STAINED_GLASS) || state.is(Blocks.LIME_STAINED_GLASS) ||
                state.is(Blocks.PINK_STAINED_GLASS) || state.is(Blocks.GRAY_STAINED_GLASS) || state.is(Blocks.LIGHT_GRAY_STAINED_GLASS) ||
                state.is(Blocks.CYAN_STAINED_GLASS) || state.is(Blocks.PURPLE_STAINED_GLASS) || state.is(Blocks.BLUE_STAINED_GLASS) ||
                state.is(Blocks.BROWN_STAINED_GLASS) || state.is(Blocks.GREEN_STAINED_GLASS) || state.is(Blocks.RED_STAINED_GLASS) ||
                state.is(Blocks.BLACK_STAINED_GLASS) || state.is(Blocks.GLASS) ||
                state.is(Blocks.WHITE_STAINED_GLASS_PANE) || state.is(Blocks.ORANGE_STAINED_GLASS_PANE) || state.is(Blocks.MAGENTA_STAINED_GLASS_PANE) ||
                state.is(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE) || state.is(Blocks.YELLOW_STAINED_GLASS_PANE) || state.is(Blocks.LIME_STAINED_GLASS_PANE) ||
                state.is(Blocks.PINK_STAINED_GLASS_PANE) || state.is(Blocks.GRAY_STAINED_GLASS_PANE) || state.is(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE) ||
                state.is(Blocks.CYAN_STAINED_GLASS_PANE) || state.is(Blocks.PURPLE_STAINED_GLASS_PANE) || state.is(Blocks.BLUE_STAINED_GLASS_PANE) ||
                state.is(Blocks.BROWN_STAINED_GLASS_PANE) || state.is(Blocks.GREEN_STAINED_GLASS_PANE) || state.is(Blocks.RED_STAINED_GLASS_PANE) ||
                state.is(Blocks.BLACK_STAINED_GLASS_PANE) || state.is(Blocks.GLASS_PANE);
    }

    private BlockState getBlackDyedBlock(BlockState state) {
        if (state.is(Blocks.WHITE_WOOL) || state.is(Blocks.ORANGE_WOOL) || state.is(Blocks.MAGENTA_WOOL) ||
                state.is(Blocks.LIGHT_BLUE_WOOL) || state.is(Blocks.YELLOW_WOOL) || state.is(Blocks.LIME_WOOL) ||
                state.is(Blocks.PINK_WOOL) || state.is(Blocks.GRAY_WOOL) || state.is(Blocks.LIGHT_GRAY_WOOL) ||
                state.is(Blocks.CYAN_WOOL) || state.is(Blocks.PURPLE_WOOL) || state.is(Blocks.BLUE_WOOL) ||
                state.is(Blocks.BROWN_WOOL) || state.is(Blocks.GREEN_WOOL) || state.is(Blocks.RED_WOOL) ||
                state.is(Blocks.BLACK_WOOL)) {
            return Blocks.BLACK_WOOL.defaultBlockState();
        } else if (state.is(Blocks.WHITE_TERRACOTTA) || state.is(Blocks.ORANGE_TERRACOTTA) || state.is(Blocks.MAGENTA_TERRACOTTA) ||
                state.is(Blocks.LIGHT_BLUE_TERRACOTTA) || state.is(Blocks.YELLOW_TERRACOTTA) || state.is(Blocks.LIME_TERRACOTTA) ||
                state.is(Blocks.PINK_TERRACOTTA) || state.is(Blocks.GRAY_TERRACOTTA) || state.is(Blocks.LIGHT_GRAY_TERRACOTTA) ||
                state.is(Blocks.CYAN_TERRACOTTA) || state.is(Blocks.PURPLE_TERRACOTTA) || state.is(Blocks.BLUE_TERRACOTTA) ||
                state.is(Blocks.BROWN_TERRACOTTA) || state.is(Blocks.GREEN_TERRACOTTA) || state.is(Blocks.RED_TERRACOTTA) ||
                state.is(Blocks.BLACK_TERRACOTTA) || state.is(Blocks.TERRACOTTA)) {
            return Blocks.BLACK_TERRACOTTA.defaultBlockState();
        } else if (state.is(Blocks.WHITE_CONCRETE) || state.is(Blocks.ORANGE_CONCRETE) || state.is(Blocks.MAGENTA_CONCRETE) ||
                state.is(Blocks.LIGHT_BLUE_CONCRETE) || state.is(Blocks.YELLOW_CONCRETE) || state.is(Blocks.LIME_CONCRETE) ||
                state.is(Blocks.PINK_CONCRETE) || state.is(Blocks.GRAY_CONCRETE) || state.is(Blocks.LIGHT_GRAY_CONCRETE) ||
                state.is(Blocks.CYAN_CONCRETE) || state.is(Blocks.PURPLE_CONCRETE) || state.is(Blocks.BLUE_CONCRETE) ||
                state.is(Blocks.BROWN_CONCRETE) || state.is(Blocks.GREEN_CONCRETE) || state.is(Blocks.RED_CONCRETE) ||
                state.is(Blocks.BLACK_CONCRETE)) {
            return Blocks.BLACK_CONCRETE.defaultBlockState();
        } else if (state.is(Blocks.WHITE_CONCRETE_POWDER) || state.is(Blocks.ORANGE_CONCRETE_POWDER) || state.is(Blocks.MAGENTA_CONCRETE_POWDER) ||
                state.is(Blocks.LIGHT_BLUE_CONCRETE_POWDER) || state.is(Blocks.YELLOW_CONCRETE_POWDER) || state.is(Blocks.LIME_CONCRETE_POWDER) ||
                state.is(Blocks.PINK_CONCRETE_POWDER) || state.is(Blocks.GRAY_CONCRETE_POWDER) || state.is(Blocks.LIGHT_GRAY_CONCRETE_POWDER) ||
                state.is(Blocks.CYAN_CONCRETE_POWDER) || state.is(Blocks.PURPLE_CONCRETE_POWDER) || state.is(Blocks.BLUE_CONCRETE_POWDER) ||
                state.is(Blocks.BROWN_CONCRETE_POWDER) || state.is(Blocks.GREEN_CONCRETE_POWDER) || state.is(Blocks.RED_CONCRETE_POWDER) ||
                state.is(Blocks.BLACK_CONCRETE_POWDER)) {
            return Blocks.BLACK_CONCRETE_POWDER.defaultBlockState();
        } else if (state.is(Blocks.WHITE_STAINED_GLASS) || state.is(Blocks.ORANGE_STAINED_GLASS) || state.is(Blocks.MAGENTA_STAINED_GLASS) ||
                state.is(Blocks.LIGHT_BLUE_STAINED_GLASS) || state.is(Blocks.YELLOW_STAINED_GLASS) || state.is(Blocks.LIME_STAINED_GLASS) ||
                state.is(Blocks.PINK_STAINED_GLASS) || state.is(Blocks.GRAY_STAINED_GLASS) || state.is(Blocks.LIGHT_GRAY_STAINED_GLASS) ||
                state.is(Blocks.CYAN_STAINED_GLASS) || state.is(Blocks.PURPLE_STAINED_GLASS) || state.is(Blocks.BLUE_STAINED_GLASS) ||
                state.is(Blocks.BROWN_STAINED_GLASS) || state.is(Blocks.GREEN_STAINED_GLASS) || state.is(Blocks.RED_STAINED_GLASS) ||
                state.is(Blocks.BLACK_STAINED_GLASS) || state.is(Blocks.GLASS)) {
            return Blocks.BLACK_STAINED_GLASS.defaultBlockState();
        } else if (state.is(Blocks.WHITE_STAINED_GLASS_PANE) || state.is(Blocks.ORANGE_STAINED_GLASS_PANE) || state.is(Blocks.MAGENTA_STAINED_GLASS_PANE) ||
                state.is(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE) || state.is(Blocks.YELLOW_STAINED_GLASS_PANE) || state.is(Blocks.LIME_STAINED_GLASS_PANE) ||
                state.is(Blocks.PINK_STAINED_GLASS_PANE) || state.is(Blocks.GRAY_STAINED_GLASS_PANE) || state.is(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE) ||
                state.is(Blocks.CYAN_STAINED_GLASS_PANE) || state.is(Blocks.PURPLE_STAINED_GLASS_PANE) || state.is(Blocks.BLUE_STAINED_GLASS_PANE) ||
                state.is(Blocks.BROWN_STAINED_GLASS_PANE) || state.is(Blocks.GREEN_STAINED_GLASS_PANE) || state.is(Blocks.RED_STAINED_GLASS_PANE) ||
                state.is(Blocks.BLACK_STAINED_GLASS_PANE) || state.is(Blocks.GLASS_PANE)) {
            return Blocks.BLACK_STAINED_GLASS_PANE.defaultBlockState();
        }
        return null;
    }

    @Nullable
    @Override
    public Entity getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        this.owner = pOwner;
        this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
    }

    private Vec3 getDirection(LivingEntity pShooter)
    {
        return this.getVectorFromRotation(pShooter.getXRot() - (5 / 2.0F) + random.nextFloat() * 2, pShooter.getYHeadRot() - (5 / 2.0F) + random.nextFloat() * 2);
    }

    private Vec3 getVectorFromRotation(float pitch, float yaw)
    {
        float f = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -Mth.cos(-pitch * 0.017453292F);
        float f3 = Mth.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

}

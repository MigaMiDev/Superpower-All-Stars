package ttv.migami.mdf.entity.ai.fruit.skeleton;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import ttv.migami.mdf.common.network.ServerPlayHandler;
import ttv.migami.mdf.effect.FruitEffect;
import ttv.migami.mdf.entity.SummonEntity;
import ttv.migami.mdf.entity.ai.FruitAttackGoal;
import ttv.migami.mdf.entity.fruit.skeleton.Bone;
import ttv.migami.mdf.entity.fruit.skeleton.BoneZone;
import ttv.migami.mdf.entity.fruit.skeleton.GasterBlaster;
import ttv.migami.mdf.init.ModEffects;
import ttv.migami.mdf.init.ModParticleTypes;
import ttv.migami.mdf.init.ModSounds;

import java.util.EnumSet;

import static ttv.migami.mdf.common.network.ServerPlayHandler.actionSlowdown;
import static ttv.migami.mdf.common.network.ServerPlayHandler.rayTrace;

public class SkeletonFruitAttackGoal extends FruitAttackGoal<PathfinderMob> {

    protected final PathfinderMob mob;
    protected final double speedModifier;
    protected int attackIntervalMin;
    protected final float attackRadiusSqr;
    protected int attackTime;
    protected boolean strafingClockwise;
    protected boolean strafingBackwards;
    protected int strafingTime;

    protected final float ATTACK_COMMON = 0.5f;         // 50% chance
    protected final float ATTACK_UNCOMMON = 0.75f;      // 25% chance
    protected final float ATTACK_RARE = 0.9f;           // 15% chance
    protected final float ATTACK_VERY_RARE = 1.0f;      // 10% chance

    protected int teleportCooldown;
    protected final int TELEPORT_TIME = 60;

    protected int controlTimer;
    protected final int MAX_CONTROL_TIME = 30;

    protected int boneInterval;
    protected int boneAmount;
    protected final int MAX_BONE_INTERVAL = 6;
    protected final int MAX_BONE_AMOUNT = 6;

    public SkeletonFruitAttackGoal(PathfinderMob mob, double speedModifier, int attackIntervalMin, float attackRadius) {
        super(mob, attackIntervalMin, attackRadius);
        this.attackTime = -1;
        this.strafingTime = -1;
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.teleportCooldown = -1;
        this.boneInterval = -1;
        this.boneAmount = 0;
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null && this.mob.getTarget().isAlive() && this.mob.getActiveEffects().stream().anyMatch(effect ->
                effect.getEffect() instanceof FruitEffect);
    }

    @Override
    public boolean canContinueToUse() {
        return (this.canUse() || !this.mob.getNavigation().isDone());
    }

    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.mob.stopUsingItem();
        this.teleportCooldown = -1;
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            boolean canSeeTarget = this.mob.getSensing().hasLineOfSight(target);
            boolean sawTargetPreviously = this.seeTime > 0;

            if (canSeeTarget != sawTargetPreviously) {
                this.seeTime = 0;
            }

            if (canSeeTarget) {
                ++this.seeTime;
            } else {
                this.seeTime = Math.max(this.seeTime - 1, -100);
            }


            if (this.distanceToTarget <= this.attackRadiusSqr && this.seeTime >= 20) {
                this.mob.getNavigation().stop();
                ++this.strafingTime;
            } else if (this.seeTime > -60) {
                this.mob.getNavigation().moveTo(target, this.speedModifier);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if (this.mob.getRandom().nextFloat() < 0.3) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if (this.mob.getRandom().nextFloat() < 0.3) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (this.distanceToTarget > (double)(this.attackRadiusSqr * 0.75F)) {
                    this.strafingBackwards = false;
                } else if (this.distanceToTarget < (double)(this.attackRadiusSqr * 0.25F)) {
                    this.strafingBackwards = true;
                }
            }

            // Resume Bow Attack
            if (this.mob.isUsingItem()) {
                if (!canSeeTarget && this.seeTime < -60) {
                    this.mob.stopUsingItem();
                } else if (canSeeTarget) {
                    int i = this.mob.getTicksUsingItem();
                    if (i >= 20) {
                        this.mob.stopUsingItem();
                        if (this.mob instanceof RangedAttackMob rangedAttackMob) {
                            rangedAttackMob.performRangedAttack(target, BowItem.getPowerForTime(i));

                        }
                        this.attackTime = this.attackIntervalMin;
                    }
                }
            }

            // Throw Bones
            if (--this.boneInterval <= 0 && this.boneAmount != 0) {
                this.boneInterval = this.MAX_BONE_INTERVAL;
                shootBone(target, this.boneAmount);
                --this.boneAmount;
            }

            // Control + Additional Move
            Vec3 distanceTo = target.getPosition(1F).subtract(this.mob.getEyePosition());
            Vec3 normal = distanceTo.normalize();
            if (--this.controlTimer <= 0 && target.hasEffect(MobEffects.GLOWING)) {
                // Summons 3 GasterBlasters
                if (target.getHealth() == target.getMaxHealth()) {
                    double angleIncrement = Math.PI * 2 / 4;
                    double radius = 4.0;
                    Vec3 pGasterPos = target.getPosition(1F);
                    for (int i = 0; i < 3; i++) {
                        double angle = angleIncrement * i;
                        double offsetX = Math.cos(angle) * radius;
                        double offsetZ = Math.sin(angle) * radius;
                        Vec3 blasterPos = pGasterPos.add(offsetX, 3, offsetZ);

                        GasterBlaster gasterBlaster = new GasterBlaster(this.mob.level(), target, blasterPos, target);
                        this.mob.level().addFreshEntity(gasterBlaster);
                    }
                    // Drops the Entity to BoneZone
                } else if (target.getHealth() <= 4F) {
                    target.hurt(this.mob.damageSources().mobAttack(this.mob), 0.1F);
                    target.invulnerableTime = 0;
                    removeEffect(target);
                    target.addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 40, 0, false, false));
                    this.mob.level().playSound(null, target.blockPosition(), ModSounds.GASTER_BLASTER_PRIME.get(), SoundSource.PLAYERS, 1F, 1F);
                    target.push(normal.x() * 0, -2, normal.z() * 0);
                    this.mob.level().addFreshEntity(new BoneZone(this.mob.level(), this.mob, BlockPos.containing(target.getPosition(1F)), 0));
                    // Pushes Entity back
                } else {
                    target.hurt(this.mob.damageSources().mobAttack(this.mob), 2);
                    target.invulnerableTime = 0;
                    removeEffect(target);
                    target.addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 40, 0, false, false));
                    this.mob.level().playSound(null, target.blockPosition(), ModSounds.GASTER_BLASTER_PRIME.get(), SoundSource.PLAYERS, 1F, 1F);
                    target.push(normal.x() * 4, normal.y() * 1, normal.z() * 4);
                }
                target.removeEffect(MobEffects.GLOWING);
            }

            --this.teleportCooldown;

            double targetEyeY = target.getEyeY();
            this.mob.getLookControl().setLookAt(target.getX(), targetEyeY, target.getZ());
            this.mob.lookAt(EntityAnchorArgument.Anchor.FEET, target.getBoundingBox().getCenter());
        }
    }

    @Override
    protected void commonAttack(LivingEntity target) {
        if (this.mob.getMainHandItem().getItem() != Items.AIR) {
            this.mob.startUsingItem(InteractionHand.MAIN_HAND);
        }
        else if (this.mob.getOffhandItem().getItem() != Items.AIR) {
            this.mob.startUsingItem(InteractionHand.OFF_HAND);
        } else {
            this.uncommonAttack(target);
        }
    }

    @Override
    protected void uncommonAttack(LivingEntity target) {
        this.boneInterval = this.MAX_BONE_INTERVAL;
        this.boneAmount = this.mob.getRandom().nextInt(this.MAX_BONE_AMOUNT);
    }

    @Override
    protected void rareAttack(LivingEntity target) {
        this.gasterBlaster();
    }

    @Override
    protected void veryRareAttack(LivingEntity target) {
        this.attackTime = this.MAX_CONTROL_TIME;
        if (this.mob.getRandom().nextBoolean()) {
            if (!this.mob.level().isClientSide) {
                ((ServerLevel) this.mob.level()).sendParticles(ModParticleTypes.SKELETON_CONTROL_PARTICLE.get(), target.getX(), target.getY(), target.getZ(), 32, 1, 0.4, 1, 1);
                this.mob.level().playSound(null, target.blockPosition(), ModSounds.GASTER_BLASTER_PRIME.get(), SoundSource.PLAYERS, 1F, 1F);
            }
            applyControlEffects(target);
            this.controlTimer = MAX_CONTROL_TIME;
        } else {
            this.mob.level().addFreshEntity(new BoneZone(this.mob.level(), this.mob, BlockPos.containing(target.getPosition(1F)), 0));
        }
    }

    @Override
    protected void escape(double distanceToTarget) {
        //if (this.distanceToTarget < 40 && (this.teleportCooldown <= 0 || this.mob.invulnerableTime != 0)) {
        if (this.distanceToTarget < 40 && this.teleportCooldown <= 0) {
            this.teleportCooldown = this.TELEPORT_TIME;
            teleportRandomDirection();
            teleportEffects();
        }
    }

    @Override
    protected void chase(double distanceToTarget, LivingEntity target) {
        if (this.distanceToTarget > 144 && this.teleportCooldown <= 0 && !target.hasEffect(MobEffects.SLOW_FALLING)) {
            this.teleportCooldown = this.TELEPORT_TIME;
            teleportTowardsTarget(target, 8);
            teleportEffects();
        }
    }

    private void teleportRandomDirection() {
        Vec3 lookVec = this.mob.getLookAngle();
        int distance = 10;

        Vec3 leftVec = new Vec3(-lookVec.z, 0, lookVec.x).normalize().scale(distance);
        Vec3 rightVec = new Vec3(lookVec.z, 0, -lookVec.x).normalize().scale(distance);
        Vec3 behindVec = lookVec.scale(-distance);
        Vec3 forwardVec = lookVec.scale(distance);

        Vec3 leftTarget = this.mob.position().add(leftVec);
        Vec3 rightTarget = this.mob.position().add(rightVec);
        Vec3 behindTarget = this.mob.position().add(behindVec);
        Vec3 forwardTarget = this.mob.position().add(forwardVec);

        double leftDistance = safeDistance(this.mob.level(), leftTarget);
        double rightDistance = safeDistance(this.mob.level(), rightTarget);
        double behindDistance = safeDistance(this.mob.level(), behindTarget);
        double forwardDistance = safeDistance(this.mob.level(), forwardTarget);

        Vec3 safePos = this.mob.position();
        double maxDistance = 0;

        if (leftDistance > maxDistance) {
            maxDistance = leftDistance;
            safePos = leftTarget;
        }
        if (rightDistance > maxDistance) {
            maxDistance = rightDistance;
            safePos = rightTarget;
        }
        if (behindDistance > maxDistance) {
            maxDistance = behindDistance;
            safePos = behindTarget;
        }

        if (maxDistance == 0 && forwardDistance > 0) {
            safePos = forwardTarget;
        }

        if (!safePos.equals(this.mob.position())) {
            this.mob.teleportTo(safePos.x, safePos.y, safePos.z);
        }
    }

    /**
     * Teleports the entity a specified distance closer to the target.
     */
    private void teleportTowardsTarget(LivingEntity target, double distance) {
        Vec3 mobPosition = this.mob.position();
        Vec3 targetPosition = target.position();
        Vec3 direction = targetPosition.subtract(mobPosition).normalize();

        Vec3 teleportPosition = mobPosition.add(direction.scale(distance));

        if (safeDistance(this.mob.level(), teleportPosition) > 0) {
            this.mob.teleportTo(teleportPosition.x, teleportPosition.y, teleportPosition.z);
        }
    }

    /**
     * Checks if the target position is safe for teleportation by performing a raytrace.
     * Returns the distance to the target if safe, otherwise returns 0.
     */
    private double safeDistance(Level level, Vec3 target) {
        BlockHitResult blockHitResult = level.clip(new ClipContext(
                this.mob.position(), target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob));

        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return this.mob.position().distanceTo(target);
        }
        return 0;
    }

    private void shootBone(LivingEntity target, int amount) {
        BlockPos blockPos = rayTrace(this.mob, 128.0D);
        EntityHitResult entityHitResult = ServerPlayHandler.hitEntity(this.mob.level(), this.mob, blockPos);

        Vec3 lookVec = this.mob.getLookAngle();
        Vec3 rightVec = new Vec3(-lookVec.z, 0, lookVec.x).normalize();
        Vec3 forwardVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

        double sideOffset;
        if (amount % 2 == 0) {
            sideOffset = 2.5;
        } else {
            sideOffset = -2.5;
        }

        double offsetX = rightVec.x * (sideOffset / 2) + forwardVec.x * -2; //Move the bone 1.25 blocks to the side and 1 block forward
        double offsetY = this.mob.getEyeHeight(); //Move the blaster slightly above the player's head
        double offsetZ = rightVec.z * (sideOffset / 2) + forwardVec.z * -2; //Move the bone 1.25 blocks to the side and 1 block forward

        Vec3 skeletonPos = this.mob.getPosition(1F).add(offsetX, offsetY, offsetZ);

        actionSlowdown(this.mob);
        Bone bone;
        if (entityHitResult != null) {
            bone = new Bone(this.mob.level(), this.mob, skeletonPos.add(0, 1, 0), target.getEyePosition().add(0, 1, 0));
        }
        else {
            bone = new Bone(this.mob.level(), this.mob, skeletonPos, blockPos.getCenter());
        }
        this.mob.level().addFreshEntity(bone);
    }

    private void gasterBlaster() {
        if (this.mob.getTarget() != null) {
            LivingEntity skeleton = this.mob;
            Level pLevel = skeleton.level();
            BlockPos blockPos = rayTrace(skeleton, 48.0D);;
            EntityHitResult entityHitResult = ServerPlayHandler.hitEntity(pLevel, skeleton, blockPos);
            Vec3 skeletonPos;

            Vec3 lookVec = skeleton.getLookAngle();
            Vec3 rightVec = new Vec3(-lookVec.z, 0, lookVec.x).normalize();
            Vec3 forwardVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();


            blockPos = rayTrace(skeleton, 48.0D);
            boolean right = this.mob.getRandom().nextBoolean();
            double sideOffset;
            if (right) {
                sideOffset = 2.5;
            } else {
                sideOffset = -2.5;
            }
            double offsetX = rightVec.x * sideOffset + forwardVec.x * 2; //Move the blaster 2.5 blocks to the side and 2 blocks forward
            double offsetY = skeleton.getEyeHeight() + 0.4; //Move the blaster slightly above the player's head
            double offsetZ = rightVec.z * sideOffset + forwardVec.z * 2; //Move the blaster 2.5 blocks to the side and 2 blocks forward

            skeletonPos = skeleton.getPosition(1F).add(offsetX, offsetY, offsetZ);

            actionSlowdown(skeleton);
            GasterBlaster gasterBlaster;
            if (!(this.mob.getTarget() instanceof Player)) {
                gasterBlaster = new GasterBlaster(pLevel, skeleton, skeletonPos, this.mob.getTarget());
            }
            else if (entityHitResult != null && !(entityHitResult.getEntity() instanceof SummonEntity)) {
                gasterBlaster = new GasterBlaster(pLevel, skeleton, skeletonPos, entityHitResult.getEntity().getPosition(1F));
            }else {
                gasterBlaster = new GasterBlaster(pLevel, skeleton, skeletonPos, blockPos.getCenter());
            }
            pLevel.addFreshEntity(gasterBlaster);
        }
    }

    private void teleportEffects() {
        ((ServerLevel) this.mob.level()).sendParticles(ModParticleTypes.SKELETON_CONTROL_PARTICLE.get(), this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ(), 32, 0.3, 0.4, 0.3, 1);
        this.mob.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false));
        this.mob.level().playSound(null, this.mob.getOnPos(), ModSounds.BLINK.get(), SoundSource.PLAYERS, 1F, 1F);
    }

    private void applyControlEffects(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 10, 24, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 6, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 6, false, false));
        target.addEffect(new MobEffectInstance(ModEffects.STUNNED.get(), 40, 4, false, false));
    }

    private void removeEffect(LivingEntity toRemove) {
        toRemove.removeEffect(MobEffects.LEVITATION);
        toRemove.removeEffect(MobEffects.SLOW_FALLING);
        toRemove.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        toRemove.removeEffect(MobEffects.DIG_SLOWDOWN);
        toRemove.removeEffect(ModEffects.STUNNED.get());
    }
}
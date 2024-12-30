package ttv.migami.mdf.entity.fruit.skeleton;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import ttv.migami.mdf.entity.CustomProjectileEntity;
import ttv.migami.mdf.init.ModEntities;
import ttv.migami.mdf.init.ModSounds;

public class Bone extends CustomProjectileEntity {

    public Bone(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Bone(Level pLevel, LivingEntity pShooter) {
        super(ModEntities.SMALL_BONE.get(), pLevel, pShooter);
        this.affectedByGravity = false;
        this.damage = 2F;
        this.speed = 3.5D;
    }

    public Bone(Level pLevel, LivingEntity owner, Vec3 pPos, Vec3 targetPos) {
        super(ModEntities.SMALL_BONE.get(), pLevel, owner);
        this.setPos(pPos.add(0, 1, 0));

        this.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
        this.getLookAngle();
        this.affectedByGravity = false;
        this.damage = 2F;
        this.speed = 3.5D;
        this.setDeltaMovement(this.getLookAngle().x * speed, this.getLookAngle().y * speed, this.getLookAngle().z * speed);
        this.updateHeading();
        this.level().playSound(this, this.blockPosition(), ModSounds.BONE_THROW.get(), SoundSource.PLAYERS, 1F, 1F);
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z)
    {
        if (!this.level().isClientSide) {
            BlockPos entityPos = this.blockPosition();
            BlockPos belowPos = entityPos.below();
            ServerLevel serverLevel = (ServerLevel) this.level();
            BlockState blockStateBelow = serverLevel.getBlockState(belowPos);

            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, this.level().getBlockState(belowPos)), this.getX(), belowPos.getY() + 1, this.getZ(), 32, 0.2, 0.0, 0.2, 0.0);
            serverLevel.playSound(null, belowPos, blockStateBelow.getSoundType().getBreakSound(), SoundSource.BLOCKS, 3.0F, 1.0F);

            if (this.life > 40) {
                this.remove(RemovalReason.KILLED);
            }
        }
    }
}

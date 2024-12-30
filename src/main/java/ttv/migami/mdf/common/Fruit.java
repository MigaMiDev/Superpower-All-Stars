package ttv.migami.mdf.common;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public class Fruit implements INBTSerializable<CompoundTag>
{
    protected ZMove zMove = new ZMove();
    protected XMove xMove = new XMove();
    protected CMove cMove = new CMove();
    protected VMove vMove = new VMove();
    protected FMove fMove = new FMove();

    public ZMove getZMove() { return this.zMove; }
    public XMove getXMove() { return this.xMove; }
    public CMove getCMove() { return this.cMove; }
    public VMove getVMove() { return this.vMove; }
    public FMove getFMove() { return this.fMove; }

    public static class Move implements INBTSerializable<CompoundTag> {
        protected float damage;
        protected boolean canBeHeld;
        protected int cooldown;
        protected int attackInterval;
        protected int attackAmount;

        public Move(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            this.damage = damage;
            this.canBeHeld = canBeHeld;
            this.cooldown = cooldown;
            this.attackInterval = attackInterval;
            this.attackAmount = attackAmount;
        }

        public Move() {
            // Default constructor
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("Damage", this.damage);
            tag.putBoolean("CanBeHeld", this.canBeHeld);
            tag.putInt("Cooldown", this.cooldown);
            tag.putInt("AttackInterval", this.attackInterval);
            tag.putInt("AttackAmount", this.attackAmount);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.damage = tag.getFloat("Damage");
            this.canBeHeld = tag.getBoolean("CanBeHeld");
            this.cooldown = tag.getInt("Cooldown");
            this.attackInterval = tag.getInt("AttackInterval");
            this.attackAmount = tag.getInt("AttackAmount");
        }

        public JsonObject toJsonObject() {
            Preconditions.checkArgument(this.damage > 0, "Damage must be more than zero");
            JsonObject object = new JsonObject();
            object.addProperty("damage", this.damage);
            if (this.canBeHeld) object.addProperty("canBeHeld", true);
            if (this.cooldown != 0) object.addProperty("cooldown", this.cooldown);
            if (this.attackInterval != 0) object.addProperty("attackInterval", this.attackInterval);
            if (this.attackAmount != 0) object.addProperty("attackAmount", this.attackAmount);
            return object;
        }

        public Move copy() {
            Move move = new Move();
            move.damage = this.damage;
            move.canBeHeld = this.canBeHeld;
            move.cooldown = this.cooldown;
            move.attackInterval = this.attackInterval;
            move.attackAmount = this.attackAmount;
            return move;
        }

        public float getDamage() {
            return this.damage;
        }

        public boolean isCanBeHeld() {
            return this.canBeHeld;
        }

        public int getCooldown() {
            return this.cooldown;
        }

        public int getAttackInterval() {
            return this.attackInterval;
        }

        public int getAttackAmount() {
            return this.attackAmount;
        }
    }

    public static class ZMove extends Move {
        public ZMove(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            super(damage, canBeHeld, cooldown, attackInterval, attackAmount);
        }

        public ZMove() {
        }

        public ZMove copy() {
            ZMove move = new ZMove();
            move.damage = this.damage;
            move.canBeHeld = this.canBeHeld;
            move.cooldown = this.cooldown;
            move.attackInterval = this.attackInterval;
            move.attackAmount = this.attackAmount;
            return move;
        }

        public void setValues(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            this.damage = damage;
            this.canBeHeld = canBeHeld;
            this.cooldown = cooldown;
            this.attackInterval = attackInterval;
            this.attackAmount = attackAmount;
        }
    }

    public static class XMove extends Move {
        public XMove(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            super(damage, canBeHeld, cooldown, attackInterval, attackAmount);
        }

        public XMove() {
        }

        public XMove copy() {
            XMove move = new XMove();
            move.damage = this.damage;
            move.canBeHeld = this.canBeHeld;
            move.cooldown = this.cooldown;
            move.attackInterval = this.attackInterval;
            move.attackAmount = this.attackAmount;
            return move;
        }

        public void setValues(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            this.damage = damage;
            this.canBeHeld = canBeHeld;
            this.cooldown = cooldown;
            this.attackInterval = attackInterval;
            this.attackAmount = attackAmount;
        }
    }

    public static class CMove extends Move {
        public CMove(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            super(damage, canBeHeld, cooldown, attackInterval, attackAmount);
        }

        public CMove() {
        }

        public CMove copy() {
            CMove move = new CMove();
            move.damage = this.damage;
            move.canBeHeld = this.canBeHeld;
            move.cooldown = this.cooldown;
            move.attackInterval = this.attackInterval;
            move.attackAmount = this.attackAmount;
            return move;
        }

        public void setValues(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            this.damage = damage;
            this.canBeHeld = canBeHeld;
            this.cooldown = cooldown;
            this.attackInterval = attackInterval;
            this.attackAmount = attackAmount;
        }
    }

    public static class VMove extends Move {
        public VMove(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            super(damage, canBeHeld, cooldown, attackInterval, attackAmount);
        }

        public VMove() {
        }

        public VMove copy() {
            VMove move = new VMove();
            move.damage = this.damage;
            move.canBeHeld = this.canBeHeld;
            move.cooldown = this.cooldown;
            move.attackInterval = this.attackInterval;
            move.attackAmount = this.attackAmount;
            return move;
        }

        public void setValues(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            this.damage = damage;
            this.canBeHeld = canBeHeld;
            this.cooldown = cooldown;
            this.attackInterval = attackInterval;
            this.attackAmount = attackAmount;
        }
    }

    public static class FMove extends Move {
        public FMove(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            super(damage, canBeHeld, cooldown, attackInterval, attackAmount);
        }

        public FMove() {
        }

        public FMove copy() {
            FMove move = new FMove();
            move.damage = this.damage;
            move.canBeHeld = this.canBeHeld;
            move.cooldown = this.cooldown;
            move.attackInterval = this.attackInterval;
            move.attackAmount = this.attackAmount;
            return move;
        }

        public void setValues(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            this.damage = damage;
            this.canBeHeld = canBeHeld;
            this.cooldown = cooldown;
            this.attackInterval = attackInterval;
            this.attackAmount = attackAmount;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("ZMove", this.zMove.serializeNBT());
        tag.put("XMove", this.xMove.serializeNBT());
        tag.put("CMove", this.cMove.serializeNBT());
        tag.put("VMove", this.vMove.serializeNBT());
        tag.put("FMove", this.fMove.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("ZMove", Tag.TAG_COMPOUND)) {
            this.zMove.deserializeNBT(tag.getCompound("ZMove"));
        }
        if (tag.contains("XMove", Tag.TAG_COMPOUND)) {
            this.xMove.deserializeNBT(tag.getCompound("XMove"));
        }
        if (tag.contains("CMove", Tag.TAG_COMPOUND)) {
            this.cMove.deserializeNBT(tag.getCompound("CMove"));
        }
        if (tag.contains("VMove", Tag.TAG_COMPOUND)) {
            this.vMove.deserializeNBT(tag.getCompound("VMove"));
        }
        if (tag.contains("FMove", Tag.TAG_COMPOUND)) {
            this.fMove.deserializeNBT(tag.getCompound("FMove"));
        }
    }

    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        object.add("ZMove", this.zMove.toJsonObject());
        object.add("XMove", this.xMove.toJsonObject());
        object.add("CMove", this.cMove.toJsonObject());
        object.add("VMove", this.vMove.toJsonObject());
        object.add("FMove", this.fMove.toJsonObject());
        return object;
    }

    public static Fruit create(CompoundTag tag) {
        Fruit fruit = new Fruit();
        fruit.deserializeNBT(tag);
        return fruit;
    }

    public Fruit copy() {
        Fruit fruit = new Fruit();
        fruit.zMove = this.zMove.copy();
        fruit.xMove = this.xMove.copy();
        fruit.cMove = this.cMove.copy();
        fruit.vMove = this.vMove.copy();
        fruit.fMove = this.fMove.copy();
        return fruit;
    }

    public static class Builder {
        private final Fruit fruit;

        private Builder() {
            this.fruit = new Fruit();
        }

        public static Builder create() {
            return new Builder();
        }

        public Fruit build() {
            return this.fruit.copy(); // Copy since the builder could be used again
        }

        public Builder setZMoveValues(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            this.fruit.zMove.setValues(damage, canBeHeld, cooldown, attackInterval, attackAmount);
            return this;
        }

        public Builder setXMoveValues(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            this.fruit.xMove.setValues(damage, canBeHeld, cooldown, attackInterval, attackAmount);
            return this;
        }

        public Builder setCMoveValues(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            this.fruit.cMove.setValues(damage, canBeHeld, cooldown, attackInterval, attackAmount);
            return this;
        }

        public Builder setVMoveValues(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            this.fruit.vMove.setValues(damage, canBeHeld, cooldown, attackInterval, attackAmount);
            return this;
        }

        public Builder setFMoveValues(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount) {
            this.fruit.fMove.setValues(damage, canBeHeld, cooldown, attackInterval, attackAmount);
            return this;
        }
    }
}

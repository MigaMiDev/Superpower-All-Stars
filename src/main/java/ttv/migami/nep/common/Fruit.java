package ttv.migami.nep.common;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import ttv.migami.jeg.annotation.Optional;
import ttv.migami.nep.annotation.Ignored;

public class Fruit implements INBTSerializable<CompoundTag>
{
    protected ZAction zAction = new ZAction();
    protected XAction xAction = new XAction();
    protected CAction cAction = new CAction();
    protected VAction vAction = new VAction();
    protected RAction rAction = new RAction();

    public ZAction getZAction() { return this.zAction; }
    public XAction getXAction() { return this.xAction; }
    public CAction getCAction() { return this.cAction; }
    public VAction getVAction() { return this.vAction; }
    public RAction getRAction() { return this.rAction; }

    public static class Action implements INBTSerializable<CompoundTag> {
        protected Component name = Component.literal("");
        protected float damage;
        @Ignored
        protected ActionMode actionMode = ActionMode.SINGLE;
        protected int cooldown;
        protected int rate;
        protected int attackAmount;
        @Optional
        protected float shooterPushback = 0.0F;
        //@Optional
        protected float recoilAngle = 0.0F;
        @Optional
        protected float recoilKick = 0.0F;

        public Action(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            this.name = name;
            this.damage = damage;
            this.actionMode = actionMode;
            this.cooldown = cooldown;
            this.rate = rate;
            this.attackAmount = attackAmount;
            this.shooterPushback = shooterPushback;
            this.recoilAngle = recoilAngle;
            this.recoilKick = recoilKick;
        }

        public Action() {
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Name", this.name.getString());
            tag.putFloat("Damage", this.damage);
            tag.putString("ActionMode", this.actionMode.getId().toString());
            tag.putInt("Cooldown", this.cooldown);
            tag.putInt("Rate", this.rate);
            tag.putInt("AttackAmount", this.attackAmount);
            tag.putFloat("ShooterPushback", this.shooterPushback);
            tag.putFloat("RecoilAngle", this.recoilAngle);
            tag.putFloat("RecoilKick", this.recoilKick);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            if(tag.contains("Name", Tag.TAG_STRING))
            {
                this.name = Component.translatable(tag.getString("Name"));
            }
            if(tag.contains("Damage", Tag.TAG_ANY_NUMERIC))
            {
                this.damage = tag.getFloat("Damage");
            }
            if(tag.contains("FireMode", Tag.TAG_STRING))
            {
                this.actionMode = ActionMode.getType(ResourceLocation.tryParse(tag.getString("ActionMode")));
            }
            if(tag.contains("Cooldown", Tag.TAG_ANY_NUMERIC))
            {
                this.cooldown = tag.getInt("Cooldown");
            }
            if(tag.contains("Rate", Tag.TAG_ANY_NUMERIC))
            {
                this.rate = tag.getInt("Rate");
            }
            if(tag.contains("AttackAmount", Tag.TAG_ANY_NUMERIC))
            {
                this.attackAmount = tag.getInt("AttackAmount");
            }
            if(tag.contains("ShooterPushback", Tag.TAG_ANY_NUMERIC))
            {
                this.shooterPushback = tag.getFloat("ShooterPushback");
            }
            if(tag.contains("RecoilAngle", Tag.TAG_ANY_NUMERIC))
            {
                this.recoilAngle = tag.getFloat("RecoilAngle");
            }
            if(tag.contains("RecoilKick", Tag.TAG_ANY_NUMERIC))
            {
                this.recoilKick = tag.getFloat("RecoilKick");
            }
        }

        public JsonObject toJsonObject() {
            Preconditions.checkArgument(this.damage > 0, "Damage must be more than zero");
            JsonObject object = new JsonObject();
            object.addProperty("name", this.name.getString());
            object.addProperty("damage", this.damage);
            object.addProperty("actionMode", this.actionMode.getId().toString());
            if (this.cooldown != 0) object.addProperty("cooldown", this.cooldown);
            if (this.rate != 0) object.addProperty("rate", this.rate);
            if (this.attackAmount != 0) object.addProperty("attackAmount", this.attackAmount);
            if(this.shooterPushback != 0.0F) object.addProperty("shooterPushback", this.shooterPushback);
            if(this.recoilAngle != 0.0F) object.addProperty("recoilAngle", this.recoilAngle);
            if(this.recoilKick != 0.0F) object.addProperty("recoilKick", this.recoilKick);
            return object;
        }

        public Action copy() {
            Action action = new Action();
            action.name = this.name;
            action.damage = this.damage;
            action.actionMode = this.actionMode;
            action.cooldown = this.cooldown;
            action.rate = this.rate;
            action.attackAmount = this.attackAmount;
            action.shooterPushback = this.shooterPushback;
            action.recoilAngle = this.recoilAngle;
            action.recoilKick = this.recoilKick;
            return action;
        }

        public Component getName() {
            return this.name;
        }

        public float getDamage() {
            return this.damage;
        }

        public ActionMode getActionMode() {
            return this.actionMode;
        }

        public int getCooldown() {
            return this.cooldown;
        }

        public int getRate() {
            return this.rate;
        }

        public int getAttackAmount() {
            return this.attackAmount;
        }

        /**
         * @return The amount of pushback applied to the shooter!
         */
        public float getShooterPushback()
        {
            return this.shooterPushback;
        }

        /**
         * @return The amount of recoil this gun produces upon firing in degrees
         */
        public float getRecoilAngle()
        {
            return this.recoilAngle;
        }

        /**
         * @return The amount of kick this gun produces upon firing
         */
        public float getRecoilKick()
        {
            return this.recoilKick;
        }
    }

    public static class ZAction extends Action {
        public ZAction(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            super(name, damage, actionMode, cooldown, rate, attackAmount, shooterPushback, recoilAngle, recoilKick);
        }

        public ZAction() {
        }

        public ZAction copy() {
            ZAction action = new ZAction();
            action.name = this.name;
            action.damage = this.damage;
            action.actionMode = this.actionMode;
            action.cooldown = this.cooldown;
            action.rate = this.rate;
            action.attackAmount = this.attackAmount;
            action.shooterPushback = this.shooterPushback;
            action.recoilAngle = this.recoilAngle;
            action.recoilKick = this.recoilKick;
            return action;
        }

        public void setValues(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            this.name = name;
            this.damage = damage;
            this.actionMode = actionMode;
            this.cooldown = cooldown;
            this.rate = rate;
            this.attackAmount = attackAmount;
            this.shooterPushback = shooterPushback;
            this.recoilAngle = recoilAngle;
            this.recoilKick = recoilKick;
        }
    }

    public static class XAction extends Action {
        public XAction(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            super(name, damage, actionMode, cooldown, rate, attackAmount, shooterPushback, recoilAngle, recoilKick);
        }

        public XAction() {
        }

        public XAction copy() {
            XAction action = new XAction();
            action.name = this.name;
            action.damage = this.damage;
            action.actionMode = this.actionMode;
            action.cooldown = this.cooldown;
            action.rate = this.rate;
            action.attackAmount = this.attackAmount;
            action.shooterPushback = this.shooterPushback;
            action.recoilAngle = this.recoilAngle;
            action.recoilKick = this.recoilKick;
            return action;
        }

        public void setValues(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            this.name = name;
            this.damage = damage;
            this.actionMode = actionMode;
            this.cooldown = cooldown;
            this.rate = rate;
            this.attackAmount = attackAmount;
            this.shooterPushback = shooterPushback;
            this.recoilAngle = recoilAngle;
            this.recoilKick = recoilKick;
        }
    }

    public static class CAction extends Action {
        public CAction(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            super(name, damage, actionMode, cooldown, rate, attackAmount, shooterPushback, recoilAngle, recoilKick);
        }

        public CAction() {
        }

        public CAction copy() {
            CAction action = new CAction();
            action.name = this.name;
            action.damage = this.damage;
            action.actionMode = this.actionMode;
            action.cooldown = this.cooldown;
            action.rate = this.rate;
            action.attackAmount = this.attackAmount;
            action.shooterPushback = this.shooterPushback;
            action.recoilAngle = this.recoilAngle;
            action.recoilKick = this.recoilKick;
            return action;
        }

        public void setValues(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            this.name = name;
            this.damage = damage;
            this.actionMode = actionMode;
            this.cooldown = cooldown;
            this.rate = rate;
            this.attackAmount = attackAmount;
            this.shooterPushback = shooterPushback;
            this.recoilAngle = recoilAngle;
            this.recoilKick = recoilKick;
        }
    }

    public static class VAction extends Action {
        public VAction(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            super(name, damage, actionMode, cooldown, rate, attackAmount, shooterPushback, recoilAngle, recoilKick);
        }

        public VAction() {
        }

        public VAction copy() {
            VAction action = new VAction();
            action.name = this.name;
            action.damage = this.damage;
            action.actionMode = this.actionMode;
            action.cooldown = this.cooldown;
            action.rate = this.rate;
            action.attackAmount = this.attackAmount;
            action.shooterPushback = this.shooterPushback;
            action.recoilAngle = this.recoilAngle;
            action.recoilKick = this.recoilKick;
            return action;
        }

        public void setValues(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            this.name = name;
            this.damage = damage;
            this.actionMode = actionMode;
            this.cooldown = cooldown;
            this.rate = rate;
            this.attackAmount = attackAmount;
            this.shooterPushback = shooterPushback;
            this.recoilAngle = recoilAngle;
            this.recoilKick = recoilKick;
        }
    }

    public static class RAction extends Action {
        public RAction(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            super(name, damage, actionMode, cooldown, rate, attackAmount, shooterPushback, recoilAngle, recoilKick);
        }

        public RAction() {
        }

        public RAction copy() {
            RAction action = new RAction();
            action.name = this.name;
            action.damage = this.damage;
            action.actionMode = this.actionMode;
            action.cooldown = this.cooldown;
            action.rate = this.rate;
            action.attackAmount = this.attackAmount;
            action.shooterPushback = this.shooterPushback;
            action.recoilAngle = this.recoilAngle;
            action.recoilKick = this.recoilKick;
            return action;
        }

        public void setValues(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            this.name = name;
            this.damage = damage;
            this.actionMode = actionMode;
            this.cooldown = cooldown;
            this.rate = rate;
            this.attackAmount = attackAmount;
            this.shooterPushback = shooterPushback;
            this.recoilAngle = recoilAngle;
            this.recoilKick = recoilKick;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("ZAction", this.zAction.serializeNBT());
        tag.put("XAction", this.xAction.serializeNBT());
        tag.put("CAction", this.cAction.serializeNBT());
        tag.put("VAction", this.vAction.serializeNBT());
        tag.put("RAction", this.rAction.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("ZAction", Tag.TAG_COMPOUND)) {
            this.zAction.deserializeNBT(tag.getCompound("ZAction"));
        }
        if (tag.contains("XAction", Tag.TAG_COMPOUND)) {
            this.xAction.deserializeNBT(tag.getCompound("XAction"));
        }
        if (tag.contains("CAction", Tag.TAG_COMPOUND)) {
            this.cAction.deserializeNBT(tag.getCompound("CAction"));
        }
        if (tag.contains("VAction", Tag.TAG_COMPOUND)) {
            this.vAction.deserializeNBT(tag.getCompound("VAction"));
        }
        if (tag.contains("RAction", Tag.TAG_COMPOUND)) {
            this.rAction.deserializeNBT(tag.getCompound("RAction"));
        }
    }

    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        object.add("zAction", this.zAction.toJsonObject());
        object.add("xAction", this.xAction.toJsonObject());
        object.add("cAction", this.cAction.toJsonObject());
        object.add("vAction", this.vAction.toJsonObject());
        object.add("rAction", this.rAction.toJsonObject());
        return object;
    }

    public static Fruit create(CompoundTag tag) {
        Fruit fruit = new Fruit();
        fruit.deserializeNBT(tag);
        return fruit;
    }

    public Fruit copy() {
        Fruit fruit = new Fruit();
        fruit.zAction = this.zAction.copy();
        fruit.xAction = this.xAction.copy();
        fruit.cAction = this.cAction.copy();
        fruit.vAction = this.vAction.copy();
        fruit.rAction = this.rAction.copy();
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

        public Builder setZAction(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            this.fruit.zAction.setValues(name, damage, actionMode, cooldown, rate, attackAmount, shooterPushback, recoilAngle, recoilKick);
            return this;
        }

        public Builder setXAction(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            this.fruit.xAction.setValues(name, damage, actionMode, cooldown, rate, attackAmount, shooterPushback, recoilAngle, recoilKick);
            return this;
        }

        public Builder setCAction(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            this.fruit.cAction.setValues(name, damage, actionMode, cooldown, rate, attackAmount, shooterPushback, recoilAngle, recoilKick);
            return this;
        }

        public Builder setVAction(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            this.fruit.vAction.setValues(name, damage, actionMode, cooldown, rate, attackAmount, shooterPushback, recoilAngle, recoilKick);
            return this;
        }

        public Builder setRAction(Component name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, float shooterPushback, float recoilAngle, float recoilKick) {
            this.fruit.rAction.setValues(name, damage, actionMode, cooldown, rate, attackAmount, shooterPushback, recoilAngle, recoilKick);
            return this;
        }
    }
}

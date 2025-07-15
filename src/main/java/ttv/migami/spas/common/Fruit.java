package ttv.migami.spas.common;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import ttv.migami.spas.annotation.Ignored;
import ttv.migami.spas.annotation.Optional;

public class Fruit implements INBTSerializable<CompoundTag>
{
    protected General general = new General();
    protected MoveA moveA = new MoveA();
    protected MoveB moveB = new MoveB();
    protected Special special = new Special();
    protected Ultimate ultimate = new Ultimate();
    protected Mobility mobility = new Mobility();

    public General getGeneral() { return this.general; }
    public MoveA getMoveA() { return this.moveA; }
    public MoveB getMoveB() { return this.moveB; }
    public Special getSpecial() { return this.special; }
    public Ultimate getUltimate() { return this.ultimate; }
    public Mobility getMobility() { return this.mobility; }

    public static class General implements INBTSerializable<CompoundTag>
    {
        private boolean disableSwimming = false;

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("DisableSwimming", this.disableSwimming);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            if(tag.contains("DisableSwimming", Tag.TAG_ANY_NUMERIC))
            {
                this.disableSwimming = tag.getBoolean("DisableSwimming");
            }
        }

        public JsonObject toJsonObject()
        {
            JsonObject object = new JsonObject();
            object.addProperty("disableSwimming", this.disableSwimming);
            return object;
        }

        /**
         * @return A copy of the general get
         */
        public General copy()
        {
            General general = new General();
            general.disableSwimming = this.disableSwimming;
            return general;
        }

        /**
         * @return If true, the user will not be able to swim with this Fruit
         */
        public boolean isSwimDisabled()
        {
            return this.disableSwimming;
        }
    }
    
    public static class Action implements INBTSerializable<CompoundTag> {
        protected ActionType actionType = ActionType.MOVE_A;
        protected String name = "";
        protected float damage;
        @Ignored
        protected ActionMode actionMode = ActionMode.PRESS;
        protected int cooldown;
        protected int rate;
        protected int attackAmount;
        protected boolean enabledByDefault = false;
        protected int masteryLevel = 5;
        protected int xpRequirement = 3;
        protected FoodExhaustion foodExhaustion = FoodExhaustion.SMALL;
        @Optional
        protected float shooterPushback = 0.0F;
        @Optional
        protected float recoilKick = 0.0F;

        public Action(ActionType actionType, String name,
                      float damage, ActionMode actionMode, int cooldown,
                      int rate, int attackAmount, boolean enabledByDefault,
                      int masteryLevel, int xpRequirement, FoodExhaustion foodExhaustion,
                      float shooterPushback, float recoilKick) {
            this.actionType = actionType;
            this.name = name;
            this.damage = damage;
            this.actionMode = actionMode;
            this.cooldown = cooldown;
            this.rate = rate;
            this.attackAmount = attackAmount;
            this.enabledByDefault = enabledByDefault;
            this.masteryLevel = masteryLevel;
            this.xpRequirement = xpRequirement;
            this.foodExhaustion = foodExhaustion;
            this.shooterPushback = shooterPushback;
            this.recoilKick = recoilKick;
        }

        public Action() {
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("ActionType", this.actionType.toString());
            tag.putString("Name", this.name);
            tag.putFloat("Damage", this.damage);
            tag.putString("ActionMode", this.actionMode.getId().toString());
            tag.putInt("Cooldown", this.cooldown);
            tag.putInt("Rate", this.rate);
            tag.putInt("AttackAmount", this.attackAmount);
            tag.putBoolean("EnabledByDefault", this.enabledByDefault);
            tag.putInt("MasteryLevel", this.masteryLevel);
            tag.putInt("XpRequirement", this.xpRequirement);
            tag.putString("FoodExhaustion", this.foodExhaustion.toString());
            tag.putFloat("ShooterPushback", this.shooterPushback);
            tag.putFloat("RecoilKick", this.recoilKick);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            if(tag.contains("ActionType", Tag.TAG_STRING))
            {
                this.actionType = ActionType.valueOf(tag.getString("ActionType"));
            }
            if(tag.contains("Name", Tag.TAG_STRING))
            {
                this.name = tag.getString("Name");
            }
            if(tag.contains("Damage", Tag.TAG_ANY_NUMERIC))
            {
                this.damage = tag.getFloat("Damage");
            }
            if(tag.contains("ActionMode", Tag.TAG_STRING))
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
            if(tag.contains("EnabledByDefault", Tag.TAG_ANY_NUMERIC))
            {
                this.enabledByDefault = tag.getBoolean("EnabledByDefault");
            }
            if(tag.contains("MasteryLevel", Tag.TAG_ANY_NUMERIC))
            {
                this.masteryLevel = tag.getInt("MasteryLevel");
            }
            if(tag.contains("XpRequirement", Tag.TAG_ANY_NUMERIC))
            {
                this.xpRequirement = tag.getInt("XpRequirement");
            }
            if(tag.contains("FoodExhaustion", Tag.TAG_STRING))
            {
                this.foodExhaustion = FoodExhaustion.valueOf(tag.getString("FoodExhaustion"));
            }
            if(tag.contains("ShooterPushback", Tag.TAG_ANY_NUMERIC))
            {
                this.shooterPushback = tag.getFloat("ShooterPushback");
            }
            if(tag.contains("RecoilKick", Tag.TAG_ANY_NUMERIC))
            {
                this.recoilKick = tag.getFloat("RecoilKick");
            }
        }

        public JsonObject toJsonObject() {
            Preconditions.checkArgument(this.damage >= 0, "Damage must be equal or greater than zero");
            JsonObject object = new JsonObject();
            object.addProperty("actionType", this.actionType.toString());
            object.addProperty("name", this.name);
            object.addProperty("damage", this.damage);
            object.addProperty("actionMode", this.actionMode.getId().toString());
            if (this.cooldown != 0) object.addProperty("cooldown", this.cooldown);
            if (this.rate != 0) object.addProperty("rate", this.rate);
            if (this.attackAmount != 0) object.addProperty("attackAmount", this.attackAmount);
            object.addProperty("enabledByDefault", this.enabledByDefault);
            object.addProperty("masteryLevel", this.masteryLevel);
            object.addProperty("xpRequirement", this.xpRequirement);
            object.addProperty("foodExhaustion", this.foodExhaustion.toString());
            if(this.shooterPushback != 0.0F) object.addProperty("shooterPushback", this.shooterPushback);
            if(this.recoilKick != 0.0F) object.addProperty("recoilKick", this.recoilKick);
            return object;
        }

        public Action copy() {
            Action action = new Action();
            action.actionType = this.actionType;
            action.name = this.name;
            action.damage = this.damage;
            action.actionMode = this.actionMode;
            action.cooldown = this.cooldown;
            action.rate = this.rate;
            action.attackAmount = this.attackAmount;
            action.enabledByDefault = this.enabledByDefault;
            action.masteryLevel = this.masteryLevel;
            action.xpRequirement = this.xpRequirement;
            action.foodExhaustion = this.foodExhaustion;
            action.shooterPushback = this.shooterPushback;
            action.recoilKick = this.recoilKick;
            return action;
        }

        public ActionType getActionType() {
            return this.actionType;
        }

        public String getName() {
            return this.name.toString();
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
         * @return The amount of kick this gun produces upon firing
         */
        public float getRecoilKick()
        {
            return this.recoilKick;
        }
    }

    public static class MoveA extends Action {
        /*public MoveA(ActionType actionType, String name,
                       float damage, ActionMode actionMode, int cooldown,
                       int rate, int attackAmount, boolean enabledByDefault,
                       int masteryLevel, int xpRequirement, FoodExhaustion foodExhaustion,
                       float shooterPushback, float recoilKick) {
            super(actionType, name, damage, actionMode, cooldown, rate, attackAmount, enabledByDefault, masteryLevel, xpRequirement, foodExhaustion, shooterPushback, recoilKick);
        }*/

        public MoveA() {
        }

        public MoveA copy() {
            MoveA action = new MoveA();
            action.actionType = this.actionType;
            action.name = this.name;
            action.damage = this.damage;
            action.actionMode = this.actionMode;
            action.cooldown = this.cooldown;
            action.rate = this.rate;
            action.attackAmount = this.attackAmount;
            action.enabledByDefault = this.enabledByDefault;
            action.masteryLevel = this.masteryLevel;
            action.xpRequirement = this.xpRequirement;
            action.foodExhaustion = this.foodExhaustion;
            action.shooterPushback = this.shooterPushback;
            action.recoilKick = this.recoilKick;
            return action;
        }

        public void setValues(ActionType actionType, String name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, boolean enabledByDefault, int masteryLevel, int xpRequirement, FoodExhaustion foodExhaustion, float shooterPushback, float recoilKick) {
            this.actionType = actionType;
            this.name = name;
            this.damage = damage;
            this.actionMode = actionMode;
            this.cooldown = cooldown;
            this.rate = rate;
            this.attackAmount = attackAmount;
            this.enabledByDefault = enabledByDefault;
            this.masteryLevel = masteryLevel;
            this.xpRequirement = xpRequirement;
            this.foodExhaustion = foodExhaustion;
            this.shooterPushback = shooterPushback;
            this.recoilKick = recoilKick;
        }
    }

    public static class MoveB extends Action {

        public MoveB() {
        }

        public MoveB copy() {
            MoveB action = new MoveB();
            action.actionType = this.actionType;
            action.name = this.name;
            action.damage = this.damage;
            action.actionMode = this.actionMode;
            action.cooldown = this.cooldown;
            action.rate = this.rate;
            action.attackAmount = this.attackAmount;
            action.enabledByDefault = this.enabledByDefault;
            action.masteryLevel = this.masteryLevel;
            action.xpRequirement = this.xpRequirement;
            action.foodExhaustion = this.foodExhaustion;
            action.shooterPushback = this.shooterPushback;
            action.recoilKick = this.recoilKick;
            return action;
        }

        public void setValues(ActionType actionType, String name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, boolean enabledByDefault, int masteryLevel, int xpRequirement, FoodExhaustion foodExhaustion, float shooterPushback, float recoilKick) {
            this.actionType = actionType;
            this.name = name;
            this.damage = damage;
            this.actionMode = actionMode;
            this.cooldown = cooldown;
            this.rate = rate;
            this.attackAmount = attackAmount;
            this.enabledByDefault = enabledByDefault;
            this.masteryLevel = masteryLevel;
            this.xpRequirement = xpRequirement;
            this.foodExhaustion = foodExhaustion;
            this.shooterPushback = shooterPushback;
            this.recoilKick = recoilKick;
        }
    }

    public static class Special extends Action {

        public Special() {
        }

        public Special copy() {
            Special action = new Special();
            action.actionType = this.actionType;
            action.name = this.name;
            action.damage = this.damage;
            action.actionMode = this.actionMode;
            action.cooldown = this.cooldown;
            action.rate = this.rate;
            action.attackAmount = this.attackAmount;
            action.enabledByDefault = this.enabledByDefault;
            action.masteryLevel = this.masteryLevel;
            action.xpRequirement = this.xpRequirement;
            action.foodExhaustion = this.foodExhaustion;
            action.shooterPushback = this.shooterPushback;
            action.recoilKick = this.recoilKick;
            return action;
        }

        public void setValues(ActionType actionType, String name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, boolean enabledByDefault, int masteryLevel, int xpRequirement, FoodExhaustion foodExhaustion, float shooterPushback, float recoilKick) {
            this.actionType = actionType;
            this.name = name;
            this.damage = damage;
            this.actionMode = actionMode;
            this.cooldown = cooldown;
            this.rate = rate;
            this.attackAmount = attackAmount;
            this.enabledByDefault = enabledByDefault;
            this.masteryLevel = masteryLevel;
            this.xpRequirement = xpRequirement;
            this.foodExhaustion = foodExhaustion;
            this.shooterPushback = shooterPushback;
            this.recoilKick = recoilKick;
        }
    }

    public static class Ultimate extends Action {

        public Ultimate() {
        }

        public Ultimate copy() {
            Ultimate action = new Ultimate();
            action.actionType = this.actionType;
            action.name = this.name;
            action.damage = this.damage;
            action.actionMode = this.actionMode;
            action.cooldown = this.cooldown;
            action.rate = this.rate;
            action.attackAmount = this.attackAmount;
            action.enabledByDefault = this.enabledByDefault;
            action.masteryLevel = this.masteryLevel;
            action.xpRequirement = this.xpRequirement;
            action.foodExhaustion = this.foodExhaustion;
            action.shooterPushback = this.shooterPushback;
            action.recoilKick = this.recoilKick;
            return action;
        }

        public void setValues(ActionType actionType, String name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, boolean enabledByDefault, int masteryLevel, int xpRequirement, FoodExhaustion foodExhaustion, float shooterPushback, float recoilKick) {
            this.actionType = actionType;
            this.name = name;
            this.damage = damage;
            this.actionMode = actionMode;
            this.cooldown = cooldown;
            this.rate = rate;
            this.attackAmount = attackAmount;
            this.enabledByDefault = enabledByDefault;
            this.masteryLevel = masteryLevel;
            this.xpRequirement = xpRequirement;
            this.foodExhaustion = foodExhaustion;
            this.shooterPushback = shooterPushback;
            this.recoilKick = recoilKick;
        }
    }

    public static class Mobility extends Action {

        public Mobility() {
        }

        public Mobility copy() {
            Mobility action = new Mobility();
            action.actionType = this.actionType;
            action.name = this.name;
            action.damage = this.damage;
            action.actionMode = this.actionMode;
            action.cooldown = this.cooldown;
            action.rate = this.rate;
            action.attackAmount = this.attackAmount;
            action.enabledByDefault = this.enabledByDefault;
            action.masteryLevel = this.masteryLevel;
            action.xpRequirement = this.xpRequirement;
            action.foodExhaustion = this.foodExhaustion;
            action.shooterPushback = this.shooterPushback;
            action.recoilKick = this.recoilKick;
            return action;
        }

        public void setValues(ActionType actionType, String name, float damage, ActionMode actionMode, int cooldown, int rate, int attackAmount, boolean enabledByDefault, int masteryLevel, int xpRequirement, FoodExhaustion foodExhaustion, float shooterPushback, float recoilKick) {
            this.actionType = actionType;
            this.name = name;
            this.damage = damage;
            this.actionMode = actionMode;
            this.cooldown = cooldown;
            this.rate = rate;
            this.attackAmount = attackAmount;
            this.enabledByDefault = enabledByDefault;
            this.masteryLevel = masteryLevel;
            this.xpRequirement = xpRequirement;
            this.foodExhaustion = foodExhaustion;
            this.shooterPushback = shooterPushback;
            this.recoilKick = recoilKick;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("General", this.general.serializeNBT());
        tag.put("MoveA", this.moveA.serializeNBT());
        tag.put("MoveB", this.moveB.serializeNBT());
        tag.put("Special", this.special.serializeNBT());
        tag.put("Ultimate", this.ultimate.serializeNBT());
        tag.put("Mobility", this.mobility.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if(tag.contains("General", Tag.TAG_COMPOUND))
        {
            this.general.deserializeNBT(tag.getCompound("General"));
        }
        if (tag.contains("MoveA", Tag.TAG_COMPOUND)) {
            this.moveA.deserializeNBT(tag.getCompound("MoveA"));
        }
        if (tag.contains("MoveB", Tag.TAG_COMPOUND)) {
            this.moveB.deserializeNBT(tag.getCompound("MoveB"));
        }
        if (tag.contains("Special", Tag.TAG_COMPOUND)) {
            this.special.deserializeNBT(tag.getCompound("Special"));
        }
        if (tag.contains("Ultimate", Tag.TAG_COMPOUND)) {
            this.ultimate.deserializeNBT(tag.getCompound("Ultimate"));
        }
        if (tag.contains("Mobility", Tag.TAG_COMPOUND)) {
            this.mobility.deserializeNBT(tag.getCompound("Mobility"));
        }
    }

    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        object.add("general", this.general.toJsonObject());
        object.add("moveA", this.moveA.toJsonObject());
        object.add("moveB", this.moveB.toJsonObject());
        object.add("special", this.special.toJsonObject());
        object.add("ultimate", this.ultimate.toJsonObject());
        object.add("mobility", this.mobility.toJsonObject());
        return object;
    }

    public static Fruit create(CompoundTag tag)
    {
        Fruit fruit = new Fruit();
        fruit.deserializeNBT(tag);
        return fruit;
    }

    public Fruit copy() {
        Fruit fruit = new Fruit();
        fruit.general = this.general.copy();
        fruit.moveA = this.moveA.copy();
        fruit.moveB = this.moveB.copy();
        fruit.special = this.special.copy();
        fruit.ultimate = this.ultimate.copy();
        fruit.mobility = this.mobility.copy();
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

        public Builder setSwimDisabled(boolean swimDisabled)
        {
            this.fruit.general.disableSwimming = swimDisabled;
            return this;
        }

        public Builder setMoveA(ActionType actionType, String name,
                                  float damage, ActionMode actionMode, int cooldown,
                                  int rate, int attackAmount, boolean enabledByDefault,
                                  int masteryLevel, int xpRequirement, FoodExhaustion foodExhaustion,
                                  float shooterPushback, float recoilKick) {
            this.fruit.moveA.setValues(actionType, name, damage, actionMode, cooldown, rate, attackAmount, enabledByDefault, masteryLevel, xpRequirement, foodExhaustion, shooterPushback, recoilKick);
            return this;
        }

        public Builder setMoveB(ActionType actionType, String name,
                                  float damage, ActionMode actionMode, int cooldown,
                                  int rate, int attackAmount, boolean enabledByDefault,
                                  int masteryLevel, int xpRequirement, FoodExhaustion foodExhaustion,
                                  float shooterPushback, float recoilKick) {
            this.fruit.moveB.setValues(actionType, name, damage, actionMode, cooldown, rate, attackAmount, enabledByDefault, masteryLevel, xpRequirement, foodExhaustion, shooterPushback, recoilKick);
            return this;
        }

        public Builder setSpecial(ActionType actionType, String name,
                                  float damage, ActionMode actionMode, int cooldown,
                                  int rate, int attackAmount, boolean enabledByDefault,
                                  int masteryLevel, int xpRequirement, FoodExhaustion foodExhaustion,
                                  float shooterPushback, float recoilKick) {
            this.fruit.special.setValues(actionType, name, damage, actionMode, cooldown, rate, attackAmount, enabledByDefault, masteryLevel, xpRequirement, foodExhaustion, shooterPushback, recoilKick);
            return this;
        }

        public Builder setUltimate(ActionType actionType, String name,
                                  float damage, ActionMode actionMode, int cooldown,
                                  int rate, int attackAmount, boolean enabledByDefault,
                                  int masteryLevel, int xpRequirement, FoodExhaustion foodExhaustion,
                                  float shooterPushback, float recoilKick) {
            this.fruit.ultimate.setValues(actionType, name, damage, actionMode, cooldown, rate, attackAmount, enabledByDefault, masteryLevel, xpRequirement, foodExhaustion, shooterPushback, recoilKick);
            return this;
        }

        public Builder setMobility(ActionType actionType, String name,
                                  float damage, ActionMode actionMode, int cooldown,
                                  int rate, int attackAmount, boolean enabledByDefault,
                                  int masteryLevel, int xpRequirement, FoodExhaustion foodExhaustion,
                                  float shooterPushback, float recoilKick) {
            this.fruit.mobility.setValues(actionType, name, damage, actionMode, cooldown, rate, attackAmount, enabledByDefault, masteryLevel, xpRequirement, foodExhaustion, shooterPushback, recoilKick);
            return this;
        }
    }
}

package com.mrjoshuat.coppergolem.entity;

import com.mrjoshuat.coppergolem.OxidizableBlockCallback;
import com.mrjoshuat.coppergolem.entity.goals.EscapeWaterGoal;
import com.mrjoshuat.coppergolem.entity.goals.PressButtonGoal;
import com.mrjoshuat.coppergolem.entity.goals.RodWiggleGoal;
import com.mrjoshuat.coppergolem.entity.goals.SpinHeadGoal;
import com.mrjoshuat.coppergolem.entity.goals.SearchForButtonsGoal;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CopperGolemEntity extends GolemEntity {
    protected static final TrackedData<Float> LAST_ROD_WIGGLE_TICKS;
    protected static final TrackedData<Float> SHOULD_BEND_OVER;
    protected static final TrackedData<Integer> OXIDIZATION_LEVEL;
    protected static final TrackedData<Integer> LAST_HEAD_SPIN_TICKS;
    protected static final TrackedData<Float> LAST_BUTTON_PRESS_TICKS;
    protected static final TrackedData<Boolean> IS_WAXED;
    protected static final List<Item> ALL_AXES;

    private static final int MIN_LEVEL = 0;
    private static final int MAX_LEVEL = 3;
    private static final float INGOT_HEALTH_INCREASE = 5F;

    private BlockPos blockTarget;
    private float headSpinProgress;
    private int cachedOxidationLevel = 0;

    static {
        SHOULD_BEND_OVER = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.FLOAT);
        OXIDIZATION_LEVEL = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
        LAST_HEAD_SPIN_TICKS = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
        LAST_BUTTON_PRESS_TICKS = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.FLOAT);
        LAST_ROD_WIGGLE_TICKS = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.FLOAT);
        IS_WAXED = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ALL_AXES = Arrays.asList(Items.NETHERITE_AXE, Items.DIAMOND_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.WOODEN_AXE);
    }

    public CopperGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);

        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0F);

        if (!world.isClient)
            this.setupRandomTickListener();
    }

    protected boolean oxidizationLevelValidForGoals() { return this.getOxidisation() != Oxidisation.OXIDIZED; }

    protected void initGoals() {
        if (!oxidizationLevelValidForGoals()) {
            return;
        }

        var priority = 0;
        this.goalSelector.add(++priority, new SwimGoal(this));
        this.goalSelector.add(++priority, new EscapeWaterGoal(this));
        this.goalSelector.add(++priority, new EscapeDangerGoal(this, 0.5D));
        this.goalSelector.add(++priority, new SearchForButtonsGoal(this));
        this.goalSelector.add(++priority, new PressButtonGoal(this));
        this.goalSelector.add(++priority, new IronGolemWanderAroundGoal(this, 0.25D));
        this.goalSelector.add(++priority, new SpinHeadGoal(this));
        this.goalSelector.add(++priority, new RodWiggleGoal(this));
        this.goalSelector.add(++priority, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(++priority, new LookAtEntityGoal(this, IronGolemEntity.class, 10.0F));
        this.goalSelector.add(++priority, new LookAroundGoal(this));
    }

    @Override
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        super.onStruckByLightning(world, lightning);
        if (!this.getWaxed()) {
            this.setOxidisationLevel(MIN_LEVEL);
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(SHOULD_BEND_OVER, 0F);
        this.dataTracker.startTracking(OXIDIZATION_LEVEL, MIN_LEVEL);
        this.dataTracker.startTracking(LAST_BUTTON_PRESS_TICKS, 0F);
        this.dataTracker.startTracking(LAST_ROD_WIGGLE_TICKS, 0F);
        this.dataTracker.startTracking(LAST_HEAD_SPIN_TICKS, 0);
        this.dataTracker.startTracking(IS_WAXED, false);
    }

    public void tickMovement() {
        super.tickMovement();
        if (this.world.isClient)
            return;
        this.tickButtonMovements();
        this.tickHeadSpin();
        this.tickOxidisationAI();
    }

    private void tickButtonMovements() {
        var buttonTicksLeft = this.getButtonTicksLeft();
        if (buttonTicksLeft > 0) {
            this.dataTracker.set(LAST_BUTTON_PRESS_TICKS, --buttonTicksLeft);
        }
        if (buttonTicksLeft == 0) {
            var bendOverTicks = this.getBendOverTicks();
            if (bendOverTicks != 0) {
                if (bendOverTicks > 0) {
                    this.setBendOverTicks(--bendOverTicks);
                } else if (bendOverTicks < 0) {
                    this.setBendOverTicks(++bendOverTicks);
                }
            }
        }
    }

    private void tickHeadSpin() {
        var spinHeadTicks = this.getLastHeadSpinTicks();
        if (spinHeadTicks > 0)  {
            spinHeadTicks -= 2;
            this.dataTracker.set(LAST_HEAD_SPIN_TICKS, spinHeadTicks);
        }
        if (spinHeadTicks <= 0)
            return;
        this.headSpinProgress = (spinHeadTicks * 0.01F) - 0.05F;
    }

    protected void tickOxidisationAI() {
        var oxidisation = getOxidisation();
        var aiDisabled = this.isAiDisabled();
        if (oxidisation == Oxidisation.OXIDIZED && !aiDisabled) {
            this.setAiDisabled(true);
            this.clearGoalsAndTasks();
            this.setLastRodWiggleTicksLeft(0);
        } else if (oxidisation != Oxidisation.OXIDIZED && aiDisabled) {
            this.setAiDisabled(false);
            this.initGoals();
        }
    }

    private void setupRandomTickListener() {
        // TODO: this should be removed, need a better way
        OxidizableBlockCallback.EVENT.register((state, world, pos, random) -> {
            if (isAlive() && !this.getWaxed()) {
                this.tickDegradation();
            }
            return ActionResult.PASS;
        });
    }

    private void tickDegradation() {
        int i = this.getOxidizationLevel();
        if (i == MAX_LEVEL)
            return;

        AtomicInteger j = new AtomicInteger();
        AtomicInteger k = new AtomicInteger();
        BlockPos pos = getBlockPos();
        Box box = new Box(pos).expand(4, 4, 4);
        var entities = world.getEntitiesByClass(CopperGolemEntity.class, box, golem -> golem != this);
        entities.forEach(entity -> {
            Enum<?> enum_ = entity.getOxidisation();
            if (this.getOxidisation().getClass() == enum_.getClass()) {
                int m = enum_.ordinal();
                if (m < i) {
                    return;
                }

                if (m > i) {
                    k.getAndIncrement();
                } else {
                    j.getAndIncrement();
                }
            }
        });

        float f = (float)(k.get() + 1) / (float)(k.get() + j.get() + 1);
        float g = f * f * 0.01F;
        float rf = random.nextFloat();
        if (rf < g) {
            this.incrementOxidisation();
        }
    }

    public void incrementOxidisation() {
        var level = this.getOxidizationLevel();
        if (level >= MAX_LEVEL) {
            return; // no more steps to go
        }
        this.setOxidisationLevel(++level);
    }

    public boolean decrementOxidization() {
        var level = this.getOxidizationLevel();
        if (level <= MIN_LEVEL) {
            return false;
        }
        this.setOxidisationLevel(--level);
        return true;
    }

    private int oxidisationToItemCountDrop() {
        var level = getOxidisation();
        if (level == Oxidisation.UNAFFECTED) {
            return 3;
        } else if (level == Oxidisation.EXPOSED) {
            return 2;
        } else if (level == Oxidisation.WEATHERED) {
            return 1;
        } else if (level == Oxidisation.OXIDIZED) {
            return 0;
        }
        // should never get here
        return 3;
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        var stack = new ItemStack(Items.COPPER_INGOT, oxidisationToItemCountDrop());
        var pos = this.getBlockPos();
        var itemEntity = new ItemEntity(this.world, pos.getX(), pos.getY(), pos.getZ(), stack);
        this.world.spawnEntity(itemEntity);
    }

    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }

        ItemStack stack = player.getStackInHand(hand);
        Item handItem = stack.getItem();

        // debug
        /*if (handItem == Items.DIAMOND_AXE) {
            this.incrementOxidisation();
            this.tickOxidisationAI();
            return ActionResult.success(this.world.isClient);
        }*/

        if (ALL_AXES.contains(handItem)) {
            if (this.getWaxed()) {
                this.setWaxed(false);
                if (!player.isCreative()) {
                    stack.damage(1, getRandom(), (ServerPlayerEntity) null);
                }
                addParticle(WorldEvents.WAX_REMOVED, SoundEvents.ITEM_AXE_WAX_OFF);
                return ActionResult.success(this.world.isClient);
            }

            if (this.decrementOxidization()) {
                this.tickOxidisationAI();
                if (!player.isCreative()) {
                    stack.damage(1, getRandom(), (ServerPlayerEntity) null);
                }
                addParticle(WorldEvents.BLOCK_SCRAPED, SoundEvents.ITEM_AXE_SCRAPE);
                return ActionResult.success(this.world.isClient);
            }
            return ActionResult.PASS;
        }

        if (handItem == Items.HONEYCOMB && !this.getWaxed()) {
            this.setWaxed(true);
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            addParticle(WorldEvents.BLOCK_WAXED, SoundEvents.ITEM_HONEYCOMB_WAX_ON);
            return ActionResult.success(this.world.isClient);
        }

        if (handItem == Items.COPPER_INGOT) {
            var currentHealth = getHealth();
            heal(INGOT_HEALTH_INCREASE);
            if (getHealth() > currentHealth) {
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                produceParticles(ParticleTypes.HEART);
                return ActionResult.success(this.world.isClient);
            }
        }

        return ActionResult.PASS;
    }

    // From MerchantEntity to create same heart effects
    protected void produceParticles(ParticleEffect parameters) {
        for(int i = 0; i < 5; ++i) {
            double d = this.random.nextGaussian() * 0.02D;
            double e = this.random.nextGaussian() * 0.02D;
            double f = this.random.nextGaussian() * 0.02D;
            this.world.addParticle(parameters, this.getParticleX(1.0D), this.getRandomBodyY() + 1.0D, this.getParticleZ(1.0D), d, e, f);
        }
    }

    private void addParticle(int worldEvent, SoundEvent soundEvent) {
        world.playSound(null, getBlockPos(),soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.syncWorldEvent(null, worldEvent, getBlockPos(), 0);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Oxidation", this.getOxidizationLevel());
        nbt.putBoolean("Waxed", this.getWaxed());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setOxidisationLevel(nbt.getInt("Oxidation"));
        this.setWaxed(nbt.getBoolean("Waxed"));
    }

    @Nullable
    public BlockPos getBlockTarget() {
        return this.blockTarget;
    }

    public void setBlockTarget(@Nullable BlockPos blockTarget) {
        this.blockTarget = blockTarget;
    }

    public void clearBlockTarget() { this.blockTarget = null; }

    public Oxidisation getOxidisation() {
        var level = this.getOxidizationLevel();
        if (level <= 0)
            return Oxidisation.UNAFFECTED;
        else if (level == 1) {
            return Oxidisation.EXPOSED;
        } else if (level == 2) {
            return Oxidisation.WEATHERED;
        }
        return Oxidisation.OXIDIZED;
    }

    public int getOxidizationLevel() {
        return cachedOxidationLevel;
    }

    protected void setOxidisationLevel(int f) {
        cachedOxidationLevel = f;
        this.dataTracker.set(OXIDIZATION_LEVEL, f);
    }

    public void updateOxidizationLevel() {
        cachedOxidationLevel = this.dataTracker.get(OXIDIZATION_LEVEL);
    }

    protected boolean getWaxed() { return this.dataTracker.get(IS_WAXED); }

    protected void setWaxed(boolean waxed) { this.dataTracker.set(IS_WAXED, waxed); }

    public void setLastButtonPressTicks(int ticks) { this.dataTracker.set(LAST_HEAD_SPIN_TICKS, ticks); }

    public int getLastHeadSpinTicks() { return this.dataTracker.get(LAST_HEAD_SPIN_TICKS); }

    public float getHeadSpinProgress() { return this.headSpinProgress; }

    public void setButtonTicksLeft(float ticks) { this.dataTracker.set(LAST_BUTTON_PRESS_TICKS, ticks); }

    public float getButtonTicksLeft() { return this.dataTracker.get(LAST_BUTTON_PRESS_TICKS); }

    public void setBendOverTicks(float ticks) { this.dataTracker.set(SHOULD_BEND_OVER, ticks); }

    public float getBendOverTicks() { return this.dataTracker.get(SHOULD_BEND_OVER); }

    public void setLastRodWiggleTicksLeft(float ticks) { this.dataTracker.set(LAST_ROD_WIGGLE_TICKS, ticks); }

    public float getLastRodWiggleTicks() { return this.dataTracker.get(LAST_ROD_WIGGLE_TICKS); }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (this.world.isClient) {
            if (data.equals(OXIDIZATION_LEVEL)) {
                this.updateOxidizationLevel();
            }
        }
    }

    public enum Oxidisation {
        UNAFFECTED,
        EXPOSED,
        WEATHERED,
        OXIDIZED;
    }
}

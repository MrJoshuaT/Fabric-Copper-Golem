package com.mrjoshuat.coppergolem.entity;

import com.google.common.collect.ImmutableList;
import com.mrjoshuat.coppergolem.ModInit;
import com.mrjoshuat.coppergolem.OxidizableBlockCallback;
import com.mrjoshuat.coppergolem.entity.goals.FindButtonsToPressGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.ai.goal.IronGolemWanderAroundGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Stream;

public class CopperGolemEntity extends GolemEntity {
    protected static final TrackedData<Integer> OXIDIZATION_LEVEL;
    protected static final TrackedData<Boolean> IS_WAXED;
    protected static final List<Item> ALL_AXES;

    private static int MIN_LEVEL = 0;
    private static int MAX_LEVEL = 3;

    private float lastDegradationTick = 0;

    static {
        OXIDIZATION_LEVEL = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
        IS_WAXED = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ALL_AXES = Arrays.asList(new Item[] {
                Items.NETHERITE_AXE, Items.DIAMOND_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.WOODEN_AXE
        });
    }

    public CopperGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);

        this.setupRandomTickListener();
    }

    protected void initGoals() {
        if (!oxidizationLevelValidForGoals()) {
            return;
        }

        this.goalSelector.add(1, new IronGolemWanderAroundGoal(this, 0.25D));
        this.goalSelector.add(2, new LookAroundGoal(this));
        this.goalSelector.add(3, new FindButtonsToPressGoal(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(5, new LookAtEntityGoal(this, IronGolemEntity.class, 10.0F));
    }

    public void tickMovement() {
        super.tickMovement();
        this.tickOxidisationAI();
    }

    protected void tickOxidisationAI() {
        var oxidisation = getOxidisation();
        if (oxidisation == Oxidisation.OXIDIZED && !this.isAiDisabled()) {
            this.setAiDisabled(true);
        } else if (oxidisation != Oxidisation.OXIDIZED && this.isAiDisabled()) {
            this.setAiDisabled(false);
        }
    }

    protected void tickDegradation() {
        if (this.random.nextFloat() < 0.005f) {
            this.incrementOxidisation();
            //ModInit.LOGGER.info("Increment oxidisation! Tick degradation " + lastDegradationTick);
        }
    }

    @Override
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        super.onStruckByLightning(world, lightning);
        // TODO: this logic should be adjusted
        this.setOxidisationLevel(MIN_LEVEL);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(OXIDIZATION_LEVEL, MIN_LEVEL);
        this.dataTracker.startTracking(IS_WAXED, false);
    }

    private void setupRandomTickListener() {
        OxidizableBlockCallback.EVENT.register(() -> {
            if (this.getWaxed()) {
                return ActionResult.PASS;
            }
            this.tickDegradation();
            return ActionResult.PASS;
        });
    }

    protected boolean oxidizationLevelValidForGoals() { return this.getOxidisation() != Oxidisation.OXIDIZED; }

    public void incrementOxidisation() {
        var level = this.getOxidizationLevel();
        if (level >= MAX_LEVEL) {
            return; // no more steps to go
        }
        this.setOxidisationLevel(level + 1);
    }

    public boolean decrementOxidization() {
        var level = this.getOxidizationLevel();
        if (level <= MIN_LEVEL) {
            return false;
        }
        this.setOxidisationLevel(level - 1);
        return true;
    }

    public Oxidisation getOxidisation() {
        return Oxidisation.from(this.getOxidizationLevel());
    }

    public int getOxidizationLevel() { return this.dataTracker.get(OXIDIZATION_LEVEL); }

    protected void setOxidisationLevel(int f) { this.dataTracker.set(OXIDIZATION_LEVEL, MathHelper.clamp(f, MIN_LEVEL, MAX_LEVEL)); }

    protected boolean getWaxed() { return this.dataTracker.get(IS_WAXED); }

    protected void setWaxed(boolean waxed) { this.dataTracker.set(IS_WAXED, waxed); }

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

        var handItem = player.getStackInHand(hand).getItem();

        // debug
        /*if (handItem == Items.DIAMOND_AXE) {
            this.incrementOxidisation();
            this.tickOxidisationAI();
            return ActionResult.success(this.world.isClient);
        }*/

        var validAxe = ALL_AXES.contains(handItem);

        if (validAxe && this.getWaxed()) {
            this.setWaxed(false);
            this.world.addParticle(ParticleTypes.WAX_OFF , getBlockX(), getBlockY(), getBlockZ(), 0d, 5d, 0d);
            return ActionResult.success(this.world.isClient);
        }

        if (validAxe) {
            if (this.decrementOxidization()) {
                this.tickOxidisationAI();
                return ActionResult.success(this.world.isClient);
            }
            return ActionResult.PASS;
        }

        if (handItem == Items.HONEYCOMB) {
            this.setWaxed(true);
            this.world.addParticle(ParticleTypes.WAX_ON, getBlockX(), getBlockY(), getBlockZ(), 0d, 5d, 0d);
            return ActionResult.success(this.world.isClient);
        }

        return ActionResult.PASS;
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

    public enum Oxidisation {
        UNAFFECTED(0),
        EXPOSED(1),
        WEATHERED(2),
        OXIDIZED(3);

        private static final List<IronGolemEntity.Crack> VALUES = (List) Stream.of(values()).sorted(Comparator.comparingDouble((oxidisation) -> {
            return (double)oxidisation.maxHealthFraction;
        })).collect(ImmutableList.toImmutableList());
        private final float maxHealthFraction;

        private Oxidisation(float maxHealthFraction) {
            this.maxHealthFraction = maxHealthFraction;
        }

        public static Oxidisation from(float healthFraction) {
            Iterator var1 = VALUES.iterator();

            Oxidisation crack;
            do {
                if (!var1.hasNext()) {
                    return OXIDIZED;
                }

                crack = (Oxidisation)var1.next();
            } while(!(healthFraction <= crack.maxHealthFraction));

            return crack;
        }
    }
}

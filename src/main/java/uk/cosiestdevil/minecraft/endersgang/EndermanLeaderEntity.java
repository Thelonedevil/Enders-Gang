package uk.cosiestdevil.minecraft.endersgang;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;

public class EndermanLeaderEntity extends EndermanEntity {
    public EndermanLeaderEntity(EntityType<? extends EndermanEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute attributeMap() {
        return EndermanEntity.func_234287_m_().createMutableAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void livingTick() {
        if (this.world.isRemote) {
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.CRIMSON_SPORE, this.getPosXRandom(0.5D), this.getPosYRandom() - 0.25D, this.getPosZRandom(0.5D), (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
            }
        }
        super.livingTick();
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!super.attackEntityFrom(source, amount)) {
            return false;
        } else if (!(this.world instanceof ServerWorld)) {
            return false;
        } else {
            ServerWorld serverworld = (ServerWorld) this.world;
            LivingEntity livingentity = this.getAttackTarget();
            if (livingentity == null && source.getTrueSource() instanceof LivingEntity) {
                livingentity = (LivingEntity) source.getTrueSource();
            }

            int i = MathHelper.floor(this.getPosX());
            int j = MathHelper.floor(this.getPosY());
            int k = MathHelper.floor(this.getPosZ());
            int max = this.rand.nextInt(5);
            for (int m = 0; m < max; ++m) {
                SummonEndermanAidEvent event = new SummonEndermanAidEvent(this, world, i, j, k, livingentity, this.getAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS).getValue());
                MinecraftForge.EVENT_BUS.post(event);
                if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return true;
                if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW ||
                        livingentity != null && this.world.getDifficulty() == Difficulty.HARD /*&& (double) this.rand.nextFloat() < this.getAttribute(Attributes.field_233829_l_).getValue()*/ && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                    EndermanEntity endermanEntity = event.getCustomSummonedAid() != null && event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW ? event.getCustomSummonedAid() : EntityType.ENDERMAN.create(this.world);

                    for (int l = 0; l < 50; ++l) {
                        int i1 = i + MathHelper.nextInt(this.rand, 2, 10) * MathHelper.nextInt(this.rand, -1, 1);
                        int j1 = j + MathHelper.nextInt(this.rand, 2, 10) * MathHelper.nextInt(this.rand, -1, 1);
                        int k1 = k + MathHelper.nextInt(this.rand, 2, 10) * MathHelper.nextInt(this.rand, -1, 1);
                        BlockPos blockpos = new BlockPos(i1, j1, k1);
                        EntityType<?> entitytype = endermanEntity.getType();
                        EntitySpawnPlacementRegistry.PlacementType entityspawnplacementregistry$placementtype = EntitySpawnPlacementRegistry.getPlacementType(entitytype);
                        if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(entityspawnplacementregistry$placementtype, this.world, blockpos, entitytype) && EntitySpawnPlacementRegistry.canSpawnEntity(entitytype, serverworld, SpawnReason.REINFORCEMENT, blockpos, this.world.rand)) {
                            endermanEntity.setPosition((double) i1, (double) j1, (double) k1);
                            if (!this.world.isPlayerWithin((double) i1, (double) j1, (double) k1, 7.0D) && this.world.checkNoEntityCollision(endermanEntity) && this.world.hasNoCollisions(endermanEntity) && !this.world.containsAnyLiquid(endermanEntity.getBoundingBox())) {
                                if (livingentity != null)
                                    endermanEntity.setAttackTarget(livingentity);
                                endermanEntity.onInitialSpawn(serverworld, this.world.getDifficultyForLocation(endermanEntity.getPosition()), SpawnReason.REINFORCEMENT, (ILivingEntityData) null, (CompoundNBT) null);
                                serverworld.func_242417_l(endermanEntity);
                                world.playSound(null,endermanEntity.getPosX(),endermanEntity.getPosY(),endermanEntity.getPosZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE,1,1);
                                endermanEntity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                                for (int a = 0; a < 40; ++a) {
                                    world.addParticle(ParticleTypes.PORTAL, endermanEntity.getPosXRandom(0.5D), endermanEntity.getPosYRandom() - 0.25D, endermanEntity.getPosZRandom(0.5D), (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
                                }
                                this.getAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS).applyPersistentModifier(new AttributeModifier("Enderman reinforcement caller charge", (double) -0.05F, AttributeModifier.Operation.ADDITION));
                                break;
                            }
                        }
                    }
                }
            }

            return true;
        }
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        applyAttributeBonuses(difficultyIn.getClampedAdditionalDifficulty());
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    protected void applyAttributeBonuses(float difficulty) {
        this.setBaseSummonChance();
        if (this.rand.nextFloat() < difficulty * 0.05F) {
            this.getAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS).applyPersistentModifier(new AttributeModifier("Leader enderman bonus", this.rand.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
            this.getAttribute(Attributes.MAX_HEALTH).applyPersistentModifier(new AttributeModifier("Leader enderman bonus", this.rand.nextDouble() * 3.0D + 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

    }

    protected void setBaseSummonChance() {
        this.getAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(this.rand.nextDouble() * net.minecraftforge.common.ForgeConfig.SERVER.zombieBaseSummonChance.get());
    }

}

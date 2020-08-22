package uk.cosiestdevil.minecraft.endersgang;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class EndermanLeader extends EndermanEntity {
    public EndermanLeader(EntityType<? extends EndermanEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute attributeMap() {
        return EndermanEntity.func_234287_m_().func_233814_a_(Attributes.field_233829_l_);
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

            SummonEndermanAidEvent event = new SummonEndermanAidEvent(this, world, i, j, k, livingentity, this.getAttribute(Attributes.field_233829_l_).getValue());
            MinecraftForge.EVENT_BUS.post(event);
            if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return true;
            if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW ||
                    livingentity != null && this.world.getDifficulty() == Difficulty.HARD && /*(double) this.rand.nextFloat() < this.getAttribute(Attributes.field_233829_l_).getValue() &&*/ this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                EndermanEntity endermanEntity = event.getCustomSummonedAid() != null && event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW ? event.getCustomSummonedAid() : EntityType.ENDERMAN.create(this.world);
                for (int m = 0; m < 5; ++m)
                    for (int l = 0; l < 500; ++l) {
                        int i1 = i + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
                        int j1 = j + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
                        int k1 = k + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
                        BlockPos blockpos = new BlockPos(i1, j1, k1);
                        EntityType<?> entitytype = endermanEntity.getType();
                        EntitySpawnPlacementRegistry.PlacementType entityspawnplacementregistry$placementtype = EntitySpawnPlacementRegistry.getPlacementType(entitytype);
                        if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(entityspawnplacementregistry$placementtype, this.world, blockpos, entitytype) && EntitySpawnPlacementRegistry.func_223515_a(entitytype, serverworld, SpawnReason.REINFORCEMENT, blockpos, this.world.rand)) {
                            endermanEntity.setPosition((double) i1, (double) j1, (double) k1);
                            if (!this.world.isPlayerWithin((double) i1, (double) j1, (double) k1, 7.0D) && this.world.checkNoEntityCollision(endermanEntity) && this.world.hasNoCollisions(endermanEntity) && !this.world.containsAnyLiquid(endermanEntity.getBoundingBox())) {
                                if (livingentity != null)
                                    endermanEntity.setAttackTarget(livingentity);
                                endermanEntity.onInitialSpawn(serverworld, this.world.getDifficultyForLocation(endermanEntity.func_233580_cy_()), SpawnReason.REINFORCEMENT, (ILivingEntityData) null, (CompoundNBT) null);
                                serverworld.func_242417_l(endermanEntity);
                                this.getAttribute(Attributes.field_233829_l_).func_233769_c_(new AttributeModifier("Zombie reinforcement caller charge", (double) -0.05F, AttributeModifier.Operation.ADDITION));
                                break;
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
            this.getAttribute(Attributes.field_233829_l_).func_233769_c_(new AttributeModifier("Leader enderman bonus", this.rand.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
            this.getAttribute(Attributes.field_233818_a_).func_233769_c_(new AttributeModifier("Leader enderman bonus", this.rand.nextDouble() * 3.0D + 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

    }

    protected void setBaseSummonChance() {
        this.getAttribute(Attributes.field_233829_l_).setBaseValue(this.rand.nextDouble() * net.minecraftforge.common.ForgeConfig.SERVER.zombieBaseSummonChance.get());
    }

}

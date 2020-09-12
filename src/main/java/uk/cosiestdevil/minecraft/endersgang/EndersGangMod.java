package uk.cosiestdevil.minecraft.endersgang;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Mod(EndersGangMod.MOD_ID)
public class EndersGangMod {
    public static final String MOD_ID = "endersgang";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public EndersGangMod() {
        ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        GlobalEntityTypeAttributes.put(EndermanLeader.get(), EndermanLeaderEntity.attributeMap().create());
        GlobalEntityTypeAttributes.put(EndermanLeader1.get(), EndermanLeaderEntity.attributeMap().create());
        ForgeRegistries.BIOMES.getValues().stream()
                .filter(biome -> biome.func_242433_b().func_242559_a(EntityClassification.MONSTER).stream().anyMatch(s -> s.field_242588_c == EntityType.ENDERMAN))
                .forEach(biome -> {
                    MobSpawnInfo info = biome.func_242433_b();
                    MobSpawnInfo.Builder builder = Helpers.GetBuilderForMobSpawnInfo(info);
                    Optional<MobSpawnInfo.Spawners> s = info.func_242559_a(EntityClassification.MONSTER).stream().filter(s1 -> s1.field_242588_c == EntityType.ENDERMAN).findFirst();
                    MobSpawnInfo.SpawnCosts sc = info.func_242558_a(EntityType.ENDERMAN);
                    s.ifPresent(spawners -> builder.func_242575_a(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EndermanLeader.get(), spawners.itemWeight / 2, spawners.field_242589_d / 2, spawners.field_242590_e / 2)));
                    if (sc != null) {
                        builder.func_242573_a(EndermanLeader.get(), sc.func_242582_a(), sc.func_242585_b());
                    }
                    biome.field_242425_l = builder.func_242577_b();
                });

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ClientStuff.Do(event);
    }


    @SubscribeEvent
    public void onEndermanReinforced(SummonEndermanAidEvent event) {
        EndermanEntity endermanEntity = EntityType.ENDERMAN.create(event.getWorld());
        endermanEntity.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 600, 1));
        event.setCustomSummonedAid(endermanEntity);
        event.setResult(Event.Result.ALLOW);
    }


    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static EntityType<EndermanLeaderEntity> CreateEnderLeaderEntityType(){
        return Helpers.SetupSpawnEgg(EntityType.Builder.create(EndermanLeaderEntity::new, EntityClassification.MONSTER).size(0.6F, 2.9F).func_233606_a_(8).build("enderman_leader"),EndermanLeaderSpawnEgg.get());
    }

    public static EntityType<EndermanLeaderEntity> CreateEnderLeaderEntityType1(){
        return Helpers.SetupSpawnEgg(EntityType.Builder.create(EndermanLeaderEntity::new, EntityClassification.MONSTER).size(0.6F, 2.9F).func_233606_a_(8).build("enderman_leader1"),EndermanLeaderSpawnEgg1.get());
    }
    public static final RegistryObject<EntityType<EndermanLeaderEntity>> EndermanLeader = ENTITY_TYPES.register("enderman_leader", EndersGangMod::CreateEnderLeaderEntityType);
    public static final RegistryObject<Item> EndermanLeaderSpawnEgg = ITEMS.register("enderman_leader_spawn_egg", () -> new ModSpawnEggItem(EndermanLeader, 1447446, 0, new Item.Properties().group(ItemGroup.MISC)));
    public static final RegistryObject<EntityType<EndermanLeaderEntity>> EndermanLeader1 = ENTITY_TYPES.register("enderman_leader1",EndersGangMod::CreateEnderLeaderEntityType1);
    public static final RegistryObject<Item> EndermanLeaderSpawnEgg1 = ITEMS.register("enderman_leader_spawn_egga", () -> new ModSpawnEggItem(EndermanLeader1, 1447446, 5456456, new Item.Properties().group(ItemGroup.MISC)));


}

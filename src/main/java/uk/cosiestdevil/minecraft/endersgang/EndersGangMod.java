package uk.cosiestdevil.minecraft.endersgang;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Optional;

@Mod("endersgang")
public class EndersGangMod {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public EndersGangMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        GlobalEntityTypeAttributes.put(RegistryEvents.leader, EndermanLeader.attributeMap().func_233813_a_());

        WorldGenRegistries.field_243657_i.stream()
                .filter(biome->biome.func_242433_b().func_242559_a(EntityClassification.MONSTER).stream().anyMatch(s->s.field_242588_c == EntityType.ENDERMAN))
                .forEach(biome->{
                    MobSpawnInfo info = biome.func_242433_b();
                    MobSpawnInfo.Builder builder =  new MobSpawnInfo.Builder();
                    if(info.func_242562_b()) {
                        builder.func_242571_a();
                    }
                    builder.func_242572_a(info.func_242557_a());
                    for(EntityClassification e : EntityClassification.values()){
                        for(MobSpawnInfo.Spawners s : info.func_242559_a(e) ){
                            builder.func_242575_a(e,s);
                        }
                    }

                    for(EntityType<?> entityType : ForgeRegistries.ENTITIES.getValues()){
                        MobSpawnInfo.SpawnCosts spawnCosts = info.func_242558_a(entityType);
                        if(spawnCosts!=null){
                            builder.func_242573_a(entityType,spawnCosts.func_242582_a(),spawnCosts.func_242585_b());
                        }
                    }
                    Optional<MobSpawnInfo.Spawners> s = info.func_242559_a(EntityClassification.MONSTER).stream().filter(s1->s1.field_242588_c==EntityType.ENDERMAN).findFirst();
                    MobSpawnInfo.SpawnCosts sc = info.func_242558_a(EntityType.ENDERMAN);
                    s.ifPresent(spawners -> builder.func_242575_a(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(RegistryEvents.leader, spawners.itemWeight, spawners.field_242589_d, spawners.field_242590_e)));
                    if(sc!=null){
                        builder.func_242573_a(RegistryEvents.leader, sc.func_242582_a(), sc.func_242585_b());
                    }

                    biome.field_242425_l = builder.func_242577_b();
                });

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        EntityRendererManager entityRendererManager = event.getMinecraftSupplier().get().getRenderManager();
        entityRendererManager.register(RegistryEvents.leader, new EndermanLeaderRenderer(entityRendererManager));
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
    }

    private void processIMC(final InterModProcessEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        static EntityType.Builder<EndermanLeader> builder = EntityType.Builder.create(EndermanLeader::new, EntityClassification.MONSTER).size(0.6F, 2.9F).func_233606_a_(8);
        public static EntityType<EndermanLeader> leader = builder.build("enderman_leader");

        @SubscribeEvent
        public static void onEntityRegistry(RegistryEvent.Register<EntityType<?>> event) {
            event.getRegistry().register(leader.setRegistryName("enderman_leader"));

        }

        @SubscribeEvent
        public static void registerSpawnEggs(final RegistryEvent.Register<Item> event) {
            event.getRegistry().register(new SpawnEggItem(leader, 1447446, 0, new Item.Properties().group(ItemGroup.MISC)).setRegistryName("enderman_leader_spawn_egg"));
        }


    }


}

package uk.cosiestdevil.minecraft.endersgang;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.Map;

public class Helpers {
    public static MobSpawnInfo.Builder GetBuilderForMobSpawnInfo(MobSpawnInfo info){
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
        return builder;
    }
    private static  Field eggsField = ObfuscationReflectionHelper.findField(SpawnEggItem.class,"EGGS");
    static{
        eggsField.setAccessible(true);
    }
    public static  EntityType<EndermanLeaderEntity> SetupSpawnEgg(EntityType<EndermanLeaderEntity> type, Item item){
        try {
            Map<EntityType<?>, SpawnEggItem> eggs = (Map<EntityType<?>, SpawnEggItem>) eggsField.get(null);
            eggs.remove(null);
            eggs.put(type, (SpawnEggItem) item);
        } catch (IllegalAccessException e) {
            //LOGGER.error(e);
        }
        return type;
    }
}

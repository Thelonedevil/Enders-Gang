package uk.cosiestdevil.minecraft.endersgang;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientStuff {
    public static void Do(FMLClientSetupEvent event){
        EntityRendererManager entityRendererManager = event.getMinecraftSupplier().get().getRenderManager();
        entityRendererManager.register(EndersGangMod.EndermanLeader.get(), new EndermanLeaderRenderer(entityRendererManager));
        entityRendererManager.register(EndersGangMod.EndermanLeader1.get(), new EndermanLeaderRenderer(entityRendererManager));
    }
}

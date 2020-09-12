package uk.cosiestdevil.minecraft.endersgang;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;

public class ModSpawnEggItem extends SpawnEggItem {
    private RegistryObject<? extends EntityType<?>> typeSupplier;

    public ModSpawnEggItem(@Nonnull RegistryObject<? extends EntityType<?>> typeIn, int primaryColorIn, int secondaryColorIn, Properties builder) {
        super(null, primaryColorIn, secondaryColorIn, builder);
        typeSupplier = typeIn;
        DispenserBlock.registerDispenseBehavior(this, spawnEggDispenseBehaviour);
       /* Field eggsField = ObfuscationReflectionHelper.findField(SpawnEggItem.class, "EGGS");
        eggsField.setAccessible(true);
        try {
            Map<EntityType<?>, SpawnEggItem> eggs = (Map<EntityType<?>, SpawnEggItem>) eggsField.get(null);
            eggs.remove(null);
            eggsField.set(null, eggs);
        } catch (IllegalAccessException e) {
            //LOGGER.error(e);
        }*/
    }
    private static DefaultDispenseItemBehavior spawnEggDispenseBehaviour = new DefaultDispenseItemBehavior() {
        /**
         * Dispense the specified stack, play the dispense sound and spawn particles.
         */
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().get(DispenserBlock.FACING);
            EntityType<?> entitytype = ((SpawnEggItem)stack.getItem()).getType(stack.getTag());
            entitytype.spawn(source.getWorld(), stack, (PlayerEntity)null, source.getBlockPos().offset(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
            stack.shrink(1);
            return stack;
        }
    };
    @Override
    public EntityType<?> getType(@Nullable CompoundNBT p_208076_1_) {
        EntityType<?> type = super.getType(p_208076_1_);
        if(type==null){
            return typeSupplier.get();
        }else
            return type;
    }
}

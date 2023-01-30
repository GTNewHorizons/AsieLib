package pl.asie.lib.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import pl.asie.lib.AsieLibMod;

public class ItemUtils {

    public static void dropItems(World world, int x, int y, int z, IInventory inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);

            if (item != null && item.stackSize > 0) {
                inventory.setInventorySlotContents(i, null);
                dropItem(world, x, y, z, item);
                item.stackSize = 0;
            }
        }
    }

    public static void dropItems(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || !(tileEntity instanceof IInventory)) {
            return;
        }
        IInventory inventory = (IInventory) tileEntity;
        dropItems(world, x, y, z, inventory);
    }

    public static void dropItem(World world, int x, int y, int z, ItemStack item) {
        float rx = AsieLibMod.rand.nextFloat() * 0.8F + 0.1F;
        float ry = AsieLibMod.rand.nextFloat() * 0.8F + 0.1F;
        float rz = AsieLibMod.rand.nextFloat() * 0.8F + 0.1F;

        EntityItem entityItem = new EntityItem(
                world,
                x + rx,
                y + ry,
                z + rz,
                new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));

        if (item.hasTagCompound()) {
            entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
        }

        float factor = 0.05F;
        entityItem.motionX = AsieLibMod.rand.nextGaussian() * factor;
        entityItem.motionY = AsieLibMod.rand.nextGaussian() * factor + 0.2F;
        entityItem.motionZ = AsieLibMod.rand.nextGaussian() * factor;
        world.spawnEntityInWorld(entityItem);
    }
}

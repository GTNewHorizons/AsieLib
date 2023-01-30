package pl.asie.lib.api.tile;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;

public interface IInventoryProvider extends IInventory, ISidedInventory {

    public void onSlotUpdate(int slot);
}

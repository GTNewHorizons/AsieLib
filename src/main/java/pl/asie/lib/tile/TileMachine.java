package pl.asie.lib.tile;

import cpw.mods.fml.common.Optional;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import mods.immibis.redlogic.api.wiring.IBareRedstoneWire;
import mods.immibis.redlogic.api.wiring.IBundledEmitter;
import mods.immibis.redlogic.api.wiring.IBundledWire;
import mods.immibis.redlogic.api.wiring.IConnectable;
import mods.immibis.redlogic.api.wiring.IWire;
import mrtjp.projectred.api.ProjectRedAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.lib.api.tile.IBattery;
import pl.asie.lib.api.tile.IBundledRedstoneProvider;
import pl.asie.lib.api.tile.IInformationProvider;
import pl.asie.lib.block.BlockBase;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.lib.reference.Mods;
import pl.asie.lib.util.EnergyConverter;

import java.util.ArrayList;
import java.util.List;

@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IConnectable", modid = Mods.RedLogic)
})
public class TileMachine extends TileEntityBase implements
	IConnectable, ISidedInventory /* RedLogic */ {

	private IBattery battery;
	private IBundledRedstoneProvider brP;
	private ItemStack[] items;

	public TileMachine() {
	}

	public IBattery getBatteryProvider() {
		return battery;
	}

	public IBundledRedstoneProvider getBundledRedstoneProvider() {
		return brP;
	}

	protected void registerBundledRedstone(IBundledRedstoneProvider brP) {
		this.brP = brP;
	}

	protected void registerBattery(IBattery p) {
		this.battery = p;
	}

	protected void createInventory(int slots) {
		this.items = new ItemStack[slots];
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!didInitIC2) {
			if(Mods.isLoaded(Mods.IC2) && this.battery != null) {
				this.initIC();
			}
			didInitIC2 = true; // Just so this check won't be done every tick.
		}
		if(!didInitIC2C) {
			didInitIC2C = true; // Just so this check won't be done every tick.
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(Mods.isLoaded(Mods.IC2) && this.battery != null) {
			this.deinitIC();
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(Mods.isLoaded(Mods.IC2) && this.battery != null) {
			this.deinitIC();
		}
	}
	// (Bundled) Redstone

	@Optional.Method(modid = Mods.RedLogic)
	public boolean connects(IWire wire, int blockFace, int fromDirection) {
		if(wire instanceof IBareRedstoneWire && this.blockType != null
			&& ((BlockBase) this.blockType).emitsRedstone(worldObj, xCoord, yCoord, zCoord, fromDirection)) {
			return true;
		} else if(wire instanceof IBundledWire) {
			if(this.brP != null) {
				return this.brP.canBundledConnectTo(fromDirection, blockFace);
			} else {
				return false;
			}
		}

		return false;
	}

	@Optional.Method(modid = Mods.RedLogic)
	public boolean connectsAroundCorner(IWire wire, int blockFace,
		int fromDirection) {
		return false;
	}

	@Optional.Method(modid = Mods.RedLogic)
	public void onBundledInputChanged() {
		if(this.brP != null) {
			for(int side = 0; side < 6; side++) {
				IBundledEmitter be = null;
				for(int face = -1; face < 6; face++) {
					if(this.brP.canBundledConnectTo(side, face)) {
						if(be == null) {
							ForgeDirection fd = ForgeDirection.values()[side];
							TileEntity t = worldObj.getTileEntity(xCoord + fd.offsetX, yCoord + fd.offsetY, zCoord + fd.offsetZ);
							if(t != null && t instanceof IBundledEmitter) {
								be = (IBundledEmitter) t;
							} else {
								break;
							}
						}
						this.brP.onBundledInputChange(side, face, be.getBundledCableStrength(face, side));
					}
				}
			}
		}
	}

	@Optional.Method(modid = Mods.RedLogic)
	public byte[] getBundledCableStrength(int blockFace, int toDirection) {
		if(this.brP != null && this.brP.canBundledConnectTo(toDirection, blockFace)) {
			return this.brP.getBundledOutput(toDirection, blockFace);
		} else {
			return null;
		}
	}

	@Optional.Method(modid = Mods.ProjectRed)
	public byte[] getBundledSignal(int side) {
		if(this.brP != null && this.brP.canBundledConnectTo(side, -1)) {
			return this.brP.getBundledOutput(side, -1);
		} else {
			return null;
		}
	}

	@Optional.Method(modid = Mods.ProjectRed)
	public boolean canConnectBundled(int side) {
		return this.brP.canBundledConnectTo(side, -1);
	}

	@Optional.Method(modid = Mods.ProjectRed)
	public void onProjectRedBundledInputChanged() {
		if(this.brP != null) {
			for(int i = 0; i < 6; i++) {
				if(!this.brP.canBundledConnectTo(i, -1)) {
					continue;
				}
				byte[] data = ProjectRedAPI.transmissionAPI.getBundledInput(worldObj, xCoord, yCoord, zCoord, i);
				if(data != null) {
					this.brP.onBundledInputChange(i, -1, data);
				}
			}
		}
	}

	// Inventory

	public void closeInventory() {
	}

	public void openInventory() {
	}

	public void onSlotUpdate(int slot) {

	}

	public ItemStack decrStackSize(int slot, int amount) {
		if(this.items != null && this.items[slot] != null) {
			ItemStack stack;
			if(this.items[slot].stackSize <= amount) {
				stack = this.items[slot];
				this.items[slot] = null;
				this.onSlotUpdate(slot);
				return stack;
			} else {
				stack = this.items[slot].splitStack(amount);

				if(this.items[slot].stackSize == 0) {
					this.items[slot] = null;
				}

				this.onSlotUpdate(slot);

				return stack;
			}
		} else {
			return null;
		}
	}

	public String getInventoryName() {
		return this.blockType != null ? this.blockType.getUnlocalizedName() + ".inventory" : null;
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public int getSizeInventory() {
		if(this.items != null) {
			return this.items.length;
		} else {
			return 0;
		}
	}

	public ItemStack getStackInSlot(int slot) {
		if(this.items != null && slot >= 0 && slot < this.items.length) {
			return this.items[slot];
		} else {
			return null;
		}
	}

	public ItemStack getStackInSlotOnClosing(int slot) {
		if(this.items != null && slot >= 0 && slot < this.items.length) {
			ItemStack is = this.getStackInSlot(slot);
			this.setInventorySlotContents(slot, null);
			return is;
		} else {
			return null;
		}
	}

	public boolean hasCustomInventoryName() {
		return false;
	}

	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if(this.items != null && slot >= 0 && slot < this.items.length) {
			return true;
		} else {
			return false;
		}
	}

	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(this.items != null && slot >= 0 && slot < this.items.length) {
			this.items[slot] = stack;
			this.onSlotUpdate(slot);
		}
	}

	public boolean canExtractItem(int arg0, ItemStack arg1, int arg2) {
		return true;
	}

	public boolean canInsertItem(int arg0, ItemStack arg1, int arg2) {
		return true;
	}

	public int[] getAccessibleSlotsFromSide(int arg0) {
		if(this.items == null) {
			return new int[0];
		}
		int[] slots = new int[this.items.length];
		for(int i = 0; i < slots.length; i++) {
			slots[i] = i;
		}
		return slots;
	}

	// Energy (RF)

	public boolean canConnectEnergy(ForgeDirection from) {
		if(this.battery != null) {
			return this.battery.canInsert(from.ordinal(), "RF");
		} else {
			return false;
		}
	}

	public int receiveEnergy(ForgeDirection from, int maxReceive,
		boolean simulate) {
		if(this.battery != null && this.battery.canInsert(from.ordinal(), "RF")) {
			return (int) Math.floor(this.battery.insert(from.ordinal(), maxReceive, simulate));
		} else {
			return 0;
		}
	}

	public int extractEnergy(ForgeDirection from, int maxExtract,
		boolean simulate) {
		if(this.battery != null && this.battery.canExtract(from.ordinal(), "RF")) {
			return (int) Math.floor(this.battery.extract(from.ordinal(), maxExtract, simulate));
		} else {
			return 0;
		}
	}

	public int getEnergyStored(ForgeDirection from) {
		if(this.battery != null) {
			return (int) Math.floor(this.battery.getEnergyStored());
		} else {
			return 0;
		}
	}

	public int getMaxEnergyStored(ForgeDirection from) {
		if(this.battery != null) {
			return (int) Math.floor(this.battery.getMaxEnergyStored());
		} else {
			return 0;
		}
	}

	// Energy (EU - IC2 Experimental)

	private boolean didInitIC2 = false;

	@Optional.Method(modid = Mods.IC2)
	public void initIC() {
		if(!didInitIC2 && (worldObj == null || !worldObj.isRemote)) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) this));
		}
		didInitIC2 = true;
	}

	@Optional.Method(modid = Mods.IC2)
	public void deinitIC() {
		if(didInitIC2 && (worldObj == null || !worldObj.isRemote)) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) this));
		}
		didInitIC2 = false;
	}

	@Optional.Method(modid = Mods.IC2)
	public boolean acceptsEnergyFrom(TileEntity emitter,
		ForgeDirection direction) {
		if(this.battery != null) {
			return this.battery.canInsert(direction.ordinal(), "EU");
		} else {
			return false;
		}
	}

	@Optional.Method(modid = Mods.IC2)
	public double injectEnergy(ForgeDirection directionFrom, double amount,
		double voltage) {
		if(this.battery != null) {
			double amountRF = EnergyConverter.convertEnergy(amount, "EU", "RF");
			double injectedRF = this.battery.insert(directionFrom.ordinal(), amountRF, false);
			return amount - EnergyConverter.convertEnergy(injectedRF, "RF", "EU");
		} else {
			return amount;
		}
	}

	@Optional.Method(modid = Mods.IC2)
	public double getDemandedEnergy() {
		if(this.battery != null) {
			return EnergyConverter.convertEnergy(battery.getMaxEnergyInserted(), "RF", "EU");
		} else {
			return 0.0;
		}
	}

	@Optional.Method(modid = Mods.IC2)
	public int getSinkTier() {
		return Integer.MAX_VALUE;
	}

	// Energy (EU - IC2 Classic)

	private boolean didInitIC2C = false;

	// NBT
	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		if(this.battery != null) {
			this.battery.readFromNBT(tagCompound);
		}
		if(this.items != null) {
			NBTTagList nbttaglist = tagCompound.getTagList("Inventory", 10);
			this.items = new ItemStack[this.getSizeInventory()];

			for(int i = 0; i < nbttaglist.tagCount(); ++i) {
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound1.getByte("Slot") & 255;

				if(j >= 0 && j < this.items.length) {
					this.items[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		if(this.battery != null) {
			this.battery.writeToNBT(tagCompound);
		}
		if(this.items != null) {
			NBTTagList itemList = new NBTTagList();
			for(int i = 0; i < items.length; i++) {
				ItemStack stack = items[i];
				if(stack != null) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setByte("Slot", (byte) i);
					stack.writeToNBT(tag);
					itemList.appendTag(tag);
				}
			}
			tagCompound.setTag("Inventory", itemList);
		}
	}

	// Remove NBT data for transfer with the BuildCraft Builder
	public void removeFromNBTForTransfer(NBTTagCompound data) {
		data.removeTag("Inventory");
		data.removeTag("bb_energy");
	}

	@Optional.Method(modid = Mods.API.CoFHTileEntities)
	public int getInfoEnergyPerTick() {
		if(this.battery != null) {
			return (int) Math.round(battery.getEnergyUsage());
		} else {
			return 0;
		}
	}

	@Optional.Method(modid = Mods.API.CoFHTileEntities)
	public int getInfoMaxEnergyPerTick() {
		if(this.battery != null) {
			return (int) Math.round(battery.getMaxEnergyUsage());
		} else {
			return 0;
		}
	}

	@Optional.Method(modid = Mods.API.CoFHTileEntities)
	public int getInfoEnergyStored() {
		if(this.battery != null) {
			return (int) Math.round(battery.getEnergyStored());
		} else {
			return 0;
		}
	}

	@Optional.Method(modid = Mods.API.CoFHTileEntities)
	public int getInfoMaxEnergyStored() {
		if(this.battery != null) {
			return (int) Math.round(battery.getMaxEnergyStored());
		} else {
			return 0;
		}
	}

	@Optional.Method(modid = Mods.API.CoFHTileEntities)
	public void getTileInfo(List<IChatComponent> info, ForgeDirection side,
		EntityPlayer player, boolean debug) {
		if(this instanceof IInformationProvider) {
			IInformationProvider p = (IInformationProvider) this;
			ArrayList<String> data = new ArrayList<String>();
			p.getInformation(player, side, data, debug);
			for(String s : data) {
				info.add(new ChatComponentText(s));
			}
		}
	}

	/*
	@Optional.Method(modid = Mods.GregTech)
	public boolean isGivingInformation() {
		return (this instanceof IInformationProvider);
	}

	@Optional.Method(modid = Mods.GregTech)
	public String[] getInfoData() {
		if(this instanceof IInformationProvider) {
			IInformationProvider p = (IInformationProvider) this;
			ArrayList<String> data = new ArrayList<String>();
			p.getInformation(null, ForgeDirection.UNKNOWN, data, false);
			return data.toArray(new String[data.size()]);
		} else {
			return new String[] {};
		}
	}
	*/
}

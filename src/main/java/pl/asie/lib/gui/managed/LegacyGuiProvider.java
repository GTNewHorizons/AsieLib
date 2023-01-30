package pl.asie.lib.gui.managed;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import pl.asie.lib.block.ContainerBase;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.lib.gui.GuiBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author Vexatos
 */
public abstract class LegacyGuiProvider extends GuiProviderBase {

    @SideOnly(Side.CLIENT)
    protected abstract GuiBase makeGuiBase(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z,
            TileEntityBase tile);

    protected abstract ContainerBase makeContainerBase(int guiID, EntityPlayer entityPlayer, World world, int x, int y,
            int z, TileEntityBase tile);

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer makeGui(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityBase) {
            return makeGuiBase(guiID, entityPlayer, world, x, y, z, (TileEntityBase) tile);
        }
        return null;
    }

    @Override
    public Container makeContainer(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityBase) {
            return makeContainerBase(guiID, entityPlayer, world, x, y, z, (TileEntityBase) tile);
        }
        return null;
    }
}

package pl.asie.lib.gui;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import pl.asie.lib.AsieLibMod;
import pl.asie.lib.block.ContainerBase;
import pl.asie.lib.block.TileEntityBase;
import cpw.mods.fml.common.network.IGuiHandler;

@Deprecated
public class GuiHandler implements IGuiHandler {

    private final ArrayList<Class<? extends ContainerBase>> containers;
    private final ArrayList<Class<? extends GuiBase>> guis;

    public GuiHandler() {
        containers = new ArrayList<Class<? extends ContainerBase>>();
        guis = new ArrayList<Class<? extends GuiBase>>();
    }

    @Deprecated
    public int registerGui(Class<? extends ContainerBase> container, Class<? extends GuiBase> gui) {
        containers.add(container);
        guis.add(gui);
        return guis.size() - 1;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        try {
            if (ID < 0 || ID >= containers.size()) return null;
            else {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te == null || !(te instanceof TileEntityBase)) {
                    AsieLibMod.log.warn("Invalid TE for requested ContainerBase! This is a bug!");
                    return null;
                } else return containers.get(ID).getConstructor(TileEntityBase.class, InventoryPlayer.class)
                        .newInstance((TileEntityBase) te, player.inventory);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        try {
            ContainerBase cb = (ContainerBase) getServerGuiElement(ID, player, world, x, y, z);
            if (ID < 0 || ID >= guis.size()) return null;
            else return guis.get(ID).getConstructor(ContainerBase.class).newInstance(cb);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

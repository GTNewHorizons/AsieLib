package pl.asie.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import pl.asie.lib.block.ContainerBase;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.lib.gui.GuiBase;

public class GuiUtils {

    public static TileEntityBase currentTileEntity() {
        GuiScreen gc = Minecraft.getMinecraft().currentScreen;
        if (gc instanceof GuiBase) {
            Container container = ((GuiBase) gc).container;
            if (container instanceof ContainerBase) {
                return ((ContainerBase) container).getEntity();
            }
        }
        return null;
    }

    public static GuiBase currentGui() {
        GuiScreen gc = Minecraft.getMinecraft().currentScreen;
        if (gc instanceof GuiBase) {
            return ((GuiBase) gc);
        }
        return null;
    }
}

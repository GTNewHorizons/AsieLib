package pl.asie.lib.gui;

import pl.asie.lib.block.ContainerBase;

public class GuiBase extends GuiSpecialContainer<ContainerBase> {

    public GuiBase(ContainerBase container, String textureName, int xSize, int ySize) {
        super(container, textureName, xSize, ySize);
    }

}

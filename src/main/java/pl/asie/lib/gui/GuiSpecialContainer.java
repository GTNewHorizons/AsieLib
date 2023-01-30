package pl.asie.lib.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/**
 * @author Vexatos
 */
public class GuiSpecialContainer<C extends Container> extends GuiContainer {

    private final ResourceLocation texture;
    public int xCenter, yCenter;
    public final C container;

    public GuiSpecialContainer(C container, String textureName, int xSize, int ySize) {
        super(container);
        this.container = container;
        this.texture = new ResourceLocation(
                textureName.split(":")[0],
                "textures/gui/" + textureName.split(":")[1] + ".png");
        this.xSize = xSize;
        this.ySize = ySize;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.xCenter = (this.width - this.xSize) / 2;
        this.yCenter = (this.height - this.ySize) / 2;
        this.mc.getTextureManager().bindTexture(texture);
        this.drawTexturedModalRect(this.xCenter, this.yCenter, 0, 0, this.xSize, this.ySize);
    }
}

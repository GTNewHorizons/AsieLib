package pl.asie.lib.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import pl.asie.lib.block.BlockBase;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class BlockBaseRender implements ISimpleBlockRenderingHandler {

    private static int renderId;
    public static int renderPass;

    private static final int[][] ROTATION = { { 3, 3, 1, 2, 0, 0 }, // Down
            { 0, 0, 2, 1, 0, 0 }, // Up
            { 3, 3, 0, 0, 0, 0 }, // North
            { 0, 0, 0, 0, 0, 0 }, // South
            { 1, 1, 0, 0, 0, 0 }, // West
            { 2, 2, 0, 0, 0, 0 } // East
    };

    public BlockBaseRender() {
        this.renderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(this);
    }

    public boolean renderBlock(boolean isInventory, BlockBase block, int x, int y, int z, int side, int metadata,
            RenderBlocks renderer) {
        renderer.uvRotateEast = ROTATION[side][5];
        renderer.uvRotateWest = ROTATION[side][4];
        renderer.uvRotateSouth = ROTATION[side][3];
        renderer.uvRotateNorth = ROTATION[side][2];
        renderer.uvRotateTop = ROTATION[side][1];
        renderer.uvRotateBottom = ROTATION[side][0];
        if (isInventory) {
            Tessellator tessellator = Tessellator.instance;
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getAbsoluteIcon(0, metadata));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getAbsoluteIcon(1, metadata));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getAbsoluteIcon(4, metadata));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getAbsoluteIcon(5, metadata));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getAbsoluteIcon(3, metadata));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getAbsoluteIcon(2, metadata));
            tessellator.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        } else renderer.renderStandardBlock(block, x, y, z);
        renderer.uvRotateEast = 0;
        renderer.uvRotateWest = 0;
        renderer.uvRotateSouth = 0;
        renderer.uvRotateNorth = 0;
        renderer.uvRotateTop = 0;
        renderer.uvRotateBottom = 0;
        return true;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        renderBlock(true, (BlockBase) block, 0, 0, 0, 2, metadata, renderer);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
            RenderBlocks renderer) {
        int metadata = world.getBlockMetadata(x, y, z);
        int side = ((BlockBase) block).getFrontSide(metadata);
        return renderBlock(false, (BlockBase) block, x, y, z, side, metadata, renderer);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return this.renderId;
    }

    public static int id() {
        return renderId;
    }
}

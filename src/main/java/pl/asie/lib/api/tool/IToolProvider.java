package pl.asie.lib.api.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public interface IToolProvider {

    /**
     * @param stack  The ItemStack to check
     * @param player The player holding the item
     * @param x      The x coordinate of the block the tool is used on
     * @param y      The y coordinate of the block the tool is used on
     * @param z      The z coordinate of the block the tool is used on
     * @return true if the provided ItemStack is a valid tool
     */
    public boolean isTool(ItemStack stack, EntityPlayer player, int x, int y, int z);

    /**
     * @param stack  The ItemStack to check
     * @param player The player holding the item
     * @param x      The x coordinate of the block the tool is used on
     * @param y      The y coordinate of the block the tool is used on
     * @param z      The z coordinate of the block the tool is used on
     * @return true if the tool has been successfully used
     */
    public boolean useTool(ItemStack stack, EntityPlayer player, int x, int y, int z);
}

package pl.asie.lib.integration.tool.oc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import li.cil.oc.api.internal.Wrench;
import pl.asie.lib.api.tool.IToolProvider;

/**
 * @author Vexatos
 */
public class ToolProviderOC implements IToolProvider {

    @Override
    public boolean isTool(ItemStack stack, EntityPlayer player, int x, int y, int z) {
        return stack.getItem() instanceof Wrench;
    }

    @Override
    public boolean useTool(ItemStack stack, EntityPlayer player, int x, int y, int z) {
        if (stack.getItem() instanceof Wrench) {
            Wrench wrench = (Wrench) stack.getItem();
            if (wrench.useWrenchOnBlock(player, player.worldObj, x, y, z, true)) {
                wrench.useWrenchOnBlock(player, player.worldObj, x, y, z, false);
                return true;
            }
        }
        return false;
    }
}

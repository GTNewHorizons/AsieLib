package pl.asie.lib;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.world.World;

import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {

    public boolean isClient() {
        return true;
    }

    public File getMinecraftDirectory() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @Override
    public World getWorld(int dimensionId) {
        if (getCurrentClientDimension() != dimensionId) {
            return null;
        } else return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public int getCurrentClientDimension() {
        return Minecraft.getMinecraft().theWorld.provider.dimensionId;
    }

    @Override
    public void handlePacket(MessageHandlerBase client, MessageHandlerBase server, Packet packet, INetHandler handler) {
        try {
            switch (FMLCommonHandler.instance().getEffectiveSide()) {
                case CLIENT:
                    if (client != null)
                        client.onMessage(packet, handler, (EntityPlayer) Minecraft.getMinecraft().thePlayer);
                    break;
                case SERVER:
                    super.handlePacket(client, server, packet, handler);
                    break;
            }
        } catch (Exception e) {
            AsieLibMod.log.warn("Caught a network exception! Is someone sending malformed packets?");
            e.printStackTrace();
        }
    }
}

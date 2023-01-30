package pl.asie.lib;

import java.io.File;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;

public class CommonProxy {

    public boolean isClient() {
        return false;
    }

    public File getMinecraftDirectory() {
        return new File(".");
    }

    public World getWorld(int dimensionId) {
        return MinecraftServer.getServer().worldServerForDimension(dimensionId);
    }

    public int getCurrentClientDimension() {
        return -9001;
    }

    public void handlePacket(MessageHandlerBase client, MessageHandlerBase server, Packet packet, INetHandler handler) {
        try {
            if (server != null) server.onMessage(packet, handler, ((NetHandlerPlayServer) handler).playerEntity);
        } catch (Exception e) {
            AsieLibMod.log.warn("Caught a network exception! Is someone sending malformed packets?");
            e.printStackTrace();
        }
    }
}

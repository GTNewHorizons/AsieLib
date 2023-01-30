package pl.asie.lib.api.tile;

import pl.asie.lib.reference.Mods;
import cofh.api.energy.IEnergyHandler;
import cofh.api.tileentity.IEnergyInfo;
import cpw.mods.fml.common.Optional;
import ic2.api.energy.tile.IEnergySink;

@Optional.InterfaceList({
        @Optional.Interface(iface = "cofh.api.tileentity.IEnergyInfo", modid = Mods.API.CoFHTileEntities),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = Mods.IC2),
        @Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = Mods.API.CoFHEnergy) })
public interface IBatteryProvider extends IEnergyHandler, IEnergyInfo, /* RF */
        IEnergySink {

}

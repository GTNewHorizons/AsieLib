package pl.asie.lib.reference;

import java.util.HashMap;

import com.google.common.collect.Iterables;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;

/**
 * @author Vexatos
 */
public class Mods {

    // The mod itself
    public static final String AsieLib = "asielib", AsieLib_NAME = "AsieLib";

    // Other mods
    public static final String AE2 = "appliedenergistics2", IC2 = "IC2",
            // GregTech = "gregtech",
            Mekanism = "Mekanism", ProjectRed = "ProjRed|Core", RedLogic = "RedLogic";

    // Other APIs
    public static class API {

        public static final String BuildCraftTools = "BuildCraftAPI|tools", CoFHBlocks = "CoFHAPI|block",
                CoFHEnergy = "CoFHAPI|energy", CoFHItems = "CoFHAPI|item", CoFHTileEntities = "CoFHAPI|tileentity",
                EiraIRC = "EiraIRC|API", EnderIOTools = "EnderIOAPI|Tools",
                OpenComputersInternal = "OpenComputersAPI|Internal";

        public static boolean hasAPI(String name) {
            return ModAPIManager.INSTANCE.hasAPI(name);
        }
    }

    public static boolean isLoaded(String name) {
        return Loader.isModLoaded(name);
    }

    // Mod versions

    private static HashMap<String, ArtifactVersion> modVersionList;

    public static ArtifactVersion getVersion(String name) {
        if (modVersionList == null) {
            modVersionList = new HashMap<String, ArtifactVersion>();

            for (ModContainer api : Iterables
                    .concat(Loader.instance().getActiveModList(), ModAPIManager.INSTANCE.getAPIList())) {
                modVersionList.put(api.getModId(), api.getProcessedVersion());
            }
        }

        if (modVersionList.containsKey(name)) {
            return modVersionList.get(name);
        }
        throw new IllegalArgumentException("Mod/API '" + name + "' does not exist!");
    }

    public static boolean hasVersion(String name, String version) {
        if (isLoaded(name) || API.hasAPI(name)) {
            ArtifactVersion v1 = VersionParser.parseVersionReference(name + "@" + version);
            ArtifactVersion v2 = getVersion(name);
            return v1.containsVersion(v2);
        }
        return false;
    }

    // Energy related

    private static boolean checkedEnergyMods = false;
    private static boolean hasEnergyMod = false;

    public static boolean hasEnergyMod() {
        if (!checkedEnergyMods) {
            hasEnergyMod = API.hasAPI(API.CoFHEnergy) || isLoaded(IC2);
            checkedEnergyMods = true;
        }
        return hasEnergyMod;
    }
}

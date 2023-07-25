package pl.asie.lib.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.block.IBlockInfo;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pl.asie.lib.api.tile.IInformationProvider;
import pl.asie.lib.client.BlockBaseRender;
import pl.asie.lib.gui.managed.IGuiProvider;
import pl.asie.lib.integration.Integration;
import pl.asie.lib.reference.Mods;
import pl.asie.lib.tile.TileMachine;
import pl.asie.lib.util.ItemUtils;
import pl.asie.lib.util.MiscUtils;

@Optional.InterfaceList({
        // @Optional.Interface(iface = "gregtech.api.interfaces.IDebugableBlock", modid = Mods.GregTech),
        @Optional.Interface(iface = "cofh.api.block.IBlockInfo", modid = Mods.API.CoFHBlocks) })
public abstract class BlockBase extends BlockContainer implements IBlockInfo/* , IDebugableBlock */ {

    public enum Rotation {
        NONE,
        FOUR,
        SIX
    }

    private Rotation rotation = Rotation.NONE;
    private final Object parent;
    private int gui = -1;
    protected IGuiProvider guiProvider;

    public BlockBase(Material material, Object parent) {
        super(material);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setHardness(2.0F);
        this.parent = parent;
    }

    // Handler: Redstone

    @Deprecated
    public boolean emitsRedstone(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    public boolean emitsRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }

    public boolean receivesRedstone(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    // Vanilla
    public int getVanillaRedstoneValue(World world, int x, int y, int z) {
        return world.getBlockPowerInput(x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (receivesRedstone(world, x, y, z)) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityBase) {
                ((TileEntityBase) te).onRedstoneSignal_internal(getVanillaRedstoneValue(world, x, y, z));
            }
        }
        if (Mods.isLoaded(Mods.ProjectRed)) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile != null && tile instanceof TileMachine) {
                ((TileMachine) tile).onProjectRedBundledInputChanged();
            }
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return (emitsRedstone(world, x, y, z, side) || receivesRedstone(world, x, y, z));
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int side) {
        if (!emitsRedstone(access, x, y, z, side)) {
            return 0;
        }
        TileEntity te = access.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityBase) {
            return ((TileEntityBase) te).requestCurrentRedstoneValue(side);
        }
        return 0;
    }

    public abstract TileEntity createNewTileEntity(World world, int metadata);

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return createNewTileEntity(world, metadata);
    }

    public int getFrontSide(int m) {
        switch (this.rotation) {
            case FOUR:
                return (m & 3) + 2;
            case SIX:
                return (m & 7) % 6;
            case NONE:
            default:
                return 2;
        }
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public int relToAbs(int side, int metadata) {
        int frontSide = getFrontSide(metadata);
        return MiscUtils.getAbsoluteSide(side, frontSide);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getAbsoluteIcon(int side, int metadata) {
        return icon;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getAbsoluteIcon(World world, int x, int y, int z, int side, int metadata) {
        return icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        return this.getAbsoluteIcon(relToAbs(side, metadata), metadata);
    }

    public ForgeDirection getFacingDirection(World world, int x, int y, int z) {
        int m = world.getBlockMetadata(x, y, z);
        return ForgeDirection.getOrientation(getFrontSide(m));
    }

    @Deprecated
    public void setRotateFrontSide(boolean v) {
        this.rotation = Rotation.FOUR;
    }

    public void setRotation(Rotation v) {
        this.rotation = v;
    }

    private void setDefaultRotation(World world, int x, int y, int z) {
        if (!world.isRemote && this.rotation != Rotation.NONE) {
            Block block = world.getBlock(x, y, z - 1);
            Block block1 = world.getBlock(x, y, z + 1);
            Block block2 = world.getBlock(x - 1, y, z);
            Block block3 = world.getBlock(x + 1, y, z);
            int m = world.getBlockMetadata(x, y, z);
            byte b0 = 3;

            if (block.func_149730_j() && !block1.func_149730_j()) {
                b0 = 3;
            } else if (block1.func_149730_j() && !block.func_149730_j()) {
                b0 = 2;
            }

            if (block2.func_149730_j() && !block3.func_149730_j()) {
                b0 = 5;
            } else if (block3.func_149730_j() && !block2.func_149730_j()) {
                b0 = 4;
            }

            if (this.rotation == Rotation.SIX && y > 0 && y < 255) {
                Block block4 = world.getBlock(x, y - 1, z);
                Block block5 = world.getBlock(x, y + 1, z);

                if (block4.func_149730_j() && !block5.func_149730_j()) {
                    b0 = 1;
                } else if (block5.func_149730_j() && !block4.func_149730_j()) {
                    b0 = 0;
                }
            } else {
                b0 -= 2;
            }

            world.setBlockMetadataWithNotify(x, y, z, m | b0, 2);
        }
    }

    private static final int[] ROT_TRANSFORM4 = { 2, 5, 3, 4 };

    private int determineRotation(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        if (this.rotation == Rotation.NONE) {
            return 0;
        }

        if (this.rotation == Rotation.SIX) {
            if (MathHelper.abs((float) entity.posX - (float) x) < 2.0F
                    && MathHelper.abs((float) entity.posZ - (float) z) < 2.0F) {
                double d0 = entity.posY + 1.82D - (double) entity.yOffset;

                if (d0 - (double) y > 2.0D) {
                    return 1;
                }
                if ((double) y - d0 > 0.0D) {
                    return 0;
                }
            }
        }

        int l = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return ROT_TRANSFORM4[l];
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        int rot = determineRotation(world, x, y, z, entity, stack);
        int m = stack.getItemDamage();
        if (this.rotation == Rotation.SIX) {
            world.setBlockMetadataWithNotify(x, y, z, (m & (~7)) | rot, 2);
        } else if (this.rotation == Rotation.FOUR) {
            world.setBlockMetadataWithNotify(x, y, z, (m & (~3)) | (rot - 2), 2);
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        this.setDefaultRotation(world, x, y, z);
    }

    // GUI handling

    public Object getOwner() {
        return parent;
    }

    @Deprecated
    public void setGuiID(int gui) {
        if (gui >= 0) {
            this.gui = gui;
        }
    }

    @Deprecated
    public boolean hasGui() {
        if (guiProvider != null) {
            this.gui = guiProvider.getGuiID();
        }
        return (guiProvider != null && gui >= 0);
    }

    @Deprecated
    public int getGuiID() {
        if (guiProvider != null) {
            this.gui = guiProvider.getGuiID();
        }
        return gui;
    }

    public boolean hasGui(World world, int x, int y, int z, EntityPlayer player, int side) {
        if (guiProvider != null) {
            this.gui = guiProvider.getGuiID();
        }
        return guiProvider != null && gui >= 0;
    }

    @Deprecated
    public int getGuiID(World world, int x, int y, int z, EntityPlayer player, int side) {
        if (guiProvider != null) {
            this.gui = guiProvider.getGuiID();
        }
        return gui;
    }

    public IGuiProvider getGuiProvider(World world, int x, int y, int z, EntityPlayer player, int side) {
        return guiProvider;
    }

    public void setGuiProvider(IGuiProvider provider) {
        this.guiProvider = provider;
        this.gui = guiProvider.getGuiID();
    }

    protected boolean rotate(World world, int x, int y, int z, EntityPlayer player, int side) {
        if (player.isSneaking()) {
            return false;
        }
        int meta = world.getBlockMetadata(x, y, z);
        if (this.rotation == Rotation.FOUR) {
            if (side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal()) {
                world.setBlockMetadataWithNotify(x, y, z, ((meta & 3) + (meta == 0 ? 3 : meta == 3 ? 2 : meta)) % 4, 2);
            } else {
                world.setBlockMetadataWithNotify(
                        x,
                        y,
                        z,
                        getFrontSide(meta) != side ? (side >= 2 ? side - 2 : 2)
                                : (ForgeDirection.getOrientation(side).getOpposite().ordinal() - 2) & 3,
                        2);
            }
            return true;
        } else if (this.rotation == Rotation.SIX) {
            world.setBlockMetadataWithNotify(
                    x,
                    y,
                    z,
                    getFrontSide(meta) != side ? side : ForgeDirection.getOrientation(side).getOpposite().ordinal(),
                    2);
            return true;
        }
        return false;
    }

    protected boolean onToolUsed(World world, int x, int y, int z, EntityPlayer player, int side) {
        return false;
    }

    protected boolean useTool(World world, int x, int y, int z, EntityPlayer player, int side) {
        ItemStack held = player.inventory.getCurrentItem();
        if (held != null && held.getItem() != null
                && Integration.isTool(held, player, x, y, z)
                && this.rotation != null) {
            boolean wrenched = Integration.useTool(held, player, x, y, z);
            return wrenched
                    && (this.onToolUsed(world, x, y, z, player, side) || this.rotate(world, x, y, z, player, side));
        }
        return false;
    }

    protected boolean canUseTool(World world, int x, int y, int z, EntityPlayer player, int side) {
        return this.rotation != Rotation.NONE;
    }

    @SuppressWarnings("deprecation")
    private void tryOpenOldGui(World world, int x, int y, int z, EntityPlayer player, int side) {
        int guiID = this.getGuiID(world, x, y, z, player, side);
        if (guiID >= 0) {
            player.openGui(this.parent, guiID, world, x, y, z);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7,
            float par8, float par9) {
        if (!world.isRemote) {
            if (!this.canUseTool(world, x, y, z, player, side) || !this.useTool(world, x, y, z, player, side)) {
                IGuiProvider guiProvider = getGuiProvider(world, x, y, z, player, side);
                if (guiProvider != null) {
                    if (guiProvider.canOpen(world, x, y, z, player, side)
                            && this.onOpenGui(world, x, y, z, player, side)) {
                        player.openGui(this.parent, guiProvider.getGuiID(), world, x, y, z);
                        return true;
                    } else {
                        this.onOpenGuiDenied(world, x, y, z, player, side);
                    }
                } else {
                    this.tryOpenOldGui(world, x, y, z, player, side);
                }
            }
            return false;
        }
        return true;
    }

    protected boolean onOpenGui(World world, int x, int y, int z, EntityPlayer player, int side) {
        return true;
    }

    protected void onOpenGuiDenied(World world, int x, int y, int z, EntityPlayer player, int side) {}

    // Simple textures

    private String iconName = null;
    private IIcon icon = null;

    public void setIconName(String name) {
        iconName = name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        if (iconName != null) {
            icon = reg.registerIcon(iconName);
        }
    }

    // Block destroy unified handler and whatnot.
    public void onBlockDestroyed(World world, int x, int y, int z, int meta) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null) {
            if (tileEntity instanceof TileEntityBase) {
                ((TileEntityBase) tileEntity).onBlockDestroy();
            }
            if (tileEntity instanceof IInventory) {
                ItemUtils.dropItems(world, x, y, z, (IInventory) tileEntity);
            }
            tileEntity.invalidate();
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
        super.onBlockDestroyedByPlayer(world, x, y, z, meta);
        this.onBlockDestroyed(world, x, y, z, meta);
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        super.onBlockDestroyedByExplosion(world, x, y, z, explosion);
        this.onBlockDestroyed(world, x, y, z, 0);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        this.onBlockDestroyed(world, x, y, z, meta);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return BlockBaseRender.id();
    }

    /* IInformationProvider boilerplate code */

    /*
     * @Override
     * @Optional.Method(modid = Mods.GregTech) public ArrayList<String> getDebugInfo(EntityPlayer aPlayer, int aX, int
     * aY, int aZ, int aLogLevel) { TileEntity te = aPlayer.worldObj.getTileEntity(aX, aY, aZ); ArrayList<String> data =
     * new ArrayList<String>(); if(te instanceof IInformationProvider) { ((IInformationProvider)
     * te).getInformation(aPlayer, ForgeDirection.UNKNOWN, data, true); } return data; }
     */

    @Override
    @Optional.Method(modid = Mods.API.CoFHBlocks)
    public void getBlockInfo(IBlockAccess world, int x, int y, int z, ForgeDirection side, EntityPlayer player,
            List<IChatComponent> info, boolean debug) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IInformationProvider) {
            ArrayList<String> data = new ArrayList<String>();
            ((IInformationProvider) te).getInformation(player, side, data, true);
            for (String s : data) {
                info.add(new ChatComponentText(s));
            }
        }
    }
}

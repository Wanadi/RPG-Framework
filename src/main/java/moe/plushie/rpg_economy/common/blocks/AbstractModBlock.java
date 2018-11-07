package moe.plushie.rpg_economy.common.blocks;

import moe.plushie.rpg_economy.RPG_Economy;
import moe.plushie.rpg_economy.client.model.ICustomModel;
import moe.plushie.rpg_economy.common.init.ModBlocks;
import moe.plushie.rpg_economy.common.items.block.ModItemBlock;
import moe.plushie.rpg_economy.common.lib.LibModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class AbstractModBlock extends Block implements ICustomItemBlock, ICustomModel {

    public AbstractModBlock(String name) {
        super(Material.IRON);
        setCreativeTab(RPG_Economy.getCreativeTabRPGEconomy());
        setHardness(3.0F);
        setSoundType(SoundType.METAL);
        setTranslationKey(name);
        ModBlocks.BLOCK_LIST.add(this);
    }

    public AbstractModBlock(String name, Material material, SoundType soundType, boolean addCreativeTab) {
        super(material);
        if (addCreativeTab) {
            setCreativeTab(RPG_Economy.getCreativeTabRPGEconomy());
        }
        setHardness(3.0F);
        setSoundType(soundType);
        setTranslationKey(name);
        ModBlocks.BLOCK_LIST.add(this);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    protected static boolean getBitBool(int value, int index) {
        return getBit(value, index) == 1;
    }
    
    protected static int getBit(int value, int index) {
        return (value >> index) & 1;
    }

    protected static int setBit(int value, int index, boolean on) {
        if (on) {
            return value | (1 << index);
        } else {
            return value & ~(1 << index);
        }
    }

    @Override
    public Block setTranslationKey(String key) {
        super.setTranslationKey(key);
        setRegistryName(new ResourceLocation(LibModInfo.ID, "tile." + key));
        return this;
    }
    
    @Override
    public void registerItemBlock(IForgeRegistry<Item> registry) {
        registry.register(new ModItemBlock(this));
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "normal"));
    }
}

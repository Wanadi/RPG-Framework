package moe.plushie.rpg_economy.common.creativetab;

import moe.plushie.rpg_economy.common.init.ModBlocks;
import moe.plushie.rpg_economy.common.lib.LibModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabRPGEconomy extends CreativeTabs {

    public CreativeTabRPGEconomy() {
        super(LibModInfo.ID);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.MAIL_BOX);
    }
}

package moe.plushie.rpgeconomy.api.loot;

import java.util.ArrayList;
import java.util.Random;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ILootTable {

    public IIdentifier getIdentifier();

    public String getName();

    public String getCategory();

    public ArrayList<ILootTablePool> getLootPools();

    public NonNullList<ItemStack> getLoot(Random random);
}

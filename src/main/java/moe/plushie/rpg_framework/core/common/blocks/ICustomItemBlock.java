package moe.plushie.rpg_framework.core.common.blocks;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public interface ICustomItemBlock {

    public void registerItemBlock(IForgeRegistry<Item> registry);

}

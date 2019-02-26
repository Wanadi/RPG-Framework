package moe.plushie.rpgeconomy.core.common.command;

import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class ModCommand extends CommandBase {

    private final ModCommand parent;
    private final String name;

    public ModCommand(ModCommand parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public int getParentCount() {
        if (parent != null) {
            return parent.getParentCount() + 1;
        }
        return 0;
    }

    @Override
    public String getUsage(ICommandSender commandSender) {
        return "commands." + LibModInfo.ID + ":" + getName() + ".usage";
    }

    @Override
    public String getName() {
        return name;
    }

    protected String[] getPlayers(MinecraftServer server) {
        return server.getOnlinePlayerNames();
    }
}

package de.raidcraft.npcs;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import lombok.Data;
import org.bukkit.command.CommandSender;

@Data
public class NpcCommands {

    private final RCNPCsPlugin plugin;

    @Command(
            aliases = "rcnpc",
            desc = "Verwaltet Custom NPCs und dient als Brücke zwischen Citizens."
    )
    @NestedCommand(SubNpcCommands.class)
    @CommandPermissions("rcnpcs.admin")
    public void baseCommand(CommandContext args, CommandSender sender) {

    }

    public class SubNpcCommands {

    }
}

package net.ragnaroknetwork.vouchers.command.commands;

import net.ragnaroknetwork.vouchers.RagnarokVouchers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends Command {
    private final RagnarokVouchers plugin;

    public ReloadCommand(RagnarokVouchers plugin) {
        super("reload");
        setDescription("Reloads this plugin");
        setUsage("/rvouchers reload");
        setPermission("rvouchers.reload");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        plugin.reloadPlugin();
        return true;
    }
}

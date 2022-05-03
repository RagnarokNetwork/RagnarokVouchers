package net.ragnaroknetwork.vouchers.command.commands;

import net.ragnaroknetwork.vouchers.RagnarokVouchers;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends Command {
    private final RagnarokVouchers plugin;

    public ReloadCommand(RagnarokVouchers plugin) {
        super("reload");
        setDescription("Reloads this plugin");
        setUsage("/rvouchers reload");
        setPermission("rvouchers.reload");
        setPermissionMessage(plugin.getLang().noPermission().toString());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        plugin.reloadPlugin();
        sender.sendMessage(ChatColor.GREEN + "Vouchers Reloaded Successfully!");
        return true;
    }
}

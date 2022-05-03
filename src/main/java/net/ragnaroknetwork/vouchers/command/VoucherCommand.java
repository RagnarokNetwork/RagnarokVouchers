package net.ragnaroknetwork.vouchers.command;

import net.ragnaroknetwork.vouchers.RagnarokVouchers;
import net.ragnaroknetwork.vouchers.command.commands.GiveCommand;
import net.ragnaroknetwork.vouchers.command.commands.ReloadCommand;
import net.ragnaroknetwork.vouchers.command.commands.UseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class VoucherCommand implements CommandExecutor {

    private final Map<String, Command> commands = new HashMap<>();

    public VoucherCommand(RagnarokVouchers plugin) {
        plugin.getCommand("rvouchers").setTabCompleter(
                (CommandSender sender, Command command, String alias, String[] args) -> {
                    if (args.length == 0) return new ArrayList<>(commands.keySet());
                    if (args.length > 1) {
                        if (args.length > 2) return new ArrayList<>();
                        if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("use")) {
                            return plugin.getPluginConfig().vouchers().keySet()
                                    .stream().filter(it -> it.startsWith(args[1].toLowerCase()))
                                    .collect(Collectors.toList());
                        }
                        return new ArrayList<>();
                    }
                    return commands.keySet().stream()
                            .filter(it -> it.startsWith(args[0].toLowerCase()))
                            .collect(Collectors.toList());
                });

        addCommand(new ReloadCommand(plugin));
        addCommand(new GiveCommand(plugin));
        addCommand(new UseCommand(plugin));
    }

    private void addCommand(Command command) {
        if (command.getName() == null)
            throw new IllegalArgumentException("Command name cannot be null : " + command.getClass().getName());
        commands.put(command.getName(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        Command subCommand = commands.get(args[0]);

        if (subCommand == null) {
            sender.sendMessage(ChatColor.RED + "No command found for " + args[0]);
            sendHelp(sender);
            return true;
        }

        if (subCommand.testPermission(sender))
            subCommand.execute(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    private void sendHelp(CommandSender sender) {
        String commands = "Solar Credits Commands : \n" +
                this.commands.values().stream()
                        .filter(it -> it.testPermission(sender))
                        .map(it -> ChatColor.BOLD + it.getName() +
                                ChatColor.RESET + " : " + it.getDescription())
                        .collect(Collectors.joining("\n")) +
                "\n/vouchers <command>";
        sender.sendMessage(commands.split("\n"));
    }
}

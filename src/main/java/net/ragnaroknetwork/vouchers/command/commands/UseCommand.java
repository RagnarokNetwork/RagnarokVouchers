package net.ragnaroknetwork.vouchers.command.commands;

import net.ragnaroknetwork.vouchers.RItemStack;
import net.ragnaroknetwork.vouchers.RagnarokVouchers;
import net.ragnaroknetwork.vouchers.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class UseCommand extends Command {
    private final RagnarokVouchers plugin;
    private final Random random;

    public UseCommand(RagnarokVouchers plugin) {
        super("use");
        setDescription("Uses a voucher");
        setUsage("/rvoucher use");
        setPermission("rvoucher.use");
        this.plugin = plugin;
        this.random = new Random();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInHand();

        if(item == null) {
            player.sendMessage(ChatColor.RED + "You have to hold a Voucher in your main hand for this command to work!");
            return true;
        }

        RItemStack rItem = RItemStack.of(item);

        if (!rItem.isVoucher()) {
            player.sendMessage(ChatColor.RED + "You have to hold a Voucher in your main hand for this command to work!");
            return true;
        }

        String voucherId = rItem.getVoucherId();
        Config.Voucher voucher = plugin.getPluginConfig().vouchers().get(voucherId);
        if (!player.hasPermission(voucher.permission())) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this voucher.");
            return true;
        }

        Long coolDownExpiry = plugin.getCoolDowns().computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>())
                .getOrDefault(voucherId, System.currentTimeMillis());
        if (coolDownExpiry > System.currentTimeMillis()) {
            player.sendMessage(ChatColor.RED + "You have to wait for " + getFormatted(coolDownExpiry - System.currentTimeMillis()));
            return true;
        }

        List<String> permanentCommands = new ArrayList<>(voucher.permanentCommands());
        List<String> randomCommands = voucher.randomCommands();

        if (!permanentCommands.get(0).equals("-")) {
            if (!randomCommands.get(0).equals("-"))
                permanentCommands.add(randomCommands.get(random.nextInt(randomCommands.size())));
            dispatchCommands(permanentCommands, player, (success) -> {
                if (!success) {
                    player.sendMessage(ChatColor.RED + "Something went wrong, report to server admin! (Invalid Command)");
                    throw new CommandException("Invalid command in key " + voucherId);
                }

                plugin.getCoolDowns().computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>())
                        .put(voucherId, System.currentTimeMillis() + voucher.cooldown() * 1000L);

                int amount = item.getAmount();
                if (amount == 1)
                    player.getInventory().setItemInHand(new ItemStack(Material.AIR));
                else
                    player.getInventory().getItemInHand().setAmount(amount - 1);
            });
        }

        return true;
    }

    private void dispatchCommands(List<String> commands, Player player, Consumer<Boolean> success) {
        Server server = plugin.getServer();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (String command : commands) {
                        server.dispatchCommand(server.getConsoleSender(),
                                command.replaceAll("@p", player.getName())
                                        .replaceFirst("^/", ""));
                    }

                    success.accept(true);
                } catch (CommandException e) {
                    success.accept(false);
                }
            }
        }.runTask(plugin);
    }

    public String getFormatted(long milliseconds) {
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;

        StringBuilder builder = new StringBuilder();
        if (milliseconds < 1000) {
            return milliseconds + " ms";
        } else {
            if (hours > 0)
                builder.append(hours).append(" H ");
            if (minutes > 0)
                builder.append(minutes).append(" M ");
            if (seconds > 0)
                builder.append(seconds).append(" S ");
        }

        return builder.toString();
    }
}

package net.ragnaroknetwork.vouchers.command.commands;

import net.ragnaroknetwork.vouchers.ChatMessage;
import net.ragnaroknetwork.vouchers.RItemStack;
import net.ragnaroknetwork.vouchers.RagnarokVouchers;
import net.ragnaroknetwork.vouchers.Utils;
import net.ragnaroknetwork.vouchers.config.Config;
import net.ragnaroknetwork.vouchers.config.MessageConfig;
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
        setUsage("/rvouchers use");
        setPermission("rvouchers.use");
        setPermissionMessage(plugin.getLang().noPermission().toString());
        this.plugin = plugin;
        this.random = new Random();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        MessageConfig.UseConfig config = plugin.getLang().use();

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInHand();
        RItemStack rItem = RItemStack.of(item);

        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(config.voucherNotInMainHand().toString());
            return true;
        }

        if (!rItem.isVoucher()) {
            player.sendMessage(config.voucherNotInMainHand().toString());
            return true;
        }

        String voucherId = rItem.getVoucherId();

        if (args.length == 0 || !args[0].equals(voucherId)) {
            sender.sendMessage(config.voucherNotSpecified().toString());
            return true;
        }

        Config.Voucher voucher = plugin.getPluginConfig().vouchers().get(voucherId);

        String blacklistPermission = voucher.blacklistPermission();
        if (!player.isOp() &&
                !blacklistPermission.equalsIgnoreCase("none") &&
                player.hasPermission(blacklistPermission)) {
            player.sendMessage(config.hasBlacklistPermission().toString());
            return true;
        }

        String voucherPermission = voucher.permission();
        if (!voucherPermission.equals("none")
                && !player.hasPermission(voucherPermission)) {
            player.sendMessage(config.noPermissionToClaimVoucher().toString());
            return true;
        }

        if (!player.hasPermission("rvouchers.bypass-cooldown")) {
            Long coolDownExpiry = plugin.getCoolDowns().computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>())
                    .getOrDefault(voucherId, System.currentTimeMillis());
            if (coolDownExpiry > System.currentTimeMillis()) {
                player.sendMessage(config.playerOnCooldown().toString()
                        .replace("{time}", Utils.getFormatted(coolDownExpiry - System.currentTimeMillis()))
                );
                return true;
            }
        }

        List<String> commands = new ArrayList<>(voucher.permanentCommands());
        List<String> randomCommands = voucher.randomCommands();

        if (!randomCommands.isEmpty())
            commands.add(randomCommands.get(random.nextInt(randomCommands.size())));

        dispatchCommands(commands, player, (success) -> {
            if (!success) {
                player.sendMessage(config.invalidCommand().toString());
                return;
            }

            plugin.getCoolDowns().computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>())
                    .put(voucherId, System.currentTimeMillis() + voucher.cooldown() * 1000L);

            int amount = item.getAmount();
            if (amount == 1)
                player.getInventory().setItemInHand(new ItemStack(Material.AIR));
            else
                player.getInventory().getItemInHand().setAmount(amount - 1);
            player.updateInventory();
            player.sendMessage(voucher.messages().stream()
                    .map(ChatMessage::toString)
                    .toArray(String[]::new));
        });

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
                    e.printStackTrace();
                }
            }
        }.runTask(plugin);
    }
}

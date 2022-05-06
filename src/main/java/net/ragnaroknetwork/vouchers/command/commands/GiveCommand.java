package net.ragnaroknetwork.vouchers.command.commands;

import net.ragnaroknetwork.vouchers.RItemStack;
import net.ragnaroknetwork.vouchers.RagnarokVouchers;
import net.ragnaroknetwork.vouchers.config.Config;
import net.ragnaroknetwork.vouchers.config.MessageConfig;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GiveCommand extends Command {
    private final RagnarokVouchers plugin;

    public GiveCommand(RagnarokVouchers plugin) {
        super("give");
        setDescription("Gives a voucher to a player");
        setUsage("/rvoucher give <voucher> [amount] [name]");
        setPermission("rvoucher.give");
        setPermissionMessage(plugin.getLang().noPermission().toString());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        MessageConfig.GiveConfig config = plugin.getLang().give();

        int len = args.length;

        if (len == 0) {
            sender.sendMessage(config.noVoucherId().toString()
                    .replace("{vouchers}", String.join(", ", plugin.getPluginConfig().vouchers().keySet())));
            return true;
        }

        String voucherId = args[0];
        int amount = len > 1 ? parseInt(args[1]) : 1;
        String target = len > 2 ? args[2] : sender.getName();

        if (amount == -1) {
            sender.sendMessage(config.invalidAmount().toString());
            return true;
        }

        Player player = sender.getServer().getPlayer(target);

        if (player == null) {
            sender.sendMessage(config.playerNotFound().toString()
                    .replace("{player}", target));
            return true;
        }

        List<ItemStack> vouchers = getVouchers(voucherId, amount);

        if (vouchers == null) {
            sender.sendMessage(config.voucherNotFound().toString());
            return true;
        }

        /*int firstEmpty = player.getInventory().firstEmpty();

        if (firstEmpty == -1) {
            sender.sendMessage(config.noEmptySlot().toString()
                    .replace("{player}", target));
            return true;
        }*/

        vouchers.forEach(it -> {
            if (player.getInventory().firstEmpty() != -1)
                player.getInventory().addItem(it);
            else {
                World world = player.getWorld();
                world.dropItem(player.getLocation(), it);
            }
        });

        player.sendMessage(config.rewardGiven().toString()
                .replace("{voucher}", vouchers.get(0).getItemMeta().getDisplayName()));

        return true;
    }

    private List<ItemStack> getVouchers(String id, int amount) {
        Config.Voucher voucher = plugin.getPluginConfig().vouchers().get(id);
        if (voucher == null)
            return null;

        ItemStack item = new ItemStack(voucher.material(), 64);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', voucher.displayName()));
        meta.setLore(voucher.lore().stream()
                .map(it -> ChatColor.translateAlternateColorCodes('&', it))
                .collect(Collectors.toList()));

        if (voucher.enchantmentGlow()) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.LUCK, 1, false);
        }
        item.setItemMeta(meta);

        List<ItemStack> vouchers = new ArrayList<>(Collections.nCopies(amount / 64, RItemStack.of(item).asVoucher(id)));

        int lastStackAmount = amount % 64;
        if (lastStackAmount > 0) {
            ItemStack lastStack = RItemStack.of(item).asVoucher(id);
            lastStack.setAmount(lastStackAmount);
            vouchers.add(lastStack);
        }

        return vouchers;
    }

    private int parseInt(String num) {
        if (num == null)
            return -1;

        try {
            int d = Integer.parseInt(num);
            return d < 0 ? 1 : d;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

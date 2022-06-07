package net.ragnaroknetwork.vouchers.event;

import net.ragnaroknetwork.vouchers.RItemStack;
import net.ragnaroknetwork.vouchers.RagnarokVouchers;
import net.ragnaroknetwork.vouchers.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class VoucherUseEvent implements Listener {
    private final RagnarokVouchers plugin;

    public VoucherUseEvent(RagnarokVouchers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerUseVoucher(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();

            ItemStack itemInHand = event.getItem();

            if (itemInHand == null || itemInHand.getType() == Material.AIR)
                return;

            RItemStack item = RItemStack.of(itemInHand);
            if (item.isVoucher()) {
                if (!player.hasPermission("rvouchers.bypass-cooldown")) {
                    Long coolDownExpiry = plugin.getCoolDowns().computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>())
                            .getOrDefault(item.getVoucherId(), System.currentTimeMillis());
                    if (coolDownExpiry > System.currentTimeMillis()) {
                        player.sendMessage(plugin.getLang().use().playerOnCooldown().toString()
                                .replace("{time}", Utils.getFormatted(coolDownExpiry - System.currentTimeMillis()))
                        );
                        event.setCancelled(true);
                        return;
                    }
                }


                player.performCommand("rvouchers use " + item.getVoucherId());
            }
        }
    }
}

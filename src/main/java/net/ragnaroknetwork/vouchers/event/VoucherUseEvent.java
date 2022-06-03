package net.ragnaroknetwork.vouchers.event;

import net.ragnaroknetwork.vouchers.RItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class VoucherUseEvent implements Listener {

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
                player.performCommand("rvouchers use " + item.getVoucherId());
            }
        }
    }
}

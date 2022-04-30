package net.ragnaroknetwork.vouchers.event;

import net.ragnaroknetwork.vouchers.RItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class VoucherUseEvent implements Listener {

    @EventHandler
    public void onPlayerUseVoucher(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();

            if (RItemStack.of(player.getInventory().getItemInHand()).isVoucher()) {
                player.performCommand("/rvouchers use");
            }
        }
    }
}

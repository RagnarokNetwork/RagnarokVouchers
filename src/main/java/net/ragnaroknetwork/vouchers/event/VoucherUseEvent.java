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
        System.out.println("Player Interact Event triggered");
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            System.out.println("Right Click Action took place");
            Player player = event.getPlayer();

            ItemStack itemInHand = event.getItem();

            if (itemInHand == null || itemInHand.getType() == Material.AIR)
                return;
            System.out.println("Item in hand is not null or air");

            RItemStack item = RItemStack.of(itemInHand);
            if (item.isVoucher()) {
                System.out.println("Item in hand is voucher");
                player.performCommand("rvouchers use " + item.getVoucherId());
                System.out.println("voucher used");
            }
        }
    }
}

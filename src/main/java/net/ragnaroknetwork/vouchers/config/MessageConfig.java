package net.ragnaroknetwork.vouchers.config;

import net.ragnaroknetwork.vouchers.ChatMessage;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

public interface MessageConfig {

    @Order(1)
    @ConfKey("command-not-found")
    @ConfDefault.DefaultString("&cNo command found for {cmd}")
    ChatMessage commandNotFound();

    @Order(2)
    @ConfKey("no-permission")
    @ConfDefault.DefaultString("&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.")
    ChatMessage noPermission();

    @Order(3)
    @ConfKey("use-cmd")
    @SubSection UseConfig use();

    @Order(4)
    @ConfKey("give-cmd")
    @SubSection GiveConfig give();

    interface UseConfig {
        @Order(1)
        @ConfKey("voucher-not-in-main-hand")
        @ConfDefault.DefaultString("&cYou have to hold a Voucher in your hand!")
        ChatMessage voucherNotInMainHand();

        @Order(2)
        @ConfKey("no-permission-to-claim-voucher")
        @ConfDefault.DefaultString("&cYou do not have permission to use this voucher!")
        ChatMessage noPermissionToClaimVoucher();

        @Order(3)
        @ConfKey("player-on-cooldown")
        @ConfDefault.DefaultString("&6You have to wait for {time} to claim this voucher!")
        ChatMessage playerOnCooldown();

        @Order(4)
        @ConfKey("invalid-command")
        @ConfComments("If the command in the config is invalid")
        @ConfDefault.DefaultString("&cSomething went wrong, report to server admin! (Invalid Command)")
        ChatMessage invalidCommand();
    }

    interface GiveConfig {
        @Order(1)
        @ConfKey("no-voucher-id-specified")
        @ConfDefault.DefaultString("&cSpecify the voucher id! These are the vouchers : {vouchers}")
        ChatMessage noVoucherId();

        @Order(2)
        @ConfKey("invalid-amount")
        @ConfDefault.DefaultString("&cThe amount should be a number (positive)!")
        ChatMessage invalidAmount();

        @Order(3)
        @ConfKey("voucher-not-found")
        @ConfDefault.DefaultString("&cVoucher with id {id} doesn't exist!")
        ChatMessage voucherNotFound();

        @Order(4)
        @ConfKey("player-not-found")
        @ConfDefault.DefaultString("&cNo Player with name {player} found!")
        ChatMessage playerNotFound();

        @Order(5)
        @ConfKey("no-empty-slot")
        @ConfDefault.DefaultString("&c{player} doesn't have a empty slot!")
        ChatMessage noEmptySlot();

        @Order(6)
        @ConfKey("reward-claimed")
        @ConfDefault.DefaultString("&aYou claimed {voucher}!")
        ChatMessage rewardClaimed();
    }
}

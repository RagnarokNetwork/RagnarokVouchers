package net.ragnaroknetwork.vouchers.config;

import org.bukkit.Material;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface Config {

    static Map<String, @SubSection Voucher> defaultVouchers(@SubSection Voucher defaultConfig) {
        return Collections.singletonMap("default", defaultConfig);
    }

    @ConfKey("vouchers")
    @ConfDefault.DefaultObject("defaultVouchers")
    Map<String, @SubSection Voucher> vouchers();

    interface Voucher {

        @Order(1)
        @ConfKey("material")
        @ConfDefault.DefaultString("name_tag")
        Material material();

        @Order(2)
        @ConfKey("display-name")
        @ConfDefault.DefaultString("&aSword")
        String displayName();

        @Order(3)
        @ConfKey("lore")
        @ConfDefault.DefaultStrings("A really op sword")
        List<String> lore();

        @Order(4)
        @ConfKey("enchantment-glow")
        @ConfDefault.DefaultBoolean(true)
        boolean enchantmentGlow();

        @Order(5)
        @ConfKey("cooldown")
        @ConfDefault.DefaultInteger(60)
        int cooldown();

        @Order(6)
        @ConfKey("permission")
        @ConfDefault.DefaultString("*")
        String permission();

        @Order(7)
        @ConfKey("perm-commands")
        @ConfDefault.DefaultStrings("-")
        @ConfComments({"The commands which will executed after using the voucher.", "Use '-' if you want none"})
        List<String> permanentCommands();

        @Order(8)
        @ConfKey("rand-commands")
        @ConfDefault.DefaultStrings({"give @p diamond 5", "give @p diamond 10", "give @p diamond 25"})
        @ConfComments({"A random command from these will be executed after using the voucher.", "Use '-' if you want none"})
        List<String> randomCommands();
    }
}

package net.ragnaroknetwork.vouchers;

import net.ragnaroknetwork.vouchers.command.VoucherCommand;
import net.ragnaroknetwork.vouchers.config.Config;
import net.ragnaroknetwork.vouchers.config.ConfigManager;
import net.ragnaroknetwork.vouchers.event.VoucherUseEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RagnarokVouchers extends JavaPlugin {
    private ConfigManager<Config> configManager;
    private Map<UUID, Map<String, Long>> coolDowns;

    @Override
    public void onEnable() {
        coolDowns = new HashMap<>();

        // Config
        configManager = ConfigManager.create(getLogger(), getDataFolder().toPath(), "config.yml", Config.class);
        reloadPlugin();

        // Commands
        getCommand("rvouchers").setExecutor(new VoucherCommand(this));

        // Events
        getServer().getPluginManager().registerEvents(new VoucherUseEvent(), this);
    }

    @Override
    public void onDisable() {
    }

    public Config getPluginConfig() {
        return configManager.getConfigData();
    }

    public void reloadPlugin() {
        configManager.reloadConfig();
    }

    public Map<UUID, Map<String, Long>> getCoolDowns() {
        return coolDowns;
    }
}

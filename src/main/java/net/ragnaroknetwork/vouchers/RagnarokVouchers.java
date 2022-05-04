package net.ragnaroknetwork.vouchers;

import net.ragnaroknetwork.vouchers.command.VoucherCommand;
import net.ragnaroknetwork.vouchers.config.Config;
import net.ragnaroknetwork.vouchers.config.ConfigManager;
import net.ragnaroknetwork.vouchers.config.MessageConfig;
import net.ragnaroknetwork.vouchers.event.VoucherUseEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RagnarokVouchers extends JavaPlugin {
    private ConfigManager<Config> configManager;
    private ConfigManager<MessageConfig> langManager;
    private Map<UUID, Map<String, Long>> coolDowns;

    @Override
    public void onEnable() {
        coolDowns = new HashMap<>();

        // Config
        configManager = ConfigManager.create(getLogger(), getDataFolder().toPath(), "config.yml", Config.class);
        langManager = ConfigManager.create(getLogger(), getDataFolder().toPath(), "lang.yml", MessageConfig.class);
        backupConfig();
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

    public MessageConfig getLang() {
        return langManager.getConfigData();
    }

    public void reloadPlugin() {
        configManager.reloadConfig();
        langManager.reloadConfig();
    }

    public Map<UUID, Map<String, Long>> getCoolDowns() {
        return coolDowns;
    }

    private void backupConfig() {
        Path dataFolder = getDataFolder().toPath();
        Path file = dataFolder.resolve("config");
        Path backup = dataFolder.resolve("config.backup.yml");

        try {
            Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);
            getLogger().info("config backup Complete!");
        } catch (IOException e) {
            throw new UncheckedIOException("Config Backup couldn't be created!!", e);
        }
    }
}

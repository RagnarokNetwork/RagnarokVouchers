package net.ragnaroknetwork.vouchers.config;

import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.CommentMode;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.helper.ConfigurationHelper;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConfigManager<C> {
    private final Logger logger;

    private final ConfigurationHelper<C> configHelper;
    private final Path filePath;
    private C configData;

    private ConfigManager(Logger logger, ConfigurationHelper<C> configHelper, Path filePath) {
        this.logger = logger;
        this.configHelper = configHelper;
        this.filePath = filePath;
    }

    public static <C> ConfigManager<C> create(Logger logger, Path configFolder, String fileName, Class<C> configClass) {
        SnakeYamlOptions yamlOptions = new SnakeYamlOptions.Builder()
                .commentMode(CommentMode.alternativeWriter()) // Enables writing YAML comments
                .build();
        ConfigurationOptions options = new ConfigurationOptions.Builder()
                .addSerialiser(new ChatMessageSerializer())
                .sorter(new AnnotationBasedSorter())
                .build();
        ConfigurationFactory<C> configFactory = SnakeYamlConfigurationFactory.create(
                configClass,
                options,
                yamlOptions);
        return new ConfigManager<>(logger, new ConfigurationHelper<>(configFolder, fileName, configFactory), configFolder.resolve(fileName));
    }

    public void reloadConfig() {
        try {
            configData = configHelper.reloadConfigData();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (ConfigFormatSyntaxException ex) {
            configData = configHelper.getFactory().loadDefaults();
            logger.log(Level.SEVERE, "The yaml syntax in your configuration is invalid. "
                    + "Check your YAML syntax with a tool such as https://yaml-online-parser.appspot.com/", ex);
        } catch (InvalidConfigException ex) {
            configData = configHelper.getFactory().loadDefaults();
            logger.log(Level.SEVERE, "One of the values in your configuration is not valid. "
                    + "Check to make sure you have specified the right data types.", ex);
        }
    }

    public void saveConfig(C configData) {
        try (final FileOutputStream output = new FileOutputStream(filePath.toFile())) {
            configHelper.getFactory().write(configData, output);
        } catch (IOException e) {
            throw new UncheckedIOException("Something went wrong when writing Config", e);
        }
    }

    public C getConfigData() {
        C configData = this.configData;
        if (configData == null) {
            throw new IllegalStateException("Configuration has not been loaded yet");
        }
        return configData;
    }
}

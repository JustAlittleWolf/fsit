package dev.rvbsm.fsit.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import dev.rvbsm.fsit.FSitMod;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

public abstract class FSitConfigManager {

	private static final Path configDir = FabricLoader.getInstance().getConfigDir();
	protected static final Path configPath = FSitConfigManager.getConfigPath();
	protected static final FileConfig config = FileConfig.of(configPath);

	private static @NotNull Path getConfigPath() {
		return configDir.resolve(FSitMod.getModId() + ".toml");
	}

	public static void load() {
		config.load();
		FSitConfig.load();
	}

	public static void save() {
		FSitConfig.save();
		config.save();
	}
}

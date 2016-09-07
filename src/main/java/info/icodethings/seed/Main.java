package info.icodethings.seed;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin{

    public static YamlConfiguration biomes;

    @Override
    public void onEnable() {
        super.onEnable();

        /* load biomes file */
        File configFile = new File(getDataFolder(), "biomes.yml");
        if (!configFile.exists()) {
            saveResource("biomes.yml", false);
        }
        biomes = YamlConfiguration.loadConfiguration(configFile);


        this.getCommand("generate").setExecutor(new GenerationCommand());
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new SeedGenerator();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}

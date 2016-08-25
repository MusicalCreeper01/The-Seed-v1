package info.icodethings.seed;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
    {
        return new SeedGenerator();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}

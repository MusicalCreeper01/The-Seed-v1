package info.icodethings.seed;

import jLibNoise.noise.NoiseQuality;
import javafx.util.Pair;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

import jLibNoise.noise.module.Perlin;
import jLibNoise.noise.utils.*;

public class SeedGenerator extends ChunkGenerator{

    public ChunkData chunkData;

    /*
    * Low frequency or there is too much random noise 0.05 - 0.1 is a good range
    *
    * */

    @Override
    public ChunkData generateChunkData(World world, Random random, int xChunk, int zChunk, BiomeGrid biome){
        chunkData = this.createChunkData(world);

        Perlin myModule = new Perlin();

        myModule.setSeed((int)world.getSeed());

        myModule.setPersistence(0.1); // detail
        myModule.setOctaveCount(4); // detail passes
        myModule.setFrequency(0.05);
        myModule.setLacunarity(0.01);

        myModule.setNoiseQuality(NoiseQuality.QUALITY_BEST);

        int heightscale = 5; //world.getMaxHeight()
        int base = 10; //blocks before noise is applied

        int xOffset = 16*xChunk;
        int yOffset = 16*zChunk;

        int x;
        for(x = 0; x < 16; ++x){
            int z;
            for(z = 0; z < 16; ++z){
                double p = (myModule.getValue((xOffset+x)- 0.98, (yOffset+z)- 0.98, .1) + 1) / 2.0 * heightscale;
                p += base;

                int y;
                for(y = 0; y < p; ++y){
                   setBlock(x, y, z, Material.COBBLESTONE);
                }
            }
        }

        return chunkData;
    }

    public boolean isType(int x, int y, int z, Material material) {
        return chunkData.getType(x, y, z).equals(material);
    }

    public Material getBlock(int x, int y, int z) {
        return chunkData.getType(x, y, z);
    }

    public void setBlock(int x, int y, int z, Material material) {
        chunkData.setBlock(x, y, z, material);
    }


}





































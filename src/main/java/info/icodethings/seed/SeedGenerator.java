package info.icodethings.seed;

import javafx.util.Pair;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class SeedGenerator extends ChunkGenerator{

    public ChunkData chunkData;

    //Store layer materials
    public Material[] layers = new Material[]{Material.BEDROCK, Material.STONE, Material.COBBLESTONE, Material.DIRT, Material.GRASS};

    //Array to hold layer height ranges
    public Pair<Integer, Integer>[] heights = new Pair[]{new Pair(0,0), new Pair(1,200), new Pair(201,220), new Pair(221, 230), new Pair(231, 231)};



    @Override
    public ChunkData generateChunkData(World world, Random random, int xChunk, int zChunk, BiomeGrid biome){
        chunkData = this.createChunkData(world);

        int x;
        int z;
        int y;
        for(x = 0; x < 16; ++x){
            for(z = 0; z < 16; ++z){
               for(y = 0; y < 256; ++y){
                   setBlock(x, y, z, getMaterialForHeight(y));
               }
            }
        }

        return chunkData;
    }

    public Material getMaterialForHeight(int y){
        for(int i = 0; i < heights.length; ++i){
            Pair<Integer, Integer> h = heights[i];

            if(y >= h.getKey() && y <= h.getValue())
                return layers[i];
        }
        return Material.AIR;
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

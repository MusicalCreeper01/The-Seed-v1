package info.icodethings.seed;

import jLibNoise.noise.NoiseQuality;
import javafx.util.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import jLibNoise.noise.module.Perlin;
import jLibNoise.noise.utils.*;

public class SeedGenerator extends ChunkGenerator{

    class BlockLayer {

        int start;
        int end;
        Material[] materials = new Material[]{Material.AIR};
        Random r = new Random();

        public BlockLayer(int s, int e, Material[] m){
            start = s;
            end = e;
            materials = m;
        }

        public Boolean blockInLayer(int y, int maxheight){
            return y <= endBlock(maxheight) && y >= startBlock(maxheight);
        }

        public int startBlock(int height){
            if(start < 0)
                return  height + start;
            else
                return start;
        }

        public int endBlock(int height){
            if(end == -1)
                return height;
            if (end < -1)
                return height+end;
            else
                return end;
        }

        public Material materialForBlock(int y, int maxheight){
            return materials[r.nextInt(materials.length)];
        }
    }

    public ChunkData chunkData;

    /*
    * Low frequency or there is too much random noise 0.05 - 0.1 is a good range
    *
    * A multiplier that determines how quickly the frequency increases for each successive octave in a Perlin-noise function.
    * The frequency of each successive octave is equal to the product of the previous octave's frequency and the lacunarity value.
    * A similar property to lacunarity is persistence, which modifies the octaves' amplitudes in a similar way.
    *
    * */

    int heightscale;
    int base;

    boolean logged = false;

    @Override
    public ChunkData generateChunkData(World world, Random random, int xChunk, int zChunk, BiomeGrid biome){
        //if(biome.getBiome(16*xChunk, 16*zChunk) == Biome.DEEP_OCEAN)

        chunkData = this.createChunkData(world);

        Perlin myModule = new Perlin();
        myModule.setSeed((int)world.getSeed());

        int xOffset = 16*xChunk;
        int yOffset = 16*zChunk;

        int x;
        for(x = 0; x < 16; ++x){
            int z;
            for(z = 0; z < 16; ++z){
                Biome b = world.getBiome(xOffset+x, yOffset+z);
                setPerlinSettings(myModule, b);
//                setPerlinSettings(myModule, world.getBiome((xOffset+x), (yOffset+z)));

                Double v = ((myModule.getValue((xOffset+x)- 0.98, (yOffset+z)- 0.98, .1) + 1) / 2.0 * heightscale);
                int p = v.intValue();
                p += base;

                int y;
                for(y = 0; y < p; ++y){
                    Material m = Material.CAKE;
                    m = getLayerBlock(y, b, p);

                   setBlock(x, y, z, m);
                }
                logged = true;
            }
        }

        return chunkData;
    }

    public void setPerlinSettings(Perlin myModule, Biome biome){

        ConfigurationSection biomeConfig = getSectionForBiome(biome);

        ConfigurationSection generationOptions = Main.biomes.getConfigurationSection("options");

        myModule.setPersistence(biomeConfig.contains("persistence") ? biomeConfig.getDouble("persistence") : 0.1);
        myModule.setOctaveCount(biomeConfig.contains("octaves") ? biomeConfig.getInt("octaves") : 4);
        myModule.setFrequency(biomeConfig.contains("frequency") ? biomeConfig.getDouble("frequency") : 0.05);
        myModule.setLacunarity(biomeConfig.contains("lacunarity") ? biomeConfig.getDouble("lacunarity") : 0.01);

        myModule.setNoiseQuality(NoiseQuality.QUALITY_BEST);

        heightscale = biomeConfig.contains("stretching") ? biomeConfig.getInt("stretching") : 5; //world.getMaxHeight()
        base = generationOptions != null ? generationOptions.getInt("base") : 10; //blocks before noise is applied
    }


    HashMap <String, List<BlockLayer>> biomeLayers = new HashMap<String, List<BlockLayer>>();

    public Material getLayerBlock(int y, Biome biome, int maxy){

        ConfigurationSection biomeConfig = getSectionForBiome(biome);

        if(!biomeConfig.contains("layers")){
            System.out.println("No layers found for " + biome.name() + "! Only air will be used.");
            return Material.AIR;
        }

        ConfigurationSection layerSection = biomeConfig.getConfigurationSection("layers");

        List<BlockLayer> layers = new ArrayList<BlockLayer>();

        if(biomeLayers.containsKey(biome.name())){
            layers = biomeLayers.get(biome.name());
        }else {
            System.out.println("Generating layers for " + biome.name() );
        /* add all layers for biome */
            //TODO: cache layers
            for (String key : layerSection.getKeys(false)) {
                System.out.println("----- layer " + key );
                ConfigurationSection layer = layerSection.getConfigurationSection(key);
                Material[] m;
                if (layer.getString("material").contains(",")) {
                    m = new Material[layer.getString("material").split(",").length];
                    int i = 0;
                    for (String s : layer.getString("material").split(",")) {
                        String matstring = s.toUpperCase();
                        m[i] = Material.getMaterial(matstring);
                        if (m[i] == null) {
                            System.out.println("Invalid material: " + matstring);
                            m[i] = Material.CAKE;
                        }

                        ++i;
                    }
                } else {

                    String matstring = layer.getString("material").toUpperCase();
                    Material theMat = Material.getMaterial(matstring);

                    if (theMat == null) {
                        System.out.println("Invalid material: " + matstring);
                        theMat = Material.CAKE;
                    }
                    m = new Material[]{theMat};

                }


                BlockLayer l = new BlockLayer(layer.getInt("start"), layer.getInt("end"), m);
                layers.add(l);
                System.out.println("start(45) " + l.startBlock(45) );
                System.out.println("end(45) " + l.endBlock(45) );
                System.out.println("test block " + (l.startBlock(45)+1) + ":" + (l.endBlock(45)) + " " + l.blockInLayer((l.startBlock(45)+1), 45) );
            }

        /* sort layers for biome */
            layers.sort((a, b) -> (a.start < b.start ? 1 : 0));

            biomeLayers.put(biome.name(), layers);
        }
        int baseOffset = maxy - y;

        for(BlockLayer layer : layers){
            if(!logged)
                System.out.println("checking if " + baseOffset + " is in " + layer.start + ":" + layer.end);

            if(layer.blockInLayer(baseOffset, maxy)){
                if(!logged)
                    System.out.println("yes!");
                return layer.materialForBlock(baseOffset, maxy);
            }
        }

        return Material.CAKE;
    }

    HashMap<String, Boolean> showedlogfor = new HashMap<String, Boolean>();

    public ConfigurationSection getSectionForBiome(Biome biome){
        String biomeName = biome.name().toLowerCase();

        /* get the configuration for the current biome, or if that doesn't exist get the default configuration */
        ConfigurationSection biomeConfig = Main.biomes.getConfigurationSection(biomeName) != null ? Main.biomes.getConfigurationSection(biomeName) : Main.biomes.getConfigurationSection("default");

        if(!showedlogfor.containsKey(biomeName) || !showedlogfor.get(biomeName)) {
            if (biomeConfig == null) {
                System.out.println("Error finding default biome configuration and biome configuration for " + biomeName);
            } else if (Main.biomes.getConfigurationSection(biomeName) == null) {
                System.out.println("No config found for biome " + biomeName + ", using default configuration");
            }
            showedlogfor.put(biomeName, true);
        }
        return biomeConfig;
    }

    public void setChunkBiome(BiomeGrid biome, Random random){

        Biome[] biomes = new Biome[] {
            Biome.DEEP_OCEAN,
            Biome.BEACHES,
            Biome.BIRCH_FOREST,
                Biome.BIRCH_FOREST_HILLS,
                Biome.COLD_BEACH,
                Biome.DESERT,
                Biome.DESERT_HILLS,
                Biome.EXTREME_HILLS,
                Biome.EXTREME_HILLS_WITH_TREES,
                Biome.FOREST,
                Biome.FOREST_HILLS,
                Biome.FROZEN_OCEAN,
                Biome.FROZEN_RIVER,
                Biome.HELL,
                Biome.ICE_FLATS,
                Biome.ICE_MOUNTAINS,
                Biome.JUNGLE,
                Biome.JUNGLE_EDGE,
                Biome.JUNGLE_HILLS,
                Biome.MESA,
                Biome.MESA_CLEAR_ROCK,
                Biome.MESA_ROCK,
                Biome.MUSHROOM_ISLAND,
                Biome.MUSHROOM_ISLAND_SHORE,
                Biome.MUTATED_BIRCH_FOREST,
                Biome.MUTATED_BIRCH_FOREST_HILLS,
                Biome.MUTATED_DESERT,
                Biome.MUTATED_EXTREME_HILLS,
                Biome.MUTATED_EXTREME_HILLS_WITH_TREES,
                Biome.MUTATED_FOREST,
                Biome.MUTATED_ICE_FLATS,
                Biome.MUTATED_JUNGLE,
                Biome.MUTATED_JUNGLE_EDGE,
                Biome.MUTATED_MESA,
                Biome.MUTATED_MESA_CLEAR_ROCK,
                Biome.MUTATED_MESA_ROCK,
                Biome.MUTATED_PLAINS,
                Biome.MUTATED_REDWOOD_TAIGA,
                Biome.MUTATED_REDWOOD_TAIGA_HILLS,
                Biome.MUTATED_ROOFED_FOREST,
                Biome.MUTATED_SAVANNA,
                Biome.MUTATED_SAVANNA_ROCK,
                Biome.MUTATED_SWAMPLAND,
                Biome.MUTATED_TAIGA,
                Biome.MUTATED_TAIGA_COLD,
                Biome.OCEAN,
                Biome.PLAINS,
                Biome.REDWOOD_TAIGA,
                Biome.REDWOOD_TAIGA_HILLS,
                Biome.RIVER,
                Biome.ROOFED_FOREST,
                Biome.SAVANNA,
                Biome.SAVANNA_ROCK,
                Biome.SKY,
                Biome.SMALLER_EXTREME_HILLS,
                Biome.STONE_BEACH,
                Biome.SWAMPLAND,
                Biome.TAIGA,
                Biome.TAIGA_COLD,
                Biome.TAIGA_COLD_HILLS,
                Biome.TAIGA_HILLS
        };

        Biome b;
        switch (random.nextInt(11)) {
            case 0:
                b = Biome.BIRCH_FOREST;
                break;
            case 1:
                b =  Biome.TAIGA_COLD;
                break;
            case 2:
                b = Biome.DESERT;
                break;
            case 3:
                b = Biome.EXTREME_HILLS;
                break;
            case 4:
                b = Biome.FOREST_HILLS;
                break;
            case 5:
                b = Biome.FOREST;
                break;
            case 6:
                b = Biome.JUNGLE;
                break;
            case 7:
                b = Biome.PLAINS;
                break;
            case 8:
                b = Biome.SAVANNA;
                break;
            case 9:
                b = Biome.MUTATED_PLAINS;
                break;
            case 10:
                b = Biome.TAIGA;
                break;
            default:
                b = Biome.PLAINS;
                break;
        }
    }

    public static boolean isBiome(Location loc, Biome b) {
        return loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ()) == b;
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





































package info.icodethings.seed;

import jLibNoise.noise.NoiseQuality;
import jLibNoise.noise.module.Perlin;
import jLibNoise.noise.utils.*;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class GenerationCommand implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        //usage: /generate <width int> <height int> <freq double> <octaves int> <persistance double> <lacunarity double> <scale int> <seed int>

        if(args.length != 8)
            return false;

        if (sender instanceof Player) {
            Player player = (Player) sender;

            System.out.println("arg length: " + args.length);
            System.out.println("width: " + args[0]);
            int width = Integer.parseInt(args[0]);

            System.out.println("height: " + args[1]);
            int height = Integer.parseInt(args[1]);

            System.out.println("freq: " + args[2]);
            double freq = Double.parseDouble(args[2]); //noiceyness

            System.out.println("octaves: " + args[3]);
            int octaves = Integer.parseInt(args[3]);

            System.out.println("persistance: " + args[4]);
            double persistance = Double.parseDouble(args[4]); // detail

            System.out.println("lacunarity: " + args[5]);
            double lacunarity = Double.parseDouble(args[5]); // height streach?

            System.out.println("scale: " + args[6]);
            int scale = Integer.parseInt(args[6]);

            System.out.println("seed: " + args[7]);
            int seed = Integer.parseInt(args[7]);


            Perlin myModule = new Perlin();

            myModule.setFrequency(freq);
            myModule.setOctaveCount(octaves); // detail passes
            myModule.setPersistence(persistance); // detail
            myModule.setLacunarity(lacunarity);

            myModule.setSeed(seed);

            myModule.setNoiseQuality(NoiseQuality.QUALITY_BEST);

            int base = 10; //blocks before noise is applied

            int x;
            int z;
            int y;
            for(z = 0; z < height; ++z){
                for(x = 0; x < width; ++x){
                    double p = (myModule.getValue(x+1, z+1, .1) + 1) / 2.0 * scale;
                    //double p = (heightMap.getValue(x+1, z+1) + 1) / 2.0 * scale;
                    p += base;

                    for(y = 0; y < p; ++y){
                        player.getWorld().getBlockAt(x+player.getLocation().getBlockX(), y+player.getLocation().getBlockY(), z+player.getLocation().getBlockZ()).setType(player.getInventory().getItemInMainHand().getType());
                    }
                }
            }


            return true;

        }

        return false;
    }

    /**
     * Notes:
     * Islands: http://gamedev.stackexchange.com/questions/95772/how-to-scale-up-my-procedurally-generated-island-continent-without-losing-its
     *
     * usage: /generate <width int> <height int> <freq double> <octaves int> <persistance double> <lacunarity double> <scale int> <seed int>
     * Nice result: /generate 45 45 0.1 4 0.1 0.01 4 0
     */

}

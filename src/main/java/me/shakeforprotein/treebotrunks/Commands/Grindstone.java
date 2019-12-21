package me.shakeforprotein.treebotrunks.Commands;

import me.shakeforprotein.treebotrunks.TreeboTrunk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Grindstone implements CommandExecutor {

    private TreeboTrunk pl;


    public Grindstone(TreeboTrunk main) {
        this.pl = main;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.getLocation().getBlock().getType() == Material.AIR){
                if(p.getLocation().getBlock().getType() == Material.AIR){
                    if (!p.getLocation().subtract(0,1,0).getBlock().isEmpty()){
                        if(!p.getLocation().subtract(0,1,0).getBlock().isLiquid()){
                            long time = System.currentTimeMillis();
                            Location l = p.getLocation().getBlock().getLocation();
                            pl.getConfig().set("Grindstones." + time + ".X", l.getBlockX());
                            pl.getConfig().set("Grindstones." + time + ".Y", l.getBlockY());
                            pl.getConfig().set("Grindstones." + time + ".Z", l.getBlockZ());
                            pl.getConfig().set("Grindstones." + time + ".World", l.getWorld().getName());

                            p.getLocation().getBlock().setType(Material.GRINDSTONE);
                            Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                                @Override
                                public void run() {
                                    int x, y, z = 0;
                                    x = pl.getConfig().getInt("Grindstones." + time + ".X");
                                    y = pl.getConfig().getInt("Grindstones." + time + ".Y");
                                    z = pl.getConfig().getInt("Grindstones." + time + ".Z");
                                    String world = pl.getConfig().getString("Grindstones." + time + ".World");
                                    Location loc = new Location(Bukkit.getWorld(world), x, y, z);
                                    loc.getBlock().setType(Material.AIR);
                                    pl.getConfig().set("Grindstones." + time, null);
                                }
                            }, 600L);
                        }
                    }
                }
            }
        }
        else{
            sender.sendMessage(pl.badge + pl.err + "You must be a player to use this command");
        }
        return true;
    }
}

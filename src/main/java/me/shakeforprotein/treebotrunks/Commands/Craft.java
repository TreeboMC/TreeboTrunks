package me.shakeforprotein.treebotrunks.Commands;

import me.shakeforprotein.treebotrunks.TreeboTrunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Craft implements CommandExecutor {

    private TreeboTrunk pl;


    public Craft(TreeboTrunk main) {
        this.pl = main;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        if(sender instanceof Player){
            Player p = (Player) sender;
            Location benchLocation = new Location(p.getWorld(), 10000001,1,1000000);
            benchLocation.getBlock().setType(Material.ANVIL);
            p.openWorkbench(benchLocation, true);
        }
        else{
            sender.sendMessage(pl.badge + pl.err + "You must be a player to use this command");
        }
        return true;
    }
}

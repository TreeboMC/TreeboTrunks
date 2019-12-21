package me.shakeforprotein.treebotrunks.Commands;

import me.shakeforprotein.treebotrunks.TreeboTrunk;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddTrunk implements CommandExecutor {

    private TreeboTrunk pl;


    public AddTrunk(TreeboTrunk main) {
        this.pl = main;

    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 3) {
            if (pl.isNumeric(args[2])) {

                Player p;
                p = Bukkit.getPlayer(args[0]);
                int current = pl.getVarPerm("treebotrunk." + args[1], p);
                sender.sendMessage(pl.badge + "Updating user permissions for " + args[0]);
                Bukkit.dispatchCommand(sender, "pex user " + args[0] + " remove treebotrunk." + args[1] + "." + current);
                Bukkit.dispatchCommand(sender, "pex user " + args[0] + " add treebotrunk." + args[1] + "." + (current + Integer.parseInt(args[2])));
                sender.sendMessage(pl.badge + "Permission treebotrunk." + args[0] + "." + current + "  updated to  treebotrunk." + args[1] + "." + (current + Integer.parseInt(args[2])));
            } else {
                sender.sendMessage(pl.badge + pl.err + "Last argument must be a number");
            }
        } else {
            sender.sendMessage(pl.badge + pl.err + "This command requires 3 arguments");
            sender.sendMessage(pl.getCommand("AddTrunk").getUsage());
        }

        return true;
    }
}

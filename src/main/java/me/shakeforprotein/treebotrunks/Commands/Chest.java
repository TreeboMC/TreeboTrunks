package me.shakeforprotein.treebotrunks.Commands;

import me.shakeforprotein.treebotrunks.TreeboTrunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Chest implements CommandExecutor {
    private TreeboTrunk pl;


    public Chest(TreeboTrunk main) {
        this.pl = main;

    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            String type = "chest";
            if (args.length == 1) {
                if (pl.isNumeric(args[0])) {
                    int max = pl.getVarPerm("treebotrunk." + type, (Player) sender);
                    if (max >= Integer.parseInt(args[0])) {
                        pl.openInventory(type, (Player) sender, Integer.parseInt(args[0]));
                    } else {
                        sender.sendMessage(pl.badge + pl.err + " You are only authorized for " + (max +1) + " " + type + "(s)");
                    }
                } else {
                    sender.sendMessage(pl.badge + pl.err + " argument one must be a number.");
                }
            } else if (args.length == 0) {
                pl.openInventory(type, (Player) sender, 0);
            } else {
                sender.sendMessage(pl.badge + pl.err + " This command only supports one argument.");
            }
        } else {
            sender.sendMessage(pl.badge + pl.err + "This command is only usable as a player");

        }
        return true;
    }
}

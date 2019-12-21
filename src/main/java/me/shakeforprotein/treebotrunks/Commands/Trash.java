package me.shakeforprotein.treebotrunks.Commands;

import me.shakeforprotein.treebotrunks.TreeboTrunk;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Trash implements CommandExecutor {

    private TreeboTrunk pl;

    public Trash(TreeboTrunk main){
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            ((Player) sender).openInventory(Bukkit.createInventory(null, 54, "Trash"));
        } else {
            sender.sendMessage(pl.badge + pl.err + "This command is only usable as a player");

        }
        return true;
    }
}
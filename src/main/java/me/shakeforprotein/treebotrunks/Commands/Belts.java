package me.shakeforprotein.treebotrunks.Commands;

import me.shakeforprotein.treebotrunks.TreeboTrunk;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

public class Belts implements CommandExecutor {

    private TreeboTrunk pl;


    public Belts(TreeboTrunk main) {
        this.pl = main;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            String type = "belt";
            String world = ((Player) sender).getWorld().getName().split("_")[0];


            Inventory inv = Bukkit.createInventory(null, 54, pl.badge + " Your " + type + "s");
            ItemStack fillerItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
            ItemStack standardChest = new ItemStack(Material.LEAD, 1);
            int i = 0;
            for (i = 0; i < inv.getSize(); i++) {
                inv.setItem(i, fillerItem);
            }

            int max = pl.getVarPerm("treebotrunk." + type, (Player) sender)+1;
            if(max > 54){
                max = 54;
            }
            for (i = 0; i < max; i++) {
                ItemMeta iMeta = standardChest.getItemMeta();
                iMeta.setDisplayName(i + "");
                standardChest.setItemMeta(iMeta);
                inv.setItem(i, standardChest);
                File invFile = new File(pl.getDataFolder() + File.separator + type + File.separator, ((Player) sender).getUniqueId().toString() + "_" + type + "_" + i + "_" + world +".yml");
                FileConfiguration invYaml = YamlConfiguration.loadConfiguration(invFile);
                if (invYaml.get("icon") != null) {
                    ItemStack icon = new ItemStack(Material.valueOf(invYaml.getString("icon")), 1);
                    iMeta = icon.getItemMeta();
                    iMeta.setDisplayName(i + "");
                    if (invYaml.get("title") != null) {
                        iMeta.setDisplayName(iMeta.getDisplayName() + " - " + invYaml.getString("title"));
                        icon.setItemMeta(iMeta);
                    }
                    else{
                        iMeta.setDisplayName(i + "");
                        icon.setItemMeta(iMeta);
                    }
                    inv.setItem(i, icon);
                }
            }
            ((Player) sender).openInventory(inv);

        }

        return true;
    }
}

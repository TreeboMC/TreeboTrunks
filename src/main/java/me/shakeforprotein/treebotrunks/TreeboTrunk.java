package me.shakeforprotein.treebotrunks;

import me.shakeforprotein.treebotrunks.Commands.*;
import me.shakeforprotein.treebotrunks.Listeners.InventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class TreeboTrunk extends JavaPlugin {

    public String badge = ChatColor.translateAlternateColorCodes('&', getConfig().getString("badge"));
    public String err = ChatColor.RED + "" + ChatColor.BOLD + "Error:";

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        getConfig().set("version", this.getDescription().getVersion());
        saveConfig();

        this.getCommand("addtrunk").setExecutor(new AddTrunk(this));
        this.getCommand("barrel").setExecutor(new Barrel(this));
        this.getCommand("chest").setExecutor(new Chest(this));
        this.getCommand("belt").setExecutor(new Belt(this));
        this.getCommand("trash").setExecutor(new Trash(this));
        this.getCommand("anvil").setExecutor(new Anvil(this));
        this.getCommand("craft").setExecutor(new Craft(this));
        this.getCommand("enchant").setExecutor(new Enchant(this));
        this.getCommand("grindstone").setExecutor(new Grindstone(this));
        this.getCommand("stonecutter").setExecutor(new Stonecutter(this));

        Bukkit.getPluginManager().registerEvents(new InventoryListener(this), this);
     }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
    }

    public static int getVarPerm(String perm, Player p) {
        int i = 100;
        int max = 0;
        while (i > 0) {
            i--;
            if (p.hasPermission(perm + "." + i)) {
                max = i;
                break;
            }
        }
        return max;
    }

    public static boolean isNumeric (String str)
    {
        return str.matches("\\d+");
    }

    public void openInventory(String type, Player p, int container){
        int size = 9;
        if(type.equalsIgnoreCase("belt")){
            size = 9;
        }
        else if(type.equalsIgnoreCase("barrel")){
            size = 27;
        }
        else if(type.equalsIgnoreCase("chest")){
            size = 54;
        }
        else{
            p.sendMessage(badge + err + "Invalid inventory specification");
        }

        File invFile = new File(this.getDataFolder() + File.separator + type + File.separator, p.getUniqueId().toString() + "_" + type + "_" + container + ".yml");
        FileConfiguration invYaml = YamlConfiguration.loadConfiguration(invFile);
        Inventory thisInv = Bukkit.createInventory(null, size, badge + p.getUniqueId().toString() + "_" + type + "_" + container);
        if(invFile.exists()){
            for(String key : invYaml.getConfigurationSection("Inventory").getKeys(false)){
                thisInv.setItem(invYaml.getInt("Inventory." + key + ".slot"), invYaml.getItemStack("Inventory." + key + ".item"));
            }
        }
        p.openInventory(thisInv);
        invFile = null;
        invYaml = null;
    }
}

package me.shakeforprotein.treebotrunks;

import me.shakeforprotein.treebotrunks.Commands.*;
import me.shakeforprotein.treebotrunks.Listeners.InventoryListener;
import me.shakeforprotein.treebotrunks.Listeners.JoinListener;
import me.shakeforprotein.treebotrunks.SpigotUpdateChecker.SpigotUpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public final class TreeboTrunk extends JavaPlugin {

    public String badge = ChatColor.translateAlternateColorCodes('&', getConfig().getString("badge"));
    public String err = ChatColor.RED + "" + ChatColor.BOLD + " Error:";
    public HashMap<Entity, Integer> astHash = new HashMap<>();
    public HashMap<Material, Material> whitelistHash = new HashMap<>();
    public static Boolean checkUpdates = false;
    public Boolean requiresUpdate = false;
    public static String spigotVersion = "0";
    public HashMap<Player, Boolean> notifyHash = new HashMap<>();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        getConfig().set("version", this.getDescription().getVersion());
        saveConfig();
        Logger logger = this.getLogger();
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
        this.getCommand("loom").setExecutor(new Loom(this));
        this.getCommand("cartographer").setExecutor(new Cartographer(this));
        this.getCommand("brew").setExecutor(new BrewingStand(this));
        this.getCommand("smoker").setExecutor(new Smoker(this));
        this.getCommand("furnace").setExecutor(new Furnace(this));
        this.getCommand("blastfurnace").setExecutor(new BlastFurnace(this));
        this.getCommand("chests").setExecutor(new Chests(this));
        this.getCommand("belts").setExecutor(new Belts(this));
        this.getCommand("barrels").setExecutor(new Barrels(this));

        Bukkit.getPluginManager().registerEvents(new InventoryListener(this), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        if(getConfig().get("bstatsIntegration") != null) {
            if (getConfig().getBoolean("bstatsIntegration")) {
                Metrics metrics = new Metrics(this);
            }
        }
        if(getConfig().get("updateChecker") != null){
            if(getConfig().getBoolean("checkUpdates")) {
                if (getConfig().getString("updateChecker").equalsIgnoreCase("spigot")) {
                    new SpigotUpdateChecker(this, 73787).getVersion(version -> {
                        if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                            logger.info(" can be updated to Version: " + version);
                            requiresUpdate = true;
                            spigotVersion = version;
                        }
                    });

                } else if (getConfig().getString("updateChecker").equalsIgnoreCase("github")) {

                }
            }
        }
        if(getConfig().get("whitelistBlocks") != null){
            List<String> whiteList = new ArrayList<>();
            whiteList = getConfig().getStringList("whitelistBlocks");
            for(String item : whiteList){
                whitelistHash.putIfAbsent(Material.valueOf(item), Material.valueOf(item));
            }
        }
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
        String world = p.getWorld().getName().split("_")[0];
        int size = 9;
        String icon = "CHEST";
        if(type.equalsIgnoreCase("belt")){
            icon = "LEAD";
            size = 9;
        }
        else if(type.equalsIgnoreCase("barrel")){
            size = 27;
            icon = "BARREL";
        }
        else if(type.equalsIgnoreCase("chest")){
            size = 54;
        }
        else{
            p.sendMessage(badge + err + "Invalid inventory specification");
        }

        File invFile = new File(this.getDataFolder() + File.separator + type + File.separator, p.getUniqueId().toString() + "_" + type + "_" + container + "_" + world + ".yml");
        FileConfiguration invYaml = YamlConfiguration.loadConfiguration(invFile);
        Inventory thisInv = Bukkit.createInventory(null, size, badge + p.getUniqueId().toString() + "_" + type + "_" + container);
        if(invFile.exists()){
            if(invYaml.getConfigurationSection("Inventory") != null) {
                for (String key : invYaml.getConfigurationSection("Inventory").getKeys(false)) {
                    thisInv.setItem(invYaml.getInt("Inventory." + key + ".slot"), invYaml.getItemStack("Inventory." + key + ".item"));
                }
                if (invYaml.get("icon") == null) {
                    invYaml.set("icon", icon);
                }
            }
        }
        p.openInventory(thisInv);
        invFile = null;
        invYaml = null;
    }
}

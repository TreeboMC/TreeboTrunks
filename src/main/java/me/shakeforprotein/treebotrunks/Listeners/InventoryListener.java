package me.shakeforprotein.treebotrunks.Listeners;

import me.shakeforprotein.treebotrunks.TreeboTrunk;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class InventoryListener implements Listener {

    private TreeboTrunk pl;

    public InventoryListener(TreeboTrunk main) {
        this.pl = main;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        String title = ChatColor.stripColor(e.getView().getTitle());
        if (title.toLowerCase().contains(ChatColor.stripColor(pl.badge).toLowerCase())) {
            String type = title.split("_")[1];
            if (!type.equalsIgnoreCase("trash")) {
                String fileName = e.getView().getTitle().replace(pl.badge, "");
                File theFile = new File(pl.getDataFolder() + File.separator + type + File.separator, e.getPlayer().getUniqueId().toString() + "_" + type + "_" + title.split("_")[2] + ".yml");
                FileConfiguration theYaml = YamlConfiguration.loadConfiguration(theFile);
                int slot = 0;
                for (slot = 0; slot < e.getInventory().getSize(); slot++) {
                    theYaml.set("Inventory.slot_" + slot + ".slot", slot);
                    theYaml.set("Inventory.slot_" + slot + ".item", e.getInventory().getItem(slot));
                }
                try {
                    theYaml.save(theFile);
                } catch (IOException err) {
                    pl.getLogger().warning(pl.badge + "Error saving inventory " + type + " " + title.split("_")[2] + " for " + e.getPlayer().getName() + " - " + e.getPlayer().getUniqueId().toString());
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.ANVIL) {
            if (pl.getConfig().getConfigurationSection("Anvils") != null) {
                for (String key : pl.getConfig().getConfigurationSection("Anvils").getKeys(false)) {
                    if (e.getBlock().getLocation().getBlockX() == pl.getConfig().getInt("Anvils." + key + ".X")) {
                        if (e.getBlock().getLocation().getBlockY() == pl.getConfig().getInt("Anvils." + key + ".Y")) {
                            if (e.getBlock().getLocation().getBlockZ() == pl.getConfig().getInt("Anvils." + key + ".Z")) {
                                if (e.getBlock().getLocation().getWorld().getName() == pl.getConfig().getString("Anvils." + key + ".World")) {
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (e.getBlock().getType() == Material.ENCHANTING_TABLE) {
            if (pl.getConfig().getConfigurationSection("EnchTables") != null) {
                for (String key : pl.getConfig().getConfigurationSection("EnchTables").getKeys(false)) {
                    if (e.getBlock().getLocation().getBlockX() == pl.getConfig().getInt("EnchTables." + key + ".X")) {
                        if (e.getBlock().getLocation().getBlockY() == pl.getConfig().getInt("EnchTables." + key + ".Y")) {
                            if (e.getBlock().getLocation().getBlockZ() == pl.getConfig().getInt("EnchTables." + key + ".Z")) {
                                if (e.getBlock().getLocation().getWorld().getName() == pl.getConfig().getString("EnchTables." + key + ".World")) {
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (e.getBlock().getType() == Material.GRINDSTONE) {
            if (pl.getConfig().getConfigurationSection("Grindstones") != null) {
                for (String key : pl.getConfig().getConfigurationSection("Grindstones").getKeys(false)) {
                    if (e.getBlock().getLocation().getBlockX() == pl.getConfig().getInt("Grindstones." + key + ".X")) {
                        if (e.getBlock().getLocation().getBlockY() == pl.getConfig().getInt("Grindstones." + key + ".Y")) {
                            if (e.getBlock().getLocation().getBlockZ() == pl.getConfig().getInt("Grindstones." + key + ".Z")) {
                                if (e.getBlock().getLocation().getWorld().getName() == pl.getConfig().getString("Grindstones." + key + ".World")) {
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (e.getBlock().getType() == Material.STONECUTTER) {
            if (pl.getConfig().getConfigurationSection("Stonecutters") != null) {
                for (String key : pl.getConfig().getConfigurationSection("Stonecutters").getKeys(false)) {
                    if (e.getBlock().getLocation().getBlockX() == pl.getConfig().getInt("Stonecutters." + key + ".X")) {
                        if (e.getBlock().getLocation().getBlockY() == pl.getConfig().getInt("Stonecutters." + key + ".Y")) {
                            if (e.getBlock().getLocation().getBlockZ() == pl.getConfig().getInt("Stonecutters." + key + ".Z")) {
                                if (e.getBlock().getLocation().getWorld().getName() == pl.getConfig().getString("Stonecutters." + key + ".World")) {
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAnvilFall(EntitySpawnEvent e) {
        int x, y, z = 0;
        if (e.getEntity() instanceof FallingBlock) {

            x = e.getLocation().getBlockX();
            y = e.getLocation().getBlockY();
            z = e.getLocation().getBlockZ();
            String world = e.getLocation().getWorld().getName();

            if (pl.getConfig().getConfigurationSection("Anvils") != null) {
                for (String key : pl.getConfig().getConfigurationSection("Anvils").getKeys(false)) {
                    if (x == pl.getConfig().getInt("Anvils." + key + ".X")) {
                        if (y == pl.getConfig().getInt("Anvils." + key + ".Y")) {
                            if (z == pl.getConfig().getInt("Anvils." + key + ".Z")) {
                                if (world == pl.getConfig().getString("Anvils." + key + ".World")) {
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e){
        List<Block> Blocks = e.getBlocks();
        for(Block block : Blocks) {
            if(block.getType() == Material.STONECUTTER) {
                if (pl.getConfig().getConfigurationSection("Stonecutters") != null) {
                    for (String key : pl.getConfig().getConfigurationSection("Stonecutters").getKeys(false)) {
                        if (block.getLocation().getBlockX() == pl.getConfig().getInt("Stonecutters." + key + ".X")) {
                            if (block.getLocation().getBlockY() == pl.getConfig().getInt("Stonecutters." + key + ".Y")) {
                                if (block.getLocation().getBlockZ() == pl.getConfig().getInt("Stonecutters." + key + ".Z")) {
                                    if (block.getLocation().getWorld().getName() == pl.getConfig().getString("Stonecutters." + key + ".World")) {
                                        e.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
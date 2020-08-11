package me.shakeforprotein.treebotrunks.Listeners;

import me.shakeforprotein.treebotrunks.TreeboTrunk;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InventoryListener implements Listener {

    private TreeboTrunk pl;

    public InventoryListener(TreeboTrunk main) {
        this.pl = main;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        String world = e.getPlayer().getWorld().getName().split("_")[0];
        String title = ChatColor.stripColor(e.getView().getTitle());
        if (title.toLowerCase().contains(ChatColor.stripColor(pl.badge).toLowerCase()) && !title.startsWith(ChatColor.stripColor(pl.badge + " Your ")) && !title.startsWith(ChatColor.stripColor(pl.badge + " Configure_"))) {
            e.getPlayer().sendMessage(pl.badge + "Saved Inventory.");
            String type = title.split("_")[1];
            if (!type.equalsIgnoreCase("trash")) {
                String fileName = e.getView().getTitle().replace(pl.badge, "");
                File theFile = new File(pl.getDataFolder() + File.separator + type + File.separator, e.getPlayer().getUniqueId().toString() + "_" + type + "_" + title.split("_")[2] + "_" + world + ".yml");
                FileConfiguration theYaml = YamlConfiguration.loadConfiguration(theFile);
                int slot = 0;
                for (slot = 0; slot < e.getInventory().getSize(); slot++) {
                    theYaml.set("Inventory.slot_" + slot + ".slot", slot);
                    theYaml.set("Inventory.slot_" + slot + ".item", e.getInventory().getItem(slot));
                }
                if (theYaml.getString("icon") == null) {
                    if (type.equalsIgnoreCase("chest")) {
                        theYaml.set("icon", "CHEST");
                    } else if (type.equalsIgnoreCase("barrel")) {
                        theYaml.set("icon", "BARREL");
                    } else if (type.equalsIgnoreCase("belt")) {
                        theYaml.set("icon", "LEAD");
                    }
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
        HashMap<Material, String> denyType = new HashMap<>();
        denyType.putIfAbsent(Material.STONECUTTER, "Stonecutters");
        denyType.putIfAbsent(Material.CARTOGRAPHY_TABLE, "Cartographers");
        denyType.putIfAbsent(Material.LOOM, "Looms");
        denyType.putIfAbsent(Material.ANVIL, "Anvils");
        denyType.putIfAbsent(Material.ENCHANTING_TABLE, "EnchTables");
        denyType.putIfAbsent(Material.GRINDSTONE, "Grindstones");
        denyType.putIfAbsent(Material.STONECUTTER, "Stonecutters");
        denyType.putIfAbsent(Material.FURNACE, "Furnaces");
        denyType.putIfAbsent(Material.BLAST_FURNACE, "BlastFurnaces");
        denyType.putIfAbsent(Material.SMOKER, "Smokers");
        denyType.putIfAbsent(Material.BREWING_STAND, "BrewingStands");
        for (Material mat : denyType.keySet()) {
            if (e.getBlock().getType() == mat) {
                if (pl.getConfig().getConfigurationSection(denyType.get(mat)) != null) {
                    for (String key : pl.getConfig().getConfigurationSection(denyType.get(mat)).getKeys(false)) {
                        if (e.getBlock().getLocation().getBlockX() == pl.getConfig().getInt(denyType.get(mat) + "." + key + ".X")) {
                            if (e.getBlock().getLocation().getBlockY() == pl.getConfig().getInt(denyType.get(mat) + "." + key + ".Y")) {
                                if (e.getBlock().getLocation().getBlockZ() == pl.getConfig().getInt(denyType.get(mat) + "." + key + ".Z")) {
                                    if (e.getBlock().getLocation().getWorld().getName() == pl.getConfig().getString(denyType.get(mat) + "." + key + ".World")) {
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
    public void onPistonExtend(BlockPistonExtendEvent e) {
        List<Block> Blocks = e.getBlocks();
        HashMap<Material, String> denyType = new HashMap<>();
        denyType.putIfAbsent(Material.STONECUTTER, "Stonecutters");
        denyType.putIfAbsent(Material.CARTOGRAPHY_TABLE, "Cartographers");
        denyType.putIfAbsent(Material.LOOM, "Looms");
        denyType.putIfAbsent(Material.ANVIL, "Anvils");
        denyType.putIfAbsent(Material.ENCHANTING_TABLE, "EnchTables");
        denyType.putIfAbsent(Material.GRINDSTONE, "Grindstones");
        denyType.putIfAbsent(Material.STONECUTTER, "Stonecutters");
        denyType.putIfAbsent(Material.FURNACE, "Furnaces");
        denyType.putIfAbsent(Material.BLAST_FURNACE, "BlastFurnaces");
        denyType.putIfAbsent(Material.SMOKER, "Smokers");
        denyType.putIfAbsent(Material.BREWING_STAND, "BrewingStands");
        for (Material mat : denyType.keySet()) {
            for (Block block : Blocks) {
                if (block.getType() == mat) {
                    if (pl.getConfig().getConfigurationSection(denyType.get(mat)) != null) {
                        for (String key : pl.getConfig().getConfigurationSection(denyType.get(mat)).getKeys(false)) {
                            if (block.getLocation().getBlockX() == pl.getConfig().getInt(denyType.get(mat) + "." + key + ".X")) {
                                if (block.getLocation().getBlockY() == pl.getConfig().getInt(denyType.get(mat) + "." + key + ".Y")) {
                                    if (block.getLocation().getBlockZ() == pl.getConfig().getInt(denyType.get(mat) + "." + key + ".Z")) {
                                        if (block.getLocation().getWorld().getName() == pl.getConfig().getString(denyType.get(mat) + "." + key + ".World")) {
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

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e) {
        List<Block> Blocks = e.getBlocks();
        HashMap<Material, String> denyType = new HashMap<>();
        denyType.putIfAbsent(Material.STONECUTTER, "Stonecutters");
        denyType.putIfAbsent(Material.CARTOGRAPHY_TABLE, "Cartographers");
        denyType.putIfAbsent(Material.LOOM, "Looms");
        denyType.putIfAbsent(Material.ANVIL, "Anvils");
        denyType.putIfAbsent(Material.ENCHANTING_TABLE, "EnchTables");
        denyType.putIfAbsent(Material.GRINDSTONE, "Grindstones");
        denyType.putIfAbsent(Material.STONECUTTER, "Stonecutters");
        denyType.putIfAbsent(Material.FURNACE, "Furnaces");
        denyType.putIfAbsent(Material.BLAST_FURNACE, "BlastFurnaces");
        denyType.putIfAbsent(Material.SMOKER, "Smokers");
        denyType.putIfAbsent(Material.BREWING_STAND, "BrewingStands");
        for (Material mat : denyType.keySet()) {
            for (Block block : Blocks) {
                if (block.getType() == mat) {
                    if (pl.getConfig().getConfigurationSection(denyType.get(mat)) != null) {
                        for (String key : pl.getConfig().getConfigurationSection(denyType.get(mat)).getKeys(false)) {
                            if (block.getLocation().getBlockX() == pl.getConfig().getInt(denyType.get(mat) + "." + key + ".X")) {
                                if (block.getLocation().getBlockY() == pl.getConfig().getInt(denyType.get(mat) + "." + key + ".Y")) {
                                    if (block.getLocation().getBlockZ() == pl.getConfig().getInt(denyType.get(mat) + "." + key + ".Z")) {
                                        if (block.getLocation().getWorld().getName() == pl.getConfig().getString(denyType.get(mat) + "." + key + ".World")) {
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

    @EventHandler
    public void manipulate(PlayerArmorStandManipulateEvent e) {
        UUID entID = e.getRightClicked().getUniqueId();
        for (String key : pl.getConfig().getKeys(false)) {
            for (String key2 : pl.getConfig().getConfigurationSection(key).getKeys(false)) {
                if (pl.getConfig().get(key + "." + key2 + ".AST") != null) {
                    if (UUID.fromString(pl.getConfig().getString(key + "." + key2 + ".AST")) == entID) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void itemDrop(ItemSpawnEvent e) {
        HashMap<Material, String> denyType = new HashMap<>();
        denyType.putIfAbsent(Material.STONECUTTER, "Stonecutters");
        denyType.putIfAbsent(Material.CARTOGRAPHY_TABLE, "Cartographers");
        denyType.putIfAbsent(Material.LOOM, "Looms");
        denyType.putIfAbsent(Material.ANVIL, "Anvils");
        denyType.putIfAbsent(Material.ENCHANTING_TABLE, "EnchTables");
        denyType.putIfAbsent(Material.GRINDSTONE, "Grindstones");
        denyType.putIfAbsent(Material.STONECUTTER, "Stonecutters");
        denyType.putIfAbsent(Material.FURNACE, "Furnaces");
        denyType.putIfAbsent(Material.BLAST_FURNACE, "BlastFurnaces");
        denyType.putIfAbsent(Material.SMOKER, "Smokers");
        denyType.putIfAbsent(Material.BREWING_STAND, "BrewingStands");
    }

    @EventHandler
    public void inventoryInteractListener(InventoryInteractEvent e) {
        if (e.getView().getTitle().toLowerCase().startsWith(ChatColor.stripColor(pl.badge + " Your"))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void inventoryClickListener(InventoryClickEvent e) {
        String world = e.getWhoClicked().getWorld().getName().split("_")[0];
        if (e.getView().getTitle().startsWith(pl.badge + " Your chests")) {
            if (e.getSlot() == -999) {
                e.getWhoClicked().closeInventory();
            } else {
                e.setCancelled(true);
                if (!(e.getClickedInventory() instanceof PlayerInventory)) {
                    int max = pl.getVarPerm("treebotrunk.chest", (Player) e.getWhoClicked());
                    if (e.getSlot() < max + 1) {
                        ClickType cT = e.getClick();
                        if (cT == ClickType.LEFT) {
                            Bukkit.dispatchCommand(e.getWhoClicked(), "chest " + e.getSlot());
                        } else if (cT == ClickType.RIGHT) {
                            Inventory newInv = Bukkit.createInventory(null, 9, pl.badge + " Configure_Chest_" + e.getSlot() + "_" + world);
                            ItemStack nameTag = new ItemStack(Material.NAME_TAG, 1);
                            ItemStack icon = new ItemStack(Material.FILLED_MAP, 1);
                            ItemStack back = new ItemStack(Material.BARRIER, 1);
                            ItemMeta tagMeta = nameTag.getItemMeta();
                            ItemMeta iconMeta = icon.getItemMeta();
                            ItemMeta backMeta = back.getItemMeta();
                            tagMeta.setDisplayName("Rename Chest " + e.getSlot() + "_" + world);
                            iconMeta.setDisplayName("Set icon for Chest " + e.getSlot() + "_" + world);
                            backMeta.setDisplayName("Return to previous menu");
                            nameTag.setItemMeta(tagMeta);
                            icon.setItemMeta(iconMeta);
                            back.setItemMeta(backMeta);
                            newInv.setItem(0, nameTag);
                            newInv.setItem(1, icon);
                            newInv.setItem(8, back);

                            e.getWhoClicked().openInventory(newInv);
                        }
                    }
                }
            }
        } else if (e.getView().getTitle().startsWith(pl.badge + " Your belts")) {
            if (e.getSlot() == -999) {
                e.getWhoClicked().closeInventory();
            } else {
                e.setCancelled(true);
                if (!(e.getClickedInventory() instanceof PlayerInventory)) {
                    int max = pl.getVarPerm("treebotrunk.belt", (Player) e.getWhoClicked());
                    if (e.getSlot() < max + 1) {
                        ClickType cT = e.getClick();
                        if (cT == ClickType.LEFT) {
                            Bukkit.dispatchCommand(e.getWhoClicked(), "belt " + e.getSlot());
                        } else if (cT == ClickType.RIGHT) {
                            Inventory newInv = Bukkit.createInventory(null, 9, pl.badge + " Configure_Belt_" + e.getSlot() + "_" + world);
                            ItemStack nameTag = new ItemStack(Material.NAME_TAG, 1);
                            ItemStack icon = new ItemStack(Material.FILLED_MAP, 1);
                            ItemStack back = new ItemStack(Material.BARRIER, 1);
                            ItemMeta tagMeta = nameTag.getItemMeta();
                            ItemMeta iconMeta = icon.getItemMeta();
                            ItemMeta backMeta = back.getItemMeta();
                            tagMeta.setDisplayName("Rename Belt " + e.getSlot() + "_" + world);
                            iconMeta.setDisplayName("Set icon for Belt " + e.getSlot() + "_" + world);
                            backMeta.setDisplayName("Return to previous menu");
                            nameTag.setItemMeta(tagMeta);
                            icon.setItemMeta(iconMeta);
                            back.setItemMeta(backMeta);
                            newInv.setItem(0, nameTag);
                            newInv.setItem(1, icon);
                            newInv.setItem(8, back);

                            e.getWhoClicked().openInventory(newInv);
                        }
                    }
                }
            }
        } else if (e.getView().getTitle().startsWith(pl.badge + " Your barrels")) {
            if (e.getSlot() == -999) {
                e.getWhoClicked().closeInventory();
            } else {
                e.setCancelled(true);
                if (!(e.getClickedInventory() instanceof PlayerInventory)) {
                    int max = pl.getVarPerm("treebotrunk.barrel", (Player) e.getWhoClicked());
                    if (e.getSlot() < max + 1) {
                        ClickType cT = e.getClick();
                        if (cT == ClickType.LEFT) {
                            Bukkit.dispatchCommand(e.getWhoClicked(), "barrel " + e.getSlot());
                        } else if (cT == ClickType.RIGHT) {
                            Inventory newInv = Bukkit.createInventory(null, 9, pl.badge + " Configure_Barrel_" + e.getSlot() + "_" + world);
                            ItemStack nameTag = new ItemStack(Material.NAME_TAG, 1);
                            ItemStack icon = new ItemStack(Material.FILLED_MAP, 1);
                            ItemStack back = new ItemStack(Material.BARRIER, 1);
                            ItemMeta tagMeta = nameTag.getItemMeta();
                            ItemMeta iconMeta = icon.getItemMeta();
                            ItemMeta backMeta = back.getItemMeta();
                            tagMeta.setDisplayName("Rename Barrel " + e.getSlot() + "_" + world);
                            iconMeta.setDisplayName("Set icon for Barrel " + e.getSlot() + "_" + world);
                            backMeta.setDisplayName("Return to previous menu");
                            nameTag.setItemMeta(tagMeta);
                            icon.setItemMeta(iconMeta);
                            back.setItemMeta(backMeta);
                            newInv.setItem(0, nameTag);
                            newInv.setItem(1, icon);
                            newInv.setItem(8, back);
                            e.getWhoClicked().openInventory(newInv);
                        }
                    }
                }
            }
        } else if (e.getView().getTitle().startsWith(pl.badge + " Configure_")) {
            if (e.getSlot() == -999) {
                e.getWhoClicked().closeInventory();
            } else {
                e.setCancelled(true);
                if (!(e.getClickedInventory() instanceof PlayerInventory)) {
                    String type = e.getView().getTitle().split("_")[1].toLowerCase();
                    String uuid = e.getWhoClicked().getUniqueId().toString();
                    String slot = e.getView().getTitle().split("_")[2];
                    Inventory inv = e.getInventory();
                    if (e.getInventory().getItem(e.getSlot()) != null && e.getInventory().getItem(e.getSlot()).getType() != Material.AIR) {
                        if (e.getInventory().getItem(e.getSlot()).getType() == Material.NAME_TAG) {
                            e.getWhoClicked().closeInventory();
                            e.getWhoClicked().sendMessage(pl.badge + ChatColor.GOLD + " Enter the new name for " + type + " " + slot + "_" + world);
                            Bukkit.getScheduler().runTaskLaterAsynchronously(pl, new Runnable() {
                                @Override
                                public void run() {
                                    if(pl.getConfig().getBoolean("ListeningTo." + e.getWhoClicked().getUniqueId().toString())){
                                        pl.getConfig().set("ListeningTo." + e.getWhoClicked().getUniqueId().toString(), false);
                                        e.getWhoClicked().sendMessage(pl.badge + ChatColor.RED + " Timeout: " + ChatColor.GOLD +"No longer monitoring user input for rename.");
                                    }
                                }
                            }, 300);
                            pl.getConfig().set("ListeningTo." + e.getWhoClicked().getUniqueId().toString(), true);
                            pl.getConfig().set("ValuesFor." + e.getWhoClicked().getUniqueId().toString() + ".type", type.toLowerCase());
                            pl.getConfig().set("ValuesFor." + e.getWhoClicked().getUniqueId().toString() + ".number", slot);
                        } else if (e.getInventory().getItem(e.getSlot()).getType() == Material.FILLED_MAP) {
                            ItemStack item = e.getWhoClicked().getInventory().getItemInMainHand();
                            if (item.getType() != Material.AIR) {
                                File invFile = new File(pl.getDataFolder() + File.separator + type + File.separator, e.getWhoClicked().getUniqueId().toString() + "_" + type + "_" + slot + "_" + world + ".yml");
                                FileConfiguration invYaml = YamlConfiguration.loadConfiguration(invFile);
                                invYaml.set("icon", item.getType().name().toUpperCase());
                                try {
                                    invYaml.save(invFile);
                                } catch (IOException err) {
                                    err.printStackTrace();
                                }
                                e.getWhoClicked().sendMessage(pl.badge + " Icon set for " + type + " " + slot);
                                Bukkit.dispatchCommand(e.getWhoClicked(), "" + type + "s");
                            } else {
                                e.getWhoClicked().sendMessage(pl.badge + " You must hold an item to use as an icon in your main hand");
                            }
                        } else if (e.getInventory().getItem(e.getSlot()).getType() == Material.BARRIER) {
                            Bukkit.dispatchCommand(e.getWhoClicked(), "" + type + "s");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String world = p.getWorld().getName().split("_")[0];
        if (pl.getConfig().getBoolean("ListeningTo." + p.getUniqueId().toString())) {
            e.setCancelled(true);
            String type = pl.getConfig().getString("ValuesFor." + p.getUniqueId().toString() + ".type").toLowerCase();
            String slot = pl.getConfig().getString("ValuesFor." + p.getUniqueId().toString() + ".number");
            File invFile = new File(pl.getDataFolder() + File.separator + type + File.separator, p.getUniqueId().toString() + "_" + type + "_" + slot + "_" + world + ".yml");
            FileConfiguration invYaml = YamlConfiguration.loadConfiguration(invFile);
            invYaml.set("title", e.getMessage().replace(" ", "_").replace("'", "").replace("/", "").replace("\"", ""));
            pl.getConfig().set("ListeningTo." + p.getUniqueId().toString(), null);
            pl.getConfig().set("ValuesFor." + p.getUniqueId().toString(), null);
            p.sendMessage(pl.badge + " Name set for " + type + " " + slot);

            try {
                invYaml.save(invFile);
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }
}
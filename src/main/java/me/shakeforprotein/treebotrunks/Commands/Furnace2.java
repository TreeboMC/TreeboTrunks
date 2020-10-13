package me.shakeforprotein.treebotrunks.Commands;

import me.shakeforprotein.treebotrunks.TreeboTrunk;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.Iterator;

public class Furnace2 implements CommandExecutor, Listener {


    public static HashMap<String, ItemStack> smeltItemHash = new HashMap<>();
    public static HashMap<String, ItemStack> fuelItemHash = new HashMap<>();
    public static HashMap<String, ItemStack> outputHash = new HashMap<>();
    public static HashMap<String, Double> fuelRemainingHash = new HashMap<>();
    public static HashMap<String, Integer> grillHash = new HashMap<>();
    private static HashMap<Material, Integer> fuelValueHash = new HashMap<>();
    private TreeboTrunk pl;

    //TODO - WRITE Method to save hashes to config.
    //TODO - WRITE Method to read hashes from config.
    //TODO - Add the rest of the fuel types that burn for integers, but exclude doubles


    public Furnace2(TreeboTrunk main) {
        this.pl = main;

        // load hash values from config.

        for (Material mat : Material.values()) {
            if(mat.name().equalsIgnoreCase("BOWL") || mat.name().contains("SAPLING") || mat.name().contains("WOOL")){
                fuelValueHash.putIfAbsent(mat, 5);
            }

            if(mat.name().equalsIgnoreCase("WOODEN_PICKAXE") || mat.name().equalsIgnoreCase("WOODEN_SHOVEL") || mat.name().equalsIgnoreCase("WOODEN_SWORD") || mat.name().equalsIgnoreCase("WOODEN_AXE") || mat.name().equalsIgnoreCase("WOODEN_HOE")){
                fuelValueHash.putIfAbsent(mat, 10);
            }

            if (mat.name().contains("_LOG") || mat.name().contains("_WOOD") || mat.name().contains("_PLANKS") || mat.name().contains("FENCE") || mat.name().contains("GATE")
                    || mat.name().contains("LADDER")
                    || mat.name().equalsIgnoreCase("CRAFTING_TABLE")
                    || mat.name().equalsIgnoreCase("CARTOGRAPHY_TABLE")
                    || mat.name().equalsIgnoreCase("FLETCHING_TABLE")
                    || mat.name().equalsIgnoreCase("LOOM")
                    || mat.name().equalsIgnoreCase("SMITHING_TABLE")
                    || mat.name().contains("BOOKSHELF")
                    || mat.name().contains("LECTERN")
                    || mat.name().contains("COMPOSTER")
                    || mat.name().contains("CHEST")
                    || mat.name().contains("BARREL")
                    || mat.name().contains("DAYLIGHT_SENSOR")
                    || mat.name().contains("JUKEBOX")
                    || mat.name().contains("NOTE_BLOCK")
                    || mat.name().contains("BANNER")
                    || mat.name().equalsIgnoreCase("CROSSBOW")
                    || mat.name().equalsIgnoreCase("BOW")
                    || mat.name().contains("FISHING_ROD")
                    || mat.name().contains("SIGN")) {
                fuelValueHash.putIfAbsent(mat, 15);
            }

            if (mat.name().equalsIgnoreCase("DRIED_KELP_BLOCK")) {
                fuelValueHash.putIfAbsent(mat, 20);
            }

            if(mat.name().equalsIgnoreCase("COAL") || mat.name().equalsIgnoreCase("CHARCOAL")){
                fuelValueHash.putIfAbsent(mat, 80);
            }
            if(mat.name().equalsIgnoreCase("COAL_BLOCK")){
                fuelValueHash.putIfAbsent(mat, 800);
            }
            if(mat.name().toUpperCase().contains("BOAT")){
                fuelValueHash.putIfAbsent(mat, 60);
            }
            if(mat.name().toUpperCase().contains("BLAZE")){
                fuelValueHash.putIfAbsent(mat, 120);
            }
            if(mat.name().equalsIgnoreCase("Scaffolding")){
                fuelValueHash.putIfAbsent(mat, 20);
            }
        }


        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {

                tickFurnace();

            }
        }, 1L, 10);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;

            openVirtualFurnace(p);
            p.sendMessage("Current fuel level - " + getFuelLevel(p) / 10);
        }
        return true;
    }


    public void tickFurnace() {
        //Cook time is 1 item every 10 seconds, or 200 ticks

        for (Player p : Bukkit.getOnlinePlayers()) {

            ItemStack itemToSmelt = getSmeltable(p);
            ItemStack outputItem = getOutputItem(p);
            ItemStack smeltResult = canSmelt(p);
            Double fuelRemaining = getFuelLevel(p);
            if(fuelRemaining == null){
                fuelRemaining = 0.0;
            }
            Integer smeltValue = 0;
            if (grillHash.containsKey(p.getUniqueId().toString())) {
                smeltValue = grillHash.get(p.getUniqueId().toString());
            } else {
                grillHash.put(p.getUniqueId().toString(), 0);
            }

            boolean didSmelt = false;

            if (itemToSmelt != null && !(itemToSmelt.getType().isAir())) {
                if (outputItem == null || (outputItem.getType().isAir() || outputItem.getType() == canSmelt(p).getType())) {
                    if(outputItem == null){outputItem = new ItemStack(Material.AIR);}
                    if (outputItem.getType().isAir() || outputItem.getAmount() < outputItem.getMaxStackSize()) {
                        if (fuelRemaining > 0) {
                            if (smeltValue == 9) {
                                smeltValue = 0;
                                fuelRemaining = fuelRemaining - 1;
                                itemToSmelt.setAmount(itemToSmelt.getAmount() - 1);
                                if (outputItem.getType().isAir()) {
                                    outputItem = new ItemStack(canSmelt(p));
                                } else {
                                    outputItem = new ItemStack(outputItem.getType(), outputItem.getAmount() + 1);
                                }
                                smeltItemHash.put(p.getUniqueId().toString(), itemToSmelt);
                                outputHash.put(p.getUniqueId().toString(), outputItem);
                                fuelRemainingHash.put(p.getUniqueId().toString(), fuelRemaining);
                                grillHash.put(p.getUniqueId().toString(), smeltValue);
                                if (p.getOpenInventory().getTitle().equalsIgnoreCase(pl.badge + "Private Furnace - " + p.getUniqueId().toString())) {
                                    p.getOpenInventory().getTopInventory().setItem(0, new ItemStack(itemToSmelt));
                                    p.getOpenInventory().getTopInventory().setItem(2, new ItemStack(outputHash.get(p.getUniqueId().toString())));
                                }
                                didSmelt = true;
                            } else {
                                smeltValue = smeltValue + 1;
                                fuelRemaining = fuelRemaining - 1;
                                fuelRemainingHash.put(p.getUniqueId().toString(), fuelRemaining);
                                grillHash.put(p.getUniqueId().toString(), smeltValue);
                                didSmelt = false;
                            }
                        }
                    }
                }
            }
        }
    }

    private ItemStack getOutputItem(Player p) {
        ItemStack outputItem = new ItemStack(Material.AIR);
        if (p.getOpenInventory().getTitle().equalsIgnoreCase(pl.badge + "Private Furnace - " + p.getUniqueId().toString())) {
            return p.getOpenInventory().getTopInventory().getItem(2);
        } else {
            if (outputHash.get(p.getUniqueId().toString()) != null) {
                return outputHash.get(p.getUniqueId().toString());
            }
        }
        return outputItem;
    }

    private ItemStack getSmeltable(Player p) {
        ItemStack returnItem = new ItemStack(Material.AIR);
        if (p.getOpenInventory().getTitle().equalsIgnoreCase(pl.badge + "Private Furnace - " + p.getUniqueId().toString())) {
            smeltItemHash.put(p.getUniqueId().toString(), returnItem);
            return p.getOpenInventory().getTopInventory().getItem(0);
        } else if (smeltItemHash.containsKey(p.getUniqueId().toString())) {
            return smeltItemHash.get(p.getUniqueId().toString());
        }
        return returnItem;
    }

    private ItemStack getFuelItem(Player p) {
        ItemStack fuelItem = new ItemStack(Material.AIR);
        if (p.getOpenInventory().getTitle().equalsIgnoreCase(pl.badge + "Private Furnace - " + p.getUniqueId().toString())) {
            fuelItem = p.getOpenInventory().getTopInventory().getItem(1);
            fuelItemHash.put(p.getUniqueId().toString(), fuelItem);
        } else if (fuelItemHash.get(p.getUniqueId().toString()) != null) {
            fuelItem = fuelItemHash.get(p.getUniqueId().toString());
        }
        return fuelItem;
    }

    private Double getFuelLevel(Player p) {
        Double fuelLevel = 0.0;
        if (fuelRemainingHash.containsKey(p.getUniqueId().toString())) {
            fuelLevel = fuelRemainingHash.get(p.getUniqueId().toString());
            if (fuelLevel == 0.0) {
                fuelLevel = fuelLevel + consumeFuel(p);
                fuelRemainingHash.put(p.getUniqueId().toString(), fuelLevel);
            } else {
                fuelLevel = fuelRemainingHash.get(p.getUniqueId().toString());
            }
        } else {
            fuelRemainingHash.put(p.getUniqueId().toString(), 0.0);
        }
        return fuelLevel;
    }

    private double consumeFuel(Player p) {
        double newFuel = 0.0;
        ItemStack fuelItem = getFuelItem(p);

        if (fuelRemainingHash.containsKey(p.getUniqueId().toString())) {
            newFuel = fuelRemainingHash.get(p.getUniqueId().toString());
        } else {
            fuelRemainingHash.put(p.getUniqueId().toString(), newFuel);
        }

        if (newFuel == 0) {
            if (fuelItem != null) {
                if (fuelValueHash.containsKey(fuelItem.getType())) {
                    newFuel = fuelValueHash.get(fuelItem.getType());
                    if (p.getOpenInventory().getTitle().equalsIgnoreCase(pl.badge + "Private Furnace - " + p.getUniqueId().toString())) {
                        p.getOpenInventory().setItem(1, new ItemStack(fuelItem.getType(), fuelItem.getAmount() - 1));
                        fuelItemHash.put(p.getUniqueId().toString(), p.getOpenInventory().getItem(1));
                    } else {
                        fuelItemHash.put(p.getUniqueId().toString(), new ItemStack(fuelItem.getType(), fuelItem.getAmount() - 1));
                    }
                }
            }
        }
        return newFuel;
    }

    public ItemStack canSmelt(Player p) {
        ItemStack item = getSmeltable(p);
        ItemStack result = null;
        if (item != null) {
            Iterator<Recipe> iter = Bukkit.recipeIterator();
            while (iter.hasNext()) {
                Recipe recipe = iter.next();
                if (!(recipe instanceof CookingRecipe)) continue;
                if (((CookingRecipe) recipe).getInputChoice().test(new ItemStack(item.getType()))) {
                    result = recipe.getResult();
                    break;
                }
            }
        }
        return result;
    }


    private void openVirtualFurnace(Player p) {
        Inventory furnace = Bukkit.createInventory(p, InventoryType.FURNACE, pl.badge + "Private Furnace - " + p.getUniqueId().toString());

        if (smeltItemHash.get(p.getUniqueId().toString()) != null) {
            furnace.setItem(0, smeltItemHash.get(p.getUniqueId().toString()));
        }
        if (fuelItemHash.containsKey(p.getUniqueId().toString()) && fuelItemHash.get(p.getUniqueId().toString()) != null && !fuelItemHash.get(p.getUniqueId().toString()).getType().isAir()) {
            furnace.setItem(1, fuelItemHash.get(p.getUniqueId().toString()));
        }
        if (outputHash.containsKey(p.getUniqueId().toString()) && outputHash.get(p.getUniqueId().toString()) != null && !outputHash.get(p.getUniqueId().toString()).getType().isAir()) {
            furnace.setItem(2, outputHash.get(p.getUniqueId().toString()));
        }

        p.openInventory(furnace);
    }


    @EventHandler
    public void onInventClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase(pl.badge + "Private Furnace - " + e.getPlayer().getUniqueId().toString())) {
            smeltItemHash.put(e.getPlayer().getUniqueId().toString(), e.getView().getTopInventory().getItem(0));
            fuelItemHash.put(e.getPlayer().getUniqueId().toString(), e.getInventory().getItem(1));
            outputHash.put(e.getPlayer().getUniqueId().toString(), e.getInventory().getItem(2));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String u = e.getPlayer().getUniqueId().toString();
        if (pl.getConfig().get("Hashes.SmeltItemHash." + u) != null) {
            smeltItemHash.put(u, pl.getConfig().getItemStack("Hashes.SmeltItemHash." + u));
        } else {
            smeltItemHash.put(e.getPlayer().getUniqueId().toString(), new ItemStack(Material.AIR));
        }

        if (pl.getConfig().get("Hashes.FuelItemHash." + u) != null) {
            fuelItemHash.put(u, pl.getConfig().getItemStack("Hashes.FuelItemHash." + u));
        } else {
            fuelItemHash.put(u, new ItemStack(Material.AIR));
        }

        if (pl.getConfig().get("Hashes.OutputHash." + u) != null) {
            outputHash.put(u, pl.getConfig().getItemStack("Hashes.OutputHash." + u));
        } else {
            outputHash.put(u, new ItemStack(Material.AIR));
        }

        if (pl.getConfig().get("Hashes.FuelRemainingHash." + u) != null) {
            fuelRemainingHash.put(u, pl.getConfig().getDouble("Hashes.FuelRemainingHash." + u));
        } else {
            fuelRemainingHash.put(u, 0.0);
        }

        if (pl.getConfig().get("Hashes.GrillHash." + u) != null) {
            grillHash.put(u, pl.getConfig().getInt("Hashes.GrillHash." + u));
        } else {
            grillHash.put(u, 0);
        }
    }

    public void saveHashes(Player p) {
        String u = p.getUniqueId().toString();
        pl.getConfig().set("Hashes.SmeltItemHash." + u, smeltItemHash.get(u));
        pl.getConfig().set("Hashes.FuelItemHash." + u, fuelItemHash.get(u));
        pl.getConfig().set("Hashes.OutputHash." + u, outputHash.get(u));
        pl.getConfig().set("Hashes.FuelRemainingHash." + u, fuelRemainingHash.get(u));
        pl.getConfig().set("Hashes.GrillHash." + u, grillHash.get(u));
        pl.saveConfig();
    }

    @EventHandler
    public void onClickInvent(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase(pl.badge + "Private Furnace - " + e.getWhoClicked().getUniqueId().toString())) {
            if (e.getClickedInventory().getSize() == 3) {
                Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                    @Override
                    public void run() {

                        if (e.getSlot() == 0) {
                            smeltItemHash.put(e.getWhoClicked().getUniqueId().toString(), e.getClickedInventory().getItem(0));
                        }
                        if (e.getSlot() == 1) {
                            fuelItemHash.put(e.getWhoClicked().getUniqueId().toString(), e.getClickedInventory().getItem(1));
                        }
                        if (e.getSlot() == 2) {
                            outputHash.put(e.getWhoClicked().getUniqueId().toString(), e.getClickedInventory().getItem(2));
                        }
                    }

                }, 1L);
            }
        }
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent e) {
        saveHashes(e.getPlayer());
    }
}

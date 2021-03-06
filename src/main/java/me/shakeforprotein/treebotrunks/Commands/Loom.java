package me.shakeforprotein.treebotrunks.Commands;

import com.sk89q.worldguard.LocalPlayer;
import me.shakeforprotein.treebotrunks.TreeboTrunk;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Loom implements CommandExecutor {


    private TreeboTrunk pl;

    public Loom(TreeboTrunk main) {
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (pl.whitelistHash.containsKey(p.getLocation().getBlock().getType())) {
                LocalPlayer wgPlayer = pl.worldGuardPlugin.wrapPlayer(p);

                if (pl.hasBypass(wgPlayer, p.getLocation()) || pl.canBuild(wgPlayer, p.getLocation())) {
                    if (!p.getLocation().subtract(0, 1, 0).getBlock().isEmpty()) {
                        if (!p.getLocation().subtract(0, 1, 0).getBlock().isLiquid()) {
                            long time = System.currentTimeMillis();
                            Location l = p.getLocation().getBlock().getLocation();
                            pl.getConfig().set("Looms." + time + ".X", l.getBlockX());
                            pl.getConfig().set("Looms." + time + ".Y", l.getBlockY());
                            pl.getConfig().set("Looms." + time + ".Z", l.getBlockZ());
                            pl.getConfig().set("Looms." + time + ".World", l.getWorld().getName());

                            p.getLocation().getBlock().setType(Material.LOOM);

                            ArmorStand as = (ArmorStand) l.getWorld().spawnEntity(l.add(0.5, -0.5, 0.5), EntityType.ARMOR_STAND); //Spawn the ArmorStand

                            int delay = 600;
                            as.setGravity(false); //Make sure it doesn't fall
                            as.setCanPickupItems(false); //I'm not sure what happens if you leave this as it is, but you might as well disable it
                            as.setCustomName((delay / 20) + ""); //Set this to the text you want
                            as.setCustomNameVisible(true); //This makes the text appear no matter if your looking at the entity or not
                            as.setVisible(false);
                            pl.getConfig().set("Looms." + time + ".AST", as.getUniqueId());
                            pl.astHash.putIfAbsent(as, delay / 20);
                            BukkitRunnable runnable = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    int currentVal = pl.astHash.get(as);
                                    if (currentVal > 0) {
                                        currentVal--;
                                        String newName = currentVal + "";
                                        as.setCustomName(ChatColor.GOLD + newName);
                                        pl.astHash.replace(as, currentVal);
                                    } else {
                                        this.cancel();
                                    }
                                }
                            };
                            runnable.runTaskTimer(pl, 20L, 20L);
                            Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                                @Override
                                public void run() {
                                    int x, y, z = 0;
                                    x = pl.getConfig().getInt("Looms." + time + ".X");
                                    y = pl.getConfig().getInt("Looms." + time + ".Y");
                                    z = pl.getConfig().getInt("Looms." + time + ".Z");
                                    String world = pl.getConfig().getString("Looms." + time + ".World");
                                    Location loc = new Location(Bukkit.getWorld(world), x, y, z);
                                    loc.getBlock().setType(Material.AIR);
                                    if (pl.getConfig().getString("Looms." + time + ".AST") != null) {
                                        Bukkit.getEntity(UUID.fromString(pl.getConfig().getString("Looms." + time + ".AST"))).remove();
                                    }
                                    pl.getConfig().set("Looms." + time, null);
                                }
                            }, 600L);
                        }
                    }
                } else {
                    p.sendMessage(pl.badge + pl.err + "You are not entitled to build here");
                }
            }
        } else {
            sender.sendMessage(pl.badge + pl.err + "You must be a player to use this command");
        }
        return true;
    }
}

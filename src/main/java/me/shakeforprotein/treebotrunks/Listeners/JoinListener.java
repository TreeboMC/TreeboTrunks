package me.shakeforprotein.treebotrunks.Listeners;

import me.shakeforprotein.treebotrunks.TreeboTrunk;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.awt.*;

import static me.shakeforprotein.treebotrunks.TreeboTrunk.*;

public class JoinListener implements Listener {

    private TreeboTrunk pl;

    public JoinListener(TreeboTrunk main) {
        this.pl = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("treebotrunk.admin") && pl.getConfig().getBoolean("checkUpdates") && pl.requiresUpdate) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(pl, new Runnable() {
                @Override
                public void run() {
                    if (pl.getConfig().getString("updateChecker").equalsIgnoreCase("spigot")) {
                        if (!pl.notifyHash.containsKey(event.getPlayer())) {
                            net.md_5.bungee.api.chat.TextComponent updateMessage = new net.md_5.bungee.api.chat.TextComponent(pl.badge + ChatColor.RED + "is out of date." + ChatColor.BOLD + " [Update - " + spigotVersion + "]");
                            ClickEvent updateClickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/treebo-trunks.73787/updates");
                            updateMessage.setClickEvent(updateClickEvent);
                            event.getPlayer().spigot().sendMessage(updateMessage);
                            pl.notifyHash.putIfAbsent(event.getPlayer(), true);
                        }
                    }
                }
            }, 150L);
        }
    }
}

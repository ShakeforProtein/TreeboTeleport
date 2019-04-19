package me.shakeforprotein.treeboteleport.Listeners;

import me.shakeforprotein.treeboteleport.TreeboTeleport;
import me.shakeforprotein.treeboteleport.UpdateChecker.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;


public class PlayerJoinListener implements Listener {

    private TreeboTeleport pl;
    private UpdateChecker uc;

    public PlayerJoinListener(TreeboTeleport main) {
        this.pl = main;
        this.uc = new UpdateChecker(main);
    }

    @EventHandler
    public boolean onPlayerJoin(PlayerJoinEvent e) {
        if (pl.getConfig().isSet("onJoinSpawn." + e.getPlayer().getWorld().getName() + ".world")) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
                public void run() {
                    System.out.println("Send to spawn triggered");
                    String world = pl.getConfig().getString("onJoinSpawn." + e.getPlayer().getWorld().getName() + ".world");
                    int x = Math.toIntExact(pl.getConfig().getInt("onJoinSpawn." + e.getPlayer().getWorld().getName() + ".x"));
                    int y = Math.toIntExact(pl.getConfig().getInt("onJoinSpawn." + e.getPlayer().getWorld().getName() + ".y"));
                    int z = Math.toIntExact(pl.getConfig().getInt("onJoinSpawn." + e.getPlayer().getWorld().getName() + ".z"));
                    float pitch = Math.toIntExact(pl.getConfig().getInt("onJoinSpawn." + e.getPlayer().getWorld().getName() + ".pitch"));
                    float yaw = Math.toIntExact(pl.getConfig().getInt("onJoinSpawn." + e.getPlayer().getWorld().getName() + ".yaw"));
                    Location spawnLoc = new Location(Bukkit.getWorld(world), x, y, z, pitch, yaw);
                    e.getPlayer().teleport(spawnLoc);

                }
            }, 20L);
        }
        if (pl.getConfig().getBoolean("isHubServer")) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
                public void run() {
                    Player p = e.getPlayer();
                    Inventory inv = p.getInventory();
                    ItemStack hubItem = pl.getHubItem();
                    if (!inv.contains(hubItem)) {
                        inv.addItem(hubItem);
                    }
                }
            }, 30L);

            if (e.getPlayer().hasPermission(uc.requiredPermission)) {
                uc.getCheckDownloadURL(e.getPlayer());
                pl.getConfig().set(e.getPlayer().getName(), "true");
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
                    public void run() {
                        pl.getConfig().set(e.getPlayer().getName(), "false");
                    }
                }, 75L);
            }
        }


        return true;
    }

    @EventHandler
    public boolean onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        if (pl.getConfig().get("tptoggle." + e.getPlayer().getName()) == null) {
            pl.getConfig().set("tptoggle." + e.getPlayer().getName(), 0);
        }
        if (pl.getConfig().get(e.getPlayer().getWorld().getName() + ".forceSpawnOnJoin") == null) {
            pl.getConfig().set(e.getPlayer().getWorld().getName() + ".forceSpawnOnJoin", false);
        }
        if (pl.getConfig().getString(e.getPlayer().getWorld().getName() + ".forceSpawnOnJoin").equalsIgnoreCase("true")) {
            File spawnsYml = new File(pl.getDataFolder(), File.separator + "spawns.yml");
            FileConfiguration spawns = YamlConfiguration.loadConfiguration(spawnsYml);
            Player p = e.getPlayer();
            String world = p.getWorld().getName();

            if (spawns.get("spawns." + world + ".x") != null) {
                Bukkit.dispatchCommand(e.getPlayer(), "spawn");
            } else {
                System.out.println("Tried to send player: " + p.getName() + " : to spawn on world : " + world + " : but no spawn information was found in the spawn.yml");
            }
        }
        return true;
    }
}
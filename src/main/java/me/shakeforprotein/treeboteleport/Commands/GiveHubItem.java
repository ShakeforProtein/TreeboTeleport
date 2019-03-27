package me.shakeforprotein.treeboteleport.Commands;

import me.shakeforprotein.treeboteleport.TreeboTeleport;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class GiveHubItem implements CommandExecutor {

    private TreeboTeleport pl;

    public GiveHubItem(TreeboTeleport main){
        this.pl = main;
    }



    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (pl.getCD((Player) sender)) {
            Player p = (Player) sender;
            Inventory inv = p.getInventory();
            ItemStack hubItem = pl.getHubItem();
            if (!inv.contains(hubItem)) {
                inv.addItem(hubItem);
            }
        }
        return true;
    }


}

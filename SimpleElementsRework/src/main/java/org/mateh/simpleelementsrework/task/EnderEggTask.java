package org.mateh.simpleelementsrework.task;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.simpleelementsrework.Main;

import java.util.Arrays;

public class EnderEggTask extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory inventory = player.getInventory();
            boolean hasEnderEgg = Arrays.stream(inventory.getContents())
                    .anyMatch(item -> item != null && item.getType() == Material.DRAGON_EGG);

            if (hasEnderEgg) {
                if (!Main.getInstance().getPlayerDataManager().getElement(player.getUniqueId()).equalsIgnoreCase("Lightning")) {
                    Main.getInstance().getPlayerDataManager().setBeforeElement(player.getUniqueId(), Main.getInstance().getPlayerDataManager().getElement(player.getUniqueId()));
                    Main.getInstance().getPlayerDataManager().setElement(player.getUniqueId(), "Lightning");
                }
            } else {
                if (Main.getInstance().getPlayerDataManager().getElement(player.getUniqueId()).equalsIgnoreCase("Lightning")) {
                    String before = Main.getInstance().getPlayerDataManager().getBeforeElement(player.getUniqueId());
                    Main.getInstance().getPlayerDataManager().setElement(player.getUniqueId(), before);
                }
            }
        }
    }
}

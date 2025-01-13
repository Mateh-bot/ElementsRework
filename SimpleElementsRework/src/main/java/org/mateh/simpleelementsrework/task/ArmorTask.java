package org.mateh.simpleelementsrework.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.simpleelementsrework.Main;

public class ArmorTask extends BukkitRunnable {
    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()){
            Main.getInstance().getPlayerListeners().addInvisibleEntity(player);
        }
    }
}

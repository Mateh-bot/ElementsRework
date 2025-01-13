package org.mateh.simpleelementsrework.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.simpleelementsrework.Main;

public class EffectTask extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String element = Main.getInstance().getPlayerDataManager().getElement(player.getUniqueId());
            if (element != null) {

                switch (element) {
                    case "Fire":
                        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0, true, false));
                        continue;
                    case "Water":
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 200, 0, true, false));
                        continue;
                    case "Earth":
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0, true, false));
                        continue;
                    case "Air":
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0, true, false));
                        continue;
                    case "Lightning":
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1, true, false));
                }
            }
        }
    }
}

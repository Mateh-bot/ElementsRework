package org.mateh.simpleelementsrework.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.Map;

public class PlayerListeners implements Listener {
    Main main;
    Map<Player, BukkitTask> tasks = new HashMap<>();

    public PlayerListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            ItemStack item = event.getItem();
            if (item == null || item.getType() == Material.AIR) return;

            if (item.getType() == Material.DIAMOND_SWORD) {
                for (Abilities abilities : main.getAbilities()) {
                    abilities.startAbilities(event, event.getPlayer());
                }
            }
        }
    }
}

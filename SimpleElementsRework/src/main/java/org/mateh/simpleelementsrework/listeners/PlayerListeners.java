package org.mateh.simpleelementsrework.listeners;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListeners implements Listener {
    Main main;
    Map<Player, BukkitTask> tasks = new HashMap<>();

    public PlayerListeners(Main main) {
        this.main = main;
    }


    public final Map<UUID, ArmorStand> playerEntities = new HashMap<>();


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            for (Abilities abilities : main.getAbilities()) {
                abilities.startAbilities(event, event.getPlayer());
            }
        }
    }



    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        addInvisibleEntity(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteractWithEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();
        if (playerEntities.containsKey(player.getUniqueId()) && playerEntities.get(player.getUniqueId()).equals(clickedEntity)) {
            event.setCancelled(true);
            PlayerInteractEvent eventNew = new PlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, null, null, BlockFace.SELF);
            for (Abilities abilities : main.getAbilities()) {
                abilities.startAbilities(eventNew, event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(playerEntities.values().stream().anyMatch(e->e.getUniqueId().equals(e.getUniqueId()))){
            if(event.getDamager() instanceof Player player){
                ArmorStand armorStand = playerEntities.get(player.getUniqueId());
                if(armorStand != null) {
                    if (armorStand.equals(event.getEntity())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private void placeTemporaryBlock(Player player) {
        Location location = player.getLocation().add(player.getEyeLocation().getDirection().multiply(1.5)).add(0, 2, 0);
        location.setY(location.getBlockY()); // Ajustar al nivel del jugador

        if(location.getBlock().getType() == Material.AIR) {

            // Bloque temporal
            Material tempBlockMaterial = Material.BARRIER;

            // Guardar el bloque original y reemplazarlo
            Material originalBlock = location.getBlock().getType();
            location.getBlock().setType(tempBlockMaterial);

            // Programar la restauración del bloque
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (location.getBlock().getType() == tempBlockMaterial) {
                        location.getBlock().setType(originalBlock);
                    }
                }
            }.runTaskLater(main, 6L); // Restaurar después de 2 ticks
        }
    }


    public void addInvisibleEntity(Player player) {
        // Si ya tiene una entidad, no la recrees
        if (playerEntities.containsKey(player.getUniqueId())) {
            return;
        }

        if (player.getInventory().getItemInMainHand().getType() != Material.AIR){
            return;
        }

        // Crea una entidad invisible (ArmorStand)
        Location location = player.getLocation().add(player.getEyeLocation().getDirection().multiply(0.75)).add(0, 0.2, 0); // Frente al jugador
        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setCollidable(false);
        armorStand.setMarker(false);

        playerEntities.put(player.getUniqueId(), armorStand);


        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !playerEntities.containsKey(player.getUniqueId()) || player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    playerEntities.remove(player.getUniqueId());
                    armorStand.remove();
                    cancel();
                    return;
                }

                ArmorStand stand = playerEntities.get(player.getUniqueId());
                Location updatedLocation = player.getLocation().add(player.getEyeLocation().getDirection().multiply(1.5));
                stand.teleport(updatedLocation);
            }
        }.runTaskTimer(main, 0L, 1L); // Actualiza cada tick
    }


}

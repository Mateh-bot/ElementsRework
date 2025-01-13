package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;

public class BoulderToss extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final double DAMAGE = 3.0;
    private static final double RADIUS = 1.5;

    public BoulderToss(Main main) {
        super("Boulder Toss", "Earth", main, AbilitiesSlot.SECONDARY);
    }

    @Override
    public void startAbilities(PlayerInteractEvent event, Player caster) {
        if (!isElement(caster)) {
            return;
        }

        if (!isRightShift(event.getAction(), caster)) {
            return;
        }

        UUID playerId = caster.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(playerId)) {
            long lastUsed = cooldowns.get(playerId);
            long timeLeft = (lastUsed + COOLDOWN_TIME * 1000) - currentTime;

            if (timeLeft > 0) {
                caster.sendMessage(ChatColor.RED + "Boulder Toss is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        Location origin = caster.getEyeLocation();
        Vector direction = origin.getDirection().normalize().multiply(0.5);

        new BukkitRunnable() {
            Location currentLocation = origin.clone();

            @Override
            public void run() {
                currentLocation.add(direction);

                caster.getWorld().spawnParticle(Particle.SMOKE_LARGE, currentLocation, 10, 0.2, 0.2, 0.2, 0.01);
                caster.getWorld().playSound(currentLocation, Sound.BLOCK_STONE_HIT, 1.0f, 1.0f);

                for (Entity entity : caster.getWorld().getNearbyEntities(currentLocation, RADIUS, RADIUS, RADIUS)) {
                    if (entity instanceof LivingEntity target && !entity.equals(caster)) {
                        target.damage(DAMAGE, caster);

                        target.getWorld().spawnParticle(Particle.BLOCK_CRACK, target.getLocation(), 20, 0.3, 0.3, 0.3, Material.STONE.createBlockData());
                        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

                        this.cancel();
                        return;
                    }
                }

                if (currentLocation.getBlock().getType().isSolid()) {
                    caster.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, currentLocation, 1, 0.5, 0.5, 0.5, 0.1);
                    caster.getWorld().playSound(currentLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 2);

        // Set cooldown
        cooldowns.put(playerId, currentTime);

        // Notify cooldown expiration
        caster.sendMessage(ChatColor.YELLOW + "Boulder Toss is now on cooldown for " + COOLDOWN_TIME + " seconds.");
    }

    @Override
    public int getCooldown(Player player) {
        if (cooldowns.containsKey(player.getUniqueId())) {
            long lastUsed = cooldowns.get(player.getUniqueId());
            long timeLeft = (lastUsed + COOLDOWN_TIME * 1000) - System.currentTimeMillis();
            if (timeLeft > 0) {
                return (int) (timeLeft / 1000);
            }
        }
        return 0;
    }

    @Override
    public String getName() {
        return super.getName();
    }
}

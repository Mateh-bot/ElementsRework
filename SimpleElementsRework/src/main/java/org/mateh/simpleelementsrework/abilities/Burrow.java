package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;

public class Burrow extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final double KNOCKBACK = 1.5;
    private static final double RADIUS = 1;

    public Burrow(Main main) {
        super("Burrow", "Earth", main, AbilitiesSlot.FOURTH);
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
                caster.sendMessage(ChatColor.RED + "Burrow is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        Location casterLocation = caster.getLocation();
        Vector direction = caster.getLocation().getDirection().normalize();

        double particleDistance = 10.0;
        double particleStep = 0.5;

        for (double i = 0; i < particleDistance; i += particleStep) {
            Location particleLocation = casterLocation.clone().add(direction.clone().multiply(i));

            Material material = Material.STONE;
            BlockData blockData = material.createBlockData();

            caster.getWorld().spawnParticle(Particle.BLOCK_CRACK, particleLocation, 10, blockData);

            for (Entity entity : particleLocation.getWorld().getNearbyEntities(particleLocation, RADIUS, RADIUS, RADIUS)) {
                if (entity instanceof Player && entity != caster) {
                    LivingEntity target = (LivingEntity) entity;

                    Vector knockbackDirection = target.getLocation().toVector().subtract(casterLocation.toVector()).normalize();
                    knockbackDirection.setY(0.5);
                    target.setVelocity(knockbackDirection.multiply(KNOCKBACK));

                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);

                    i = particleDistance;
                    break;
                }
            }
        }

        caster.getWorld().playSound(casterLocation, Sound.BLOCK_STONE_BREAK, 1.0f, 1.0f);

        // Set cooldown
        cooldowns.put(playerId, currentTime);

        // Notify cooldown expiration
        caster.sendMessage(ChatColor.YELLOW + "Burrow is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;

public class InfernalShield extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final double DAMAGE_PER_SECOND = 2.5 / 5;
    private static final int DURATION = 3;
    private static final double RADIUS = 3;

    public InfernalShield(Main main) {
        super("Infernal Shield", "Fire", main, AbilitiesSlot.THIRD);
    }

    @Override
    public void startAbilities(PlayerInteractEvent event, Player caster) {
        if (!isElement(caster)) {
            return;
        }

        if (!isLeftShift(event.getAction(), caster)) {
            return;
        }

        UUID playerId = caster.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(playerId)) {
            long lastUsed = cooldowns.get(playerId);
            long timeLeft = (lastUsed + COOLDOWN_TIME * 1000) - currentTime;

            if (timeLeft > 0) {
                caster.sendMessage(ChatColor.RED + "Infernal Shield is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        caster.sendMessage(ChatColor.AQUA + "Infernal Shield activated! You gain damage reduction and damage nearby enemies.");
        Location origin = caster.getLocation();

        double originalDamageReduction = caster.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ARMOR).getBaseValue();
        caster.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ARMOR).setBaseValue(originalDamageReduction + (originalDamageReduction * 0.3));

        caster.getWorld().playSound(origin, Sound.BLOCK_ANVIL_PLACE, 1.0f, 0.8f);
        caster.getWorld().playSound(origin, Sound.ITEM_ARMOR_EQUIP_DIAMOND, 1.0f, 0.8f);

        new BukkitRunnable() {
            int secondsElapsed = 0;

            @Override
            public void run() {
                if (secondsElapsed >= DURATION) {
                    this.cancel();
                    return;
                }

                caster.getWorld().spawnParticle(Particle.REDSTONE, origin, 50, RADIUS, 1, RADIUS, new Particle.DustOptions(Color.PURPLE, 1.0f));

                for (Entity entity : caster.getWorld().getNearbyEntities(origin, RADIUS, RADIUS, RADIUS)) {
                    if (entity instanceof LivingEntity target && !entity.equals(caster)) {

                        if (entity instanceof Player targetP) {
                            targetP.setHealth(Math.max(targetP.getHealth() - ((double) DAMAGE_PER_SECOND / 2), 0)); // Reduce health but don't go below 0
                        }
                        target.damage(DAMAGE_PER_SECOND, caster);
                        target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, target.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
                    }
                }
                secondsElapsed++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);

        // Set cooldown
        cooldowns.put(playerId, currentTime);

        // Notify cooldown expiration
        caster.sendMessage(ChatColor.YELLOW + "Infernal Shield is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

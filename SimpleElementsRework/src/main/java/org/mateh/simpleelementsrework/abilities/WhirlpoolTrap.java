package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
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

public class WhirlpoolTrap extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final double DAMAGE_PER_SECOND = 2.5 / 5;
    private static final int DURATION = 3;
    private static final double RADIUS = 3;

    public WhirlpoolTrap(Main main) {
        super("Whirlpool Trap", "Water", main, AbilitiesSlot.FIVE);
    }

    @Override
    public void startAbilities(PlayerInteractEvent event, Player caster) {
        if (!isElement(caster)) {
            return;
        }

        if (!isRightShiftSword(event.getAction(), caster)) {
            return;
        }

        UUID playerId = caster.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(playerId)) {
            long lastUsed = cooldowns.get(playerId);
            long timeLeft = (lastUsed + COOLDOWN_TIME * 1000) - currentTime;

            if (timeLeft > 0) {
                caster.sendMessage(ChatColor.RED + "Whirlpool Trap is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        Location origin = caster.getLocation();
        caster.getWorld().playSound(origin, Sound.BLOCK_WATER_AMBIENT, 1.5f, 1.0f);

        new BukkitRunnable() {
            int secondsElapsed = 0;

            @Override
            public void run() {
                if (secondsElapsed >= DURATION) {
                    this.cancel();
                    return;
                }

                for (int angle = 0; angle < 360; angle += 20) {
                    double radians = Math.toRadians(angle);
                    double x = RADIUS * Math.cos(radians);
                    double z = RADIUS * Math.sin(radians);
                    double y = 0.5 * Math.sin((secondsElapsed + angle / 360.0) * Math.PI); // Wave-like effect

                    Location particleLocation = origin.clone().add(x, y, z);
                    caster.getWorld().spawnParticle(Particle.WATER_SPLASH, particleLocation, 1, 0, 0, 0, 0.1);
                    caster.getWorld().spawnParticle(Particle.WATER_BUBBLE, particleLocation, 1, 0, 0, 0, 0.1);
                    caster.getWorld().spawnParticle(Particle.WATER_WAKE, particleLocation, 1, 0, 0, 0, 0.1);
                }

                // Damage nearby entities
                for (Entity entity : caster.getWorld().getNearbyEntities(origin, RADIUS, RADIUS, RADIUS)) {
                    if (entity instanceof LivingEntity target && !entity.equals(caster)) {
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
        caster.sendMessage(ChatColor.YELLOW + "Whirlpool Trap is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

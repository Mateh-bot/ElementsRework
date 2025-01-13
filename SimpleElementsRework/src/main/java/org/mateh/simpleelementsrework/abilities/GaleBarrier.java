package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;

public class GaleBarrier extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final int DURATION = 100; // 5 seconds

    public GaleBarrier(Main main) {
        super("Gale Barrier", "Air", main, AbilitiesSlot.THIRD);
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
                caster.sendMessage(ChatColor.RED + "Gale Barrier is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        caster.getWorld().playSound(caster.getLocation(), Sound.ITEM_ELYTRA_FLYING, 1.0f, 1.5f);
        caster.getWorld().spawnParticle(Particle.SWEEP_ATTACK, caster.getLocation(), 50, 1.0, 1.0, 1.0, 0.1);

        Main.getInstance().getServer().getScheduler().runTaskTimer(Main.getInstance(), task -> {
            if (DURATION <= 0 || !caster.isOnline()) {
                task.cancel();
                return;
            }

            caster.getNearbyEntities(3, 3, 3).stream()
                    .filter(entity -> entity instanceof org.bukkit.entity.Projectile)
                    .forEach(projectile -> {
                        projectile.setVelocity(projectile.getVelocity().multiply(-1));
                        projectile.getWorld().spawnParticle(Particle.CRIT, projectile.getLocation(), 10, 0.2, 0.2, 0.2, 0.1);
                    });
        }, 0L, 5L);

        // Set cooldown
        cooldowns.put(playerId, currentTime);

        // Notify cooldown expiration
        caster.sendMessage(ChatColor.YELLOW + "Gale Barrier is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

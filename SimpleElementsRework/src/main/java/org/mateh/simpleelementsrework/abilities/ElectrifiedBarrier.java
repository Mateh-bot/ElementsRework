package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;

public class ElectrifiedBarrier extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final double DAMAGE = 1.0;
    private static final double RADIUS = 3.0;
    private static final int DURATION = 6;
    private static final int REDUCTION_LEVEL = 1;

    public ElectrifiedBarrier(Main main) {
        super("Electrified Barrier", "Lightning", main, AbilitiesSlot.THIRD);
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
                caster.sendMessage(ChatColor.RED + "Electrified Barrier is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);

        caster.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, DURATION * 20, REDUCTION_LEVEL, false, false));

        new BukkitRunnable() {
            int secondsElapsed = 0;

            @Override
            public void run() {
                if (secondsElapsed >= DURATION) {
                    this.cancel();
                    return;
                }

                caster.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, caster.getLocation(), 30, RADIUS, 1, RADIUS, 0.1);

                for (Entity entity : caster.getWorld().getNearbyEntities(caster.getLocation(), RADIUS, RADIUS, RADIUS)) {
                    if (entity instanceof LivingEntity target && !entity.equals(caster)) {
                        target.damage(DAMAGE, caster);

                        target.getWorld().spawnParticle(Particle.CRIT_MAGIC, target.getLocation(), 10, 0.2, 0.2, 0.2, 0.1);
                        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.5f, 1.5f);
                    }
                }

                secondsElapsed++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);

        // Set cooldown
        cooldowns.put(playerId, currentTime);

        // Notify cooldown expiration
        caster.sendMessage(ChatColor.YELLOW + "Electrified Barrier is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

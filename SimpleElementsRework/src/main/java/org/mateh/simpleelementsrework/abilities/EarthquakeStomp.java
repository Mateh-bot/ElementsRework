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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;

public class EarthquakeStomp extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final int DURATION = 5;
    private static final double RADIUS = 5.0;
    private static final double DAMAGE_PER_SECOND = 1.0;
    private static final int SLOW_DURATION = 3;
    private static final int SLOW_LEVEL = 1;


    public EarthquakeStomp(Main main) {
        super("Earthquake Stomp", "Earth", main, AbilitiesSlot.THIRD);
    }

    @Override
    public void startAbilities(PlayerInteractEvent event, Player caster) {

        if (!isElement(caster)) {
            return;
        }

        if (isRightShift(event.getAction(), caster)) {
            return;
        }

        if (!isRight(event.getAction())) {
            return;
        }

        UUID playerId = caster.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(playerId)) {
            long lastUsed = cooldowns.get(playerId);
            long timeLeft = (lastUsed + COOLDOWN_TIME * 1000) - currentTime;

            if (timeLeft > 0) {
                caster.sendMessage(ChatColor.RED + "Earthquake Stomp is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        Location origin = caster.getLocation();

        caster.getWorld().playSound(origin, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f);
        caster.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, origin, 5, 0.5, 0.5, 0.5, 0.1);

        new BukkitRunnable() {
            int secondsElapsed = 0;

            @Override
            public void run() {
                if (secondsElapsed >= DURATION) {
                    this.cancel();
                    return;
                }

                caster.getWorld().spawnParticle(Particle.BLOCK_CRACK, origin, 30, RADIUS, 0.5, RADIUS, 0.1, Material.DIRT.createBlockData());
                caster.getWorld().playSound(origin, Sound.BLOCK_ANVIL_LAND, 1.0f, 0.5f);

                for (Entity entity : caster.getWorld().getNearbyEntities(origin, RADIUS, RADIUS, RADIUS)) {
                    if (entity instanceof LivingEntity target && !entity.equals(caster)) {
                        target.damage(DAMAGE_PER_SECOND, caster);

                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SLOW_DURATION * 20, SLOW_LEVEL, true, true));

                        target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, target.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
                    }
                }
                secondsElapsed++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);

        cooldowns.put(playerId, currentTime);

        caster.sendMessage(ChatColor.YELLOW + "Earthquake Stomp is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

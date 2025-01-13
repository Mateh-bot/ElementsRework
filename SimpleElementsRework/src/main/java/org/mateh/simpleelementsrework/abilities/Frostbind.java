package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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

public class Frostbind extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final double FREEZE_RADIUS = 5.0;
    private static final int SLOW_DURATION = 100;
    private static final int SLOW_AMPLIFIER = 1;

    public Frostbind(Main main) {
        super("Frostbind", "Water", main, AbilitiesSlot.FOURTH);
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
                caster.sendMessage(ChatColor.RED + "Frostbind is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        Location origin = caster.getLocation();

        for (Entity entity : caster.getNearbyEntities(FREEZE_RADIUS, FREEZE_RADIUS, FREEZE_RADIUS)) {
            if (entity instanceof LivingEntity && !entity.equals(caster)) {
                LivingEntity target = (LivingEntity) entity;
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SLOW_DURATION, SLOW_AMPLIFIER, true, true, true));
                caster.getWorld().spawnParticle(Particle.SNOWFLAKE, target.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
            }
        }

        caster.getWorld().playSound(origin, Sound.BLOCK_GLASS_BREAK, 1.5f, 1.0f);
        caster.getWorld().spawnParticle(Particle.CLOUD, origin, 20, 1, 1, 1, 0.1);

        // Set cooldown
        cooldowns.put(playerId, currentTime);

        // Notify cooldown expiration
        caster.sendMessage(ChatColor.YELLOW + "Frostbind is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

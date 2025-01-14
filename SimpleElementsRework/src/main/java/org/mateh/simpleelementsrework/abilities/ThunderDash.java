package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
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

public class ThunderDash extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final int DASH_DISTANCE = 8;
    private static final double STUN_RADIUS = 3.0;
    private static final int STUN_DURATION = 60;

    public ThunderDash(Main main) { super("Thunder Dash", "Lightning", main, AbilitiesSlot.THIRD);}

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
                caster.sendMessage(ChatColor.RED + "Thunder Dash is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        Location startLocation = caster.getLocation();
        Location dashLocation = startLocation.clone().add(startLocation.getDirection().multiply(DASH_DISTANCE));

        caster.getWorld().playSound(startLocation, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        caster.getWorld().spawnParticle(Particle.CLOUD, startLocation, 50, 0.5, 0.5, 0.5, 0.1);
        caster.teleport(dashLocation);
        caster.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, dashLocation, 50, 0.5, 0.5, 0.5, 0.1);

        caster.getWorld().getNearbyEntities(dashLocation, STUN_RADIUS, STUN_RADIUS, STUN_RADIUS).forEach(entity -> {
            if (entity instanceof Player target && !target.equals(caster)) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, STUN_DURATION, 4));
                target.getWorld().spawnParticle(Particle.CRIT_MAGIC, target.getLocation(), 10, 0.3, 0.3, 0.3, 0.1);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.5f, 1.5f);
            }
        });

        cooldowns.put(playerId, currentTime);

        caster.sendMessage(ChatColor.YELLOW + "Thunder Dash is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

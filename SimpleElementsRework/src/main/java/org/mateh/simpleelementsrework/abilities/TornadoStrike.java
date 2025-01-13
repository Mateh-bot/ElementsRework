package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;

public class TornadoStrike extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final double DAMAGE = 2.0;
    private static final double PULL_STRENGTH = 0.2;
    private static final double RADIUS = 5.0;

    public TornadoStrike(Main main) {
        super("Tornado Strike", "Air", main, AbilitiesSlot.SECONDARY);
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
                caster.sendMessage(ChatColor.RED + "Tornado Strike is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        Location tornadoCenter = caster.getLocation();

        tornadoCenter.getWorld().spawnParticle(Particle.CLOUD, tornadoCenter, 30, RADIUS, RADIUS, RADIUS, 0.1);

        tornadoCenter.getWorld().getNearbyEntities(tornadoCenter, RADIUS, RADIUS, RADIUS).forEach(entity -> {
            if (entity instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) entity;

                if (target != caster) {
                    target.damage(DAMAGE);

                    Location targetLocation = target.getLocation();
                    Vector pullDirection = tornadoCenter.toVector().subtract(targetLocation.toVector()).normalize();
                    target.setVelocity(pullDirection.multiply(PULL_STRENGTH));
                }
            }
        });

        // Set cooldown
        cooldowns.put(playerId, currentTime);

        // Notify cooldown expiration
        caster.sendMessage(ChatColor.YELLOW + "Tornado Strike is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

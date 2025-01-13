package org.mateh.simpleelementsrework.abilities;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;


public class HydroBlast extends AbstractAbilities implements Abilities {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 5; // Cooldown in seconds

    public HydroBlast(Main main) {
        super("Hydro Blast", "Water", main, AbilitiesSlot.PRIMARY);
    }

    @Override
    public void startAbilities(PlayerInteractEvent event, Player caster) {
        if (!isElement(caster)) {
            return;
        }

        if (isLeftShift(event.getAction(), caster)) {
            return;
        }

        if (!isleft(event.getAction())) {
            return;
        }

        UUID playerId = caster.getUniqueId();
        if (cooldowns.containsKey(playerId)) {
            long lastUsed = cooldowns.get(playerId);
            long timeLeft = (lastUsed + COOLDOWN_TIME * 1000) - System.currentTimeMillis();
            if (timeLeft > 0) {
                caster.sendMessage("§cHydro Blast is on cooldown! Time left: " + (timeLeft / 1000) + "s");
                return;
            }
        }

        // Activate ability
        double range = 10;
        for (Player nearbyPlayer : caster.getWorld().getPlayers()) {
            if (nearbyPlayer == caster) continue;
            if (nearbyPlayer.getLocation().distance(caster.getLocation()) > range)
                continue;

            Vector directionToPlayer = nearbyPlayer.getLocation().toVector()
                    .subtract(caster.getLocation().toVector())
                    .normalize()
                    .multiply(3); // Push back 3 blocks

            nearbyPlayer.damage(1, caster); // Deal 1 damage
            nearbyPlayer.setVelocity(directionToPlayer);

            // Play water sound at the target's location
            nearbyPlayer.getWorld().playSound(nearbyPlayer.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1.0f, 1.0f);

            // Spawn water particles along the direction to the target
            for (int i = 0; i <= 10; i++) {
                Vector step = directionToPlayer.clone().multiply(i / 10.0);
                caster.getWorld().spawnParticle(Particle.WATER_SPLASH,
                        caster.getLocation().add(step), 1, 0.1, 0.1, 0.1, 0.05);
            }
        }

        // Set cooldown
        cooldowns.put(playerId, System.currentTimeMillis());
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
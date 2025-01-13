package org.mateh.simpleelementsrework.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;

public class BlazingStrike extends AbstractAbilities implements Abilities {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 0; // Cooldown in seconds

    public BlazingStrike(Main main) {
        super("Blazing Strike", "Fire", main, AbilitiesSlot.PRIMARY);
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

        // Activate ability
        Player target = null;
        double range = 5;

        for (Player nearbyPlayer : caster.getWorld().getPlayers()) {
            if (nearbyPlayer == caster) continue;
            if (nearbyPlayer.getLocation().distance(caster.getLocation()) > range)
                continue;

            Vector directionToPlayer = nearbyPlayer.getEyeLocation().toVector()
                    .subtract(caster.getEyeLocation().toVector())
                    .normalize();

            double dotProduct = caster.getEyeLocation().getDirection().normalize().dot(directionToPlayer);

            if (dotProduct > 0.99) {
                target = nearbyPlayer;
                break;
            }
        }

        if(target != null) {

            target.damage(2, caster);
            target.setFireTicks(60);
        }

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

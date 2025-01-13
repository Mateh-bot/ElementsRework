package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;

public class TidalSurge extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final double DASH_DISTANCE = 7.0;
    private static final double KNOCKBACK_STRENGTH = 1.5;
    private static final double KNOCKBACK_RADIUS = 3.0;


    public TidalSurge(Main main) { super("Tidal Surge", "Water", main, AbilitiesSlot.PRIMARY);}

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
                caster.sendMessage(ChatColor.RED + "Tidal Surge is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        Location origin = caster.getLocation();
        Location dashLocation = origin.clone().add(origin.getDirection().normalize().multiply(DASH_DISTANCE));
        dashLocation.setY(origin.getY());

        caster.teleport(dashLocation);

        for (Entity entity : caster.getNearbyEntities(KNOCKBACK_RADIUS, KNOCKBACK_RADIUS, KNOCKBACK_RADIUS)) {
            if (entity instanceof Player && !entity.equals(caster)) {
                Vector knockback = entity.getLocation().toVector().subtract(origin.toVector()).normalize().multiply(KNOCKBACK_STRENGTH);
                entity.setVelocity(knockback);
            }
        }

        caster.getWorld().playSound(origin, Sound.ITEM_TRIDENT_RIPTIDE_3, 2.0f, 1.0f);
        caster.getWorld().spawnParticle(Particle.SWEEP_ATTACK, dashLocation, 30, 1, 1, 1, 0.1);

        cooldowns.put(playerId, currentTime);

        caster.sendMessage(ChatColor.YELLOW + "Tidal Surge is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

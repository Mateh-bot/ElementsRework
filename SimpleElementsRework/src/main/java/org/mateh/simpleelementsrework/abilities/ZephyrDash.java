package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;

public class ZephyrDash extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final double DASH_DISTANCE = 8.0;
    private static final double RADIUS = 5;

    public ZephyrDash(Main main) {
        super("Zephyr Dash", "Air", main, AbilitiesSlot.PRIMARY);
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
                caster.sendMessage(ChatColor.RED + "Zephyr Dash is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        Vector direction = caster.getLocation().getDirection().normalize().multiply(DASH_DISTANCE);
        Location dashLocation = caster.getLocation().add(direction);
        caster.teleport(dashLocation);

        caster.getWorld().getNearbyEntities(caster.getLocation(), RADIUS, RADIUS, RADIUS).forEach(entity -> {
            if (entity instanceof Player) {
                Player ally = (Player) entity;
                if (!ally.equals(caster)) {
                    ally.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
                }
            }
        });

        cooldowns.put(playerId, currentTime);

        caster.sendMessage(ChatColor.YELLOW + "Zephyr Dash is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

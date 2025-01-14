package org.mateh.simpleelementsrework.abilities;

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

public class WindBlade extends AbstractAbilities implements Abilities {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 5;
    private static final double DAMAGE = 2.0;
    private static final double RANGE = 10.0;
    private static final double ARMOR_REDUCTION = 2.0;

    public WindBlade(Main main) {
        super("Wind Blade", "Air", main, AbilitiesSlot.PRIMARY);
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
                caster.sendMessage("§cWind Blade is on cooldown! Time left: " + (timeLeft / 1000) + "s");
                return;
            }
        }

        // Activate ability
        Vector direction = caster.getLocation().getDirection().normalize();

        caster.getWorld().spawnParticle(Particle.SWEEP_ATTACK, caster.getLocation(), 5, 0.5, 0.5, 0.5, 0.1);
        caster.getWorld().playSound(caster.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0f, 1.2f);

        for (double i = 0; i < RANGE; i += 0.5) {
            Vector step = direction.clone().multiply(i);
            Location bladeLocation = caster.getLocation().add(step);

            caster.getWorld().spawnParticle(Particle.CLOUD, bladeLocation, 3, 0.1, 0.1, 0.1, 0.02);

            for (Entity entity : bladeLocation.getWorld().getNearbyEntities(bladeLocation, 1.0, 1.0, 1.0)) {
                if (entity instanceof Player && entity != caster) {
                    Player target = (Player) entity;

                    target.damage(DAMAGE, caster);

                    double newArmor = Math.max(0, target.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ARMOR).getValue() - ARMOR_REDUCTION);
                    target.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ARMOR).setBaseValue(newArmor);

                    i = RANGE;
                    break;
                }
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
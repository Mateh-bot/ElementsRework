package org.mateh.simpleelementsrework.abilities;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;

public class StaticShock extends AbstractAbilities implements Abilities {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 5;
    private static final double RANGE = 8.0;
    private static final double DAMAGE = 2.0;
    private static final int DURATION = 20;

    public StaticShock(Main main) {
        super("Static Shock", "Lightning", main, AbilitiesSlot.PRIMARY);
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
                caster.sendMessage("§cStatic Shock is on cooldown! Time left: " + (timeLeft / 1000) + "s");
                return;
            }
        }

        // Activate ability
        caster.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, caster.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
        caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.2f);

        Entity target = caster.getWorld().getNearbyEntities(caster.getLocation(), RANGE, RANGE, RANGE).stream()
                .filter(entity -> entity instanceof Player && entity != caster)
                .findFirst()
                .orElse(null);

        if (target != null && target instanceof Player) {
            Player enemy = (Player) target;

            enemy.damage(DAMAGE, caster);

            enemy.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.SLOW,
                    DURATION,
                    255,
                    false, false, true
            ));

            // Set cooldown
            cooldowns.put(playerId, System.currentTimeMillis());
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
package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
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

public class ChainLightning extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final int MAX_CHAIN_LENGTH = 3;
    private static final double RADIUS = 10.0;
    private static final int STUN_DURATION = 60;

    public ChainLightning(Main main) {
        super("Chain Lightning", "Lightning", main, AbilitiesSlot.FOURTH);
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
                caster.sendMessage(ChatColor.RED + "Chain Lightning is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        Entity target = null;
        int chainCount = 0;

        for (Entity entity : caster.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
            if (entity instanceof Player && !entity.equals(caster)) {
                target = entity;
                chainCount++;
                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, STUN_DURATION, 2));

                caster.getWorld().spawnParticle(Particle.SPELL, caster.getLocation(), 1, 0.5, 0.5, 0.5, 0.1);
                target.getWorld().spawnParticle(Particle.SPELL_INSTANT, target.getLocation(), 1, 0.5, 0.5, 0.5, 0.1);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.0f);

                if (chainCount >= MAX_CHAIN_LENGTH) {
                    break;
                }
            }
        }
        // Set cooldown
        cooldowns.put(playerId, currentTime);

        // Notify cooldown expiration
        caster.sendMessage(ChatColor.YELLOW + "Chain Lightning is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

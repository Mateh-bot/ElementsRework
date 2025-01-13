package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
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

public class AquaShield extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;

    public AquaShield(Main main) {
        super("Aqua Shield", "Water", main, AbilitiesSlot.SECONDARY);
    }

    @Override
    public void startAbilities(PlayerInteractEvent event, Player caster) {
        if (!isElement(caster)) {
            return;
        }

        if (!isLeftShift(event.getAction(), caster)) {
            return;
        }

        UUID playerId = caster.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(playerId)) {
            long lastUsed = cooldowns.get(playerId);
            long timeLeft = (lastUsed + COOLDOWN_TIME * 1000) - currentTime;

            if (timeLeft > 0) {
                caster.sendMessage(ChatColor.RED + "Aqua Shield is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        caster.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 10, 0));
        caster.setHealth(Math.min(caster.getHealth() + 2, caster.getMaxHealth()));

        caster.getWorld().spawnParticle(Particle.WATER_SPLASH, caster.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
        caster.getWorld().playSound(caster.getLocation(), Sound.ITEM_BUCKET_FILL, 1.0f, 1.0f);

        // Set cooldown
        cooldowns.put(playerId, currentTime);

        // Notify cooldown expiration
        caster.sendMessage(ChatColor.YELLOW + "Aqua Shield is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

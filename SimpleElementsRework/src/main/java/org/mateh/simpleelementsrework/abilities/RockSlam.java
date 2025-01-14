package org.mateh.simpleelementsrework.abilities;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
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

public class RockSlam extends AbstractAbilities implements Abilities {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 5; // Cooldown in seconds
    private static final double RADIUS = 12.0;
    private static final int DAMAGE = 2;
    private static final int STUN_DURATION = 60;

    public RockSlam(Main main) {
        super("Rock Slam", "Earth", main, AbilitiesSlot.PRIMARY);
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
                //caster.sendMessage("§cRock Slam is on cooldown! Time left: " + (timeLeft / 1000) + "s");
                return;
            }
        }

        // Activate ability
        caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        caster.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, caster.getLocation(), 1, 0.5, 0.5, 0.5, 0.1);

        caster.getNearbyEntities(RADIUS, RADIUS, RADIUS).stream()
                .filter(entity -> entity instanceof Player)
                .forEach(entity -> {
                    Player target = (Player) entity;
                    target.damage(DAMAGE, caster);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, STUN_DURATION, 4));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, STUN_DURATION, 128));

                    Material material = Material.STONE;
                    BlockData blockData = material.createBlockData();


                    target.getWorld().spawnParticle(Particle.BLOCK_CRACK, target.getLocation(), 20,  blockData);
                });

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
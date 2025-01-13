package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashMap;
import java.util.UUID;

public class Stonewall extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;

    public Stonewall(Main main) {
        super("Stonewall", "Earth", main, AbilitiesSlot.THIRD);
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
                caster.sendMessage(ChatColor.RED + "Stonewall is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        int width = 5;
        int height = 3;

        Location origin = caster.getLocation().add(caster.getLocation().getDirection().normalize().multiply(3));
        origin.setY(origin.getY() + 1);

        for (int x = -width / 2; x <= width / 2; x++) {
            for (int y = 0; y < height; y++) {
                Location blockLocation = origin.clone().add(x, y, 0);
                if (blockLocation.getBlock().isEmpty()) {
                    blockLocation.getBlock().setType(Material.STONE);
                }
            }
        }

        caster.getWorld().playSound(origin, Sound.BLOCK_STONE_PLACE, 1.5f, 1.0f);
        caster.getWorld().spawnParticle(Particle.BLOCK_CRACK, origin, 50, width, height / 2.0, width / 2.0, Material.STONE.createBlockData());

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int x = -width / 2; x <= width / 2; x++) {
                    for (int y = 0; y < height; y++) {
                        Location blockLocation = origin.clone().add(x, y, 0);
                        if (blockLocation.getBlock().getType() == Material.STONE) {
                            blockLocation.getBlock().setType(Material.AIR);
                        }
                    }
                }

                caster.getWorld().playSound(origin, Sound.BLOCK_STONE_BREAK, 1.5f, 1.0f);
                caster.getWorld().spawnParticle(Particle.CLOUD, origin, 30, width, height / 2.0, width / 2.0, 0.1);
            }
        }.runTaskLater(Main.getInstance(), 6 * 20);

        // Set cooldown
        cooldowns.put(playerId, currentTime);

        // Notify cooldown expiration
        caster.sendMessage(ChatColor.YELLOW + "Stonewall is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

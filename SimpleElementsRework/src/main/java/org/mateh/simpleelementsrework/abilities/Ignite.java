package org.mateh.simpleelementsrework.abilities;

import net.md_5.bungee.api.ChatColor;
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

public class Ignite extends AbstractAbilities implements Abilities {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 3;
    private static final int DAMAGE = 2;

    public Ignite(Main main) {
        super("Ignite", "Fire", main, AbilitiesSlot.FOURTH);
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
                caster.sendMessage(ChatColor.RED + "Ignite is on cooldown for " + (timeLeft / 1000) + " seconds.");
                return;
            }
        }

        // Activate ability
        Player target = null;
        double range = 20;

        // Realiza un ray tracing y busca entidades en la dirección de la mirada
        for (Entity entity : caster.getWorld().getNearbyEntities(caster.getEyeLocation(), range, range, range)) {

            if (!(entity instanceof Player potentialTarget)) continue; // Filtrar solo jugadores
            if (entity == caster) continue; // Ignorar al caster

            // Verifica si el jugador está directamente en la línea de visión
            Vector directionToEntity = potentialTarget.getLocation().toVector().subtract(caster.getEyeLocation().toVector()).normalize();
            double dotProduct = caster.getEyeLocation().getDirection().normalize().dot(directionToEntity);

            if (dotProduct > 0.99) {
                // Verifica si hay obstrucciones entre el caster y el objetivo
                if (caster.getWorld().rayTraceEntities(caster.getEyeLocation(), directionToEntity, range, 0.1, e -> e instanceof Player && e != caster) != null) {
                    target = potentialTarget;
                    break;
                }
            }
        }

        if (target == null) {
            caster.sendMessage(ChatColor.RED + "You must look at a player to use Ignite!");
            return;
        }

        target.setFireTicks(60);
        target.damage(DAMAGE, caster);

        // Set cooldown
        cooldowns.put(playerId, currentTime);

        // Notify cooldown expiration
        caster.sendMessage(ChatColor.YELLOW + "Ignite is now on cooldown for " + COOLDOWN_TIME + " seconds.");
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

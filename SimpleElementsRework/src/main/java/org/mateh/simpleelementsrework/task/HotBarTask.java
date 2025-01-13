package org.mateh.simpleelementsrework.task;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.abstracts.AbstractAbilities;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;
import org.mateh.simpleelementsrework.interfaces.Abilities;

import java.util.HashSet;
import java.util.Set;

public class HotBarTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String element = Main.getInstance().getPlayerDataManager().getElement(player.getUniqueId());

            if (element.isEmpty()) continue;
            Set<Abilities> a = new HashSet<>();
            for (Abilities abilities : Main.getInstance().getAbilities()) {
                if (abilities instanceof AbstractAbilities abstractAbilities) {
                    if (abstractAbilities.getElement().equalsIgnoreCase(element)) {
                        a.add(abilities);
                    }
                }
            }

            AbstractAbilities primary = null;
            AbstractAbilities secondary = null;
            AbstractAbilities third = null;
            AbstractAbilities fourth = null;
            AbstractAbilities five = null;
            for (Abilities abilities : a) {
                if (abilities instanceof AbstractAbilities abstractAbilities) {
                    if (abstractAbilities.getSlot() == AbilitiesSlot.PRIMARY) {
                        primary = abstractAbilities;
                    } else if (abstractAbilities.getSlot() == AbilitiesSlot.SECONDARY) {
                        secondary = abstractAbilities;
                    } else if (abstractAbilities.getSlot() == AbilitiesSlot.THIRD) {
                        third = abstractAbilities;
                    } else if (abstractAbilities.getSlot() == AbilitiesSlot.FOURTH) {
                        fourth = abstractAbilities;
                    } else if (abstractAbilities.getSlot() == AbilitiesSlot.FIVE){
                        five = abstractAbilities;
                    }
                }
            }

            if (primary == null) continue;
            if (secondary == null) continue;
            if (third == null) continue;
            if (fourth == null) continue;
            if (five == null) continue;

            if (element.equalsIgnoreCase("Fire")) {
                element = ChatColor.RED + element;
            } else if (element.equalsIgnoreCase("Water")) {
                element = ChatColor.BLUE + element;
            } else if (element.equalsIgnoreCase("Earth")) {
                element = ChatColor.GREEN + element;
            } else if (element.equalsIgnoreCase("Air")) {
                element = ChatColor.WHITE + element;
            } else if (element.equalsIgnoreCase("Lightning")) {
                element = ChatColor.YELLOW + element;
            }

            String primaryCooldown = ((Abilities) primary).getCooldown(player) == 0 ? "§aREADY" : "§c" + ((Abilities) primary).getCooldown(player);
            String secondCooldown = ((Abilities) secondary).getCooldown(player) == 0 ? "§aREADY" : "§c" + ((Abilities) secondary).getCooldown(player);
            String thirdCooldown = ((Abilities) third).getCooldown(player) == 0 ? "§aREADY" : "§c" + ((Abilities) third).getCooldown(player);
            String fourthCooldown = ((Abilities) fourth).getCooldown(player) == 0 ? "§aREADY" : "§c" + ((Abilities) fourth).getCooldown(player);
            String fiveCooldown = ((Abilities) five).getCooldown(player) == 0 ? "§aREADY" : "§c" + ((Abilities) five).getCooldown(player);


            TextComponent textComponent = new TextComponent(element.toUpperCase() + " §f§l| §f" + primaryCooldown + " §f§l| §f" + secondCooldown + " §f§l| §F" + thirdCooldown + " §f§l| §F " + fourthCooldown + " §f§l| §F " + fiveCooldown );
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);

        }
    }
}

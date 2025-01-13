package org.mateh.simpleelementsrework.abstracts;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.enums.AbilitiesSlot;

public abstract class AbstractAbilities implements Listener {
    String name;
    String element;
    AbilitiesSlot slot;

    public AbstractAbilities(String name, String element, Main main, AbilitiesSlot abilitiesSlot) {
        this.name = name;
        this.element = element;
        this.slot = abilitiesSlot;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    public AbilitiesSlot getSlot() {
        return slot;
    }

    public String getElement() {
        return element;
    }

    public boolean isRight(Action action) {
        return action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR;
    }

    public boolean isRightShiftSword(Action action, Player player){
        if(!player.isSneaking()){
            return false;
        }
        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
            ItemStack item = player.getInventory().getItemInMainHand();
            return item.getType() == Material.DIAMOND_SWORD;
        } else {
            return false;
        }
    }

    public boolean isRightShift(Action action, Player player) {
        if (!player.isSneaking()) {
            return false;
        }
        return action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR;
    }

    public boolean isleft(Action action) {
        return action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR;
    }

    public boolean isLeftShift(Action action, Player player) {
        if (!player.isSneaking()) {
            return false;
        }
        return action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR;
    }

    public boolean isElement(Player player) {
        String elementPlayer = Main.getInstance().getPlayerDataManager().getElement(player.getUniqueId());
        return this.element.equalsIgnoreCase(elementPlayer);
    }

    public String getName() {
        if (element.equalsIgnoreCase("Fire")) {
            return ChatColor.RED + name;
        } else if (element.equalsIgnoreCase("Water")) {
            return ChatColor.BLUE + name;
        } else if (element.equalsIgnoreCase("Earth")) {
            return ChatColor.GREEN + name;
        } else if (element.equalsIgnoreCase("Air")) {
            return ChatColor.WHITE + name;
        } else if (element.equalsIgnoreCase("Lightning")) {
            return ChatColor.YELLOW + name;
        }
        return name;
    }
}

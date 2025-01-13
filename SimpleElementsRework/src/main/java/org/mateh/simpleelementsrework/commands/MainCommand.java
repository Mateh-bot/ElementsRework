package org.mateh.simpleelementsrework.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mateh.simpleelementsrework.Main;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player player) {
            if(strings != null && strings.length > 0) {
                if (player.isOp()) {
                    String selectedElement = strings[0];
                    Main.getInstance().getPlayerDataManager().setElement(player.getUniqueId(), selectedElement);
                    player.sendTitle("§aElement Selected!", "§f" + selectedElement, 10, 20, 10);
                }
            }
        }
        return false;
    }
}

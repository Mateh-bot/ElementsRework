package org.mateh.simpleelementsrework.utils;

import org.mateh.simpleelementsrework.Main;
import org.mateh.simpleelementsrework.interfaces.ElementType;

public class ConfigUtils {

    public static int getDamage(ElementType elementType){
        Main main = Main.getInstance();
        return main.getConfig().getInt(elementType.getConfigPath(), 0);
    }




}

package org.mateh.simpleelementsrework;

import org.bukkit.plugin.java.JavaPlugin;
import org.mateh.simpleelementsrework.abilities.*;
import org.mateh.simpleelementsrework.commands.MainCommand;
import org.mateh.simpleelementsrework.data.PlayerDataManager;
import org.mateh.simpleelementsrework.interfaces.Abilities;
import org.mateh.simpleelementsrework.listeners.PlayerListeners;
import org.mateh.simpleelementsrework.task.EffectTask;
import org.mateh.simpleelementsrework.task.EnderEggTask;
import org.mateh.simpleelementsrework.task.HotBarTask;

import java.util.HashSet;
import java.util.Set;

public final class Main extends JavaPlugin {

    static Main instance;
    PlayerDataManager playerDataManager;
    Set<Abilities> abilities = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        instance = this;
        playerDataManager = new PlayerDataManager();

        getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);

        loadAbilities();

        new HotBarTask().runTaskTimer(this, 0, 20L);
        new EnderEggTask().runTaskTimer(this, 0, 20L);
        new EffectTask().runTaskTimer(this, 0, 20L);

        getCommand("element").setExecutor(new MainCommand());
    }

    @Override
    public void onDisable() {
    }

    void loadAbilities() {

        // Fire
        FlameDash flameDash = new FlameDash(this);
        abilities.add(flameDash);

        //Ignite ignite = new Ignite(this);
        //abilities.add(ignite);

        InfernalShield infernalShield = new InfernalShield(this);
        abilities.add(infernalShield);

        BlazingStrike blazingStrike = new BlazingStrike(this);
        abilities.add(blazingStrike);

        PhoenixLeap phoenixLeap = new PhoenixLeap(this);
        abilities.add(phoenixLeap);

        // Water
        AquaShield aquaShield = new AquaShield(this);
        abilities.add(aquaShield);

        TidalSurge tidalSurge = new TidalSurge(this);
        abilities.add(tidalSurge);

        //Frostbind frostbind = new Frostbind(this);
        //abilities.add(frostbind);

        WhirlpoolTrap whirlpoolTrap = new WhirlpoolTrap(this);
        abilities.add(whirlpoolTrap);

        // Earth
        Stonewall stonewall = new Stonewall(this);
        abilities.add(stonewall);

        EarthquakeStomp earthquakeStomp = new EarthquakeStomp(this);
        abilities.add(earthquakeStomp);

        BoulderToss boulderToss = new BoulderToss(this);
        abilities.add(boulderToss);

        // Air
        GaleBarrier galeBarrier = new GaleBarrier(this);
        abilities.add(galeBarrier);

        ZephyrDash zephyrDash = new ZephyrDash(this);
        abilities.add(zephyrDash);

        //TornadoStrike tornadoStrike = new TornadoStrike(this);
        //abilities.add(tornadoStrike);

        UpdraftLeap updraftLeap = new UpdraftLeap(this);
        abilities.add(updraftLeap);

        // Lightning
        ElectrifiedBarrier electrifiedBarrier = new ElectrifiedBarrier(this);
        abilities.add(electrifiedBarrier);

        ThunderDash thunderDash = new ThunderDash(this);
        abilities.add(thunderDash);

        //ChainLightning chainLightning = new ChainLightning(this);
        //abilities.add(chainLightning);

        Stormcall stormcall = new Stormcall(this);
        abilities.add(stormcall);
    }

    public static Main getInstance() {
        return instance;
    }

    public Set<Abilities> getAbilities() {
        return abilities;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}

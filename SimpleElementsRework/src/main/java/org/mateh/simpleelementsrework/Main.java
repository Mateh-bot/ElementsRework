package org.mateh.simpleelementsrework;

import org.bukkit.plugin.java.JavaPlugin;
import org.mateh.simpleelementsrework.abilities.*;
import org.mateh.simpleelementsrework.commands.MainCommand;
import org.mateh.simpleelementsrework.data.PlayerDataManager;
import org.mateh.simpleelementsrework.interfaces.Abilities;
import org.mateh.simpleelementsrework.listeners.PlayerListeners;
import org.mateh.simpleelementsrework.task.ArmorTask;
import org.mateh.simpleelementsrework.task.EffectTask;
import org.mateh.simpleelementsrework.task.EnderEggTask;
import org.mateh.simpleelementsrework.task.HotBarTask;

import java.util.HashSet;
import java.util.Set;

public final class Main extends JavaPlugin {

    static Main instance;
    PlayerDataManager playerDataManager;
    Set<Abilities> abilities = new HashSet<>();
    PlayerListeners playerListeners;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        instance = this;
        playerDataManager = new PlayerDataManager();
        playerListeners = new PlayerListeners(this);

        getServer().getPluginManager().registerEvents(playerListeners, this);

        loadAbilities();

        new HotBarTask().runTaskTimer(this, 0, 20L);
        new EnderEggTask().runTaskTimer(this, 0, 20L);
        new EffectTask().runTaskTimer(this, 0, 20L);
        new ArmorTask().runTaskTimer(this, 0, 20L);

        getCommand("element").setExecutor(new MainCommand());
    }

    @Override
    public void onDisable() {
    }

    void loadAbilities() {

        // FIRE PRIMARY
        BlazingStrike blazingStrike = new BlazingStrike(this);
        abilities.add(blazingStrike);

        // FIRE SECONDARY
        InfernalShield infernalShield = new InfernalShield(this);
        abilities.add(infernalShield);

        // FIRE THIRD
        FlameDash flameDash = new FlameDash(this);
        abilities.add(flameDash);

        // FIRE FOURTH
        Ignite ignite = new Ignite(this);
        abilities.add(ignite);

        // FIRE FIVE
        PhoenixLeap phoenixLeap = new PhoenixLeap(this);
        abilities.add(phoenixLeap);

        // WATER PRIMARY
        HydroBlast hydroBlast = new HydroBlast(this);
        abilities.add(hydroBlast);

        // WATER SECONDARY
        AquaShield aquaShield = new AquaShield(this);
        abilities.add(aquaShield);

        // WATER THIRD
        TidalSurge tidalSurge = new TidalSurge(this);
        abilities.add(tidalSurge);

        // WATER FOURTH
        Frostbind frostbind = new Frostbind(this);
        abilities.add(frostbind);

        // WATER FIVE
        WhirlpoolTrap whirlpoolTrap = new WhirlpoolTrap(this);
        abilities.add(whirlpoolTrap);

        // EARTH PRIMARY
        RockSlam rockSlam = new RockSlam(this);
        abilities.add(rockSlam);

        // EARTH SECONDARY
        Stonewall stonewall = new Stonewall(this);
        abilities.add(stonewall);

        // EARTH THIRD
        EarthquakeStomp earthquakeStomp = new EarthquakeStomp(this);
        abilities.add(earthquakeStomp);

        // EARTH FOURTH
        Burrow burrow = new Burrow(this);
        abilities.add(burrow);

        // EARTH FIVE
        BoulderToss boulderToss = new BoulderToss(this);
        abilities.add(boulderToss);

        // AIR PRIMARY
        WindBlade windBlade = new WindBlade(this);
        abilities.add(windBlade);

        // AIR SECONDARY
        GaleBarrier galeBarrier = new GaleBarrier(this);
        abilities.add(galeBarrier);

        // AIR THIRD
        ZephyrDash zephyrDash = new ZephyrDash(this);
        abilities.add(zephyrDash);

        // AIR FOURTH
        TornadoStrike tornadoStrike = new TornadoStrike(this);
        abilities.add(tornadoStrike);

        // AIR FIVE
        UpdraftLeap updraftLeap = new UpdraftLeap(this);
        abilities.add(updraftLeap);

        // LIGHTNING PRIMARY
        StaticShock staticShock = new StaticShock(this);
        abilities.add(staticShock);

        // LIGHTNING SECONDARY
        ElectrifiedBarrier electrifiedBarrier = new ElectrifiedBarrier(this);
        abilities.add(electrifiedBarrier);

        // LIGHTNING THIRD
        ThunderDash thunderDash = new ThunderDash(this);
        abilities.add(thunderDash);

        // LIGHTNING FOURTH
        ChainLightning chainLightning = new ChainLightning(this);
        abilities.add(chainLightning);

        // LIGHTNING FIVE
        Stormcall stormcall = new Stormcall(this);
        abilities.add(stormcall);
    }

    public PlayerListeners getPlayerListeners() {
        return playerListeners;
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

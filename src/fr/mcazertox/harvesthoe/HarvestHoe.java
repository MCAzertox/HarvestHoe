package fr.mcazertox.harvesthoe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HarvestHoe extends JavaPlugin {
  public static Economy economy = null;
  
  public static Permission permission = null;
  
  private static HarvestHoe instance;
  
  public static HashMap<String, Integer> levels = new HashMap<>();
  
  public static HashMap<String, List<HashMap<String, Integer>>> boosters = new HashMap<>();
  
  public static HashMap<UUID, Boolean> safemode = new HashMap<>();
  
  public static List<ArmorStand> loadedHolos = new ArrayList<>();
  
  public static HashMap<UUID, Boolean> getSafeMode() {
    return safemode;
  }
  
  public static HashMap<String, Integer> getLevels() {
    return levels;
  }
  
  public static HashMap<String, List<HashMap<String, Integer>>> getBoosters() {
    return boosters;
  }
  
  public static List<ArmorStand> getHolos() {
    return loadedHolos;
  }
  
  public void onEnable() {
    super.onEnable();
    safemode = new HashMap<>();
    instance = this;
    BoostersScheduler.startDecreasingBoosters();
    getCommand("hh").setExecutor(new Commands(this));
    getServer().getPluginManager().registerEvents(new OnBreakEvent(this), (Plugin)this);
    getServer().getPluginManager().registerEvents(new OnInteractEvent(this), (Plugin)this);
    getServer().getPluginManager().registerEvents(new LeftClickBreakBlocks(this), (Plugin)this);
    getServer().getPluginManager().registerEvents(new OnUseBoosters(), (Plugin)this);
    new BoostersScheduler();
    saveDefaultConfig();
    setupPermissions();
    CustomConfig.setup();
    CustomConfig.get().options().copyDefaults(true);
    CustomConfig.save();
    CustomConfigMessages.reload();
    CustomConfigMessages.saveDefaultConfig();
    CustomConfigLevels.reload();
    CustomConfigLevels.saveDefaultConfig();
    CustomConfigBoosters.reload();
    CustomConfigBoosters.saveDefaultConfig();
    for (String s : CustomConfig.get().getKeys(false)) {
      UUID uuid = UUID.fromString(s);
      safemode.put(uuid, Boolean.valueOf(CustomConfig.get().getBoolean(String.valueOf(s) + ".safemode")));
      levels.put(s, Integer.valueOf(CustomConfig.get().getInt(String.valueOf(s) + ".level")));
    } 
    if (!setupEconomy()) {
      System.out.println("[HarvestHoe] Disabled (Vault dependency), Vault not found!");
      getServer().getPluginManager().disablePlugin((Plugin)this);
      return;
    } 
    (new BukkitRunnable() {
        public void run() {
          for (String uuid : CustomConfig.get().getKeys(false)) {
            List<HashMap<String, Integer>> list = new ArrayList<>();
            if (CustomConfig.get().getConfigurationSection(String.valueOf(uuid) + ".boosters") != null)
              for (String booster : CustomConfig.get().getConfigurationSection(String.valueOf(uuid) + ".boosters").getKeys(false)) {
                HashMap<String, Integer> map = new HashMap<>();
                map.put(booster, Integer.valueOf(CustomConfig.get().getInt(String.valueOf(uuid) + ".boosters." + booster + ".delay")));
                list.add(map);
              }  
            HarvestHoe.getBoosters().put(uuid, list);
          } 
        }
      }).runTaskLater((Plugin)HarvestHoe.getInstance(), 40L);
    System.out.println("[HarvestHoe] Loaded succesfully !");
  }
  
  public void onDisable() {
    CustomConfig.setup();
    System.out.println("[HarvestHoe] Shut off successfully !");
    for (Map.Entry<UUID, Boolean> safe : safemode.entrySet())
      CustomConfig.get().set(String.valueOf(((UUID)safe.getKey()).toString()) + ".safemode", safe.getValue()); 
    for (Map.Entry<String, Integer> entry : levels.entrySet())
      CustomConfig.get().set(String.valueOf(entry.getKey()) + ".level", entry.getValue()); 
    for (Map.Entry<String, List<HashMap<String, Integer>>> eachPlayers : getBoosters().entrySet()) {
      for (HashMap<String, Integer> map : eachPlayers.getValue()) {
        for (Map.Entry<String, Integer> entry : map.entrySet())
          CustomConfig.get().set(String.valueOf(eachPlayers.getKey()) + ".boosters." + (String)entry.getKey() + ".delay", entry.getValue()); 
      } 
    } 
    CustomConfig.save();
    if (!getHolos().isEmpty())
      for (ArmorStand as : getHolos())
        as.remove();  
  }
  
  public static HarvestHoe getInstance() {
    return instance;
  }
  
  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null)
      return false; 
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null)
      return false; 
    economy = (Economy)rsp.getProvider();
    return (economy != null);
  }
  
  public static Economy getEconomy() {
    return economy;
  }
  
  public boolean setupPermissions() {
    RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
    if (permissionProvider != null)
      permission = (Permission)permissionProvider.getProvider(); 
    return (permission != null);
  }
  
  public static Permission getPermissions() {
    return permission;
  }
}

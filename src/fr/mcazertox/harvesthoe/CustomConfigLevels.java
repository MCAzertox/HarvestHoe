package fr.mcazertox.harvesthoe;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfigLevels {
  private static File customConfigFile = null;
  
  private static FileConfiguration customConfig = null;
  
  private static HarvestHoe plugin = (HarvestHoe)HarvestHoe.getPlugin(HarvestHoe.class);
  
  public static void reload() {
    if (customConfigFile == null)
      customConfigFile = new File(plugin.getDataFolder(), "levels.yml"); 
    customConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(customConfigFile);
    Reader defConfigStream = null;
    try {
      defConfigStream = new InputStreamReader(plugin.getResource("levels.yml"), "UTF8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } 
    if (defConfigStream != null) {
      YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
      customConfig.setDefaults((Configuration)defConfig);
    } 
  }
  
  public static FileConfiguration get() {
    if (customConfig == null)
      reload(); 
    return customConfig;
  }
  
  public static void save() {
    if (customConfig == null || customConfigFile == null)
      return; 
    try {
      get().save(customConfigFile);
    } catch (IOException ex) {
      plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
    } 
  }
  
  public static void saveDefaultConfig() {
    if (customConfigFile == null)
      customConfigFile = new File(plugin.getDataFolder(), "levels.yml"); 
    if (!customConfigFile.exists())
      plugin.saveResource("levels.yml", false); 
  }
}

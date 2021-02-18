package fr.mcazertox.harvesthoe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class Commands implements CommandExecutor {
  private HarvestHoe main;
  
  public Commands(HarvestHoe harvestHoe) {
    this.main = harvestHoe;
  }
  
  @SuppressWarnings("deprecation")
public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if (cmd.getName().equalsIgnoreCase("hh")) {
      if (args.length == 0 && sender.hasPermission("harvesthoe.help")) {
        sender.sendMessage("§m                  §b§lHarvestHoe                  ");
        sender.sendMessage("");
        sender.sendMessage("§6 - §7/hh     §e---> See all the HarvestHoe commands");
        sender.sendMessage("§6 - §7/hh reload     §e---> Reload the config");
        sender.sendMessage("§6 - §7/hh give &3<player>     §e---> Give the harvest hoe to a player");
        sender.sendMessage("§6 - §7/hh safemode     §e---> Enabled/Disable the safemode");
        sender.sendMessage("§6 - §7/hh boosters     §e---> Shows the booster's commands");
        sender.sendMessage("");
        sender.sendMessage("                                                   ");
        return true;
      } 
      if (args.length == 0 && !sender.hasPermission("harvesthoe.help")) {
        sender.sendMessage(CustomConfigMessages.get().getString("no-permission-message").replace("&", "§"));
        return true;
      } 
      if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("harvesthoe.reload")) {
        this.main.reloadConfig();
        CustomConfig.reload();
        CustomConfigMessages.reload();
        CustomConfigMessages.save();
        CustomConfigLevels.reload();
        CustomConfigLevels.save();
        CustomConfigBoosters.reload();
        CustomConfigBoosters.save();
        for (Map.Entry<UUID, Boolean> safe : HarvestHoe.getSafeMode().entrySet())
          CustomConfig.get().set(String.valueOf(((UUID)safe.getKey()).toString()) + ".safemode", safe.getValue()); 
        for (String uuid : CustomConfig.get().getConfigurationSection("").getKeys(false)) {
          HarvestHoe.getSafeMode().put(UUID.fromString(uuid), Boolean.valueOf(CustomConfig.get().getBoolean(String.valueOf(uuid) + ".safemode")));
          HarvestHoe.getLevels().put(uuid, Integer.valueOf(CustomConfig.get().getInt(String.valueOf(uuid) + ".level")));
        } 
        CustomConfig.save();
        sender.sendMessage(ChatColor.GREEN + "§bHarvestHoe has been reloaded !");
        return true;
      } 
      if (args[0].equalsIgnoreCase("reload") && !sender.hasPermission("harvesthoe.reload")) {
        sender.sendMessage(CustomConfigMessages.get().getString("no-permission-message").replace("&", "§"));
        return true;
      } 
      if ((args[0].equalsIgnoreCase("safemode") && sender.hasPermission(this.main.getConfig().getString("main-config.safe-mode.permission-require.permission")) && this.main.getConfig().getBoolean("main-config.safe-mode.permission-require.enabled")) || (
        args[0].equalsIgnoreCase("safemode") && !this.main.getConfig().getBoolean("main-config.safe-mode.permission-require.enabled"))) {
        if (!(sender instanceof Player)) {
          sender.sendMessage("§cOnly Players can do that :/");
          return true;
        } 
        Player p = (Player)sender;
        if (!this.main.getConfig().getBoolean("main-config.safe-mode.enabled")) {
          p.sendMessage("§cSafe mode is not active on the server !");
          return true;
        } 
        if (!HarvestHoe.getSafeMode().containsKey(p.getUniqueId())) {
          HarvestHoe.getSafeMode().put(p.getUniqueId(), Boolean.valueOf(true));
          p.sendMessage(this.main.getConfig().getString("main-config.safe-mode.enable-message").replace("&", "§"));
          return true;
        } 
        if (!((Boolean)HarvestHoe.getSafeMode().get(p.getUniqueId())).booleanValue()) {
          HarvestHoe.getSafeMode().put(p.getUniqueId(), Boolean.valueOf(true));
          p.sendMessage(this.main.getConfig().getString("main-config.safe-mode.enable-message").replace("&", "§"));
          return true;
        } 
        HarvestHoe.getSafeMode().put(p.getUniqueId(), Boolean.valueOf(false));
        p.sendMessage(this.main.getConfig().getString("main-config.safe-mode.disable-message").replace("&", "§"));
        return true;
      } 
      if (args[0].equalsIgnoreCase("safemode") && !sender.hasPermission(this.main.getConfig().getString("main-config.safe-mode.permission-require.permission")) && this.main.getConfig().getBoolean("main-config.safe-mode.permission-require.enabled")) {
        sender.sendMessage(CustomConfigMessages.get().getString("no-permission-message").replace("&", "§"));
        return true;
      } 
      if (args[0].equalsIgnoreCase("give")) {
        if (sender.hasPermission("harvesthoe.give")) {
          if (args.length >= 2) {
            if (Bukkit.getPlayerExact(args[1]) == null) {
              sender.sendMessage("§cPlayer not found :(");
              return true;
            } 
            Player p = Bukkit.getPlayerExact(args[1]);
            PlayerInventory inventory = p.getInventory();
            ArrayList<String> lore = getLore("main-config.hoe-lore");
            ItemStack harvesthoe = new ItemStack(Material.DIAMOND_HOE, 1);
            ItemMeta harvesthoeM = harvesthoe.getItemMeta();
            harvesthoeM.setDisplayName(this.main.getConfig().getString("main-config.hoe-name").replace("&", "§"));
            harvesthoeM.setLore(lore);
            if (this.main.getConfig().getBoolean("main-config.glowing")) {
              harvesthoeM.addEnchant(Enchantment.SILK_TOUCH, 1, true);
              harvesthoeM.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
            } 
            harvesthoeM.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
            harvesthoe.setItemMeta(harvesthoeM);
            inventory.addItem(new ItemStack[] { new ItemStack(harvesthoe) });
            p.sendMessage(CustomConfigMessages.get().getString("harvesthoe-recieve-msg").replace("&", "§"));
            sender.sendMessage("§bYou gave an HarvestHoe to §c" + p.getName() + "§b !");
            return true;
          }
          sender.sendMessage("§cInssuficient arguments...");
          sender.sendMessage("§7/hh give §c<player> &3--> give HarvestHoe !");
          return true;
        } 
        sender.sendMessage(CustomConfigMessages.get().getString("no-permission-message").replace("&", "§"));
        return true;
      } 
    } else {
      sender.sendMessage("Type /hh to get HarvestHoe plugin's help");
    } 
    if (args[0].equalsIgnoreCase("boosters") && args.length == 1) {
      if (sender.hasPermission("harvesthoe.boosters.help")) {
    	  sender.sendMessage("§m                  §b§lHarvestHoe §3Boosters                  ");
        sender.sendMessage("");
        sender.sendMessage("§6 - §7/hh boosters                §e-->Shows the booster's commands");
        sender.sendMessage("§6 - §7/hh boosters list           §e-->Shows all avaible boosters");
        sender.sendMessage("§6 - §7/hh boosters check          §e-->Shows yours boosters");
        sender.sendMessage("§6 - §7/hh boosters view    §e-->Views the current value of the player's booster");
        sender.sendMessage("§6 - §7/hh boosters give §3<type> <amount>   §e-->Give a booster to a player");
        sender.sendMessage("§6 - §7/hh boosters remove §3<type>   §e-->Remove the booster of a player");
        sender.sendMessage("§6 - §7/hh boosters removeall       §e-->Remove all boosters of a player");
        sender.sendMessage("");
        sender.sendMessage("                                                                   ");
        return true;
      } 
      sender.sendMessage(CustomConfigMessages.get().getString("no-permission-message").replace("&", "§"));
      return true;
    } 
    if (args[0].equalsIgnoreCase("boosters")) {
      if (args[1].equalsIgnoreCase("remove")) {
        if (args.length == 4) {
          if (!sender.hasPermission("harvesthoe.boosters.remove")) {
            sender.sendMessage(CustomConfigMessages.get().getString("no-permission-message").replace("&", "§"));
            return true;
          } 
          if (Bukkit.getOfflinePlayer(args[2]) == null) {
            sender.sendMessage("§cPlayer not found :(");
            return true;
          } 
          OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
          if (!HarvestHoe.getBoosters().containsKey(p.getUniqueId().toString()))
            sender.sendMessage("player doesn't have any boosters"); 
          List<HashMap<String, Integer>> list = HarvestHoe.getBoosters().get(p.getUniqueId().toString());
          for (HashMap<String, Integer> map : list) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
              if (((String)entry.getKey()).equals(args[3])) {
                list.remove(map);
                CustomConfig.get().set(String.valueOf(p.getUniqueId().toString()) + ".boosters." + args[3], null);
                CustomConfig.save();
                sender.sendMessage("§3You removed a §b" + args[3] + " §3booster from the player §b" + args[2]);
                return true;
              } 
            } 
          } 
          sender.sendMessage("§cPlayer §b" + args[2] + " §3doesn't have §b" + args[3] + "§c booster !");
          sender.sendMessage("§cType §7/hh boosters view §3" + args[2] + " §cto see his boosters");
          return true;
        } 
        sender.sendMessage("§cInssuficient arguments...");
        sender.sendMessage("§cPlease try: §7/hh boosters remove §3<type>");
        return true;
      } 
      if (args[1].equalsIgnoreCase("removeall")) {
        if (args.length == 3) {
          if (!sender.hasPermission("harvesthoe.boosters.remove")) {
            sender.sendMessage(CustomConfigMessages.get().getString("no-permission-message").replace("&", "§"));
            return true;
          } 
          if (Bukkit.getOfflinePlayer(args[2]) == null) {
            sender.sendMessage("§cPlayer not found :(");
            return true;
          } 
          OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
          if (!HarvestHoe.getBoosters().containsKey(p.getUniqueId().toString()))
            sender.sendMessage("§cThis player doesn't have any boosters"); 
          HarvestHoe.getBoosters().put(p.getUniqueId().toString(), new ArrayList<>());
          CustomConfig.get().set(String.valueOf(p.getUniqueId().toString()) + ".boosters", null);
          CustomConfig.save();
          sender.sendMessage("§3You removed all boosters of " + args[2]);
          return true;
        } 
        sender.sendMessage("§cInssuficient arguments...");
        sender.sendMessage("§cPlease try: §7/hh boosters remove §3<type>");
        return true;
      } 
      if (args[1].equalsIgnoreCase("list")) {
        if (!sender.hasPermission("harvesthoe.boosters.list")) {
          sender.sendMessage(CustomConfigMessages.get().getString("no-permission-message").replace("&", "§"));
          return true;
        } 
        sender.sendMessage("       §3Avaiable Boosters:");
        sender.sendMessage("");
        for (String boosterName : CustomConfigBoosters.get().getConfigurationSection("boosters").getKeys(false))
          sender.sendMessage(" §6- §b§l" + boosterName + "   §ex" + CustomConfigBoosters.get().getString("boosters." + boosterName + ".value") + "   §c" + CustomConfigBoosters.get().getString("boosters." + boosterName + ".duration") + " seconds"); 
        sender.sendMessage("");
        return true;
      } 
      if (args[1].equalsIgnoreCase("check")) {
        if (!(sender instanceof Player)) {
          sender.sendMessage("§cThis command is only avaible for players :/");
          return true;
        } 
        if (!sender.hasPermission("harvesthoe.boosters.check")) {
          sender.sendMessage(CustomConfigMessages.get().getString("no-permission-message").replace("&", "§"));
          return true;
        } 
        Player p = (Player)sender;
        if (!HarvestHoe.getBoosters().containsKey(p.getUniqueId().toString())) {
          sender.sendMessage("       §3Active Boosters:");
          sender.sendMessage("");
          sender.sendMessage("§cNone");
          sender.sendMessage("");
          return true;
        } 
        List<HashMap<String, Integer>> list = HarvestHoe.getBoosters().get(p.getUniqueId().toString());
        sender.sendMessage("       §3Active Boosters:");
        sender.sendMessage("");
        for (HashMap<String, Integer> map : list) {
          for (Map.Entry<String, Integer> entry : map.entrySet())
            sender.sendMessage("   §b§l" + String.valueOf(entry.getKey()) + "  §3(§ex" + CustomConfigBoosters.get().getDouble("boosters." + String.valueOf(entry.getKey()) + ".value") + "§3)       §6- §b" + timeLeft(((Integer)entry.getValue()).intValue()) + " §bremaining"); 
        } 
        sender.sendMessage("");
        return true;
      } 
      if (args[1].equalsIgnoreCase("view")) {
        if (sender.hasPermission("harvesthoe.boosters.view")) {
          if (args.length == 3) {
            if (Bukkit.getOfflinePlayer(args[2]) == null) {
              sender.sendMessage("not found :(");
              return true;
            } 
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
            if (!HarvestHoe.getBoosters().containsKey(p.getUniqueId().toString())) {
              sender.sendMessage("       §3Active Boosters:");
              sender.sendMessage("");
              sender.sendMessage("§cNone");
              sender.sendMessage("");
              return true;
            } 
            List<HashMap<String, Integer>> list = HarvestHoe.getBoosters().get(p.getUniqueId().toString());
            sender.sendMessage("       §3Active Boosters:");
            sender.sendMessage("");
            for (HashMap<String, Integer> map : list) {
              for (Map.Entry<String, Integer> entry : map.entrySet())
            	  sender.sendMessage("   §b§l" + String.valueOf(entry.getKey()) + "  §3(§ex" + CustomConfigBoosters.get().getDouble("boosters." + String.valueOf(entry.getKey()) + ".value") + "§3)       §6- §b" + timeLeft(((Integer)entry.getValue()).intValue()) + " §bremaining");
            } 
            sender.sendMessage("");
            return true;
          } 
          sender.sendMessage("§cPlease specify a player");
          return true;
        } 
        sender.sendMessage(CustomConfigMessages.get().getString("no-permission-message").replace("&", "§"));
        return true;
      } 
      if (args[1].equalsIgnoreCase("give")) {
        if (sender.hasPermission("harvesthoe.boosters.give")) {
          if (args.length == 5) {
            if (Bukkit.getPlayerExact(args[2]) == null) {
              sender.sendMessage("§cPlayer not found :(");
              return true;
            } 
            Player p = Bukkit.getPlayerExact(args[2]);
            PlayerInventory inventory = p.getInventory();
            if (CustomConfigBoosters.get().getConfigurationSection("boosters." + args[3]) == null) {
              sender.sendMessage("§cThis booster doesn't exist");
              return true;
            } 
            ArrayList<String> lore = getBoosterLore("boosters." + args[3] + ".lore");
            Material mat = Material.matchMaterial(CustomConfigBoosters.get().getString("boosters." + args[3] + ".material"));
            ItemStack booster = new ItemStack(mat, Integer.valueOf(args[4]).intValue());
            ItemMeta meta = booster.getItemMeta();
            meta.setDisplayName(CustomConfigBoosters.get().getString("boosters." + args[3] + ".name").replace("&", "§"));
            meta.setLore(lore);
            NamespacedKey key = new NamespacedKey((Plugin)HarvestHoe.getInstance(), args[3]);
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, args[3]);
            if (CustomConfigBoosters.get().getBoolean("boosters." + args[3] + ".glowing")) {
              meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
              meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
            } 
            meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
            booster.setItemMeta(meta);
            inventory.addItem(new ItemStack[] { new ItemStack(booster) });
            p.sendMessage(CustomConfigMessages.get().getString("booster-recieve-msg").replace("%type%", args[3]).replace("%amount%", args[4]).replace("&", "§"));
            sender.sendMessage("§3You gave " + args[4] + " " + args[3] + " §3booster to " + p.getName() + " !");
            return true;
          } 
          sender.sendMessage("§cPlease specify a player");
          return true;
        } 
        sender.sendMessage(CustomConfigMessages.get().getString("no-permission-message").replace("&", "§"));
        return true;
      } 
    } 
    return false;
  }
  
  public ArrayList<String> getLore(String s) {
	ArrayList<String> lore = (ArrayList<String>)this.main.getConfig().getStringList(s);
    for (Integer i = Integer.valueOf(0); i.intValue() < lore.size(); i = Integer.valueOf(i.intValue() + 1)) {
      String str = lore.get(i.intValue());
      lore.set(i.intValue(), str.replace("&", "§"));
    } 
    return lore;
  }
  
  public static ArrayList<String> getBoosterLore(String s) {
    CustomConfigBoosters.reload();
    ArrayList<String> lore = (ArrayList<String>)CustomConfigBoosters.get().getStringList(s);
    for (Integer i = Integer.valueOf(0); i.intValue() < lore.size(); i = Integer.valueOf(i.intValue() + 1)) {
      String str = lore.get(i.intValue());
      lore.set(i.intValue(), str.replace("&", "§"));
    } 
    return lore;
  }
  
  public static String timeLeft(int timeoutSeconds) {
    int days = timeoutSeconds / 86400;
    int hours = timeoutSeconds / 3600 % 24;
    int minutes = timeoutSeconds / 60 % 60;
    int seconds = timeoutSeconds % 60;
    return String.valueOf((days > 0) ? (" " + days + " day" + ((days != 1) ? "s" : "")) : "") + ((hours > 0) ? (" " + hours + " hour" + ((hours != 1) ? "s" : "")) : "") + (
      (minutes > 0) ? (" " + minutes + " minute" + ((minutes != 1) ? "s" : "")) : "") + ((seconds > 0) ? (" " + seconds + " second" + ((seconds != 1) ? "s" : "")) : "");
  }
}

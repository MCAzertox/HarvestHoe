package fr.mcazertox.harvesthoe;

import java.util.ArrayList;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class OnInteractEvent implements Listener {
  private HarvestHoe main;
  
  public OnInteractEvent(HarvestHoe harvestHoe) {
    this.main = harvestHoe;
  }
  
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    String uuid = e.getPlayer().getUniqueId().toString();
    if (!CustomConfigLevels.get().getBoolean("no-levels-by-default")) {
      if (HarvestHoe.getLevels().isEmpty()) {
        HarvestHoe.getLevels().put(uuid, Integer.valueOf(1));
        CustomConfig.get().set(String.valueOf(uuid) + ".level", Integer.valueOf(1));
        CustomConfig.save();
        return;
      } 
      if (CustomConfig.get().contains(uuid)) {
        if (((Integer)HarvestHoe.getLevels().get(uuid)).intValue() == 0) {
          HarvestHoe.getLevels().put(uuid, Integer.valueOf(1));
          CustomConfig.get().set(String.valueOf(uuid) + ".level", Integer.valueOf(1));
          CustomConfig.save();
          return;
        } 
      } else if (!CustomConfig.get().contains(uuid)) {
        HarvestHoe.getLevels().put(uuid, Integer.valueOf(1));
        CustomConfig.get().set(String.valueOf(uuid) + ".level", Integer.valueOf(1));
        CustomConfig.save();
        return;
      } 
      return;
    } 
    if (HarvestHoe.getLevels().isEmpty()) {
      HarvestHoe.getLevels().put(uuid, Integer.valueOf(0));
      CustomConfig.get().set(String.valueOf(uuid) + ".level", Integer.valueOf(0));
      CustomConfig.save();
      return;
    } 
    if (!HarvestHoe.getLevels().containsKey(uuid)) {
      HarvestHoe.getLevels().put(uuid, Integer.valueOf(0));
      CustomConfig.get().set(String.valueOf(uuid) + ".level", Integer.valueOf(0));
      CustomConfig.save();
      return;
    } 
  }
  
  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Action action = event.getAction();
    ItemStack it = event.getItem();
    if (it == null)
      return; 
    if (it.getType() == Material.DIAMOND_HOE && (
      action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) && 
      it.hasItemMeta() && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equals(this.main.getConfig().getString("main-config.hoe-name").replace("&", "§")))
      if ((player.hasPermission(this.main.getConfig().getString("main-config.open-permission")) && this.main.getConfig().getBoolean("main-config.requires-permission")) || !this.main.getConfig().getBoolean("main-config.requires-permission")) {
        event.setCancelled(true);
        CustomConfigLevels.reload();
        if (this.main.getConfig().getBoolean("main-config.open-sound.enabled"))
          PlaySound(player, this.main.getConfig().getString("main-config.open-sound.sound"), Float.valueOf(10.0F), Float.valueOf(1.0F)); 
        Boolean sendmsg = Boolean.valueOf(this.main.getConfig().getBoolean("main-config.enable-open-menu-message"));
        if (sendmsg.booleanValue())
          player.sendMessage(this.main.getConfig().getString("main-config.open-menu-message").replace("&", "§")); 
        Inventory inv = Bukkit.createInventory(null, this.main.getConfig().getInt("gui.gui-size"), this.main.getConfig().getString("gui.gui-name").replace("&", "§"));
        ArrayList<String> lorequit = MaingetLore("gui.quit.lore");
        Material matQuit = null;
        matQuit = Material.matchMaterial(this.main.getConfig().getString("gui.quit.material"));
        Boolean noench = Boolean.valueOf(false);
        for (String s : this.main.getConfig().getConfigurationSection("gui.filler").getKeys(false)) {
          String[] slots = this.main.getConfig().getString("gui.filler." + s + ".slots").split(",");
          byte b;
          int i;
          String[] arrayOfString1;
          for (i = (arrayOfString1 = slots).length, b = 0; b < i; ) {
            String oneslot = arrayOfString1[b];
            int theSlot = Integer.parseInt(oneslot);
            Boolean ench = Boolean.valueOf(this.main.getConfig().getBoolean("gui.filler." + s + ".glowing"));
            Material mat = Material.matchMaterial(this.main.getConfig().getString("gui.filler." + s + ".material"));
            ArrayList<String> lore = MaingetLore("gui.filler." + s + ".lore");
            inv.setItem(theSlot, createMyItem(mat, this.main.getConfig().getString("gui.filler." + s + ".title").replace("&", "§"), lore, Integer.valueOf(1), ench));
            b++;
          } 
        } 
        for (String level : CustomConfigLevels.get().getConfigurationSection("levels").getKeys(false)) {
          ArrayList<String> lore = getLore("levels." + level + ".lore");
          Material mat = Material.matchMaterial(CustomConfigLevels.get().getString("levels." + level + ".material"));
          Integer qt = Integer.valueOf(CustomConfigLevels.get().getInt("levels." + level + ".quantity"));
          Boolean ench = Boolean.valueOf(false);
          if (HarvestHoe.getLevels().containsKey(player.getUniqueId().toString()) && (
            (Integer)HarvestHoe.getLevels().get(player.getUniqueId().toString())).intValue() >= Integer.valueOf(level).intValue()) {
            ench = Boolean.valueOf(true);
            lore = getLore("levels." + level + ".unlocked-lore");
          } 
          inv.setItem(CustomConfigLevels.get().getInt("levels." + level + ".slot"), createMyItem(mat, CustomConfigLevels.get().getString("levels." + level + ".title").replace("&", "§"), lore, qt, ench));
        } 
        inv.setItem(this.main.getConfig().getInt("gui.quit.slot"), createMyItem(matQuit, this.main.getConfig().getString("gui.quit.title").replace("&", "§"), lorequit, Integer.valueOf(1), noench));
        player.openInventory(inv);
      } else {
        player.sendMessage(this.main.getConfig().getString("main-config.no-perm-message").replace("&", "§"));
      }  
  }
  
  @EventHandler
  public void onClick(InventoryClickEvent e) {
    InventoryView inv = e.getView();
    CustomConfigLevels.reload();
    if (!inv.getTitle().toString().equals(this.main.getConfig().getString("gui.gui-name").replace("&", "§")))
      return; 
    e.setCancelled(true);
    Material quit = Material.matchMaterial(this.main.getConfig().getString("gui.quit.material"));
    try {
      if (e.getCurrentItem().getType() == quit && 
        e.getCurrentItem().getItemMeta().getDisplayName().equals(this.main.getConfig().getString("gui.quit.title").replace("&", "§"))) {
        inv.close();
        return;
      } 
    } catch (Exception e2) {
      return;
    } 
    for (String s : CustomConfigLevels.get().getConfigurationSection("levels").getKeys(false)) {
      if (e.getCurrentItem().getType() == Material.matchMaterial(CustomConfigLevels.get().getString("levels." + s + ".material")) && 
        e.getCurrentItem().getItemMeta().getDisplayName().equals(CustomConfigLevels.get().getString("levels." + s + ".title").replace("&", "§"))) {
        Player p = (Player)e.getWhoClicked();
        if (HarvestHoe.getLevels().containsKey(p.getUniqueId().toString())) {
          if (HarvestHoe.getLevels().get(p.getUniqueId().toString()) == Integer.valueOf(s)) {
            p.sendMessage(String.valueOf(this.main.getConfig().getString("main-config.prefix").replace("&", "§")) + this.main.getConfig().getString("main-config.level-already-unlocked-message").replace("%level-clicked%", s).replace("%level-player%", s).replace("&", "§"));
            return;
          } 
          if (((Integer)HarvestHoe.getLevels().get(p.getUniqueId().toString())).intValue() > Integer.valueOf(s).intValue()) {
            p.sendMessage(String.valueOf(this.main.getConfig().getString("main-config.prefix").replace("&", "§")) + this.main.getConfig().getString("main-config.level-above-unlocked-message").replace("%level-clicked%", s).replace("%level-player%", String.valueOf(HarvestHoe.getLevels().get(p.getUniqueId().toString()))).replace("&", "§"));
            return;
          } 
          if (((Integer)HarvestHoe.getLevels().get(p.getUniqueId().toString())).intValue() == Integer.valueOf(s).intValue() - 1) {
            if (!levelUp(p, s).booleanValue())
              return; 
            ArrayList<String> lore = getLore("levels." + s + ".unlocked-lore");
            Material mat = Material.matchMaterial(CustomConfigLevels.get().getString("levels." + s + ".material"));
            Integer qt = Integer.valueOf(CustomConfigLevels.get().getInt("levels." + s + ".quantity"));
            inv.setItem(CustomConfigLevels.get().getInt("levels." + s + ".slot"), null);
            Boolean ench = Boolean.valueOf(true);
            inv.setItem(CustomConfigLevels.get().getInt("levels." + s + ".slot"), createMyItem(mat, CustomConfigLevels.get().getString("levels." + s + ".title").replace("&", "§"), lore, qt, ench));
            return;
          } 
          p.sendMessage(String.valueOf(this.main.getConfig().getString("main-config.prefix").replace("&", "§")) + this.main.getConfig().getString("main-config.buy-lower-level-first-message").replace("%level-required%", String.valueOf(Integer.valueOf(s).intValue() - 1)).replace("&", "§"));
          return;
        } 
      } 
    } 
  }
  
  public Boolean levelUp(Player p, String level) {
    CustomConfigLevels.reload();
    Economy economy = HarvestHoe.getEconomy();
    if (economy.getBalance((OfflinePlayer)p) >= CustomConfigLevels.get().getInt("levels." + level + ".price-to-upgrade")) {
      economy.withdrawPlayer((OfflinePlayer)p, CustomConfigLevels.get().getInt("levels." + level + ".price-to-upgrade"));
      HarvestHoe.getLevels().put(p.getUniqueId().toString(), Integer.valueOf(level));
      p.sendMessage(String.valueOf(this.main.getConfig().getString("main-config.prefix").replace("&", "§")) + this.main.getConfig().getString("main-config.levelup-message").replace("%level%", String.valueOf(Integer.valueOf(level).intValue() - 1)).replace("%to-level%", level).replace("&", "§"));
      CustomConfig.get().set(String.valueOf(p.getUniqueId().toString()) + ".level", Integer.valueOf(level));
      CustomConfig.save();
      if (CustomConfigLevels.get().getBoolean("levels." + level + ".levelup-sound.enabled"))
        PlaySound(p, CustomConfigLevels.get().getString("levels." + level + ".levelup-sound.sound"), Float.valueOf(10.0F), Float.valueOf(1.0F)); 
      return Boolean.valueOf(true);
    } 
    String message = this.main.getConfig().getString("main-config.not-enought-money-message");
    message = message.replace("&", "§");
    message = message.replace("%level%", String.valueOf(Integer.valueOf(level).intValue() - 1));
    message = message.replace("%to-level%", level);
    p.sendMessage(message);
    return Boolean.valueOf(false);
  }
  
  public void PlaySound(Player player, String sound, Float Volume, Float Pitch) {
    Sound thesound = null;
    thesound = Sound.valueOf(sound);
    player.playSound(player.getLocation(), thesound, Volume.floatValue(), Pitch.floatValue());
  }
  
  public ArrayList<String> MaingetLore(String s) {
	ArrayList<String> lore = (ArrayList<String>)this.main.getConfig().getStringList(s);
    for (Integer i = Integer.valueOf(0); i.intValue() < lore.size(); i = Integer.valueOf(i.intValue() + 1)) {
      String str = lore.get(i.intValue());
      lore.set(i.intValue(), str.replace("&", "§"));
    } 
    return lore;
  }
  
  public ArrayList<String> getLore(String s) {
    CustomConfigLevels.reload();
	ArrayList<String> lore = (ArrayList<String>)CustomConfigLevels.get().getStringList(s);
    for (Integer i = Integer.valueOf(0); i.intValue() < lore.size(); i = Integer.valueOf(i.intValue() + 1)) {
      String str = lore.get(i.intValue());
      lore.set(i.intValue(), str.replace("&", "§"));
    } 
    return lore;
  }
  
  public ItemStack createMyItem(Material material, String customName, ArrayList<String> lore, Integer quantity, Boolean ench) {
    ItemStack it = new ItemStack(material, quantity.intValue());
    ItemMeta itM = it.getItemMeta();
    if (customName != null)
      itM.setDisplayName(customName); 
    if (lore != null)
      itM.setLore(lore); 
    if (ench.booleanValue()) {
      itM.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
      itM.addEnchant(Enchantment.SILK_TOUCH, 1, true);
    } 
    it.setItemMeta(itM);
    return it;
  }
}

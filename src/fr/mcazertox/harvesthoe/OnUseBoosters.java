package fr.mcazertox.harvesthoe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class OnUseBoosters implements Listener {
  @EventHandler
  public void onRightClick(PlayerInteractEvent e) {
    if (e.getAction() != Action.RIGHT_CLICK_AIR)
      return; 
    String type = " ";
    ItemStack it = e.getPlayer().getInventory().getItemInMainHand();
    CustomConfigBoosters.reload();
    for (String s : CustomConfigBoosters.get().getConfigurationSection("boosters").getKeys(false)) {
      Material mat = Material.matchMaterial(CustomConfigBoosters.get().getString("boosters." + s + ".material"));
      if (mat == it.getType() && 
        it.hasItemMeta()) {
        NamespacedKey key = new NamespacedKey((Plugin)HarvestHoe.getInstance(), s);
        if (it.getItemMeta().getPersistentDataContainer().getKeys().contains(key))
          type = s; 
      } 
    } 
    if (type == " ")
      return; 
    Player p = e.getPlayer();
    String uuid = p.getUniqueId().toString();
    HashMap<String, Integer> booster = new HashMap<>();
    if (HarvestHoe.getBoosters().containsKey(uuid))
      for (HashMap<String, Integer> boosters : HarvestHoe.getBoosters().get(uuid)) {
        if (boosters.containsKey(type)) {
          e.getPlayer().sendMessage(CustomConfigMessages.get().getString("booster-already-enabled-message").replace("%type%", type).replace("&", "§"));
          return;
        } 
      }  
    booster.put(type, Integer.valueOf(CustomConfigBoosters.get().getInt("boosters." + type + ".duration")));
    List<HashMap<String, Integer>> list = new ArrayList<>();
    if (HarvestHoe.getBoosters().get(uuid) != null)
      list = HarvestHoe.getBoosters().get(uuid); 
    list.add(booster);
    HarvestHoe.getBoosters().put(uuid, list);
    it.setAmount(it.getAmount() - 1);
    if (CustomConfigBoosters.get().getBoolean("boosters." + type + ".activate-sound.enabled"))
      PlaySound(e.getPlayer(), CustomConfigBoosters.get().getString("boosters." + type + ".activate-sound.sound"), Float.valueOf(10.0F), Float.valueOf(1.0F)); 
    e.getPlayer().sendMessage(CustomConfigMessages.get().getString("booster-active-message").replace("%type%", type).replace("&", "§"));
  }
  
  public void PlaySound(Player player, String sound, Float Volume, Float Pitch) {
    Sound thesound = null;
    thesound = Sound.valueOf(sound);
    player.playSound(player.getLocation(), thesound, Volume.floatValue(), Pitch.floatValue());
  }
}

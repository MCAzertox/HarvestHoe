package fr.mcazertox.harvesthoe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_16_R2.ChatMessageType;
import net.minecraft.server.v1_16_R2.IChatBaseComponent;
import net.minecraft.server.v1_16_R2.IChatMutableComponent;
import net.minecraft.server.v1_16_R2.Packet;
import net.minecraft.server.v1_16_R2.PacketPlayOutChat;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftCocoa;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LeftClickBreakBlocks implements Listener {
  private HarvestHoe main;
  
  public LeftClickBreakBlocks(HarvestHoe harvestHoe) {
    this.main = harvestHoe;
  }
  
  @EventHandler
  public void leftClick(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    ItemStack it = e.getItem();
    if (e.getAction() != Action.LEFT_CLICK_BLOCK)
      return; 
    if (p.getInventory().getItemInMainHand().getType() == Material.AIR)
      return; 
    if (it.getType() != Material.DIAMOND_HOE)
      return; 
    if (!it.hasItemMeta())
      return; 
    if (!it.getItemMeta().hasDisplayName())
      return; 
    if (!it.getItemMeta().getDisplayName().equals(this.main.getConfig().getString("main-config.hoe-name").replace("&", "§")))
      return; 
    Block b = e.getClickedBlock();
    if (b.getType() == null)
      return; 
    if (b.getType() != Material.PUMPKIN && b.getType() != Material.MELON && b.getType() != Material.COCOA)
      return; 
    CraftCocoa cocoa = null;
    String type = "";
    CustomConfigLevels.reload();
    String level = ((Integer)HarvestHoe.getLevels().get(p.getUniqueId().toString())).toString();
    if (b.getBlockData() instanceof CraftCocoa) {
      cocoa = (CraftCocoa)b.getBlockData();
      type = "cocoabeans";
      if (cocoa.getAge() == cocoa.getMaximumAge() && CustomConfigLevels.get().getBoolean("levels." + HarvestHoe.getLevels().get(p.getUniqueId().toString()) + ".potatoes.enabled")) {
        if (CustomConfigLevels.get().getBoolean("levels." + level + "." + type + ".leftclick-instant-break"))
          breakCrops(b, p, type, level); 
      } else if (HarvestHoe.getSafeMode().containsKey(p.getUniqueId()) && (
        (Boolean)HarvestHoe.getSafeMode().get(p.getUniqueId())).booleanValue()) {
        e.setCancelled(true);
        return;
      } 
      return;
    } 
    if (b.getType() == Material.PUMPKIN) {
      type = "pumpkins";
    } else {
      type = "melons";
    } 
    if (CustomConfigLevels.get().getBoolean("levels." + level + "." + type + ".leftclick-instant-break"))
      breakCrops(b, p, type, level); 
  }
  
  public void breakCrops(Block b, Player p, String type, String level) {
    CustomConfig.setup();
    sell(p, level, type, Integer.valueOf(1), b);
    if (this.main.getConfig().getBoolean("sounds." + type + ".enabled"))
      PlaySound(p, this.main.getConfig().getString("sounds." + type + ".sound"), Float.valueOf(10.0F), Float.valueOf(1.0F)); 
    if (type == "cocoabeans" && 
      CustomConfigLevels.get().getBoolean("levels." + level + "." + type + ".auto-replant")) {
      CraftCocoa coco = (CraftCocoa)b.getBlockData();
      BlockFace face = coco.getFacing();
      b.setType(b.getType());
      coco = (CraftCocoa)b.getBlockData();
      coco.setFacing(face);
      BlockState st = b.getState();
      st.setBlockData((BlockData)coco);
      st.update();
      return;
    } 
    b.setType(Material.AIR);
  }
  
  public void sell(Player p, String level, String type, Integer amount, Block b) {
    Economy economy = HarvestHoe.getEconomy();
    CustomConfigLevels.reload();
    Double sellPrice = Double.valueOf(CustomConfigLevels.get().getDouble("levels." + level + "." + type + ".sell-price") * amount.intValue());
    Double boosterValue = Double.valueOf(1.0D);
    Boolean hasBooster = Boolean.valueOf(false);
    if (HarvestHoe.getBoosters().containsKey(p.getUniqueId().toString())) {
      List<HashMap<String, Integer>> list = HarvestHoe.getBoosters().get(p.getUniqueId().toString());
      for (HashMap<String, Integer> map : list) {
        for (Map.Entry<String, Integer> OneBoosterEntry : map.entrySet()) {
          boosterValue = Double.valueOf(boosterValue.doubleValue() + CustomConfigBoosters.get().getDouble("boosters." + (String)OneBoosterEntry.getKey() + ".value") - 1.0D);
          hasBooster = Boolean.valueOf(true);
        } 
      } 
    } 
    if (hasBooster.booleanValue())
      sellPrice = Double.valueOf(Math.round(sellPrice.doubleValue() * boosterValue.doubleValue() * 100.0D) / 100.0D); 
    economy.depositPlayer((OfflinePlayer)p, sellPrice.doubleValue());
    if (CustomConfigMessages.get().getBoolean(String.valueOf(type) + ".tchat.enabled")) {
      String message = String.valueOf(CustomConfigMessages.get().getString("prefix-sell-messages")) + CustomConfigMessages.get().getString(String.valueOf(type) + ".tchat.message");
      message = message.replace("&", "§");
      message = message.replace("%quantity%", amount.toString());
      message = message.replace("%price%", sellPrice.toString());
      message = message.replace("%player%", p.getName());
      p.sendMessage(message);
    } 
    if (CustomConfigMessages.get().getBoolean(String.valueOf(type) + ".hotbar.enabled")) {
      String message = CustomConfigMessages.get().getString(String.valueOf(type) + ".hotbar.message");
      message = message.replace("&", "§");
      message = message.replace("%quantity%", amount.toString());
      message = message.replace("%price%", sellPrice.toString());
      message = message.replace("%player%", p.getName());
      sendActionBar(p, message);
    } 
    if (CustomConfigMessages.get().getBoolean(String.valueOf(type) + ".hologram.enabled")) {
      String message = CustomConfigMessages.get().getString(String.valueOf(type) + ".hologram.message");
      message = message.replace("&", "§");
      message = message.replace("%quantity%", amount.toString());
      message = message.replace("%price%", sellPrice.toString());
      message = message.replace("%player%", p.getName());
      OnBreakEvent.sendHologram(b.getLocation(), message, type);
    } 
  }
  
  @SuppressWarnings("rawtypes")
public void sendActionBar(Player player, String message) {
    CraftPlayer p = (CraftPlayer)player;
    IChatMutableComponent iChatMutableComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
    PacketPlayOutChat ppoc = new PacketPlayOutChat((IChatBaseComponent)iChatMutableComponent, ChatMessageType.GAME_INFO, p.getUniqueId());
    (p.getHandle()).playerConnection.sendPacket((Packet)ppoc);
  }
  
  public void PlaySound(Player player, String sound, Float Volume, Float Pitch) {
    Sound thesound = null;
    thesound = Sound.valueOf(sound);
    player.playSound(player.getLocation(), thesound, Volume.floatValue(), Pitch.floatValue());
  }
}

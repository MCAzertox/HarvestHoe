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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftBeetroot;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftCarrots;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftCocoa;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftCrops;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftNetherWart;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftPotatoes;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;

public class OnBreakEvent implements Listener {
  private HarvestHoe main;
  
  public OnBreakEvent(HarvestHoe harvestHoe) {
    this.main = harvestHoe;
  }
  
  @SuppressWarnings("deprecation")
@EventHandler
  public void onBreak(BlockBreakEvent e) {
    Block b = e.getBlock();
    Player p = e.getPlayer();
    ItemStack it = e.getPlayer().getInventory().getItemInMainHand();
    if (it.getType() != Material.DIAMOND_HOE)
      return; 
    if (!it.hasItemMeta())
      return; 
    if (!it.getItemMeta().hasDisplayName())
      return; 
    if (!it.getItemMeta().getDisplayName().equals(this.main.getConfig().getString("main-config.hoe-name").replace("&", "§")))
      return; 
    it.setDurability((short)0);
    if (b.getType() != Material.WHEAT && 
      b.getType() != Material.CARROTS && 
      b.getType() != Material.POTATOES && 
      b.getType() != Material.COCOA && 
      b.getType() != Material.NETHER_WART && 
      b.getType() != Material.SUGAR_CANE && 
      b.getType() != Material.PUMPKIN && 
      b.getType() != Material.MELON && 
      b.getType() != Material.BEETROOTS)
      return;
    CraftCrops wheat = null;
    CraftCarrots carrots = null;
    CraftPotatoes potatoes = null;
    CraftNetherWart netherwarts = null;
    CraftCocoa cocoa = null;
    CraftBeetroot beetroot = null;
    if (!HarvestHoe.getLevels().containsKey(p.getUniqueId().toString())) {
      p.sendMessage("§cYou don't have levels...");
      return;
    }
    if (b.getBlockData() instanceof CraftCrops) {
      wheat = (CraftCrops)b.getBlockData();
    } else if (b.getBlockData() instanceof CraftCarrots) {
      carrots = (CraftCarrots)b.getBlockData();
    } else if (b.getBlockData() instanceof CraftPotatoes) {
      potatoes = (CraftPotatoes)b.getBlockData();
    } else if (b.getBlockData() instanceof CraftNetherWart) {
      netherwarts = (CraftNetherWart)b.getBlockData();
    } else if (b.getBlockData() instanceof CraftCocoa) {
      cocoa = (CraftCocoa)b.getBlockData();
    } else if (b.getBlockData() instanceof CraftBeetroot) {
      beetroot = (CraftBeetroot)b.getBlockData();
    }
    if (b.getType() == Material.WHEAT) {
      if (wheat.getAge() == wheat.getMaximumAge() && 
        CustomConfigLevels.get().getBoolean("levels." + HarvestHoe.getLevels().get(p.getUniqueId().toString()) + ".wheat.enabled")) {
        e.setCancelled(true);
        breakCrops(e, "wheat", ((Integer)HarvestHoe.getLevels().get(p.getUniqueId().toString())).toString());
        return;
      } 
      if (HarvestHoe.getSafeMode().containsKey(p.getUniqueId()) && (
        (Boolean)HarvestHoe.getSafeMode().get(p.getUniqueId())).booleanValue()) {
        e.setCancelled(true);
        return;
      } 
      return;
    } 
    if (b.getType() == Material.CARROTS) {
      if (carrots.getAge() == carrots.getMaximumAge() && CustomConfigLevels.get().getBoolean("levels." + HarvestHoe.getLevels().get(p.getUniqueId().toString()) + ".carrots.enabled")) {
        e.setCancelled(true);
        breakCrops(e, "carrots", ((Integer)HarvestHoe.getLevels().get(p.getUniqueId().toString())).toString());
        return;
      } 
      if (HarvestHoe.getSafeMode().containsKey(p.getUniqueId()) && (
        (Boolean)HarvestHoe.getSafeMode().get(p.getUniqueId())).booleanValue()) {
        e.setCancelled(true);
        return;
      } 
      return;
    } 
    if (b.getType() == Material.POTATOES) {
      if (potatoes.getAge() == potatoes.getMaximumAge() && CustomConfigLevels.get().getBoolean("levels." + HarvestHoe.getLevels().get(p.getUniqueId().toString()) + ".potatoes.enabled")) {
        e.setCancelled(true);
        breakCrops(e, "potatoes", ((Integer)HarvestHoe.getLevels().get(p.getUniqueId().toString())).toString());
        return;
      } 
      if (HarvestHoe.getSafeMode().containsKey(p.getUniqueId()) && (
        (Boolean)HarvestHoe.getSafeMode().get(p.getUniqueId())).booleanValue()) {
        e.setCancelled(true);
        return;
      } 
      return;
    } 
    if (b.getType() == Material.NETHER_WART) {
      if (netherwarts.getAge() == netherwarts.getMaximumAge() && CustomConfigLevels.get().getBoolean("levels." + HarvestHoe.getLevels().get(p.getUniqueId().toString()) + ".netherwarts.enabled")) {
        e.setCancelled(true);
        breakCrops(e, "netherwarts", ((Integer)HarvestHoe.getLevels().get(p.getUniqueId().toString())).toString());
        return;
      } 
      if (HarvestHoe.getSafeMode().containsKey(p.getUniqueId()) && (
        (Boolean)HarvestHoe.getSafeMode().get(p.getUniqueId())).booleanValue()) {
        e.setCancelled(true);
        return;
      } 
      return;
    } 
    if (b.getType() == Material.COCOA) {
      if (cocoa.getAge() == cocoa.getMaximumAge() && CustomConfigLevels.get().getBoolean("levels." + HarvestHoe.getLevels().get(p.getUniqueId().toString()) + ".potatoes.enabled")) {
        e.setCancelled(true);
        breakCrops(e, "cocoabeans", ((Integer)HarvestHoe.getLevels().get(p.getUniqueId().toString())).toString());
        return;
      } 
      if (HarvestHoe.getSafeMode().containsKey(p.getUniqueId()) && (
        (Boolean)HarvestHoe.getSafeMode().get(p.getUniqueId())).booleanValue()) {
        e.setCancelled(true);
        return;
      } 
      return;
    } 
    if (b.getType() == Material.BEETROOTS) {
      if (beetroot.getAge() == beetroot.getMaximumAge() && CustomConfigLevels.get().getBoolean("levels." + HarvestHoe.getLevels().get(p.getUniqueId().toString()) + ".beetroots.enabled")) {
        e.setCancelled(true);
        breakCrops(e, "beetroots", ((Integer)HarvestHoe.getLevels().get(p.getUniqueId().toString())).toString());
        return;
      } 
      if (HarvestHoe.getSafeMode().containsKey(p.getUniqueId()) && (
        (Boolean)HarvestHoe.getSafeMode().get(p.getUniqueId())).booleanValue()) {
        e.setCancelled(true);
        return;
      } 
      return;
    } 
    if (b.getType() == Material.PUMPKIN) {
      if (CustomConfigLevels.get().getBoolean("levels." + HarvestHoe.getLevels().get(p.getUniqueId().toString()) + ".pumpkins.enabled")) {
        e.setCancelled(true);
        breakCrops(e, "pumpkins", ((Integer)HarvestHoe.getLevels().get(p.getUniqueId().toString())).toString());
        return;
      } 
      if (HarvestHoe.getSafeMode().containsKey(p.getUniqueId()) && (
        (Boolean)HarvestHoe.getSafeMode().get(p.getUniqueId())).booleanValue()) {
        e.setCancelled(true);
        return;
      } 
      return;
    } 
    if (b.getType() == Material.MELON) {
      if (CustomConfigLevels.get().getBoolean("levels." + HarvestHoe.getLevels().get(p.getUniqueId().toString()) + ".melons.enabled")) {
        e.setCancelled(true);
        breakCrops(e, "melons", ((Integer)HarvestHoe.getLevels().get(p.getUniqueId().toString())).toString());
        return;
      } 
      if (HarvestHoe.getSafeMode().containsKey(p.getUniqueId()) && (
        (Boolean)HarvestHoe.getSafeMode().get(p.getUniqueId())).booleanValue()) {
        e.setCancelled(true);
        return;
      } 
      return;
    } 
    if (b.getType() == Material.SUGAR_CANE && 
      CustomConfigLevels.get().getBoolean("levels." + HarvestHoe.getLevels().get(p.getUniqueId().toString()) + ".scane.enabled")) {
      Integer plus = Integer.valueOf(0);
      e.setCancelled(true);
      if (b.getLocation().add(0.0D, -1.0D, 0.0D).getBlock().getType() != Material.SUGAR_CANE && 
        HarvestHoe.getSafeMode().containsKey(p.getUniqueId()) && (
        (Boolean)HarvestHoe.getSafeMode().get(p.getUniqueId())).booleanValue())
        plus = Integer.valueOf(1); 
      Block bloc = b.getLocation().add(0.0D, plus.intValue(), 0.0D).getBlock();
      if (bloc.getType() != Material.SUGAR_CANE)
        return; 
      Integer high = Integer.valueOf(0);
      while (bloc.getLocation().add(0.0D, high.intValue(), 0.0D).getBlock().getType() == Material.SUGAR_CANE)
        high = Integer.valueOf(high.intValue() + 1); 
      bloc = bloc.getLocation().add(0.0D, (high.intValue() - 1), 0.0D).getBlock();
      for (int i = high.intValue(); i > 0; i--) {
        bloc.setType(Material.AIR);
        bloc = bloc.getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
      } 
      sell(p, ((Integer)HarvestHoe.getLevels().get(p.getUniqueId().toString())).toString(), "scane", high, b);
      if (this.main.getConfig().getBoolean("sounds.scane.enabled"))
        PlaySound(p, this.main.getConfig().getString("sounds.scane.sound"), Float.valueOf(10.0F), Float.valueOf(1.0F)); 
    } 
  }
  
  public void breakCrops(BlockBreakEvent e, String type, String level) {
    CustomConfig.setup();
    CustomConfigLevels.reload();
    Player p = e.getPlayer();
    Block b = e.getBlock();
    sell(p, level, type, Integer.valueOf(1), e.getBlock());
    if (this.main.getConfig().getBoolean("sounds." + type + ".enabled"))
      PlaySound(p, this.main.getConfig().getString("sounds." + type + ".sound"), Float.valueOf(10.0F), Float.valueOf(1.0F)); 
    if (type != "scane" && type != "pumpkins" && type != "melons" && type != "cocoabeans" && 
      CustomConfigLevels.get().getBoolean("levels." + level + "." + type + ".auto-replant")) {
      e.getBlock().setType(e.getBlock().getType());
      return;
    } 
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
    e.getBlock().setType(Material.AIR);
  }
  
  public void sell(Player p, String level, String type, Integer amount, Block b) {
    Economy economy = HarvestHoe.getEconomy();
    CustomConfigMessages.reload();
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
      sendHologram(b.getLocation(), message, type);
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
  
  public static void sendHologram(Location loc, final String message, String type) {
    Double d = Double.valueOf(Math.random() * 2.0D - Math.random() * 2.0D);
    Double d2 = Double.valueOf(Math.random() * 2.0D - Math.random() * 2.0D);
    final ArmorStand stand = (ArmorStand)loc.getWorld().spawn(loc.add(d.doubleValue() + 0.5D, 1.0D, d2.doubleValue() + 0.5D), ArmorStand.class, new Consumer<ArmorStand>() {
          public void accept(ArmorStand stand) {
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setCanPickupItems(false);
            stand.setCustomName(message);
            stand.setCustomNameVisible(true);
            stand.setMarker(true);
          }
        });
    HarvestHoe.getHolos().add(stand);
    Integer delay = Integer.valueOf(CustomConfigMessages.get().getInt(String.valueOf(type) + ".hologram.delay"));
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)HarvestHoe.getPlugin(HarvestHoe.class), new Runnable() {
          public void run() {
            stand.remove();
            HarvestHoe.getHolos().remove(stand);
          }
        },  delay.intValue());
  }
}

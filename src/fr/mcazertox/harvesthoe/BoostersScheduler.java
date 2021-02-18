package fr.mcazertox.harvesthoe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BoostersScheduler {
  
  public static void startDecreasingBoosters() {
	(new BukkitRunnable() {
		public void run() {
			for (Map.Entry<String, List<HashMap<String, Integer>>> l : HarvestHoe.getBoosters().entrySet()) {
				for (HashMap<String, Integer> boosters : l.getValue()) {
					for (Map.Entry<String, Integer> OneBoosterEntry : boosters.entrySet()) {
						if (((Integer)OneBoosterEntry.getValue()).intValue() < 1) {
							boosters.remove(OneBoosterEntry.getKey());
							Bukkit.getPlayer(UUID.fromString(l.getKey())).sendMessage(CustomConfigMessages.get().getString("booster-ended-message").replace("%type%", OneBoosterEntry.getKey()).replace("&", "§"));
									CustomConfig.get().set(String.valueOf(l.getKey()) + ".boosters." + (String)OneBoosterEntry.getKey(), null);
							CustomConfig.save();
	                  continue;
						} 
						OneBoosterEntry.setValue(Integer.valueOf(((Integer)OneBoosterEntry.getValue()).intValue() - 1));
					} 
				} 
			} 
		}
		}).runTaskTimer(HarvestHoe.getInstance(), 0L, 20L);
	}
}

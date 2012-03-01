package com.reil.bukkit.MonsterTriggers;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.reil.bukkit.rTriggers.rTriggers;

public class MTListener implements Listener {
	rTriggers rTriggersPlugin;
	Logger log = Logger.getLogger("Minecraft");
	HashMap<Integer, Integer> targetMap = new HashMap<Integer, Integer>();
	HashMap<Integer, String> killerMap = new HashMap<Integer, String>();

	/**
	 * @param rTriggers
	 */
	MTListener(rTriggers rTriggers) {
		this.rTriggersPlugin = rTriggers;
	}
	
	@EventHandler
	public void onEntityTarget (EntityTargetEvent event){
		int previousTarget;
		Entity target = event.getTarget();
		Entity targeter = event.getEntity();
		String targeterName = rTriggers.getName(targeter);
		TargetReason reason = event.getReason();
		if (reason == TargetReason.CLOSEST_PLAYER ||
				reason == TargetReason.TARGET_ATTACKED_ENTITY ||
				reason == TargetReason.PIG_ZOMBIE_TARGET){
			if (targetMap.containsKey(targeter.getEntityId()))
				previousTarget = targetMap.get(targeter.getEntityId());
			else
				previousTarget = target.getEntityId() - 1;
			if (target instanceof Player && previousTarget != target.getEntityId()){
				if (((Player) target).getHealth() == 0) return;
				targetMap.put(targeter.getEntityId(),target.getEntityId());
				Player targetPlayer = (Player) target;
				String[] eventToReplace =  {"<<targeter>>"};
				String []eventReplaceWith = {targeterName};
				rTriggersPlugin.triggerMessages(targetPlayer, "targetsplayer", eventToReplace, eventReplaceWith);
				rTriggersPlugin.triggerMessages(targetPlayer, "targetsplayer|" + targeterName,eventToReplace,eventReplaceWith);
			}
		}
	}
	
	
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event ){
		String damageCause; 
		String triggerOption;
		Entity gotHurt = event.getEntity();
		
		String damaged = rTriggers.getName(gotHurt);
		triggerOption = event.getCause().toString().toLowerCase();
		damageCause = rTriggers.damageCauseNatural(event.getCause());
		
		if (event instanceof EntityDamageByEntityEvent) {
			Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
			if (damager instanceof Player)
				killerMap.put(gotHurt.getEntityId(), ((Player) damager).getName());
			else if (gotHurt instanceof Player)
				killerMap.put(gotHurt.getEntityId(), rTriggers.getName(damager));
		} else killerMap.remove(gotHurt.getEntityId());
		
		String [] replaceThese = {"<<damage-cause>>"};
		String [] withThese = {damageCause};
		rTriggersPlugin.triggerMessages("mobdamage|" + damaged + "|" + triggerOption,replaceThese,withThese);
		rTriggersPlugin.triggerMessages("mobdamage|" + damaged                      ,replaceThese,withThese);
	}
	
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		Entity isDead = event.getEntity();
		int deadId = isDead.getEntityId();
		if (!killerMap.containsKey(deadId)) return;
		if (isDead instanceof Player) {
			String killer = killerMap.get(isDead.getEntityId());
			Player isDeadP = (Player) isDead;
			String [] replaceThese = {"<<killer>>"};
			String [] withThese    = {killer};
			
			if (rTriggersPlugin.triggerMessages(isDeadP, "playerkilledbymob",replaceThese,withThese) |
					rTriggersPlugin.triggerMessages(isDeadP, "playerkilledbymob|" + killer,replaceThese,withThese)){
				log.info("[MonsterTriggers] Outdated option: playerkilledbymob.  Please use mobkillsplayer instead;");
			}
			
			rTriggersPlugin.triggerMessages(isDeadP, "mobkillsplayer",replaceThese,withThese);
			rTriggersPlugin.triggerMessages(isDeadP, "mobkillsplayer|" + killer,replaceThese,withThese);
		} else {
			String deadMob = rTriggers.getName(isDead);
		
			Player killer = rTriggersPlugin.getServer().getPlayer(killerMap.get(deadId));
			if (killer == null) return;
			String murderWeapon = killer.getItemInHand().getType().toString().toLowerCase().replace("_", " ");
			if (murderWeapon.equals("air")) murderWeapon = "fist";
			String [] replaceThese = {"<<weapon>>", "<<mob>>"};
			String [] withThese    = {murderWeapon, deadMob};
			
			if (rTriggersPlugin.triggerMessages(killer, "mobkilledbyplayer",replaceThese,withThese) |
					rTriggersPlugin.triggerMessages(killer, "mobkilledbyplayer|" + deadMob,replaceThese,withThese)|
					rTriggersPlugin.triggerMessages(killer, "mobkilledbyplayer|" + deadMob  + "|" + murderWeapon,replaceThese,withThese)){
				log.info("[MonsterTriggers] Outdated option: mobkilledbyplayer.  Please use playerkillsmob instead;");
			}
			
			rTriggersPlugin.triggerMessages(killer, "playerkillsmob",replaceThese,withThese);
			rTriggersPlugin.triggerMessages(killer, "playerkillsmob|" + deadMob,replaceThese,withThese);
			rTriggersPlugin.triggerMessages(killer, "playerkillsmob|" + deadMob  + "|" + murderWeapon,replaceThese,withThese);
		}
	}
	
	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent event){
		log.info("Explosion primed!");
		if (!(event.getEntity() instanceof Creeper)) return; // Should not ever happen. o_o
		Creeper gonnaBlow = (Creeper) event.getEntity();
		if (!(gonnaBlow.getTarget() instanceof Player)) return;
		Player target = (Player) gonnaBlow.getTarget();
		
		rTriggersPlugin.triggerMessages(target, "creeperfuse");
		event.setCancelled(rTriggersPlugin.triggerMessages(target, "creeperfuse|override"));
	}
}
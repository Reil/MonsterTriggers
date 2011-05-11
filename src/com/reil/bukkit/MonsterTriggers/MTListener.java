package com.reil.bukkit.MonsterTriggers;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.reil.bukkit.rTriggers.rTriggers;

public class MTListener extends EntityListener {
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
	
	@Override
	public void onEntityTarget (EntityTargetEvent event){
		int previousTarget;
		Entity target = event.getTarget();
		Entity targeter = event.getEntity();
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
				Player targetPlayer =(Player) target;
				String targeterName = targeter.getClass().getName();
				targeterName = targeterName.substring(targeterName.lastIndexOf("Craft") + "Craft".length());
				String[] eventToReplace =  {"<<targeter>>"};
				String []eventReplaceWith = {targeterName};
				rTriggersPlugin.triggerMessages("targetsplayer", eventToReplace, eventReplaceWith);
				rTriggersPlugin.triggerMessages("targetsplayer|" + targeterName,eventToReplace,eventReplaceWith);
				rTriggersPlugin.triggerMessages(targetPlayer, "targetsplayer", eventToReplace, eventReplaceWith);
				rTriggersPlugin.triggerMessages(targetPlayer, "targetsplayer|" + targeterName,eventToReplace,eventReplaceWith);
			}
		}
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event ){
		String damageCause; 
		String triggerOption;
		Entity gotHurt = event.getEntity();
		
		String damaged = gotHurt.getClass().getName();
		damaged = damaged.substring(damaged.lastIndexOf("Craft") + 5);
		triggerOption = event.getCause().toString().toLowerCase();
		damageCause = rTriggers.damageCauseNatural(event.getCause());
		
		String [] replaceThese = {"<<damage-cause>>"};
		String [] withThese = {damageCause};
		rTriggersPlugin.triggerMessages("mobdamage|" + damaged + "|" + triggerOption,replaceThese,withThese);
		rTriggersPlugin.triggerMessages("mobdamage|" + damaged                      ,replaceThese,withThese);
		
		if (event instanceof EntityDamageByEntityEvent) {
			Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
			if (damager instanceof Player)
				killerMap.put(gotHurt.getEntityId(), ((Player) damager).getName());
			else if (gotHurt instanceof Player)
				killerMap.put(gotHurt.getEntityId(), damager.getClass().getName().substring(damager.getClass().getName().lastIndexOf("Craft") + 5));
		} else killerMap.remove(gotHurt.getEntityId());
	}
	
	
	@Override
	public void onEntityDeath(EntityDeathEvent event){
		Entity isDead = event.getEntity();
		int deadId = isDead.getEntityId();
		if (!killerMap.containsKey(deadId)) return;
		if (isDead instanceof Player) {
			String killer = killerMap.get(isDead.getEntityId());
			Player isDeadP = (Player) isDead;
			String [] replaceThese = {"<<killer>>"};
			String [] withThese    = {killer};
			
			rTriggersPlugin.triggerMessages(isDeadP, "playerkilledbymob",replaceThese,withThese);
			rTriggersPlugin.triggerMessages(isDeadP, "playerkilledbymob|" + killer,replaceThese,withThese);
		} else {
			String deadMob = isDead.getClass().getName();
			deadMob = deadMob.substring(deadMob.lastIndexOf("Craft") + 5);
		
			Player killer = rTriggersPlugin.getServer().getPlayer(killerMap.get(deadId));
			String murderWeapon = killer.getItemInHand().getType().toString().toLowerCase().replace("_", " ");
			if (murderWeapon.equals("air")) murderWeapon = "fist";
			String [] replaceThese = {"<<weapon>>", "<<mob>>"};
			String [] withThese    = {murderWeapon, deadMob};
			
			rTriggersPlugin.triggerMessages(killer, "mobkilledbyplayer",replaceThese,withThese);
			rTriggersPlugin.triggerMessages(killer, "mobkilledbyplayer|" + deadMob,replaceThese,withThese);
			rTriggersPlugin.triggerMessages(killer, "mobkilledbyplayer|" + deadMob  + "|" + murderWeapon,replaceThese,withThese);
		
		}
	}
}
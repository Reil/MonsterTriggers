package com.reil.bukkit.MonsterTriggers;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.reil.bukkit.rTriggers.rTriggers;

public class MTListener extends EntityListener {
	rTriggers rTriggers;
	Logger log = Logger.getLogger("Minecraft");
	HashMap<Integer, Integer> targetMap = new HashMap<Integer, Integer>();
	HashMap<Integer, String> killerMap = new HashMap<Integer, String>();

	/**
	 * @param rTriggers
	 */
	MTListener(rTriggers rTriggers) {
		this.rTriggers = rTriggers;
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
				rTriggers.triggerMessagesWithOption("targetsplayer", eventToReplace, eventReplaceWith);
				rTriggers.triggerMessagesWithOption("targetsplayer|" + targeterName,eventToReplace,eventReplaceWith);
				rTriggers.triggerMessagesWithOption(targetPlayer, "targetsplayer", eventToReplace, eventReplaceWith);
				rTriggers.triggerMessagesWithOption(targetPlayer, "targetsplayer|" + targeterName,eventToReplace,eventReplaceWith);
			}
		}
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event ){
		String damageCause; 
		String triggerOption;
		Entity gotHurt = event.getEntity();
		if (gotHurt instanceof Player) return;
		
		String damaged = gotHurt.getClass().getName();
		damaged = damaged.substring(damaged.lastIndexOf("Craft") + 5);
		triggerOption = event.getCause().toString().toLowerCase();
		switch (event.getCause()) {
			case CONTACT:
				damageCause = "touching something";
				break;
			case ENTITY_ATTACK:
				damageCause = "being hit";
				break;
			case SUFFOCATION:
				damageCause = "suffocation";
				break;
			case FALL:
				damageCause = "falling";
				break;
			case FIRE:
				damageCause = "fire";
				break;
			case FIRE_TICK:
				damageCause = "burning";
				break;
			case LAVA:
				damageCause = "lava";
				break;
			case DROWNING:
				damageCause = "drowning";
				break;
			case BLOCK_EXPLOSION:
				damageCause = "explosion";
				break;
			case ENTITY_EXPLOSION:
				damageCause = "creeper";
				break;
			case CUSTOM:
				damageCause = "the unknown";
				break;
			default:
				triggerOption = "something";
				damageCause = "something";
				break;
		}
		String [] replaceThese = {"<<damage-cause>>"};
		String [] withThese = {damageCause};
		rTriggers.triggerMessagesWithOption("mobdamage|" + damaged + "|" + triggerOption,replaceThese,withThese);
		rTriggers.triggerMessagesWithOption("mobdamage|" + damaged                      ,replaceThese,withThese);
		
		if (event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)event).getDamager() instanceof Player) {
			killerMap.put(gotHurt.getEntityId(),
					((Player) ((EntityDamageByEntityEvent)event).getDamager()).getName());
		} else killerMap.remove(gotHurt.getEntityId());
	}
	
	
	@Override
	public void onEntityDeath(EntityDeathEvent event){
		Entity isDead = event.getEntity();
		if (isDead instanceof Player) return;
		int deadId = isDead.getEntityId();
		String deadMob = isDead.getClass().getName();
		deadMob = deadMob.substring(deadMob.lastIndexOf("Craft") + 5);
		if (killerMap.containsKey(deadId)){
			Player killer = rTriggers.getServer().getPlayer(killerMap.get(deadId));
			String murderWeapon = killer.getItemInHand().getType().toString().toLowerCase().replace("_", " ");
			if (murderWeapon.equals("air")) murderWeapon = "fist";
			String [] replaceThese = {"<<weapon>>", "<<mob>>"};
			String [] withThese    = {murderWeapon, deadMob};
			
			rTriggers.triggerMessagesWithOption(killer, "mobkilledbyplayer",replaceThese,withThese);
			rTriggers.triggerMessagesWithOption(killer, "mobkilledbyplayer|" + deadMob,replaceThese,withThese);
		}
	}
}
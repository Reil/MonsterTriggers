package com.reil.bukkit.MonsterTriggers;

import java.util.logging.Logger;

import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.reil.bukkit.rTriggers.rTriggers;

public class MTListener extends EntityListener {
	private final rTriggers rTriggers;
	Logger log = Logger.getLogger("Minecraft");

	/**
	 * @param rTriggers
	 */
	MTListener(rTriggers rTriggers) {
		this.rTriggers = rTriggers;
	}
	public void onEntityTarget (EntityTargetEvent event){
		Entity target = event.getTarget();
		TargetReason reason = event.getReason();
		if (reason == TargetReason.CLOSEST_PLAYER ||
				reason == TargetReason.TARGET_ATTACKED_ENTITY ||
				reason == TargetReason.PIG_ZOMBIE_TARGET){
			if (target instanceof Player){
				if (((Player) target).getHealth() == 0) return;
				Player targetPlayer =(Player) target;
				String targeter = event.getEntity().getClass().getName();
				targeter = targeter.substring(targeter.lastIndexOf("Craft") + "Craft".length());
				String[] eventToReplace =  {"<<targeter>>"};
				String []eventReplaceWith = {targeter};
				rTriggers.triggerMessagesWithOption("targetsplayer", eventToReplace, eventReplaceWith);
				rTriggers.triggerMessagesWithOption("targetsplayer|" + targeter,eventToReplace,eventReplaceWith);
				rTriggers.triggerMessagesWithOption(targetPlayer, "targetsplayer", eventToReplace, eventReplaceWith);
				rTriggers.triggerMessagesWithOption(targetPlayer, "targetsplayer|" + targeter,eventToReplace,eventReplaceWith);
			}
		}
	}
	public void onEntityDamage(EntityDamageEvent event ){
		String damageCause; 
		String triggerOption;
		if (event.getEntity() instanceof Player) return;
		String damaged = event.getEntity().getClass().getName();
		damaged = damaged.substring(damaged.lastIndexOf("Craft") + "Craft".length());
		switch (event.getCause()) {
			case CONTACT:
				triggerOption = "contact";
				damageCause = "touching something";
				break;
			case ENTITY_ATTACK:
				triggerOption = "entity_attack";
				damageCause = "being hit";
				break;
			case SUFFOCATION:
				triggerOption = "suffocation";
				damageCause = "suffocation";
				break;
			case FALL:
				triggerOption = "fall";
				damageCause = "falling";
				break;
			case FIRE:
				triggerOption = "fire";
				damageCause = "fire";
				break;
			case FIRE_TICK:
				triggerOption = "fire_tick";
				damageCause = "burning";
				break;
			case LAVA:
				triggerOption = "lava";
				damageCause = "lava";
				break;
			case DROWNING:
				triggerOption = "drowning";
				damageCause = "drowning";
				break;
			case BLOCK_EXPLOSION:
				triggerOption = "block_explosion";
				damageCause = "explosion";
				break;
			case ENTITY_EXPLOSION:
				triggerOption = "entity_explosion";
				damageCause = "creeper";
				break;
			case CUSTOM:
				triggerOption = "custom";
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
		rTriggers.triggerMessagesWithOption("mobdamage|" + damaged,replaceThese,withThese);
	}
}
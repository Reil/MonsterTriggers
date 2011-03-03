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
			log.info("Entity changing targets.");
			if (target instanceof Player){
				String targeter = event.getEntity().getClass().getName();
				targeter = targeter.substring(targeter.lastIndexOf("Craft") + "Craft".length());
				String[] eventToReplace =  new String[0];
				String []eventReplaceWith = new String[0];
				rTriggers.triggerMessagesWithOption("targetsplayer", eventToReplace, eventReplaceWith);
				rTriggers.triggerMessagesWithOption("targetsplayer|" + targeter,eventToReplace,eventReplaceWith);
			}
		}
	}
}

package com.reil.bukkit.MonsterTriggers;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.reil.bukkit.rTriggers.rTriggers;


public class MTPlugin extends JavaPlugin {
	Listener listener = new Listener();
	boolean registered = false;
	EntityListener entityListener;
	Logger log = Logger.getLogger("Minecraft");
	
	public void onEnable(){
		Server MCServer = getServer();
		PluginManager loader = MCServer.getPluginManager();
		loader.registerEvent(Event.Type.PLUGIN_ENABLE, listener, Priority.Monitor, this);
	}
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
	public void register() {
		if (registered = false) {
			Server MCServer = getServer();
			PluginManager loader = MCServer.getPluginManager();
			loader.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Event.Priority.Monitor, this);
		}
	}
	private class Listener extends ServerListener {

        public Listener() { }

        public void onPluginEnabled(PluginEvent event) {
            if(event.getPlugin().getDescription().getName().equals("rTriggers")) {
                log.info("[MonsterTriggers] Attached to rTriggers.");
    			entityListener = new MTListener((rTriggers)event.getPlugin());
    			/* register events */
    			register();
    			registered = true;
    			log.info("MonsterTriggers loaded!");
            }
        }
    }
}

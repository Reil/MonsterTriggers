package com.reil.bukkit.MonsterTriggers;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.reil.bukkit.rTriggers.rTriggers;


public class MTPlugin extends JavaPlugin {
	Listener listener = new enableListener();
	boolean registered = false;
	MTListener entityListener;
	Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public void onEnable(){
		Server MCServer = getServer();
		PluginManager loader = MCServer.getPluginManager();
		loader.registerEvents(listener, this);
		Plugin rTriggers = MCServer.getPluginManager().getPlugin("rTriggers");
        if(rTriggers != null ) {
            log.info("[MonsterTriggers] Attached to rTriggers.");
			entityListener = new MTListener((rTriggers) rTriggers);
			/* register events */
			register();
			log.info("MonsterTriggers loaded!");
        }
	}
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
	public void register() {
		if (registered == false) {
			Server MCServer = getServer();
			PluginManager loader = MCServer.getPluginManager();
			loader.registerEvents(entityListener, this);
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new MTCleaner(entityListener), 20, 6000);
			registered = true;
		}
	}

	private class enableListener implements Listener {

        public enableListener() { }
        
        @EventHandler
        public void onPluginEnable(PluginEnableEvent event) {
            if(event.getPlugin().getDescription().getName().equals("rTriggers")) {
                log.info("[MonsterTriggers] Attached to rTriggers.");
    			entityListener = new MTListener((rTriggers)event.getPlugin());
    			/* register events */
    			register();
    			log.info("MonsterTriggers loaded!");
            }
        }
    }
}

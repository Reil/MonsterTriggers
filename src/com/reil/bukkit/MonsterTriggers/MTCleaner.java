package com.reil.bukkit.MonsterTriggers;

public class MTCleaner implements Runnable {

	MTListener Listener;
	public MTCleaner(MTListener Listener){
		this.Listener = Listener;
	}
	@Override
	public void run() {
		Listener.targetMap.clear();
		Listener.killerMap.clear();
	}

}

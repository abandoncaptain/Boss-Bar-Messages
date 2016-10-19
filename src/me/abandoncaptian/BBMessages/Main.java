package me.abandoncaptian.BBMessages;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener{

	public static Plugin plugin;
	Logger myPluginLogger = Bukkit.getLogger();
	MyConfigManager manager;
	MyConfig Config;
	int index = 1;
	public static boolean works = true;
	BossBar bar;

	@Override
	public void onEnable()
	{
		plugin = this;
		myPluginLogger.info("--------------------------------");
		myPluginLogger.info("    Boss Bar Messages Enabled");
		myPluginLogger.info("--------------------------------");
		manager = new MyConfigManager(this);
		Config = manager.getNewConfig("Config.yml", new String[] {"Boss Bar Config Settings"});
		int Interval = (int) Config.getInt("Interval");
		Interval = Interval * 1200;
		Bukkit.getScheduler().runTaskTimer(this, sched(), 0, Interval);
		setConfigDefaults();
		Config.reloadConfig();
	}

	@Override
	public void onDisable()
	{
		myPluginLogger.info("--------------------------------");
		myPluginLogger.info("    Boss Bar Messages Disabled");
		myPluginLogger.info("--------------------------------");
	}

	@Override
	public boolean onCommand(CommandSender theSender, Command cmd, String commandLabel,String[] args)
	{
		if((commandLabel.equalsIgnoreCase("bossbar") || commandLabel.equalsIgnoreCase("bb")) && (theSender instanceof Player)){
			Player p = (Player) theSender;
			if(!p.hasPermission("bbmessages.all")){
				p.sendMessage("§cYou don't have permission to perform this command!");
				return true;
			}
			if(args.length > 0){
				//BossBar reload
				if(args[0].equalsIgnoreCase("Reload") && args.length == 1){
					bossBarReloadClear(bar);
					setConfigDefaults();
					Config.reloadConfig();
					p.sendMessage("§bConfig Reloaded");
				}
				if(args[0].equalsIgnoreCase("Reload") && args.length > 1){
					p.sendMessage("§cIncorrect Arguments!");
					p.sendMessage("§7/bossbar reload");
				}
				//BossBar Send
				if(args[0].equalsIgnoreCase("Send") && args.length >= 3){
					StringBuilder str = new StringBuilder();
					for(int i = 2; i < args.length; i++){
						str.append(args[i] + " ");
					}
					String mess = str.toString();
					mess = mess.replace("&", "§");
					int duration = Integer.parseInt(args[1]);
					sendBossBar(mess, duration);
					return true;
				}else if(args[0].equalsIgnoreCase("Send") && args.length == 2){
					p.sendMessage("§cIncorrect Arguments!");
					p.sendMessage("§7/bossbar Send <Duration(in seconds)> <Message to send>");
					return true;
				}  else if(args[0].equalsIgnoreCase("Send")){
					p.sendMessage("§cIncorrect Arguments!");
					p.sendMessage("§7/bossbar Send <Duration(in seconds)> <Message to send>");
					return true;
				}

				//BossBar Set
				if(args[0].equalsIgnoreCase("Set") && args.length >= 3){
					StringBuilder str = new StringBuilder();
					for(int i = 2; i < args.length; i++){
						str.append(args[i] + " ");
					}
					String mess = str.toString();
					String pathNum = (String) args[1].toString();
					p.sendMessage("Message Index " + pathNum + " has been set to " + mess);
					if(Config.contains("Message."+pathNum)){
						Config.set("Messages."+pathNum , mess);
						Config.saveConfig();
						Config.reloadConfig();
						return true;
					}else{
						Config.set("Messages."+pathNum, mess);
						Config.saveConfig();
						Config.reloadConfig();
						return true;
					}

				} else if(args[0].equalsIgnoreCase("Set") && args.length == 2){
					p.sendMessage("§cIncorrect Arguments!");
					p.sendMessage("§7/bossbar Set <Message #> <Message to send>");
					return true;
				} else if(args[0].equalsIgnoreCase("Set") && args.length == 1){
					p.sendMessage("§cIncorrect Arguments!");
					p.sendMessage("§7/bossbar Set <Message #> <Message to send>");
					return true;
				}
				return true;
			} else {
				//Default
				p.sendMessage("§cInvalid Arguments!");
				p.sendMessage("§7/bossbar [Send/Set/Reload]");
				return true;
			}

		}
		return  true;
	}
	public void setConfigDefaults(){
		if(!(Config.contains("Messages"))){
			Config.set("Messages.1", "&bTest Message #1");
			Config.set("Messages.2", "&bTest Message #2");
			Config.set("Messages.3", "&bEdit these at any time!");
			Config.set("Messages.4", "&bYou can have as many as you wish!");
		}
		if(!(Config.contains("Interval"))){
			Config.set("Interval", 1);
		}
		if(!(Config.contains("Bar"))){
			Config.set("Bar.Color", "GREEN");
			Config.set("Bar.Style", "SOLID");
		}
		Config.saveConfig();
		return;
	}

	public Runnable sched(){
		return new BukkitRunnable() {
			@Override
			public void run() {
				String mess;
				String sIndex = String.valueOf(index);
				if (!Config.contains("Messages." + sIndex)){
					index = 1;
				}
				mess = Config.getString("Messages." + sIndex);
				if(mess.contains("&"))mess = mess.replace("&", "§");
				sendBossBar(mess, 10);
				index = index + 1;
			}
		};
	}

	boolean active = false;
	
	public void sendBossBar(String mess, int duration) {
		if(active){
			return;
		}
		BarColor bC = BarColor.valueOf(Config.getString("Bar.Color"));
		BarStyle bS = BarStyle.valueOf(Config.getString("Bar.Style"));
		bar = Bukkit.createBossBar(mess, bC, bS, new BarFlag[0]);
		active = true;
		for(Player p: Bukkit.getOnlinePlayers()){
			bar.addPlayer(p);
		}
		duration = duration * 20;
		active = clearBossBar(bar, duration);
	}
	public Boolean clearBossBar(final BossBar bar, int duration) {
		new BukkitRunnable() {
			@Override
			public void run() {
				bar.removeAll();
				bar.setVisible(false);
				active = false;
			}
		}.runTaskTimer(plugin, duration, 0);
		return active;
	}
	
	public void bossBarReloadClear(final BossBar bar) {
		bar.removeAll();
		bar.setVisible(false);
	}
}
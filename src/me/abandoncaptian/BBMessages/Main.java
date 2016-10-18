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
	public static boolean works = true;

	@Override
	public void onEnable()
	{
		plugin = this;
		myPluginLogger.info("--------------------------------");
		myPluginLogger.info("    Boss Bar Messages Enabled");
		myPluginLogger.info("--------------------------------");
		manager = new MyConfigManager(this);
		Config = manager.getNewConfig("Config.yml", new String[] {"Boss Bar Config Settings"});
		Bukkit.getScheduler().runTaskTimer(this, sched(), (20*10), 1200);
		if(!Config.contains("Messages")){
			Config.set("Messages.1", "&bTest Message #1");
			Config.set("Messages.2", "&bTest Message #2");
			Config.set("Messages.3", "&bEdit these at any time!");
			Config.set("Messages.4", "&bYou can have as many as you wish!");
		}
		if(!Config.contains("Interval")){
			Config.set("Interval", 1);
		}
		Config.saveConfig();
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
		if(commandLabel.equalsIgnoreCase("bb") && (theSender instanceof Player)){
			Player p = (Player) theSender;
			if(!p.hasPermission("bbmessages.all")){
				p.sendMessage("�cYou don't have permission to perform this command!");
				return true;
			}
			if(args.length > 0){
				if(args[0].equalsIgnoreCase("Reload") && args.length == 1){
					if(!Config.contains("Messages")){
						Config.set("Messages.1", "&bTest Message #1");
						Config.set("Messages.2", "&bTest Message #2");
						Config.set("Messages.3", "&bEdit these at any time!");
						Config.set("Messages.4", "&bYou can have as many as you wish!");
					}
					if(!Config.contains("Interval")){
						Config.set("Interval", 1);
					}
					Config.saveConfig();
					Config.reloadConfig();
					return true;
				}
				if(args[0].equalsIgnoreCase("Send") && args.length >= 2){
					StringBuilder str = new StringBuilder();
					for(int i = 1; i < args.length; i++){
						str.append(args[i] + " ");
					}
					String mess = str.toString();
					mess = mess.replace("&", "�");
					sendBossBar(mess);
					return true;
				} else if(args[0].equalsIgnoreCase("Send")){
					p.sendMessage("�cIncorrect Arguments!");
					p.sendMessage("�7/bb Send <Message to send>");
					return true;
				}
				if(args[0].equalsIgnoreCase("Set") && args.length >= 3){
					StringBuilder str = new StringBuilder();
					for(int i = 3; i < args.length; i++){
						str.append(args[i] + " ");
					}
					String mess = str.toString();
					if(Config.contains("Message."+args[1].toString())){
						Config.set("Messages."+args[1].toString() , mess);
						Config.saveConfig();
						Config.reloadConfig();
						return true;
					}else{
						Config.set("Messages."+args[1], mess);
						Config.saveConfig();
						Config.reloadConfig();
						return true;
					}

				} else if(args[0].equalsIgnoreCase("Set") && args.length == 2){
					p.sendMessage("�cIncorrect Arguments!");
					p.sendMessage("�7/bb Set <Message #> <Message to send>");
					return true;
				} else if(args[0].equalsIgnoreCase("Set") && args.length == 1){
					p.sendMessage("�cIncorrect Arguments!");
					p.sendMessage("�7/bb Set <Message #> <Message to send>");
					return true;
				}
				return true;
			} else {
				p.sendMessage("�cInvalid Arguments!");
				p.sendMessage("�7/bb [Send/Set/Reload]");
				return true;
			}

		}
		return  true;
	}
	public Runnable sched(){
		return new BukkitRunnable() {

			@Override
			public void run() {
				String mess;
				int index = 1;
				String sIndex = String.valueOf(index);
				if (!Config.contains("Messages." + sIndex)){
					index = 1;
				}
				mess = Config.getString("Messages." + sIndex);
				sendBossBar(mess);
			}
		};
	}
	
	public void sendBossBar(String mess) {
		BossBar bar = Bukkit.createBossBar(mess, BarColor.GREEN, BarStyle.SOLID, new BarFlag[0]);
		for(Player p: Bukkit.getOnlinePlayers()){
			bar.addPlayer(p);
		}
		clearBossBar(bar);
	}
	public void clearBossBar(BossBar bar) {
		new BukkitRunnable() {
			@Override
			public void run() {
				bar.removeAll();
				bar.setVisible(false);
			}
		}.runTaskTimer(plugin, (20*10), 0);
	}
}
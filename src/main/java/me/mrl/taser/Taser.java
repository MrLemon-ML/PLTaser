package me.mrl.taser;

import me.mrl.taser.command.CTaser;
import me.mrl.taser.events.Events;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public final class Taser extends JavaPlugin implements Listener {

    public static Taser plugin;

    @Override
    public void onEnable() {
        plugin = this;
        this.getServer().getPluginManager().registerEvents(new Events(), this);
        getLogger().info(ChatColor.GREEN + "TaserPlugin abilitato.");
        this.getCommand("taser").setExecutor(new CTaser());


    }

    @Override
    public void onDisable() {
        getLogger().info(org.bukkit.ChatColor.RED + "TaserPlugin disabilitato!");
        plugin = null;

    }



}

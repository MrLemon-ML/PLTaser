package me.mrl.taser;

import me.mrl.taser.command.CTaser;
import me.mrl.taser.events.Events;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public final class Taser extends JavaPlugin implements Listener {

    public static Taser plugin;

    // Variabili config
    public int taserCooldown;

    // Variabili messages
    private File messagesFile;
    private static FileConfiguration messages;

    @Override
    public void onEnable() {

        plugin = this;

        // 1️⃣ Crea cartella plugin se non esiste
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // 2️⃣ Crea config.yml SOLO se non esiste
        saveDefaultConfig();

        // 3️⃣ Carica valori dal config
        loadConfigValues();

        // 4️⃣ Carica messages.yml
        loadMessages();






        this.getServer().getPluginManager().registerEvents(new Events(), this);

        this.getCommand("taser").setExecutor(new CTaser());

        getLogger().info(ChatColor.GREEN + "TaserPlugin abilitato.");



    }

    @Override
    public void onDisable() {
        getLogger().info(org.bukkit.ChatColor.RED + "TaserPlugin disabilitato!");
        plugin = null;

    }




    // Metodo che carica valori dal config
    public void loadConfigValues() {

        reloadConfig(); // Rilegge il file dal disco

        try {

            taserCooldown = getConfig().getInt("taser.cooldown");

            if (taserCooldown < 0) {
                getLogger().warning("Cooldown non valido! Uso default 5.");
                taserCooldown = 5;
            }

        } catch (Exception e) {
            getLogger().severe("Errore nel config.yml!");
        }
    }

    // Metodo per caricare messages.yml
    public void loadMessages() {

        messagesFile = new File(getDataFolder(), "messages.yml");

        // Se non esiste lo copia dalle resources
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    // Metodo per ottenere messaggi colorati
    public static String getMessage(String path) {
        String msg = messages.getString(path);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    // Comando /taser reload
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission("taser.reload")) {
                sender.sendMessage(getMessage("no-permission"));
                return true;
            }

            loadConfigValues();
            loadMessages();

            sender.sendMessage(getMessage("reload-success"));
            return true;
        }

        return false;
    }


    public int getTaserCooldown() {
        return taserCooldown;
    }



}

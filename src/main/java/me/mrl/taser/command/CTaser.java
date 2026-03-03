package me.mrl.taser.command;

import me.mrl.taser.item.TaserItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class CTaser implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command cmd,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player)) return true;
        Player player;
        player = (Player) sender;
        int quantita;
        if (!player.hasPermission("taser.get")) {
            player.sendMessage("§cErrore: non hai i permessi per eseguire questo comando!");

            return true;
        }

        if (!cmd.getName().equalsIgnoreCase("taser") && args.length > 0) return true;

        switch (args[0].toLowerCase()) {
            case "taser":

                player.getInventory().addItem(TaserItem.Taser(3));
                player.sendMessage("§7Hai ricevuto un taser.");
                return true;


            case "ricaricataser":
                quantita = 1;

                // Controlliamo se l'utente ha scritto qualcosa dopo il comando
                if (args.length > 1) {
                    try {
                        // Trasformiamo la stringa args[1] in un numero intero
                        quantita = Integer.parseInt(args[1]);
                    } catch (Exception e) {
                        // Se l'utente scrive /get-ricarica-taser "ciao", entra qui
                        player.sendMessage("§cErrore: devi inserire un numero valido!");
                        return true;
                    }
                }

                player.getInventory().addItem(TaserItem.RicaricaTaser(quantita));
                player.sendMessage("§7Hai ricevuto " + quantita + " ricarica/he taser.");
                return true;
            case "ostia":
                quantita = 1;

                // Controlliamo se l'utente ha scritto qualcosa dopo il comando
                if (args.length > 1) {
                    try {
                        // Trasformiamo la stringa args[1] in un numero intero
                        quantita = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        // Se l'utente scrive /get-ricarica-taser "ciao", entra qui
                        player.sendMessage("§cErrore: devi inserire un numero valido!");
                        return true;
                    }
                }

                player.getInventory().addItem(TaserItem.Ostia(quantita));
                player.sendMessage("§7Hai ricevuto " + quantita + " ostia/e.");
                return true;

        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      @NotNull String[] args) {

        if (!command.getName().equalsIgnoreCase("taser") && args.length > 0) return null;
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("taser");
            completions.add("ricaricataser");
            completions.add("ostia");
        }

        if (args.length == 2 &&
                (args[0].equalsIgnoreCase("ricaricataser") ||
                        args[0].equalsIgnoreCase("ostia"))) {

            completions.add("1");
            completions.add("16");
            completions.add("32");
            completions.add("64");
        }

        return completions;
    }
}

package me.mrl.taser.command;

import me.mrl.taser.item.TaserItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import org.jetbrains.annotations.NotNull;


public class CTaser implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player;
        player = (Player) sender;
        int quantita;
        if (!player.hasPermission("taser.get")) {
            player.sendMessage("§cErrore: non hai i permessi per eseguire questo comando!");

            return true;
        }
        switch (cmd.getName().toLowerCase()) {
            case "get-taser":

                player.getInventory().addItem(TaserItem.Taser(3));
                player.sendMessage("§7Hai ricevuto un taser.");
                return true;
            case "get-ricarica-taser":
                quantita = 1;

                // Controlliamo se l'utente ha scritto qualcosa dopo il comando
                if (args.length > 0) {
                    try {
                        // Trasformiamo la stringa args[0] in un numero intero
                        quantita = Integer.parseInt(args[0]);
                    } catch (Exception e) {
                        // Se l'utente scrive /get-ricarica-taser "ciao", entra qui
                        player.sendMessage("§cErrore: devi inserire un numero valido!");
                        return true;
                    }
                }

                player.getInventory().addItem(TaserItem.RicaricaTaser(quantita));
                player.sendMessage("§7Hai ricevuto " + quantita + " ricarica/he taser.");
                return true;
            case "get-ostia":
                quantita = 1;

                // Controlliamo se l'utente ha scritto qualcosa dopo il comando
                if (args.length > 0) {
                    try {
                        // Trasformiamo la stringa args[0] in un numero intero
                        quantita = Integer.parseInt(args[0]);
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
}

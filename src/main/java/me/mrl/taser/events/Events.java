package me.mrl.taser.events;


import me.mrl.taser.Taser;
import me.mrl.taser.item.TaserItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Events implements Listener {

    public BidiMap<UUID, UUID> taserMap = new DualHashBidiMap<>();
    private final Map<UUID, Long> clickCooldown = new HashMap<>();


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        clickCooldown.remove(event.getPlayer().getUniqueId());

        taserMap.remove(uuid);
        taserMap.removeValue(uuid);


    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        clickCooldown.remove(event.getPlayer().getUniqueId());

        taserMap.remove(uuid);
        taserMap.removeValue(uuid);


    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (player.getInventory().getItemInMainHand().equals(TaserItem.Ostia())){
            if (!canInsertLink(taserMap, uuid)) {
                player.sendMessage("§eSei stato §f§lBenedetto");
                taserMap.remove(uuid);
                taserMap.removeValue(uuid);
            }
            else{
                player.sendMessage("§cNon puoi mangiare l'ostia se non sei Taserato");
            }


        }
    }


    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent event) {
        // Controlliamo che l'entità cliccata sia un Player
        if (!(event.getRightClicked() instanceof Player)) return;

        Player player = event.getPlayer(); // Il taseratore
        Player target = (Player) event.getRightClicked(); // La vittima

        UUID uuid = player.getUniqueId();
        UUID uuid2 = target.getUniqueId();

        ItemStack item = player.getInventory().getItemInMainHand();

        int livello = estraiLivello(item);

        if (livello == -1) return;

        long currentTime = System.currentTimeMillis();

        if (clickCooldown.containsKey(uuid)) {
            long lastClick = clickCooldown.get(uuid);
            if (currentTime - lastClick < 200) {
                // Troppo veloce! Esci senza fare nulla
                //event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacy("§e⚡ §cClicchi troppo velocemente§e⚡"));
                return;
            }
        }
        clickCooldown.put(uuid, currentTime);


        // Confronto dell'item (usa .isSimilar invece di .equals per gli ItemStack)
        /*
        if (item.isSimilar(TaserItem.Taser(0))) {

            player.sendMessage("§cHai finito il Taser");

        }
         */
        if (livello <= 0) {
            player.sendMessage("§cHai il Taser scarico");
            return;
        }

        if (item.getAmount() > 1 && !canFitItem(player, TaserItem.Taser(livello - 1))) {
            player.sendMessage("§cInventario pieno! Non puoi taserare");
            return;
        }


        if (!canInsertLink(taserMap, uuid, uuid2)) {
            player.sendMessage("§cNon puoi Taserare: uno di voi è già occupato!");
        } else {
            // Se il controllo canInsertLink passa, ricordati di AGGIUNGERE il legame!
            if (isBlocking(target)) {
                // Controlla se il player è dietro il target
                Vector targetToPlayer = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize();
                Vector targetFacing = target.getLocation().getDirection().normalize();

                double dot = targetToPlayer.dot(targetFacing); // da -1 (dietro) a 1 (davanti)

                if (dot > -0.3) { // Se non è abbastanza dietro (~110°)
                    //player.sendMessage("§cIl bersaglio sta parando con lo scudo!");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cIl bersaglio sta parando con lo scudo!"));
                    return;
                }
            }

            taserMap.put(uuid, uuid2);

            player.sendMessage("§a Hai taserato " + target.getName() + "!");
            target.sendMessage("§c Sei stato taserato da " + player.getName() + "!");

            if (target.getInventory().getItemInMainHand().getType() == Material.SHIELD ||
                    target.getInventory().getItemInOffHand().getType() == Material.SHIELD) {

                // Imposta il cooldown del Materiale Scudo per il target
                // 200 tick = 10 secondi
                target.setCooldown(Material.SHIELD, 200);
            }

            // aggiungendo 10 secondi (10000 ms)
            long newExpiry = currentTime + 10000;
            clickCooldown.put(uuid, newExpiry);


            new BukkitRunnable() {
                @Override
                public void run() {
                    // Rimuove il legame dalla mappa dopo 5 secondi
                    taserMap.remove(uuid);
                    if (target.isOnline()) {
                        target.sendMessage("§a L'effetto del taser è svanito!");
                    }
                }
            }.runTaskLater(Taser.plugin, 100L); // 20 tick = 1 secondo

            if (target.isBlocking()) {
                // Interrompi l'uso dello scudo scambiando temporaneamente l'oggetto
                final ItemStack shield;
                final boolean shieldInMainHand = target.getInventory().getItemInMainHand().getType() == Material.SHIELD;

                if (shieldInMainHand) {
                    shield = target.getInventory().getItemInMainHand();
                    target.getInventory().setItemInMainHand(new ItemStack(Material.STRUCTURE_VOID));
                } else {
                    shield = target.getInventory().getItemInOffHand();
                    target.getInventory().setItemInOffHand(null);
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Assicurati che il giocatore sia ancora online e che lo slot sia ancora vuoto
                        if (target.isOnline()) {
                            if (shieldInMainHand) {
                                if (target.getInventory().getItemInMainHand() == null || target.getInventory().getItemInMainHand().getType() == Material.AIR || target.getInventory().getItemInMainHand().getType() == Material.STRUCTURE_VOID) {
                                    target.getInventory().setItemInMainHand(shield);
                                }
                            } else {
                                if (target.getInventory().getItemInOffHand() == null || target.getInventory().getItemInOffHand().getType() == Material.AIR) {
                                    target.getInventory().setItemInOffHand(shield);
                                }
                            }
                        }
                    }
                }.runTaskLater(Taser.plugin, 3L);
                //target.stopActiveHand(); 1.17.+
                //target.damage(0.01, player);
                target.setCooldown(Material.SHIELD, 80); // 4 secondi di cooldown
                target.playSound(target.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
                player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
            }

            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
                player.getInventory().addItem(TaserItem.Taser(livello - 1));
            } else {
                player.getInventory().setItemInMainHand(TaserItem.Taser(livello - 1));
            }
        }

    }


    private boolean canFitItem(Player player, ItemStack itemToCheck) {
        // Creiamo una copia dell'inventario per non sporcare quello reale
        for (ItemStack invItem : player.getInventory().getStorageContents()) {
            // Se c'è uno slot vuoto, ci sta sicuramente
            if (invItem == null || invItem.getType().isAir()) return true;

            // Se l'item è simile e c'è spazio nello stack
            if (invItem.isSimilar(itemToCheck)) {
                if (invItem.getAmount() < invItem.getMaxStackSize()) return true;
            }
        }
        return false;
    }













    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        ItemStack item = player.getInventory().getItemInMainHand();
        int livello = estraiLivello(item);

        if (livello == -1) return;

        long currentTime = System.currentTimeMillis();

        if (clickCooldown.containsKey(uuid)) {
            long lastClick = clickCooldown.get(uuid);
            if (currentTime - lastClick < 200) {
                // Troppo veloce! Esci senza fare nulla
                //event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacy("§e⚡ §cClicchi troppo velocemente§e⚡"));
                return;
            }
        }
        clickCooldown.put(uuid, currentTime);

        if (livello >= 3) {
            player.sendMessage("§cHai il Taser full");
            return;
        }

        // Confronto dell'item (usa .isSimilar invece di .equals per gli ItemStack)
        /*if (item.isSimilar(TaserItem.Taser(0))) {
            player.sendMessage("§cHai il Taser full");
            return;
        }
         */

        if (item.getAmount() > 1 && !canFitItem(player, TaserItem.Taser(livello + 1))) {
            player.sendMessage("§cInventario pieno! Non puoi ricaricare");
            return;
        }

        player.sendMessage("§aHai ricaricato il Taser");

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
            player.getInventory().addItem(TaserItem.Taser(livello + 1));
        } else {
            player.getInventory().setItemInMainHand(TaserItem.Taser(livello + 1));
        }
    }











    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        // Se il giocatore è presente come VALORE (vittima) nella mappa

        if (taserMap.containsValue(uuid)) {
            // Se si è spostato (non solo girato la visuale)
            if (       event.getFrom().getX() != event.getTo().getX()
                    || event.getFrom().getZ() != event.getTo().getZ()
                    || event.getFrom().getY() != event.getTo().getY()
                    || event.getFrom().getPitch() != event.getTo().getPitch()
                    || event.getFrom().getYaw() != event.getTo().getYaw()) {

                // Annulla il movimento
                //event.getPlayer().getInventory().setHeldItemSlot(4);
                event.setCancelled(true);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacy("§e⚡ §cSEI TASERATO - NON PUOI MUOVERTI §e⚡"));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();


        if (taserMap.containsValue(uuid)) {
            // Annulla l'interazione (sia click sinistro che destro)
            event.setCancelled(true);


            //event.getPlayer().sendMessage("§cNon puoi interagire mentre sei taserato!");
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacy("§e⚡ §cSEI TASERATO - NON PUOI MUOVERTI §e⚡"));
        }
    }

    public boolean canInsertLink(BidiMap<UUID, UUID> bidi, UUID k) {
        return !bidi.containsKey(k) && !bidi.containsValue(k);
    }

    public boolean canInsertLink(BidiMap<UUID, UUID> bidi, UUID k, UUID v) {
        return canInsertLink(bidi, k) && canInsertLink(bidi, v);
    }


    private boolean isBlocking(Player player) {
        if (player.isHandRaised()) {
            ItemStack main = player.getInventory().getItemInMainHand();
            ItemStack off = player.getInventory().getItemInOffHand();
            return (main.getType() == Material.SHIELD || off.getType() == Material.SHIELD);
        }
        return false;
    }

    private int estraiLivello(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return -1;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return -1;

        String nome = meta.getDisplayName();

        // Controllo sicurezza
        if (!ChatColor.stripColor(nome).contains("Taser")) return -1;

        // Rimuoviamo i colori
        String nomePulito = ChatColor.stripColor(nome);

        // Cerchiamo numero dentro []
        Pattern pattern = Pattern.compile("\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(nomePulito);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return -1;
    }
}

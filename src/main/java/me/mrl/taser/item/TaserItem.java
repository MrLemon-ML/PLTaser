package me.mrl.taser.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class TaserItem {
    public static ItemStack Taser(int livello) {
        ItemStack taser = new ItemStack(Material.STICK);
        ItemMeta meta = taser.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§6Taser §8[§e" + livello + "§8]");
        meta.setLore(Collections.singletonList("§7Usalo per taserare le persone§8."));
        meta.setCustomModelData(163);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        taser.setItemMeta(meta);
        return taser;
    }

    public static ItemStack RicaricaTaser(int quantita) {
        ItemStack taser = new ItemStack(Material.STICK);
        taser.setAmount(quantita);
        ItemMeta meta = taser.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§eRicarica Taser");
        meta.setLore(Collections.singletonList("§7Usala per ricaricare il Taser§8."));
        meta.setCustomModelData(163);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        taser.setItemMeta(meta);
        return taser;
    }

    public static ItemStack Ostia(int quantita) {
        ItemStack taser = new ItemStack(Material.APPLE);
        taser.setAmount(quantita);
        ItemMeta meta = taser.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§eOstia");
        meta.setLore(Collections.singletonList("§7Usala per togliere l'effetto del Taser§8."));
        meta.setCustomModelData(163);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        taser.setItemMeta(meta);
        return taser;
    }

    public static ItemStack Ostia() {
        ItemStack taser = new ItemStack(Material.APPLE);
        ItemMeta meta = taser.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§eOstia");
        meta.setLore(Collections.singletonList("§7Usala per togliere l'effetto del Taser§8."));
        meta.setCustomModelData(163);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        taser.setItemMeta(meta);
        return taser;
    }
}

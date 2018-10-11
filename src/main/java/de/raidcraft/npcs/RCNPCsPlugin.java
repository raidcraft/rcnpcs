package de.raidcraft.npcs;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.Comment;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.util.CustomItemUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * Plugin for testing various stuff and creating proof of concepts.
 */
@Getter
public class RCNPCsPlugin extends BasePlugin {

    private LocalConfiguration config;

    @Override
    public void enable() {
        this.config = configure(new LocalConfiguration(this));
    }

    @Override
    public void disable() {
    }

    public class LocalConfiguration extends ConfigurationBase<RCNPCsPlugin> {



        public LocalConfiguration(RCNPCsPlugin plugin) {
            super(plugin, "config.yml");
        }
    }

}

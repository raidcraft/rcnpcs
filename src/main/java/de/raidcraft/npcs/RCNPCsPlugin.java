package de.raidcraft.npcs;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.quests.RCQuestConfigsLoaded;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Plugin for testing various stuff and creating proof of concepts.
 */
@Getter
public class RCNPCsPlugin extends BasePlugin implements Listener {

    private CustomNPCDataStore store;
    private LocalConfiguration config;

    @Override
    public void enable() {
        this.config = configure(new LocalConfiguration(this));
        registerEvents(this);

        store = new CustomNPCDataStore(this);
        CitizensPlugin plugin = (CitizensPlugin) CitizensAPI.getPlugin();
        if (plugin != null) {
            plugin.setDefaultNPCDataStore(store);
            getLogger().info("Enabled custom default NPC data store!");
        }
    }

    @Override
    public void disable() {
    }

    @EventHandler
    public void onQuestConfigsLoaded(RCQuestConfigsLoaded event) {

        getStore().loadInto(CitizensAPI.getNPCRegistry());
    }

    public class LocalConfiguration extends ConfigurationBase<RCNPCsPlugin> {

        public LocalConfiguration(RCNPCsPlugin plugin) {
            super(plugin, "config.yml");
        }
    }

}

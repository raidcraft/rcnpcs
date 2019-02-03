package de.raidcraft.npcs;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.Comment;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.quests.RCQuestConfigsLoaded;
import de.raidcraft.npcs.traits.DisguiseTrait;
import de.raidcraft.npcs.traits.ToFNPCTrait;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

/**
 * Plugin for testing various stuff and creating proof of concepts.
 */
@Getter
public class RCNPCsPlugin extends BasePlugin implements Listener {

    private CustomNPCDataStore store;
    private LocalConfiguration config;
    private File newNpcsPath ;

    @Override
    public void enable() {
        this.config = configure(new LocalConfiguration(this));
        registerEvents(this);

        newNpcsPath = new File(getDataFolder(), getConfig().npcPath);
        newNpcsPath.mkdirs();

        store = new CustomNPCDataStore(this);
        CitizensPlugin plugin = (CitizensPlugin) CitizensAPI.getPlugin();
        if (plugin != null) {
            plugin.setDefaultNPCDataStore(store);
            getLogger().info("Enabled custom default NPC data store!");
        }

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(DisguiseTrait.class));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ToFNPCTrait.class));
    }

    @Override
    public void disable() {
        getStore().saveToDiskImmediate();
    }

    @EventHandler
    public void onQuestConfigsLoaded(RCQuestConfigsLoaded event) {

        getStore().loadInto(CitizensAPI.getNPCRegistry());
    }

    public class LocalConfiguration extends ConfigurationBase<RCNPCsPlugin> {

        @Setting("npcs-path")
        @Comment("Path to a directory where newly created NPCs are stored after calling /citizens save.")
        public String npcPath = "npcs";

        public LocalConfiguration(RCNPCsPlugin plugin) {
            super(plugin, "config.yml");
        }
    }

}

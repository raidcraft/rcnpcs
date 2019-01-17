package de.raidcraft.npcs;

import de.raidcraft.api.config.ConfigLoader;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.ConfigUtil;
import lombok.Data;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class CustomNPCDataStore implements NPCDataStore {

    private final RCNPCsPlugin plugin;
    private final Map<String, ConfigurationSection> loadedNPCConfigs = new CaseInsensitiveMap<>();
    private final Map<Integer, String> idToPathMapping = new HashMap<>();
    private int lastCreatedNPCId = -1;

    public CustomNPCDataStore(RCNPCsPlugin plugin) {
        this.plugin = plugin;
        Quests.registerQuestLoader(new ConfigLoader(plugin, "npc", 50) {
            @Override
            public void loadConfig(String id, ConfigurationSection config) {
                loadNpcConfig(id, config);
            }

            @Override
            public void unloadConfig(String id) {
                unloadNpcConfig(id);
            }
        });
    }

    private void loadNpcConfig(String id, ConfigurationSection config) {
        if (loadedNPCConfigs.containsKey(id)) {
            getPlugin().getLogger().warning("NPC with id " + id + " already exists!");
            return;
        }
        this.loadedNPCConfigs.put(id, config);
    }

    private void unloadNpcConfig(String id) {
        ConfigurationSection config = this.loadedNPCConfigs.remove(id);
    }

    @Override
    public void clearData(NPC npc) {

    }

    @Override
    public int createUniqueNPCId(NPCRegistry registry) {
        int newId = getLastCreatedNPCId();
        if (newId == -1 || registry.getById(newId + 1) != null) {
            int maxId = Integer.MIN_VALUE;
            for (NPC npc : registry) {
                if (npc.getId() > maxId) {
                    maxId = npc.getId();
                }
            }
            newId = maxId == Integer.MIN_VALUE ? 0 : maxId + 1;
        } else {
            newId++;
        }
        setLastCreatedNPCId(newId);
        return newId;
    }

    @Override
    public void loadInto(NPCRegistry registry) {
        for (ConfigurationSection config : getLoadedNPCConfigs().values()) {
            if (!config.isSet("name")) {
                getPlugin().getLogger().warning("NPC " + ConfigUtil.getFileName(config) + " has no name defined!");
                continue;
            }
            EntityType entityType = EntityType.valueOf(config.getString("traits.type", "PLAYER"));
            if (entityType == null) {
                getPlugin().getLogger().warning("NPC " + ConfigUtil.getFileName(config) + " has invalid TYPE: " + config.getString("traits.type", "PLAYER"));
                continue;
            }

            NPC npc = registry.createNPC(entityType, UUID.randomUUID(), ++lastCreatedNPCId, config.getString("name"));
            
        }
    }

    @Override
    public void saveToDisk() {

    }

    @Override
    public void saveToDiskImmediate() {

    }

    @Override
    public void store(NPC npc) {

    }

    @Override
    public void storeAll(NPCRegistry npcRegistry) {

    }

    @Override
    public void reloadFromSource() {

    }
}

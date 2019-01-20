package de.raidcraft.npcs;

import de.raidcraft.api.config.ConfigLoader;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Data;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.util.DataKey;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class CustomNPCDataStore implements NPCDataStore {

    private final RCNPCsPlugin plugin;
    private final Map<String, ConfigurationBase> loadedNPCConfigs = new CaseInsensitiveMap<>();
    private final Map<Integer, String> idToPathMapping = new HashMap<>();
    private int lastCreatedNPCId = -1;
    private boolean loaded = false;

    public CustomNPCDataStore(RCNPCsPlugin plugin) {
        this.plugin = plugin;
        Quests.registerQuestLoader(new ConfigLoader<RCNPCsPlugin>(plugin, "npc", 50) {
            @Override
            public void loadConfig(String id, ConfigurationBase<RCNPCsPlugin> config) {
                loadNpcConfig(id, config);
            }

            @Override
            public void unloadConfig(String id) {
                unloadNpcConfig(id);
            }
        });
    }

    private void loadNpcConfig(String id, ConfigurationBase config) {
        if (loadedNPCConfigs.containsKey(id)) {
            getPlugin().getLogger().warning("NPC with id " + id + " already exists!");
            return;
        }
        config.load();
        if (!config.getBoolean("enabled", true)) {
            getPlugin().getLogger().info("NOT loading " + id + ": DISABLED");
            return;
        }
        this.loadedNPCConfigs.put(id, config);
        if (isLoaded()) {
            loadNpcFromConfig(CitizensAPI.getNPCRegistry(), id, new ConfigDataKey(config));
        }
    }

    private void loadNpcFromConfig(NPCRegistry registry, String id, DataKey dataKey) {
        EntityType entityType = matchEntityType(dataKey.getString("traits.type", "PLAYER"));
        NPC npc = registry.createNPC(entityType, UUID.randomUUID(), ++lastCreatedNPCId, dataKey.getString("name"));
        idToPathMapping.put(npc.getId(), id);
        npc.load(dataKey);
    }

    private void unloadNpcConfig(String id) {
        ConfigurationBase config = this.loadedNPCConfigs.remove(id);
        if (config != null) {
            config.set("enabled", false);
            config.save();
        }
    }

    @Override
    public void clearData(NPC npc) {
        Validate.notNull(npc);

        String id = idToPathMapping.get(npc.getId());
        if (id == null) {
            getPlugin().getLogger().warning("Cannot clear unknown NPC " + npc.getName() + " with ID " + npc.getId());
            return;
        }
        unloadNpcConfig(id);
    }

    @Override
    public int createUniqueNPCId(NPCRegistry registry) {
        return ++lastCreatedNPCId;
    }

    @Override
    public void loadInto(NPCRegistry registry) {
        for (String id : getLoadedNPCConfigs().keySet()) {
            try {
                ConfigurationBase config = getLoadedNPCConfigs().get(id);
                ConfigDataKey dataKey = new ConfigDataKey(config);
                if (!dataKey.keyExists("name")) {
                    getPlugin().getLogger().warning("NPC " + config.getFile().getAbsolutePath() + " has no name defined!");
                    continue;
                }
                loadNpcFromConfig(registry, id, dataKey);
            } catch (IllegalArgumentException e) {
                getPlugin().getLogger().warning(e.getMessage());
            }
        }
        loaded = true;
    }

    @Override
    public void saveToDisk() {
        final ConfigurationBase[] configs = getLoadedNPCConfigs().values().toArray(new ConfigurationBase[0]);
        new Thread(() -> {
            for (ConfigurationBase config : configs) {
                config.save();
            }
        }).start();
    }

    @Override
    public void saveToDiskImmediate() {
        getLoadedNPCConfigs().values().forEach(ConfigurationBase::save);
    }

    @Override
    public void store(NPC npc) {
        String id = npc.getId() + "-" + npc.getFullName();
        File file = new File(getPlugin().getNewNpcsPath(), id + ".npc.yml");
        SimpleConfiguration<RCNPCsPlugin> npcConfig = getPlugin()
                .configure(new SimpleConfiguration<>(getPlugin(), file));

        loadedNPCConfigs.put(id, npcConfig);
        idToPathMapping.put(npc.getId(), id);
        npc.save(new ConfigDataKey(npcConfig));
        getPlugin().getLogger().info("Created new NPC save file: " + npcConfig.getName());
    }

    @Override
    public void storeAll(NPCRegistry registry) {
        for (NPC npc : registry) {
            store(npc);
        }
        getPlugin().getLogger().info("Stored all NPCs into custom save files.");
    }

    @Override
    public void reloadFromSource() {
        getLoadedNPCConfigs().values().forEach(ConfigurationBase::load);
        getPlugin().getLogger().info("Reloaded Custom NPC Stores from disk.");
    }

    @SuppressWarnings("deprecation")
    private static EntityType matchEntityType(String toMatch) {
        EntityType type;
        try {
            type = EntityType.valueOf(toMatch);
        } catch (IllegalArgumentException ex) {
            type = EntityType.fromName(toMatch);
        }
        if (type != null)
            return type;
        return matchEnum(EntityType.values(), toMatch);
    }

    private static <T extends Enum<?>> T matchEnum(T[] values, String toMatch) {
        T type = null;
        for (T check : values) {
            String name = check.name();
            if (name.matches(toMatch) || name.equalsIgnoreCase(toMatch)
                    || name.replace("_", "").equalsIgnoreCase(toMatch)
                    || name.replace('_', '-').equalsIgnoreCase(toMatch)
                    || name.replace('_', ' ').equalsIgnoreCase(toMatch) || name.startsWith(toMatch)) {
                type = check;
                break;
            }
        }
        return type;
    }
}

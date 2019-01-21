package de.raidcraft.npcs;

import de.raidcraft.api.config.ConfigLoader;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.npcs.traits.ToFNPCTrait;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.ConfigUtil;
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
    private final Map<String, ConfigStorage> loadedNPCConfigs = new CaseInsensitiveMap<>();
    private final Map<Integer, String> idToPathMapping = new HashMap<>();
    private int lastCreatedNPCId = 0;
    private boolean loaded = false;

    public CustomNPCDataStore(RCNPCsPlugin plugin) {
        this.plugin = plugin;
        ConfigLoader<RCNPCsPlugin> configLoader = new ConfigLoader<RCNPCsPlugin>(plugin, "npc", 50) {
            @Override
            public void loadConfig(String id, ConfigurationBase<RCNPCsPlugin> config) {
                loadNpcConfig(id, config);
            }

            @Override
            public void unloadConfig(String id) {
                unloadNpcConfig(id);
            }
        };
        Quests.registerQuestLoader(configLoader);
        ConfigUtil.loadRecursiveConfigs(plugin, plugin.getConfig().npcPath, configLoader);
    }

    private void loadNpcConfig(String id, ConfigurationBase config) {
        if (loadedNPCConfigs.containsKey(id)) {
            getPlugin().getLogger().warning("NPC with id " + id + " already exists!");
            return;
        }
        ConfigStorage storage = new ConfigStorage(config);
        storage.load();

        DataKey dataKey = storage.getKey("");
        if (!dataKey.getBoolean("enabled", true)) {
            getPlugin().getLogger().info(id + " NOT ENABLED.");
            return;
        }
        this.loadedNPCConfigs.put(id, storage);
        if (isLoaded()) {
            loadNpcFromConfig(CitizensAPI.getNPCRegistry(), id, dataKey);
        }
    }

    private void loadNpcFromConfig(NPCRegistry registry, String id, DataKey dataKey) {
        UUID uuid = UUID.fromString(dataKey.getString("uuid", UUID.randomUUID().toString()));

        if (registry.getByUniqueId(uuid) != null) {
            getPlugin().getLogger().warning("NPC " + id + " with UUID " + uuid + " already loaded!");
            return;
        }

        EntityType entityType = matchEntityType(dataKey.getString("traits.type", "PLAYER"));
        NPC npc = registry.createNPC(entityType, uuid, createUniqueNPCId(registry), dataKey.getString("name"));
        addToFTrait(npc, id);
        idToPathMapping.put(npc.getId(), id);
        npc.load(dataKey);
    }

    private void unloadNpcConfig(String id) {
        ConfigStorage config = this.loadedNPCConfigs.remove(id);
        if (config != null) {
            config.getConfig().set("enabled", false);
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
        while (registry.getById(lastCreatedNPCId) != null) {
            lastCreatedNPCId++;
        }
        return lastCreatedNPCId;
    }

    @Override
    public void loadInto(NPCRegistry registry) {
        for (String id : getLoadedNPCConfigs().keySet()) {
            try {
                ConfigStorage config = getLoadedNPCConfigs().get(id);
                DataKey dataKey = config.getKey("");
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
        final ConfigStorage[] configs = getLoadedNPCConfigs().values().toArray(new ConfigStorage[0]);
        new Thread(() -> {
            for (ConfigStorage config : configs) {
                config.save();
            }
        }).start();
    }

    @Override
    public void saveToDiskImmediate() {
        getLoadedNPCConfigs().values().forEach(ConfigStorage::save);
    }

    @Override
    public void store(NPC npc) {
        String id = idToPathMapping.containsKey(npc.getId()) ? idToPathMapping.get(npc.getId()) : npc.getUniqueId() + "-" + npc.getFullName();
        ConfigStorage npcConfig;
        if (loadedNPCConfigs.containsKey(id)) {
            npcConfig = loadedNPCConfigs.get(id);
        } else {
            File file = new File(getPlugin().getNewNpcsPath(), id + ".npc.yml");
            npcConfig = new ConfigStorage(getPlugin().configure(new SimpleConfiguration<>(getPlugin(), file)));
            loadedNPCConfigs.put(id, npcConfig);
            idToPathMapping.put(npc.getId(), id);
        }
        addToFTrait(npc, id);
        npc.save(npcConfig.getKey(""));
        getPlugin().getLogger().info("Created new NPC save file: " + npcConfig.getFile().getName());
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
        getLoadedNPCConfigs().values().forEach(ConfigStorage::load);
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

    private static void addToFTrait(NPC npc, String pathId) {
        ToFNPCTrait trait = new ToFNPCTrait();
        trait.setConfigPath(pathId);
        npc.addTrait(trait);
    }
}

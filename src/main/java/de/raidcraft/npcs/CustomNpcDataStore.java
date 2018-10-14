package de.raidcraft.npcs;

import lombok.Data;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.SimpleNPCDataStore;
import net.citizensnpcs.api.util.Storage;

@Data
public class CustomNpcDataStore implements NPCDataStore {

    private final RCNPCsPlugin plugin;

    @Override
    public void clearData(NPC npc) {

    }

    @Override
    public int createUniqueNPCId(NPCRegistry npcRegistry) {
        return 0;
    }

    @Override
    public void loadInto(NPCRegistry npcRegistry) {

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

package de.raidcraft.npcs.traits;

import de.raidcraft.npcs.ConfigStorage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;

@TraitName("ToF")
@Data
@EqualsAndHashCode(callSuper = true)
public class ToFNPCTrait extends Trait {

    @Persist
    private String configPath;

    public ToFNPCTrait() {
        super("ToF");
    }

    @Override
    public void load(DataKey key) throws NPCLoadException {
        if (key instanceof ConfigStorage.YamlKey) {
            this.configPath = ((ConfigStorage.YamlKey) key).getId();
        }
    }
}

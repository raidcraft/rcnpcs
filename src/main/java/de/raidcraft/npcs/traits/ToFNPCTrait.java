package de.raidcraft.npcs.traits;

import lombok.Data;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

@TraitName("ToF")
@Data
public class ToFNPCTrait extends Trait {

    @Persist
    private String configPath;

    public ToFNPCTrait() {
        super("ToF");
    }
}

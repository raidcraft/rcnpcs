package de.raidcraft.npcs.traits;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

@TraitName("ToF")
@Data
@EqualsAndHashCode(callSuper = true)
public class ToFNPCTrait extends Trait {

    @Persist
    private String configPath;

    public ToFNPCTrait() {
        super("ToF");
    }
}

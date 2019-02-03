package de.raidcraft.npcs.traits;

import de.raidcraft.api.disguise.Disguise;
import de.raidcraft.api.npc.RC_Traits;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.npc.skin.SkinnableEntity;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@TraitName("Disguise")
public class DisguiseTrait extends Trait {

    private Disguise disguise;
    private boolean appliedSkin = false;

    public DisguiseTrait() {
        super(RC_Traits.DISGUISE);
    }

    @Override
    public void load(DataKey key) throws NPCLoadException {
        Disguise.fromAlias(key.getString("")).ifPresent(this::setDisguise);
    }

    @Override
    public void save(DataKey key) {
        key.setString("", getDisguise().map(Disguise::getAlias).orElse(null));
    }

    public void setDisguise(Disguise disguise) {
        this.disguise = disguise;
        disguise();
    }

    public Optional<Disguise> getDisguise() {
        return Optional.ofNullable(this.disguise);
    }

    @Override
    public void onSpawn() {

        disguise();
    }

    private void disguise() {

        if (getNPC() == null || !getNPC().isSpawned() || !(getNPC().getEntity() instanceof SkinnableEntity)) {
            return;
        }

        // citizens will respawn the NPC after changing its skin
        // not checking this will cause an endless loop
        if (appliedSkin) return;

        getDisguise().ifPresent(disguise -> {
            appliedSkin = true;
            SkinnableEntity skinnableEntity = (SkinnableEntity) getNPC().getEntity();
            skinnableEntity.setSkinPersistent(disguise.getSkinOwner(), disguise.getSkinSignature(), disguise.getSkinTexture());
        });
    }
}

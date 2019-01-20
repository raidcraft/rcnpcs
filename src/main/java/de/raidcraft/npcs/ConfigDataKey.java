package de.raidcraft.npcs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConfigDataKey extends DataKey {

    private final ConfigurationSection config;

    protected ConfigDataKey(ConfigurationSection config) {
        super("");
        this.config = config;
    }

    @Override
    public boolean getBoolean(String s) {
        return getConfig().getBoolean(s);
    }

    @Override
    public double getDouble(String s) {
        return getConfig().getDouble(s);
    }

    @Override
    public int getInt(String s) {
        return getConfig().getInt(s);
    }

    @Override
    public long getLong(String s) {
        return getConfig().getLong(s);
    }

    @Override
    public Object getRaw(String s) {
        return getConfig().get(s);
    }

    @Override
    public DataKey getRelative(String s) {
        return new ConfigDataKey(getConfig().getConfigurationSection(s));
    }

    @Override
    public String getString(String s) {
        return getConfig().getString(s);
    }

    @Override
    public Iterable<DataKey> getSubKeys() {
        return getConfig().getKeys(false).stream()
                .map(this::getRelative)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getValuesDeep() {
        return getConfig().getKeys(true).stream()
                .collect(Collectors.toMap(key -> key, this::getRaw));
    }

    @Override
    public boolean keyExists(String s) {
        return getConfig().isSet(s);
    }

    @Override
    public String name() {
        return getConfig().getName();
    }

    @Override
    public void removeKey(String s) {
        getConfig().set(s, null);
    }

    @Override
    public void setBoolean(String s, boolean b) {
        getConfig().set(s, b);
    }

    @Override
    public void setDouble(String s, double v) {
        getConfig().set(s, v);
    }

    @Override
    public void setInt(String s, int i) {
        getConfig().set(s, i);
    }

    @Override
    public void setLong(String s, long l) {
        getConfig().set(s, l);
    }

    @Override
    public void setRaw(String s, Object o) {
        getConfig().set(s, o);
    }

    @Override
    public void setString(String s, String s1) {
        getConfig().set(s, s1);
    }
}

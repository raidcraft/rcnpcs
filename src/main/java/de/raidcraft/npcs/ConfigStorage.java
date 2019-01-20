package de.raidcraft.npcs;

import de.raidcraft.api.config.ConfigurationBase;
import lombok.Data;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.FileStorage;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class ConfigStorage implements FileStorage {

    private final ConfigurationBase config;

    public ConfigStorage(ConfigurationBase config) {
        this.config = config;
    }

    @Override
    public File getFile() {
        return getConfig().getFile();
    }

    @Override
    public DataKey getKey(String root) {
        return new YamlKey(root);
    }

    @Override
    public boolean load() {
        getConfig().load();
        return true;
    }

    @Override
    public void save() {
        getConfig().save();
    }

    private boolean pathExists(String key) {
        return config.get(key) != null;
    }

    public class YamlKey extends DataKey {
        public YamlKey(String root) {
            super(root);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj) || getClass() != obj.getClass()) {
                return false;
            }
            YamlKey other = (YamlKey) obj;
            return getOuterType().equals(other.getOuterType());
        }

        @Override
        public boolean getBoolean(String key) {
            String path = createRelativeKey(key);
            if (pathExists(path)) {
                if (config.getString(path) == null)
                    return config.getBoolean(path);
                return Boolean.parseBoolean(config.getString(path));
            }
            return false;
        }

        @Override
        public boolean getBoolean(String key, boolean def) {
            return config.getBoolean(createRelativeKey(key), def);
        }

        @Override
        public double getDouble(String key) {
            return getDouble(key, 0);
        }

        @Override
        public double getDouble(String key, double def) {
            String path = createRelativeKey(key);
            if (pathExists(path)) {
                Object value = config.get(path);
                if (value instanceof Number)
                    return ((Number) value).doubleValue();
                String raw = value.toString();
                if (raw.isEmpty())
                    return def;
                return Double.parseDouble(raw);
            }
            return def;
        }

        @Override
        public int getInt(String key) {
            return getInt(key, 0);
        }

        @Override
        public int getInt(String key, int def) {
            String path = createRelativeKey(key);
            if (pathExists(path)) {
                Object value = config.get(path);
                if (value instanceof Number)
                    return ((Number) value).intValue();
                String raw = value.toString();
                if (raw.isEmpty())
                    return def;
                return Integer.parseInt(raw);
            }
            return def;
        }

        @Override
        public long getLong(String key) {
            return getLong(key, 0L);
        }

        @Override
        public long getLong(String key, long def) {
            String path = createRelativeKey(key);
            if (pathExists(path)) {
                Object value = config.get(path);
                if (value instanceof Number)
                    return ((Number) value).longValue();
                String raw = value.toString();
                if (raw.isEmpty())
                    return def;
                return Long.parseLong(raw);
            }
            return def;
        }

        private ConfigStorage getOuterType() {
            return ConfigStorage.this;
        }

        @Override
        public Object getRaw(String key) {
            return config.get(createRelativeKey(key));
        }

        @Override
        public YamlKey getRelative(String relative) {
            if (relative == null || relative.isEmpty())
                return this;
            return new YamlKey(createRelativeKey(relative));
        }

        public ConfigStorage getStorage() {
            return ConfigStorage.this;
        }

        @Override
        public String getString(String key) {
            String path = createRelativeKey(key);
            if (pathExists(path)) {
                return config.get(path).toString();
            }
            return "";
        }

        @Override
        public Iterable<DataKey> getSubKeys() {
            ConfigurationSection section = config.getConfigurationSection(path);
            if (section == null)
                return Collections.emptyList();
            List<DataKey> res = new ArrayList<DataKey>();
            for (String key : section.getKeys(false)) {
                res.add(getRelative(key));
            }
            return res;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map<String, Object> getValuesDeep() {
            ConfigurationSection subSection = config.getConfigurationSection(path);
            return (Map<String, Object>) (subSection == null ? Collections.emptyMap() : subSection.getValues(true));
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = prime * super.hashCode() + getOuterType().hashCode();
            return result;
        }

        @Override
        public boolean keyExists(String key) {
            Object value = config.get(createRelativeKey(key));
            if (value == null) {
                return false;
            }
            return true;
        }

        @Override
        public String name() {
            int last = path.lastIndexOf('.');
            return path.substring(last == 0 ? 0 : last + 1);
        }

        @Override
        public void removeKey(String key) {
            config.set(createRelativeKey(key), null);
        }

        @Override
        public void setBoolean(String key, boolean value) {
            config.set(createRelativeKey(key), value);
        }

        @Override
        public void setDouble(String key, double value) {
            config.set(createRelativeKey(key), String.valueOf(value));
        }

        @Override
        public void setInt(String key, int value) {
            config.set(createRelativeKey(key), value);
        }

        @Override
        public void setLong(String key, long value) {
            config.set(createRelativeKey(key), value);
        }

        @Override
        public void setRaw(String key, Object value) {
            config.set(createRelativeKey(key), value);
        }

        @Override
        public void setString(String key, String value) {
            config.set(createRelativeKey(key), value);
        }

        @Override
        public String toString() {
            return "YamlKey [path=" + path + "]";
        }
    }
}

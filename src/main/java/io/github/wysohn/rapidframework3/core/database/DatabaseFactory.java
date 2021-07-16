package io.github.wysohn.rapidframework3.core.database;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.utils.FileUtil;

import javax.inject.Named;
import java.io.File;
import java.util.function.Function;

public class DatabaseFactory implements IDatabaseFactory {
    private final String pluginName;
    private final File pluginDir;
    private final ISerializer serializer;
    private final ManagerConfig config;
    private final String typeName;

    @Inject
    public DatabaseFactory(@Named("pluginName") String pluginName,
                           @PluginDirectory File pluginDir,
                           ISerializer serializer,
                           ManagerConfig config,
                           @Assisted String typeName) {
        this.pluginName = pluginName;
        this.pluginDir = pluginDir;
        this.serializer = serializer;
        this.config = config;
        this.typeName = typeName;
    }

    @Override
    public <K, V extends CachedElement<K>> IDatabase<K, V> create(String tableName,
                                                                  Class<V> type,
                                                                  Function<String, K> strToKey) {
        switch (typeName) {
            case "mysql":
                return new MySQLDatabase<>(type,
                                           (String) config.get("db.address").orElse("127.0.0.1"),
                                           (String) config.get("db.name").orElse(pluginName),
                                           (String) config.get("db.tablename").orElse(tableName),
                                           (String) config.get("db.username").orElse("root"),
                                           (String) config.get("db.password").orElse("1234"));
            case "file":
                if (!pluginDir.exists())
                    pluginDir.mkdirs();

                return new DatabaseFile<>(
                        serializer,
                        tableName,
                        type,
                        "json",
                        FileUtil::readFromFile,
                        FileUtil::writeToFile,
                        FileUtil.join(pluginDir, tableName),
                        strToKey);
            default:
                if (!pluginDir.exists())
                    pluginDir.mkdirs();

                File file = new File(pluginDir, tableName);
                return new H2PersistDatabase<>(type,
                                               file.getAbsolutePath(),
                                               (String) config.get("db.username").orElse("root"),
                                               (String) config.get("db.password").orElse("1234"));
        }
    }
}
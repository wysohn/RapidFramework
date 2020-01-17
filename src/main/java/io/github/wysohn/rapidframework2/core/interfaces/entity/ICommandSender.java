package io.github.wysohn.rapidframework2.core.interfaces.entity;

import java.util.Locale;

public interface ICommandSender extends IPluginEntity {
    void sendMessageRaw(String... msg);

    Locale getLocale();

    /**
     * Check if this sender has permission. The permission is checked
     * based on OR operation, which means that it will return true if this sender has at least
     * one of the permissions provided.
     * @param permissions permissions to check
     * @return true if has permission; false if this sender does not have any matching permission.
     */
    boolean hasPermission(String... permissions);

    String getDisplayName();
}

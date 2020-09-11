package io.github.wysohn.rapidframework3.core.interfaces.plugn;

public interface PluginRuntime {
    default void preload() throws Exception {

    }

    void enable() throws Exception;

    void load() throws Exception;

    void disable() throws Exception;
}

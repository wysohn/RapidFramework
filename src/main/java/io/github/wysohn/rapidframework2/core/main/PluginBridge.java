package io.github.wysohn.rapidframework2.core.main;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.TaskSupervisor;

import java.util.List;
import java.util.function.Consumer;

public interface PluginBridge {
    TaskSupervisor getTaskSupervisor();

    void forEachSender(Consumer<ICommandSender> consumer);

    PluginMain getMain();

    void init();

    void enable();

    void disable();

    boolean onCommand(ICommandSender sender, String command, String label, String[] args_in);

    List<String> onTabComplete(ICommandSender sender, String command, String alias, String[] args);
}
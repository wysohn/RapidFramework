package io.github.wysohn.rapidframework2.bukkit.testutils.manager;

import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.config.ManagerConfig;
import io.github.wysohn.rapidframework2.tools.FileUtil;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.mockito.internal.util.reflection.Whitebox;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ManagerTestBuilder2<M extends PluginMain.Manager> {
    private final PluginMain main = mock(PluginMain.class);
    private final ManagerConfig config = mock(ManagerConfig.class);

    private final M manager;

    private final List<Execution<M, ?>> executions = new LinkedList<>();

    private ManagerTestBuilder2(M manager) {
        this.manager = manager;
        Whitebox.setInternalState(manager, "main", main);
        when(main.getPluginDirectory()).thenReturn(new File("build/tmp/test/"));
        when(main.conf()).thenReturn(config);


    }

    public static <M extends PluginMain.Manager> ManagerTestBuilder2<M> of(M manager) {
        return new ManagerTestBuilder2<>(manager);
    }

    public ManagerTestBuilder2<M> config(String key, Object output) {
        when(config.get(eq(key))).thenReturn(Optional.of(output));
        return this;
    }

    public ManagerTestBuilder2<M> enable() {
        try {
            manager.enable();
            return this;
        } catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    public ManagerTestBuilder2<M> disable() {
        try {
            manager.disable();
            return this;
        } catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    public ManagerTestBuilder2<M> mockAny(Execution<M, Void> fn) {
        this.executions.add(fn);
        return this;
    }

    public ManagerTestBuilder2<M> mockEvent(Execution<M, Event> fn) {
        this.executions.add(fn);
        return this;
    }

    public ManagerTestBuilder2<M> expect(Execution<M, Boolean> fn) {
        this.executions.add(fn);
        return this;
    }

    public boolean test() {
        try {
            for (Execution<M, ?> execution : executions) {
                Object result = execution.apply(manager);
                if (result instanceof Event) {
                    Class<? extends Event> eventClass = (Class<? extends Event>) result.getClass();
                    for (Method method : manager.getClass().getMethods()) {
                        Annotation annotation = method.getAnnotation(EventHandler.class);
                        if (annotation == null
                                || method.getParameterCount() != 1
                                || method.getParameterTypes()[0] != eventClass)
                            continue;

                        try {
                            method.invoke(manager, result);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (result instanceof Boolean) {
                    if (!(boolean) result)
                        return false;
                }
            }

            return true;
        } finally {
            FileUtil.delete(main.getPluginDirectory());
        }
    }

    @FunctionalInterface
    public interface Execution<M extends PluginMain.Manager, T> {
        T apply(M manager);
    }
}

package io.github.wysohn.rapidframework4.interfaces.entity;

import io.github.wysohn.rapidframework4.interfaces.IMemento;

public interface IEntitySnapshot {
    IMemento saveState();

    void restoreState(IMemento savedState);
}

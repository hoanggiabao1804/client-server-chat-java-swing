package component;

import java.awt.Component;
import java.awt.Dimension;

public interface AppContext {
    void draw();

    void switchContext(String newContext);

    Component getRootComponent();

    Dimension getSize();
}

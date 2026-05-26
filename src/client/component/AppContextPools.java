package component;

import java.awt.Container;
import java.util.HashMap;
import java.util.Map;

import component.auth.LoginForm;
import component.auth.RegisterForm;
import component.menu.MainMenu;
import component.menu.UserProfile;

public class AppContextPools {
    private static Map<String, AppContext> contextPool;

    public AppContextPools(Container rootContainer) {
        contextPool = new HashMap<String, AppContext>();

        contextPool.put("loginForm", new LoginForm(rootContainer));
        contextPool.put("registerForm", new RegisterForm(rootContainer));
        contextPool.put("mainMenu", new MainMenu(rootContainer));
        contextPool.put("userProfile", new UserProfile(rootContainer));
    }

    public AppContext getContext(String name) {
        return contextPool.getOrDefault(name, null);
    }

    public void setContext(String name, AppContext context) {
        contextPool.put(name, context);
    }

    public void reset(Container rootContainer) {
        contextPool.clear();
        contextPool.put("loginForm", new LoginForm(rootContainer));
        contextPool.put("registerForm", new RegisterForm(rootContainer));
        contextPool.put("mainMenu", new MainMenu(rootContainer));
        contextPool.put("userProfile", new UserProfile(rootContainer));
    }
}

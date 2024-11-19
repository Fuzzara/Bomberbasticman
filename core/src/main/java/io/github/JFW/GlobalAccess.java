package io.github.JFW;

import io.github.JFW.Entitys.Actors;
import io.github.JFW.Entitys.Player;
import io.github.JFW.System.Config;

public class GlobalAccess {
    private static GlobalAccess instance;
    private Actors actors;
    private Config config;

    private GlobalAccess() {}

    public static GlobalAccess getInstance() {
        if (instance == null) {
            instance = new GlobalAccess();
        }
        return instance;
    }

    public Actors getActors() {
        return actors;
    }

    public void setActors(Actors actors) {
        this.actors = actors;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public boolean isBonusLevel() {return config.isBonusLevel();}
}

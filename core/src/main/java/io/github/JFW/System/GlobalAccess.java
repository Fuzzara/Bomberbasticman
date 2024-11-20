package io.github.JFW.System;

import io.github.JFW.Entities.Actors;

public class GlobalAccess {
    private static GlobalAccess instance;
    private Actors actors;
    private Config config;
    private Scoreboard scoreboard;

    private GlobalAccess() {}

    public static GlobalAccess getInstance() {
        if (instance == null) {
            instance = new GlobalAccess();
        }
        return instance;
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

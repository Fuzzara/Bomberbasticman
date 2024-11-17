package io.github.JFW.MapEnv;

import com.badlogic.gdx.utils.Array;

public class MapSystem {
    private Array<Map> maps;

    public MapSystem() {
        maps = new Array<>();
        loadMaps();
    }

    private void loadMaps() {
        maps.add(new Map("maps/lvl1.tmx"));
        maps.add(new Map("maps/lvl2.tmx"));
        maps.add(new Map("maps/lvl3.tmx"));
        maps.add(new Map("maps/lvl4.tmx"));
        maps.add(new Map("maps/lvl5.tmx"));
    }

    public Map getMap(int index) {
        return maps.get(index);
    }

    public int getMapCount() {
        return maps.size;
    }
}

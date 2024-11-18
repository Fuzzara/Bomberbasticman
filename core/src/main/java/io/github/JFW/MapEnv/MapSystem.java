package io.github.JFW.MapEnv;

import com.badlogic.gdx.utils.Array;

public class MapSystem {
    private Array<Map> maps;

    public MapSystem() {
        maps = new Array<>();
        loadMaps();
    }

    private void loadMaps() {
        maps.add(new Map("maps/lvl1.tmx", 2)); //aqui indica que powerup va a aparecer por nivel
        maps.add(new Map("maps/lvl2.tmx", 0));
        maps.add(new Map("maps/lvl3.tmx", 0));
        maps.add(new Map("maps/lvl4.tmx", 0));
        maps.add(new Map("maps/lvl5.tmx", 0));
    }

    public Map getMap(int index) {
        return maps.get(index);
    }

    public int getMapCount() {
        return maps.size;
    }
}

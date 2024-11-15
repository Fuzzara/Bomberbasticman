package io.github.JFW;

import com.badlogic.gdx.utils.Array;

public class MapSystem {
    private Array<Map> maps;
    
    public MapSystem() {
        maps = new Array<>();
        loadMaps();
    }
    
    private void loadMaps() {
        maps.add(new Map("maps/map1.tmx"));
    }
    
    public Map getMap(int index) {
        return maps.get(index);
    }
    
    public int getMapCount() {
        return maps.size;
    }
}

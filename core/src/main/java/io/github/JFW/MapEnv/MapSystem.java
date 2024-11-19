package io.github.JFW.MapEnv;

import com.badlogic.gdx.utils.Array;

public class MapSystem {
    private Array<Map> maps;
    private Map currentMap;

    public MapSystem() {
        maps = new Array<>();
        loadMaps();
    }
    public enum PowerUpType {
        SUN, //0
        GOLDEN_BOMB, //1
        DETONATOR, //2
        SKATES, //3
        STRIPPED_BOMB, //4
        STRIPPED_WALL, //5
        QUESTION_MARK, //6
        FIRE_MAN //7
    }
    private void loadMaps() {
        //TODO: RANDOM FOR THE POWERUPS PLAYER ALREADY HAS
        int x = 0;
        //Primera skin 1
        maps.add(new Map("maps/lvl1.tmx", 0)); //aqui indica que powerup va a aparecer por nivel
        maps.add(new Map("maps/lvl1.tmx", 1));
        maps.add(new Map("maps/lvl1.tmx", 2));
        maps.add(new Map("maps/lvl1.tmx", 3));
        //Nivel bonus
            //Aqui va el nivel bonus :3
        //Segunda skin 6
        maps.add(new Map("maps/lvl2.tmx", x));
        maps.add(new Map("maps/lvl2.tmx", x));
        maps.add(new Map("maps/lvl2.tmx", x));
        maps.add(new Map("maps/lvl2.tmx", x));
        //Nivel bonus
            //Aqui va el nivel bonus :3
        //Tercera skin 11
        maps.add(new Map("maps/lvl3.tmx", 4));
        maps.add(new Map("maps/lvl3.tmx", x));
        maps.add(new Map("maps/lvl3.tmx", x));
        maps.add(new Map("maps/lvl3.tmx", 5));
        //Nivel bonus
            //Aqui va el nivel bonus :3
        //Cuarta skin 15
        maps.add(new Map("maps/lvl4.tmx", x));
        maps.add(new Map("maps/lvl4.tmx", x));
        maps.add(new Map("maps/lvl4.tmx", x));
        maps.add(new Map("maps/lvl4.tmx", x));
        //Nivel bonus
            //Aqui va el nivel bonus :3
        //Quinta skin 21
        maps.add(new Map("maps/lvl5.tmx", x));
        maps.add(new Map("maps/lvl5.tmx", x));
        maps.add(new Map("maps/lvl5.tmx", 5));
        maps.add(new Map("maps/lvl5.tmx", x));
        //Nivel bonus
            //Aqui va el nivel bonus :3
        //TODO: alguna manera para que siga infinitamente o algo asi xd
    }

    public Map getMap(int index) {
        // Dispose of the current map if it exists
        if (currentMap != null) {
            currentMap.dispose();
        }

        // Get and store the new current map
        currentMap = maps.get(index);

        // Ensure collisions are properly prepared for the new map
        currentMap.PrepareMapCollisions();

        return currentMap;
    }

    public int getMapCount() {
        return maps.size;
    }

    public void dispose() {
        // Dispose all maps when the system is disposed
        for (Map map : maps) {
            if (map != null) {
                map.dispose();
            }
        }
        maps.clear();
    }
}

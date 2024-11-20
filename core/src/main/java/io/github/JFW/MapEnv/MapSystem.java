package io.github.JFW.MapEnv;

import com.badlogic.gdx.utils.Array;
import io.github.JFW.Entitys.Player;
import io.github.JFW.statePlayer;

import java.util.Random;

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

        //Primera skin 1
        maps.add(new Map("maps/lvl1.tmx", 0)); //este no cuenta porque la mierda es indice 1
        maps.add(new Map("maps/lvl1.tmx", 0));
        maps.add(new Map("maps/lvl1.tmx", 1));
        maps.add(new Map("maps/lvl1.tmx", 2));
        maps.add(new Map("maps/lvl1.tmx", 3));
        //Nivel bonus
        maps.add(new Map("maps/lvl6.tmx", 1));
        //Segunda skin 6
        maps.add(new Map("maps/lvl2.tmx", 100));
        maps.add(new Map("maps/lvl2.tmx", 100));
        maps.add(new Map("maps/lvl2.tmx", 100));
        maps.add(new Map("maps/lvl2.tmx", 100));
        //Nivel bonus
        maps.add(new Map("maps/lvl6.tmx", 1));
        //Tercera skin 11
        maps.add(new Map("maps/lvl3.tmx", 4));
        maps.add(new Map("maps/lvl3.tmx", 100));
        maps.add(new Map("maps/lvl3.tmx", 100));
        maps.add(new Map("maps/lvl3.tmx", 5));
        //Nivel bonus
        maps.add(new Map("maps/lvl6.tmx", 1));
        //Cuarta skin 15
        maps.add(new Map("maps/lvl4.tmx", 100));
        maps.add(new Map("maps/lvl4.tmx", 100));
        maps.add(new Map("maps/lvl4.tmx", 100));
        maps.add(new Map("maps/lvl4.tmx", 100));
        //Nivel bonus
        maps.add(new Map("maps/lvl6.tmx", 1));
        //Quinta skin 21
        maps.add(new Map("maps/lvl5.tmx", 100));
        maps.add(new Map("maps/lvl5.tmx", 100));
        maps.add(new Map("maps/lvl5.tmx", 5));
        maps.add(new Map("maps/lvl5.tmx", 100));
        //Nivel bonus
        maps.add(new Map("maps/lvl6.tmx", 1));
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

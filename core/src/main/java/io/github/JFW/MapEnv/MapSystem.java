package io.github.JFW.MapEnv;

import com.badlogic.gdx.utils.Array;

public class MapSystem {
    private Array<Map> maps;
    private Map currentMap;

    public MapSystem() {
        maps = new Array<>();
        loadMaps();
    }

    // Enum para los tipos de PowerUp
    public enum PowerUpType {
        SUN, //------------0
        GOLDEN_BOMB, //----1
        DETONATOR, //------2
        SKATES, //---------3
        STRIPPED_BOMB, //--4
        STRIPPED_WALL, //--5
        QUESTION_MARK, //--6
        FIRE_MAN //--------7
    }

    // Carga los mapas en el sistema
    private void loadMaps() {
        // Primera skin 1
        maps.add(new Map("maps/lvl1.tmx", 0)); // este no cuenta porque la mierda es indice 1
        maps.add(new Map("maps/lvl1.tmx", 0));
        maps.add(new Map("maps/lvl1.tmx", 1));
        maps.add(new Map("maps/lvl1.tmx", 2));
        maps.add(new Map("maps/lvl1.tmx", 3));
        // Nivel bonus
        maps.add(new Map("maps/lvl6.tmx", 1));
        // Segunda skin 6
        maps.add(new Map("maps/lvl2.tmx", 100));
        maps.add(new Map("maps/lvl2.tmx", 100));
        maps.add(new Map("maps/lvl2.tmx", 100));
        maps.add(new Map("maps/lvl2.tmx", 100));
        // Nivel bonus
        maps.add(new Map("maps/lvl6.tmx", 1));
        // Tercera skin 11
        maps.add(new Map("maps/lvl3.tmx", 4));
        maps.add(new Map("maps/lvl3.tmx", 100));
        maps.add(new Map("maps/lvl3.tmx", 100));
        maps.add(new Map("maps/lvl3.tmx", 5));
        // Nivel bonus
        maps.add(new Map("maps/lvl6.tmx", 1));
        // Cuarta skin 15
        maps.add(new Map("maps/lvl4.tmx", 100));
        maps.add(new Map("maps/lvl4.tmx", 100));
        maps.add(new Map("maps/lvl4.tmx", 100));
        maps.add(new Map("maps/lvl4.tmx", 100));
        // Nivel bonus
        maps.add(new Map("maps/lvl6.tmx", 1));
        // Quinta skin 21
        maps.add(new Map("maps/lvl5.tmx", 100));
        maps.add(new Map("maps/lvl5.tmx", 100));
        maps.add(new Map("maps/lvl5.tmx", 5));
        maps.add(new Map("maps/lvl5.tmx", 100));
        // Nivel bonus
        maps.add(new Map("maps/lvl6.tmx", 1));
        // TODO: alguna manera para que siga infinitamente o algo asi xd
    }

    // Devuelve el mapa n y prepara sus colisiones
    public Map getMap(int index) {
        // Limpiar cosas del mapa anterior
        if (currentMap != null) {
            currentMap.dispose();
        }
        // Setea el mapa actual
        currentMap = maps.get(index);

        // Prepara colisiones del mapa nueva
        currentMap.prepareMapCollisions();

        return currentMap;
    }

    // Devuelve la cantidad de mapas cargados
    public int getMapCount() {
        return maps.size;
    }

    // Libera los recursos de todos los mapas
    public void dispose() {
        // Bota todos los mapas
        for (Map map : maps) {
            if (map != null) {
                map.dispose();
            }
        }
        maps.clear();
    }
}

package io.github.JFW;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.JFW.Entitys.Enemy;
import io.github.JFW.MapEnv.Map;

public class EnemyFactory { //muy poo de nuestra parte
    public static Enemy createEnemy(int type, float x, float y, Map map, float PlayerSpeed) {
    float speed;
        switch (type) {
            case 1:
                speed = (2.0f / 3.0f) * PlayerSpeed;
                return new Enemy(speed, x, y, 100, 1, false,"enemies/Globo.png", map);
            case 2:
                return new Enemy(PlayerSpeed, x, y, 200, 1, false,"enemies/Cel.png", map);
            case 3:
                speed = (4.0f / 3.0f) * PlayerSpeed;
                return new Enemy(speed, x, y, 400, 1, false,"enemies/Haki.png", map);
            case 4:
                speed = (1.0f / 3.0f) * PlayerSpeed;
                return new Enemy(speed, x, y, 1000, 1, true,"enemies/Espon.png", map);
                case 5:
                return new Enemy(PlayerSpeed, x, y, 2000, 1, true,"enemies/Fant.png", map);
            case 6:
                speed = (3.0f / 2.0f) * PlayerSpeed;
                return new Enemy(speed, x, y, 3000, 1, false,"enemies/Mon.png", map);
            case 7:
                speed = (3.0f / 2.0f) * PlayerSpeed;
                return new Enemy(speed, x, y, 4000, 1, true,"enemies/MonG.png", map);
            default:
                return null;
        }
    }
}

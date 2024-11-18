package io.github.JFW;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.JFW.Entitys.Enemy;
import io.github.JFW.MapEnv.Map;
import io.github.JFW.System.Animator;

public class EnemyFactory { //muy poo de nuestra parte
    public static Enemy createEnemy(int type, float x, float y, Map map, float PlayerSpeed) {
    float speed;
        switch (type) {
            case 1:
                speed = (2.0f / 3.0f) * PlayerSpeed;
                Animator GloboAnimation = new Animator("enemies/Globo.png", 4, 1, 0, 3, 0.2f, Animation.PlayMode.LOOP);
                return new Enemy(speed, x, y, 100, 1, false, map, GloboAnimation);
            case 2:
                Animator CelAnimation = new Animator("enemies/Cel.png", 6, 1, 0, 5, 0.3f, Animation.PlayMode.LOOP_PINGPONG);
                return new Enemy(PlayerSpeed, x, y, 200, 1, false, map, CelAnimation);
            case 3:
                speed = (4.0f / 3.0f) * PlayerSpeed;
                Animator HakiAnimation = new Animator("enemies/Haki.png", 12, 1, 0, 11, 0.1f, Animation.PlayMode.LOOP);
                return new Enemy(speed, x, y, 400, 1, false, map, HakiAnimation);
            case 4:
                speed = (1.0f / 3.0f) * PlayerSpeed;
                Animator EsponAnimation = new Animator("enemies/Espon.png", 3, 1, 0, 2, 0.3f, Animation.PlayMode.LOOP_PINGPONG);
                return new Enemy(speed, x, y, 1000, 1, true, map, EsponAnimation);
                case 5:
                    Animator FantAnimation = new Animator("enemies/Fant.png", 10, 1, 0, 9, 0.05f, Animation.PlayMode.LOOP_PINGPONG);
                return new Enemy(PlayerSpeed, x, y, 2000, 1, true, map, FantAnimation);
            case 6:
                speed = (3.0f / 2.0f) * PlayerSpeed;
                Animator MonAnimation = new Animator("enemies/Mon.png", 10, 1, 0, 9, 0.1f, Animation.PlayMode.LOOP);
                return new Enemy(speed, x, y, 3000, 1, false, map, MonAnimation);
            case 7:
                speed = (3.0f / 2.0f) * PlayerSpeed;
                Animator MonGAnimation = new Animator("enemies/MonG.png", 10, 1, 0, 9, 0.1f, Animation.PlayMode.LOOP);
                return new Enemy(speed, x, y, 4000, 1, true, map, MonGAnimation);
            default:
                return null;
        }
    }
}

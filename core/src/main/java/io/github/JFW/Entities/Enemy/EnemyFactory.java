package io.github.JFW.Entities.Enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import io.github.JFW.MapEnv.Map;
import io.github.JFW.Graphics.Animator;

public class EnemyFactory { //muy poo de nuestra parte
    public static Enemy createEnemy(int type, float x, float y, Map map, float PlayerSpeed) {
    float speed;
        switch (type) {
            case 0:
                speed = (2.0f / 3.0f) * PlayerSpeed;
                Animator GloboAnimation = new Animator("enemies/Globo.png", 4, 1, 0, 3, 0.2f, Animation.PlayMode.LOOP);
                return new Enemy(speed, x, y, 100, 0, false, map, GloboAnimation);
            case 1:
                Animator CelAnimation = new Animator("enemies/Cel.png", 6, 1, 0, 5, 0.3f, Animation.PlayMode.LOOP_PINGPONG);
                return new Enemy(PlayerSpeed, x, y, 200, 0, false, map, CelAnimation);
            case 2:
                speed = (4.0f / 3.0f) * PlayerSpeed;
                Animator HakiAnimation = new Animator("enemies/Haki.png", 12, 1, 0, 11, 0.1f, Animation.PlayMode.LOOP);
                return new Enemy(speed, x, y, 400, 2, false, map, HakiAnimation);
            case 3:
                speed = (1.0f / 3.0f) * PlayerSpeed;
                Animator EsponAnimation = new Animator("enemies/Espon.png", 3, 1, 0, 2, 0.3f, Animation.PlayMode.LOOP_PINGPONG);
                return new Enemy(speed, x, y, 1000, 1, true, map, EsponAnimation);
            case 4:
                Animator FantAnimation = new Animator("enemies/Fant.png", 10, 1, 0, 9, 0.05f, Animation.PlayMode.LOOP_PINGPONG);
                return new Enemy(PlayerSpeed, x, y, 2000, 2, true, map, FantAnimation);
            case 5:
                speed = (3.0f / 2.0f) * PlayerSpeed;
                Animator MonAnimation = new Animator("enemies/Mon.png", 10, 1, 0, 9, 0.1f, Animation.PlayMode.LOOP);
                return new Enemy(speed, x, y, 3000, 3, false, map, MonAnimation);
            case 6:
                speed = (3.0f / 2.0f) * PlayerSpeed;
                Animator MonGAnimation = new Animator("enemies/MonG.png", 10, 1, 0, 9, 0.1f, Animation.PlayMode.LOOP);
                return new Enemy(speed, x, y, 4000, 4, true, map, MonGAnimation);
            default:
                return null;
        }
    }
}

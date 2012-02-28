package com.unhappyrobot.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.tween.GameObjectAccessor;
import com.unhappyrobot.tween.TweenSystem;
import com.unhappyrobot.utils.Random;

public class Rain extends GameObject {

  public static final int RAIN_TEXURE_SIZE = 128;

  public Rain(Vector2 worldSize) {
    super();

    Texture rainDropTexture = new Texture(Gdx.files.internal("rain-drop.png"));
    rainDropTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    setTexture(rainDropTexture);

    float width = worldSize.x + (RAIN_TEXURE_SIZE * 2);
    float height = worldSize.y + (RAIN_TEXURE_SIZE * 2);
    setPosition(-RAIN_TEXURE_SIZE, -RAIN_TEXURE_SIZE);
    setSize(width, height);
    setRegion(0, 0, width / RAIN_TEXURE_SIZE, height / RAIN_TEXURE_SIZE);

    setOpacity(Math.max(Random.randomFloat(), 0.5f));

    Tween.to(this, GameObjectAccessor.OPACITY, Random.randomInt(1000, 3000))
            .target(1f)
            .repeatYoyo(Tween.INFINITY, 500)
            .start(TweenSystem.getTweenManager());

    Tween.to(this, GameObjectAccessor.TEXTURE_VV2, Random.randomInt(8000, 10000))
            .target(-getV2(), 0f)
            .repeat(Tween.INFINITY, 0)
            .addCallback(TweenCallback.EventType.BEGIN, new TweenCallback() {
              public void onEvent(EventType eventType, BaseTween source) {
                setPosition(Random.randomInt(-RAIN_TEXURE_SIZE / 2, RAIN_TEXURE_SIZE / 2), 0);
              }
            })
            .start(TweenSystem.getTweenManager());
  }
}
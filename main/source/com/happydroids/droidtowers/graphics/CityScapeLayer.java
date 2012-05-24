/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Iterables;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.events.RespondsToWorldSizeChange;
import com.happydroids.droidtowers.platform.Display;

import java.util.Iterator;

public class CityScapeLayer extends GameLayer implements RespondsToWorldSizeChange {
  private final Iterator<TextureAtlas.AtlasRegion> regions;

  public CityScapeLayer() {
    TextureAtlas cityScapeAtlas = TowerAssetManager.textureAtlas("backgrounds/cityscape.txt");
    regions = Iterables.cycle(cityScapeAtlas.getRegions()).iterator();
  }

  @Override
  public void updateWorldSize(Vector2 worldSize) {
    float worldWidth = worldSize.x + (Display.getBiggestScreenDimension() * 2);
    float nextX = width() - Display.getBiggestScreenDimension() - (5f * gameObjects.size());
    while (width() - 64 < worldWidth) {
      GameObject sprite = new GameObject(regions.next());
      sprite.setX(nextX);
      sprite.setY(TowerConsts.GROUND_HEIGHT - 5f);
      addChild(sprite);

      nextX += sprite.getWidth() - 5f;
    }
  }
}

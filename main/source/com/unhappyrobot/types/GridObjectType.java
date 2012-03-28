package com.unhappyrobot.types;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.unhappyrobot.TowerAssetManager;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GridPositionCache;
import com.unhappyrobot.math.Bounds2d;
import com.unhappyrobot.math.GridPoint;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Set;
import java.util.WeakHashMap;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class GridObjectType {
  private String name;
  private int height;
  private int width;
  private int coins;
  private int experienceAward;
  private String atlasFilename;
  private String imageFilename;
  private boolean continuousPlacement;
  private boolean canShareSpace;
  private boolean locked;
  private float noiseLevel;

  @JsonIgnore
  private static WeakHashMap<String, TextureAtlas> atlases;

  public abstract GridObject makeGridObject(GameGrid gameGrid);

  public abstract boolean canBeAt(GridObject gridObject);

  public String getName() {
    return name;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public int getCoins() {
    return coins;
  }

  public int getExperienceAward() {
    return experienceAward;
  }

  public String getImageFilename() {
    return imageFilename;
  }

  public String getAtlasFilename() {
    return atlasFilename;
  }

  public boolean continuousPlacement() {
//    return continuousPlacement;
    return true;
  }

  public int getZIndex() {
    return 0;
  }

  public float getNoiseLevel() {
    return noiseLevel;
  }

  public int getCoinsEarned() {
    return (int) Math.round(coins * 0.0125);
  }

  public boolean canShareSpace(GridObject gridObject) {
    return canShareSpace;
  }

  protected boolean checkIfTouchingAnotherObject(GridObject gridObject) {
    Bounds2d belowObject = new Bounds2d(gridObject.getPosition().cpy().sub(0, 1), gridObject.getSize());

    GridPoint gridPoint = gridObject.getPosition().cpy();
    gridPoint.sub(0, 1);

    Set<GridObject> objectsBelow = GridPositionCache.instance().getObjectsAt(gridPoint, gridObject.getSize(), gridObject);
    return objectsBelow.size() != 0;
  }

  protected boolean checkForOverlap(GridObject gridObject) {
    Set<GridObject> objectsOverlapped = GridPositionCache.instance().getObjectsAt(gridObject.getPosition(), gridObject.getSize(), gridObject);
    for (GridObject object : objectsOverlapped) {
      if (!gridObject.canShareSpace(object)) {
        return false;
      }
    }

    return objectsOverlapped.size() > 0;
  }

  @Override
  public String toString() {
    return "GridObjectType{" +
                   "name='" + name + '\'' +
                   ", height=" + height +
                   ", width=" + width +
                   '}';
  }

  public TextureRegion getTextureRegion() {
    if (atlasFilename != null) {
      if (atlases == null) {
        atlases = new WeakHashMap<String, TextureAtlas>();
      }

      TextureAtlas objectAtlas = atlases.get(atlasFilename);
      if (objectAtlas == null) {
        objectAtlas = TowerAssetManager.textureAtlas(atlasFilename);
        atlases.put(atlasFilename, objectAtlas);
      }

      return objectAtlas.findRegion(imageFilename);
    }

    return null;
  }

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }
}

package com.unhappyrobot.types;

import com.unhappyrobot.entities.GridObject;

public abstract class TransitType extends GridObjectType {
  @Override
  public int getZIndex() {
    return 90;
  }

  public abstract boolean connectsToFloor(GridObject gridObject, float floor);
}
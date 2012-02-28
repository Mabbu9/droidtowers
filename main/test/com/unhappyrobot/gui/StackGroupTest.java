package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.Group;
import org.junit.Before;
import org.junit.Test;

import static com.unhappyrobot.Expect.expect;

public class StackGroupTest {
  private StackGroup stackGroup;

  @Before
  public void setUp() {
    stackGroup = new StackGroup();
    stackGroup.addActor(makeGroup());
    stackGroup.addActor(makeGroup());
  }

  @Test
  public void layout_shouldVerticallyStackChildren() {
    stackGroup.layout();

    expect(stackGroup.getActors().get(0)).toBeAt(0.0f, 0.0f);
    expect(stackGroup.getActors().get(1)).toBeAt(0.0f, 50.0f);
  }

  @Test
  public void layout_whenPaddingSet_shouldWork() {
    stackGroup.pad(10);
    stackGroup.layout();

    expect(stackGroup.getActors().get(0)).toBeAt(10.0f, 10.0f);
    expect(stackGroup.getActors().get(1)).toBeAt(10.0f, 70.0f);
  }

  private Group makeGroup() {
    Group group = new Group();
    group.height = 50;
    group.width = 150;
    return group;
  }
}
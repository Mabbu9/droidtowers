/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.scenes.TowerScene;
import com.happydroids.droidtowers.types.*;

import static com.happydroids.droidtowers.platform.Display.scale;

public class HeadsUpDisplay extends WidgetGroup {
  private TextureAtlas hudAtlas;
  private Skin guiSkin;
  private OrthographicCamera camera;
  private GameGrid gameGrid;
  private Menu addRoomMenu;
  private Label statusLabel;
  private float updateMoneyLabel;
  private static HeadsUpDisplay instance;
  private Toast toast;
  private Menu overlayMenu;
  private Table topBar;
  private ToolTip mouseToolTip;
  private GridObjectPurchaseMenu purchaseDialog;
  private InputCallback closeDialogCallback = null;
  private RadialMenu toolMenu;
  private final StackGroup notificationStack;
  private ImageButton toolButton;
  private ImageButton.ImageButtonStyle toolButtonStyle;
  private TutorialStepNotification tutorialStep;
  private final StatusBarPanel statusBarPanel;

  public HeadsUpDisplay(TowerScene towerScene) {
    instance = this;

    notificationStack = new StackGroup();

    this.stage = towerScene.getStage();
    this.camera = towerScene.getCamera();
    this.gameGrid = towerScene.getGameGrid();
    guiSkin = TowerAssetManager.getGuiSkin();

    hudAtlas = TowerAssetManager.textureAtlas("hud/buttons.txt");

    statusBarPanel = new StatusBarPanel(towerScene);
    statusBarPanel.x = 0;
    statusBarPanel.y = stage.height() - statusBarPanel.height;
    addActor(statusBarPanel);

    mouseToolTip = new ToolTip(guiSkin);
    addActor(mouseToolTip);


    addActor(new ExpandLandOverlay(gameGrid, guiSkin));

    buildToolButtonMenu();

    buildHeaderButtons();

    notificationStack.pad(10);
    notificationStack.x = 0;
    notificationStack.y = 0;
    addActor(notificationStack);

    stage.addActor(this);
  }

  private void buildHeaderButtons() {
    final AudioControl audioControl = new AudioControl(hudAtlas);
    final OverlayControl overlayControl = new OverlayControl(hudAtlas, guiSkin, gameGrid.getRenderer());

    ImageButton achievementsButton = new ImageButton(TowerAssetManager.textureFromAtlas("achievements", "hud/buttons.txt"));
    achievementsButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        new AchievementListView(getStage(), guiSkin).show();
      }
    });

    Table container = new Table(guiSkin);
    container.defaults().center().right().space(5);
    container.clear();
    container.add(achievementsButton);
    container.add(audioControl);
    container.add(overlayControl);
    container.pack();

    container.x = stage.width() - container.width - 5;
    container.y = stage.height() - container.height - 5;
    addActor(container);
  }

  private void buildToolButtonMenu() {
    toolMenu = new RadialMenu();
    toolMenu.arc = 35f;
    toolMenu.radius = scale(180);
    toolMenu.rotation = 3f;

    ImageButton housingButton = new ImageButton(hudAtlas.findRegion("tool-housing"));
    housingButton.setClickListener(makePurchaseButtonClickListener("Housing", RoomTypeFactory.instance()));


    ImageButton transitButton = new ImageButton(hudAtlas.findRegion("tool-transit"));
    transitButton.setClickListener(makePurchaseButtonClickListener("Transit", TransitTypeFactory.instance()));

    ImageButton commerceButton = new ImageButton(hudAtlas.findRegion("tool-commerce"));
    commerceButton.setClickListener(makePurchaseButtonClickListener("Commerce", CommercialTypeFactory.instance()));


    ImageButton servicesButton = new ImageButton(hudAtlas.findRegion("tool-services"));
    servicesButton.setClickListener(makePurchaseButtonClickListener("Services", ServiceRoomTypeFactory.instance()));


    final ImageButton sellButton = new ImageButton(hudAtlas.findRegion("tool-sell"));
    sellButton.setClickListener(new VibrateClickListener() {
      public void onClick(Actor actor, float x, float y) {
        toolMenu.hide();
        toolButton.setStyle(sellButton.getStyle());
        InputSystem.instance().switchTool(GestureTool.SELL, new Runnable() {
          public void run() {
            toolButton.setStyle(toolButtonStyle);
          }
        });
      }
    });

    toolMenu.addActor(housingButton);
    toolMenu.addActor(transitButton);
    toolMenu.addActor(commerceButton);
    toolMenu.addActor(servicesButton);
    toolMenu.addActor(sellButton);

    toolButton = new ImageButton(hudAtlas.findRegion("tool-sprite"));
    toolButton.x = stage.width() - toolButton.width - 5;
    toolButton.y = 5;
    addActor(toolButton);
    toolButtonStyle = toolButton.getStyle();
    toolButton.setClickListener(new VibrateClickListener() {
      public void onClick(Actor actor, float x, float y) {
        if (!toolMenu.visible) {
          stage.addActor(toolMenu);
          toolMenu.x = toolButton.x + 20f;
          toolMenu.y = toolButton.y;
          toolMenu.show();
          TutorialEngine.instance().moveToStepWhenReady("tutorial-unlock-lobby");
        } else {
          toolMenu.hide();
          toolMenu.markToRemove(true);
        }
      }
    });
  }

  private void layoutTopLeftButtons(Table topLeftButtons, TextButton connectFacebookButton, AudioControl audioControl, OverlayControl overlayControl) {
    topLeftButtons.clear();
    topLeftButtons.add(connectFacebookButton);
    topLeftButtons.add(audioControl);
    topLeftButtons.add(overlayControl);
    topLeftButtons.pack();
  }

  private ClickListener makePurchaseButtonClickListener(final String dialogTitle, final GridObjectTypeFactory typeFactory) {
    return new VibrateClickListener() {
      public void onClick(Actor actor, float x, float y) {
        toolMenu.hide();

        if (purchaseDialog == null) {
          if (typeFactory instanceof RoomTypeFactory) {
            TutorialEngine.instance().moveToStepWhenReady("tutorial-unlock-lobby");
          }

          makePurchaseDialog(dialogTitle, typeFactory, ((ImageButton) actor).getStyle());
          toolButton.setStyle(((ImageButton) actor).getStyle());
        } else {
          purchaseDialog.dismiss();
          purchaseDialog = null;
        }
      }
    };
  }

  private void makePurchaseDialog(String title, GridObjectTypeFactory typeFactory, ImageButton.ImageButtonStyle style) {
    purchaseDialog = new GridObjectPurchaseMenu(getStage(), guiSkin, title, typeFactory, new Runnable() {
      public void run() {
        toolButton.setStyle(toolButtonStyle);
      }
    });

    purchaseDialog.setDismissCallback(new Runnable() {
      public void run() {
        purchaseDialog = null;
        if (InputSystem.instance().getCurrentTool() == GestureTool.PICKER) {
          toolButton.setStyle(toolButtonStyle);
        }
      }
    });

    purchaseDialog.show();
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);
  }

  @Override
  public boolean touchMoved(float x, float y) {
    Actor hit = hit(x, y);
    if (hit == null || hit == mouseToolTip) {
      updateGridPointTooltip(x, y);
    } else {
      mouseToolTip.visible = false;
    }

    return super.touchMoved(x, y);
  }

  private void updateGridPointTooltip(float x, float y) {
    if (HappyDroidConsts.DEBUG) {
      Vector3 worldPoint = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY()).getEndPoint(1);

      GridPoint gridPointAtMouse = gameGrid.closestGridPoint(worldPoint.x, worldPoint.y);
      GridPosition gridPosition = gameGrid.positionCache().getPosition(gridPointAtMouse);
      if (gridPosition != null) {
        int totalVisitors = 0;
        for (GridObject gridObject : gridPosition.getObjects()) {
          if (gridObject instanceof CommercialSpace) {
            totalVisitors = ((CommercialSpace) gridObject).getNumVisitors();
          }
        }

        mouseToolTip.visible = true;
        mouseToolTip.setText(String.format("%s\nobjects: %s\nelevator: %s\nstairs: %s\nvisitors: %d", gridPointAtMouse, gridPosition.size(), gridPosition.elevator != null, gridPosition.stair != null, totalVisitors));
        mouseToolTip.x = x + 5;
        mouseToolTip.y = y + 5;
      } else {
        mouseToolTip.visible = false;
      }
    }
  }

  public static void showToast(String message, Object... objects) {
    if (instance.toast == null) {
      instance.toast = new Toast();
      instance.addActor(instance.toast);
    }

    instance.toast.setMessage(String.format(message, objects));
    instance.toast.show();
  }

  public float getPrefWidth() {
    return stage.width();
  }

  public float getPrefHeight() {
    return stage.height();
  }

  public void showTipBubble(GridObject gridObject, String message) {
    SpeechBubble bubble = new SpeechBubble();
    bubble.setText(message);
    bubble.followObject(gridObject);
    bubble.show();
    System.out.println("Bubble: " + message);
    addActor(bubble);
  }

  public static StackGroup getNotificationStack() {
    return instance.notificationStack;
  }

  public static HeadsUpDisplay instance() {
    return instance;
  }

  public static void setTutorialStepNotification(TutorialStepNotification nextStep) {
    if (instance.tutorialStep != null) {
      instance.tutorialStep.markToRemove(true);
    }

    instance.tutorialStep = nextStep;

    if (instance.tutorialStep != null) {
      instance.getStage().addActor(instance.tutorialStep);

      instance.tutorialStep.x = 10;
      instance.tutorialStep.y = ((int) (instance.getStage().height() - (instance.statusBarPanel.height + instance.tutorialStep.height + 6)));
    }
  }
}

package com.unhappyrobot;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.unhappyrobot.gamestate.server.HappyDroidService;
import com.unhappyrobot.platform.AndroidBrowserUtil;
import com.unhappyrobot.platform.AndroidUncaughtExceptionHandler;

public class DroidTowerGame extends AndroidApplication {
  public void onCreate(android.os.Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    HappyDroidService.setDeviceOSName("android");
    HappyDroidService.setDeviceOSVersion("sdk" + getVersion());

    TowerGame towerGame = new TowerGame();
    towerGame.setUncaughtExceptionHandler(new AndroidUncaughtExceptionHandler(this));
    towerGame.setPlatformBrowserUtil(new AndroidBrowserUtil(this));
    initialize(towerGame, true);
    Gdx.input.setCatchBackKey(true);
    Gdx.input.setCatchMenuKey(true);
  }
}

package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.TowerConsts;

public class Device extends HappyDroidServiceObject {
  public String uuid;
  public String type;
  public String osVersion;
  public boolean isAuthenticated;

  public Device() {
    uuid = HappyDroidService.instance().getDeviceId();
    type = HappyDroidService.getDeviceType();
    osVersion = HappyDroidService.getDeviceOSVersion();
  }

  @Override
  protected String getResourceBaseUri() {
    return TowerConsts.HAPPYDROIDS_URI + "/api/v1/register-device/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }
}
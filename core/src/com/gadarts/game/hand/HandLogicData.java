package com.gadarts.game.hand;

import com.gadarts.engine.entities.bullets.BulletDefinitionEntity;
import com.gadarts.game.utils.C;

public class HandLogicData {
    static float gunMoveBy = C.Player.Hand.MOVEMENT_MOVE_BY;
    private boolean ready;
    private boolean holdingTrigger;
    private long lastShoot;
    private boolean loaded;
    private BulletDefinitionEntity currentWeapon;

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean b) {
        ready = b;
    }

    public boolean isHoldingTrigger() {
        return holdingTrigger;
    }

    public void setHoldingTrigger(boolean b) {
        holdingTrigger = b;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean b) {
        loaded = b;
    }

    public BulletDefinitionEntity getCurrentWeapon() {
        return currentWeapon;
    }

    public void setCurrentWeapon(BulletDefinitionEntity bulletDefinition) {
        currentWeapon = bulletDefinition;
    }

    public long getLastShoot() {
        return lastShoot;
    }

    public void setLastShoot(long millis) {
        lastShoot = millis;
    }
}

package com.gadarts.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.engine.input.interfaces.GameInputProcessor;
import com.gadarts.engine.input.interfaces.MouseMovedSubscriber;
import com.gadarts.engine.systems.player.PlayerController;
import com.gadarts.game.hand.Hand;
import com.gadarts.game.hand.HandMovementSequenceFactory;
import com.gadarts.game.utils.C.Player.PlayerMovement;

import java.util.ArrayList;

import static com.gadarts.game.hand.HandMovementSequenceFactory.Movement.JUMPING;
import static com.gadarts.game.hand.HandMovementSequenceFactory.Movement.RUNNING;

public class InputHandler implements GameInputProcessor {
    private final PlayerController pController;
    private final Hand hand;
    private int lastMouseX = -1;
    private int lastMouseY = -1;
    private Vector3 auxVector = new Vector3();
    private ArrayList<MouseMovedSubscriber> mousedMovedSubs = new ArrayList<MouseMovedSubscriber>();

    public InputHandler(PlayerController playerController, Hand hand) {
        this.pController = playerController;
        this.hand = hand;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) Gdx.app.exit();
        runningAccelerate(keycode);
        strafingAccelerate(keycode);
        jump(keycode);
        return true;
    }

    private void jump(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            hand.move(HandMovementSequenceFactory.obtainMovement(JUMPING), false);
            pController.setJumping(true);
        }
    }

    private void strafingAccelerate(int keycode) {
        if (keycode == Input.Keys.A) {
            strafe(PlayerMovement.ACCELERATION);
        } else if (keycode == Input.Keys.D) {
            strafe(-PlayerMovement.ACCELERATION);
        }
    }

    private void strafe(float acceleration) {
        if (!pController.isStrafing() && !pController.isRunning())
            hand.move(HandMovementSequenceFactory.obtainMovement(RUNNING), true);
        pController.setStrafing(true);
        pController.setStrafingAcceleration(acceleration);
    }

    private void runningAccelerate(int keycode) {
        if (keycode == Input.Keys.W) {
            run(PlayerMovement.ACCELERATION);
        } else if (keycode == Input.Keys.S) {
            run(-PlayerMovement.ACCELERATION);
        }
    }

    private void run(float acceleration) {
        if (!pController.isStrafing() && !pController.isRunning()) {
            hand.move(HandMovementSequenceFactory.obtainMovement(RUNNING), true);
        }
        pController.setRunning(true);
        pController.setRunningAcceleration(acceleration);
    }

    @Override
    public boolean keyUp(int keycode) {
        stopRunning(keycode);
        stopStrafing(keycode);
        if (keycode == Input.Keys.SPACE) {
            pController.setJumping(false);
        }
        return true;
    }

    private void stopStrafing(int keycode) {
        float strafingAcceleration = pController.getStrafingAcceleration();
        if ((keycode == Input.Keys.A && strafingAcceleration > 0)
                || (keycode == Input.Keys.D && strafingAcceleration < 0)) {
            if (hand.isReady() && !pController.isRunning()) hand.resetMovement();
            pController.setStrafing(false);
            pController.setStrafingAcceleration(PlayerMovement.DECELERATION);
        }
    }


    private void stopRunning(int keycode) {
        if ((keycode == Input.Keys.W && pController.getRunningAcceleration() > 0)
                || (keycode == Input.Keys.S && pController.getRunningAcceleration() < 0)) {
            if (hand.isReady() && !pController.isStrafing()) hand.resetMovement();
            pController.setRunning(false);
            pController.setRunningAcceleration(PlayerMovement.DECELERATION);
        }
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            hand.holdTriggerToShoot();
            facePlayerDirection(screenX, screenY, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            hand.releaseTrigger();
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        facePlayerDirection(screenX, screenY, false);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        facePlayerDirection(screenX, screenY, true);
        return true;
    }

    private void facePlayerDirection(int screenX, int screenY, boolean informMouseMoved) {
        resetLastMousePositionIfNeeded(screenX, screenY);
        screenX = handleAxisX(screenX, screenY);
        screenY = handleAxisY(screenX, screenY);
        handlePlayerControllerForFacing();
        if (informMouseMoved)
            for (MouseMovedSubscriber sub : mousedMovedSubs)
                sub.mouseMoved(Math.abs(lastMouseX - screenX), Math.abs(lastMouseY - screenY));
        updateLastMousePosition(screenX, screenY);
    }

    private void handlePlayerControllerForFacing() {
        if (pController.getCameraUpZ() < 0) pController.setCameraUpZ(0);
        pController.updateCamera();
        if (pController.isOnGround()) {
            pController.updateRunningDirection();
        }
    }

    private void resetLastMousePositionIfNeeded(int screenX, int screenY) {
        if (lastMouseX == -1) lastMouseX = screenX;
        if (lastMouseY == -1) lastMouseY = screenY;
    }

    private void updateLastMousePosition(int screenX, int screenY) {
        lastMouseX = screenX;
        lastMouseY = screenY;
    }

    private int handleAxisY(int screenX, int screenY) {
        int magY = Math.abs(lastMouseY - screenY);
        screenY = rotateByMagY(screenX, screenY, magY);
        return screenY;
    }

    private int rotateByMagY(int screenX, int screenY, int magY) {
        if (screenY + magY > Gdx.graphics.getHeight()) {
            screenY = 0;
            Gdx.input.setCursorPosition(screenX, screenY);
        } else if (screenY - magY < 0) {
            screenY = Gdx.graphics.getHeight();
            Gdx.input.setCursorPosition(screenX, screenY);
        } else rotateVertical(screenY, magY);
        return screenY;
    }

    private int handleAxisX(int screenX, int screenY) {
        int magX = Math.abs(lastMouseX - screenX);
        screenX = rotateByMagX(screenX, screenY, magX);
        return screenX;
    }

    private int rotateByMagX(int screenX, int screenY, int magX) {
        if (screenX + magX > Gdx.graphics.getWidth()) {
            screenX = 0;
            Gdx.input.setCursorPosition(screenX, screenY);
        } else if (screenX - magX < 0) {
            screenX = Gdx.graphics.getWidth();
            Gdx.input.setCursorPosition(screenX, screenY);
        } else rotateHorizontal(screenX, magX);
        return screenX;
    }

    private void rotateHorizontal(int screenX, int magX) {
        int magSign = lastMouseX > screenX ? 1 : -1;
        magX = magSign * magX;
        pController.rotateCamera(Vector3.Z, magX * PlayerMovement.MOUSE_LOOK_SENSITIVITY);
        hand.panHorizontal(magX);
    }

    private void rotateVertical(int screenY, int magY) {
        float cameraDirectionZ = pController.getCameraDirectionZ();
        if (lastMouseY < screenY && cameraDirectionZ > -PlayerMovement.MAX_VERTICAL_LOOK) {
            rotateUp(magY);
        } else if (lastMouseY > screenY && cameraDirectionZ < PlayerMovement.MAX_VERTICAL_LOOK) {
            rotateDown(magY);
        }
    }

    private void rotateDown(int magY) {
        float cameraDirectionX = pController.getCameraDirectionX();
        float cameraDirectionY = pController.getCameraDirectionY();
        float cameraDirectionZ = pController.getCameraDirectionZ();
        auxVector.set(cameraDirectionX, cameraDirectionY, cameraDirectionZ);
        pController.rotateCamera(auxVector.crs(Vector3.Z), 1 * magY * PlayerMovement.MOUSE_LOOK_SENSITIVITY);
        hand.panVertical(-magY);
    }

    private void rotateUp(int magY) {
        float cameraDirectionX = pController.getCameraDirectionX();
        float cameraDirectionY = pController.getCameraDirectionY();
        float cameraDirectionZ = pController.getCameraDirectionZ();
        auxVector.set(cameraDirectionX, cameraDirectionY, cameraDirectionZ);
        pController.rotateCamera(auxVector.crs(Vector3.Z), -1 * magY * PlayerMovement.MOUSE_LOOK_SENSITIVITY);
        hand.panVertical(magY);
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void subscribeForMouseMoved(MouseMovedSubscriber subscriber) {
        if (!mousedMovedSubs.contains(subscriber)) {
            mousedMovedSubs.add(subscriber);
        }
    }
}

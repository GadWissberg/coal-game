package com.gadarts.game.hand;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.gadarts.engine.World;
import com.gadarts.engine.components.position.PositionEventsSubscriber;
import com.gadarts.engine.systems.player.PlayerController;
import com.gadarts.game.utils.C;
import com.gadarts.game.utils.C.Player;
import com.gadarts.game.utils.C.Player.Hand.RegionNames;
import com.gadarts.game.utils.C.Player.Hand.Rotation;
import com.gadarts.game.utils.C.Resolution;

import static com.gadarts.game.hand.HandMovementSequenceFactory.Movement.JUMPING;
import static com.gadarts.game.hand.HandMovementSequenceFactory.Movement.RUNNING;

public class Hand extends Actor implements PositionEventsSubscriber {

    private final World world;
    private HandTextureData textureHandler = new HandTextureData();
    private HandLogicData handLogicData = new HandLogicData();
    private Vector3 auxVector = new Vector3();
    private Runnable readyAction = new Runnable() {
        @Override
        public void run() {
            handLogicData.setLoaded(true);
            handLogicData.setReady(true);
            textureHandler.setCurrentRegion(textureHandler.getAtlas().findRegion(RegionNames.IDLE));
            if (world.getPlayerController().isRunning()) {
                move(HandMovementSequenceFactory.obtainMovement(RUNNING), true);
            }
        }
    };


    public Hand(World world) {
        this.world = world;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        TextureAtlas.AtlasRegion currentRegion = textureHandler.getCurrentRegion();
        if (currentRegion != null) {
            batch.draw(currentRegion, getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (handLogicData.isReady()) {
            handleShooting(delta);
        }
        handleRotationPanReset(delta);
        if (getY() > 0) setY(0);
    }

    private void handleRotationPanReset(float delta) {
        if (isReady() && !hasActions()) {
            setX(handleRotationAxisPanReset(delta, getX(), Resolution.UI_WORLD_WIDTH / 2 - getWidth() / 2));
            setY(handleRotationAxisPanReset(delta, getY(), Player.Hand.IDLE_Y));
        }
    }

    private float handleRotationAxisPanReset(float delta, float currentCoord, float targetCoord) {
        float rotSpeed = Rotation.RESET_SPEED * delta;
        float result = currentCoord;
        if (currentCoord > targetCoord)
            result = currentCoord > targetCoord + Rotation.MIN_DISTANCE ? currentCoord - rotSpeed : targetCoord;
        else if (currentCoord < targetCoord)
            result = currentCoord < targetCoord - Rotation.MIN_DISTANCE ? currentCoord + rotSpeed : targetCoord;
        return result;
    }

    private void handleShooting(float delta) {
        float shootingAnimationStateTime = textureHandler.getShootingAnimationStateTime();
        if (handLogicData.isHoldingTrigger()) {
            shoot();
            textureHandler.setCurrentRegion(textureHandler.getShootingAnimation().getKeyFrame(shootingAnimationStateTime,
                    false));
        }
        handleShootingAnimation(delta, shootingAnimationStateTime);
        reloadWeapon();
    }

    private void handleShootingAnimation(float delta, float shootingAnimationStateTime) {
        if (textureHandler.getShootingAnimation().isAnimationFinished(shootingAnimationStateTime))
            textureHandler.setCurrentRegion(textureHandler.getAtlas().findRegion(RegionNames.IDLE));
        else textureHandler.setShootingAnimationStateTime(shootingAnimationStateTime + delta);
    }

    private void shoot() {
        if (handLogicData.isLoaded()) {
            textureHandler.setShootingAnimationStateTime(0);
            createBullet();
            handLogicData.setLoaded(false);
            handLogicData.setLastShoot(TimeUtils.millis());
            animateGunRecoil();
        }
    }

    private void animateGunRecoil() {
        addAction(Actions.sequence(
                Actions.moveBy(0, -Player.Hand.SHOOT_MOVE_BY, Player.Hand.SHOOT_RECOIL_DURATION, Interpolation.smooth),
                Actions.moveBy(0, Player.Hand.SHOOT_MOVE_BY, Player.Hand.SHOOT_CALM_DURATION, Interpolation.smooth)));
    }

    private void createBullet() {
        Vector3 direction = this.auxVector;
        PlayerController playerController = world.getPlayerController();
        playerController.getCameraDirection(direction);
        world.createBullet(handLogicData.getCurrentWeapon(), playerController, direction);
        playerController.initiateZoomSequence(1);
    }

    private void reloadWeapon() {
        if (!handLogicData.isLoaded() && TimeUtils.timeSinceMillis(handLogicData.getLastShoot()) >= Player.Hand.GUN_RELOAD_TIME) {
            handLogicData.setLoaded(true);
        }
    }

    public void init(TextureAtlas textureAtlas) {
        textureHandler.setAtlas(textureAtlas);
        handLogicData.setCurrentWeapon(world.getBulletDefinition(C.Bullets.GUN_BULLET));
        Array<TextureAtlas.AtlasRegion> shootingRegions = textureAtlas.findRegions(RegionNames.SHOOT);
        Animation shootingAnimation = new Animation<TextureAtlas.AtlasRegion>(Player.Hand.SHOOTING_FRAME_DURATION, shootingRegions);
        textureHandler.setShootingAnimation(shootingAnimation);
        animateSwitch();
    }

    private void animateSwitch() {
        textureHandler.setCurrentRegion(textureHandler.getAtlas().findRegion(RegionNames.SWITCH));
        TextureAtlas.AtlasRegion currentRegion = textureHandler.getCurrentRegion();
        setSize(currentRegion.getRegionWidth() * 5, currentRegion.getRegionHeight() * 5);
        setPosition(Resolution.UI_WORLD_WIDTH / 3 * 2, -getHeight());
        float targetX = Resolution.UI_WORLD_WIDTH / 2 - getWidth() / 2;
        MoveToAction action = Actions.moveTo(targetX, Player.Hand.IDLE_Y,
                Player.Hand.SWITCH_DURATION, Interpolation.smooth);
        addAction(Actions.sequence(action, Actions.run(readyAction)));
    }

    public boolean isReady() {
        return handLogicData.isReady();
    }

    public void resetMovement() {
        clearActions();
        addAction(getResetMovement());
    }

    private MoveToAction getResetMovement() {
        return Actions.moveTo(Resolution.UI_WORLD_WIDTH / 2 - getWidth() / 2, Player.Hand.IDLE_Y, 0.2f);
    }

    public void holdTriggerToShoot() {
        if (handLogicData.isReady()) {
            handLogicData.setHoldingTrigger(true);
        }
    }

    public void releaseTrigger() {
        handLogicData.setHoldingTrigger(false);
    }

    @Override
    public void onPositionChanged(float x, float y, float z, float delta) {

    }

    @Override
    public void onCollisionWithNonPassableLine() {
    }

    @Override
    public void onLanding(float fallingSpeedOnLanding) {
        if (fallingSpeedOnLanding > 4) {
            move(HandMovementSequenceFactory.obtainMovement(JUMPING), false);
        }
    }

    @Override
    public void onCeilingCollision() {

    }

    public void move(SequenceAction gunMovementSequence, boolean loop) {
        if (isReady()) {
            if (loop) addAction(Actions.forever(gunMovementSequence));
            else addAction(gunMovementSequence);
        }
    }

    public void panHorizontal(int magX) {
        magX /= 2;
        float rightBound = Resolution.UI_WORLD_WIDTH / 2 + Player.Hand.Rotation.MAX_DISTANCE - getWidth() / 2;
        float leftBound = Resolution.UI_WORLD_WIDTH / 2 - (Player.Hand.Rotation.MAX_DISTANCE + getWidth() / 2);
        float destinationX = getX() + magX;
        if (destinationX > rightBound) setX(rightBound);
        else setX(Math.max(destinationX, leftBound));
    }

    public void panVertical(int magY) {
        magY /= 2;
        float upperBound = 5;
        float lowerBound = -getHeight() / 3;
        float destinationY = getY() + magY;
        if (destinationY > upperBound) setY(upperBound);
        else setY(Math.max(destinationY, lowerBound));
    }
}

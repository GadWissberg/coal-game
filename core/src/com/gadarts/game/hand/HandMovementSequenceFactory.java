package com.gadarts.game.hand;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.gadarts.game.utils.C.Player;

import static com.gadarts.game.hand.HandMovementSequenceFactory.Movement.*;

public class HandMovementSequenceFactory {

    public static SequenceAction obtainMovement(Movement movement) {
        if (movement == RUNNING) {
            HandLogicData.gunMoveBy *= -1;
            float abs = Math.abs(HandLogicData.gunMoveBy);
            return Actions.sequence(
                    Actions.moveBy(HandLogicData.gunMoveBy, abs, Player.Hand.MOVEMENT_DURATION, Interpolation.sineOut),
                    Actions.moveBy(-HandLogicData.gunMoveBy, -abs, Player.Hand.MOVEMENT_DURATION),
                    Actions.moveBy(-HandLogicData.gunMoveBy, abs, Player.Hand.MOVEMENT_DURATION),
                    Actions.moveBy(HandLogicData.gunMoveBy, -abs, Player.Hand.MOVEMENT_DURATION, Interpolation.slowFast));
        } else if (movement == JUMPING) {
            return Actions.sequence(
                    Actions.moveBy(0, -Player.Hand.JUMPING_MOVE_BY, Player.Hand.JUMPING_DURATION, Interpolation.smooth),
                    Actions.moveBy(0, Player.Hand.JUMPING_MOVE_BY, Player.Hand.JUMPING_DURATION, Interpolation.smooth));
        } else if (movement == LANDING) {
            return Actions.sequence(
                    Actions.moveBy(0, Player.Hand.LANDING_MOVE_BY, Player.Hand.JUMPING_DURATION, Interpolation.exp5),
                    Actions.moveBy(0, -Player.Hand.LANDING_MOVE_BY, Player.Hand.JUMPING_DURATION, Interpolation.circle));
        }
        return null;
    }

    public enum Movement {RUNNING, LANDING, JUMPING}

}

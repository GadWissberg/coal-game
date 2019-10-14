package com.gadarts.game.utils;

import com.badlogic.gdx.graphics.Color;

import static com.gadarts.game.utils.C.Resolution.UI_WORLD_WIDTH;

public final class C {

    public static final float GRAVITY = 100;

    public static final class Resolution {
        public static final float UI_WORLD_WIDTH = 1920;
        public static final float UI_WORLD_HEIGHT = 1080;
    }

    public static final class Pickups {
        public static final String GUN = "gun";
        public static final float RADIUS = 0.5f;
        public static final Color PICKUP_COLOR = new Color(0.7f, 0.6f, 0.2f, 1);
        public static final float BODY_ALTITUDE = 0.5f;
    }

    public static final class Bullets {
        public static final String GUN_BULLET = "gun_bullet";
        public static final float BULLET_SPEED = 150;
        public static final float RADIUS = 0.2f;
    }

    public static final class Enemies {
        public static final String ENEMY_TEST = "enemy_test";
        public static final float RADIUS = 0.5f;
        public static final float SPEED = 1.5f;
        public static final float FOV = 90;
        public static final int HP = 15;
        public static final float PAIN_CHANCE = 20;
    }

    public static final class Player {
        public static final float HEAD_ALTITUDE = 2;
        public static final float FOV = 67;

        public static final class Hand {
            public static final float SWITCH_DURATION = 0.5f;
            public static final long GUN_RELOAD_TIME = 100;
            public static final float SHOOTING_FRAME_DURATION = 0.05f;
            public static final float MOVEMENT_MOVE_BY = 25;
            public static final float SHOOT_MOVE_BY = 50;
            public static final float JUMPING_MOVE_BY = 100;
            public static final float LANDING_MOVE_BY = 50;
            public static final float MOVEMENT_DURATION = 0.15f;
            public static final float JUMPING_DURATION = 0.2f;
            public static final float SHOOT_CALM_DURATION = 0.05f;
            public static final float SHOOT_RECOIL_DURATION = 0.01f;
            public static final float IDLE_Y = -100;

            public static final class Rotation {

                public static final float RESET_SPEED = 160;
                public static final float MAX_DISTANCE = UI_WORLD_WIDTH / 32;
                public static final float MIN_DISTANCE = 5;
            }

            public static final class RegionNames {

                public static final String IDLE = "idle";
                public static final String SWITCH = "switch";
                public static final String SHOOT = "shoot";
            }
        }

        public static final class PlayerMovement {
            public static final float MAX_SPEED = 7;
            public static final float ACCELERATION = 0.8f;
            public static final float DECELERATION = 0.7f;
            public static final float MAX_VERTICAL_LOOK = 0.9f;
            public static final float MOUSE_LOOK_SENSITIVITY = 0.16f;
            public static final float MOVEMENT_ALTITUDE_SPEED = 0.4f;
            public static final float JUMP = 9;
            public static final float RADIUS = 0.5f;
            public static final float MAX_STEP_ALTITUDE = 0.7f;
            public static final float RAISE_SPEED = 3;
        }
    }
}

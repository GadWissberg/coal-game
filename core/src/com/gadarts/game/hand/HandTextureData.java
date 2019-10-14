package com.gadarts.game.hand;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class HandTextureData {
    private TextureAtlas atlas;
    private TextureAtlas.AtlasRegion currentRegion;
    private Animation<TextureAtlas.AtlasRegion> shootingAnimation;
    private float shootingAnimationStateTime;

    public TextureAtlas.AtlasRegion getCurrentRegion() {
        return currentRegion;
    }

    public void setCurrentRegion(TextureAtlas.AtlasRegion keyFrame) {
        currentRegion = keyFrame;
    }

    public Animation<TextureAtlas.AtlasRegion> getShootingAnimation() {
        return shootingAnimation;
    }

    public void setShootingAnimation(Animation animation) {
        shootingAnimation = animation;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public void setAtlas(TextureAtlas textureAtlas) {
        atlas = textureAtlas;
    }

    public float getShootingAnimationStateTime() {
        return shootingAnimationStateTime;
    }

    public void setShootingAnimationStateTime(float v) {
        shootingAnimationStateTime = v;
    }
}

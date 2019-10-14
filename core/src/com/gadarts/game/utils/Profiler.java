package com.gadarts.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.StringBuilder;

public class Profiler {
    private static final String FPS_STRING = "FPS: ";
    private static final String GL_CALL_STRING = "Calls: ";
    private static final String GL_DRAW_CALL_STRING = "Draw Calls: ";
    private static final String GL_SHADER_SWITCHES_STRING = "Shader Switches: ";
    private static final String GL_TEXTURE_BINDINGS_STRING = "Texture Bindings: ";
    private static final String GL_VERTEX_COUNT_STRING = "Vertex Count: ";
    private static final String BATCH_UI_CALLS_STRING = "UI Batch Calls: ";

    private final Stage stage;
    private GLProfiler glProfiler;
    private StringBuilder stringBuilder;
    private Label label;

    public Profiler(Stage stage) {
        this.stage = stage;
        stringBuilder = new StringBuilder();
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        label = new Label(stringBuilder, style);
        label.setPosition(0, C.Resolution.UI_WORLD_HEIGHT - 70);
        stage.addActor(label);
        setGlProfiler();
    }

    private void setGlProfiler() {
        if (Settings.SHOW_DEBUG_INFO && Settings.SHOW_GL_PROFILING) {
            glProfiler = new GLProfiler(Gdx.graphics);
            glProfiler.enable();
        }
    }

    public void update() {
        if (Settings.SHOW_DEBUG_INFO) {
            stringBuilder.setLength(0);
            displayLine(FPS_STRING, Gdx.graphics.getFramesPerSecond());
            displayGlProfiling();
            displayBatchCalls();
            label.setText(stringBuilder);
        }
    }

    private void displayBatchCalls() {
        if (Settings.SHOW_BATCH_CALLS) {
            displayLine(BATCH_UI_CALLS_STRING, ((SpriteBatch) stage.getBatch()).renderCalls);
        }
    }

    private void displayGlProfiling() {
        if (Settings.SHOW_GL_PROFILING) {
            displayLine(GL_CALL_STRING, glProfiler.getCalls());
            displayLine(GL_DRAW_CALL_STRING, glProfiler.getDrawCalls());
            displayLine(GL_SHADER_SWITCHES_STRING, glProfiler.getShaderSwitches());
            displayLine(GL_TEXTURE_BINDINGS_STRING, glProfiler.getTextureBindings());
            displayLine(GL_VERTEX_COUNT_STRING, glProfiler.getVertexCount().total);
            glProfiler.reset();
        }
    }

    private void displayLine(String label, Object value) {
        stringBuilder.append(label);
        stringBuilder.append(value);
        stringBuilder.append('\n');
    }
}

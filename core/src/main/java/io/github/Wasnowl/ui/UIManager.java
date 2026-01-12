package io.github.Wasnowl.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;

import io.github.Wasnowl.entities.TowerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * UIManager centralise la création et le rendu du HUD et menus.
 * - fourni un InputProcessor (la Stage) pour l'InputMultiplexer
 * - notifie les listeners quand une tour est sélectionnée via le menu
 */
public class UIManager {
    private Stage stage;
    private Skin skin;
    private BitmapFont hudFont;
    private Label balanceLabel;
    private Label costLabel;
    private Window towerWindow;

    private List<Consumer<TowerType>> towerSelectionListeners = new ArrayList<>();

    /**
     * Construit le HUD et le menu de selection des tours.
     */
    public UIManager() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new ScreenViewport());

        hudFont = new BitmapFont(Gdx.files.internal("default.fnt"));
        balanceLabel = new Label("Gold: 0", new Label.LabelStyle(hudFont, Color.WHITE));
        costLabel = new Label("", new Label.LabelStyle(hudFont, Color.YELLOW));

        Table hudTable = new Table();
        hudTable.setFillParent(true);
        hudTable.top().left();
        hudTable.add(balanceLabel).pad(6).row();
        hudTable.add(costLabel).pad(6);
        stage.addActor(hudTable);

        // Bottom-center Towers button
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        TextButton towersButton = new TextButton("Towers", skin);
        table.bottom().add(towersButton).padBottom(8);

        towersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleTowerWindow();
            }
        });
    }

    /**
     * Retourne l'InputProcessor de la UI.
     * @return input processor UI
     */
    public InputProcessor getInputProcessor() {
        return stage;
    }

    /**
     * Ajoute un listener appele lors du choix d'une tour.
     * @param listener listener de selection
     */
    public void addTowerSelectionListener(Consumer<TowerType> listener) {
        towerSelectionListeners.add(listener);
    }

    /**
     * Met a jour l'affichage du solde.
     * @param amount solde a afficher
     */
    public void setBalance(int amount) {
        balanceLabel.setText("Gold: " + amount);
    }

    /**
     * Met a jour le texte de cout (previsualisation).
     * @param text texte a afficher
     */
    public void setCostText(String text) {
        costLabel.setText(text);
    }

    /**
     * Met a jour les animations UI.
     * @param delta temps ecoule (secondes)
     */
    public void act(float delta) {
        stage.act(delta);
    }

    /**
     * Dessine l'UI.
     */
    public void draw() {
        stage.draw();
    }

    /**
     * Met a jour la taille du viewport UI.
     * @param width largeur
     * @param height hauteur
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Libere les ressources de la UI.
     */
    public void dispose() {
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (hudFont != null) hudFont.dispose();
    }

    private void toggleTowerWindow() {
        if (towerWindow != null) {
            towerWindow.remove();
            towerWindow = null;
            return;
        }

        towerWindow = new Window("Choose Tower", skin);
        towerWindow.defaults().pad(6);

        TextButton btnSimple = new TextButton("Simple (" + TowerType.SIMPLE.getCost() + ")", skin);
        TextButton btnAOE = new TextButton("AOE (" + TowerType.AOE.getCost() + ")", skin);
        TextButton btnCleave = new TextButton("Cleave (" + TowerType.RICOCHET.getCost() + ")", skin);
        TextButton btnClose = new TextButton("Close", skin);

        towerWindow.add(btnSimple).row();
        towerWindow.add(btnAOE).row();
        towerWindow.add(btnCleave).row();
        towerWindow.add(btnClose).row();
        towerWindow.pack();
        towerWindow.setPosition(stage.getViewport().getWorldWidth()/2f - towerWindow.getWidth()/2f, 60);

        btnSimple.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                notifyTowerSelected(TowerType.SIMPLE);
                towerWindow.remove();
                towerWindow = null;
            }
        });
        btnSimple.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                costLabel.setText("Cost: " + TowerType.SIMPLE.getCost());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                costLabel.setText("");
            }
        });

        btnAOE.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                notifyTowerSelected(TowerType.AOE);
                towerWindow.remove();
                towerWindow = null;
            }
        });
        btnAOE.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                costLabel.setText("Cost: " + TowerType.AOE.getCost());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                costLabel.setText("");
            }
        });

        btnCleave.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                notifyTowerSelected(TowerType.RICOCHET);
                towerWindow.remove();
                towerWindow = null;
            }
        });
        btnCleave.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                costLabel.setText("Cost: " + TowerType.RICOCHET.getCost());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                costLabel.setText("");
            }
        });

        btnClose.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (towerWindow != null) {
                    towerWindow.remove();
                    towerWindow = null;
                }
            }
        });

        stage.addActor(towerWindow);
    }

    private void notifyTowerSelected(TowerType type) {
        setCostText("Cost: " + type.getCost());
        for (Consumer<TowerType> c : towerSelectionListeners) c.accept(type);
    }
}

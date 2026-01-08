package io.github.Wasnowl.screens;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.Wasnowl.GameMain;
import io.github.Wasnowl.entities.*;
import io.github.Wasnowl.managers.WaveManager;
import io.github.Wasnowl.managers.ProjectileManager;
import io.github.Wasnowl.builders.TowerBuilder;
import io.github.Wasnowl.managers.CurrencyManager;
import io.github.Wasnowl.entities.TowerType;

public class GameScreen extends ScreenAdapter {
    private final GameMain game;
    private String mapPath = "maps/BeginningFields.tmx";


    private OrthographicCamera camera;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    // UI
    private Stage uiStage;
    private Skin uiSkin;
    private Actor towerMenuWindow;
    private boolean showTowerMenu = false;
    private boolean placingPreview = false;
    private TowerType previewTowerType = null;
    private Vector2 previewPosition = new Vector2();
    private ShapeRenderer shapeRenderer;
    private com.badlogic.gdx.InputMultiplexer inputMultiplexer;
    private BitmapFont hudFont;
    private Label balanceLabel;
    private Label costLabel;

    private PlayerTower player;
    private Array<Tower> towers;
    private Array<Enemy> enemies;
    private Array<Projectile> projectiles;
    private ProjectileManager projectileManager;
    private CurrencyManager currencyManager;

    private WaveManager waveManager;


    public GameScreen(GameMain game) {
        this.game = game;
    }

    public GameScreen(GameMain game, String mapPath) {
        this.game = game;
        this.mapPath = mapPath;
    }

    @Override
    public void show() {

        batch = new SpriteBatch();

        // Initialisation du joueur

        // Initialisation des listes (doit précéder la création du joueur/tour)
        towers = new Array<>();
        enemies = new Array<>();
        projectiles = new Array<>();
        projectileManager = new ProjectileManager(projectiles);

        Array<Portal> portals = new Array<>();
        // Exemple : portal large sur la bordure inférieure (y proche de 0)
        portals.add(new Portal(new Rectangle(350, 0, 100, 16), "combat/SideBoss", Portal.Type.COMBAT));
        // Exemple de portal menant à une autre map top-down
        portals.add(new Portal(new Rectangle(750, 550, 50, 50), "maps/NextLevel.tmx", Portal.Type.MAP));

        player = new PlayerTower(100, 100, 150, 1f, enemies, projectiles, portals, game);
        // Currency manager
        currencyManager = new CurrencyManager(300); // monnaie de départ

        // UI stage for tower menu
        uiSkin = new Skin(Gdx.files.internal("uiskin.json"));
        uiStage = new Stage(new ScreenViewport());

        // HUD font and labels
        hudFont = new BitmapFont(Gdx.files.internal("default.fnt"));
        balanceLabel = new Label("Gold: " + currencyManager.getBalance(), new Label.LabelStyle(hudFont, Color.WHITE));
        costLabel = new Label("", new Label.LabelStyle(hudFont, Color.YELLOW));

        // Top-left HUD table
        Table hudTable = new Table();
        hudTable.setFillParent(true);
        hudTable.top().left();
        hudTable.add(balanceLabel).pad(6).row();
        hudTable.add(costLabel).pad(6);
        uiStage.addActor(hudTable);

        // Bottom-center button to open tower menu
        Table table = new Table();
        table.setFillParent(true);
        uiStage.addActor(table);

        TextButton towersButton = new TextButton("Towers", uiSkin);
        table.bottom().add(towersButton).padBottom(8);

        // Tower selection window (created when needed)
        towersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleTowerMenu();
            }
        });

        // ShapeRenderer for preview
        shapeRenderer = new ShapeRenderer();

        // Input multiplexer: UI stage first, then game input adapter
        inputMultiplexer = new com.badlogic.gdx.InputMultiplexer();
        inputMultiplexer.addProcessor(uiStage);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Left click placement
                if (placingPreview && button == com.badlogic.gdx.Input.Buttons.LEFT) {
                    Vector3 tmp = new Vector3(screenX, screenY, 0);
                    viewport.unproject(tmp);
                    float wx = tmp.x;
                    float wy = tmp.y;
                    // simple overlap check: don't place if too close to existing tower
                    boolean overlap = false;
                    for (Tower t : towers) {
                        if (t.getPosition().dst(wx, wy) < 32f) { overlap = true; break; }
                    }
                    if (!overlap && previewTowerType != null && currencyManager.canAfford(previewTowerType.getCost())) {
                        currencyManager.spend(previewTowerType.getCost());
                        balanceLabel.setText("Gold: " + currencyManager.getBalance());
                        Tower placed = new TowerBuilder(wx, wy)
                            .withRange(previewTowerType.getProjectileType().isAOE() ? 180f : 200f)
                            .withFireRate(previewTowerType.getProjectileType().isAOE() ? 1f : 1.5f)
                            .withTowerType(previewTowerType)
                            .withEnemies(enemies)
                            .withProjectiles(projectiles)
                            .build();
                        towers.add(placed);
                    }
                    // exit placement mode
                    placingPreview = false;
                    previewTowerType = null;
                    costLabel.setText("");
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                // Cancel with ESC while placing
                if (placingPreview && keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
                    placingPreview = false;
                    previewTowerType = null;
                    costLabel.setText("");
                    return true;
                }
                return false;
            }
        });

        Gdx.input.setInputProcessor(inputMultiplexer);

        waveManager = new WaveManager();

        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);

        map = new TmxMapLoader().load(mapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        camera.position.set(400, 300, 0);
        camera.update();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleCameraInput(delta);

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Update
        waveManager.update(delta);
        player.update(delta);
        for (Tower t : towers) t.update(delta);
        for (Enemy e : enemies) e.update(delta);
        projectileManager.update(delta);  // Utiliser le ProjectileManager

        // Render
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        for (Tower t : towers) t.render(batch);
        for (Enemy e : enemies) e.render(batch);
        projectileManager.render(batch);  // Utiliser le ProjectileManager
        batch.end();

        // Render preview (using ShapeRenderer)
        if (placingPreview && previewTowerType != null) {
            Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(mouse);
            previewPosition.set(mouse.x, mouse.y);

            batch.begin();
            // optionally draw ghost with lower alpha via shape renderer
            batch.end();

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            Color c = previewTowerType == TowerType.AOE ? new Color(1f, 0.5f, 0.2f, 0.5f) : new Color(0.2f, 0.8f, 0.2f, 0.5f);
            shapeRenderer.setColor(c);
            float w = 32f, h = 32f;
            shapeRenderer.rect(previewPosition.x - w/2f, previewPosition.y - h/2f, w, h);
            shapeRenderer.end();
        }

        // Draw UI on top
        uiStage.act(delta);
        uiStage.draw();
    }

    private void handleCameraInput(float delta) {
        float speed = 200 * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) camera.position.x -= speed;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) camera.position.x += speed;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) camera.position.y += speed;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) camera.position.y -= speed;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        if (uiStage != null) uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        batch.dispose();
        if (uiStage != null) uiStage.dispose();
        if (uiSkin != null) uiSkin.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (hudFont != null) hudFont.dispose();
    }

    private void toggleTowerMenu() {
        if (showTowerMenu) {
            if (towerMenuWindow != null) towerMenuWindow.remove();
            towerMenuWindow = null;
            showTowerMenu = false;
            return;
        }

        // create menu window
        Window window = new Window("Choose Tower", uiSkin);
        window.defaults().pad(6);

        TextButton btnSimple = new TextButton("Simple (" + TowerType.SIMPLE.getCost() + ")", uiSkin);
        TextButton btnAOE = new TextButton("AOE (" + TowerType.AOE.getCost() + ")", uiSkin);
        TextButton btnClose = new TextButton("Close", uiSkin);

        window.add(btnSimple).row();
        window.add(btnAOE).row();
        window.add(btnClose).row();
        window.pack();
        window.setPosition(uiStage.getViewport().getWorldWidth()/2f - window.getWidth()/2f, 60);

        btnSimple.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                previewTowerType = TowerType.SIMPLE;
                placingPreview = true;
                costLabel.setText("Cost: " + previewTowerType.getCost());
                toggleTowerMenu();
            }
        });
        btnSimple.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                costLabel.setText("Cost: " + TowerType.SIMPLE.getCost());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!placingPreview) costLabel.setText("");
            }
        });

        btnAOE.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                previewTowerType = TowerType.AOE;
                placingPreview = true;
                costLabel.setText("Cost: " + previewTowerType.getCost());
                toggleTowerMenu();
            }
        });
        btnAOE.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                costLabel.setText("Cost: " + TowerType.AOE.getCost());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!placingPreview) costLabel.setText("");
            }
        });

        btnClose.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleTowerMenu();
            }
        });

        uiStage.addActor(window);
        towerMenuWindow = window;
        showTowerMenu = true;
    }
}

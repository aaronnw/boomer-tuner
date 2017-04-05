package menu;

import base.CategoryView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import songs.SongsController;
import songs.SongsView;
import utils.Category;
import utils.Icon;
import videos.VideosView;

import java.io.IOException;

/**
 * Created by bryancapps on 4/4/17.
 */
public class MenuView extends BorderPane implements SelectedCategoryListener {
    private MenuModel menuModel;
    private MenuController menuController;
    private Button playlist;
    private ListView<Category> menu;
    private ImageView previous;
    private ImageView play;
    private ImageView next;

    public MenuView(MenuModel model, MenuController controller) throws IOException {
        menuModel = model;
        menuController = controller;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        menuModel.addSelectedCategoryListener(this);

        lookupViews();

        ObservableList<Category> menuList = FXCollections.observableArrayList(Category.Songs, Category.Albums,
                Category.Playlists, Category.Artists, Category.Videos);
        menu.setItems(menuList);

        menu.getSelectionModel().selectedItemProperty().addListener(menuController.getMenuListener());
        menu.getSelectionModel().select(Category.Songs); // does this work? I want it to select in sidebar and show in center

        previous.setImage(Icon.PREVIOUS.image());
        play.setImage(Icon.PLAY.image());
        next.setImage(Icon.NEXT.image());

        previous.setOnMousePressed(e -> menuController.previousPressed());
        next.setOnMousePressed(e -> menuController.nextPressed());
        play.setOnMousePressed(e -> menuController.playPressed());

        playlist.setOnAction(e -> menuController.togglePlaylist());
    }

    private void lookupViews() {
        playlist = (Button) lookup("#playlist");
        menu = (ListView<Category>) lookup("#menu");
        previous = (ImageView) lookup("#previous");
        play = (ImageView) lookup("#play");
        next = (ImageView) lookup("#next");
    }

    public void initializeMenuBar(final Stage stage) {
        final MenuBar menuBar = new MenuBar();
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.setUseSystemMenuBar(true);
        }

        final Menu file = new Menu("File");
        final MenuItem open = new MenuItem("Import Media...");
        open.setOnAction(e -> menuController.chooseDirectory(stage));
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        file.getItems().add(open);
        menuBar.getMenus().add(file);
        setTop(menuBar);
    }

    @Override
    public void selectedCategoryChanged(Category value) {
        CategoryView newView = null;
        switch (value) {
            case Songs:
                newView = new SongsView(new SongsController());
                break;
            case Playlists:
                break;
            case Albums:
                break;
            case Artists:
                break;
            case Videos:
                newView = new VideosView();
                break;
        }
        if (newView != null) {
            newView.setMenuModel(menuModel);
            setCenter((Node) newView);
        }
    }
}
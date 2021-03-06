package songs;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Album;
import models.Artist;
import models.Playlist;
import models.Song;
import root.RootModel;
import utils.CategoryView;
import utils.MediaLibrary;
import utils.Player;

import java.io.IOException;
import java.util.ArrayList;

public class SongsView extends TableView<Song> implements CategoryView {
    private SongsController songsController;
    private TableColumn<Song, Integer> trackCol;
    private TableColumn<Song, String> titleCol;
    private TableColumn<Song, Artist> artistCol;
    private TableColumn<Song, Album> albumCol;
    private RootModel rootModel;
    private ChangeListener<Song> songListener = (ov, oldValue, newValue) -> {
        if (newValue == null) {
            return; // If user selects new directory
        }
        Player.instance().playSongs(getItems(), getSelectionModel().getSelectedIndex());
    };


    public SongsView(final SongsController controller) {
        songsController = controller;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("songs.fxml"));
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        lookupViews();

		setItems(MediaLibrary.instance().getSongs());

        setPlaceholder(new Label("Import media to view songs"));
        trackCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getTrack()));
        titleCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getTitle()));
        artistCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getArtist()));
        albumCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getAlbum()));

		// Subtract 20 total to leave room for the scrollbar
		trackCol.prefWidthProperty().bind(widthProperty().multiply(0.1).subtract(5));
		titleCol.prefWidthProperty().bind(widthProperty().multiply(0.3).subtract(5));
		artistCol.prefWidthProperty().bind(widthProperty().multiply(0.3).subtract(5));
		albumCol.prefWidthProperty().bind(widthProperty().multiply(0.3).subtract(5));

        getSelectionModel().selectedItemProperty().addListener(songListener);
    }

    @SuppressWarnings("unchecked")
    private void lookupViews() {
        trackCol = (TableColumn<Song, Integer>) getVisibleLeafColumn(0);
        titleCol = (TableColumn<Song, String>) getVisibleLeafColumn(1);
        artistCol = (TableColumn<Song, Artist>) getVisibleLeafColumn(2);
        albumCol = (TableColumn<Song, Album>) getVisibleLeafColumn(3);
    }


    public void setRootModel(final RootModel rootModel) {
        this.rootModel = rootModel;
    }

    public void setListeners(final RootModel rootModel) {
		rootModel.setPlaylistModeListener(this::playlistModeChanged);
        rootModel.setSearchListener(this::filterSongs);
    }

    public void playlistModeChanged(final boolean playlistMode) {
        if (playlistMode) {
            getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            getSelectionModel().selectedItemProperty().removeListener(songListener);
        } else {
            ObservableList<Song> selectedCells = getSelectionModel().getSelectedItems();
            if (selectedCells.size() > 0) {
                createPlaylistName(selectedCells);
            }
        }
    }

    public void createPlaylistName(final ObservableList<Song> selectedCells) {
        Scene scene = new Scene(new Pane());

        TextField textField = new TextField ();
        Button createButton = new Button("Create");
        Label label = new Label("Playlist Name: ");

        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setHgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(label, 0, 0);
        grid.add(textField, 1, 0);
        grid.add(createButton, 2, 0);

        Pane root = (Pane)scene.getRoot();
        root.getChildren().add(grid);

        if (rootModel.darkModeProperty().get()) {
            scene.getStylesheets().add("root/darkMode.css");
            grid.getStyleClass().add("background-root");
        }

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Create Your Playlist");
        stage.show();
        stage.setOnCloseRequest(event -> {
            getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            getSelectionModel().selectedItemProperty().addListener(songListener);
        });

        createButton.setOnAction(e -> {
            playlistCreated(textField.getText(), selectedCells, stage);
        });

        textField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                playlistCreated(textField.getText(), selectedCells, stage);
            }
        });
    }

    public void playlistCreated(final String text, final ObservableList<Song> songs, Stage stage) {
        Playlist playlist = MediaLibrary.instance().addPlaylist(text,new ArrayList<>(songs));
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        rootModel.playlistCreated(playlist);
    }

    private void filterSongs(String searchText) {
        getSelectionModel().clearSelection();
        setItems(MediaLibrary.instance().getSongs().filtered(songsController.searchFilter(searchText)));
    }
}

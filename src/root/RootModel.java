package root;

import models.Playlist;
import utils.CategoryType;
import utils.DirectoryListener;

import java.util.ArrayList;
import java.util.List;

public class RootModel {
    private boolean playlistMode = false;
    private boolean directorySelected = false;
    private CategoryType selectedCategory;
	private PlaylistModeListener playlistListener = null;
	private List<DirectoryListener> directoryListeners = new ArrayList<>();
	private List<SelectedCategoryListener> categoryListeners = new ArrayList<>();

    public boolean isPlaylistMode() {
        return playlistMode;
    }

    public void setPlaylistMode(boolean playlistMode) {
        this.playlistMode = playlistMode;
        playlistModeChanged();
    }
    public void setDirectorySelection(boolean selection){
        directorySelected = selection;
        directorySelectionChanged();
    }
    public void addDirectoryListener(DirectoryListener listener){
        directoryListeners.add(listener);
        directorySelectionChanged();
    }
    public boolean isDirectorySelected(){
        return directorySelected;
    }

    public void togglePlaylistMode() {
        setPlaylistMode(!isPlaylistMode());
    }

    public CategoryType getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(CategoryType selectedCategory) {
        this.selectedCategory = selectedCategory;
        categoryChanged();
    }

	public void setPlaylistModeListener(PlaylistModeListener listener) {
		playlistListener = listener;
		playlistListener.playlistModeChanged(playlistMode);
	}

	public void playlistCreated(Playlist playlist) {
        this.selectedCategory = CategoryType.Playlists;
        for (SelectedCategoryListener listener: categoryListeners) {
            listener.playlistCreated(playlist);
        }
    }

    public void addSelectedCategoryListener(SelectedCategoryListener listener) {
        categoryListeners.add(listener);
    }

    private void categoryChanged() {
        for (SelectedCategoryListener listener : categoryListeners) {
            listener.selectedCategoryChanged(selectedCategory);
        }
    }

    private void playlistModeChanged() {
		if (playlistListener != null) {
			playlistListener.playlistModeChanged(playlistMode);
		}
	}
	private  void directorySelectionChanged(){
        for(DirectoryListener listener : directoryListeners){
            listener.directorySet(directorySelected);
        }
    }
}

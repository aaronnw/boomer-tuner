package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import models.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class MediaLibrary {
	private static MediaLibrary instance = new MediaLibrary();

	private ObservableList<Song> songs = FXCollections.observableArrayList();
	private ObservableList<Playlist> playlists = FXCollections.observableArrayList();
	private ObservableList<Album> albums = FXCollections.observableArrayList();
	private ObservableList<Artist> artists = FXCollections.observableArrayList();
	private ObservableList<Video> videos = FXCollections.observableArrayList();

	public static MediaLibrary instance() {
		return instance;
	}

	private MediaLibrary() {
	}

	public void importPath(Path folder) {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws InterruptedException, IOException {
				List<Path> paths = Files.walk(folder, 5).collect(Collectors.toList());
				int size = paths.size();
				for (int i = 0; i < size; i++) {
					Path path = paths.get(i);
					if (Song.accepts(path)) {
						Song song = Song.from(path.toUri());
						if (song != null && !songs.contains(song)) {
							songs.add(song);
							if (!artists.contains(song.getArtist())) {
								artists.add(song.getArtist());
							}
							if (!albums.contains(song.getAlbum())) {
								albums.add(song.getAlbum());
							}
				 		}
					} else if (Video.accepts(path)) {
						Video video = Video.from(path.toUri());
						if (video != null && !videos.contains(video)) {
							videos.add(video);
						}
					}
					updateProgress(i, size);
				}
				return null;
			}
		};
		TaskRunner.run(task, "Importing Media...");
	}

	public Playlist addPlaylist(String name, List<? extends Playable> items) {
		Playlist playlist = new Playlist(name, items);
		playlists.add(playlist);
		return playlist;
	}

	public ObservableList<Song> getSongs() {
		return songs;
	}

	public ObservableList<Playlist> getPlaylists() {
		return playlists;
	}

	public ObservableList<Album> getAlbums() {
		return albums;
	}

	public ObservableList<Artist> getArtists() {
		return artists;
	}

	public ObservableList<Video> getVideos() {
		return videos;
	}
}

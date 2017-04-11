package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Playlist implements Category {
	private String name;
	private List<Song> songs = new ArrayList<>();

	public Playlist(List<Song> songs) {
		this.songs.addAll(songs);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Playlist playlist = (Playlist) o;

		return Objects.equals(name, playlist.name) && Objects.equals(songs, playlist.songs);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + songs.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return name;
	}

	public List<Song> getSongs() {
		return songs;
	}
}

package org.example.musicplayer.service;

import org.example.musicplayer.model.Song;
import org.example.musicplayer.model.SongWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongService {
    private List<Song> songs;

    public SongService() {
        songs = new ArrayList<>();
    }

    public List<File> findMP3Files(File directory) {
        List<File> mp3Files = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
                    mp3Files.add(file);
                }
            }
        }
        return mp3Files;
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public Song findSong(SongWrapper songToFind) {
        for (Song song : songs) {
            if (song.getTitle().equals(songToFind.getTitle())) {
                return song;
            }
        }
        return null;
    }
}

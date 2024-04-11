package org.example.musicplayer;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import org.example.musicplayer.model.Song;
import org.example.musicplayer.model.SongWrapper;
import org.example.musicplayer.service.MediaUtil;
import org.example.musicplayer.service.SongService;

import java.io.File;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    @FXML
    private ImageView statusImageView, previousImageView, nextImageView;
    @FXML
    private Label titleLabel, artistLabel, albumLabel, currentTimeLabel, durationLabel;
    @FXML
    private Button chooseDirectoryButton;
    @FXML
    private ListView<SongWrapper> songsListView;
    @FXML
    private Slider progressSlider;

    private SongService songService;
    private MediaUtil mediaUtil;

    private MediaPlayer mediaPlayer;
    private boolean playStatus;
    private int selectedSongIndex = -1;

    private ChangeListener<Number> sliderListener;

    @FXML
    public void initialize() {
        songService = new SongService();
        mediaUtil = new MediaUtil();
        playStatus = false;

        // Initialize the slider listener
        sliderListener = (observable, oldValue, newValue) -> {
            if (!progressSlider.isValueChanging() && mediaPlayer != null) {
                if (Math.abs(newValue.doubleValue() - mediaPlayer.getCurrentTime().toSeconds()) > 0.5) {
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
            }
        };
    }

    @FXML
    protected void chooseDirectory() {
        songsListView.getItems().clear();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory");
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            List<File> mp3Files;
            mp3Files = songService.findMP3Files(selectedDirectory);
            songsListView.getItems().clear();
            for (File mp3File : mp3Files) {
                Song song = new Song(mp3File);
                songService.addSong(song);
                songsListView.getItems().add(new SongWrapper(song.getTitle(), song.getArtist()));
            }
        }
    }

    @FXML
    protected void getSelection(MouseEvent event) {
        if (event.getClickCount() == 2) {
            SongWrapper selectedItem = songsListView.getSelectionModel().getSelectedItem();
            //todo
            selectedSongIndex = songsListView.getSelectionModel().getSelectedIndex();
            if (selectedItem != null) {
                loadSong(selectedItem);
            }
        }
    }

    protected void loadSong(SongWrapper selectedItem) {
        Song song = songService.findSong(selectedItem);
        titleLabel.setText(song.getTitle());
        artistLabel.setText(song.getArtist());
        albumLabel.setText(song.getAlbum());
        Media media = new Media(song.getFile().toURI().toString());

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // Release resources
        }

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(this::playSong);
    }

    public void bindSlider(Slider slider, MediaPlayer mediaPlayer, Duration totalDuration) {
        // Set maximum value of the slider to the total duration of the media
        slider.setMax(totalDuration.toSeconds());

        // Add listener to update slider value based on media player's current time
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!slider.isValueChanging()) {
                slider.setValue(newValue.toSeconds());
            }
        });

        // Remove previous listeners if they exist
        if (this.sliderListener != null) {
            slider.valueProperty().removeListener(this.sliderListener);
        }

        // Add listeners to update media player's current time based on slider value change
        slider.valueProperty().addListener(this.sliderListener);
    }

    private void playSong() {
        playStatus = true;
        mediaPlayer.play();
        statusImageView.setImage(new Image(new File("src/main/resources/org/example/musicplayer/images/pause_icon.png").toURI().toString()));

        // Format duration and update label
        durationLabel.setText(mediaUtil.formatDuration(mediaPlayer.getTotalDuration()));

        // Update currentTimeLabel periodically
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            currentTimeLabel.setText(mediaUtil.formatDuration(newValue));
        });

        mediaPlayer.setOnEndOfMedia(this::nextSong);

        // Bind progress bar to media time
        bindSlider(progressSlider, mediaPlayer, mediaPlayer.getTotalDuration());
    }

    private void pauseSong() {
        playStatus = false;
        mediaPlayer.pause();
        statusImageView.setImage(new Image(new File("src/main/resources/org/example/musicplayer/images/play_icon.png").toURI().toString()));
    }

    @FXML
    protected void changeSongStatus() {
        if (mediaPlayer != null) {
            if (playStatus) {
                pauseSong();
            } else {
                playSong();
            }
        }
    }

    @FXML
    protected void nextSong() {
        selectedSongIndex++;
        if (selectedSongIndex == songsListView.getItems().size()) {
            selectedSongIndex = 0;
        }
        songsListView.getSelectionModel().select(selectedSongIndex);
        loadSong(songsListView.getSelectionModel().getSelectedItem());
    }

    @FXML
    protected void previousSong() {
        selectedSongIndex--;
        if (selectedSongIndex == -1) {
            selectedSongIndex = songsListView.getItems().size() - 1;
        }
        songsListView.getSelectionModel().select(selectedSongIndex);
        loadSong(songsListView.getSelectionModel().getSelectedItem());
    }
}

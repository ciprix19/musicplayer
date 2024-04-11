package org.example.musicplayer.service;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Slider;
import javafx.util.Duration;

public class MediaUtil {
    public String formatDuration(Duration duration) {
        int totalSeconds = (int) Math.floor(duration.toSeconds());
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

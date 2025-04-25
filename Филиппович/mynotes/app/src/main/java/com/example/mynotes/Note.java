package com.example.mynotes;

public class Note {
    private int id;
    private String description;
    private String imagePath; // Новый путь к изображению
    private String audioPath; // Новый путь к аудио

    public Note(int id, String description, String imagePath, String audioPath) {
        this.id = id;
        this.description = description;
        this.imagePath = imagePath;
        this.audioPath = audioPath;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getAudioPath() {
        return audioPath;
    }
}

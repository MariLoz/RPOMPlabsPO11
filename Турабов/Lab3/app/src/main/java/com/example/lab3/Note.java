package com.example.lab3;

import java.io.Serializable;

public class Note implements Serializable {

    public Note(int _id, String authorName, String noteTitle, String mainTask) {
        this._id = _id;
        this.authorName = authorName;
        this.noteTitle = noteTitle;
        this.mainTask = mainTask;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getMainTask() {
        return mainTask;
    }

    public void setMainTask(String mainTask) {
        this.mainTask = mainTask;
    }

    private int _id;
    private String authorName;
    private String noteTitle;
    private String mainTask;

}

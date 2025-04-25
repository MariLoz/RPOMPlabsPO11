package by.rpomp.lab_1.api;

import androidx.annotation.NonNull;

public class Photo {
    private int id;
    private int sol;
    private Camera camera;
    private String img_src;
    private String earth_date;
    private Rover rover;

    public Photo(int id, String img_src) {
        this.id = id;
        this.img_src = img_src;
    }

    public int getId() { return id; }
    public int getSol() { return sol; }
    public Camera getCamera() { return camera; }
    public String getImgSrc() { return img_src; }
    public String getEarthDate() { return earth_date; }
    public Rover getRover() { return rover; }

    public static class Camera {
        private int id;
        private String name;
        private int rover_id;
        private String full_name;

        public int getId() { return id; }
        public String getFullName() { return full_name; }
    }

    public static class Rover {
        private int id;
        private String name;
        private String landing_date;

        private String launch_date;
        private String status;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getStatus() { return status; }
        public String getLanding_date() { return landing_date; }

        public String getLaunch_date() { return launch_date; }
    }

    @NonNull
    @Override
    public String toString() {
        return "Id: " + id + "\nSol: " + sol + "\nImage_source: " + img_src + "\nEarth date: " + earth_date;
    }
}

package com.eduframepackage.eduframe.model;

public class Video {
    private String id;
    private String title;
    private String description;
    private String lecturer;
    private String duration;
    private String category;
    private String uploadDate;
    private int views;
    private String thumbnailUrl;
    private String videoUrl;

    // Constructors
    public Video() {}

    public Video(String id, String title, String description, String lecturer, String duration, 
                 String category, String uploadDate, int views, String thumbnailUrl, String videoUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lecturer = lecturer;
        this.duration = duration;
        this.category = category;
        this.uploadDate = uploadDate;
        this.views = views;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLecturer() { return lecturer; }
    public void setLecturer(String lecturer) { this.lecturer = lecturer; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUploadDate() { return uploadDate; }
    public void setUploadDate(String uploadDate) { this.uploadDate = uploadDate; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}

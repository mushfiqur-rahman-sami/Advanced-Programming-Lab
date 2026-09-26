package com.studyapp;

public class StudySession {

    private int id;
    private int userId;
    private String subject;
    private int duration;
    private String studyDate;


    public StudySession(
            int id,
            int userId,
            String subject,
            int duration,
            String studyDate) {

        this.id = id;
        this.userId = userId;
        this.subject = subject;
        this.duration = duration;
        this.studyDate = studyDate;
    }


    public int getId() {
        return id;
    }


    public int getUserId() {
        return userId;
    }


    public String getSubject() {
        return subject;
    }


    public int getDuration() {
        return duration;
    }


    public String getStudyDate() {
        return studyDate;
    }


    @Override
    public String toString() {

        return subject +
                "  •  " +
                duration +
                " minutes  •  " +
                studyDate;
    }
}
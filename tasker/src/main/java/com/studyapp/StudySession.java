package com.studyapp;

public class StudySession extends StudyRecord implements Displayable {

    private final int duration;
    private final String studyDate;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public StudySession(
            int id,
            int userId,
            String subject,
            int duration,
            String studyDate) {

        super(id, userId, subject);

        this.duration = duration;
        this.studyDate = studyDate;
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public int getDuration() {
        return duration;
    }


    public String getStudyDate() {
        return studyDate;
    }


    // =========================================================
    // POLYMORPHIC METHOD
    // =========================================================

    @Override
    public String getRecordType() {
        return "Study Session";
    }


    // =========================================================
    // INTERFACE METHOD
    // =========================================================

    @Override
    public String getDisplayText() {

        return getSubject()
                + " | "
                + duration
                + " minutes"
                + " | "
                + studyDate;
    }


    // =========================================================
    // TOSTRING
    // =========================================================

    @Override
    public String toString() {
        return getDisplayText();
    }
}
package com.studyapp;

public class Task {

    private int id;
    private int userId;

    private String title;
    private String subject;
    private String priority;
    private String status;
    private String dueDate;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Task(
            int id,
            int userId,
            String title,
            String subject,
            String priority,
            String status,
            String dueDate) {

        this.id = id;
        this.userId = userId;
        this.title = title;
        this.subject = subject;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
    }


    // Constructor for creating a new task
    public Task(
            int userId,
            String title,
            String subject,
            String priority,
            String dueDate) {

        this.userId = userId;
        this.title = title;
        this.subject = subject;
        this.priority = priority;
        this.status = "Pending";
        this.dueDate = dueDate;
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public int getId() {
        return id;
    }


    public int getUserId() {
        return userId;
    }


    public String getTitle() {
        return title;
    }


    public String getSubject() {
        return subject;
    }


    public String getPriority() {
        return priority;
    }


    public String getStatus() {
        return status;
    }


    public String getDueDate() {
        return dueDate;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setStatus(String status) {
        this.status = status;
    }


    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }


    // =========================================================
    // DISPLAY
    // =========================================================

    @Override
    public String toString() {

        String dateText =
                dueDate == null || dueDate.isEmpty()
                        ? "No due date"
                        : "Due: " + dueDate;

        return title +
                "  |  " +
                subject +
                "  |  " +
                priority +
                "  |  " +
                status +
                "  |  " +
                dateText;
    }
}
package com.studyapp;

public class Task extends StudyRecord implements Displayable {

    private String title;
    private String priority;
    private String status;
    private String dueDate;


    public Task(
            int id,
            int userId,
            String title,
            String subject,
            String priority,
            String status,
            String dueDate) {

        super(id, userId, subject);

        this.title = title;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
    }


    public Task(
            int userId,
            String title,
            String subject,
            String priority,
            String dueDate) {

        super(0, userId, subject);

        this.title = title;
        this.priority = priority;
        this.status = "Pending";
        this.dueDate = dueDate;
    }


    public String getTitle() {
        return title;
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


    public void setStatus(String status) {
        this.status = status;
    }


    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }


    @Override
    public String getRecordType() {
        return "Task";
    }


    @Override
    public String getDisplayText() {

        return title
                + " | "
                + getSubject()
                + " | "
                + priority
                + " | "
                + status
                + " | Due: "
                + dueDate;
    }


    @Override
    public String toString() {
        return getDisplayText();
    }
}


/*
 * Interface
 */
interface Displayable {

    String getDisplayText();
}


/*
 * Abstract parent class
 *
 * StudyRecord now implements Displayable,
 * so DashboardController can use:
 *
 * StudyRecord record
 *
 * and call getDisplayText().
 */
abstract class StudyRecord implements Displayable {

    private final int id;
    private final int userId;
    private final String subject;


    protected StudyRecord(
            int id,
            int userId,
            String subject) {

        this.id = id;
        this.userId = userId;
        this.subject = subject;
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


    public abstract String getRecordType();
}
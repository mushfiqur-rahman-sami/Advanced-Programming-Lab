package com.studyflow;

public record Task(int id, int userId, String title, String subject, String dueDate, String priority, boolean completed) { }

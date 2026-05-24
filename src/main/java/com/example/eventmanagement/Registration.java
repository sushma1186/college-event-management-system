package com.example.eventmanagement;

import jakarta.persistence.*;

@Entity
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentEmail;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public Registration() {}

    public Registration(String studentEmail, Event event) {
        this.studentEmail = studentEmail;
        this.event = event;
    }

    public Long getId() { return id; }
    public String getStudentEmail() { return studentEmail; }
    public Event getEvent() { return event; }
}
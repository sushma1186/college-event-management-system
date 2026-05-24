package com.example.eventmanagement;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String venue;
    private LocalDate date;
    private LocalTime time;
    private double fee;
    private int maxSeats;
    private int availableSeats;
    private String imageUrl;   // ✅ NEW FIELD

    public Event() {}

    public Event(String name, String category, String venue,
                 LocalDate date, LocalTime time,
                 double fee, int maxSeats, String imageUrl) {

        this.name = name;
        this.category = category;
        this.venue = venue;
        this.date = date;
        this.time = time;
        this.fee = fee;
        this.maxSeats = maxSeats;
        this.availableSeats = maxSeats;
        this.imageUrl = imageUrl;
    }
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations;
    public List<Registration> getRegistrations() {
        return registrations;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getVenue() { return venue; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public double getFee() { return fee; }
    public int getMaxSeats() { return maxSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public String getImageUrl() { return imageUrl; }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

}
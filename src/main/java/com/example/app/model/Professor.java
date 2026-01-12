package com.example.app.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Professor implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger ID_GEN = new AtomicInteger(1);

    private final int id;
    private String firstName;
    private String lastName;
    private String email;

    public Professor(String firstName, String lastName, String email) {
        this.id = ID_GEN.getAndIncrement();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() { return firstName + " " + lastName + " (" + id + ")"; }

    public static void setNextId(int next) { if (next < 1) next = 1; ID_GEN.set(next); }
}


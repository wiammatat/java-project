package com.example.app.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger ID_GEN = new AtomicInteger(1);

    private final int id;
    private String code;
    private String name;
    private Integer professorId; // nullable

    public Course(String code, String name) {
        this.id = ID_GEN.getAndIncrement();
        this.code = code;
        this.name = name;
        this.professorId = null;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getProfessorId() { return professorId; }
    public void setProfessorId(Integer professorId) { this.professorId = professorId; }

    @Override
    public String toString() { return code + " - " + name + (professorId!=null?" (Prof:"+professorId+")":""); }

    public static void setNextId(int next) {
        if (next < 1) next = 1;
        ID_GEN.set(next);
    }
}

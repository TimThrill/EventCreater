package com.cong.eventcreater.model;

import java.io.Serializable;

/**
 * Created by admin on 20/07/2016.
 */
public class Person implements Serializable {
    private int id;
    private String fName;
    private String lName;
    private String fullName;

    public Person(int id)
    {
        this(id, null, null, null);
    }

    public Person(int id, String fullName)
    {
        this(id, fullName, null, null);
    }

    public Person(int id, String fullName, String fName, String lName)
    {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.fullName = fullName;
    }

    public String getfName() {
        return fName;
    }

    public String getFullName() {
        return fullName;
    }

    public int getId() {
        return id;
    }

    public String getlName() {
        return lName;
    }
}

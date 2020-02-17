/**************************************************************G*********o****o****g**o****og**joob*********************
 * File: Ticker.java
 * Course materials (19WF) CST 8277
 * @author (original) Mike Norman
 */
package com.algonquincollege.cst8277.assignment1.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tickets")
/**
 *
 * <b>Description</b></br></br>
 * Model class for tickets table for Assignment 1
 *
 * @date 2019 09
 *
 * @author mwnorman
 *
 */
public class Ticket implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    protected int id;
    protected String customerFirstname;
    protected String customerLastname;
    protected String customerEmail;
    protected LocalDateTime tstamp;

    public Ticket() {
        /*
         * do not set in constructor ... wait until Hibernate saves to database
        java.time.OffsetDateTime utcNow = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC);
        tstamp = utcNow.toLocalDateTime();
        */
    }
    
    @Id     // the getter of the primary key is annotated, so Hibernate will use property access
            // H2 will automatically generate the primary key value based on its IDENTITY column-type
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Column(name="first_name")
    public String getCustomerFirstname() {
        return customerFirstname;
    }
    public void setCustomerFirstname(String customerFirstname) {
        this.customerFirstname = customerFirstname;
    }

    @Column(name="last_name")
    public String getCustomerLastname() {
        return customerLastname;
    }
    public void setCustomerLastname(String customerLastname) {
        this.customerLastname = customerLastname;
    }

    @Column(name="email")
    public String getCustomerEmail() {
        return customerEmail;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    @Column(name="tstamp")
    public LocalDateTime getTstamp() {
        return tstamp;
    }
    public void setTstamp(LocalDateTime tstamp) {
        this.tstamp = tstamp;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Ticket [id=%s, customerFirstname=%s, customerLastname=%s, customerEmail=%s]",
            id, customerFirstname, customerLastname, customerEmail);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Ticket other = (Ticket)obj;
        if (id != other.id)
            return false;
        return true;
    }
}
package com.example.book.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="u")
public class Business{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    private String ownerId;

    private String handle;

    private String name;

    private int location;

    private String timezone;

}

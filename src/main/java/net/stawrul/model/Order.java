package net.stawrul.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * Klasa encyjna reprezentująca zamówienie w sklepie.
 */
@Entity
@Table(name = "orders")
@EqualsAndHashCode(of = "id")
public class Order {

    @Getter
    @Id
    UUID id = UUID.randomUUID();

    @Getter
    @ManyToMany(cascade = {MERGE})
    List<Book> books = new ArrayList<>();

    @Getter
    @ManyToMany(cascade = {MERGE})
    List<CD> CDs = new ArrayList<>();

    @Getter
    @ManyToMany(cascade = {MERGE})
    List<Movie> movies = new ArrayList<>();

    @Getter
    @Temporal(TIMESTAMP)
    Date creationDate;

    @Getter
    @Setter
    Double totalPrice;

    /**
     * Ustawienie pola creationDate na aktualny czas w chwili zapisu zamówienia
     * do bazy danych.
     */
    @PrePersist
    public void prePersist(){
        this.creationDate = new Date();
    }
}

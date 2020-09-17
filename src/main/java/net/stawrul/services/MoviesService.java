package net.stawrul.services;

import net.stawrul.model.Book;
import net.stawrul.model.Movie;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Komponent (serwis) biznesowy do realizacji operacji na książkach.
 */
@Service
public class MoviesService extends EntityService<Movie> {

    //Instancja klasy EntityManger zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public MoviesService(EntityManager em) {

        //Book.class - klasa encyjna, na której będą wykonywane operacje
        //Book::getId - metoda klasy encyjnej do pobierania klucza głównego
        super(em, Movie.class, Movie::getId);
    }

    /**
     * Pobranie wszystkich książek z bazy danych.
     *
     * @return lista książek
     */
    public List<Movie> findAll() {
        //pobranie listy wszystkich książek za pomocą zapytania nazwanego (ang. named query)
        //zapytanie jest zdefiniowane w klasie Book
        return em.createNamedQuery(Movie.FIND_ALL, Movie.class).getResultList();
    }

}

package net.stawrul.services;

import net.stawrul.model.Book;
import net.stawrul.model.CD;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Komponent (serwis) biznesowy do realizacji operacji na książkach.
 */
@Service
public class CDService extends EntityService<CD> {

    //Instancja klasy EntityManger zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public CDService(EntityManager em) {

        //Book.class - klasa encyjna, na której będą wykonywane operacje
        //Book::getId - metoda klasy encyjnej do pobierania klucza głównego
        super(em, CD.class, CD::getId);
    }

    /**
     * Pobranie wszystkich książek z bazy danych.
     *
     * @return lista książek
     */
    public List<CD> findAll() {
        //pobranie listy wszystkich książek za pomocą zapytania nazwanego (ang. named query)
        //zapytanie jest zdefiniowane w klasie Book
        return em.createNamedQuery(CD.FIND_ALL, CD.class).getResultList();
    }

}

package net.stawrul.services;

import net.stawrul.model.Book;
import net.stawrul.model.CD;
import net.stawrul.model.Movie;
import net.stawrul.model.Order;
import net.stawrul.services.exceptions.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Komponent (serwis) biznesowy do realizacji operacji na zamówieniach.
 */
@Service
public class OrdersService extends EntityService<Order> {

    //Instancja klasy EntityManger zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public OrdersService(EntityManager em) {

        //Order.class - klasa encyjna, na której będą wykonywane operacje
        //Order::getId - metoda klasy encyjnej do pobierania klucza głównego
        super(em, Order.class, Order::getId);
    }

    /**
     * Pobranie wszystkich zamówień z bazy danych.
     *
     * @return lista zamówień
     */
    public List<Order> findAll() {
        return em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
    }

    /**
     * Złożenie zamówienia w sklepie.
     * <p>
     * Zamówienie jest akceptowane, jeśli wszystkie objęte nim produkty są dostępne (przynajmniej 1 sztuka). W wyniku
     * złożenia zamówienia liczba dostępnych sztuk produktów jest zmniejszana o jeden. Metoda działa w sposób
     * transakcyjny - zamówienie jest albo akceptowane w całości albo odrzucane w całości. W razie braku produktu
     * wyrzucany jest wyjątek OutOfStockException.
     *
     * @param order zamówienie do przetworzenia
     */
    @Transactional
    public void placeOrder(Order order) {
        if(order.getCDs().size() != 0 && order.getMovies().size() != 0){
            throw new MoviesAndCDsException();
        }
        if(order.getCDs().size() == 0 && order.getBooks().size() == 0 && order.getMovies().size() == 0)
            throw new EmptyOrderException();

        List<Book> TMPBooks = new ArrayList<Book>();
        for (Book bookStub : order.getBooks()) {
            Book book = em.find(Book.class, bookStub.getId());

            if(book == null){
                String msg = "We don't have " + bookStub.getId().toString() + " in our store.";
                throw new NotExistException(msg);
            }
            for(Book check : TMPBooks){
                if( book.getId() == check.getId() ){
                    throw new ReduplicationException();
                }
            }
            TMPBooks.add(book);
            if (book.getAmount() < 1 || bookStub.getAmount() > book.getAmount()) {
                //wyjątek z hierarchii RuntineException powoduje wycofanie transakcji (rollback)
                String msg = book.getId().toString() + "is out of stock";
                throw new OutOfStockException(msg);
            }
        }

        List<CD> TMPCDs = new ArrayList<CD>();
        for (CD cdShelf : order.getCDs()) {
            CD cd = em.find(CD.class, cdShelf.getId());

            if(cd == null){
                String msg = "We don't have " + cdShelf.getId().toString() + " in our store.";
                throw new NotExistException(msg);
            }
            for(CD check : TMPCDs){
                if( cd.getId() == check.getId() ){
                    throw new ReduplicationException();
                }
            }
            TMPCDs.add(cd);
            if (cd.getAmount() < 1 || cdShelf.getAmount() > cd.getAmount()) {
                String msg = cd.getId().toString() + "is out of stock";
                throw new OutOfStockException(msg);
            }
        }

        List<Movie> TMPMovies = new ArrayList<Movie>();
        for (Movie movieCatalog : order.getMovies()) {
            Movie movie = em.find(Movie.class, movieCatalog.getId());

            if(movie == null){
                String msg = "We don't have " + movieCatalog.getId().toString() + " in our store.";
                throw new NotExistException(msg);
            }
            for(Movie check : TMPMovies){
                if( movie.getId() == check.getId() ){
                    throw new ReduplicationException();
                }
            }
            TMPMovies.add(movie);
            if (movie.getAmount() < 1 || movieCatalog.getAmount() > movie.getAmount()) {
                String msg = movie.getId().toString() + "is out of stock";
                throw new OutOfStockException(msg);
            }
        }

        Double totalPriceNum = 0.0;
        // WSZYSTKO OK MOZNA ZMIENIC AMOUNT I ZAPISAC ZAMOWIENIE
        for(Book bookStub : order.getBooks()){
            Book book = em.find(Book.class, bookStub.getId());
            totalPriceNum += bookStub.getAmount() * book.getPrice();
            int newAmount = book.getAmount() - bookStub.getAmount();
            book.setAmount(newAmount);
        }
        for(CD cdShelf : order.getCDs()){
            CD cd = em.find(CD.class, cdShelf.getId());
            totalPriceNum += cdShelf.getAmount() * cd.getPrice();
            int newAmount = cd.getAmount() - cdShelf.getAmount();
            cd.setAmount(newAmount);
        }
        for(Movie movieCatalog : order.getMovies()){
            Movie movie = em.find(Movie.class, movieCatalog.getId());
            totalPriceNum += movieCatalog.getAmount() * movie.getPrice();
            int newAmount = movie.getAmount() - movieCatalog.getAmount();
            movie.setAmount(newAmount);
        }
        order.setTotalPrice(totalPriceNum);
        save(order);
    }
}

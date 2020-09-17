package net.stawrul.controllers;

import net.stawrul.model.Book;
import net.stawrul.model.CD;
import net.stawrul.model.Movie;
import net.stawrul.model.Order;
import net.stawrul.services.BooksService;
import net.stawrul.services.CDService;
import net.stawrul.services.MoviesService;
import net.stawrul.services.OrdersService;
import net.stawrul.services.exceptions.OutOfStockException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

/**
 * Kontroler obejmujący akcje na zamówieniach.
 */
@RestController
public class SearchController {

    final OrdersService ordersService;
    final BooksService booksService;
    final MoviesService moviesService;
    final CDService cdsService;
    public SearchController(OrdersService ordersService, MoviesService moviesService, CDService cdsService, BooksService booksService) {
        this.ordersService = ordersService;
        this.booksService = booksService;
        this.cdsService = cdsService;
        this.moviesService = moviesService;
    }


    @PostMapping("/SearchPrice")
    public List<Object> findProducts(@RequestBody Order order, UriComponentsBuilder uriBuilder) {
        List<Object> searchResults = new ArrayList<Object>();

        List<Book> AllBooks = new ArrayList<Book>();
        AllBooks.addAll(booksService.findAll());
        List<CD> AllCDs = new ArrayList<CD>();
        AllCDs.addAll(cdsService.findAll());
        List<Movie> AllMovies = new ArrayList<Movie>();
        AllMovies.addAll(moviesService.findAll());

        double bookPrice = 0;
        for (Book bookStub : order.getBooks()) {
            bookPrice = bookStub.getPrice();
        }
        for (Book bookStub : AllBooks) {
            if(bookStub.getPrice() != null) {
                if (bookStub.getPrice() <= bookPrice) {
                    searchResults.add(bookStub);
                }
            }
        }
        double cdPrice = 0;
        for (CD bookStub : order.getCDs()) {
            cdPrice = bookStub.getPrice();
        }
        for (CD bookStub : AllCDs) {
            if(bookStub.getPrice() != null) {
                if (bookStub.getPrice() <= cdPrice) {
                    searchResults.add(bookStub);
                }
            }
        }
        double moviePrice = 0;
        for (Movie bookStub : order.getMovies()) {
            moviePrice = bookStub.getPrice();
        }
        for (Movie bookStub : AllMovies) {
            if(bookStub.getPrice() != null) {
                if (bookStub.getPrice() <= moviePrice) {
                    searchResults.add(bookStub);
                }
            }
        }

        return searchResults;
    }
}

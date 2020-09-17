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
public class SearchNameController {

    final OrdersService ordersService;
    final BooksService booksService;
    final MoviesService moviesService;
    final CDService cdsService;
    public SearchNameController(OrdersService ordersService, MoviesService moviesService, CDService cdsService, BooksService booksService) {
        this.ordersService = ordersService;
        this.booksService = booksService;
        this.cdsService = cdsService;
        this.moviesService = moviesService;
    }


    @PostMapping("/SearchName")
    public List<Object> findProducts(@RequestBody Order order, UriComponentsBuilder uriBuilder) {
        List<Object> searchResults = new ArrayList<Object>();

        List<Book> AllBooks = new ArrayList<Book>();
        AllBooks.addAll(booksService.findAll());
        List<CD> AllCDs = new ArrayList<CD>();
        AllCDs.addAll(cdsService.findAll());
        List<Movie> AllMovies = new ArrayList<Movie>();
        AllMovies.addAll(moviesService.findAll());

        String bookTitle = "";
        for (Book bookStub : order.getBooks()) {
            bookTitle = bookStub.getTitle();
        }
        for (Book bookStub : AllBooks) {
            if(bookStub.getTitle() != null) {
                if (bookStub.getTitle().toLowerCase().indexOf(bookTitle.toLowerCase()) != -1) {
                    searchResults.add(bookStub);
                }
            }
        }
        String CDTitle = "";
        for (CD bookStub : order.getCDs()) {
            CDTitle = bookStub.getTitle();
        }
        for (CD bookStub : AllCDs) {
            if(bookStub.getTitle() != null) {
                if (bookStub.getTitle().toLowerCase().indexOf(CDTitle.toLowerCase()) != -1) {
                    searchResults.add(bookStub);
                }
            }
        }
        String movieTitle = "";
        for (Movie bookStub : order.getMovies()) {
            movieTitle = bookStub.getTitle();
        }
        for (Movie bookStub : AllMovies) {
            if(bookStub.getTitle() != null) {
                if (bookStub.getTitle().toLowerCase().indexOf(movieTitle.toLowerCase()) != -1) {
                    searchResults.add(bookStub);
                }
            }
        }

        return searchResults;
    }
}

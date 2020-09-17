package net.stawrul.controllers;

import net.stawrul.model.Book;
import net.stawrul.model.Movie;
import net.stawrul.services.BooksService;
import net.stawrul.services.MoviesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;


/**
 * Kontroler zawierający akcje związane z książkami w sklepie.
 *
 * Parametr "/Movies" w adnotacji @RequestMapping określa prefix dla adresów wszystkich akcji kontrolera.
 */
@RestController
@RequestMapping("/Movies")
public class MoviesController {

    //Komponent realizujący logikę biznesową operacji na książkach
    final MoviesService moviesService;

    //Instancja klasy BooksService zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public MoviesController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    /**
     * Pobieranie listy wszystkich książek.
     *
     * Żądanie:
     * GET /books
     *
     * @return lista książek
     */
    @GetMapping
    public List<Movie> listMovies() {
        return moviesService.findAll();
    }

    /**
     * Dodawanie nowej książki.
     *
     * Żądanie:
     * POST /Movies
     *
     * @param movie obiekt zawierający dane nowej książki, zostanie zbudowany na podstawie danych
     *             przesłanych w ciele żądania (automatyczne mapowanie z formatu JSON na obiekt
     *             klasy Book)
     * @param uriBuilder pomocniczy obiekt do budowania adresu wskazującego na nowo dodaną książkę,
     *                   zostanie wstrzyknięty przez framework Spring
     *
     * @return odpowiedź HTTP dla klienta
     */
    @PostMapping
    public ResponseEntity<Void> addMovie(@RequestBody Movie movie, UriComponentsBuilder uriBuilder) {

        if (moviesService.find(movie.getId()) == null) {
            //Identyfikator nie istnieje w bazie danych - nowa książka zostaje zapisana
            moviesService.save(movie);

            //Jeśli zapisywanie się powiodło zwracana jest odpowiedź 201 Created z nagłówkiem Location, który zawiera
            //adres nowo dodanej książki
            URI location = uriBuilder.path("/Movies/{id}").buildAndExpand(movie.getId()).toUri();
            return ResponseEntity.created(location).build();

        } else {
            //Identyfikator książki już istnieje w bazie danych. Żądanie POST służy do dodawania nowych elementów,
            //więc zwracana jest odpowiedź z kodem błędu 409 Conflict
            return ResponseEntity.status(CONFLICT).build();
        }
    }

    /**
     * Pobieranie informacji o pojedynczej książce.
     *
     * Żądanie:
     * GET /Movie/{id}
     *
     * @param id identyfikator książki
     *
     * @return odpowiedź 200 zawierająca dane książki lub odpowiedź 404, jeśli książka o podanym identyfikatorze nie
     * istnieje w bazie danych
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable UUID id) {
        //wyszukanie książki w bazie danych
        Movie movie = moviesService.find(id);

        //W warstwie biznesowej brak książki o podanym id jest sygnalizowany wartością null. Jeśli książka nie została
        //znaleziona zwracana jest odpowiedź 404 Not Found. W przeciwnym razie klient otrzymuje odpowiedź 200 OK
        //zawierającą dane książki w domyślnym formacie JSON
        return movie != null ? ResponseEntity.ok(movie) : ResponseEntity.notFound().build();
    }

    /**
     * Aktualizacja danych książki.
     *
     * Żądanie:
     * PUT /Movies/{id}
     *
     * @param movie
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMovie(@RequestBody Movie movie) {
        if (moviesService.find(movie.getId()) != null) {
            //aktualizacja danych jest możliwa o ile książka o podanym id istnieje w bazie danych
            moviesService.save(movie);
            return ResponseEntity.ok().build();

        } else {
            //nie odnaleziono książki o podanym id - odpowiedź 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

}

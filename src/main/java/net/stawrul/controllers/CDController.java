package net.stawrul.controllers;

import net.stawrul.model.Book;
import net.stawrul.model.CD;
import net.stawrul.services.BooksService;
import net.stawrul.services.CDService;
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
 * Parametr "/books" w adnotacji @RequestMapping określa prefix dla adresów wszystkich akcji kontrolera.
 */
@RestController
@RequestMapping("/CDs")
public class CDController {

    //Komponent realizujący logikę biznesową operacji na książkach
    final CDService CDsService;

    //Instancja klasy BooksService zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public CDController(CDService CDsService) {
        this.CDsService = CDsService;
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
    public List<CD> listCDs() {
        return CDsService.findAll();
    }

    /**
     * Dodawanie nowej książki.
     *
     * Żądanie:
     * POST /books
     *
     * @param cd obiekt zawierający dane nowej książki, zostanie zbudowany na podstawie danych
     *             przesłanych w ciele żądania (automatyczne mapowanie z formatu JSON na obiekt
     *             klasy Book)
     * @param uriBuilder pomocniczy obiekt do budowania adresu wskazującego na nowo dodaną książkę,
     *                   zostanie wstrzyknięty przez framework Spring
     *
     * @return odpowiedź HTTP dla klienta
     */
    @PostMapping
    public ResponseEntity<Void> addMovie(@RequestBody CD cd, UriComponentsBuilder uriBuilder) {

        if (CDsService.find(cd.getId()) == null) {
            //Identyfikator nie istnieje w bazie danych - nowa książka zostaje zapisana
            CDsService.save(cd);

            //Jeśli zapisywanie się powiodło zwracana jest odpowiedź 201 Created z nagłówkiem Location, który zawiera
            //adres nowo dodanej książki
            URI location = uriBuilder.path("/CDs/{id}").buildAndExpand(cd.getId()).toUri();
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
     * GET /CDs/{id}
     *
     * @param id identyfikator książki
     *
     * @return odpowiedź 200 zawierająca dane książki lub odpowiedź 404, jeśli książka o podanym identyfikatorze nie
     * istnieje w bazie danych
     */
    @GetMapping("/{id}")
    public ResponseEntity<CD> getBook(@PathVariable UUID id) {
        //wyszukanie książki w bazie danych
        CD cd = CDsService.find(id);

        //W warstwie biznesowej brak książki o podanym id jest sygnalizowany wartością null. Jeśli książka nie została
        //znaleziona zwracana jest odpowiedź 404 Not Found. W przeciwnym razie klient otrzymuje odpowiedź 200 OK
        //zawierającą dane książki w domyślnym formacie JSON
        return cd != null ? ResponseEntity.ok(cd) : ResponseEntity.notFound().build();
    }

    /**
     * Aktualizacja danych książki.
     *
     * Żądanie:
     * PUT /books/{id}
     *
     * @param cd
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCD(@RequestBody CD cd) {
        if (CDsService.find(cd.getId()) != null) {
            //aktualizacja danych jest możliwa o ile książka o podanym id istnieje w bazie danych
            CDsService.save(cd);
            return ResponseEntity.ok().build();

        } else {
            //nie odnaleziono książki o podanym id - odpowiedź 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

}

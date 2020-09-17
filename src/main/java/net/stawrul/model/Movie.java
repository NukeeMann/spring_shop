package net.stawrul.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.UUID;

/**
 * Klasa encyjna reprezentująca towar w sklepie (Film).
 */
@Entity
@EqualsAndHashCode(of = "id")
@NamedQueries(value = {
        @NamedQuery(name = Movie.FIND_ALL, query = "SELECT b FROM Movie b")
})
public class Movie {
    public static final String FIND_ALL = "Movie.FIND_ALL";

    @Getter
    @Id
    UUID id = UUID.randomUUID();

    @Getter
    @Setter
    String title;

    @Getter
    @Setter
    Integer amount;

    @Getter
    @Setter
    String director;

    @Getter
    @Setter
    String people;

    @Getter
    @Setter
    Integer durationInMinutes;

    @Getter
    @Setter
    Double price;
}

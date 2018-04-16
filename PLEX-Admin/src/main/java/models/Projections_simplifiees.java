package models;

import java.util.Calendar;
import java.util.List;

public class Projections_simplifiees {
    private long id;

    public long getId() {
        return id;
    }

    public Calendar getDateHeure() {
        return dateHeure;
    }

    public Films_simplifies getFilm() {
        return film;
    }

    private Calendar dateHeure;
    private Films_simplifies film;


    public Projections_simplifiees(Projection projection){
        film = new Films_simplifies(projection.getFilm());
        id = projection.getId();
        dateHeure = projection.getDateHeure();
    }
}

package models;

public class Films_simplifies {
    private Acteurs_simplifies Acteurs;

    public Acteurs_simplifies getActeurs() {
        return Acteurs;
    }

    public String getTitre() {
        return Titre;
    }

    private String Titre;
    public Films_simplifies(Film film){
        Acteurs = new Acteurs_simplifies(film);
        Titre = film.getTitre();
    }
}

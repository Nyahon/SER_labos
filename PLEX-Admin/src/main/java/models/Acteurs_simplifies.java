package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Acteurs_simplifies {
    public String getRole1() {
        return Role1;
    }

    public String getRole2() {
        return Role2;
    }

    private String Role1;
    private String Role2;

    public Acteurs_simplifies(Film film){
        List<RoleActeur> listRoles = new ArrayList<RoleActeur>(film.getRoles());
        Role1 = listRoles.get(0).getActeur().getNom();
        Role2 = listRoles.get(1).getActeur().getNom();

    }
}

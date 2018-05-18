import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import org.jdom2.*;
import org.jdom2.filter.Filters;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class main {
    public static void main(String[] args) {
        String xmlFile = "proj.XML";
        Document document = XMLGen.getDOMParsedDocument(xmlFile);

        Element rootNode = document.getRootElement();
        System.out.println("Root Element: " + rootNode.getName());
        XPathFactory xpfac = XPathFactory.instance();
        XPathExpression<Element> projections = xpfac.compile("//Projection", Filters.element());
        Element plex = new Element("plex");
        // Creation du document XML avec Projections comme racine
        DocType dtype = new DocType(plex.getName());
        dtype.setSystemID("projections.dtd")    ;

        Document plex_doc = new Document(plex, dtype);

        // On défini la feuille xslt
        Map<String, String> map = new HashMap<String, String>();
        map.put("href", "projections.xsl");
        map.put("type", "text/xsl");
        plex_doc.addContent(0, new ProcessingInstruction("xml-stylesheet", map));


        Element projectionsElement = new Element("projections");
        Element filmsElement = new Element("films");
        Element acteursElement = new Element("acteurs");
        Element listeLangagesElement = new Element("liste_langages");
        Element listeGenresElement = new Element("liste_genres");
        Element listeMotsClesElement = new Element("liste_mots_cles");

        for (Element e : projections.evaluate(document))
        {
            // ==============================================================
            // ===================== PROJECTIONS ============================
            // ==============================================================


            // ========================= PROJECTION =========================
            Element projectionElement = new Element("projection");
            projectionElement.setAttribute("film_id", "F" +  e.getChild("Film").getAttribute("Id").getValue());
            projectionElement.setAttribute("titre", e.getChild("Film").getChildText("titre"));

            // ===== SALLE ======
            Element salleElement = new Element("salle");
            salleElement.setAttribute("taille", e.getChild("Salle").getChildText("taille"));
            salleElement.setText(e.getChild("Salle").getChildText("numero"));
            projectionElement.addContent(salleElement);

            // ===== DATEHEURE =====
            Element dateHeureElement = new Element("date_heure");
            dateHeureElement.setAttribute("format", "dd.MM.YYYY HH:mm");
            String formatDate = e.getChild("Date").getChildText("jour") + ".";
            formatDate += e.getChild("Date").getChildText("mois") + ".";
            formatDate += e.getChild("Date").getChildText("annee").substring(2);
            formatDate += " 00:00";
            dateHeureElement.setText(formatDate);
            projectionElement.addContent(dateHeureElement);

            projectionsElement.addContent(projectionElement);


            // ====================== FILM =================================
            Element filmElement = new Element("film");
            filmElement.setAttribute("no", "F" + e.getChild("Film").getAttribute("Id").getValue());
            // => TITRE
            Element titreElement = new Element("titre");
            titreElement.setText(e.getChild("Film").getChildText("titre"));
            filmElement.addContent(titreElement);
            // => DUREE
            Element dureeElement = new Element("duree");
            dureeElement.setAttribute("format", "minutes");
            dureeElement.setText(e.getChild("Film").getChildText("duree"));
            filmElement.addContent(dureeElement);
            // => SYNOPSIS
            Element synopsisElement = new Element("synopsys");
            synopsisElement.setText("<![CDATA[" + e.getChild("Film").getChildText("synopsis") + "]]>");
            filmElement.addContent(synopsisElement);
            // => PHOTO
            if(e.getChild("Film").getChild("photo") != null){
            Element photoElement = new Element("photo");
                photoElement.setAttribute("url", e.getChild("Film").getChild("photo").getAttributeValue("url"));
                filmElement.addContent(photoElement);
            }
            // => CRITIQUES
            Element critiquesElement = new Element("critiques");
            for(Element cri : e.getChild("Film").getChildren("Critique")){
                Element critiqueElement = new Element("critique");
                critiqueElement.setAttribute("note", cri.getAttributeValue("Note"));
                critiqueElement.setText(cri.getText());
                critiquesElement.addContent(critiqueElement);
            }
            if(critiquesElement.getChildren() != null){
                filmElement.addContent(critiquesElement);
            }
            // => LANGAGES
            Element langagesElement = new Element("langages");
            String listeLangages = "";
            for(Element lan : e.getChild("Film").getChildren("Langage")){
                listeLangages += "L" + lan.getAttributeValue("Id") + " ";
            }
            langagesElement.setAttribute("liste", listeLangages);
            filmElement.addContent(langagesElement);

            // => GENRES
            Element genresElement = new Element("genres");
            String listeGenres = "";
            for(Element gen : e.getChild("Film").getChildren("Genre")){
                listeGenres += "G" + gen.getAttributeValue("Id") + " ";
            }
            genresElement.setAttribute("liste", listeGenres);
            filmElement.addContent(genresElement);

            // => MOTS CLES
            Element motsclesElement = new Element("mots_cles");
            String listeMotsCles = "";
            for(Element mot : e.getChild("Film").getChildren("Motcle")){
                listeMotsCles += "M" + mot.getAttributeValue("Id") + " ";
            }
            motsclesElement.setAttribute("liste", listeMotsCles);
            filmElement.addContent(motsclesElement);

            // => ROLES
            Element rolesElement = new Element("roles");
            for(Element rol : e.getChild("Film").getChildren("Role")){
                Element roleElement = new Element("role");
                roleElement.setAttribute("place", rol.getChildText("place"));
                roleElement.setAttribute("personnage", rol.getChildText("personnage"));
                roleElement.setAttribute("acteur_id", "A"+ rol.getChild("Acteur").getAttributeValue
                        ("Id").substring(6, rol.getChild("Acteur").getAttributeValue("Id").length()));
                rolesElement.addContent(roleElement);
            }
            filmElement.addContent(rolesElement);

            filmsElement.addContent(filmElement);


            // =================================== ACTEURS ==================================
            for (Element rol : e.getChild("Film").getChildren("Role")){
                Element act = rol.getChild("Acteur");
                Element acteurElement = new Element("acteur");
                acteurElement.setAttribute("no", "A"+ act.getAttributeValue("Id").substring
                        (6, act.getAttributeValue("Id").length()));
                // => NOM
                Element nomActeurElement = new Element("nom");
                nomActeurElement.setText(act.getChildText("nom"));
                acteurElement.addContent(nomActeurElement);

                // => NOM NAISSANCE
                Element nomNaissanceActeurElement = new Element("nom_naissance");
                if(act.getChild("nomNaissance") != null){
                    nomNaissanceActeurElement.setText(act.getChildText("nomNaissance"));
                }else{
                    nomNaissanceActeurElement.setText("n/a");
                }
                acteurElement.addContent(nomNaissanceActeurElement);
                // => SEXE
                Element sexeActeurElement = new Element("sexe");
                sexeActeurElement.setAttribute("valeur", act.getAttributeValue("sexe"));
                acteurElement.addContent(sexeActeurElement);

                // => DATE NAISSANCE
                Element dateNaissanceActeur = new Element("date_naissance");
                dateNaissanceActeur.setAttribute("format", "dd.mm.yyyy");
                if(act.getChild("dateNaissance") != null) {

                    String[] date = act.getChildText("dateNaissance").split("/");
                    dateNaissanceActeur.setText(date[0] + "." + date[1] + "." + date[2]);
                }
                acteurElement.addContent(dateNaissanceActeur);

                // => DATE DECES
                Element dateDecesActeur = new Element("date_deces");
                dateDecesActeur.setAttribute("format", "dd.mm.yyyy");
                if(act.getChild("dateDeces") != null){

                    String[] date2 = act.getChildText("dateDeces").split("/");
                    dateDecesActeur.setText(date2[0] + "." + date2[1] + "." + date2[2]);
                }
                acteurElement.addContent(dateDecesActeur);

                // => BIOGRAPHIE
                Element biographieActeurElement = new Element("biographie");
                if(act.getChild("biographie") != null){
                    biographieActeurElement.setText("<![CDATA[" + act.getChildText("biographie") + "]]>");
                }
                acteurElement.addContent(biographieActeurElement);

                acteursElement.addContent(acteurElement);

            }
            // ======================= LISTE LANGAGES ==================================

            for(Element lan : e.getChild("Film").getChildren("Langage")){
                Element langageElement = new Element("langage");
                langageElement.setAttribute("no", "L" + lan.getAttributeValue("Id"));
                langageElement.setText(lan.getChildText("Label"));
                listeLangagesElement.addContent(langageElement);
            }

            // ======================== LISTE GENRES ==================================
            for(Element gen : e.getChild("Film").getChildren("Genre")){
                Element genreElement = new Element("genre");
                genreElement.setAttribute("no", "G" + gen.getAttributeValue("Id"));
                genreElement.setText(gen.getChildText("Label"));
                listeGenresElement.addContent(genreElement);
            }

            // ========================= LISTE MOTS CLES ============================
            for(Element mot : e.getChild("Film").getChildren("Motcle")){
                Element motCleElement = new Element("mot_cle");
                motCleElement.setAttribute("no", "M" + mot.getAttributeValue("Id"));
                motCleElement.setText(mot.getChildText("label"));
                listeMotsClesElement.addContent(motCleElement);
                System.out.println(motCleElement.getText());
                System.out.println(motCleElement.getAttributeValue("no"));
            }


        }

        plex.addContent(projectionsElement);
        plex.addContent(filmsElement);
        plex.addContent(acteursElement);
        plex.addContent(listeLangagesElement);
        plex.addContent(listeGenresElement);
        plex.addContent(listeMotsClesElement);

        XMLOutputter xmlOutput = new XMLOutputter();


        xmlOutput.setFormat(Format.getPrettyFormat());
        // Ecriture dans un fichier
        try{
            xmlOutput.output(plex_doc, new FileWriter("./projections.xml"));
        }catch(IOException e){
            System.out.println(e.getMessage());
        }


//Read employee first names
        /*
        XPathExpression<Element> xPathN = xpfac.compile("//employees/employee/firstName", Filters.element());

        for (Element element : xPathN.evaluate(document))
        {
            System.out.println("Employee First Name :: " + element.getValue());
        }
*/
    }
}

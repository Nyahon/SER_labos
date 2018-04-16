package controllers;

import ch.heigvd.iict.ser.imdb.models.Role;
import models.*;
import views.*;

import java.awt.image.CropImageFilter;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import com.thoughtworks.xstream.XStream;

public class ControleurXMLCreation {

	//private ControleurGeneral ctrGeneral;
	private static MainGUI mainGUI;
	private ORMAccess ormAccess;

	private GlobalData globalData;

	public ControleurXMLCreation(ControleurGeneral ctrGeneral, MainGUI mainGUI, ORMAccess ormAccess){
		//this.ctrGeneral=ctrGeneral;
		ControleurXMLCreation.mainGUI=mainGUI;
		this.ormAccess=ormAccess;
	}

	public void createXML(){
		new Thread(){
				public void run(){
					mainGUI.setAcknoledgeMessage("Creation XML... WAIT");
					long currentTime = System.currentTimeMillis();
					try {
						globalData = ormAccess.GET_GLOBAL_DATA();

						/*
						PrintWriter file = new PrintWriter("Projections.xml");
						file.print("<?xml version=\"1.0\"?>");
						file.println("<Projections>");
						file.println("</Projections>");
						file.close();
						*/
						//int projNumber = 0;
						//int roleNumber = 0;

						mainGUI.setWarningMessage("Creation XML: Commencement...");

						List<Projection> projections = globalData.getProjections();
						System.out.println("Nombre de projections: " + projections.size());
						Element projectionsElements = new Element("Projections");
						Document plex_doc = new Document(projectionsElements);
						//plex_doc.setRootElement(projectionsElements);

						for(Projection projection : projections){ // pour chaque projection
							//++projNumber;

							Element projectionElement = new Element("Projection");
							projectionElement.setAttribute("Id", "projection" + String.valueOf(projection.getId()));

							// ---------------------- FILM -------------------------------
							Film film = projection.getFilm();
							Element filmElement = new Element("Film");
							//Ajout attirbut ID au film
							filmElement.setAttribute("Id", String.valueOf(film.getId()));
							//Ajout champ titre au film
							filmElement.addContent(new Element("titre").setText(film.getTitre()));
							//Ajout champ durée au film
							filmElement.addContent(new Element("duree").setText(String.valueOf(film.getDuree())));
							//Ajout champ photo
							if(film.getPhoto()!=null)
								filmElement.addContent(new Element("photo").setAttribute("url", film.getPhoto()));
							//Ajout champ synopsis
							filmElement.addContent(new Element("synopsis").setText(film.getSynopsis()));

							// ?????????????????? ROLES ???????????????????????
							Set<RoleActeur> roles = film.getRoles();

							for(RoleActeur role : roles){
								//++roleNumber;
								Element roleElement = new Element("Role");
								roleElement.setAttribute("Id", String.valueOf(role.getId()));

								// ???????????????????? ACTEUR ????????????????????????
								Acteur acteur = role.getActeur();
								Element acteurElement = new Element("Acteur");
								acteurElement.setAttribute("Id", "acteur"+String.valueOf(acteur.getId()));
								acteurElement.setAttribute("sexe", String.valueOf(acteur.getSexe()));
								acteurElement.addContent(new Element("nom").setText(acteur.getNom()));
								if(acteur.getDateDeces() != null)
									acteurElement.addContent(new Element("dateDeces").setText(String.valueOf(acteur.getDateDeces())));
								if(acteur.getDateNaissance()!=null)
									acteurElement.addContent(new Element("dateNaissance").setText(String.valueOf(acteur.getDateNaissance())));
								if(acteur.getNomNaissance()!=null)
									acteurElement.addContent(new Element("nomNaissance").setText(acteur.getNomNaissance()));
								if(acteur.getBiographie()!=null)
									acteurElement.addContent(new Element("biographie").setText(acteur.getBiographie()));

								roleElement.addContent(acteurElement);
								roleElement.addContent(new Element("place").setText(String.valueOf(role.getPlace())));
								roleElement.addContent(new Element("personnage").setText(role.getPersonnage()));

								filmElement.addContent(roleElement);

							}

							// ???????????????????? MOTSCLES ????????????????????????
							Set<Motcle> motscles = film.getMotcles();

							for(Motcle motcle : motscles){
								Element motcleElement = new Element("Motcle");
								motcleElement.setAttribute("Id", String.valueOf(motcle.getId()));
								motcleElement.addContent(new Element("label").setText(motcle.getLabel()));

								filmElement.addContent(motcleElement);
							}


							// ????????????????????? Langage ??????????????????????????

							Set<Langage> langages = film.getLangages();

							for(Langage langage : langages){
								Element langageElement = new Element("Langage");
								langageElement.setAttribute("Id", String.valueOf(langage.getId()));
								langageElement.addContent(new Element("Label").setText(langage.getLabel()));

								filmElement.addContent(langageElement);
							}

							// ??????????????????? Critique ???????????????????????

							Set<Critique> critiques = film.getCritiques();

							if(critiques.size()!=0){
								for(Critique critique : critiques){
									Element critiqueElement = new Element("Critique");
									critiqueElement.setAttribute("Id", String.valueOf(critique.getId()));
									critiqueElement.setAttribute("Note", String.valueOf(critique.getNote()));
									critiqueElement.addContent(new Element("texte").setText(critique.getTexte()));

									filmElement.addContent(critiqueElement);
								}
							}



							// ???????????????????? GENRE ?????????????????????????

							Set<Genre> genres = film.getGenres();

							for(Genre genre : genres){
								Element genreElement = new Element("Genre");
								genreElement.setAttribute("Id", String.valueOf(genre.getId()));
								genreElement.addContent(new Element("Label").setText(genre.getLabel()));

								filmElement.addContent(genreElement);

							}

							projectionElement.addContent(filmElement);

							// ----------------------- SALLE ------------------------------

							Salle salle = projection.getSalle();
							Element salleElement = new Element("Salle");
							salleElement.setAttribute("Id", String.valueOf(salle.getId()));
							salleElement.addContent(new Element("numero").setText(salle.getNo()));
							salleElement.addContent(new Element("taille").setText(String.valueOf(salle.getTaille())));

							projectionElement.addContent(salleElement);



							// ----------------------- DATE -------------------------------

							Calendar dateHeure = projection.getDateHeure();
							Element dateElement = new Element("Date");


							dateElement.addContent(new Element("jour").setText(String.valueOf(dateHeure.get(Calendar.DAY_OF_MONTH))));
							dateElement.addContent(new Element("mois").setText(String.valueOf(dateHeure.get(Calendar.MONTH))));
							dateElement.addContent(new Element("annee").setText(String.valueOf(dateHeure.get(Calendar.YEAR))));

							projectionElement.addContent(dateElement);

							projectionsElements.addContent(projectionElement);
						}
						XMLOutputter xmlOutput = new XMLOutputter();


						xmlOutput.setFormat(Format.getPrettyFormat());
						xmlOutput.output(plex_doc, new FileWriter("/home/johanna/Documents/Cours/S4/SER/Labos/Projections.XML"));



						//mainGUI.setWarningMessage("Creation XML: Fonction non encore implementee");
					}
					catch (Exception e){
						mainGUI.setErrorMessage("Construction XML impossible", e.toString());
					}
				}
		}.start();
	}

	public void createXStreamXML(){
		new Thread(){
				public void run(){
					mainGUI.setAcknoledgeMessage("Creation XML... WAIT");
					long currentTime = System.currentTimeMillis();
					try {
						globalData = ormAccess.GET_GLOBAL_DATA();
						globalDataControle();
					}
					catch (Exception e){
						mainGUI.setErrorMessage("Construction XML impossible", e.toString());
					}

					XStream xstream = new XStream();
					writeToFile("global_data.xml", xstream, globalData);
					System.out.println("Done [" + displaySeconds(currentTime, System.currentTimeMillis()) + "]");
					mainGUI.setAcknoledgeMessage("XML cree en "+ displaySeconds(currentTime, System.currentTimeMillis()) );
				}
		}.start();
	}

	private static void writeToFile(String filename, XStream serializer, Object data) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
			serializer.toXML(data, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final DecimalFormat doubleFormat = new DecimalFormat("#.#");
	private static final String displaySeconds(long start, long end) {
		long diff = Math.abs(end - start);
		double seconds = ((double) diff) / 1000.0;
		return doubleFormat.format(seconds) + " s";
	}

	private void globalDataControle(){
		for (Projection p:globalData.getProjections()){
			System.out.println("******************************************");
			System.out.println(p.getFilm().getTitre());
			System.out.println(p.getSalle().getNo());
			System.out.println("Acteurs *********");
			for(RoleActeur role : p.getFilm().getRoles()) {
				System.out.println(role.getActeur().getNom());
			}
			System.out.println("Genres *********");
			for(Genre genre : p.getFilm().getGenres()) {
				System.out.println(genre.getLabel());
			}
			System.out.println("Mot-cles *********");
			for(Motcle motcle : p.getFilm().getMotcles()) {
				System.out.println(motcle.getLabel());
			}
			System.out.println("Langages *********");
			for(Langage langage : p.getFilm().getLangages()) {
				System.out.println(langage.getLabel());
			}
			System.out.println("Critiques *********");
			for(Critique critique : p.getFilm().getCritiques()) {
				System.out.println(critique.getNote());
				System.out.println(critique.getTexte());
			}
		}
	}
}




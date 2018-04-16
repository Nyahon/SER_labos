package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.*;
import views.*;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class ControleurMedia {

	private ControleurGeneral ctrGeneral;
	private static MainGUI mainGUI;
	private ORMAccess ormAccess;
	
	private GlobalData globalData;

	public ControleurMedia(ControleurGeneral ctrGeneral, MainGUI mainGUI, ORMAccess ormAccess){
		this.ctrGeneral=ctrGeneral;
		ControleurMedia.mainGUI=mainGUI;
		this.ormAccess=ormAccess;
	}

	public void sendJSONToMedia(){
		new Thread(){
			public void run(){
				mainGUI.setAcknoledgeMessage("Envoi JSON ... WAIT");
				//long currentTime = System.currentTimeMillis();
				try {
					globalData = ormAccess.GET_GLOBAL_DATA();
					// Creation d'un objet Gson formatee
					Gson gson = new GsonBuilder().setPrettyPrinting().create();

					// Creation d'une liste de projections simplifiees vide
					List<Projections_simplifiees> projections = new ArrayList<>();
					// Recuperation des projections completes
					List<Projection> projectionsCompletes = globalData.getProjections();
					for(Projection projectionComplete : projectionsCompletes){ // Parcours des projections completes
						// Creation d'une projections simplifiee a partir de la projection complete
						Projections_simplifiees projectionSimplifiee = new Projections_simplifiees(projectionComplete);
						// Ajout de la projection simplifiee a la liste de projections simplifiees
						projections.add(projectionSimplifiee);
					}

					Writer writer = new FileWriter("./Projections.json");

					try{
						// Ecriture dans un fichier
						gson.toJson(projections, writer);
						writer.flush();
					}catch(Exception e ){
						System.out.println(e.getMessage());
					}

					mainGUI.setAcknoledgeMessage("Envoi JSON: DONE");
				}
				catch (Exception e){
					mainGUI.setErrorMessage("Construction JSON impossible", e.toString());
				}
			}
		}.start();
	}

}
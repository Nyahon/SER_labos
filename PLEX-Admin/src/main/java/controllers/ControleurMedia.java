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
					Gson gson = new GsonBuilder().setPrettyPrinting().create();

					List<Projections_simplifiees> projections = new ArrayList<>();
					List<Projection> proj = globalData.getProjections();
					for(Projection p : proj){
						Projections_simplifiees project = new Projections_simplifiees(p);
						System.out.println(project.getFilm().getActeurs().getRole1());
						projections.add(project);
					}

					System.out.println(projections.size());
					Writer writer = new FileWriter("./Projections.json");

					try{
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
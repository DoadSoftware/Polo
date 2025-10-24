package com.polo.broadcaster;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.opencsv.exceptions.CsvException;
import com.polo.containers.Scene;
import com.polo.containers.ScoreBug;
import com.polo.model.*;
import com.polo.service.PoloService;
import com.polo.util.PoloFunctions;
import com.polo.util.PoloUtil;

import net.sf.json.JSONArray;

public class POLO extends Scene{
	
	public String session_selected_broadcaster = "POLO";
	
	public PrintWriter print_writer;
	public ScoreBug scorebug = new ScoreBug(); 
	public String which_graphics_onscreen = "";
	public boolean is_infobar = false;
	public String flag_scorebug_path = "IMAGE*/Default/Essentials/Flags_ScoreBug/";
	public String flag_path = "IMAGE*/Default/Essentials/Flags/";
	public String base_path = "IMAGE*/Default/Essentials/Base/";
	public String text_path = "IMAGE*/Default/Essentials/Text/";
	private String status;
	private String slashOrDash = "-";
	public static List<String> penalties;
	public static List<String> penaltiesremove;
	
	
	public POLO() {
		super();
	}
	
	public ScoreBug updateScoreBug(List<Scene> scenes, Match match, Socket session_socket) throws InterruptedException, MalformedURLException, IOException, CsvException
	{
		if(scorebug.isScorebug_on_screen() == true) {
			scorebug = populateScoreBug(true,scorebug, session_socket, scenes.get(0).getScene_path(),match, session_selected_broadcaster);
		}
		return scorebug;
	}
	public Object ProcessGraphicOption(String whatToProcess,Match match,Clock clock, PoloService hockeyService,Socket session_socket,
			List<Scene> scenes, String valueToProcess) throws InterruptedException, NumberFormatException, MalformedURLException, IOException, CsvException, JAXBException{
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		
		if (which_graphics_onscreen == "PENALTY")
		{
			int iHomeCont = 0, iAwayCont = 0;
			penalties.add(valueToProcess.split(",")[1]);
			/*if(((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses())%5) == 0 && ((match.getAwayPenaltiesHits()+match.getAwayPenaltiesMisses())%5) == 0) {
				System.out.println("hello");
				penalties = new ArrayList<String>();
			}*/
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomePenaltiesHits() + "-" + match.getAwayPenaltiesHits() + ";");
			for(String pen : penalties)
			{	
				if(pen.toUpperCase().contains(PoloUtil.HOME + "_" + PoloUtil.INCREMENT + "_" + "PENALTIES" + "_" + PoloUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(PoloUtil.HOME + "_" + PoloUtil.INCREMENT + "_" + "PENALTIES" + "_" + PoloUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(PoloUtil.AWAY + "_" + PoloUtil.INCREMENT + "_" + "PENALTIES" + "_" + PoloUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(PoloUtil.AWAY + "_" + PoloUtil.INCREMENT + "_" + "PENALTIES" + "_" + PoloUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(PoloUtil.HOME + "_" + PoloUtil.DECREMENT + "_" + "PENALTIES" + "_" + PoloUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");
					
//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penaltiesremove = new ArrayList<String>();
//					penalties.remove(penalties.size() - 1);
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(PoloUtil.HOME + "_" + PoloUtil.DECREMENT + "_" + "PENALTIES" + "_" + PoloUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");

					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(PoloUtil.AWAY + "_" + PoloUtil.DECREMENT + "_" + "PENALTIES" + "_" + PoloUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");

					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(PoloUtil.AWAY + "_" + PoloUtil.DECREMENT + "_" + "PENALTIES" + "_" + PoloUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");

					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
			if(match.getHomePenaltiesHits() == 0 && match.getAwayPenaltiesHits() == 0) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
		} else {
			if(penalties == null) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
			if(match.getHomePenaltiesHits() == 0 && match.getAwayPenaltiesHits() == 0) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
			int iHomeCont = 0, iAwayCont = 0;
			penalties.add(valueToProcess.split(",")[1]);
			if(((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses())%5) == 0 && ((match.getAwayPenaltiesHits()+match.getAwayPenaltiesMisses())%5) == 0) {
				if(match.getHomePenaltiesHits() == match.getAwayPenaltiesHits()) {
					penalties = new ArrayList<String>();
				}
			}
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomePenaltiesHits() + "-" + match.getAwayPenaltiesHits() + ";");

			for(String pen : penalties)
			{
				//System.out.println("ELSE LOOP - " + iHomeCont);
				if(pen.toUpperCase().contains(PoloUtil.HOME + "_" + PoloUtil.INCREMENT + "_" + "PENALTIES" + "_" + PoloUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(PoloUtil.HOME + "_" + PoloUtil.INCREMENT + "_" + "PENALTIES" + "_" + PoloUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(PoloUtil.AWAY + "_" + PoloUtil.INCREMENT + "_" + "PENALTIES" + "_" + PoloUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(PoloUtil.AWAY + "_" + PoloUtil.INCREMENT + "_" + "PENALTIES" + "_" + PoloUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(PoloUtil.HOME + "_" + PoloUtil.DECREMENT + "_" + "PENALTIES" + "_" + PoloUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");

//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
					
				}else if(pen.toUpperCase().contains(PoloUtil.HOME + "_" + PoloUtil.DECREMENT + "_" + "PENALTIES" + "_" + PoloUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");

//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(PoloUtil.AWAY + "_" + PoloUtil.DECREMENT + "_" + "PENALTIES" + "_" + PoloUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");
			
//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(PoloUtil.AWAY + "_" + PoloUtil.DECREMENT + "_" + "PENALTIES" + "_" + PoloUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");
					
//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
		}
		
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-SCOREBUG": case "POPULATE-SCOREBUG_WITHTIME": case "POPULATE-L3-BUG-FREETEXT": case "POPULATE-L3-BUG-DB": case "POPULATE-L3-SCOREUPDATE":
			
			switch (whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG": case "POPULATE-SCOREBUG_WITHTIME":
				populateScoreBug(false, scorebug, session_socket, "", match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-BUG-DB":
				for(Bugs bug : hockeyService.getBugs()) {
					  if(bug.getBugId() == Integer.valueOf(valueToProcess.split(",")[1])) {
						  populateBugsDB(session_socket, PoloUtil.POLO_OVERLAYS, bug, match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-L3-BUG-FREETEXT":
				populateBugsFreeText(session_socket, PoloUtil.POLO_OVERLAYS, valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-SCOREUPDATE":
				populateScoreUpdate(session_socket, PoloUtil.POLO_OVERLAYS, hockeyService,match,clock, session_selected_broadcaster);
				break;
			}
			
		case "NAMESUPER_GRAPHICS-OPTIONS": 
			return JSONArray.fromObject(hockeyService.getNameSupers()).toString();
		case "BUG_DB_GRAPHICS-OPTIONS":
			return JSONArray.fromObject(hockeyService.getBugs()).toString();
		case "STAFF_GRAPHICS-OPTIONS":
			return JSONArray.fromObject(hockeyService.getStaffs()).toString();
		case "PROMO_GRAPHICS-OPTIONS":
			return JSONArray.fromObject(PoloFunctions.processAllFixtures(hockeyService)).toString();
			
		case "ANIMATE-IN-SCOREBUG": case "ANIMATE-IN-SCOREBUG_WITHTIME":
		case "CLEAR-ALL": case "ANIMATE-OUT": 
		case "ANIMATE-IN-BUG-DB": case "ANIMATE-IN-SCOREUPDATE": case "ANIMATE-IN-BUG-FREETEXT":
			
			switch (whatToProcess.toUpperCase()) {
			case "ANIMATE-IN-BUG-DB":
				processAnimation(session_socket, "anim_Bug$In_Out", "START", session_selected_broadcaster,1);
				which_graphics_onscreen = "BUG-DB";
				break;
			case "ANIMATE-IN-BUG-FREETEXT":
				processAnimation(session_socket, "anim_Bug$In_Out", "START", session_selected_broadcaster,1);
				which_graphics_onscreen = "BUG-FREETEXT";
				break;
			case "ANIMATE-IN-SCOREUPDATE":
				processAnimation(session_socket, "anim__LT_Score", "START", session_selected_broadcaster,1);
				which_graphics_onscreen = "SCOREUPDATE";
				break;
			case "ANIMATE-IN-SCOREBUG": case "ANIMATE-IN-SCOREBUG_WITHTIME":
				processAnimation(session_socket, "anim_ScoreBug$EventLogo", "START", session_selected_broadcaster,1);
				processAnimation(session_socket, "anim_ScoreBug$In_Out$Essentials", "START", session_selected_broadcaster,1);
				
				if(whatToProcess.equalsIgnoreCase("ANIMATE-IN-SCOREBUG_WITHTIME")) {
					processAnimation(session_socket, "anim_ScoreBug$In_Out$Time", "START", session_selected_broadcaster,1);
				}
				is_infobar = true;
				scorebug.setScorebug_on_screen(true);
				break;
			case "CLEAR-ALL":
				processAnimation(session_socket, "anim_ScoreBug", "SHOW 0.0", session_selected_broadcaster,1);
				processAnimation(session_socket, "anim__LT_Score", "SHOW 0.0", session_selected_broadcaster,1);
				processAnimation(session_socket, "anim_Bug", "SHOW 0.0", session_selected_broadcaster,1);
				which_graphics_onscreen = "";
				break;
			
			case "ANIMATE-OUT-SCOREBUG":
				if(is_infobar == true) {
					processAnimation(session_socket, "anim_ScoreBug", "CONTINUE", session_selected_broadcaster,1);
					is_infobar = false;
					scorebug.setScorebug_on_screen(false);
				}
				break;
			case "ANIMATE-OUT":
				switch(which_graphics_onscreen) {	
				case "BUG-DB": case "BUG-FREETEXT":
					processAnimation(session_socket, "anim_Bug$In_Out", "CONTINUE", session_selected_broadcaster,1);
					which_graphics_onscreen = "";
					break;
				case "SCOREUPDATE":
					processAnimation(session_socket, "anim__LT_Score", "CONTINUE", session_selected_broadcaster,1);
					which_graphics_onscreen = "";
					break;
				}
				break;
			}
			break;
			}
		return null;
	}
	
	public void processAnimation(Socket session_socket, String animationName,String animationCommand, String which_broadcaster,int which_layer) throws IOException
	{
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		switch(which_broadcaster.toUpperCase()) {
		case "POLO":
			switch(which_layer) {
			case 1:
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*" + animationName + " "+ animationCommand + "\0");
				break;
			case 2:
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*" + animationName + " "+ animationCommand + "\0");
				break;
			}
			break;
		}
	}
	public String toString() {
		return "Doad [status=" + status + ", slashOrDash=" + slashOrDash + "]";
	}
	
	public void AnimateLogo(Socket session_socket) throws InterruptedException, IOException
	{
		processAnimation(session_socket, "anim_ScoreBug", "SHOW 0.0", session_selected_broadcaster,1);
		processAnimation(session_socket, "anim__LT_Score", "SHOW 0.0", session_selected_broadcaster,1);
		processAnimation(session_socket, "anim_Bug", "SHOW 0.0", session_selected_broadcaster,1);
		which_graphics_onscreen = "";
	}
	
	public ScoreBug populateScoreBug(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,String viz_sence_path,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug*ACTIVE SET 1\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$Team1$Text$txt_Score*GEOM*TEXT SET " + match.getHomeTeamScore() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$Team2$Text$txt_Score*GEOM*TEXT SET " + match.getAwayTeamScore() + "\0");
			
			if(is_this_updating == false) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$Team1$Out$img_Base*TEXTURE*IMAGE SET " + base_path + match.getHomeTeam().getTeamName4() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$Team2$Out$img_Base*TEXTURE*IMAGE SET " + base_path + match.getAwayTeam().getTeamName4() + "\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$Team1$Text$img_Text*TEXTURE*IMAGE SET " + text_path + match.getHomeTeam().getTeamName4() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$Team2$Text$img_Text*TEXTURE*IMAGE SET " + text_path + match.getAwayTeam().getTeamName4() + "\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$Team1$Flag$Shadow*TEXTURE*IMAGE SET " + flag_scorebug_path + match.getHomeTeam().getTeamName4() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$Team2$Flag$Shadow*TEXTURE*IMAGE SET " + flag_scorebug_path + match.getAwayTeam().getTeamName4() + "\0");

				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$Team1$Flag$img_Flags_ScoreBug*TEXTURE*IMAGE SET " + flag_scorebug_path 
						+ match.getHomeTeam().getTeamName4() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$Team2$Flag$img_Flags_ScoreBug*TEXTURE*IMAGE SET " + flag_scorebug_path 
						+ match.getAwayTeam().getTeamName4() + "\0");
				
				String result = "";
				if(match.getClock() != null) {
					if (match.getClock().getMatchHalves().equalsIgnoreCase("quarter1")) {
					    result = "CH 1";
					} else if (match.getClock().getMatchHalves().equalsIgnoreCase("quarter2")) {
					    result = "CH 2";
					} else if (match.getClock().getMatchHalves().equalsIgnoreCase("quarter3")) {
					    result = "CH 3";
					} else if (match.getClock().getMatchHalves().equalsIgnoreCase("quarter4")) {
					    result = "CH 4";
					}else if (match.getClock().getMatchHalves().equalsIgnoreCase("quarter5")) {
					    result = "CH 5";
					} else if (match.getClock().getMatchHalves().equalsIgnoreCase("quarter6")) {
					    result = "CH 6";
					} else if (match.getClock().getMatchHalves().equalsIgnoreCase("quarter7")) {
					    result = "CH 7";
					}else if (match.getClock().getMatchHalves().equalsIgnoreCase("quarter8")) {
					    result = "CH 8";
					}
				}
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$TimeGrp$Text$img_Text$txt_TeamName*GEOM*TEXT SET " + result + "\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$Team1$Text$img_Text$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName4() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main$Team2$Text$img_Text$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName4() + "\0");
			}
		}
		return scorebug;
	}
	
	public void populateBugsDB(Socket session_socket,String viz_scene, Bugs bug ,Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Bug*ACTIVE SET 1\0");
			
			if(bug.getText1() != null && bug.getText2() != null) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Bug$Text$txt_TeamName*GEOM*TEXT SET " + bug.getText1().toUpperCase() 
						+ " " + bug.getText2().toUpperCase() + "\0");
			}else if(bug.getText1() != null) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Bug$Text$txt_TeamName*GEOM*TEXT SET " + bug.getText1().toUpperCase() + "\0");
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Bug$Text$txt_TeamName*GEOM*TEXT SET " + bug.getText2().toUpperCase() + "\0");
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png anim_Bug$In_Out 0.500 anim_Bug$In_Out$In 0.500 \0");
		}
	}
	public void populateBugsFreeText(Socket session_socket,String viz_scene, String freetext ,Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Bug*ACTIVE SET 1\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Bug$Text$txt_TeamName*GEOM*TEXT SET " + freetext + "\0");
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png anim_Bug$In_Out 0.500 anim_Bug$In_Out$In 0.500 \0");
		}
	}
	public void populateScoreUpdate(Socket session_socket,String viz_scene,PoloService hockeyService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$TopLine$Text$img_Text$txt_TeamName*GEOM*TEXT SET " + "CHUKKAS SCORES" + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$TopLine$Text$Score$txt_1*GEOM*TEXT SET " + "1" + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$TopLine$Text$Score$txt_2*GEOM*TEXT SET " + "2" + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$TopLine$Text$Score$txt_3*GEOM*TEXT SET " + "3" + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$TopLine$Text$Score$txt_4*GEOM*TEXT SET " + "4" + "\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team1$Text$img_Text$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team1$img_Base*TEXTURE*IMAGE SET " + base_path + match.getHomeTeam().getTeamName4() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team1$Text$img_Text*TEXTURE*IMAGE SET " + text_path + match.getHomeTeam().getTeamName4() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team1$Flag$Shadow*TEXTURE*IMAGE SET " + flag_scorebug_path + match.getHomeTeam().getTeamName4() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team1$Flag$img_Flags_ScoreBug*TEXTURE*IMAGE SET " + flag_scorebug_path + match.getHomeTeam().getTeamName4() + "\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team2$Text$img_Text$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team2$img_Base*TEXTURE*IMAGE SET " + base_path + match.getAwayTeam().getTeamName4() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team2$Text$img_Text*TEXTURE*IMAGE SET " + text_path + match.getAwayTeam().getTeamName4() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team2$Flag$Shadow*TEXTURE*IMAGE SET " + flag_scorebug_path + match.getAwayTeam().getTeamName4() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team2$Flag$img_Flags_ScoreBug*TEXTURE*IMAGE SET " + flag_scorebug_path + match.getAwayTeam().getTeamName4() + "\0");
			
			
			for(int i=1;i <= match.getSets().size() ; i++) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team1$Text$Score$txt_" + i + "*GEOM*TEXT SET " 
						+ match.getSets().get(i-1).getHomeScore() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team2$Text$Score$txt_" + i + "*GEOM*TEXT SET " 
						+ match.getSets().get(i-1).getAwayScore() + "\0");
			}
			
			for(int j = match.getSets().size()+1;j<=4;j++) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team1$Text$Score$txt_" + j + "*GEOM*TEXT SET " + "-" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Score$Subline$Team2$Text$Score$txt_" + j + "*GEOM*TEXT SET " + "-" + "\0");
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png anim__LT_Score$In_Out 0.900 anim__LT_Score$In_Out$In 0.900 \0");
		}
	}
	
}
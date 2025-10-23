package com.polo.broadcaster;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
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
	
	public String session_selected_broadcaster = "HOCKEY_KHELO_INDIA";
	
	public PrintWriter print_writer;
	public ScoreBug scorebug = new ScoreBug(); 
	public String which_graphics_onscreen = "";
	public boolean is_infobar = false;
	private String logo_path = "C:\\Images\\KHELO_INDIA\\Logos\\";
	private String icon_path = "D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_Khelo_India_2023\\Icons\\";
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
		case "POPULATE-SCOREBUG":
		case "POPULATE-L3-NAMESUPER": case "POPULATE-L3-NAMESUPER-PLAYER": case "POPULATE-FF-MATCHID": case "POPULATE-FF-PLAYINGXI": case "POPULATE-L3-BUG-DB":
		case "POPULATE-L3-SCOREUPDATE": case "POPULATE-L3-MATCHSTATUS": case "POPULATE-L3-NAMESUPER-CARD": case "POPULATE-L3-SUBSTITUTE": case "POPULATE-L3-MATCHPROMO":
		case "POPULATE-L3-STAFF": case "POPULATE-FF-MATCHSTATS": case "POPULATE-FF-PROMO": case "POPULATE-DOUBLE_PROMO": case "POPULATE-POINTS_TABLE": 
		case "POPULATE-OFFICIALS": case "POPULATE-PENALTY": case "POPULATE-L3-SINGLE_SUBSTITUTE":
			switch(whatToProcess.toUpperCase()) {
			//case "POPULATE-SCOREBUG_STATS": case "POPULATE-RED_CARD": case "POPULATE-EXTRA_TIME": case "POPULATE-SPONSOR": case "POPULATE-SCOREBUG_STATS_TWO":
			//case "POPULATE-SCOREBUG-CARD":	case "POPULATE-SCOREBUG-SUBS": case "POPULATE-EXTRA_TIME_BOTH":
				//break;
			case "POPULATE-SCOREBUG":
				scenes.get(0).scene_load(session_socket, session_selected_broadcaster);
				break;
			default:
				scenes.get(1).setScene_path(valueToProcess.split(",")[1]);
				scenes.get(1).scene_load(session_socket,session_selected_broadcaster);
				break;
			}
			switch (whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG":
				populateScoreBug(false,scorebug,session_socket, valueToProcess.split(",")[1],match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-NAMESUPER":
				//System.out.println("Value1 : " + valueToProcess.split(",")[1] + "Value2 : " + valueToProcess.split(",")[2]);
				for(NameSuper ns : hockeyService.getNameSupers()) {
					  if(ns.getNamesuperId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateNameSuper(session_socket, valueToProcess.split(",")[1], ns, match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-L3-NAMESUPER-PLAYER":
				populateNameSuperPlayer(session_socket, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]), valueToProcess.split(",")[3], Integer.valueOf(valueToProcess.split(",")[4]), match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-NAMESUPER-CARD":
				populateNameSuperCard(session_socket, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]), valueToProcess.split(",")[3], Integer.valueOf(valueToProcess.split(",")[4]), match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-MATCHID":
				populateMatchId(session_socket,valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-MATCHSTATS":
				populateMatchStats(session_socket,valueToProcess.split(",")[1], hockeyService,match,clock, session_selected_broadcaster);
				break;
			/*
			 * case "POPULATE-FF-PLAYINGXI": populatePlayingXI(session_socket,
			 * valueToProcess.split(",")[1],
			 * Integer.valueOf(valueToProcess.split(",")[2]),hockeyService.getFormations(),
			 * hockeyService.getTeams(),match, session_selected_broadcaster); break;
			 */
			case "POPULATE-L3-BUG-DB":
				for(Bugs bug : hockeyService.getBugs()) {
					  if(bug.getBugId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateBugsDB(session_socket, valueToProcess.split(",")[1], bug, match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-L3-SCOREUPDATE":
				populateScoreUpdate(session_socket, valueToProcess.split(",")[1], hockeyService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-PENALTY":
				populateLtPenalty(session_socket, valueToProcess.split(",")[1],valueToProcess, hockeyService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-L3-MATCHSTATUS":
				populateMatchStatus(session_socket, valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-SUBSTITUTE":
				populateSubstitute(session_socket, valueToProcess.split(",")[1],Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3],
						hockeyService.getAllPlayer(),hockeyService.getTeams(), match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-SINGLE_SUBSTITUTE":
				populateSingleSubstitute(session_socket, valueToProcess.split(",")[1],Integer.valueOf(valueToProcess.split(",")[2]),
						hockeyService.getAllPlayer(),hockeyService.getTeams(), match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-MATCHPROMO":
				populateMatchPromo(session_socket, valueToProcess.split(",")[1], match,hockeyService.getFixtures(),hockeyService.getTeams(),hockeyService.getGrounds(), session_selected_broadcaster);
				break;
			case "POPULATE-FF-PROMO":
				populateMatchPromoSingle(session_socket, valueToProcess.split(",")[1] ,Integer.valueOf(valueToProcess.split(",")[2]),hockeyService.getTeams(),
						hockeyService.getFixtures(),hockeyService.getGrounds(),match , session_selected_broadcaster);
				break;
			case "POPULATE-DOUBLE_PROMO":
				populateMatchDoublePromo(session_socket, valueToProcess.split(",")[1], match,hockeyService.getFixtures(),hockeyService.getTeams(),hockeyService.getGrounds(), session_selected_broadcaster);
				break;
			case "POPULATE-POINTS_TABLE":
				LeagueTable league_table1 = null;
				LeagueTable league_table2 = null;
				if(new File(PoloUtil.KHELO_INDIA_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableA" + ".XML").exists()) {
					league_table1 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(PoloUtil.KHELO_INDIA_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableA" + ".XML"));
				}
				if(new File(PoloUtil.KHELO_INDIA_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableB" + ".XML").exists()) {
					league_table2 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(PoloUtil.KHELO_INDIA_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableB" + ".XML"));
				}
				
				populatePointsTable(session_socket, valueToProcess.split(",")[1],league_table1.getLeagueTeams(),league_table2.getLeagueTeams(),
						hockeyService.getTeams(),session_selected_broadcaster,match);
				break;
			case "POPULATE-L3-STAFF":
				for(Staff st : hockeyService.getStaffs()) {
					  if(st.getStaffId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateStaff(session_socket, valueToProcess.split(",")[1], st,hockeyService.getTeams(), match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-OFFICIALS":
				populateOfficials(session_socket, valueToProcess.split(",")[1],hockeyService.getOfficials(),match, session_selected_broadcaster);
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
			
		case "ANIMATE-IN-SCOREBUG": 
		case "CLEAR-ALL": 
		case "ANIMATE-OUT":
		case "ANIMATE-IN-SPONSOR": case "ANIMATE-OUT-SPONSOR": case "ANIMATE-IN-PLAYINGXI": case "ANIMATE-IN-MATCHID": case "ANIMATE-IN-NAMESUPER": 
		case "ANIMATE-IN-NAMESUPERDB": case "ANIMATE-IN-BUG-DB": case "ANIMATE-IN-SCOREUPDATE": case "ANIMATE-IN-MATCHSTATUS": case "ANIMATE-IN-NAMESUPER_CARD":
		case "ANIMATE-IN-SUBSTITUTE": case "ANIMATE-IN-MATCHPROMO": case "ANIMATE-IN-STAFF": case "ANIMATE-IN-MATCHSTATS": case "ANIMATE-IN-PROMO": 
		case "ANIMATE-IN-DOUBLE_PROMO": case "ANIMATE-OUT-SCOREBUG": case "ANIMATE-IN-POINTS_TABLE": case "ANIMATE-IN-OFFICIALS":case "ANIMATE-IN-PENALTY": 
		case "ANIMATE-IN-SINGLE_SUBSTITUTE":
			
			switch (whatToProcess.toUpperCase()) {
			case "ANIMATE-IN-PENALTY":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PENALTY";
				break;
			case "ANIMATE-IN-NAMESUPER_CARD":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "NAMESUPER_CARD";
				break;
			case "ANIMATE-IN-NAMESUPER":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "NAMESUPER";
				break;
			case "ANIMATE-IN-NAMESUPERDB":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "NAMESUPERDB";
				break;
			case "ANIMATE-IN-MATCHID":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHID";
				break;
			case "ANIMATE-IN-MATCHSTATS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHSTATS";
				break;
			case "ANIMATE-IN-PLAYINGXI":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-BUG-DB":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "BUG-DB";
				break;
			case "ANIMATE-IN-SCOREUPDATE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				TimeUnit.MILLISECONDS.sleep(500);
				if(match.getHomeTeamScore() > 0 || match.getAwayTeamScore() > 0) {
					if(match.getHomeTeamScore() > 4 || match.getAwayTeamScore() > 4) {
						processAnimation(session_socket, "Scorer3Line_In", "START", session_selected_broadcaster, 2);
					}else if(match.getHomeTeamScore() > 2 || match.getAwayTeamScore() > 2) {
						processAnimation(session_socket, "Scorer2Line_In", "START", session_selected_broadcaster, 2);
					}else {
						processAnimation(session_socket, "Scorer1Line_In", "START", session_selected_broadcaster, 2);
					}
				}
				which_graphics_onscreen = "SCOREUPDATE";
				break;
			case "ANIMATE-IN-MATCHSTATUS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHSTATUS";
				break;
			case "ANIMATE-IN-SUBSTITUTE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "SUBSTITUTE";
				break;
			case "ANIMATE-IN-SINGLE_SUBSTITUTE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "SINGLE_SUBSTITUTE";
				break;
			case "ANIMATE-IN-MATCHPROMO":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHPROMO";
				break;
			case "ANIMATE-IN-STAFF":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "STAFF";
				break;
			case "ANIMATE-IN-PROMO":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PROMO";
				break;
			case "ANIMATE-IN-DOUBLE_PROMO":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "DOUBLE_PROMO";
				break;
			case "ANIMATE-IN-POINTS_TABLE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "POINTS_TABLE";
				break;
			case "ANIMATE-IN-SCOREBUG":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,1);
				is_infobar = true;
				scorebug.setScorebug_on_screen(true);
				break;
			case "ANIMATE-IN-SPONSOR":
				processAnimation(session_socket, "SponsorIn", "START", session_selected_broadcaster,1);
				break;
			case "ANIMATE-IN-OFFICIALS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "OFFICIALS";
				break;
			case "CLEAR-ALL":
				print_writer.println("LAYER1*EVEREST*SINGLE_SCENE CLEAR;");
				print_writer.println("LAYER2*EVEREST*SINGLE_SCENE CLEAR;");
				which_graphics_onscreen = "";
				break;
			
			case "ANIMATE-OUT-SCOREBUG":
				if(is_infobar == true) {
					processAnimation(session_socket, "Out", "START", session_selected_broadcaster,1);
					is_infobar = false;
					scorebug.setScorebug_on_screen(false);
				}
				break;
			case "ANIMATE-OUT":
				switch(which_graphics_onscreen) {
				case "MATCHID": case "PLAYINGXI": case "POINTS_TABLE": case "MATCHSTATS":
					processAnimation(session_socket, "In", "CONTINUE", session_selected_broadcaster,2);
					which_graphics_onscreen = "";
					break;
				
				case "NAMESUPERDB": case "NAMESUPER":  case "BUG-DB": case "SCOREUPDATE": case "MATCHSTATUS": case "NAMESUPER_CARD":
				case "SUBSTITUTE": case "MATCHPROMO": case "STAFF":  case "PROMO": case "DOUBLE_PROMO": case "OFFICIALS": 
				case "PENALTY": case "SINGLE_SUBSTITUTE":
					processAnimation(session_socket, "Out", "START", session_selected_broadcaster,2);
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
		case "HOCKEY_KHELO_INDIA":
			switch(which_layer) {
			case 1:
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			case 2:
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			}
			break;
		}
	}
	public String toString() {
		return "Doad [status=" + status + ", slashOrDash=" + slashOrDash + "]";
	}

	
	public ScoreBug populateScoreBug(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,String viz_sence_path,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore01 " + match.getHomeTeamScore() + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore02 " + match.getAwayTeamScore() + ";");
			
			
			//System.out.println("Player : " + match.getHomeSquad().get(0).getFull_name());
			if(is_this_updating == false) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tMatchInfo " + match.getMatchIdent() + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam01 " + match.getHomeTeam().getTeamName1() + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam02 " + match.getAwayTeam().getTeamName1() + ";");
				
//				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tColor01 " + colors_path 
//						+ match.getHomeTeamJerseyColor() + HockeyUtil.PNG_EXTENSION + ";");
//				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tColor02 " + colors_path
//						+ match.getAwayTeamJerseyColor() + HockeyUtil.PNG_EXTENSION + ";");
				
			}
		}
		return scorebug;
	}
	
	public void populateNameSuper(Socket session_socket,String viz_scene, NameSuper ns ,Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			
			if(ns.getFirstname() == null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + ns.getSurname().toUpperCase() +";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(ns.getSurname() == null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + ns.getFirstname().toUpperCase() +";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + ns.getFirstname().toUpperCase() + " " + ns.getSurname().toUpperCase() +";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgIcon " + icon_path + "HOCKEY" + 
					PoloUtil.PNG_EXTENSION + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOtherInfo " + " " +";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + ns.getSubLine().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 90.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
		
	}
	public void populateNameSuperPlayer(Socket session_socket,String viz_scene, int TeamId, String captainGoalKeeper, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			String Home_or_Away="";
			int l = 4;
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOtherInfo " + " " +";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgIcon " + icon_path + "HOCKEY" + 
					PoloUtil.PNG_EXTENSION + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(TeamId == match.getHomeTeamId()) {
				
				Home_or_Away = match.getHomeTeam().getTeamName1().toUpperCase();
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + hs.getJersey_number() +";");
						//TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + hs.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
							
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + hs.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName4().toUpperCase() + ";");
						
						}
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + hsub.getJersey_number() +";");
						//TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + hsub.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
							
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + hsub.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName4().toUpperCase() + ";");
						}
					}
				}
			}
			else {

				Home_or_Away = match.getAwayTeam().getTeamName1().toUpperCase();
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + as.getJersey_number() +";");
//						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + as.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + as.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName4().toUpperCase() + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + asub.getJersey_number() +";");
						//TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + asub.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + asub.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName4().toUpperCase() + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
				}
			}
			
			switch(captainGoalKeeper.toUpperCase())
			{
			case "CAPTAIN":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + captainGoalKeeper.toUpperCase() + " , " + Home_or_Away + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "PLAYER OF THE MATCH":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + "HERO OF THE MATCH " + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "GOAL_KEEPER":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + "GOAL KEEPER" + " , " + Home_or_Away + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "PLAYER_TODAY_GOAL":
				int player_goal_count=0;
				if(player_goal_count != 0) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + "GOALS TODAY - " + player_goal_count + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + Home_or_Away + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
				
				break;
			case "GOAL_SCORER":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + "GOAL SCORER" + " , " + Home_or_Away + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "CAPTAIN-GOALKEEPER":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + "CAPTAIN & GOAL KEEPER" + " , " + Home_or_Away + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			}

			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 90;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateNameSuperCard(Socket session_socket,String viz_scene, int TeamId, String cardType, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			if(TeamId == match.getHomeTeamId()) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
						PoloUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour1 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + "\\Colour1" + HockeyUtil.PNG_EXTENSION + ";");
				//TimeUnit.MILLISECONDS.sleep(l);
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour2 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + "\\Colour2" + HockeyUtil.PNG_EXTENSION + ";");
				//TimeUnit.MILLISECONDS.sleep(l);
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + hs.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + hs.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + hs.getRole().toUpperCase() + " - " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
						
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + hsub.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + hsub.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + hsub.getRole().toUpperCase() + " - " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
					}
				}
			}
			else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
						PoloUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour1 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + "\\Colour1" + HockeyUtil.PNG_EXTENSION + ";");
				//TimeUnit.MILLISECONDS.sleep(l);
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour2 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + "\\Colour2" + HockeyUtil.PNG_EXTENSION + ";");
				//TimeUnit.MILLISECONDS.sleep(l);
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + as.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + as.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + as.getRole().toUpperCase() + " - " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + asub.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + asub.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + asub.getRole().toUpperCase() + " - " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
					}
				}
			}
			
			switch(cardType.toUpperCase())
			{
			case PoloUtil.YELLOW:
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCard 0;");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case PoloUtil.RED:
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCard 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "YELLOW_RED":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCard 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			}

			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 92.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
		}
	}
	public void populateMatchId(Socket session_socket,String viz_scene, Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "HOCKEY" + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubHeader 2;");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText01 " + match.getMatchIdent() + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgGameIcon " + icon_path + "HOCKEY" + 
					PoloUtil.PNG_EXTENSION + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam01 " + match.getHomeTeam().getTeamName1() + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam02 " + match.getAwayTeam().getTeamName1() + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tBottomInfo " + "LIVE FROM " + match.getVenueName().toUpperCase() + ";");
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateSubstitute(Socket session_socket,String viz_scene,int Team_id,String Num_Of_Subs,List<Player> plyr,List<Team> team, Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			List<Event> evnt = new ArrayList<Event>();
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + team.get(Team_id - 1).getTeamName4() + 
					PoloUtil.PNG_EXTENSION + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "SUBSTITUTIONS" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			switch(Num_Of_Subs.toUpperCase())
			{
			case "SINGLE":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 0;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 0;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + PoloUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + PoloUtil.PNG_EXTENSION + ";");
				
				break;
			case "DOUBLE":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + PoloUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + PoloUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2B " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2B " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2B " + logo_path + "Red_Arrow" + PoloUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1B " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1B " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1B " + logo_path + "Green_Arrow" + PoloUtil.PNG_EXTENSION + ";");
				
				break;
				
			case "TRIPLE":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + PoloUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + PoloUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2B " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2B " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2B " + logo_path + "Red_Arrow" + PoloUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1B " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1B " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1B " + logo_path + "Green_Arrow" + PoloUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2C " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2C " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2C " + logo_path + "Red_Arrow" + PoloUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1C " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1C " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1C " + logo_path + "Green_Arrow" + PoloUtil.PNG_EXTENSION + ";");
				
				break;
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 36.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateSingleSubstitute(Socket session_socket,String viz_scene,int Team_id,List<Player> plyr,List<Team> team, Match match, String session_selected_broadcaster) throws IOException, InterruptedException {
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			List<Event> evnt = new ArrayList<Event>();
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + team.get(Team_id - 1).getTeamName4() + 
					PoloUtil.PNG_EXTENSION + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "SUBSTITUTIONS" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 0;");
			//TimeUnit.MILLISECONDS.sleep(l);
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 0;");
			//TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2 " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2 " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2 " + logo_path + "Red_Arrow" + PoloUtil.PNG_EXTENSION + ";");
		
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1 " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1 " + logo_path + "Green_Arrow" + PoloUtil.PNG_EXTENSION + ";");
				
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 36.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	
//	public void populatePlayingXI(Socket session_socket,String viz_scene, int TeamId,List<Formation> formation, List<Team> team ,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
//	{
//		if (match == null) {
//			this.status = "ERROR: Match is null";
//		}else {
//			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
//			int i,row_id = 0,row_id_sub=0,l=4;
//			
//			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "HOCKEY" + ";");
//			
//			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatHead01 " + "STARTING XI" + ";");
//			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatHead02 " + "SUBSTITUTES" + ";");
//			
//			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgGameIcon " + icon_path + "HOCKEY" + 
//					PoloUtil.PNG_EXTENSION + ";");
//			
//			if(TeamId == match.getHomeTeamId()) {
//				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText01 " + match.getHomeTeam().getTeamName1() + ";");
//				
//				for(Player hs : match.getHomeSquad()) {
//					row_id = row_id + 1;
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayer0"+ row_id + " " + hs.getFull_name().toUpperCase() + ";");
//					TimeUnit.MILLISECONDS.sleep(l);
//					
//					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(PoloUtil.CAPTAIN)) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + "(C)" + ";");
//						TimeUnit.MILLISECONDS.sleep(l);
//					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(PoloUtil.GOAL_KEEPER)) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + "(GK)" + ";");
//						TimeUnit.MILLISECONDS.sleep(l);
//					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + "(C)(GK)" + ";");
//						TimeUnit.MILLISECONDS.sleep(l);
//					}else {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + " " + ";");
//						TimeUnit.MILLISECONDS.sleep(l);
//					}
//				}
//				
//				for(int k=1;k<=11;k++) {
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubVisibility0"+ k + " " + "0" + ";");
//				}
//				
//				for(i = 0; i <= match.getHomeSubstitutesPerTeam()-1; i++) {
//					row_id_sub = row_id_sub + 1;
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubVisibility0"+ row_id_sub + " " + "1" + ";");
//					TimeUnit.MILLISECONDS.sleep(l);
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubs0"+ row_id_sub + " " + match.getHomeSubstitutes().get(i).getFull_name().toUpperCase() + ";");
//					TimeUnit.MILLISECONDS.sleep(l);
//					
//					if(match.getHomeSubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(PoloUtil.GOAL_KEEPER)) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole0"+ row_id_sub + " " + "(GK)" + ";");
//						TimeUnit.MILLISECONDS.sleep(l);
//					}else {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole0"+ row_id_sub + " " + " " + ";");
//						TimeUnit.MILLISECONDS.sleep(l);
//					}
//				}
//			}else if(TeamId == match.getAwayTeamId()){
//				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText01 " + match.getAwayTeam().getTeamName1() + ";");
//				
//				for(Player hs : match.getAwaySquad()) {
//					row_id = row_id + 1;
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayer0"+ row_id + " " + hs.getFull_name().toUpperCase() + ";");
//					TimeUnit.MILLISECONDS.sleep(l);
//					
//					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(PoloUtil.CAPTAIN)) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + "(C)" + ";");
//						TimeUnit.MILLISECONDS.sleep(l);
//					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(PoloUtil.GOAL_KEEPER)) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + "(GK)" + ";");
//						TimeUnit.MILLISECONDS.sleep(l);
//					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + "(C)(GK)" + ";");
//						TimeUnit.MILLISECONDS.sleep(l);
//					}else {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + " " + ";");
//						TimeUnit.MILLISECONDS.sleep(l);
//					}
//					
//				}
//				for(int k=1;k<=11;k++) {
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubVisibility0"+ k + " " + "0" + ";");
//				}
//				
//				for(i = 0; i <= match.getAwaySubstitutesPerTeam()-1; i++) {
//					row_id_sub = row_id_sub + 1;
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubVisibility0"+ row_id_sub + " " + "1" + ";");
//					TimeUnit.MILLISECONDS.sleep(l);
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubs0"+ row_id_sub + " " + match.getAwaySubstitutes().get(i).getFull_name().toUpperCase() + ";");
//					TimeUnit.MILLISECONDS.sleep(l);
//					
//					if(match.getAwaySubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(PoloUtil.GOAL_KEEPER)) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole0"+ row_id_sub + " " + "(GK)" + ";");
//						TimeUnit.MILLISECONDS.sleep(l);
//					}else {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole0"+ row_id_sub + " " + " " + ";");
//						TimeUnit.MILLISECONDS.sleep(l);
//					}
//				}
//			}
//			
//			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
//			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
//			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
//			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
//			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
//			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
//			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
//			TimeUnit.SECONDS.sleep(1);
//			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
//			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
//			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
//			TimeUnit.SECONDS.sleep(1);
//		}
//	}
	public void populateBugsDB(Socket session_socket,String viz_scene, Bugs bug ,Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			if(bug.getText1() != null && bug.getText2() != null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01 " + bug.getText1().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "" +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1B " + bug.getText2().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1C " + "" +";");
				
			}else if(bug.getText1() != null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01 " + bug.getText1().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "" +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1B " + "" +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1C " + "" +";");
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01 " + bug.getText2().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "" +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1B " + "" +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1C " + "" +";");
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 32.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateScoreUpdate(Socket session_socket,String viz_scene,PoloService hockeyService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int l=4;
			String h1="",h2="",h3="",a1="",a2="",a3="";
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
					PoloUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
					PoloUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase(PoloUtil.HALF)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + clock.getMatchHalves().toUpperCase() + " TIME" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(PoloUtil.FULL)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + clock.getMatchHalves().toUpperCase() + " TIME"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(PoloUtil.FIRST)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "FIRST HALF"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(PoloUtil.SECOND)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "SECOND HALF"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(PoloUtil.EXTRA1)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "EXTRA TIME 1"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(PoloUtil.EXTRA2)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "EXTRA TIME 2"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(match.getHomeTeamScore() == 0 ) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeScorerData " + "0" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getHomeTeamScore() > 0 && match.getHomeTeamScore() <= 2) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeScorerData " + "1" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getHomeTeamScore() > 2 && match.getHomeTeamScore() <= 4) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeScorerData " + "2" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeScorerData " + "3" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			if(match.getAwayTeamScore() == 0 ) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayScorerData " + "0" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getAwayTeamScore() > 0 && match.getAwayTeamScore() <= 2) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayScorerData " + "1" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getAwayTeamScore() > 2 && match.getAwayTeamScore() <= 4) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayScorerData " + "2" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayScorerData " + "3" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			List<String> home_stats = new ArrayList<String>();
			List<String> away_stats = new ArrayList<String>();
			
			for(int i=0;i<=home_stats.size()-1;i++) {
				if(i < 2) { 
					h1 = h1 + home_stats.get(i); 
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamPlayers1 " + h1 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 4) {
					h2 = h2 + home_stats.get(i);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamPlayers2 " + h2 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 6){
					h3 = h3 + home_stats.get(i);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamPlayers3 " + h3 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			for(int i=0;i<=away_stats.size()-1;i++) {
				if(i < 2) { 
					a1 = a1 + away_stats.get(i); 
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamPlayers1 " + a1 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 4) {
					a2 = a2 + away_stats.get(i);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamPlayers2 " + a2 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 6){
					a3 = a3 + away_stats.get(i);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamPlayers3 " + a3 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 58.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateMatchStatus(Socket session_socket,String viz_scene,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, CsvException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int l = 4;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + match.getMatchIdent().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + match.getTournament().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
					PoloUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName1() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
					PoloUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName1() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomeTeamScore() + " - " + match.getAwayTeamScore() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getClock().getMatchHalves().toUpperCase() + " TIME" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getClock().getMatchHalves().toUpperCase() + " TIME" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "FIRST HALF" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "SECOND HALF" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "EXTRA TIME 1" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "EXTRA TIME 2" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead1 " + PoloUtil.SHOTS + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead2 " + PoloUtil.SHOTS_ON_TARGET.replace("_", " ") + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead3 " + PoloUtil.RED + " CARDS" + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead4 " + PoloUtil.YELLOW + " CARDS" + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead5 " + "TACKLES" + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead6 " + "OFFSIDES" + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead7 " + PoloUtil.CORNERS + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead8 " + "POSSESSION (%)" + ";");
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 100.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateMatchPromo(Socket session_socket,String viz_scene,Match match,List<Fixture> fixture,List<Team> team,List<Ground> ground, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int count = 0 , count1 = 0,row_id = 0,l = 4 ;
			String groun = "";
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "UPCOMING MATCHES" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getTournament().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(Fixture fx :fixture) {
				if(match.getMatchFileName().replace(".xml", "").equalsIgnoreCase(fx.getMatchfilename())) {
					count = fx.getMatchnumber();
					count1 = count + 4;
				}
			}
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vMatches " + "1" + ";");
			
			for(;count < count1;) {
			//if(count < count1) {
				row_id = row_id + 1;
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeLogo" + row_id + " " + logo_path + team.get(fixture.get(count).getHometeamid() - 1).getTeamName4().toUpperCase() + 
						PoloUtil.PNG_EXTENSION+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayLogo" + row_id + " " + logo_path + team.get(fixture.get(count).getAwayteamid() - 1).getTeamName4().toUpperCase() + 
						PoloUtil.PNG_EXTENSION+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName" + row_id + " " + team.get(fixture.get(count).getHometeamid() - 1).getTeamName1().toUpperCase() + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName" + row_id + " " + team.get(fixture.get(count).getAwayteamid() - 1).getTeamName1().toUpperCase() + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				
				for(int j = 0; j <= ground.size()-1; j++) {
					if(ground.get(j).getGroundId() == Integer.valueOf(fixture.get(count).getVenue())) {
						groun = ground.get(j).getCity();
					}
				}
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo" + row_id + " " + fixture.get(count).getMatchfilename().toUpperCase() + " - " + fixture.get(count).getDate() + " (" + fixture.get(count).getTime() + ") LIVE FROM " + groun + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				
				count = count + 1;
				
			}
			
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 85.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateStaff(Socket session_socket,String viz_scene, Staff st,List<Team> team ,Match match, String selectedbroadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + team.get(st.getClubId() - 1).getTeamName4() + 
					PoloUtil.PNG_EXTENSION + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam " + team.get(st.getClubId() - 1).getTeamName1().toUpperCase() +";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + st.getName().toUpperCase() +";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + st.getRole().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			

			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 35;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
		}
	}
	public void populateMatchStats(Socket session_socket,String viz_scene,PoloService hockeyService, Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "HOCKEY" + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubHeader 2;");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText01 " + match.getMatchIdent() + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgGameIcon " + icon_path + "HOCKEY" + 
					PoloUtil.PNG_EXTENSION + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vVSOptions 0;");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam01 " + match.getHomeTeam().getTeamName1() + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam02 " + match.getAwayTeam().getTeamName1() + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScoreHome " + match.getHomeTeamScore() + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScoreAway " + match.getAwayTeamScore() + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tBottomInfo " + "LIVE FROM " + match.getVenueName().toUpperCase() + ";");
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateMatchPromoSingle(Socket session_socket,String viz_sence_path, int match_number ,List<Team> team,List<Fixture> fix,List<Ground>ground,Match match, String broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {			
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int l =4;
			String grounds = "";
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + fix.get(match_number - 1).getMatchfilename().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(Team TM : team) {
				if(fix.get(match_number - 1).getHometeamid() == TM.getTeamId()) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + TM.getTeamName4().toUpperCase() + PoloUtil.PNG_EXTENSION + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName "+ TM.getTeamName1().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
				if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + TM.getTeamName4().toUpperCase() + PoloUtil.PNG_EXTENSION + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName "+ TM.getTeamName1().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			Calendar cal = Calendar.getInstance();
			//cal.add(Calendar.DATE, +1);
			if(fix.get(match_number - 1).getDate().equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()))) {
				for(int j = 0; j <= ground.size()-1; j++) {
					if(ground.get(j).getGroundId() == Integer.valueOf(fix.get(match_number - 1).getVenue())) {
						grounds = ground.get(j).getFullname();
					}
				}
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "UP NEXT " + "(" + fix.get(match_number - 1).getTime() + ") - LIVE FROM " + grounds + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				for(int j = 0; j <= ground.size()-1; j++) {
					if(ground.get(j).getGroundId() == Integer.valueOf(fix.get(match_number - 1).getVenue())) {
						grounds = ground.get(j).getFullname();
					}
				}
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + fix.get(match_number - 1).getDate() + " (" + fix.get(match_number - 1).getTime() + ") - LIVE FROM " + grounds + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 196.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
				
		}
	}
	public void populateMatchDoublePromo(Socket session_socket,String viz_scene,Match match,List<Fixture> fixture,List<Team> team,List<Ground> ground, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int row_id = 1 ,l=4;
			String Date = "",grou = "";
			Calendar cal = Calendar.getInstance();
			
			cal.add(Calendar.DATE, +1);
			Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "TOMORROW'S MATCHES" + ";");
			
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getTournament().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vMatches " + "0" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(int i = 0; i <= fixture.size()-1; i++) {
				if(fixture.get(i).getDate().equalsIgnoreCase(Date)) {
					for(int j = 0; j <= ground.size()-1; j++) {
						if(ground.get(j).getGroundId() == Integer.valueOf(fixture.get(i).getVenue())) {
							grou = ground.get(j).getFullname();
						}
					}
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeLogo" + row_id + " " + logo_path + team.get(fixture.get(i).getHometeamid() - 1).getTeamName4().toUpperCase() + 
							PoloUtil.PNG_EXTENSION+ ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayLogo" + row_id + " " + logo_path + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName4().toUpperCase() + 
							PoloUtil.PNG_EXTENSION+ ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamFirstName" + row_id + " " + team.get(fixture.get(i).getHometeamid() - 1).getTeamName2().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamLastName" + row_id + " " + team.get(fixture.get(i).getHometeamid() - 1).getTeamName3().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamFirstName" + row_id + " " + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName2().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamLastName" + row_id + " " + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName3().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo" + row_id + " " + fixture.get(i).getMatchfilename().toUpperCase() + " - " + "(" + fixture.get(i).getTime() + ") LIVE FROM " + grou + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					row_id = row_id +1;
				}
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 85.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populatePointsTable(Socket session_socket,String viz_sence_path,List<LeagueTeam> point_table1,List<LeagueTeam> point_table2, List<Team> team,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		int row_no=0,l=4;
		print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "HOCKEY POINTS TABLE" + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vTableType 0;");
		print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText01 " + "GROUP A" + ";");
		print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText02 " + "GROUP B" + ";");
		
		print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgGameIcon " + icon_path + "HOCKEY" + 
				PoloUtil.PNG_EXTENSION + ";");
		
		for(int i = 0; i <= point_table1.size() - 1 ; i++) {
			row_no = row_no + 1;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam0" + row_no + " " + point_table1.get(i).getTeamName() + ";");
			if(point_table1.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tQ0" + row_no + " " + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tQ0" + row_no + " " + "(Q)" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
				
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayer0" + row_no + " " + point_table1.get(i).getPlayed() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tWon0" + row_no + " " + point_table1.get(i).getWon() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tLost0" + row_no + " " + point_table1.get(i).getDrawn() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTied0" + row_no + " " + point_table1.get(i).getLost() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPTS0" + row_no + " " + point_table1.get(i).getPoints() + ";");
			TimeUnit.MILLISECONDS.sleep(l);

		}
		
		for(int j = 0; j <= point_table2.size() - 1 ; j++) {
			row_no = row_no + 1;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam0" + row_no + " " + point_table2.get(j).getTeamName() + ";");
			if(point_table2.get(j).getQualifiedStatus().trim().equalsIgnoreCase("")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tQ0" + row_no + " " + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tQ0" + row_no + " " + "(Q)" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
				
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayer0" + row_no + " " + point_table2.get(j).getPlayed() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tWon0" + row_no + " " + point_table2.get(j).getWon() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tLost0" + row_no + " " + point_table2.get(j).getDrawn() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTied0" + row_no + " " + point_table2.get(j).getLost() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPTS0" + row_no + " " + point_table2.get(j).getPoints() + ";");
			TimeUnit.MILLISECONDS.sleep(l);

		}
		print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Infobar*CONTAINER SET ACTIVE 0;");
		print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
		print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
		print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
		print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
		print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
		print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
		TimeUnit.SECONDS.sleep(1);
		print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
		print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Infobar*CONTAINER SET ACTIVE 1;");
		print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		
	}
	
	public void populateOfficials(Socket session_socket,String viz_scene,List<Officials> officials,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);

			int l=4;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOfficialName1 " + officials.get(0).getReferee().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOfficialName2 " + officials.get(0).getFourthOfficial().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOfficialName3 " + officials.get(0).getAssistantReferee2().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOfficialName4 " + officials.get(0).getAssistantReferee1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 50.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateLtPenalty(Socket session_socket,String viz_scene,String valueToProcess,PoloService hockeyService,Match match,Clock clock, String session_selected_broadcaster) 
			throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int l=4;
			int iHomeCont = 0, iAwayCont = 0;
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
					PoloUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
					PoloUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomePenaltiesHits() + "-" + match.getAwayPenaltiesHits() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
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
				}
				
				
				if(pen.toUpperCase().contains(PoloUtil.HOME + "_" + PoloUtil.DECREMENT + "_" + "PENALTIES" + "_" + PoloUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");
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
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 40.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
}
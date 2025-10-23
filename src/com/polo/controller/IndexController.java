package com.polo.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvException;
import com.polo.broadcaster.POLO;
import com.polo.containers.Scene;
import com.polo.containers.ScoreBug;
import com.polo.model.Clock;
import com.polo.model.Configurations;
import com.polo.model.Event;
import com.polo.model.EventFile;
import com.polo.model.Match;
import com.polo.model.Player;
import com.polo.service.PoloService;
import com.polo.util.PoloFunctions;
import com.polo.util.PoloUtil;

import net.sf.json.JSONObject;

@Controller
public class IndexController 
{
	@Autowired
	PoloService poloService;
	
	public static String expiry_date = "2025-12-31";
	public static String current_date = "";
	public static String error_message = "";
	public static Clock session_clock = new Clock();
	public static Configurations session_configurations;
	public static Match session_match;
	public static EventFile session_event;
	public static String session_selected_broadcaster;
	public static Socket session_socket;
	public static POLO session_khelo_india;
	public static List<Scene> session_selected_scenes;
	
	@RequestMapping(value = {"/","/initialise"}, method={RequestMethod.GET,RequestMethod.POST}) 
	public String initialisePage(ModelMap model) 
		throws IOException, JAXBException 
	{
		
		if(current_date == null || current_date.isEmpty()) {
			current_date = PoloFunctions.getOnlineCurrentDate();
		}
		model.addAttribute("session_viz_scenes", new File(PoloUtil.POLO_DIRECTORY + 
				PoloUtil.SCENES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".via") && pathname.isFile();
		    }
		}));

		model.addAttribute("match_files", new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + 
				PoloUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".json") && pathname.isFile();
		    }
		}));
		
		if(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.CONFIGURATIONS_DIRECTORY + PoloUtil.OUTPUT_XML).exists()) {
			session_configurations = (Configurations)JAXBContext.newInstance(Configurations.class).createUnmarshaller().unmarshal(
					new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.CONFIGURATIONS_DIRECTORY + PoloUtil.OUTPUT_XML));
		} else {
			session_configurations = new Configurations();
			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_configurations, 
					new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.CONFIGURATIONS_DIRECTORY + PoloUtil.OUTPUT_XML));
		}
		
		model.addAttribute("session_configurations",session_configurations);
	
		return "initialise";
	}
	
	@RequestMapping(value = {"/setup"}, method = RequestMethod.POST)
	public String setupPage(ModelMap model) throws JAXBException, IllegalAccessException, 
		InvocationTargetException, IOException, ParseException  
	{
		model.addAttribute("match_files", new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + 
				PoloUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".json") && pathname.isFile();
		    }
		}));
		model.addAttribute("session_match", session_match);
		model.addAttribute("teams", poloService.getTeams());
		/* model.addAttribute("formations", poloService.getFormations()); */
		model.addAttribute("teamcolor", poloService.getTeamColors());
		model.addAttribute("grounds", poloService.getGrounds());
		model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));

		return "setup";
	}

	@RequestMapping(value = {"/match"}, method = {RequestMethod.POST,RequestMethod.GET})
	public String poloMatchPage(ModelMap model,
		@RequestParam(value = "selectedBroadcaster", required = false, defaultValue = "") String selectedBroadcaster,
		@RequestParam(value = "vizIPAddress", required = false, defaultValue = "") String vizIPAddresss,
		@RequestParam(value = "vizPortNumber", required = false, defaultValue = "") String vizPortNumber,
		@RequestParam(value = "vizScene", required = false, defaultValue = "") String vizScene)
			throws IOException, ParseException, JAXBException, InterruptedException  
	{
		if(current_date == null || current_date.isEmpty()) {
		
			model.addAttribute("error_message","You must be connected to the internet online");
			return "error";
		
		} else if(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
			
			model.addAttribute("error_message","This software has expired");
			return "error";
			
		}else {

			session_selected_broadcaster = selectedBroadcaster;
			session_selected_scenes = new ArrayList<Scene>();
			if(!vizIPAddresss.trim().isEmpty() && !vizPortNumber.trim().isEmpty()) {
				//System.out.println("Broad : " + session_selected_broadcaster + " Port : " + vizPortNumber);
				session_socket = new Socket(vizIPAddresss, Integer.valueOf(vizPortNumber));
				switch (session_selected_broadcaster.toUpperCase()) {
				case PoloUtil.POLO:
					session_selected_scenes.add(new Scene(PoloUtil.POLO_SCORE_BUG_SCENE_PATH,PoloUtil.ONE)); // Front layer
					session_selected_scenes.add(new Scene("",PoloUtil.TWO));
					session_selected_scenes.get(0).scene_load(session_socket, session_selected_broadcaster);
					session_khelo_india = new POLO();
					session_khelo_india.scorebug = new ScoreBug();
					break;
					
				}
			}
			
			model.addAttribute("match_files", new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + 
					PoloUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".json") && pathname.isFile();
			    }
			}));

			model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
			
			session_match = new Match();
			session_event = new EventFile();
			if(session_event.getEvents() == null || session_event.getEvents().size() <= 0)
				session_event.setEvents(new ArrayList<Event>());
			if(session_match.getClock() == null) 
				session_match.setClock(new Clock());
			
			session_configurations.setBroadcaster(selectedBroadcaster);
			session_configurations.setVizscene(vizScene);
			session_configurations.setIpAddress(vizIPAddresss);
			
			if(!vizPortNumber.trim().isEmpty()) {
				session_configurations.setPortNumber(Integer.valueOf(vizPortNumber));
			}

			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_configurations, 
					new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.CONFIGURATIONS_DIRECTORY + PoloUtil.OUTPUT_XML));
			
			model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
			model.addAttribute("session_match", session_match);
			model.addAttribute("session_event", session_event);
			model.addAttribute("session_configurations", session_configurations);
			model.addAttribute("session_socket", session_socket);
			model.addAttribute("session_khelo_india", session_khelo_india);
			model.addAttribute("session_selected_scenes", session_selected_scenes);
			
			return "match";
		}
	}
	
	@RequestMapping(value = {"/back_to_match"}, method = RequestMethod.POST)
	public String backToMatchPage(ModelMap model) throws ParseException
	{
		if(current_date == null || current_date.isEmpty()) {
		
			model.addAttribute("error_message","You must be connected to the internet online");
			return "error";
		
		} else if(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
			
			model.addAttribute("error_message","This software has expired");
			return "error";
			
		}else {
		
			model.addAttribute("match_files", new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + 
					PoloUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".json") && pathname.isFile();
			    }
			}));
			model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
			
			model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
			model.addAttribute("session_match", session_match);

			return "match";
		
		}
	}	
	
	@RequestMapping(value = {"/upload_match_setup_data", "/reset_and_upload_match_setup_data"}
		,method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String uploadFormDataToSessionObjects(MultipartHttpServletRequest request) 
			throws IllegalAccessException, InvocationTargetException, JAXBException, IOException
	{
		if (request.getRequestURI().contains("upload_match_setup_data") 
				|| request.getRequestURI().contains("reset_and_upload_match_setup_data")) {
			
			List<Player> home_squad = new ArrayList<Player>(); List<Player> away_squad = new ArrayList<Player>();
			List<Player> home_substitutes = new ArrayList<Player>(); List<Player> away_substitutes = new ArrayList<Player>();

	   		boolean reset_all_variables = false;
			if(request.getRequestURI().contains("reset_and_upload_match_setup_data")) {
				reset_all_variables = true;
			} else if(request.getRequestURI().contains("upload_match_setup_data")) {
				for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
					if(entry.getKey().equalsIgnoreCase("select_existing_hockey_matches") && entry.getValue()[0].equalsIgnoreCase("new_match")) {
						reset_all_variables = true;
						break;
					}
				}
			}
			if(reset_all_variables == true) {
				session_match = new Match(); 
				session_event = new EventFile();
				session_event.setEvents(new ArrayList<Event>());
			}
			
			for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
	   			if(entry.getKey().contains("_")) {
   					if(entry.getKey().split("_")[0].equalsIgnoreCase(PoloUtil.HOME + PoloUtil.PLAYER)) {
   						switch (Integer.parseInt(entry.getKey().split("_")[1])) {
   						case 1: case 2: case 3: case 4:
   		   					home_squad.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), PoloUtil.PLAYER));
   							break;
   						default:
   		   					home_substitutes.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), PoloUtil.SUBSTITUTE));
   							break;
   						}
   					} else if(entry.getKey().split("_")[0].equalsIgnoreCase(PoloUtil.AWAY + PoloUtil.PLAYER)) {
   						switch (Integer.parseInt(entry.getKey().split("_")[1])) {
   						case 1: case 2: case 3: case 4: 
   		   					away_squad.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), PoloUtil.PLAYER));
   							break;
   						default:
   		   					away_substitutes.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), PoloUtil.SUBSTITUTE));
   							break;
   						}
   					}
	   			} else {
	   				BeanUtils.setProperty(session_match, entry.getKey(), entry.getValue()[0]);
	   			}
	   		}
			
			for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
	   			if(entry.getKey().contains("_")) {
	   				if(entry.getKey().split("_")[0].equalsIgnoreCase(PoloUtil.HOME + PoloUtil.CAPTAIN 
	   						+ PoloUtil.GOAL_KEEPER.replace("_", ""))) {
	   					for(Player plyr:home_squad) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
	   					for(Player plyr:home_substitutes) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
	   				} else if(entry.getKey().split("_")[0].equalsIgnoreCase(PoloUtil.AWAY + PoloUtil.CAPTAIN 
	   						+ PoloUtil.GOAL_KEEPER.replace("_", ""))) {
	   					for(Player plyr:away_squad) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
	   					for(Player plyr:away_substitutes) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
   					}
	   			}
	   		}

			session_match.setHomeSquad(home_squad);
			session_match.setAwaySquad(away_squad);
			
			Collections.sort(session_match.getHomeSquad());
			Collections.sort(session_match.getAwaySquad());

			session_match.setHomeSubstitutes(home_substitutes);
			session_match.setAwaySubstitutes(away_substitutes);
			
			Collections.sort(session_match.getHomeSubstitutes());
			Collections.sort(session_match.getAwaySubstitutes());
			
			session_match.setHomeOtherSquad(PoloFunctions.getPlayersFromDB(poloService, PoloUtil.HOME, session_match));
			session_match.setAwayOtherSquad(PoloFunctions.getPlayersFromDB(poloService, PoloUtil.AWAY, session_match));

			new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()).createNewFile();
			new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.EVENT_DIRECTORY + session_match.getMatchFileName()).createNewFile();
			
			session_match = PoloFunctions.populateMatchVariables(poloService, session_match);

			new ObjectMapper().writeValue(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), 
					session_match);
			new ObjectMapper().writeValue(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.EVENT_DIRECTORY + session_match.getMatchFileName()), 
					session_event);
		}
		session_match.setEvents(session_event.getEvents());
		return JSONObject.fromObject(session_match).toString();
	}
	
	@RequestMapping(value = {"/processPoloProcedures"}, method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String processPoloProcedures(
			@RequestParam(value = "whatToProcess", required = false, defaultValue = "") String whatToProcess,
			@RequestParam(value = "valueToProcess", required = false, defaultValue = "") String valueToProcess)
					throws JAXBException, IllegalAccessException, InvocationTargetException, IOException, NumberFormatException, InterruptedException, 
						CsvException, SAXException, ParserConfigurationException
	{	
		Event this_event = new Event();
		if(!whatToProcess.equalsIgnoreCase(PoloUtil.LOAD_TEAMS)) {
			if(valueToProcess.contains(",")) {
				if(session_match.getMatchFileName() == null || session_match.getMatchFileName().isEmpty()) {
					
					session_match = new ObjectMapper().readValue
							(new File(PoloUtil.POLO_DIRECTORY + PoloUtil.MATCHES_DIRECTORY+ valueToProcess.split(",")[0]), Match.class);

					session_event = new ObjectMapper().readValue(new File(PoloUtil.POLO_DIRECTORY + PoloUtil.EVENT_DIRECTORY+
							valueToProcess.split(",")[0]), EventFile.class);
				}
			}
		}
		
		switch (whatToProcess.toUpperCase()) {
		case PoloUtil.LOG_STAT:

			if(valueToProcess.toUpperCase().contains(PoloUtil.PENALTIES)) {
				if(valueToProcess.split(",")[1].split("_")[1].toUpperCase().contains(PoloUtil.INCREMENT)) {
					if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(PoloUtil.HOME)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(PoloUtil.HIT)) {
							session_match.setHomePenaltiesHits(session_match.getHomePenaltiesHits() + 1);
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(PoloUtil.MISS)) {
							session_match.setHomePenaltiesMisses(session_match.getHomePenaltiesMisses() + 1);
						}
					}else if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(PoloUtil.AWAY)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(PoloUtil.HIT)) {
							session_match.setAwayPenaltiesHits(session_match.getAwayPenaltiesHits() + 1);
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(PoloUtil.MISS)) {
							session_match.setAwayPenaltiesMisses(session_match.getAwayPenaltiesMisses() + 1);
						}
					}
				}else if(valueToProcess.split(",")[1].split("_")[1].toUpperCase().contains(PoloUtil.DECREMENT)) {
					if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(PoloUtil.HOME)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(PoloUtil.HIT)) {
							if(session_match.getHomePenaltiesHits() > 0) {
								session_match.setHomePenaltiesHits(session_match.getHomePenaltiesHits() - 1);
							}
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(PoloUtil.MISS)) {
							if(session_match.getHomePenaltiesMisses() > 0) {
								session_match.setHomePenaltiesMisses(session_match.getHomePenaltiesMisses() - 1);
							}
						}
					}else if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(PoloUtil.AWAY)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(PoloUtil.HIT)) {
							if(session_match.getAwayPenaltiesHits() > 0) {
								session_match.setAwayPenaltiesHits(session_match.getAwayPenaltiesHits() - 1);
							}
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(PoloUtil.MISS)) {
							if(session_match.getAwayPenaltiesMisses() > 0) {
								session_match.setAwayPenaltiesMisses(session_match.getAwayPenaltiesMisses() - 1);
							}
						}
					}
				}
			}
			
			
			
			new ObjectMapper().writeValue(new File(PoloUtil.POLO_DIRECTORY + PoloUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), 
					session_match);
			
			
			switch (session_selected_broadcaster) {
			case PoloUtil.POLO:
				session_khelo_india.ProcessGraphicOption(whatToProcess,session_match, session_clock,poloService,session_socket, 
						session_selected_scenes, valueToProcess);
				break;
			}
			//session_i_league.ProcessGraphicOption(whatToProcess,session_match, session_clock,footballService,session_socket, session_selected_scenes, valueToProcess);
			return JSONObject.fromObject(session_match).toString();
			
		case "RESET_PENALTY":
			
			session_match.setHomePenaltiesHits(0);
			session_match.setHomePenaltiesMisses(0);
			session_match.setAwayPenaltiesHits(0);
			session_match.setAwayPenaltiesMisses(0);

			new ObjectMapper().writeValue(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), 
					session_match);
			
			return JSONObject.fromObject(session_match).toString();
		
		case "NAMESUPER_GRAPHICS-OPTIONS": case "BUG_DB_GRAPHICS-OPTIONS": case "STAFF_GRAPHICS-OPTIONS": case "PROMO_GRAPHICS-OPTIONS": case "LTPROMO_GRAPHICS-OPTIONS":
		case "SCOREBUGPROMO_GRAPHICS-OPTIONS":	case "RESULT_PROMO_GRAPHICS-OPTIONS":
			switch (session_selected_broadcaster) {
			case PoloUtil.POLO:
				return session_khelo_india.ProcessGraphicOption(whatToProcess,session_match,session_clock, 
						poloService,session_socket, session_selected_scenes, valueToProcess).toString();
			}
			
			return JSONObject.fromObject(session_match).toString();
			
		case PoloUtil.REPLACE:
			
			Player store_player = new Player();
			for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
				
				if(session_match.getHomeSquad().get(i).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[1])) {
					store_player = session_match.getHomeSquad().get(i);
					session_match.getHomeSquad().remove(i);
					for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
						if(session_match.getHomeSubstitutes().get(j).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
							session_match.getHomeSubstitutes().remove(j);
							session_match.getHomeSubstitutes().add(j, store_player);
						}
					}
				}
			}
			for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
				
				if(session_match.getAwaySquad().get(i).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[1])) {
					store_player = session_match.getAwaySquad().get(i);
					session_match.getAwaySquad().remove(i);
					for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
						if(session_match.getAwaySubstitutes().get(j).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
							session_match.getAwaySubstitutes().remove(j);
							session_match.getAwaySubstitutes().add(j, store_player);
						}
					}
				}
			}
			
			session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, 0, session_match.getClock().getMatchHalves(), 
					0,whatToProcess, "replace", Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[2]),0));
			
			new ObjectMapper().writeValue(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), 
					session_match);
			new ObjectMapper().writeValue(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.EVENT_DIRECTORY + session_match.getMatchFileName()), 
					session_event);
			
			session_match.setEvents(session_event.getEvents());
			
			return JSONObject.fromObject(session_match).toString();
			
		case "UPDATE_SCORE_USING_TXT":
			String goal = valueToProcess.split(",")[1];
			
			session_match.setHomeTeamScore(Integer.valueOf(goal.split("-")[0]));
			session_match.setAwayTeamScore(Integer.valueOf(goal.split("-")[1]));
			
			session_match = PoloFunctions.populateMatchVariables(poloService, session_match);
			new ObjectMapper().writeValue(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), 
					session_match);
			
			return JSONObject.fromObject(session_match).toString();
		
		case "LOG_GOALS":
			String which_team_goal = valueToProcess.split(",")[1];
			
			if (which_team_goal.split("_")[0].equalsIgnoreCase("home")) {
			    if (which_team_goal.split("_")[1].equalsIgnoreCase("increment")) {
			        session_match.setHomeTeamScore(session_match.getHomeTeamScore() + 1);
			    } else if (which_team_goal.split("_")[1].equalsIgnoreCase("decrement")) {
			        session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
			    }
			} else if (which_team_goal.split("_")[0].equalsIgnoreCase("away")) {
			    if (which_team_goal.split("_")[1].equalsIgnoreCase("increment")) {
			        session_match.setAwayTeamScore(session_match.getAwayTeamScore() + 1);
			    } else if (which_team_goal.split("_")[1].equalsIgnoreCase("decrement")) {
			        session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
			    }
			}

			
			session_match = PoloFunctions.populateMatchVariables(poloService, session_match);
			new ObjectMapper().writeValue(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), 
					session_match);
			
			return JSONObject.fromObject(session_match).toString();
				
		case "LOG_OVERWRITE_MATCH_SUBS":
			//System.out.println(valueToProcess);
			if(valueToProcess.contains(",")) {
				int overwrite_palyer_off_id = 0,overwrite_palyer_on_id = 0;
				Player sub_store_player = new Player();
				if(session_event.getEvents() != null) {
					for(Event evnt : session_event.getEvents()) {
						if(evnt.getEventNumber() == Integer.valueOf(valueToProcess.split(",")[1])) {
							if(Integer.valueOf(valueToProcess.split(",")[3]) > 0 && Integer.valueOf(valueToProcess.split(",")[2]) == 0) {
								overwrite_palyer_off_id = evnt.getOnPlayerId();
								overwrite_palyer_on_id = Integer.valueOf(valueToProcess.split(",")[3]);
								
								evnt.setOnPlayerId(overwrite_palyer_on_id);
								
							}else if(Integer.valueOf(valueToProcess.split(",")[3]) == 0 && Integer.valueOf(valueToProcess.split(",")[2]) > 0) {
								overwrite_palyer_off_id = Integer.valueOf(valueToProcess.split(",")[2]);
								overwrite_palyer_on_id = evnt.getOffPlayerId();
								
								evnt.setOffPlayerId(overwrite_palyer_off_id);
								
							}else if(Integer.valueOf(valueToProcess.split(",")[3]) > 0 && Integer.valueOf(valueToProcess.split(",")[2]) > 0) {
								overwrite_palyer_off_id = Integer.valueOf(valueToProcess.split(",")[2]);
								overwrite_palyer_on_id = Integer.valueOf(valueToProcess.split(",")[3]);
								
								evnt.setOnPlayerId(overwrite_palyer_on_id);
								evnt.setOffPlayerId(overwrite_palyer_off_id);
								
							}
							
						}
					}
				}
				//System.out.println("ON - " + overwrite_palyer_on_id + " OFF - " + overwrite_palyer_off_id);
				for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
					if(session_match.getHomeSquad().get(i).getPlayerId() == overwrite_palyer_off_id) {
						sub_store_player = session_match.getHomeSquad().get(i);
						session_match.getHomeSquad().remove(i);
						for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
							if(session_match.getHomeSubstitutes().get(j).getPlayerId() == overwrite_palyer_on_id) {
								session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
								session_match.getHomeSubstitutes().remove(j);
								session_match.getHomeSubstitutes().add(j, sub_store_player);
							}
						}
					}
				}
				for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
					if(session_match.getAwaySquad().get(i).getPlayerId() == overwrite_palyer_off_id) {
						sub_store_player = session_match.getAwaySquad().get(i);
						session_match.getAwaySquad().remove(i);
						for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
							if(session_match.getAwaySubstitutes().get(j).getPlayerId() == overwrite_palyer_on_id) {
								session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
								session_match.getAwaySubstitutes().remove(j);
								session_match.getAwaySubstitutes().add(j, sub_store_player);
							}
						}
					}
				}
			}
			

			new ObjectMapper().writeValue(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), 
					session_match);
			new ObjectMapper().writeValue(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.EVENT_DIRECTORY + session_match.getMatchFileName()), 
					session_event);
			
			session_match.setEvents(session_event.getEvents());
			
			return JSONObject.fromObject(session_match).toString();
				
		case PoloUtil.UNDO:

			if(valueToProcess.contains(",")) {
				if(session_event.getEvents() != null && Integer.valueOf(valueToProcess.split(",")[1]) <= session_event.getEvents().size()) {
					for(int iUndo=1;iUndo<=Integer.valueOf(valueToProcess.split(",")[1]);iUndo++) {

						this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
						
						switch (this_event.getEventLog().toUpperCase()) {
						case PoloUtil.REPLACE:
							ArrayList<Player> undo_store_player = new ArrayList<Player>();
							for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
								if(session_match.getHomeSquad().get(i).getPlayerId() == this_event.getOnPlayerId()) {
									undo_store_player.add(session_match.getHomeSquad().get(i));
									session_match.getHomeSquad().remove(i);
									for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
										if(session_match.getHomeSubstitutes().get(j).getPlayerId() == this_event.getOffPlayerId()) {
											session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
											session_match.getHomeSubstitutes().remove(j);
											session_match.getHomeSubstitutes().add(j, undo_store_player.get(0));
											undo_store_player.remove(0);
										}
									}
								}
							}
							for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
								if(session_match.getAwaySquad().get(i).getPlayerId() == this_event.getOnPlayerId()) {
									undo_store_player.add(session_match.getAwaySquad().get(i));
									session_match.getAwaySquad().remove(i);
									for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
										if(session_match.getAwaySubstitutes().get(j).getPlayerId() == this_event.getOffPlayerId()) {
											session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
											session_match.getAwaySubstitutes().remove(j);
											session_match.getAwaySubstitutes().add(j, undo_store_player.get(0));
											undo_store_player.remove(0);
										}
									}
								}
							}
							break;
						}
						session_event.getEvents().remove(this_event);
					}
				}
			}
			
			

			new ObjectMapper().writeValue(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), 
					session_match);
			new ObjectMapper().writeValue(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.EVENT_DIRECTORY + session_match.getMatchFileName()), 
					session_event);
			
			session_match.setEvents(session_event.getEvents());
			
			return JSONObject.fromObject(session_match).toString();
			
		case PoloUtil.LOAD_TEAMS:
			if(!valueToProcess.trim().isEmpty()) {
				
				session_match.setHomeTeam(poloService.getTeam(PoloUtil.TEAM, valueToProcess.split(",")[0]));
				session_match.setAwayTeam(poloService.getTeam(PoloUtil.TEAM, valueToProcess.split(",")[1]));
				
				session_match.setHomeSquad(poloService.getPlayers(PoloUtil.TEAM, valueToProcess.split(",")[0]));
				session_match.setAwaySquad(poloService.getPlayers(PoloUtil.TEAM, valueToProcess.split(",")[1]));
			}
			
			return JSONObject.fromObject(session_match).toString();

		case PoloUtil.LOAD_MATCH: case PoloUtil.LOAD_SETUP:

			session_match = new ObjectMapper().readValue
			(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.MATCHES_DIRECTORY+ valueToProcess), Match.class);
			
			switch (whatToProcess.toUpperCase()) {
			case PoloUtil.LOAD_MATCH:
				
				if(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.EVENT_DIRECTORY + valueToProcess).exists()) {
					session_event = new ObjectMapper().readValue(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.EVENT_DIRECTORY+
							valueToProcess), EventFile.class);
				} else {
					session_event = new EventFile();
					new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.EVENT_DIRECTORY + valueToProcess).createNewFile();
				}

				if(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.CLOCK_XML).exists()) {
					session_match.setClock((Clock) JAXBContext.newInstance(Clock.class).createUnmarshaller().unmarshal(
							new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + PoloUtil.CLOCK_XML)));
				} else {
					session_match.setClock(new Clock());
				}
				break;
				
			} 
			
			switch (whatToProcess.toUpperCase()) {
			case PoloUtil.LOAD_SETUP:
				session_match.setHomeOtherSquad(PoloFunctions.getPlayersFromDB(poloService, PoloUtil.HOME, session_match));
				session_match.setAwayOtherSquad(PoloFunctions.getPlayersFromDB(poloService, PoloUtil.AWAY, session_match));
				break;
			}
			session_match = PoloFunctions.populateMatchVariables(poloService,session_match);
			
			session_match.setEvents(session_event.getEvents());
			
			return JSONObject.fromObject(session_match).toString();			

		case "READ_CLOCK":
			File clockfile = new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + "CLOCK.json");
			
			if(new File(PoloUtil.SPORTS_DIRECTORY + PoloUtil.POLO_DIRECTORY + "CLOCK.json").exists() && clockfile.canRead()) {
				session_clock = new ObjectMapper().readValue(clockfile, Clock.class);
				session_match.setClock(session_clock);
				
				switch (session_selected_broadcaster) {
				case PoloUtil.POLO:
					session_khelo_india.updateScoreBug(session_selected_scenes,session_match, session_socket);
					break;
				}
			}
			return JSONObject.fromObject(session_match).toString();
						
		default:
			switch (session_selected_broadcaster) {
			case PoloUtil.POLO:
				session_khelo_india.ProcessGraphicOption(whatToProcess,session_match, session_clock,poloService,session_socket, 
						session_selected_scenes, valueToProcess);
				break;	
			}
			return JSONObject.fromObject(session_match).toString();
		}
	}
}
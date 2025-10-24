var match_data,home_team,away_team,home_team_name,away_team_name;
function millisToMinutesAndSeconds(millis) {
  var m = Math.floor(millis / 60000);
  var s = ((millis % 60000) / 1000).toFixed(0);
  return (m < 10 ? '0' + m : m) + ":" + (s < 10 ? '0' + s :s);
}
function displayMatchTime() {
	processPoloProcedures('READ_CLOCK',null);
}
function getPlayerMatchStats(playerId){
	var value='';
	if(match_data.events != null && match_data.events.length > 0)
	{
		for(var k = 0; k < match_data.events.length; k++){
			if(match_data.events[k].eventPlayerId == playerId){
				if(match_data.events[k].eventType == 'yellow'){
					value = value + 'Y';
				}else if(match_data.events[k].eventType == 'red'){
					value = value + 'R';
				}
			}else if(match_data.events[k].eventPlayerId == 0){
				if(match_data.events[k].offPlayerId == playerId){
					value = '(OFF) ' + value;
				}else if(match_data.events[k].onPlayerId == playerId){
					value = '(ON) ' + value;
				}
			}
			else{
				value = value + '';
			}
		}
	}else{
		value = value + '';
	}
	return value ;
}
function processWaitingButtonSpinner(whatToProcess) 
{
	switch (whatToProcess) {
	case 'START_WAIT_TIMER': 
		$('.spinner-border').show();
		$(':button').prop('disabled', true);
		break;	
	case 'END_WAIT_TIMER': 
		$('.spinner-border').hide();
		$(':button').prop('disabled', false);
		break;
	}
}
function afterPageLoad(whichPageHasLoaded)
{
	switch (whichPageHasLoaded) {
	case 'SETUP':
		$('#homeTeamId').select2();
		$('#awayTeamId').select2();
		$('#homeTeamFormationId').select2();
		$('#awayTeamFormationId').select2();
		$('#homeTeamJerseyColor').select2();
		$('#awayTeamJerseyColor').select2();
		$('#homeTeamGKJerseyColor').select2();
		$('#awayTeamGKJerseyColor').select2();
		break;
	case 'MATCH':
		addItemsToList('LOAD_EVENTS',null);
		break;
	}
}
function initialiseForm(whatToProcess, dataToProcess)
{
	switch (whatToProcess) {
	case 'TIME':
	
		if(match_data) {
			if(document.getElementById('match_time_hdr')) {
				document.getElementById('match_time_hdr').innerHTML = 'MATCH TIME : ' + 
					millisToMinutesAndSeconds(match_data.clock.matchTotalMilliSeconds);
			}
		}
		break;
	
/*	case 'MATCH':
	
		if(match_data) {
			document.getElementById('select_match_halves').value = match_data.clock.matchHalves;
		} else {
			document.getElementById('select_match_halves').selectedIndex = 0;
		}
		break;*/
		
	case 'SETUP':
		
		if(dataToProcess) {
			document.getElementById('matchFileName').value = dataToProcess.matchFileName;
			document.getElementById('tournament').value = dataToProcess.tournament;
			document.getElementById('matchIdent').value = dataToProcess.matchIdent;
			document.getElementById('matchId').value = dataToProcess.matchId;
			document.getElementById('groundId').value = dataToProcess.groundId;
			document.getElementById('homeSubstitutesPerTeam').value = dataToProcess.homeSubstitutesPerTeam;
			document.getElementById('awaySubstitutesPerTeam').value = dataToProcess.awaySubstitutesPerTeam;
			document.getElementById('homeTeamId').value = dataToProcess.homeTeamId;
			document.getElementById('awayTeamId').value = dataToProcess.awayTeamId;
			/*document.getElementById('homeTeamFormationId').value = dataToProcess.homeTeamFormationId;
			document.getElementById('awayTeamFormationId').value = dataToProcess.awayTeamFormationId;
			document.getElementById('homeTeamJerseyColor').value = dataToProcess.homeTeamJerseyColor;
			document.getElementById('awayTeamJerseyColor').value = dataToProcess.awayTeamJerseyColor;
			document.getElementById('homeTeamGKJerseyColor').value = dataToProcess.homeTeamGKJerseyColor;
			document.getElementById('awayTeamGKJerseyColor').value = dataToProcess.awayTeamGKJerseyColor;*/
			addItemsToList('LOAD_TEAMS',dataToProcess);
			document.getElementById('save_match_div').style.display = '';
		} else {
			document.getElementById('matchFileName').value = '';
			document.getElementById('tournament').value = '';
			document.getElementById('matchIdent').value = '';
			document.getElementById('matchId').value = '';
			document.getElementById('groundId').selectedIndex = 0;
			document.getElementById('homeSubstitutesPerTeam').selectedIndex = 0;
			document.getElementById('awaySubstitutesPerTeam').selectedIndex = 0;
			document.getElementById('homeTeamId').selectedIndex = 0;
			document.getElementById('awayTeamId').selectedIndex = 1;
			document.getElementById('homeTeamFormationId').selectedIndex = 0;
			document.getElementById('awayTeamFormationId').selectedIndex = 1;
			document.getElementById('homeTeamJerseyColor').selectedIndex = 0;
			document.getElementById('awayTeamJerseyColor').selectedIndex = 1;
			document.getElementById('homeTeamGKJerseyColor').selectedIndex = 0;
			document.getElementById('awayTeamGKJerseyColor').selectedIndex = 1;
			addItemsToList('LOAD_TEAMS',null);
			document.getElementById('save_match_div').style.display = 'none';
		}
		$('#homeTeamId').prop('selectedIndex', document.getElementById('homeTeamId').options.selectedIndex).change();
		$('#awayTeamId').prop('selectedIndex', document.getElementById('awayTeamId').options.selectedIndex).change();
		
	/*	$('#homeTeamFormationId').prop('selectedIndex', document.getElementById('homeTeamFormationId').options.selectedIndex).change();
		$('#awayTeamFormationId').prop('selectedIndex', document.getElementById('awayTeamFormationId').options.selectedIndex).change();
		
		$('#homeTeamJerseyColor').prop('selectedIndex', document.getElementById('homeTeamJerseyColor').options.selectedIndex).change();
		$('#awayTeamJerseyColor').prop('selectedIndex', document.getElementById('awayTeamJerseyColor').options.selectedIndex).change();
		
		$('#homeTeamGKJerseyColor').prop('selectedIndex', document.getElementById('homeTeamGKJerseyColor').options.selectedIndex).change();
		$('#awayTeamGKJerseyColor').prop('selectedIndex', document.getElementById('awayTeamGKJerseyColor').options.selectedIndex).change();*/
		break;
	}
}
function uploadFormDataToSessionObjects(whatToProcess)
{
	var formData = new FormData();
	var url_path;

	$('input, select, textarea').each(
		function(index){  
			if($(this).is("select")) {
				formData.append($(this).attr('id'),$('#' + $(this).attr('id') + ' option:selected').val());  
			} else {
				formData.append($(this).attr('id'),$(this).val());  
			}	
		}
	);
	
	switch(whatToProcess.toUpperCase()) {
	case 'RESET_MATCH':
		url_path = 'reset_and_upload_match_setup_data';
		break;
	case 'SAVE_MATCH':
		url_path = 'upload_match_setup_data';
		break;
	}
	
	$.ajax({    
		headers: {'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')},
        url : url_path,     
        data : formData,
        cache: false,
        contentType: false,
        processData: false,
        type: 'POST',     
        success : function(data) {

        	switch(whatToProcess.toUpperCase()) {
			case 'RESET_MATCH_BEFORE_SETUP_MATCH':
        		processWaitingButtonSpinner('END_WAIT_TIMER');
        		break;
        	case 'RESET_MATCH':
        		alert('Match has been reset');
        		processWaitingButtonSpinner('END_WAIT_TIMER');
        		break;
        	case 'SAVE_MATCH':
        		document.setup_form.method = 'post';
        		document.setup_form.action = 'back_to_match';
        	   	document.setup_form.submit();
        		break;
        	}
        	
        },    
        error : function(e) {    
       	 	console.log('Error occured in uploadFormDataToSessionObjects with error description = ' + e);     
        }    
    });		
	
}
function processUserSelectionData(whatToProcess,dataToProcess){
	
	switch (whatToProcess) {
	case 'LOGGER_FORM_KEYPRESS':
		switch (dataToProcess) {
		case ' '://Space
			processPoloProcedures('CLEAR-ALL');
			break;	
		case '=':
			processPoloProcedures('ANIMATE-OUT-SCOREBUG');
			break;
		case '-': '189'
			if(confirm('It will Also Delete Your Preview from Directory...\r\n\r\n Are You Sure To Animate Out?') == true){
				processPoloProcedures('ANIMATE-OUT');
			}
			break;
			
		case 'F1':
			processPoloProcedures('POPULATE-SCOREBUG');
			break;
		case 'F2':
			processPoloProcedures('POPULATE-SCOREBUG_WITHTIME');
			break;
		case 'F7':
			processPoloProcedures('POPULATE-L3-SCOREUPDATE');
			break;	
			
		case 'F3': case 'F6':
			$("#select_event_div").hide();
			$("#select_set_div").hide();
			$("#match_configuration").hide();
			$("#polo_div").hide();
			
			switch (dataToProcess){
			case 'F3':
				addItemsToList('BUG_TEXT-OPTIONS',null); 
				break;
			case 'F6':
				processPoloProcedures('BUG_DB_GRAPHICS-OPTIONS');
				break;
			}
			break;
		}
		break;
	}
}
function processUserSelection(whichInput)
{	
	var error_msg = '';
	switch ($(whichInput).attr('name')) {
	case 'overwrite_match_stats_index':

		document.getElementById('overwrite_match_stats_player_id').selectedIndex = 0;
		document.getElementById('overwrite_match_stats_type').value = '';
		document.getElementById('overwrite_match_stats_total_seconds').value = '';
	
		match_data.matchStats.forEach(function(ms,index,arr){
			if ($('#overwrite_match_stats_index option:selected').val() == ms.statsId) {
				document.getElementById('overwrite_match_stats_player_id').value = ms.playerId;
				document.getElementById('overwrite_match_stats_type').value = ms.statsType;
				document.getElementById('overwrite_match_stats_total_seconds').value = ms.totalMatchSeconds;
			}
		});

		break;

	case 'load_scene_btn':
	
		/*if(checkEmpty($('#vizIPAddress'),'IP Address Blank') == false
			|| checkEmpty($('#vizPortNumber'),'Port Number Blank') == false) {
			return false;
		}*/
    
	  	document.initialise_form.submit();
		break;
	
	case 'cancel_graphics_btn':
		$('#select_graphic_options_div').empty();
		document.getElementById('select_graphic_options_div').style.display = 'none';
		$("#select_event_div").show();
		$("#select_set_div").show();
		$("#match_configuration").show();
		$("#polo_div").show();
		break;
	case 'selectedBroadcaster':
		switch ($('#selectedBroadcaster :selected').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY':
			$('#vizPortNumber').attr('value','1980');
			$('label[for=vizScene], input#vizScene').hide();
			$('label[for=which_scene], select#which_scene').hide();
			$('label[for=which_layer], select#which_layer').hide();
			break;
		case 'VIZ_SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
			$('#vizPortNumber').attr('value','6100');
			$('label[for=vizScene], input#vizScene').hide();
			$('label[for=which_scene], select#which_scene').hide();
			$('label[for=which_layer], select#which_layer').hide();
			break;	
		}
		break;
	case 'homePlayers': case 'awayPlayers':
		$('#selected_player_name').html(whichInput.innerHTML);
		$('#selected_player_id').val(whichInput.value);
		document.getElementById('select_event_div').style.display = '';
		break;
		
	case 'log_match_subs_overwrite_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		switch ($(whichInput).attr('name')) {
		case 'log_match_subs_overwrite_btn':
			processPoloProcedures('LOG_OVERWRITE_MATCH_SUBS',whichInput);
			break;
		}
		break;
	case 'number_of_undo_txt':
		if(whichInput.value < 0 && whichInput.value > match_data.events.length) {
			alert('Number of undos is invalid.\r\n Must be a positive number and less than the number of events available [' + match_data.events.length + ']');
			whichInput.selected = true;
			return false;
		}
		break;
	case 'selectTeam': case 'selectCaptianWicketKeeper':
		addItemsToList('POPULATE-PLAYER',match_data);
		break;
	case 'select_existing_polo_matches':
		if(whichInput.value.toLowerCase().includes('new_match')) {
			initialiseForm('SETUP',null);
		} else {
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processPoloProcedures('LOAD_SETUP',$('#select_existing_polo_matches option:selected'));
		}
		break;
	case 'log_undo_btn':
		if(match_data.events.length > 0) {
			if($('#number_of_undo_txt').val() > match_data.events.length) {
				if(confirm('Number of undo [' + $('#number_of_undo_txt').val() + '] is bigger than number of events [' 
						+ match_data.events.length + ']. We will make both of them similiar') == false) {
					return false;
				}
				$('#number_of_undo_txt').val(match_data.events.length);
			}
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processPoloProcedures('UNDO',$('#number_of_undo_txt'));
		} else {
			alert('No events found');
		}
		break;
	case 'log_replace_btn':
		processPoloProcedures('REPLACE',match_data);
		break;
		
	case 'undo_set':
		 processPoloProcedures('UNDO_SET');
		break;
		
	case 'Overwrite_set':
		addItemsToList('OVERWRITE_SET_OPTIONS',match_data);
		break;
	case 'log_setOverwrite_btn':
		  processPoloProcedures('OVERWRITE_SET');
		  $('#select_player_div').hide();
		break;
		
	case 'start_set_btn':
		match_data.sets.forEach(function(set){
			if(set.set_status.toLowerCase() == 'start') {
				error_msg = 'Set number ' + set.set_number + ' already in play. End this set first before starting a new one';
			}
		});
		if(error_msg) {
			alert(error_msg);
			return false;			
		} else {
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processPoloProcedures('LOG_SET','START');
		}
		break;
	case 'end_set_btn': case 'reset_set_btn':
		error_msg = 'Cannot find any started set. Please start a set first';
		match_data.sets.forEach(function(set){
			if(set.set_status.toLowerCase() == 'start') {
				error_msg = '';
			}
		});
		if(error_msg) {
			alert(error_msg);
			return false;			
		} else {
			switch ($(whichInput).attr('name')) {
			case 'end_set_btn':
				processVariousStats('CHECK-END_SET-WINNER',match_data); 
				if(confirm('Confirm ' + $('#select_set_winner option:selected').text() + ' wins this set?')) {
					processWaitingButtonSpinner('START_WAIT_TIMER');
					processPoloProcedures('LOG_SET','END');
				}
				break;
			case 'reset_set_btn':
				if(confirm('Do you really wish to RESET this set?')) {
					processWaitingButtonSpinner('START_WAIT_TIMER');
					processPoloProcedures('LOG_SET','RESET');
				}
				break;
			}
		}
		break;
		
	case 'cancel_event_btn_evn':
		document.getElementById('select_player_div').style.display = 'none';
		break;
		
	case 'cancel_match_setup_btn':
		document.setup_form.method = 'post';
		document.setup_form.action = 'back_to_match';
	   	document.setup_form.submit();
		break;
	case 'matchFileName':
		if(document.getElementById('matchFileName').value) {
			document.getElementById('matchFileName').value = 
				document.getElementById('matchFileName').value.replace('.json','') + '.json';
		}
		break;
	case 'save_match_btn': case 'reset_match_btn':
		switch ($(whichInput).attr('name')) {
		case 'reset_match_btn':
	    	if (confirm('The setup selections of this match will be retained ' +
	    			'but the match data will be deleted permanently. Are you sure, you want to RESET this match?') == false) {
	    		return false;
	    	}
			break;
		}
		if (!checkEmpty(document.getElementById('matchFileName'),'Match Name')) {
			return false;
		} 
		if($('#homeTeamId option:selected').val() == $('#awayTeamId option:selected').val()) {
			alert('Both teams cannot be same. Please choose different home and away team');
			return false;
		}
		for(var tm=1;tm<=2;tm++) {
			for(var i=1;i<4;i++) {
				for(var j=i+1;j<=4;j++) {
					if(tm == 1) {
						if(document.getElementById('homePlayer_' + i).selectedIndex == document.getElementById('homePlayer_' + j).selectedIndex) {
							alert(document.getElementById('homePlayer_' + i).options[
								document.getElementById('homePlayer_' + i).selectedIndex].text.toUpperCase() + 
								' selected multiple times for HOME team');
							return false;
						}
					} else {
						if(document.getElementById('awayPlayer_' + i).selectedIndex == document.getElementById('awayPlayer_' + j).selectedIndex) {
							alert(document.getElementById('awayPlayer_' + i).options[
								document.getElementById('awayPlayer_' + i).selectedIndex].text.toUpperCase() + 
								' selected multiple times for AWAY team');
							return false;
						}
					}
				}
			}
		}
		switch ($(whichInput).attr('name')) {
		case 'save_match_btn': 
			uploadFormDataToSessionObjects('SAVE_MATCH');
			break;
		case 'reset_match_btn':
			processWaitingButtonSpinner('START_WAIT_TIMER');
			uploadFormDataToSessionObjects('RESET_MATCH');
			break;
		}
		break;
	case 'load_default_team_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		if($('#homeTeamId option:selected').val() == $('#awayTeamId option:selected').val()) {
			alert('Both teams cannot be same. Please choose different home and away team');
    		processWaitingButtonSpinner('END_WAIT_TIMER');
			return false;
		}
		processPoloProcedures('LOAD_TEAMS',whichInput);
		document.getElementById('save_match_div').style.display = '';
		break;
	case 'setup_match_btn':
		document.polo_form.method = 'post';
		document.polo_form.action = 'setup';
	   	document.polo_form.submit();
	   	processWaitingButtonSpinner('START_WAIT_TIMER');
		break;
	case 'load_match_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		processPoloProcedures('LOAD_MATCH',$('#select_polo_matches option:selected'));
		break;
	case 'log_event_btn':
		if(whichInput.id.toLowerCase() == 'undo') {
    		if(match_data == null || match_data.events.length <= 0) {
    			alert('No events found to perform undoes');
    			return false;
    		}
    		addItemsToList('LOAD_UNDO',match_data);
		} else if(whichInput.id.toLowerCase() == 'replace'){
			addItemsToList('LOAD_REPLACE',match_data);
			addItemsToList('POPULATE-OFF_PLAYER',match_data);
			addItemsToList('POPULATE-ON_PLAYER',match_data);
		} else if(whichInput.id.toLowerCase() == 'penalty'){
			processPoloProcedures('RESET_PENALTY', null);
			addItemsToList('LOAD_PENALTY',match_data);
		}else {
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processPoloProcedures('LOG_EVENT',whichInput);
		}
		break;
	case 'Home_goal_btn':
		processPoloProcedures('LOG_EVENT',whichInput);
		break;	
	case 'cancel_undo_btn': case 'cancel_overwrite_btn': case 'cancel_event_btn': case 'cancel_replace_btn': case 'cancel_penalty_btn':
		document.getElementById('select_event_div').style.display = 'none';
		addItemsToList('LOAD_EVENTS',match_data); 
		processWaitingButtonSpinner('END_WAIT_TIMER');
		break;
	case 'select_teams':
		addItemsToList('POPULATE-OFF_PLAYER',match_data);
		addItemsToList('POPULATE-ON_PLAYER',match_data);
		break;
	
	case 'populate_bug_db_btn': case 'populate_bug_freetext_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		switch ($(whichInput).attr('name')) {
		case 'populate_bug_db_btn':
			processPoloProcedures('POPULATE-L3-BUG-DB');
			break;
		case 'populate_bug_freetext_btn':
			processPoloProcedures('POPULATE-L3-BUG-FREETEXT');
			break;
		}
		break;
	
	case 'homeScore': case 'awayScore':
		processPoloProcedures('UPDATE_SCORE_USING_TXT',"");
		break;
		
	default:
		switch ($(whichInput).attr('id')) {
		case 'home_increment_score_btn': case 'away_increment_score_btn': case 'home_decrement_score_btn': case 'away_decrement_score_btn':
			processPoloProcedures('LOG_GOALS',whichInput);
			break;
		case 'overwrite_teams_total': case 'overwrite_match_time': 
			addItemsToList('LOAD_' + $(whichInput).attr('id').toUpperCase(),null);
			document.getElementById('select_event_div').style.display = '';
			break;
		default:
			if($(whichInput).attr('id').includes('_btn') && $(whichInput).attr('id').split('_').length >= 4) {
	    		switch ($(whichInput).attr('id').split('_')[1]) {
	    		case 'increment':
	    			$('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
						+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val(
						parseInt($('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
						+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val()) + 1
					);
					break;
	    		case 'decrement':
					if(parseInt($('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
						+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val()) > 0) {
		    			
						$('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
							+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val(
							parseInt($('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
							+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val()) - 1
						);
						
					}
					break;
				}				
				processWaitingButtonSpinner('START_WAIT_TIMER');
				processPoloProcedures('LOG_STAT',whichInput);
			}
			break;
		}
		break;
	}
}
function processVariousStats(whatToProcess, whichInput)
{
	switch(whatToProcess){
	case 'CHECK-END_SET-WINNER':
		match_data.sets.forEach(function(set){
			if(set.set_status.toLowerCase() == 'start') {
				if(set.homeScore > set.awayScore){
					$('#select_set_winner').val('home');
				}else if(set.awayScore > set.homeScore){
					$('#select_set_winner').val('away');
				}
			}
		});
		break;	
	}
}

function processPoloProcedures(whatToProcess, whichInput)
{
	var value_to_process; 
	var prev_match_data = match_data;
	
	switch(whatToProcess) {
	case 'READ_CLOCK':
		//initialiseForm('UPDATE-MATCH-ON-OUTPUT-FORM');
		valueToProcess = $('#matchFileTimeStamp').val();
		//alert("1");
		break;
		
	case "OVERWRITE_SET":
			value_to_process = document.getElementById("select_set").value + "," + document.getElementById("select_Winner").value+","+
			+ document.getElementById("select_Homescore").value+ ","+document.getElementById("select_Awayscore").value;
		break;
			
	case 'UPDATE_SCORE_USING_TXT':
		value_to_process = document.getElementById("homeScore").value + "-" + document.getElementById("awayScore").value;
		break;
			
	case 'LOG_STAT': case 'LOG_GOALS':
		value_to_process = whichInput.id;
		break;
	case 'LOG_OVERWRITE_MATCH_SUBS': 
		switch (whatToProcess) {
		case 'LOG_OVERWRITE_MATCH_SUBS':
			value_to_process = $('#overwrite_match_sub_index option:selected').val() + ',' + $('#overwrite_match_player_id option:selected').val()
				+ ',' + $('#overwrite_match_subs_player_id option:selected').val();
			break;
		}
		break;	
	case 'LOAD_TEAMS':
		value_to_process = $('#homeTeamId option:selected').val() + ',' + $('#awayTeamId option:selected').val();
		break;
		
	case 'LOG_SET':
		if(whichInput == 'START' || whichInput == 'RESET') {
			value_to_process = whichInput;
		}else if(whichInput == 'END') {
			switch(whatToProcess) {
			case 'LOG_SET': 
				value_to_process = whichInput + ',' + $('#select_set_winner option:selected').val();
				break;
			}
		}
		break;

	case 'LOAD_MATCH': case 'LOAD_SETUP':
		value_to_process = whichInput.val();
		break;
		
	case 'LOG_EVENT':
		value_to_process =  whichInput.id + ',' + $('#selected_player_id').val();
		break;
	
	case 'UNDO':
		value_to_process = $('#number_of_undo_txt').val();
		break;
	case 'REPLACE':
		value_to_process = $('#select_player option:selected').val() + ',' + $('#select_sub_player option:selected').val();
		break;
		
	case 'POPULATE-L3-BUG-FREETEXT':
		switch ($('#selectedBroadcaster').val()) {
			case 'POLO':
				value_to_process = $('#selectFreeText').val();
				break;
		}
		break;
	
	case 'POPULATE-L3-BUG-DB':
		switch ($('#selectedBroadcaster').val()) {
			case 'POLO':
				value_to_process = $('#selectBugdb option:selected').val() ;
				break;
		}
		break;
	}
		
	
	if(match_data){
		if(whatToProcess != "LOAD_TEAMS"){
			value_to_process = match_data.matchFileName + ',' + value_to_process;
		}
	}

	$.ajax({    
        type : 'Get',     
        url : 'processPoloProcedures.html',     
        data : 'whatToProcess=' + whatToProcess + '&valueToProcess=' + value_to_process, 
        dataType : 'json',
        success : function(data) {
			match_data = data;
			//alert(whatToProcess);
        	switch(whatToProcess) {
			case 'READ_CLOCK':
				if(match_data.clock) {
					if(document.getElementById('match_time_hdr')) {
						document.getElementById('match_time_hdr').innerHTML = 'MATCH TIME : ' + 
							millisToMinutesAndSeconds(match_data.clock.matchTotalMilliSeconds);
					}
				}
				
				if(data){
					if($('#matchFileTimeStamp').val() != data.matchFileTimeStamp) {
						document.getElementById('matchFileTimeStamp').value = data.matchFileTimeStamp;
						session_match = data;
						addItemsToList('LOAD_MATCH',data);
						addItemsToList('LOAD_EVENTS',data);
						addItemsToList('LOAD_SET',data);
						document.getElementById('select_event_div').style.display = 'none';
					}
				}
				break;
				
			case 'POPULATE-SCOREBUG': case 'POPULATE-SCOREBUG_WITHTIME': case 'POPULATE-L3-SCOREUPDATE': case 'POPULATE-L3-BUG-DB':
			case 'POPULATE-L3-BUG-FREETEXT':
				if(confirm('Animate In?') == true){
					switch(whatToProcess){
					case 'POPULATE-SCOREBUG':
						processPoloProcedures('ANIMATE-IN-SCOREBUG');				
						break;
					case 'POPULATE-SCOREBUG_WITHTIME':
						processPoloProcedures('ANIMATE-IN-SCOREBUG_WITHTIME');				
						break;
					case 'POPULATE-L3-BUG-DB':
						processPoloProcedures('ANIMATE-IN-BUG-DB');
						break;
					case 'POPULATE-L3-BUG-FREETEXT':
						processPoloProcedures('ANIMATE-IN-BUG-FREETEXT');
						break;
					case 'POPULATE-L3-SCOREUPDATE':
						processPoloProcedures('ANIMATE-IN-SCOREUPDATE');
						break;
					}
				}
				break;
				
			case 'BUG_DB_GRAPHICS-OPTIONS':
				addItemsToList('BUG_DB-OPTIONS',data);
				addItemsToList('POPULATE-BUG-SCENE',data);
				break;
								
    		case 'LOG_OVERWRITE_MATCH_SUBS': case 'UNDO': case 'REPLACE': case 'LOG_GOALS': case 'UPDATE_SCORE_USING_TXT':
    		case 'LOG_SET': case 'OVERWRITE_SET': case 'UNDO_SET':
        		addItemsToList('LOAD_MATCH',data);
				addItemsToList('LOAD_EVENTS',data);
				addItemsToList('LOAD_SET',data);
				document.getElementById('select_event_div').style.display = 'none';
        		break;
        	case 'LOAD_TEAMS':
        		addItemsToList('LOAD_TEAMS',data);
        		break;	
			case 'LOG_EVENT': case 'LOAD_MATCH':
        		addItemsToList('LOAD_MATCH',data);
        		addItemsToList('LOAD_SET',data);
	        	switch(whatToProcess) {
	        	case 'LOAD_MATCH':
	        		document.getElementById('select_set_div').style.display = '';
					document.getElementById('polo_div').style.display = '';
					document.getElementById('select_event_div').style.display = 'none';
					setInterval(displayMatchTime, 500);
					break;
				}
        		break;
        	case 'LOAD_SETUP':
        		initialiseForm('SETUP',data);
        		break;
        	}
    		processWaitingButtonSpinner('END_WAIT_TIMER');
	    },    
	    error : function(e) {    
	  	 	console.log('Error occured in ' + whatToProcess + ' with error description = ' + e);     
	    }    
	});
}
function addItemsToList(whatToProcess, dataToProcess)
{
	var max_cols,div,linkDiv,anchor,row,cell,header_text,select,option,tr,th,thead,text,table,tbody,playerName,api_value_home,api_value_away;
	var cellCount = 0;
	var addSelect = false;
	
	switch (whatToProcess) {
	case 'POPULATE-PLAYER':
		$('#selectPlayer').empty();
		if(match_data.homeTeamId ==  $('#selectTeam option:selected').val()){
			match_data.homeSquad.forEach(function(hs,index,arr){
				$('#selectPlayer').append(
					$(document.createElement('option')).prop({
	                value: hs.playerId,
	                text: hs.jersey_number + ' - ' + hs.full_name
		        }))					
			});
			match_data.homeSubstitutes.forEach(function(hsub,index,arr){
				$('#selectPlayer').append(
					$(document.createElement('option')).prop({
					value: hsub.playerId,
					text: hsub.jersey_number + ' - ' + hsub.full_name + ' (SUB)'
				}))
			});
		}
		else {
			match_data.awaySquad.forEach(function(as,index,arr){
				$('#selectPlayer').append(
					$(document.createElement('option')).prop({
	                value: as.playerId,
	                text: as.jersey_number + ' - ' + as.full_name
		        }))					
			});
			match_data.awaySubstitutes.forEach(function(asub,index,arr){
				$('#selectPlayer').append(
					$(document.createElement('option')).prop({
					value: asub.playerId,
					text: asub.jersey_number + ' - ' + asub.full_name + ' (SUB)'
				}))
			});
		}
		break;
		
	case "OVERWRITE_SET_OPTIONS":
		$('#select_player_div').empty();
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
		
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);
		
		let selection = document.createElement('select');
		selection.id = 'select_set';
		selection.name = selection.id;
		
		dataToProcess.sets.forEach(value => {
		    if (value.set_status === "END") {  // only include sets with status "end"
		        const option = document.createElement('option');
		        option.value = value.set_number;
		        option.text = "SET - " + value.set_number;
		        selection.appendChild(option);
		    }
		});
		header_text = document.createElement('label');
		header_text.innerHTML = 'SET NUMBER: ';
		header_text.htmlFor = selection.id;
		selection.setAttribute('onchange', "processUserSelection(this)");
		
		row.insertCell(0).appendChild(header_text).appendChild(selection);
		
		select = document.createElement('select');
		select.id = 'select_Winner';
		select.name = select.id;
		
		option = document.createElement('option');
		option.value = dataToProcess.homeTeamId;
		option.text = dataToProcess.homeTeam.teamName1;
		select.appendChild(option);
		
		option = document.createElement('option');
		option.value = dataToProcess.awayTeamId;
		option.text = dataToProcess.awayTeam.teamName1;
		select.appendChild(option);
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'SELECT WINNER: ';
		header_text.htmlFor = select.id;
		row.insertCell(1).appendChild(header_text).appendChild(select);
		
		let homeInput = document.createElement('input');
		homeInput.type = 'text';
		homeInput.id = 'select_Homescore';
		homeInput.name = homeInput.id;
		homeInput.style = 'width:35%; height:50px; text-align:center; font-size:24px;';
		homeInput.value = '0';
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'HOME SCORE: ';
		header_text.htmlFor = homeInput.id;
		row.insertCell(2).appendChild(header_text).appendChild(homeInput);
		
		let awayInput = document.createElement('input');
		awayInput.type = 'text';
		awayInput.id = 'select_Awayscore';
		awayInput.name = awayInput.id;
		awayInput.style = 'width:35%; height:50px; text-align:center; font-size:24px;';
		awayInput.value = '0';
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'AWAY SCORE: ';
		header_text.htmlFor = awayInput.id;
		awayInput.setAttribute('onchange', "processUserSelection(this)");
		row.insertCell(3).appendChild(header_text).appendChild(awayInput);
		
		selection.addEventListener('change', function() {
		    var set = dataToProcess.sets.find(set => set.set_number === parseInt(this.value, 10));
		    
		    if (set) {
		        $('#select_Homescore').val(set.homeScore);
		        $('#select_Awayscore').val(set.awayScore);
		        
		        // Automatically select the winner based on setWinner
		        if (set.set_winner === "home") {
		            $('#select_Winner').val(dataToProcess.homeTeamId);
		        } else if (set.set_winner === "away") {
		            $('#select_Winner').val(dataToProcess.awayTeamId);
		        }
		    }
		    $('#select_Homescore').trigger('change');
		    $('#select_Awayscore').trigger('change');
		    $('#select_Winner').trigger('change');
		});
		
		div = document.createElement('div');
		
		let replaceBtn = document.createElement('input');
		replaceBtn.type = 'button';
		replaceBtn.name = 'log_setOverwrite_btn';
		replaceBtn.id = replaceBtn.name;
		replaceBtn.value = 'Save';
		replaceBtn.style.backgroundColor = "green";
		replaceBtn.style.color = "white";
		replaceBtn.style.border = '2px solid #d4af37';
		replaceBtn.style.padding = '10px 20px';
		replaceBtn.style.borderRadius = '5px';
		replaceBtn.style.boxShadow = '2px 4px 5px rgba(0, 0, 0, 0.2)';
		replaceBtn.setAttribute('onclick', 'processUserSelection(this);');
		
		div.append(replaceBtn);
		
		let cancelBtn = document.createElement('input');
		cancelBtn.type = 'button';
		cancelBtn.name = 'cancel_event_btn_evn';
		cancelBtn.id = cancelBtn.name;
		cancelBtn.value = 'Cancel';
		cancelBtn.style.backgroundColor = "red";
		cancelBtn.style.color = "white";
		cancelBtn.style.border = '2px solid #d4af37';
		cancelBtn.style.padding = '10px 20px';
		cancelBtn.style.borderRadius = '5px';
		cancelBtn.style.boxShadow = '2px 4px 5px rgba(0, 0, 0, 0.2)';
		cancelBtn.setAttribute('onclick', 'processUserSelection(this);');
		
		div.append(document.createElement('br'));
		div.append(cancelBtn);
		
		row.insertCell(4).appendChild(div);
		
		table.appendChild(tbody);
		document.getElementById('select_player_div').appendChild(table);
		document.getElementById('select_player_div').style.display = '';
				
		selection.selectedIndex = 0;
		selection.dispatchEvent(new Event('change'));
    	break;
		
	case 'BUG_DB-OPTIONS': case 'BUG_TEXT-OPTIONS':
	
		switch ($('#selectedBroadcaster').val().toUpperCase()) {
		case 'POLO':
			$('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
			
			switch(whatToProcess){	
			case 'BUG_DB-OPTIONS':
				select = document.createElement('select');
				select.style = 'width:130px';
				select.id = 'selectBugdb';
				select.name = select.id;
				
				dataToProcess.forEach(function(bug,index,arr1){
					option = document.createElement('option');
					option.value = bug.bugId;
					option.text = bug.prompt;
					select.appendChild(option);
				});
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;
			case 'BUG_TEXT-OPTIONS':
				select = document.createElement('input');
				select.type = "text";
				select.id = 'selectFreeText';
				select.value = '';
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;
			}
			
			switch (whatToProcess) {
			case 'BUG_DB-OPTIONS':
				option = document.createElement('input');
		    	option.type = 'button';
				option.name = 'populate_bug_db_btn';
			    option.value = 'Populate Bug';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				break;
			case 'BUG_TEXT-OPTIONS':
				option = document.createElement('input');
		    	option.type = 'button';
				option.name = 'populate_bug_freetext_btn';
			    option.value = 'Populate Bug Free Text';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				break;
			}
			
			option = document.createElement('input');
			option.type = 'button';
			option.name = 'cancel_graphics_btn';
			option.id = option.name;
			option.value = 'Cancel';
			option.setAttribute('onclick','processUserSelection(this)');
	
		    div.append(option);
		    row.insertCell(cellCount).appendChild(div);
		    cellCount = cellCount + 1;
		    
			document.getElementById('select_graphic_options_div').style.display = '';
			break;
		}
		break;	
	case 'LOAD_OVERWRITE_MATCH_SUB':
		$('#select_event_div').empty();

		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);
		
		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_sub_index';
		select.name = select.id;
		select.setAttribute('onchange',"processUserSelection(this)");
		if(match_data.events != null && match_data.events.length > 0){
			for(var i = 0; i < match_data.events.length; i++) {
				if(match_data.events[(match_data.events.length - 1) - i].eventType == 'replace') {
					option = document.createElement('option');
					option.value = match_data.events[(match_data.events.length - 1) - i].eventNumber;
				    option.text = match_data.events[(match_data.events.length - 1) - i].onPlayerId + ' - ' + match_data.events[(match_data.events.length - 1) - i].eventType;
				    select.appendChild(option);
				}
			}
		}
		header_text = document.createElement('label');
		header_text.innerHTML = 'SUBS';
		header_text.htmlFor = select.id;
		row.insertCell(0).appendChild(header_text).appendChild(select);
		
		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_player_id';
		
		option = document.createElement('option');
		option.value = '0';
		option.text = '';
		select.appendChild(option);
		
		match_data.homeSquad.forEach(function(hp,index,arr){
			option = document.createElement('option');
			option.value = hp.playerId;
		    option.text = hp.jersey_number + ' - ' + hp.full_name + ' ('+ match_data.homeTeam.teamName4 +')';
		    select.appendChild(option);
		});
		match_data.awaySquad.forEach(function(as,index,arr){
			option = document.createElement('option');
			option.value = as.playerId;
		    option.text = as.jersey_number + ' - ' + as.full_name + ' ('+ match_data.awayTeam.teamName4 +')';
		    select.appendChild(option);
		});
		
	    header_text = document.createElement('label');
		header_text.innerHTML = 'Player';
		header_text.htmlFor = select.id;
		row.insertCell(1).appendChild(header_text).appendChild(select);
		
		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_subs_player_id';
		
		option = document.createElement('option');
		option.value = '0';
		option.text = '';
		select.appendChild(option);
		
		match_data.homeSubstitutes.forEach(function(hsub,index,arr){
			option = document.createElement('option');
			option.value = hsub.playerId;
		    option.text = hsub.jersey_number + ' - ' + hsub.full_name + ' ('+ match_data.homeTeam.teamName4 +') - Sub';
		    select.appendChild(option);
		});
		match_data.awaySubstitutes.forEach(function(asub,index,arr){
			option = document.createElement('option');
			option.value = asub.playerId;
		    option.text = asub.jersey_number + ' - ' + asub.full_name + ' ('+ match_data.awayTeam.teamName4 +') - Sub';
		    select.appendChild(option);
		});
		
	    header_text = document.createElement('label');
		header_text.innerHTML = 'Sub Player';
		header_text.htmlFor = select.id;
		row.insertCell(2).appendChild(header_text).appendChild(select);
		
		option = document.createElement('input');
	    option.type = 'button';
	    option.name = 'log_match_subs_overwrite_btn';
	    option.value = 'Log Match Subs Overwrite';
	    option.id = option.name;
	    option.setAttribute('onclick','processUserSelection(this);');
	    
	    div = document.createElement('div');
	    div.append(option);

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_overwrite_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.append(document.createElement('br'));
	    div.append(option);
	    
	    row.insertCell(3).appendChild(div);

		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);
		break;
			
	case 'LOAD_TEAMS':

		//var otherSquadWithoutSubs, player_ids;
		
		$('#team_selection_div').empty();
		document.getElementById('team_selection_div').style.display = 'none';
		
		if (dataToProcess)
		{
			if(dataToProcess.homeSquad.length <=0 || dataToProcess.awaySquad.length <=0) {
				if(dataToProcess.homeSquad.length <=0) {
					alert(dataToProcess.homeTeam.teamName1 + ' has no players in the database');
				} else if(dataToProcess.awaySquad.length <=0) {
					alert(dataToProcess.awayTeam.teamName1 + ' has no players in the database');
				}
				return false;
			}
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			table.setAttribute('id', 'setup_teams');
			tr = document.createElement('tr');
			for (var j = 0; j <= 3; j++) {
			    th = document.createElement('th'); //column
			    switch (j) {
				case 0:
				    text = document.createTextNode(dataToProcess.homeTeam.teamName1); 
					break;
				case 1:
				    text = document.createTextNode(dataToProcess.homeTeam.teamName4 + ' captain/keeper'); 
					break;
				case 2:
				    text = document.createTextNode(dataToProcess.awayTeam.teamName1); 
					break;
				case 3:
				    text = document.createTextNode(dataToProcess.awayTeam.teamName4 + ' captain/keeper'); 
					break;
				}
			    th.appendChild(text);
			    tr.appendChild(th);
			}
			
			thead = document.createElement('thead');
			thead.appendChild(tr);
			table.appendChild(thead);

			tbody = document.createElement('tbody');
			max_cols = parseInt(3 + parseInt($('#homeSubstitutesPerTeam option:selected').val()));
			if(parseInt($('#homeSubstitutesPerTeam option:selected').val()) < parseInt($('#awaySubstitutesPerTeam option:selected').val())) {
				max_cols = parseInt(3 + parseInt($('#awaySubstitutesPerTeam option:selected').val()));
			}

			for(var i=0; i <= max_cols; i++) {
				row = tbody.insertRow(tbody.rows.length);
				for(var j=0; j<=3; j++) {
					addSelect = false;
					switch(j) {
					case 0: case 1:
						if(i <= parseInt(3 + parseInt($('#homeSubstitutesPerTeam option:selected').val()))) {
							addSelect = true;
						}
						break;
					case 2: case 3:
						if(i <= parseInt(3 + parseInt($('#awaySubstitutesPerTeam option:selected').val()))) {
							addSelect = true;
						}
						break;
					}

					if(addSelect == true) {
						select = document.createElement('select');
						select.style = 'width:75%';
						switch(j) {
						case 0: case 2:
							if(j==0) {
								select.name = 'selectHomePlayers';
								select.id = 'homePlayer_' + (i + 1);
							} else if(j==2) {
								select.name = 'selectAwayPlayers';
								select.id = 'awayPlayer_' + (i + 1);
							}
							if(j==0) {
								dataToProcess.homeSquad.forEach(function(hp,index,arr){
									option = document.createElement('option');
									option.value = hp.playerId;
								    option.text = hp.jersey_number + ' - ' + hp.full_name;
								    select.appendChild(option);
								});
								dataToProcess.homeSubstitutes.forEach(function(hp,index,arr){
									option = document.createElement('option');
									option.value = hp.playerId;
								    option.text = hp.jersey_number + ' - ' + hp.full_name;
								    select.appendChild(option);
								});
								dataToProcess.homeOtherSquad.forEach(function(hs,index,arr){
									option = document.createElement('option');
									option.value = hs.playerId;
								    option.text = hs.jersey_number + ' - ' + hs.full_name;
								    select.appendChild(option);
								});
								
							} else if (j==2) {
								
								dataToProcess.awaySquad.forEach(function(ap,index,arr){
									option = document.createElement('option');
									option.value = ap.playerId;
								    option.text = ap.jersey_number + ' - ' + ap.full_name;
								    select.appendChild(option);
								});
								dataToProcess.awaySubstitutes.forEach(function(ap,index,arr){
									option = document.createElement('option');
									option.value = ap.playerId;
								    option.text = ap.jersey_number + ' - ' + ap.full_name;
								    select.appendChild(option);
								});
								dataToProcess.awayOtherSquad.forEach(function(as,index,arr){
									option = document.createElement('option');
									option.value = as.playerId;
								    option.text = as.jersey_number + ' - ' + as.full_name;
								    select.appendChild(option);
								});
							}
						    select.selectedIndex = i;
							break;
						
						case 1: case 3:
						
							if(j==1) {
								select.name = 'selectHomeCaptainGoalKeeper';
								select.id = 'homeCaptainGoalKeeper_' + (i + 1);
							} else {
								select.name = 'selectAwayCaptainGoalKeeper';
								select.id = 'awayCaptainGoalKeeper_' + (i + 1);
							}
							for(var k=0; k<=3; k++) {
								option = document.createElement('option');
								switch (k) {
								case 0:
									option.value = '';
								    option.text = '';
									break;
								case 1:
									option.value = 'captain';
								    option.text = 'Captain';
									break;
								case 2:
									option.value = 'goal_keeper';
								    option.text = 'Goal Keeper';
									break;
								case 3:
									option.value = 'captain_goal_keeper';
								    option.text = 'Captain And Goal Keeper';
									break;
								}
							    select.appendChild(option);
							}
							if(i <= 3) {
								switch(j) {
								case 1: 
									select.value = dataToProcess.homeSquad[i].captainGoalKeeper;
									break;
								case 3:
									select.value = dataToProcess.awaySquad[i].captainGoalKeeper;
									break;
								}
							}
							if(i > 3 && (i-4) <= dataToProcess.homeSubstitutes.length -1){
								switch(j) {
								case 1:
									select.value = dataToProcess.homeSubstitutes[i-4].captainGoalKeeper;
									break;
								}
							}
							if(i > 3 && (i-4) <= dataToProcess.awaySubstitutes.length -1){
								switch(j) {
								case 3:
									select.value = dataToProcess.awaySubstitutes[i-4].captainGoalKeeper;
									break;
								}
							}
							break;
						}
						row.insertCell(j).appendChild(select);
						removeSelectDuplicates(select.id);
						$(select).select2();
					} else {
						select = document.createElement('label');
						row.insertCell(j).appendChild(select);
					}
				}
			}
		
			table.appendChild(tbody);
			document.getElementById('team_selection_div').appendChild(table);
			document.getElementById('team_selection_div').style.display = '';
		} 
		break;
		
	case 'POPULATE-OFF_PLAYER':
		
		$('#select_player').empty();
		
		if(dataToProcess.homeTeamId ==  $('#select_teams option:selected').val()){
			dataToProcess.homeSquad.forEach(function(hs,index,arr){
				$('#select_player').append(
					$(document.createElement('option')).prop({
	                value: hs.playerId,
	                text: hs.jersey_number + ' - ' + hs.full_name
		        }))					
			});
		}
		else {
			dataToProcess.awaySquad.forEach(function(as,index,arr){
				$('#select_player').append(
					$(document.createElement('option')).prop({
	                value: as.playerId,
	                text: as.jersey_number + ' - ' + as.full_name
		        }))					
			});
		}
		break;
		
	case 'POPULATE-ON_PLAYER':
		
		$('#select_sub_player').empty();
		if(dataToProcess.homeTeamId ==  $('#select_teams option:selected').val()){
			dataToProcess.homeSubstitutes.forEach(function(hsub,index,arr){
				$('#select_sub_player').append(
					$(document.createElement('option')).prop({
	                value: hsub.playerId,
	                text: hsub.jersey_number + ' - ' + hsub.full_name
		        }))					
			});
		}
		else {
			dataToProcess.awaySubstitutes.forEach(function(asub,index,arr){
				$('#select_sub_player').append(
					$(document.createElement('option')).prop({
	                value: asub.playerId,
	                text: asub.jersey_number + ' - ' + asub.full_name
		        }))					
			});
		}
		break;

	case 'LOAD_PENALTY':
		
		$('#select_event_div').empty();
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);

		for(var i=1; i<=2; i++) {
			div = document.createElement('div');
			div.style = 'text-align:center;';
			switch(i){
			case 1:
				text = 'home';
				break;
			case 2:
				text = 'away';
				break;
			}
			div.id = text + '_penalties_div';
			for(var j=0; j<=5; j++) {
				switch(j){
				case 0: case 3:
					header_text = document.createElement('label');
					header_text.htmlFor = div.id;
					option = document.createElement('input');
					option.type = "button";
					option.style = 'text-align:center;';
					option.setAttribute('onclick','processUserSelection(this)');
					switch(j){
					case 0:
						header_text.innerHTML = text.toUpperCase() + ' Hits: ';
						option.id = text + '_increment_penalties_hit_btn';
						break;
					case 3:
						header_text.innerHTML = 'Misses: ';
						option.id = text + '_increment_penalties_miss_btn';
						break;
					}
					option.value = "+";
					div.appendChild(header_text).appendChild(option);
					break;
				case 1: case 4:
	    			option = document.createElement('input');
					option.type = 'text';
					switch(j){
					case 1:
						option.id = text + '_penalties_hit_txt';
						break;
					case 4:
						option.id = text + '_penalties_miss_txt';
						break;
					}
					option.value = '0';
					option.style = 'width:10%;text-align:center;';
					div.appendChild(option);
					break;
				case 2: case 5:
					option = document.createElement('input');
					option.type = "button";
					option.style = 'text-align:center;';
					option.setAttribute('onclick','processUserSelection(this)');
					switch(j){
					case 2:
						option.id = text + '_decrement_penalties_hit_btn';
						break;
					case 5:
						option.id = text + '_decrement_penalties_miss_btn';
						break;
					}
					option.value = "-";
					div.appendChild(option);
				    div.append(document.createElement('br'));
					break;
				}
			}
			row.insertCell(i-1).appendChild(div);
		}

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_penalty_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.appendChild(option);

		row.insertCell(2).appendChild(div);
		
		table.appendChild(tbody);
		
		document.getElementById('select_event_div').appendChild(table);
		
		break;
				
	case 'LOAD_REPLACE':
		
		$('#select_event_div').empty();
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);
		
		select = document.createElement('select');
		select.id = 'select_teams';
		select.name = select.id;
		
		option = document.createElement('option');
		option.value = dataToProcess.homeTeamId;
		option.text = dataToProcess.homeTeam.teamName1;
		select.appendChild(option);
		
		option = document.createElement('option');
		option.value = dataToProcess.awayTeamId;
		option.text = dataToProcess.awayTeam.teamName1;
		select.appendChild(option);
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Teams: '
		header_text.htmlFor = select.id;
		select.setAttribute('onchange',"processUserSelection(this)");
		row.insertCell(0).appendChild(header_text).appendChild(select);
		
		select = document.createElement('select');
		select.id = 'select_player';
		select.name = select.id;
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Player: '
		header_text.htmlFor = select.id;
		row.insertCell(1).appendChild(header_text).appendChild(select);

	    select = document.createElement('select');
		select.id = 'select_sub_player';
		select.name = select.id;
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Sub-Player: '
		header_text.htmlFor = select.id;
		row.insertCell(2).appendChild(header_text).appendChild(select);
		
	    div = document.createElement('div');

	    option = document.createElement('input');
	    option.type = 'button';
	    option.name = 'log_replace_btn';
	    option.id = option.name;
	    option.value = 'Replace Player';
	    option.setAttribute('onclick','processUserSelection(this);');
	    
	    div.append(option);

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_replace_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.append(document.createElement('br'));
	    div.append(option);

	    row.insertCell(3).appendChild(div);

		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);
		
		break;
		
	case 'LOAD_UNDO':

		$('#select_event_div').empty();
		
		if(dataToProcess.events.length > 0) {

			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
			row = tbody.insertRow(tbody.rows.length);
			
			select = document.createElement('select');
			select.id = 'select_undo';
			dataToProcess.events = dataToProcess.events.reverse();
			var max_loop = dataToProcess.events.length;
			if(max_loop > 5) {
				max_loop = 5;
			}
			for(var i = 0; i < max_loop; i++) {
				option = document.createElement('option');
				option.value = dataToProcess.events[i].eventNumber;
			    option.text = dataToProcess.events[i].eventNumber + '. ' + dataToProcess.events[i].eventType;
			    select.appendChild(option);
			}
			header_text = document.createElement('label');
			header_text.innerHTML = 'Last Five Events: '
			header_text.htmlFor = select.id;
			row.insertCell(0).appendChild(header_text).appendChild(select);

		    option = document.createElement('input');
		    option.type = 'text';
		    option.name = 'number_of_undo_txt';
		    option.value = '1';
		    option.id = option.name;
		    option.setAttribute('onblur','processUserSelection(this)');
			header_text = document.createElement('label');
			header_text.innerHTML = 'Number of undos: '
			header_text.htmlFor = option.id;
			row.insertCell(1).appendChild(header_text).appendChild(option);
			
		    div = document.createElement('div');

		    option = document.createElement('input');
		    option.type = 'button';
		    option.name = 'log_undo_btn';
		    option.id = option.name;
		    option.value = 'Undo Last Event';
		    option.setAttribute('onclick','processUserSelection(this);');
		    
		    div.append(option);

			option = document.createElement('input');
			option.type = 'button';
			option.name = 'cancel_undo_btn';
			option.id = option.name;
			option.value = 'Cancel';
			option.setAttribute('onclick','processUserSelection(this)');

		    div.append(document.createElement('br'));
		    div.append(option);

		    row.insertCell(2).appendChild(div);

			table.appendChild(tbody);
			document.getElementById('select_event_div').appendChild(table);

		} else {
			return false;
		}
		
		break;
	
	case 'LOAD_EVENTS':
		
		$('#select_event_div').empty();

		header_text = document.createElement('label');
		header_text.id = 'selected_player_name';
		header_text.innerHTML = '';
		document.getElementById('select_event_div').appendChild(header_text);
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		
		for(var iRow=0;iRow<=0;iRow++) {
			
			row = tbody.insertRow(tbody.rows.length);
			max_cols = 4;
			
			for(var iCol=0;iCol<=max_cols;iCol++) {
				
				cell = row.insertCell(iCol);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'log_event_btn';
				
				switch (iRow) {
				case 0:
					
					switch (iCol) {
					/*case 0:
						option.id = 'goal';
						option.value = 'Goal';
						break;
					case 1:
						option.id = 'card';
						option.value = 'Card';
						break;*/
					case 0:
						option.id = 'replace';
						option.value = 'Replace';
						break;
					case 1:
						option.id = 'undo';
						option.value = 'Undo';
						break;
					case 2:
						option.id = 'overwrite';
						option.value = 'Overwrite';
						break;
					case 3:
						option.id = 'penalty';
						option.value = 'Penalty';
						break;
					case 4:
						option.name = 'cancel_event_btn';
						option.id = option.name;
						option.value = 'Cancel';
					}
					
					break;
					
				}
				
				if(option.id) {
					
					switch (option.id) {
					case 'overwrite': case 'goal': case 'card': case 'stats':
						
						option.setAttribute('data-toggle', 'dropdown');
						option.setAttribute('aria-haspopup', 'true');
						option.setAttribute('aria-expanded', 'false');					
						
						div = document.createElement('div');
					    div.append(option);
					    div.className='dropdown';
					    
					    linkDiv = document.createElement('div');
					    linkDiv.id = option.id + '_div';
					    linkDiv.className='dropdown-menu';
					    linkDiv.setAttribute('aria-labelledby',option.id);

						switch (option.id) {
						case 'stats':
					
							for(var ibound=1; ibound<=8; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
			
								switch(ibound) {
								case 1:
								    anchor.id = 'off_side';
								    anchor.innerText = 'Off Side';
									break;
								case 2:
								    anchor.id = 'assists';
								    anchor.innerText = 'Assists';
									break;
								case 3:
								    anchor.id = 'shots';
								    anchor.innerText = 'Shots';
									break;
								case 4:
								    anchor.id = 'shots_on_target';
								    anchor.innerText = 'Shots On Target';
									break;
								case 5:
								    anchor.id = 'fouls';
								    anchor.innerText = 'Fouls';
									break;
								case 6:
								    anchor.id = 'corners';
								    anchor.innerText = 'Corners';
									break;
								case 7:
								    anchor.id = 'corners_converted';
								    anchor.innerText = 'Corners Converted';
									break;
								}
								switch(ibound){
									case 1: case 2: case 3: case 4: case 5: case 6: case 7:
										 anchor.setAttribute('onclick','processWaitingButtonSpinner("START_WAIT_TIMER");processPoloProcedures("LOG_EVENT",this);');
										break;
								}
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
							
						case 'overwrite': 
							
							for(var ibound=1; ibound<=1; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
			
							    switch(ibound) {
								case 1:
								    anchor.id = 'overwrite_match_substitute';
								    anchor.innerText = 'Match Subs';
								    anchor.setAttribute('onclick','addItemsToList("LOAD_OVERWRITE_MATCH_SUB",this);');
									break;
								}
							    
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
							
						case 'goal':
						
							for(var ibound=1; ibound<=3; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
			
							    if(ibound == 1) {
								    anchor.id = 'goal';
								    anchor.innerText = 'Goal';
							    } else if(ibound == 2) {
								    anchor.id = 'own_goal';
								    anchor.innerText = 'Own goal';
							    }else{
									anchor.id = 'penalty';
								    anchor.innerText = 'Penalty';
								}
							    anchor.setAttribute('onclick','processWaitingButtonSpinner("START_WAIT_TIMER");processPoloProcedures("LOG_EVENT",this);');
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
							
						case 'card':
							
							for(var ibound=1; ibound<=2; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
								
								if(ibound == 1) {
								    anchor.id = 'yellow';
								    anchor.innerText = 'Yellow Card';
							    } else if(ibound == 2) {
								    anchor.id = 'red';
								    anchor.innerText = 'Red Card';
							    }
							    
							    anchor.setAttribute('onclick','processWaitingButtonSpinner("START_WAIT_TIMER");processPoloProcedures("LOG_EVENT",this);');
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
						}
					    div.append(linkDiv);				    
						cell.append(div);
						break;
						
					default:
					
						option.onclick = function() {processUserSelection(this)};
						cell.appendChild(option);
						
						break;
					
					}
				}
			}
		}
			
		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);

		break;
		
	case 'LOAD_SET':
	    $('#select_set_div').empty();
	
	    table = document.createElement('table');
	    table.setAttribute('class', 'table table-bordered');
	    table.style.backgroundColor = '#f4f0ff';
	    table.style.borderCollapse = 'separate';
	    table.style.borderSpacing = '0 6px'; // smaller spacing
	    tbody = document.createElement('tbody');
	
	    table.appendChild(tbody);
	    document.getElementById('select_set_div').appendChild(table);
	
	    // ---------------- ROW 1: COMPACT BUTTONS ----------------
	    row = tbody.insertRow(tbody.rows.length);
	
	    const buttons = [
	        { name: 'start_set_btn', value: 'Start Set', color: 'green' },
	        { name: 'end_set_btn', value: 'End Set', color: 'red' },
	        { name: 'undo_set', value: 'Undo Set', color: 'blue' },
	        { name: 'Overwrite_set', value: 'Overwrite Set', color: 'blue' },
	        { name: 'reset_set_btn', value: 'Reset Set', color: 'black' }
	    ];
	
	    buttons.forEach((btn, i) => {
	        const cell = row.insertCell(i);
	        option = document.createElement('input');
	        option.type = 'button';
	        option.name = btn.name;
	        option.value = btn.value;
	        option.style.width = 'auto';
	        option.style.whiteSpace = 'nowrap';
	        option.style.color = btn.color;
	        option.style.border = btn.color === 'blue' ? '1.5px solid gray' : '1.5px solid #d4af37';
	        option.style.padding = '6px 12px';
	        option.style.borderRadius = '6px';
	        option.style.boxShadow = '1px 2px 3px rgba(0, 0, 0, 0.2)';
	        option.style.fontWeight = '500';
	        option.style.fontSize = '13px';
	        option.style.backgroundColor = 'white';
	        option.setAttribute('onclick', 'processUserSelection(this);');
	        cell.appendChild(option);
	        cell.style.padding = '2px 6px';
	    });
	
	    // ---------------- ROW 2: SET NUMBER HEADER ----------------
	    row = tbody.insertRow(tbody.rows.length);
	    cell = row.insertCell(row.cells.length);
	    cell.innerHTML = '<b style="font-size: 15px; color:#2a0077;">Team</b>';
	    cell.style.textAlign = 'center';
	    cell.style.padding = '6px 10px';
	    cell.style.backgroundColor = '#e4dbff';
	    cell.style.border = '1px solid #c7b6e2';
	
	    for (var s = 1; s <= match_data.sets.length; s++) {
	        cell = row.insertCell(row.cells.length);
	        cell.innerHTML = `<b style="font-size: 14px; color:#2a0077;">Set ${s}</b>`;
	        cell.style.textAlign = 'center';
	        cell.style.padding = '6px 10px';
	        cell.style.backgroundColor = '#e4dbff';
	        cell.style.border = '1px solid #c7b6e2';
	        cell.style.borderRadius = '4px';
	    }
	
	    // ---------------- ROW 3 & 4: TEAM SCORES ----------------
	    for (var j = 0; j <= 1; j++) {
	        row = tbody.insertRow(tbody.rows.length);
	        cell = row.insertCell(row.cells.length);
	
	        const teamName = j === 0 ? dataToProcess.homeTeam.teamName1 : dataToProcess.awayTeam.teamName1;
	        cell.innerHTML = `<b style="font-size: 15px; color:#2a0077; text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);">${teamName}</b>`;
	        cell.style.padding = '6px 10px';
	        cell.style.backgroundColor = '#f4f0ff';
	        cell.style.border = '1px solid #c7b6e2';
	
	        const teamSets = j === 0 ? match_data.sets.map(s => s.homeScore) : match_data.sets.map(s => s.awayScore);
	
	        teamSets.forEach(score => {
	            cell = row.insertCell(row.cells.length);
	            cell.innerHTML = `<span style="font-size:14px; color:#2a0077; font-weight:600;">${score}</span>`;
	            cell.style.textAlign = 'center';
	            cell.style.border = '1px solid #c7b6e2';
	            cell.style.borderRadius = '4px';
	            cell.style.padding = '6px 10px';
	            cell.style.backgroundColor = '#ebe3ff';
	            cell.style.boxShadow = '1px 1px 2px rgba(0,0,0,0.1)';
	        });
	    }
	    break;

	
	case 'LOAD_MATCH':
		
		$('#polo_div').empty();
		
		if (dataToProcess)
		{
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			tbody = document.createElement('tbody');

			table.appendChild(tbody);
			document.getElementById('polo_div').appendChild(table);

			row = tbody.insertRow(tbody.rows.length);
			header_text = document.createElement('h6');
			header_text.id = 'match_time_hdr';
			header_text.innerHTML = 'Match Time: 00:00:00';
			row.insertCell(0).appendChild(header_text);
			
			if(dataToProcess.events != null && dataToProcess.events.length > 0) {
				max_cols = dataToProcess.events.length;
				if (max_cols > 20) {
					max_cols = 20;
				}
				header_text = document.createElement('h6');
				for(var i = 0; i < max_cols; i++) {
					if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId != 0){
						dataToProcess.homeSquad.forEach(function(hs,index,arr){
							if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId == hs.playerId){
								playerName = ' {'+ hs.ticker_name +'}' ;
							}				
						});
						dataToProcess.awaySquad.forEach(function(as,index,arr){
							if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId == as.playerId){
								playerName = ' {'+ as.ticker_name +'}';
							}				
						});
						dataToProcess.homeSubstitutes.forEach(function(hsub,index,arr){
							if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId == hsub.playerId){
								playerName = ' {'+ hsub.ticker_name +'}';
							}		
						});
						dataToProcess.awaySubstitutes.forEach(function(asub,index,arr){
							if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId == asub.playerId){
								playerName = ' {'+ asub.ticker_name +'}';
							}			
						});
					}else{
						playerName = '';
					}
					
					if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType) {
						if(header_text.innerHTML) {
							header_text.innerHTML = header_text.innerHTML + ', ' + dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType.replaceAll('_',' ') + playerName; // .match(/\b(\w)/g).join('')
						} else {
							header_text.innerHTML = dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType.replaceAll('_',' ') + playerName; // .match(/\b(\w)/g).join('')
						}
					}
				}
				header_text.innerHTML = 'Events: ' + header_text.innerHTML;
				row.insertCell(1).appendChild(header_text);
			}

			//Teams Score and other details
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			thead = document.createElement('thead');
			tr = document.createElement('tr');
			for (var j = 0; j <= 1; j++) {
			    th = document.createElement('th'); // Column
				th.scope = 'col';
			    switch (j) {
				case 0:
				    th.innerHTML = dataToProcess.homeTeam.teamName1 + ' - ' + dataToProcess.homeTeamScore;
				    link_div = document.createElement('div');
					for(var k=0; k<=2; k++) {
						switch (k) {
						case 0: case 2:
		        			option = document.createElement('input');
		    				option.type = "button";
		    				option.classList.add('btn'); 
		    				option.style.fontSize = '18px'; 
		    				if (k == 0) {
							    option.id = 'home_increment_score_btn';
							    option.value = "+"; 
							    option.classList.add('btn-success');
							    option.setAttribute('onclick', 'processUserSelection(this);');
							} else {
							    option.id = 'home_decrement_score_btn';
							    option.value = "-";
							    option.classList.add('btn-danger');
							    option.setAttribute('onclick', 'processUserSelection(this);');
							}
		    				option.style = 'text-align:center;';
							break;
						case 1: 
		        			option = document.createElement('input');
		    				option.type = "text";
		    				option.id = 'homeScore';
		    				option.name = 'homeScore';
							option.style = 'width:30%; height:35px; text-align:center; font-size:24px;'; // Increased size
							option.value = '0';
							match_data.sets.forEach(function(set){
								option.value = set.homeScore;
							});
							option.onblur = function() { processUserSelection(this);};
		    				break;
						}
						link_div.appendChild(option);
				    }
				    th.appendChild(link_div);
					break;
				case 1:
					th.innerHTML = dataToProcess.awayTeam.teamName1 + ' - ' + dataToProcess.awayTeamScore;
					link_div = document.createElement('div');
					for(var k=0; k<=2; k++) {
						switch (k) {
						case 0: case 2:
		        			option = document.createElement('input');
		    				option.type = "button";
		    				option.classList.add('btn'); 
		    				option.style.fontSize = '18px'; 
		    				if (k == 0) {
							    option.id = 'away_increment_score_btn';
							    option.value = "+"; 
							    option.classList.add('btn-success');
							    option.setAttribute('onclick', 'processUserSelection(this);');
							} else {
							    option.id = 'away_decrement_score_btn';
							    option.value = "-";
							    option.classList.add('btn-danger');
							    option.setAttribute('onclick', 'processUserSelection(this);');
							}
		    				option.style = 'text-align:center;';
							break;
						case 1: 
		        			option = document.createElement('input');
		    				option.type = "text";
		    				option.id = 'awayScore';
		    				option.name = 'awayScore';
							option.style = 'width:30%; height:35px; text-align:center; font-size:24px;'; // Increased size
							option.value = '0';
							match_data.sets.forEach(function(set){
								option.value = set.awayScore;
							});
							option.onblur = function() { processUserSelection(this);};
		    				break;
						}
						link_div.appendChild(option);
				    }
				    th.appendChild(link_div);
					break;
				}
			    tr.appendChild(th);
			}
			thead.appendChild(tr);
			table.appendChild(thead);
			document.getElementById('polo_div').appendChild(table);
			
			tbody = document.createElement('tbody');
			for(var i = 0; i <= dataToProcess.homeSquad.length - 1; i++) {
				row = tbody.insertRow(tbody.rows.length);
				for(var j = 1; j <= 2; j++) {
					anchor = document.createElement('a');
					switch(j){
					case 1:
						anchor.name = 'homePlayers';
						anchor.id = 'homePlayer_' + dataToProcess.homeSquad[i].playerId;
						anchor.value = dataToProcess.homeSquad[i].playerId;
						if(getPlayerMatchStats(dataToProcess.homeSquad[i].playerId) == ''){
							anchor.innerHTML = dataToProcess.homeSquad[i].jersey_number + ': ' + dataToProcess.homeSquad[i].full_name ;
						}else{
							anchor.innerHTML = dataToProcess.homeSquad[i].jersey_number + ': ' + dataToProcess.homeSquad[i].full_name 
								+ '  ['+ getPlayerMatchStats(dataToProcess.homeSquad[i].playerId) + ']';
						}
						break;
					case 2:
						anchor.name = 'awayPlayers';
						anchor.id = 'awayPlayer_' + dataToProcess.awaySquad[i].playerId;
						anchor.value = dataToProcess.awaySquad[i].playerId;
						if(getPlayerMatchStats(dataToProcess.awaySquad[i].playerId) == ''){
							anchor.innerHTML = dataToProcess.awaySquad[i].jersey_number + ': ' + dataToProcess.awaySquad[i].full_name ;
						}else{
							anchor.innerHTML = dataToProcess.awaySquad[i].jersey_number + ': ' + dataToProcess.awaySquad[i].full_name 
								+ '  ['+ getPlayerMatchStats(dataToProcess.awaySquad[i].playerId) + ']';
						}
						break;
					}
					anchor.setAttribute('onclick','processUserSelection(this);');
					anchor.setAttribute('style','cursor: pointer;');
					row.insertCell(j - 1).appendChild(anchor);
				}
			}				
			row = tbody.insertRow(tbody.rows.length);
			header_text = document.createElement('header');
			header_text.innerHTML = 'Substitutes';
			row.insertCell(0).appendChild(header_text);
			
			max_cols = dataToProcess.homeSubstitutes.length;
			if(dataToProcess.homeSubstitutes.length < dataToProcess.awaySubstitutes.length) {
				max_cols = dataToProcess.awaySubstitutes.length;
			}
			
			for(var i = 0; i <= max_cols-1; i++) {
				
				row = tbody.insertRow(tbody.rows.length);
				
				for(var j = 1; j <= 2; j++) {
					
					addSelect = false;
					
					switch(j) {
					case 0: case 1:
						if(i <= parseInt(dataToProcess.homeSubstitutes.length - 1)) {
							addSelect = true;
						}
						break;
					case 2: case 3:
						if(i <= parseInt(dataToProcess.awaySubstitutes.length - 1)) {
							addSelect = true;
						}
						break;
					}

					text = document.createElement('label');
					
					if(addSelect == true) {
					
						switch(j){
						case 1:
							
							text.name = 'homeSubstitutes';
							text.id = 'homeSubstitute_' + dataToProcess.homeSubstitutes[i].playerId;
							text.value = dataToProcess.homeSubstitutes[i].playerId;
							if(getPlayerMatchStats(dataToProcess.homeSubstitutes[i].playerId) == ''){
								text.innerHTML = dataToProcess.homeSubstitutes[i].jersey_number + ': ' + dataToProcess.homeSubstitutes[i].full_name;
							}else{
								text.innerHTML = dataToProcess.homeSubstitutes[i].jersey_number + ': ' + dataToProcess.homeSubstitutes[i].full_name 
									+ '  ['+ getPlayerMatchStats(dataToProcess.homeSubstitutes[i].playerId) + ']';
							}
							break;
							
						case 2:
							
							text.name = 'awaySubstitutes';
							text.id = 'awaySubstitute_' + dataToProcess.awaySubstitutes[i].playerId;
							text.value = dataToProcess.awaySubstitutes[i].playerId;
							if(getPlayerMatchStats(dataToProcess.awaySubstitutes[i].playerId) == '') {
								text.innerHTML = dataToProcess.awaySubstitutes[i].jersey_number + ': ' + dataToProcess.awaySubstitutes[i].full_name;
							}else{
								text.innerHTML = dataToProcess.awaySubstitutes[i].jersey_number + ': ' + dataToProcess.awaySubstitutes[i].full_name 
									+ '  ['+ getPlayerMatchStats(dataToProcess.awaySubstitutes[i].playerId) + ']';
							}
							break;
							
						}
					
						text.setAttribute('style','cursor: pointer;');
					
					}	
				
					row.insertCell(j - 1).appendChild(text);
				
				}
			}				

			table.appendChild(tbody);
			document.getElementById('polo_div').appendChild(table);
			
		}
		break;
	}
}
function removeSelectDuplicates(select_id)
{
	var this_list = {};
	$("select[id='" + select_id + "'] > option").each(function () {
	    if(this_list[this.text]) {
	        $(this).remove();
	    } else {
	        this_list[this.text] = this.value;
	    }
	});
}
function checkEmpty(inputBox,textToShow) {

	var name = $(inputBox).attr('id');
	
	document.getElementById(name + '-validation').innerHTML = '';
	document.getElementById(name + '-validation').style.display = 'none';
	$(inputBox).css('border','');
	if(document.getElementById(name).value.trim() == '') {
		$(inputBox).css('border','#E11E26 2px solid');
		document.getElementById(name + '-validation').innerHTML = textToShow + ' required';
		document.getElementById(name + '-validation').style.display = '';
		document.getElementById(name).focus({preventScroll:false});
		return false;
	}
	return true;	
}	

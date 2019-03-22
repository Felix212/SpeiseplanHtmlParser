var ua = navigator.userAgent.toLowerCase();
var isIE = (ua.indexOf("msie") != -1 && ua.indexOf("opera") == -1 && ua.indexOf("webtv") == -1);
var isGecko = ua.indexOf("gecko") != -1;
var isOpera = (ua.indexOf("opera") != -1);
var isSafari = (ua.indexOf("safari") != -1);
var isKonqueror = (ua.indexOf("konqueror") != -1);
var Monate;
var WochenTageKurtz;

var MonatsTage = new Array('31','28','31','30','31','30','31','31','30','31','30','31');



//document.writeln('<link href="typo3conf/ext/ppw_lunchmenu/res/calendar/calendar.css" rel="stylesheet" type="text/css">');
// *************************

function getParentByTagName(node, tag_name){
   tag_name = tag_name.toLowerCase();
   var p = node;
   do{
	  if(tag_name == '' || p.nodeName.toLowerCase() == tag_name) return p;
   }while(p = p.parentNode)
   return node;
}//#

// *** DATE FUNCTIONS ***
function getDate(){
	var datum= new Date();
	return{d:datum.getDate(), m:datum.getMonth()+1, y:datum.getFullYear()}
}//

// return nummer des wochentages von 1 tag den monat
function getWeekday(jahr,monat){//monat 1-12
	var dw= new Date(jahr,monat-1,1);//function verwendet monat von 0 bis 11
	dw= dw.getDay(); // 0 - So, 1 - Mo, 2 - Di, 3 - Mi, 4 - Do, 5 - Fr, 6 - Sa
	if (!dw){
		return 6;
	}else{
		return dw-1;
	}
}//# return 0 - Mo, 1 - Di, 2 - Mi, 3 - Do, 4 - Fr, 5 - Sa, 6 - So

//return Anzahl Tage im Monat(1-12)
function getDaysPerMonth(monat,jahr){
	if (monat==2){// Februar
		if (jahr % 4){
			return 28;
		}else{
			return 29;
		}
	}else{
		return MonatsTage[monat-1];
	}
}//#

// *** CHECK FUNCTIONS ***
function isZahl(e,f,len){
	 if (isIE) key = e.keyCode;
	 else key = e.which;
	 ln = parseInt(f.value.length);
	 if (ln==3 && (key >= 48 && key <= 57)){
		return "ok";
	 }
	 if ( (ln > len-1) && (key !=8 ) ) {
		return false;
	 }else if ( (key >= 48 && key <= 57) || (key ==8 ) || (key ==0 ) ){
		return true;
	 }else{
		return false;
	 }
}//#

function isDatumValid(str){
	var valid= "^(([0-9]{2})[-.\/]{1}([0-9]{2})[-.\/]{1}([0-9]{4}))$";
	var regex= new RegExp(valid,"g");
	var datum= regex.exec(str); //array: [2] - TT; [3] - MM; [4] - JJJJ
	if ( !datum || datum[2]<1 || datum[3]<1 || datum[3]>12 || datum[4]<1900 ){
		return false;
	}
	var dmax= getDaysPerMonth(datum[3],datum[4]);
	if ( datum[2]>dmax ){
		return false;
	}else{
		return{d:datum[2],m:datum[3],y:datum[4]}
	}
}//#

function isInputDatumValid(str){
	var valid= "^(([0-9]{2})[-.\/]{1}([0-9]{2})[-.\/]{1}([0-9]{4}))$";
	var regex= new RegExp(valid,"g");
	var datum= regex.exec(str); //array: [2] - TT; [3] - MM; [4] - JJJJ
	if ( !datum){
		return false;
	}else{
		var err= 0;
		if(datum[4] < 1900){
			datum[4]= 1900;
			err++;
		}
		if(datum[3] > 12){
			datum[3]= 12;
			err++;
		}
		var dmax= getDaysPerMonth(datum[3],datum[4]);
		if(datum[2] > dmax){
			datum[2]= dmax;
			err++;
		}
		return{d:datum[2],m:datum[3],y:datum[4],error:err}
	}
}//#

// *** EVENT FUNCTIONS ***

document.onmousedown= function(event){onCalendarBlur(event);}

function onCalendarBlur(e){
	if(!selected_calendarID){
        return;
	}
	if(!e){
		var e= window.event;
	}
    
    
    
	var node = e.target || e.srcElement;
	var parent= getParentByTagName(node, "span");
	var calendarID= selected_calendarID+calID;
	var buttonID= selected_calendarID+calButtonID;
	if(parent.id != calendarID && node.id != buttonID){
        document.getElementById(calendarID).style.visibility='hidden';
	}
}//#
function onCalendarFocus(id){
	selected_calendarID= id;
}//

function onInputDate(id){
	var obj= document.getElementById(id);
	var datum= isInputDatumValid(obj.value);
	if(!datum){
		return;
	}
	selected_date[selected_calendarID]= datum;
	if(selected_date[selected_calendarID].error){
		setInputDate(selected_calendarID);
	}
	setCalendar();
	return;
}//#

function onclick_setCalendarDay(id){
	var newselected_cellObj= document.getElementById(id);
	day= newselected_cellObj.innerHTML;
	if (day != "&nbsp;"){
		day= eval(day);
	}else{
		return;
	}

	// remove selection
	if ( isToday(selected_date[selected_calendarID]) ){
		selected_cellObj[selected_calendarID].className= 	'td_today_unselected';
	}else{
		selected_cellObj[selected_calendarID].className= 	'td_unselected';
	}

	selected_date[selected_calendarID].d= day;

	// set new selection
	if ( isToday(selected_date[selected_calendarID]) ){
		newselected_cellObj.className= 	'td_today_selected';
	}else{
		newselected_cellObj.className= 	'td_selected';
	}

	selected_cellObj[selected_calendarID]= newselected_cellObj;
	setInputDate(selected_calendarID);
}//#
function isToday(dt){
	var today= getDate();
	if(dt.d==today.d && dt.m==today.m && dt.y==today.y){
		return true;
	}
	return false;
}//#

//******* HEUTE *************
function onclick_setToday(){
	selected_date[selected_calendarID]= getDate();
	setCalendar();
	setInputDate(selected_calendarID);
}//#

//******* MONAT *************
function setCalendarNextMonth(){
	var obj= document.getElementById(selected_calendarID+calMonthID);
	var pos= obj.selectedIndex+1;
	if (pos >= 12){
		setCalendarNextYear();
		pos= 0;
	}
	selected_date[selected_calendarID].m= pos+1;
	return pos;
}//#

function onclick_setCalendarNextMonth(){
	var pos= setCalendarNextMonth();
	var obj= document.getElementById(selected_calendarID+calMonthID);
	obj[pos].selected= true;
	setCalendarDays();
	setInputDate(selected_calendarID);
}//#
function onclick_setCalendarPrevMonth(){
	var obj= document.getElementById(selected_calendarID+calMonthID);
	var pos= obj.selectedIndex-1;
	if (pos < 0){
		setCalendarPrevYear();
		pos= 11;
	}
	obj[pos].selected= true;
	selected_date[selected_calendarID].m= pos+1;
	setCalendarDays();
	setInputDate(selected_calendarID);
}//#
function onchange_setCalendarMonth(id){
	selected_date[selected_calendarID].m= document.getElementById(id).selectedIndex+1;
	setCalendarDays();
	setInputDate(selected_calendarID);
}//#

//******** JAHR ***************
function onchange_setCalendarYear(set){
	if (set=="ok"){
		selected_date[selected_calendarID].y= document.getElementById(selected_calendarID+calYearID).value;
		setCalendarDays();
		setInputDate(selected_calendarID);
		result= "";
	}
}//#
function setCalendarNextYear(){
	var obj= document.getElementById(selected_calendarID+calYearID);
	obj.value++;
	selected_date[selected_calendarID].y= obj.value;
}//#
function onclick_setCalendarNextYear(){
	setCalendarNextYear();
	setCalendarDays();
	setInputDate(selected_calendarID);
}//#
function setCalendarPrevYear(){
	var obj= document.getElementById(selected_calendarID+calYearID);
	obj.value--;
	selected_date[selected_calendarID].y= obj.value;
}//#
function onclick_setCalendarPrevYear(){
	setCalendarPrevYear();
	setCalendarDays();
	setInputDate(selected_calendarID);
}//#


//******* SET CALENDAR ***********
function setCalendar(){
	setCalendarMonth(selected_date[selected_calendarID].m);
	setCalendarYear(selected_date[selected_calendarID].y);
	setCalendarDays();
}//#


// *** NEW CALENDAR ***
var selected_calendarID;

// private:
var calID= '_cal';
var calButtonID= '_calButton';
var calMonthID= '_calMonth';
var calYearID= '_calYear';
var selected_cellObj= Array(); // object von current cell(selected day) in table
var result=''; //result on keypress
// end private

// id - id von Datum Input Feld
// start_date, current_date, end_date- string
// vom und/oder bis kann sein:
// 1. '', dann Zeitraum ist unbegrenzt
// 2. in format 'dd-mm-yyyy', wenn format falsch ist dann wird unbegrenzt
// 3. 'today', ist heutige Datum
// 4. 'today+x', wo x sind Tage - heutige Datum + x Tage                                                    
var start_date= Array();
var end_date= Array();
var selected_date= Array();
var pageLng;
function NewCalendar(id,start_dt,init_dt,end_dt){
	for (var i = 0; i < document.getElementsByTagName('meta').length; i++) {
		if (document.getElementsByTagName('meta')[i].getAttribute('name') == 'language') {
			pageLng = document.getElementsByTagName('meta')[i].getAttribute('content');
		} 
	}
    
	switch(pageLng) {
		case "default":
			Monate =  new Array('January','February','March','April','May','June','July','August','September','October','November','December');  
			WochenTageKurtz=new Array('Sun','Mon','Tue','Wed','Thu','Fri','Sat');
            
		break;	
		default:
            Monate =  new Array('Januar','Februar','M&auml;rz','April','Mai','Juni','Juli','August','September','Oktober','November','Dezember');
            WochenTageKurtz=new Array('Son','Mon','Die','Mit','Don','Fre','Sam');
		break;
	}
	
	
	
	selected_calendarID= id;
	start_date[id]= getSplitedDate(start_dt);
	end_date[id]= getSplitedDate(end_dt);
	selected_date[id]= getSplitedDate(init_dt);
	if(!selected_date[id]){ // wenn Init Datum nicht selekt, dann stellen heutige Datum
		selected_date[id]= getDate();
	}

	//arr_calendarIDs[id]= id;

	makeTpl_Calendar(id);
	//setInputDate(id);
	setCalendar();
	setInputDate(selected_calendarID);
}//#

function getSplitedDate(dt){
	var datum= dt.toLowerCase().split('+',2);
	var d,m,y;
	var indays;
	if(datum[0] == 'today'){
		indays= Math.abs(datum[1]);
		datum= new Date();
		d= datum.getDate();
		m= datum.getMonth()+1;
		y= datum.getFullYear();
		if(indays){
			d+= indays;
			var dmax= getDaysPerMonth(m, y);
			if (d > dmax){
				d= d - dmax;
				m++;
				if(m > 12){
					m= 1;
					y++;
				}
			}//if dmax
		}// if indays
	}else{
		datum= isDatumValid(dt);
		if (datum){
			d= datum.d;
			m= datum.m;
			y= datum.y;
		}else{
			return false;
		}
	}//if
	return{d:d,m:m,y:y}
}//#

function setInputDate(id){
	var obj= document.getElementById(id);
	var d= selected_date[selected_calendarID].d;
	var m= selected_date[selected_calendarID].m;
	var y= selected_date[selected_calendarID].y;
	if (d < 10){
		d= "0"+eval(d);
	}
	if (m < 10){
		m= "0"+eval(m);
	}
	obj.value= d+"."+m+"."+y;
}//#

// ********** TEMPLATE ***************
var CacheCalendar= new Array(); //Cache of id names of table cells
function setCacheCalendar(){
	CacheCalendar[selected_calendarID]= new Array();
	var tpl= '';
	
    for ( var j= 0; j < 6; j++){
		CacheCalendar[selected_calendarID][j]= new Array();
		document.writeln('<tr>');
		for ( var ii= 0; ii < 7; ii++){
			idname= j+'_'+ii;
            
            test = 'hidden';
			tpl= '<td id='+selected_calendarID+idname+' class=td_nodate ondblclick="onclick_setCalendarDay('+"'"+selected_calendarID+idname+"'"+');document.getElementById('+"'"+selected_calendarID+calID+"'"+').style.visibility='+"'"+test+"'"+';">&nbsp</td>';
			document.writeln(tpl);
			CacheCalendar[selected_calendarID][j][ii]= document.getElementById(selected_calendarID+idname);
		}
		document.writeln('</tr>');
	}
}//#

function makeTpl_Calendar(id){
	var calendarID= id+calID;

	var valign= 'middle';
	if(isGecko){
		valign= 'baseline';
	}
	var class_style= 'date_ddmmyyyy';
	if(isIE){
		class_style= 'date_ddmmyyyy_IE';
	}
    
	var tpl=
'<span style="border:0px solid #000;vertical-align:'+valign+';">'+
	'<input type="text"  value="" id='+id+' onkeyup="onInputDate('+"'"+id+"'"+');return true;" name='+id+' class="'+class_style+'" onclick="onCalendarFocus('+"'"+id+"'"+');">'+
	'<img id="'+id+calButtonID+'" src="/typo3conf/ext/ppw_lunchmenu/res/calendar/calendaricon.gif" onclick="showCalendar('+"'"+calendarID+"'"+','+"'"+id+"'"+');" style="vertical-align:bottom">'+

	'<SPAN id='+calendarID+' class=div_kalender onselectstart="return false;" ondragstart="return false;">'+
	'<TABLE class=table_kalender>'+
	'<tr>'+
	'<td>'+
	'<table width=100%>'+
		'<tr>'+
			'<td nowrap><a class=prev_next href="" onclick="onclick_setCalendarPrevMonth(); return false;" >&#171;</a>'+
			'<select id='+id+calMonthID+' onchange="onchange_setCalendarMonth('+"'"+id+calMonthID+"'"+')">';
			for(var i= 0; i < 12; i++){
				tpl+= '<option>'+Monate[i]+'</option>';
			}
			tpl+=
			'</select>'+
			'<a class=prev_next href="" onclick="onclick_setCalendarNextMonth(); return false;">&#187;</a></td>'+

			'<td nowrap><a class=prev_next href="" onclick="onclick_setCalendarPrevYear(); return false;">&#171;</a>'+
				'<input id='+id+calYearID+' value="" size=2  style="width:36px"  onkeypress="return result= isZahl(event,this,4);" onkeyup="onchange_setCalendarYear(result);">'+
			'<a class=prev_next href="" onclick="onclick_setCalendarNextYear(); return false;">&#187;</a></td>'+
		'</tr>'+
	'</table>'+
	'</td>'+
	'</tr>'+

	'<tr>'+
	'<td align="center">'+
	'<a class=lnk_heute href="" onclick="onclick_setToday(); return false;" >Today</a>'+
	'</td>'+
	'</tr>'+

	'<tr>'+
	'<td>'+

	'<table>'+
		'<tr>';
			for(var i=1; i < 6; i++){
				tpl+= '<td class=td_woche>'+WochenTageKurtz[i]+'</td>';
			}
			tpl+=
			'<td class=td_wocheende>'+WochenTageKurtz[6]+'</td>'+
			'<td class=td_wocheende>'+WochenTageKurtz[0]+'</td>'+
		'</tr>'+
	'<script type=text/javascript>setCacheCalendar();</script>'+

	'</table>'+

	'</td>'+
	'</tr>'+

	/*'<tr>'+
	'<td align="center">'+
	'<a class=lnk_heute href="" onclick="showCalendar('+"'"+calendarID+"'"+','+"'"+id+"'"+'); return false;" >schlie&szlig;en</a>'+
	'</td>'+
	'</tr>'+*/

	'</TABLE>'+
	'</SPAN>'+

'</span>';
'<!-- Input Feld + Kalender  -->';

document.writeln(tpl);
}//#

// *** TAMPLATE MANIPULATIONS ***
function showCalendar(calendarID, id){

	var obj= document.getElementById(calendarID);
	if (obj.style.visibility=='visible'){
		obj.style.visibility='hidden';
	}else{
		//selected_calendarID= arr_calendarIDs[id];
		obj= document.getElementById(calendarID);
		obj.style.visibility='visible';
		selected_calendarID= id;
	}
}//#

function getCalendarMonth(){//return 1-12
	var obj= document.getElementById(selected_calendarID+calMonthID);
	return (obj.selectedIndex+1);
}//#
function setCalendarMonth(n){// 1-12
	var obj= document.getElementById(selected_calendarID+calMonthID);
	obj[n-1].selected= true;
}//#

function getCalendarYear(){
	var obj= document.getElementById(selected_calendarID+calYearID);
	return obj.value;
}//#
function setCalendarYear(n){
	var obj= document.getElementById(selected_calendarID+calYearID);
	obj.value=n;
}//#

function setCalendarDays(){
	var month= selected_date[selected_calendarID].m;
	var year= selected_date[selected_calendarID].y;

	var dweek= getWeekday(year,month);
	var dmax= getDaysPerMonth(month, year);
	if (selected_date[selected_calendarID].d > dmax){
		selected_date[selected_calendarID].d= dmax;
	}

	var today= getDate();

	var className;
	var day= 1;
	var cell= 0;
	for ( var j= 0; j < 6; j++){
		for ( var i= 0; i < 7; i++){

			if ( (cell < dweek) || (day > dmax) ){//set empty cells
				CacheCalendar[selected_calendarID][j][i].innerHTML= "&nbsp;";
				CacheCalendar[selected_calendarID][j][i].className= 'td_nodate';
				cell++;
				continue;
			}

			if( day == selected_date[selected_calendarID].d ){
				selected_cellObj[selected_calendarID]= CacheCalendar[selected_calendarID][j][i];
			}

			CacheCalendar[selected_calendarID][j][i].innerHTML= day;
			if (4 < i){
				CacheCalendar[selected_calendarID][j][i].style.color= "#AA4444"; // Sa, So - rot
			}else { // Mo,Di,Mi,Do,Fr
			}

			//
			if( day == today.d && month == today.m && year == today.y ){ // ist heute
				if( day == selected_date[selected_calendarID].d ){// is selected
					className= 'td_today_selected';
				}else{
					className= 'td_today_unselected';
				}
			}else{ // nicht heute
				if( day == selected_date[selected_calendarID].d ){// is selected
					className= 'td_selected';
				}else{
					className= 'td_unselected';
				}
			}// if
			CacheCalendar[selected_calendarID][j][i].className= className;

			day++;
			cell++;
		}//for i
	}//for j
}//#

/*function show_kalender_err(id,msg,show){
	var obj= document.getElementById(id+'_err');
	if (!show){
		obj.innerHTML= '';
		obj.className= 'noerr_kalender';
	}else{
		obj.innerHTML= msg;
		obj.className= 'err_kalender';
	}
}//#*/
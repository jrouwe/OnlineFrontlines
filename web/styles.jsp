<%@ page contentType="text/css; charset=UTF-8" session="false" %>

/* top.jsp */

body {
	margin: 0px;
	padding: 0px;
	border: 0px;
	color: #FFFFFF;
	background-color: #000000;
	background-image: url(${applicationScope.assetsUrl}/layout/background_gradient.jpg);
	background-repeat: repeat-x;
	font-family: Arial, sans-serif;
	font-weight: normal;
	font-size: 12px; 
	scrollbar-arrow-color: #808080;
	scrollbar-3dlight-color: #808080;
	scrollbar-darkshadow-color: #808080;
	scrollbar-face-color: #2f393c;
	scrollbar-highlight-color: #2f393c;
	scrollbar-shadow-color: #2f393c;
	scrollbar-track-color: #1d5673;
}

table {
	margin: 0px auto 0px auto;
}

a:link, a:visited { 
	color: #BEBEBE; 
}

a:hover, a:active { 
	color: #BFB849;
}

form {
	margin: 0px;
	padding: 0px;
	border: 0px;
}

img {
	margin: 0px;
	padding: 0px;
	border: 0px;
}

#main {
	margin: 10px auto;
	padding: 0px;
	width: 990px;
}

#banner {
	clear: left;
	float: left;
	width: 750px;
	height: 33px;
	margin: 0px;
	padding: 75px 0px 0px 0px;
	background-image: url(${applicationScope.assetsUrl}/layout/top_banner.jpg);
	background-repeat: no-repeat;
	font-family: "Arial Black", Arial, sans-serif;
	font-size: 11px;
	color: #BEBEBE; 
	text-align: center;
}

#user_info {
	float: left;
	width: 225px;
	height: 93px;
	margin: 0px;
	padding: 15px 0px 0px 15px;
	background-image: url(${applicationScope.assetsUrl}/layout/user_info.jpg);
	background-repeat: no-repeat;
}

#userinfo_left {
	float: left;
	width: 50px;
	margin: 0px 7px 0px 0px;
	padding: 0px;
}

#userinfo_image {
	clear: left;
	float: left;
	width: 50px;
	height: 50px;
	margin: 0px;
	padding: 0px;
}

#userinfo_right {
	float: left;
	width: 150px;
	margin: 0px; 
	padding: 0px;
}

#userinfo_name {
	clear: left;
	float: left;
	width: 150px;
	height: 24px;
	margin: 0px;
	padding: 0px;
	font-size: 18px;
	font-weight: bold; 
	white-space: nowrap;
	color: #BFB849;
	text-overflow: ellipsis;
	overflow: hidden;
	white-space: nowrap;
}

#userinfo_loggedin_info {
	clear: left;
	float: left;
	width: 150px;
	margin: 2px 0px;
	padding: 0px;
	font-size: 9px; 
	color: #BEBEBE; 
}

#userinfo_logout_button {
	clear: left;
	float: left;
	width: 94px;
	height: 13px;
	margin: 0px;
	padding: 0px;
	background-image: url(${applicationScope.assetsUrl}/layout/black_button.jpg);
	background-repeat: repeat-x;
	background-color: #505050;
	font-family: "Arial Black", Arial, sans-serif;
	font-size: 10px;
	text-align: center;
}

#userinfo_logout_button a {
	text-decoration: none;
}

#userinfo_notloggedin_info {
	margin: 27px 0px 0px 70px;
	clear: left;
	float: left;
	font-size: 12px; 
}

#menu {
	clear: left;
	width: 880px;
	height: 26px;
	margin: 0px;
	padding: 0px 0px 0px 110px;
	background-image: url(${applicationScope.assetsUrl}/layout/top_empty_off.jpg);
	background-repeat: repeat-x; 
	background-color: #3B5D19;
}

.menu_item, .menu_item_selected {
	float: left;
	width: 110px;
	height: 22px;
	margin: 0px;
	padding: 4px 0px 0px 0px;
	text-align: center;
} 

.menu_item {
	background-image: url(${applicationScope.assetsUrl}/layout/top_button_off.jpg);
	background-repeat: no-repeat;
} 

.menu_item_selected {
	background-image: url(${applicationScope.assetsUrl}/layout/top_button_on.jpg);
	background-repeat: no-repeat;
	background-color: #6d8b45;
} 

.menu_item a, .menu_item_selected a {
	font-weight: bold; 
	font-size: 14px; 
	text-decoration: none;				
}

#sub_menu {
	clear: left;
	width: 100%;
	height: 16px;
	background-image: url(${applicationScope.assetsUrl}/layout/sub_menu_bar.jpg);
	background-repeat: no-repeat; 
	background-color: #3B4D19;
}

.sub_menu_item, .sub_menu_item_selected {
	float: left;
	height: 12px;
	padding: 2px 15px;
	font-size: 9px; 
}

.sub_menu_item a:link, .sub_menu_item a:visited {
	color: #cdcdcd;
}

.sub_menu_item a:hover, .sub_menu_item a:active {
	color: #ffffff;
}

.sub_menu_item_selected a:link, .sub_menu_item_selected a:visited {
	color: #BFB849;
}

.sub_menu_item_selected a:hover, .sub_menu_item_selected a:active {
	color: #FFFF50;
}

#member_line {
	clear: left;
	width: 100%;
	height: 14px;
	padding: 2px 0px 0px 0px;
	color: #cdcdcd;
	font-size: 9px;
	text-align: center; 
	background-image: url(${applicationScope.assetsUrl}/layout/sub_menu_bar.jpg);
	background-repeat: no-repeat; 
	background-color: #3B4D19;
}

.member_line_emph {
	color: #FF2B2B;
}

#content {
	clear: left;
	width: 100%;
	margin: 0px;
	text-align: center;
}

#content_left {
	width: 260px;
	float: left;
}

#content_center {
	width: 466px;
	float: left;
}

#content_center_single_col {
	width: 466px;
	margin: 0px auto;
	padding: 20px 0px;
}

#content_right {
	width: 260px;
	float: right;
}		

.vertical_divider {
	float: left;
	width: 2px;
	height: 526px;
	padding: 0px;
	margin: 0px;
	background-image: url(${applicationScope.assetsUrl}/layout/vertical_divider.jpg);
	background-repeat: repeat-y; 
}

#footer {
	clear: left;
	width: 100%;
	text-align: center;
}

/* Ads */

.ad {
	float: left; 
	clear: left; 
 	padding: 0px; 
 	margin: 0px; 
 	border: 0px; 
 	background-color: #303d1f;
 	background-image: url(${applicationScope.assetsUrl}/layout/green_background.png);
	background-repeat: repeat;
}

.adbanner_left, .adbanner_right {
	float: left;
	width: 131px;
	height: 90px;
}

.adbanner_left {
 	background-image: url(${applicationScope.assetsUrl}/ad/bottom_adv_left.png);
}

.adbanner {
	float: left;
	width: 728px;
	height: 90px;
}

.adbanner_right {
 	background-image: url(${applicationScope.assetsUrl}/ad/bottom_adv_right.png);
}

/* General headers */

.sub_title_260, .sub_title_466, .sub_title_260_dark {	
	clear: left;
	float: left;
	height: 20px;
	padding: 3px 0px 0px 0px;
	font-weight: bold; 
	font-size: 14px; 
	text-align: center;
	color: #BEBEBE; 
	background-repeat: no-repeat; 
	background-color: #3B5D19;
	text-transform: uppercase;
	text-overflow: ellipsis;
	overflow: hidden;
	white-space: nowrap;
}

.sub_title_260, .sub_title_260_dark {
	width: 260px;
	background-image: url(${applicationScope.assetsUrl}/layout/sub_title_small.jpg);
}

.sub_title_466 {
	width: 466px;
	background-image: url(${applicationScope.assetsUrl}/layout/sub_title_large.jpg);
}

.sub_title_260_dark {
	color: #606060;
}

/* General boxes */

.box_466 {
	clear: left;
	float: left;
	width: 466px;
	padding: 0px;
	margin: 0px;
	border: 0px;
	background-image: url(${applicationScope.assetsUrl}/layout/box_466_body.png);
	background-repeat: repeat-y;
	background-color: #1d5673;
}

.box_466_body {
	clear: left;
	width: 442px;
	margin: 0px 12px;
	padding: 0px;
	border: 0px;
	overflow: hidden;
}

.box_466_top, .box_466_bottom {
	clear: left;
	width: 466px;
	height: 11px;
	padding: 0px;
	margin: 0px;
	border: 0px;
	font-size: 1px;
	background-repeat: no-repeat;
}

.box_466_top {
	background-image: url(${applicationScope.assetsUrl}/layout/box_466_top.png);
}

.box_466_bottom {
	background-image: url(${applicationScope.assetsUrl}/layout/box_466_bottom.png);
}

.box_260, .box_260_dark {
	clear: left;
	float: left;
	width: 260px;
	padding: 0px;
	margin: 0px;
	border: 0px;
	background-repeat: repeat-y;
}

.box_260 {
	background-image: url(${applicationScope.assetsUrl}/layout/box_260_body.png);
	background-color: #1d5673;
}

.box_260_dark {
	background-image: url(${applicationScope.assetsUrl}/layout/box_260_dark_body.png);
	background-color: #001f2f;
}

.box_260_top, .box_260_bottom, .box_260_dark_top, .box_260_dark_bottom {
	clear: left;
	width: 260px;
	height: 11px;
	padding: 0px;
	margin: 0px;
	border: 0px;
	font-size: 1px;
	background-repeat: no-repeat;
}

.box_260_body, .box_260_dark_body {
	clear: left;
	width: 236px;
	margin: 0px 12px;
	padding: 0px;
	border: 0px;
	overflow: hidden;
}

.box_260_top {
	background-image: url(${applicationScope.assetsUrl}/layout/box_260_top.png);
}

.box_260_bottom {
	background-image: url(${applicationScope.assetsUrl}/layout/box_260_bottom.png);
}

.box_260_dark_top {
	background-image: url(${applicationScope.assetsUrl}/layout/box_260_dark_top.png);
}

.box_260_dark_bottom {
	background-image: url(${applicationScope.assetsUrl}/layout/box_260_dark_bottom.png);
}

/* Plain table */

.ptable, .ptable th, .ptable td {
	border-color: #ffffff;
	border-width: 1px;
	border-style: solid;
	border-collapse: collapse;
}

/* General table with name / value pairs */

.stable {
	float: left;
	width: 100%;
	border: 0px;
	border-spacing: 0px;
	border-collapse: collapse;
	table-layout: fixed;	
}

.slabel, .svalue {
	height: 20px;
	padding: 0px;
	margin: 0px;
	font-size: 12px;
	text-align: center;
	vertical-align: top;
	background-repeat: repeat-x;
	text-overflow: ellipsis;
	overflow: hidden;
	white-space: nowrap;
}

.slabel {
	background-image: url(${applicationScope.assetsUrl}/layout/text_bar_black.jpg);
}

.svalue {
	background-image: url(${applicationScope.assetsUrl}/layout/text_bar_green.jpg);
}

/* Generic list table with alternating lines */

.ltable {
	float: left;
	width: 100%;
	border: 0px;
	border-spacing: 0px;
	border-collapse: collapse;
	table-layout: fixed;	
}

.ltable th {
	height: 14px;
	font-size: 12px;
	font-weight: bold;
	border: 0px;
	padding: 0px;
	background-repeat: repeat-x;
	background-image: url(${applicationScope.assetsUrl}/layout/line_top.png);
}

.lline1, .lline1_left, .lline1_right, .lline2, .lline2_left, .lline2_right {
	height: 14px;
	font-size: 12px;
	border: 0px;
	padding: 0px 1px;
	background-repeat: repeat-x;
	text-overflow: ellipsis;
	overflow: hidden;
	white-space: nowrap;
}

.lline1, .lline1_left, .lline1_right {
	background-image: url(${applicationScope.assetsUrl}/layout/line_back1.png);
}

.lline2, .lline2_left, .lline2_right {
	background-image: url(${applicationScope.assetsUrl}/layout/line_back2.png);
}

.lline1_left, .lline2_left {
	padding: 0px 0px 0px 10px;
}

.lline1_right, .lline2_right {
	padding: 0px 10px 0px 0px;
}

.llink:link, .llink:visited { 
	color: #ffffff; 
}

.llink:hover, .llink:active { 
	color: #BFB849;
}

/* Action with GO button */

.gtable {
	width: 100%;
	padding: 0px;
	margin: 0px auto;
	border: 0px;
	border-spacing: 0px;
	border-collapse: collapse;
}

.go_action {
	height: 20px;
	margin: 0px;
	padding: 0px;
	color: #cdcdcd; 
	background-image: url(${applicationScope.assetsUrl}/layout/text_bar_black.jpg);
	background-repeat: repeat-x;
	font-size: 12px;
	vertical-align: top;
}

.go_action a {
	text-decoration: none;
}

.go_action a:link, .go_action a:visited { 
	color: #ffffff;
	text-decoration: none;
}

.go_action a:hover, .go_action a:active { 
	color: #E0D345;
	text-decoration: none;
}

.go_arrow {
	width: 22px;
	height: 20px;
	margin: 0px;
	padding: 0px;
	background-image: url(${applicationScope.assetsUrl}/layout/go_arrow.jpg);
	background-repeat: no-repeat;
}

.go_button {
	width: 50px;
	height: 20px;
	margin: 0px;
	padding: 0px;
	background-image: url(${applicationScope.assetsUrl}/layout/black_button.jpg);
	background-repeat: repeat-x;
	font-family: "Arial Black", Arial, sans-serif;
	font-size: 10px;
	vertical-align: top;
}

.go_button a:link, .go_button a:visited { 
	color: #C4B93B;
	text-decoration: none;
}

.go_button a:hover, .go_button a:active { 
	color: #E0D345;
	text-decoration: none;
}

/* interface tag styles */

.errorMessage {
	color: #ff0000;
}

.wwgrp {
	clear: left;
	float: left;
	width: 100%;
	margin: 0px;
	padding: 5px 0px;
}

.wwlbl, .wwctrl {
	float: left;
	width: 43%;
	margin: 0px 2%;
}

.wwlbl {
	text-align: right;
	color: #bdc12c;
	font-variant: small-caps;	
}

.wwctrl {
	text-align: left;
}

.input_text, .input_textarea, .input_password, .input_select, .input_file, .input_submit {
	color: #ffffff;
	background-color: #2f393c;
	border: 1px solid #808080;
	margin: 0px;
	overflow-x: hidden;
}

.input_text, .input_textarea, .input_password, .input_select, .input_file {
	width: 100%;
	padding: 0px;
}

.input_checkbox {
	color: #ffffff;
	margin: 0px;
	padding: 0px;
}

/* UserStats.jsp / UserStatsList.jsp */

#userstats_icons {
	float: left;
	height: 66px;
	margin: 0px 0px 7px 15px;
}

.userstats_icon {
	float: left;
	width: 50px;
	height: 50px;
	padding: 8px;
	background-image: url(${applicationScope.assetsUrl}/layout/border_66x66.gif);
	background-repeat: no-repeat; 
}

#userstats_worldmap, #userstats_worldmap1, #userstats_worldmap2, #userstats_worldmap3, #userstats_worldmap4, #userstats_worldmap5, #userstats_worldmap6, #userstats_worldmap7 {
	float: left;
	position: relative;
	top: 0px;
	left: 0px;
	width: 466px;
	height: 240px;
	padding: 0px;
	margin: 0px;
	background-repeat: no-repeat; 
}

#userstats_worldmap {
	background-image: url(${applicationScope.assetsUrl}/userstats/world_map_small.jpg);
}

#userstats_worldmap1 {
	background-image: url(${applicationScope.assetsUrl}/userstats/world_map_small_1.jpg);
}

#userstats_worldmap2 {
	background-image: url(${applicationScope.assetsUrl}/userstats/world_map_small_2.jpg);
}

#userstats_worldmap3 {
	background-image: url(${applicationScope.assetsUrl}/userstats/world_map_small_3.jpg);
}

#userstats_worldmap4 {
	background-image: url(${applicationScope.assetsUrl}/userstats/world_map_small_4.jpg);
}

#userstats_worldmap5 {
	background-image: url(${applicationScope.assetsUrl}/userstats/world_map_small_5.jpg);
}

#userstats_worldmap6 {
	background-image: url(${applicationScope.assetsUrl}/userstats/world_map_small_6.jpg);
}

#userstats_worldmap7 {
	background-image: url(${applicationScope.assetsUrl}/userstats/world_map_small_7.jpg);
}

.userstats_worldmap_number {
	position: absolute;
	padding: 2px;
	margin: 0px;
	background-color: #000000;
	border: 1px solid #ffffff;
	text-align: left;
	font-size: 14px;
	color: #ffffff;
}

.userstats_medal {
	float: left;
	width: 42px;
	height: 42px;
	padding: 8px;
	background-image: url(${applicationScope.assetsUrl}/layout/border_58x58.gif);
	background-repeat: no-repeat; 
}

.userstats_challenge_button {
	float: left;
	padding: 5px 0px 0px 90px;
}

/* Home.jsp */

#home_banner {
	float: left;
	clear: left;
	width: 426px;
	height: 56px;
	margin: 0px;
	padding: 45px 20px 0px 20px;
	background-image: url(${applicationScope.assetsUrl}/home/banner_home.png);
	font-size: 12px;
}

#playnow_button_back {
	float: left;
	clear: left;
	width: 466px;
	height: 162px;
	background-color: #15190F;
}

#playnow_button {
	float: left;
	clear: left;
	width: 154px;
	height: 27px;
	padding: 67px 0px 0px 156px;
}

#home_red, #home_blue {
	float: left;
	clear: left;
	width: 240px;
	height: 100px;
	margin: 0px;
	padding: 140px 10px 0px 10px;
}

#home_red {
	background-image: url(${applicationScope.assetsUrl}/home/home_red.png);
}

#home_blue {
	background-image: url(${applicationScope.assetsUrl}/home/home_blue.png);
}

.news_item {
}

.news_header {
	clear: left;
 	color: #AFAFAF;
 	font-size: 12px;
 	padding-bottom: 2px;
}

.news_text {
	clear: left;
 	color: #FFFFFF;
 	font-size: 12px;
 	padding-bottom: 15px;
}

.news_viewall {
	clear: left;
 	font-size: 12px;
}

.challenge_button {
	padding: 1px 0px 0px 0px;
}

/* PlayNow.jsp */

#playnow_ranked, #playnow_ai, #playnow_mail, #playnow_continue, #playnow_custom {
	clear: left;
	width: 466px;
	height: 101px;
	margin: 0px;
	padding: 0px;
}

#playnow_ai {
	background-image: url(${applicationScope.assetsUrl}/play_now/play_now_ai.jpg);
}

#playnow_ranked {
	background-image: url(${applicationScope.assetsUrl}/play_now/play_now_world.jpg);
}

#playnow_mail {
	background-image: url(${applicationScope.assetsUrl}/play_now/play_now_mail.jpg);
}

#playnow_custom {
	background-image: url(${applicationScope.assetsUrl}/play_now/play_now_custom.jpg);
}

#playnow_continue {
	background-image: url(${applicationScope.assetsUrl}/play_now/play_now_continue.jpg);
}

.playnow_actions1 {
	float: left;
	width: 300px;
	padding: 45px 0px 0px 120px;
}

.playnow_actions2 {
	float: left;
	width: 300px;
	padding: 30px 0px 0px 120px;
}

/* PBMCreateInvitation.jsp */

#PBMCreateInvitation_mapPreview, .tv_map_preview {
	clear: left;
	float: left;
	width: 466px;
	height: 141px;
	padding: 0px;
	margin: 0px;
	background-image: url(${applicationScope.assetsUrl}/play_by_mail/map_preview.png);
	background-repeat: no-repeat;
}

#PBMCreateInvitation_previewImage, .tv_preview_image {
	float: left;
	width: 185px;
	height: 130px;
	padding: 11px 0px 0px 11px;
	margin: 0px;
	text-align: left;
}

#PBMCreateInvitation_previewStats, .tv_preview_stats {
	float: left;
	width: 256px;
	height: 121px;
	padding: 20px 0px 0px 8px;
	margin: 0px;
}

#wwctrl_play, #PBMCreateInvitation_or {
	clear: left;
	float: left;
	width: 100%;
	margin: 0px auto;
	padding: 5px 0px;
}

#PBMCreateInvitation_description {
	font-size: 12px;
}

/* PBMShowInvitation.jsp */

#PBMShowInvitation_avatar {
	float: left;
	padding: 5px;
}

#PBMShowInvitation_invitation {
	float: left;
	padding: 5px;
	width: 365px;
	text-align: left;
}

.PBMShowInvitation_request_div {
	clear: left;
	float: left;
	width: 100%;
	text-align: center;
}

#wwgrp_PBMShowInvitation_accept {
	clear: left;
	float: left;
	padding: 0px 0px 5px 0px;
	width: 220px;
}

#wwgrp_PBMShowInvitation_decline {
	clear: none;
	float: left;
	padding: 0px 0px 5px 0px;
	width: 220px;
}

/* GameContinue.jsp */

.game_continue_your_turn:link, .game_continue_your_turn:visited {
	text-decoration: none;
	color: #FFFFFF;
}

.game_continue_your_turn:hover, .game_continue_your_turn:active {
	text-decoration: none;
	color: #BFB849;
}

.game_continue_other_turn:link, .game_continue_other_turn:visited {
	text-decoration: none;
	color: #7F7F7F;
}

.game_continue_other_turn:hover, .game_continue_other_turn:active {
	text-decoration: none;
	color: #BFB849;
}

/* HelpUnit.jsp */

.help_menu_divider {
	clear: left;
	float: left;
	width: 240px;	
	height: 4px;
	border: 0px;
	padding: 0px;
	margin: 0px;
	background-image: url(${applicationScope.assetsUrl}/layout/menu_divider.jpg);
	background-repeat: no-repeat;
	font-size: 1px;
}

.help_menu_line {
	clear: left;
	float: left;
	width: 100%;
	height: 31px;
}

.help_menu_tile_image {
	float: left;
	width: 35px;
	height: 31px;
	border: 0px;
	margin: 0px;
	padding: 0px;
}

.help_menu_item, .help_menu_item_selected {
	float: left;
	padding: 0px;
	margin: 10px 0px 0px 5px;
	border: 0px;
	background-repeat: repeat-x;
	background-image: url(${applicationScope.assetsUrl}/layout/text_bar_black.jpg);
}

.help_menu_item_selected {
	color: #BFB849;
}

.help_menu_item a {
	font-size: 12px;
	text-align: center;
	text-decoration: none;
	vertical-align: top;
}

.help_banner {
	clear: left;
	float: left;
	width: 466px;
	height: 99px;
}

.help_stats_table {
	float: left;
	width: 100%;
	border: 0px;
	border-spacing: 0px;
	border-collapse: collapse;
}

.help_stats_header, .help_stats_label, .help_stats_value {
	font-size: 12px;
	font-weight: bold; 
	text-align: center;
	vertical-align: top;
	background-repeat: repeat-x;
}

.help_stats_header {
	width: 50px;
}

.help_stats_label {
	background-image: url(${applicationScope.assetsUrl}/layout/text_bar_black.jpg);
}

.help_stats_value {
	background-image: url(${applicationScope.assetsUrl}/layout/text_bar_green.jpg);
	width: 50px;
}

.help_stats_divider {
	width: 2px;
	padding: 0px;
	margin: 0px;
	background-image: url(${applicationScope.assetsUrl}/layout/vertical_divider.jpg);
	background-repeat: repeat-y; 
}

.help_content {
	margin: 0px 5px;
	text-align: left;
}

/* TV.jsp */

#tv_banner_match_day {
	float: left;
	clear: left;
	width: 466px;
	height: 49px;
	margin: 0px;
	padding: 0px;
	background-image: url(${applicationScope.assetsUrl}/tv/banner_match_day.png);
}

.tv_banner_match_day_name {
	float: left;
	width: 193px;
	height: 19px;
	padding: 15px 20px;
	font-size: 14px;
	font-weight: bold;
	color: #ffffff; 
	overflow: hidden;
}

#tv_top_match {
	float: left;
	width: 100%;
	padding: 0px;
	margin: 0px;
	border: 0px;
}

/* HelpScreens.jsp */

.screens_image {
	clear: left;
	float: left;
	width: 100%;
	margin: 0px;
	border: 0px;
	padding: 0px;
}

.screens_nav {
	clear: left;
	float: left;
	width: 100%;
	height: 120px;
 	padding: 5px 0px; 
 	margin: 0px; 
 	border: 0px; 
 	background-color: #303d1f;
 	background-image: url(${applicationScope.assetsUrl}/layout/green_background.png);
	background-repeat: repeat;
	overflow: auto;
	overflow-x: scroll;
	overflow-y: hidden;
}

.screens_thumb {
	float: left;
	width: 166px;
	height: 100px;
	padding: 0px;
	border: 0px;
	margin: 0px 0px 0px 10px;
}

.screens_thumb a {
	padding: 0px;
	border: 0px;
	margin: 0px;
}

/* Tips.jsp */

.tip_image {
	float: left;
	clear: left;
	margin: 0px 5px 0px 0px;
	padding: 0px;
	border: 0px;
	width: 40px;
	height: 85px;
}

.tip_image_text {
	float: left;
	margin: 0px;
	padding: 0px;
	border: 0px;	
	width: 190px;
	height: 85px;
	text-align: left;
}

.tip_text {
	float: left;
	margin: 0px;
	padding: 0px;
	border: 0px;	
	height: 85px;
	text-align: left;
}

/* Social.jsp */

#twitter {
	float: left;
	clear: left;
	text-align: left;
	padding: 10px 0px 0px 46px;
	overflow: visible;
}

#facebook {
	float: left;
	clear: left;
	text-align: left;
	padding: 30px 0px 0px 46px;
	overflow: visible;
}

/* GamePlay.jsp */

.levellikes {
	float: left;
	clear: left;
}

.levellikeitem {
	float: left;
	padding: 5px 0px 0px 0px;
}

/* FacebookCanvas.jsp */

#FacebookCanvas_main {
	margin: 50px auto 0px auto;
	padding: 0px;
	width: 466px;
	text-align: center;
}

/* WorldMap.jsp */

.worldmap_background {
	float: left;
	clear: left;
	position: relative;
	width: 990px;
	height: 595px;
	background-image: url(${applicationScope.assetsUrl}/world_map/world_map.png);
}

.worldmap_lobby:link, .worldmap_lobby:visited {
	width: 83px;
	height: 82px;
	position: absolute;
	background-image: url(${applicationScope.assetsUrl}/world_map/info_window_up.png);
	text-decoration: none;
}

.worldmap_lobby_grey {
	width: 83px;
	height: 82px;
	position: absolute;
	background-image: url(${applicationScope.assetsUrl}/world_map/info_window_disabled.png);
}

.worldmap_lobby:hover {
	background-image: url(${applicationScope.assetsUrl}/world_map/info_window_over.png);
	text-decoration: none;
}

.worldmap_lobby:active { 
	background-image: url(${applicationScope.assetsUrl}/world_map/info_window_down.png);
	text-decoration: none;
}

.worldmap_lobbyname {
	position: absolute;
	left: 10px;
	top: 0px;
	width: 63px;
	font-size: 9px;
	color: #DFDF40;
}

.worldmap_games {
	position: absolute;
	left: 46px;
	top: 21px;
	width: 26px;
	font-size: 9px;
	color: #DFDF40;
}

.worldmap_red {
	position: absolute;
	left: 46px;
	top: 36px;
	height: 9px;
	background-image: url(${applicationScope.assetsUrl}/world_map/country_bar_red.png);
}

.worldmap_blue {
	position: absolute;
	left: 46px;
	top: 50px;
	height: 9px;
	background-image: url(${applicationScope.assetsUrl}/world_map/country_bar_blue.png);
}

.worldmap_level {
	position: absolute;
	left: 46px;
	top: 63px;
	width: 26px;
	font-size: 9px;
	color: #DFDF40;
}

.worldmap_totalgames {
	position: absolute;
	left: 67px;
	top: 573px;
	width: 26px;
	font-size: 9px;
	color: #DFDF40;
}

.worldmap_totalred {
	position: absolute;
	left: 383px;
	top: 574px;
	height: 9px;
	background-image: url(${applicationScope.assetsUrl}/world_map/country_bar_red.png);
}

.worldmap_totalblue {
	position: absolute;
	left: 613px;
	top: 574px;
	height: 9px;
	background-image: url(${applicationScope.assetsUrl}/world_map/country_bar_blue.png);
}

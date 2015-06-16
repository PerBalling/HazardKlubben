/*******************************************************************************
 * Snow Effect Script- By Altan d.o.o. (http://www.altan.hr/snow/index.html)
 * Visit Dynamic Drive DHTML code library (http://www.dynamicdrive.com/) for
 * full source code Last updated Nov 9th, 05' by DD. This notice must stay
 * intact for use Modified by PB
 ******************************************************************************/

// Configure below to change URL path to the snow image
var snowsrc1 = "pic/1000f160.gif"
var snowsrc2 = "pic/1000b160.gif"
// var snowsrc = snowsrc1
var snowImage = new Image()
// Configure below to change number of snow to render
var no = 6;
// Configure whether snow should disappear after x seconds (0=never; -1=Do not start):
// Passed from HTML <script>var hideSnow = {secs};</script>
var hidesnowtime = (typeof (hideSnow) == "undefined") ? 0 : hideSnow;
// Configure how much snow should drop down before fading ("windowheight" or
// "pageheight")
var snowdistance = "pageheight";

// /////////END Config//////////////////////////////////

var ie4up = (document.all) ? 1 : 0;
var ns6up = (document.getElementById && !document.all) ? 1 : 0;

var dx, xp, yp; // coordinate and position variables
var am, stx, sty; // amplitude and step variables
var i, doc_width = 800, doc_height = 600;

//alert(hidesnowtime)
if (hidesnowtime == -1){
	// Do not start snow
	alert("hide");
	//	hidesnow()
	hidesnowtime = 0.1;
}

if (ns6up) {
	doc_width = self.innerWidth;
	doc_height = self.innerHeight;
} else if (ie4up) {
	doc_width = iecompattest().clientWidth;
	doc_height = iecompattest().clientHeight;
}

dx = new Array();
xp = new Array();
yp = new Array();
am = new Array();
stx = new Array();
sty = new Array();
// snowImage.src = (snowsrc.indexOf("dynamicdrive.com") != -1) ? "snow.gif" :
// snowImage.src
for (i = 0; i < no; ++i) {
	// Toggle image
	if (i % 2 == 1) {
		snowImage.src = snowsrc1;
	} else {
		snowImage.src = snowsrc2;
	}

	dx[i] = 0; // set coordinate variables
	am[i] = Math.random() * 20; // set amplitude variables
	stx[i] = 0.02 + Math.random() / 10; // set step variables
	sty[i] = 0.7 + Math.random(); // set step variables
	// set position variables
	// xp[i] = Math.random() * (doc_width - snowImage.width - am[i]);
	setXp_i()
	yp[i] = Math.random() * doc_height;
	if (ie4up || ns6up) {
		if (i == 0) {
			document.write("<div id=\"dot" + i + "\" style=\"POSITION: absolute; Z-INDEX: " + i
					+ "; VISIBILITY: visible; TOP: 15px; LEFT: 15px;\"><a href=\"http://dynamicdrive.com\"><img src='"
					+ snowImage.src + "' border=\"0\"><\/a><\/div>");
		} else {
			document.write("<div id=\"dot" + i + "\" style=\"POSITION: absolute; Z-INDEX: " + i
					+ "; VISIBILITY: visible; TOP: 15px; LEFT: 15px;\"><img src='" + snowImage.src + "' border=\"0\"><\/div>");
		}
	}
}

if (ie4up || ns6up) {
	snowIE_NS6();

	if (hidesnowtime > 0)
		setTimeout("hidesnow()", hidesnowtime * 1000)
}

function iecompattest() {
	return (document.compatMode && document.compatMode != "BackCompat") ? document.documentElement : document.body
}

function snowIE_NS6() { // IE and NS6 main animation function
	doc_width = ns6up ? window.innerWidth - 10 : iecompattest().clientWidth - 10;
	doc_height = (window.innerHeight && snowdistance == "windowheight") ? window.innerHeight
			: (ie4up && snowdistance == "windowheight") ? iecompattest().clientHeight
					: (ie4up && !window.opera && snowdistance == "pageheight") ? iecompattest().scrollHeight
							: iecompattest().offsetHeight;
	for (i = 0; i < no; ++i) { // iterate for every dot
		yp[i] += sty[i];

		if (yp[i] > doc_height - snowImage.height - 1) {
//			xp[i] = Math.random() * (doc_width - snowImage.width - am[i]);
			setXp_i()
			yp[i] = 0;
			stx[i] = 0.02 + Math.random() / 10;
			sty[i] = 0.7 + Math.random();
		}
		dx[i] += stx[i];
		document.getElementById("dot" + i).style.top = yp[i] + "px";
		document.getElementById("dot" + i).style.left = xp[i] + am[i] * Math.sin(dx[i]) + "px";
	}
	snowtimer = setTimeout("snowIE_NS6()", 20);
}

function hidesnow() {
	if (window.snowtimer)
		clearTimeout(snowtimer)
	for (i = 0; i < no; i++)
		document.getElementById("dot" + i).style.visibility = "hidden"
}

function setXp_i() {
	// Do not use the first xx cols
	xp[i] = Math.random() * (doc_width - snowImage.width - am[i] - 300) + 300;
}

$(document).ready(main);

function main() {
  // Apply the xslt transformation to the xml file
  transform();

  // Change the title: remove the "T"
  $("#title").text(function () {
    return "Log: " + $(this).text().replace("T", " ");
  });

  // change the milis to time
  $('.millis').each(convertToTime);

  $('#search-input').focus();
  $('#search-input').keyup(function(){
    search($('#search-input').val());
  });

  // Set the function for the select buton for the level
  $("#levelSelect").on('change', select);
  // Set the function for the select buton for the logger
  $("#loggerSelect").on('change', select);
  // Set the function for the select buton for the children
  $("#children").on('change', select);

  select();
}

function hideAll() {
  $(".log-tr").hide();
}

function select() {
  hideAll();
  var authorizedLevels = [];
  switch ($('#levelSelect').val()) {
    case 'finest':
      authorizedLevels.push('FINEST');
    case 'finer':
      authorizedLevels.push('FINER');
    case 'fine':
      authorizedLevels.push('FINE');
    case 'config':
      authorizedLevels.push('CONFIG');
    case 'info':
      authorizedLevels.push('INFO');
    case 'warning':
      authorizedLevels.push('WARNING');
    case 'severe':
      authorizedLevels.push('SEVERE');
      break;
    default:
      //Nothing
  }
  var logger = $('#loggerSelect').val();
  var children = document.getElementById("children");
  $('.log-tr').each(function() {
    if (($(this).attr("logger") == logger || logger == 'All' || (children.checked == true && $(this).attr("logger").indexOf(logger) !== -1)) && authorizedLevels.includes($(this).attr("level"))) {
      $(this).show();
    }
  });
}

function search(string){
  //alert("Not implemented");
  $('.log-tr').each(function(){
    if ($(this).text().indexOf(string)!=-1) {
      $(this).removeClass('notSearched');
    }else{
      $(this).addClass('notSearched');
    }
  });
}

function convertToTime() {
  milis = $(this).text();
  $(this).text(msToTime(milis));
}

function msToTime(s) {
  var ms = s % 1000;
  var secs = (s / 1000) % 60;
  var mins = (s / (1000 * 60)) % 60;
  var hrs = (s / (1000 * 60 * 60)) % 24;
  if (hrs < 10) {
    hours = "0" + hrs.toFixed(0);
  } else {
    hours = "" + hrs.toFixed(0);
  }
  if (mins < 10) {
    minutes = "0" + mins.toFixed(0);
  } else {
    minutes = "" + mins.toFixed(0);
  }
  if (secs < 10) {
    seconds = "0" + secs.toFixed(0);
  } else {
    seconds = "" + secs.toFixed(0);
  }
  return hours + 'H' + minutes + ' ' + seconds + 's ' + ms.toFixed(0) + 'ms';
}

// *****************************************************************************
// These 2 function are from https://www.w3schools.com/xml/xsl_client.asp
function loadXMLDoc(filename) {
  if (window.ActiveXObject) {
    xhttp = new ActiveXObject("Msxml2.XMLHTTP");
  } else {
    xhttp = new XMLHttpRequest();
  }
  xhttp.open("GET", filename, false);
  try {
    xhttp.responseType = "msxml-document"
  } catch (err) {} // Helping IE11
  xhttp.send("");
  return xhttp.responseXML;
}

function transform() {
  xml = loadXMLDoc("log.xml");
  xsl = loadXMLDoc("log.xsl");
  // code for IE
  if (window.ActiveXObject || xhttp.responseType == "msxml-document") {
    ex = xml.transformNode(xsl);
    document.getElementById("content").innerHTML = ex;
  }
  // code for Chrome, Firefox, Opera, etc.
  else if (document.implementation && document.implementation.createDocument) {
    xsltProcessor = new XSLTProcessor();
    xsltProcessor.importStylesheet(xsl);
    resultDocument = xsltProcessor.transformToFragment(xml, document);
    document.getElementById("content").appendChild(resultDocument);
  }
}
// *****************************************************************************

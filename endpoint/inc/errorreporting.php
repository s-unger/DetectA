<?php
//Bitte auskommentieren wenn nicht benötigt:
errorreporting();
function errorreporting() {
  ini_set('display_errors', 1);
  ini_set('display_startup_errors', 1);
  error_reporting(E_ALL);
  echo "Error Reporting ist an.<br/>";
}

?>

<?php
include "inc/errorreporting.php";
include "inc/jwt.php";
include "inc/db.php";

$id = get_id_and_check_jwt($_SERVER['HTTP_USER_AGENT'], $secret);
//$id = get_id_and_check_jwt("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDk4ODc1MzYsImlkIjoiNjQwOTRhZDgxMzQ1NCIsInZlciI6IjEifQ.2GLd1EnNPifLcRBxUzSd0fT2ghHG-o3y8ZBLTguKgms");

if($id == "") {
	header('HTTP/1.0 403 Forbidden');
	echo "403 Forbidden";
	exit;
} else {
	$statement = $pdo->prepare("INSERT INTO data (id, data) VALUES (?, ?)");
	$statement->execute(array($id, $_POST["data"])); 
}

?>

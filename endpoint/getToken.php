<?php
include "inc/errorreporting.php";
include "inc/jwt.php";
include "inc/db.php";

$headers = array('alg'=>'HS256','typ'=>'JWT');
$payload = array('exp'=>(time() + 31556952),'id'=>uniqid(),'ver'=>'1');

$jwt = generate_jwt($headers, $payload, $secret);

echo $jwt;
?>

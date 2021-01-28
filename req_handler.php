<?php
use Rize\UriTemplate;

function handle($_method, $_rif, $callback) {
  $method_correct = strtoupper($_method) == strtoupper($_SERVER['REQUEST_METHOD']);
  if (!$method_correct) return;
  $rif = $_GET['rif'];
  $rif_correct = strtoupper($_rif) == strtoupper($rif);
  if (!$rif_correct) return;
  $callback();
  exit;
}
?>
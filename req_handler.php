<?php

function handle($_method, $_rif, $callback) {
  $method_correct = strtoupper($_method) == strtoupper($_SERVER['REQUEST_METHOD']);
  if (!$method_correct) return;
  $rif = $_GET['rif'];
  try {
    $data = url_tpl_data($rif, $_rif);
    $callback($data);
    exit;
  } catch (exception $e) {
    return $e;
  }
}

function url_tpl_data($_url, $_url_tpl) {
  $_url = trim($_url, '/');
  $_url_tpl = trim($_url_tpl, '/');

  $parts_a = explode('/', $_url);
  $parts_b = explode('/', $_url_tpl);

  if (count($parts_a) != count($parts_b))
    throw new Exception('part len does not match');

  $data = array();
  for ($i=0; $i<=count($parts_a); $i++) {
    if ($parts_b[$i][0] != '{') {
      if (strtoupper($parts_b[$i]) != strtoupper($parts_a[$i]))
        throw new Exception('url segment does not match');
      continue;
    }
    $var_name = str_replace(array('{', '}'), '', $parts_b[$i]);
    $data[$var_name] = $parts_a[$i];
  }
  return $data;
}

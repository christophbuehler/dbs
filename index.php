<?php
/**
 * Access the api using the rif (ressource identifier) param (?rif=xy).
 * If this parameter is not provided, the contents of index.html are returned.
 */

require_once('req_handler.php');
require_once('database.php');

/**
 * Get all unis.
 * [GET] /uni
 */
handle('GET', '/uni', function ($data) {
  $env = get_env();
  $db = new Database($env);
  return $db->getUnis();
});

/**
 * Get all posts of an uni.
 * [GET] /post/{uni}
 */
handle('GET', '/post/{uni}', function ($data) {
  echo $data['uni'];
  $env = get_env();
  echo $env['repo'];
  $db = new Database($env);
});

return file_get_contents('index.html');

function get_env() {
  $env = array();
  $env_file = file_get_contents('./univie.env');
  $lines = explode(PHP_EOL, $env_file);
  for ($i=0; $i<count($lines); $i++) {
    $parts = explode('=', $lines[$i]);
    if (count($parts) != 2) continue;
    $env[$parts[0]] = $parts[1];
  }
  return $env;
}



/// read
// [GET] /api/posts/univie
// => call stored procedure "universityPosts"

// /// create
// [POST] /api/posts/univie?refId=xy
// => create new post


// /// update
// [PUT] /api/posts/univie/post_id


// /// delete
// [DELETE] /api/posts/univie/post_id

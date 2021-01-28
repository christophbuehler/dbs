<?php
require_once('req_handler.php');
require_once('database.php');



/**
 * Get all posts of an uni.
 * 
 * example:
 * http://wwwlab.cs.univie.ac.at/~christophb77/dbs/index.php?rif=/posts/univie
 */
handle('GET', '/posts/{uni}', function ($data) {
  echo $data['uni'];

  $env = get_env();
  echo $env['repo'];

  $db = new Database($env);
});

function get_env() {
  $env = array();
  $env_file = file_get_contents('./univie.env');
  $lines = explode('\n', $env_file);
  for ($i=0; $i<count($lines); $i++) {
    $parts = explode('=', $lines[$i]);
    $env[$parts[0]] = $parts[$i];
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

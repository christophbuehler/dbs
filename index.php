<?php
/**
 * Access the api using the rif (ressource identifier) param (?rif=xy).
 * If this parameter is not provided, the contents of index.html are returned.
 */

require_once('req_handler.php');
require_once('database.php');

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS, PUT, DELETE');
header('Access-Control-Allow-Headers: Content-Type, X-Auth-Token, Origin, Authorization');

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
  $env = get_env();
  $db = new Database($env);
  return $db->getPosts($data['uni']);
});

/**
 * Get post discussion.
 * [GET] /post/{uni}/{post}
 */
handle('GET', '/post/{uni}/{post}', function ($data) {
  $env = get_env();
  $db = new Database($env);
  return $db->getPostDiscussion($data['uni'], $data['post']);
});

/**
 * Create a post for an uni.
 * [POST] /post/{uni}
 */
handle('POST', '/post/{uni}', function ($data) {
  $env = get_env();
  $db = new Database($env);
  $body = json_decode(file_get_contents('php://input'), true);
  return $db->createPost($data['uni'], $body);
});

/**
 * Create a reply for a post.
 * [POST] /post/{uni}/{post}
 */
handle('POST', '/post/{uni}/{post}', function ($data) {
  $env = get_env();
  $db = new Database($env);
  $body = json_decode(file_get_contents('php://input'), true);
  return $db->createReply($data['uni'], $data['post'], $body);
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

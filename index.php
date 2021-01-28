<?php

handle('GET', '/posts/univie', function () {
  echo '/posts/univie';
});

// [GET] /api/posts/univie

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





lorem

/ => index.html





/// read
[GET] /api/posts/univie
=> call stored procedure "universityPosts"

/// create
[POST] /api/posts/univie?refId=xy
=> create new post


/// update
[PUT] /api/posts/univie/post_id


/// delete
[DELETE] /api/posts/univie/post_id

#4897
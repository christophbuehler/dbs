<?php
require_once req_handler;

// https://wwwlab.cs.univie.ac.at/~christophb77/dbs/index.php?rif=/posts/univie
handle('GET', '/posts/{uni}', function () {

  echo '/posts/univie';
});

// [GET] /api/posts/univie


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
?>

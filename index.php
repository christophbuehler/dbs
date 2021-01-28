<?php


$postData = json_encode($_POST);
$_POST = json_decode(Utils::utf8_urldecode(

  // replace plus sign
  str_replace('+', '%2B', $postData)
), true);

$this->reqArgs = array_merge($_POST, $_GET);


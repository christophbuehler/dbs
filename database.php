<?php

class Database
{
  protected $conn;

  public function __construct($env)
  {
    try {
      $this->conn = oci_connect(
        $env['uname'],
        $env['pwd'],
        $env['php_conn_str']
      );
      if (!$this->conn) {
        die("DB error: Connection can't be established!");
      }
    } catch (Exception $e) {
      die("DB error: {$e->getMessage()}");
    }
  }

  public function __destruct()
  {
      oci_close($this->conn);
  }

  public function getUnis()
  {
    $sql = 'SELECT * FROM universitaet';
    $statement = oci_parse($this->conn, $sql);
    oci_execute($statement);
    oci_fetch_all($statement, $res, null, null, OCI_FETCHSTATEMENT_BY_ROW);
    oci_free_statement($statement);
    return $res;
  }

  public function getPosts($uniId)
  {
    $sql = "SELECT * FROM REV_POSTS WHERE UNI_ID = {$uniId}";
    $statement = oci_parse($this->conn, $sql);
    oci_execute($statement);
    oci_fetch_all($statement, $res, null, null, OCI_FETCHSTATEMENT_BY_ROW);
    oci_free_statement($statement);
    return $res;
  }

  private function createAuthor($kuerzel, $uniId) {
    $sql = "INSERT INTO AUTOR (kuerzel, uni_id) VALUES ('{$kuerzel}', {$uniId}) returning id into :inserted_id";
    $statement = oci_parse($this->conn, $sql);
    oci_bind_by_name($statement, ':inserted_id', $idNumber, 32);
    oci_execute($statement);
    return $idNumber;
  }

  private function createRevision($postId, $uniId, $titel, $inhalt, $versnr = 0) {
    $sql = "INSERT INTO REVISION (post_id, uni_id, versnr, titel, inhalt, datum) VALUES ({$postId}, {$uniId}, {$versnr}, '{$titel}', '{$inhalt}', CURRENT_DATE)";
    $statement = oci_parse($this->conn, $sql);
    oci_execute($statement);
  }

  public function getMaxRevNr($uniId, $postId) {
    $sql = "SELECT MAX(VERSNR) maxvers FROM REVISION WHERE UNI_ID = {$uniId} AND POST_ID = {$postId}";
    $statement = oci_parse($this->conn, $sql);
    oci_define_by_name($statement, 'maxvers', $max);
    oci_execute($statement);
    oci_fetch($statement, $res);
    oci_free_statement($statement);
    return $max;
  }

  public function getPostDiscussion($uniId, $postId) {
    $sql = "SELECT * FROM POST_DISCUSSION WHERE UNI_ID = {$uniId} AND REF_POST_ID = {$postId}";
    $statement = oci_parse($this->conn, $sql);
    oci_execute($statement);
    oci_fetch_all($statement, $res, null, null, OCI_FETCHSTATEMENT_BY_ROW);
    oci_free_statement($statement);
    return $res;
  }

  public function deletePost($uniId, $postId)
  {
    // delete revisions
    $sql = "DELETE FROM REVISION WHERE POST_ID = {$postId}";
    $statement = oci_parse($this->conn, $sql);
    oci_execute($statement);
    oci_free_statement($statement);
  }

  public function updatePost($uniId, $postId, $data)
  {
    $revnr = self::getMaxRevNr($uniId, $postId) + 1;
    self::createRevision($postId, $uniId, $data['titel'], $data['inhalt'], $revnr);
  }

  public function createPost($uniId, $data)
  {
    // create author
    $authorId = self::createAuthor('anonymous', $uniId);

    // create post
    $sql = "INSERT INTO POST (hauptautor_id, ref_post_id, uni_id, datum) VALUES (:p_hauptautor_id, NULL, :p_uni_id, CURRENT_DATE) returning id into :inserted_id";
    // $sql = "BEGIN INSERT_POST(:p_hauptautor_id, :p_ref_post_id, :p_uni_id); END;";
    $statement = oci_parse($this->conn, $sql);

    oci_bind_by_name($statement, ':p_hauptautor_id', $authorId, 32);
    // oci_bind_by_name($statement, ':p_ref_post_id', null, 32);
    oci_bind_by_name($statement, ':p_uni_id', $uniId, 32);
    oci_bind_by_name($statement, ':inserted_id', $idNumber, 32);

    oci_execute($statement);
    oci_free_statement($statement);

    // create revision
    self::createRevision($idNumber, $uniId, $data['titel'], $data['inhalt']);

    return $idNumber;
  }

  public function createReply($uniId, $refPostId, $data)
  {
    // create author
    $authorId = self::createAuthor('anonymous', $uniId);

    // create post
    $sql = "INSERT INTO POST (hauptautor_id, ref_post_id, uni_id, datum) VALUES (:p_hauptautor_id, :p_ref_post_id, :p_uni_id, CURRENT_DATE) returning id into :inserted_id";
    // $sql = "BEGIN INSERT_POST(:p_hauptautor_id, :p_ref_post_id, :p_uni_id); END;";
    $statement = oci_parse($this->conn, $sql);

    oci_bind_by_name($statement, ':p_hauptautor_id', $authorId, 32);
    oci_bind_by_name($statement, ':p_ref_post_id', $refPostId, 32);
    oci_bind_by_name($statement, ':p_uni_id', $uniId, 32);
    oci_bind_by_name($statement, ':inserted_id', $idNumber, 32);

    oci_execute($statement);
    oci_free_statement($statement);

    // create revision
    self::createRevision($idNumber, $uniId, $data['titel'], $data['inhalt']);

    return $idNumber;
  }
}
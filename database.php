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
    $sql = "SELECT * FROM REV_POSTS WHERE uni_id = '%{$uniId}%'";
    $statement = oci_parse($this->conn, $sql);
    oci_execute($statement);
    oci_fetch_all($statement, $res, null, null, OCI_FETCHSTATEMENT_BY_ROW);
    oci_free_statement($statement);
    return $res;
  }
}
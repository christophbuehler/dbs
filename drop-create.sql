----------------- DROP -----------------
DROP TABLE revision;
DROP TABLE mitarbeiter;
DROP TABLE autor_hat_artikel;
DROP TABLE autor_hat_sprache;
DROP TABLE autor;
DROP TABLE person;
DROP TABLE artikel;
DROP TABLE universitaet;
DROP TABLE sprache;

----------------- CREATE -----------------
CREATE TABLE person (
    id NUMBER GENERATED BY DEFAULT AS IDENTITY,
    nachname VARCHAR(20),
    vorname VARCHAR(20),
    dob DATE,
    PRIMARY KEY (id)
);
CREATE TABLE mitarbeiter (
    snr NUMBER,
    person_id NUMBER,
    lohn VARCHAR(20),
    einst_datum DATE,
    PRIMARY KEY (snr),
    FOREIGN KEY (person_id) REFERENCES person
);
CREATE TABLE sprache (
    kuerzel VARCHAR(8),
    ist_rtl NUMBER(1),
    name VARCHAR(20),
    zeichen VARCHAR(20),
    PRIMARY KEY (kuerzel)
);
CREATE TABLE universitaet (
    id NUMBER GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(80),
    studierende NUMBER,
    PRIMARY KEY (id)
);
CREATE TABLE autor (
    id NUMBER GENERATED BY DEFAULT AS IDENTITY,
    uni_id NUMBER,
    taetig_seit DATE,
    kuerzel VARCHAR(20),
    PRIMARY KEY (id),
    FOREIGN KEY (uni_id) REFERENCES universitaet
);
CREATE TABLE autor_hat_sprache (
    autor_id NUMBER,
    sprache_kuerzel VARCHAR(8),
    PRIMARY KEY (autor_id, sprache_kuerzel),
    FOREIGN KEY (autor_id) REFERENCES autor,
    FOREIGN KEY (sprache_kuerzel) REFERENCES sprache
);
CREATE TABLE post (
    id NUMBER GENERATED BY DEFAULT AS IDENTITY,
    uni_id NUMBER, 
    hauptautor_id NUMBER,
    ref_post_id NUMBER,
    sprache_kuerzel VARCHAR(8),
    datum DATE,
    ist_live NUMBER(1),
    likes NUMBER,
    PRIMARY KEY (id, uni_id),
    FOREIGN KEY (hauptautor_id) REFERENCES autor,
    FOREIGN KEY (ref_post_id) REFERENCES post,
    FOREIGN KEY (sprache_kuerzel) REFERENCES sprache,
    FOREIGN KEY (uni_id) REFERENCES universitaet
);
CREATE TABLE autor_hat_post (
    autor_id NUMBER,
    post_id NUMBER,
    PRIMARY KEY(autor_id, post_id),
    FOREIGN KEY (autor_id) REFERENCES autor,
    FOREIGN KEY (post_id) REFERENCES post
);
CREATE TABLE revision (
    post_id NUMBER,
    uni_id NUMBER,
    versnr NUMBER,
    autor_id NUMBER,
    datum DATE,
    titel VARCHAR(80),
    inhalt VARCHAR(4000),
    PRIMARY KEY(post_id, versnr),
    FOREIGN KEY (uni_id) REFERENCES universitaet,
    FOREIGN KEY (autor_id) REFERENCES autor
);


----------------- CREATE VIEWS -----------------
CREATE OR REPLACE VIEW REV_POSTS
AS SELECT 
    p.id, r.titel, p.uni_id, r.inhalt, p.datum, a.kuerzel, p.likes
FROM post p
    -- MOST RECFENT REVISION
    INNER JOIN revision r ON r.post_id = p.id
    INNER JOIN
    (
        SELECT post_id, MAX(versnr) maxVers
        FROM revision
        GROUP BY post_id
    ) mx ON mx.post_id = p.id AND r.versnr = mx.maxVers
    
    -- RESOLVE AUTHOR
    INNER JOIN autor a ON a.id = p.hauptautor_id
    
    WHERE
        p.ref_post_id IS NULL
    ORDER BY p.datum DESC;

CREATE OR REPLACE VIEW POST_DISCUSSION
AS SELECT 
    p.id, r.titel, p.uni_id, r.inhalt, p.datum, a.kuerzel, p.likes
FROM post p
    -- MOST RECFENT REVISION
    INNER JOIN revision r ON r.post_id = p.id
    INNER JOIN
    (
        SELECT post_id, MAX(versnr) maxVers
        FROM revision
        GROUP BY post_id
    ) mx ON mx.post_id = p.id AND r.versnr = mx.maxVers
    
    -- RESOLVE AUTHOR
    INNER JOIN autor a ON a.id = p.hauptautor_id
    
    WHERE
        p.ref_post_id IS NOT NULL
    ORDER BY p.datum DESC;

----------------- CREATE PROCEDURES -----------------
CREATE OR REPLACE PROCEDURE INSERT_POST (
       p_hauptautor_id IN POST.hauptautor_id%TYPE,
       p_ref_post_id IN POST.ref_post_id%TYPE,
       p_uni_id IN POST.uni_id%TYPE
) IS
BEGIN
  INSERT INTO POST (hauptautor_id, ref_post_id, uni_id, datum) 
  VALUES (p_hauptautor_id, p_ref_post_id, p_uni_id, CURRENT_DATE);
  COMMIT;
END;
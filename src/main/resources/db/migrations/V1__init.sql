-- """"""""""""""""""""""""""""" --
-- Creation de la base de donnée --
-- """"""""""""""""""""""""""""" --

-- Création de la table des utilisateurs
DROP TABLE IF EXISTS "user" CASCADE;
CREATE TABLE "user" (
    "id" SERIAL PRIMARY KEY,
    "username" VARCHAR(32),
    "name" VARCHAR(32),
    "password" VARCHAR(255)
);

-- Créations des paramètres applicatifs de l'utilisateurs
-- Ici si l'utilisateur a activé ou non les notifications de follow
DROP TABLE IF EXISTS "settings" CASCADE;
CREATE TABLE "settings" (
    "user" BIGINT PRIMARY KEY REFERENCES "user"("id") ON DELETE CASCADE,
    "notification_enabled" BOOLEAN NOT NULL DEFAULT TRUE
);

-- Création des groupes de posts
DROP TABLE IF EXISTS "group" CASCADE;
CREATE TABLE "group" (
    "id" SERIAL PRIMARY KEY,
    "name" VARCHAR(100) NOT NULL,
    "owner" BIGINT NOT NULL REFERENCES "user"("id") ON DELETE CASCADE,
    "is_public" BOOLEAN NOT NULL DEFAULT FALSE
);

-- Création des roles dans un groupe
DROP TABLE IF EXISTS "role" CASCADE;
CREATE TABLE "role" (
    "id" SERIAL PRIMARY KEY,
    "name" VARCHAR(32) NOT NULL
);

-- Insertions des roles par défaut.
INSERT INTO "role" ("id", "name") VALUES
(1, 'admin'),
(2, 'modo'),
(3, 'editor');

-- Création de la table d'association permettant a une utilisateur de rejoindre un groupe avec un certain role
-- Si le role est null; l'utilisateur est un visiteur.
DROP TABLE IF EXISTS "group_users" CASCADE;
CREATE TABLE "group_users" (
    "user" BIGINT NOT NULL REFERENCES "user"("id") ON DELETE CASCADE,
    "group" BIGINT NOT NULL REFERENCES "group"("id") ON DELETE CASCADE,
    "role" BIGINT REFERENCES "role"("id") ON DELETE SET NULL
);

-- Création de la table des posts
-- Les posts sont créés par un utilisateur et donc supprimé si l'utilisateurs supprime son compte.
-- Le post peut être publié dans un groupe. Mais si le groupe est supprimé je garde le post pour l'utilisateur.
-- Le post peut-être un commentaire d'un autre post. Si ce post parent est supprimé, le post est gardé et deviens un post a part-entière.
-- Le post peux être a l'état de brouillon et donc pas visible sur les fils d'actualités.
DROP TABLE IF EXISTS "post" CASCADE;
CREATE TABLE "post" (
    "id" SERIAL PRIMARY KEY,
    "owner" BIGINT REFERENCES "user"("id") ON DELETE CASCADE,
    "group" BIGINT REFERENCES "group"("id") ON DELETE SET NULL,
    "parent" BIGINT REFERENCES "post"("id") ON DELETE SET NULL,
    "content" TEXT,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "is_draft" BOOLEAN DEFAULT FALSE
);

-- Création des types de pièces jointes
DROP TYPE IF EXISTS ATTACHMENT_TYPE CASCADE;
CREATE TYPE ATTACHMENT_TYPE AS ENUM(
    'link', 
    'image',
    'video',
    'document',
    'post'
);

-- Création de la table des pièces jointes.
-- Si le type post est utilisé, alors le post est un partage de post avec ou sans texte.
DROP TABLE IF EXISTS "attachment" CASCADE;
CREATE TABLE "attachment" (
    "id" SERIAL PRIMARY KEY,
    "owner" BIGINT NOT NULL REFERENCES "post"("id") ON DELETE CASCADE,
    "type" ATTACHMENT_TYPE NOT NULL,
    "link" VARCHAR(255),
    "image" VARCHAR(255),
    "video" VARCHAR(255),
    "document" VARCHAR(255),
    "post" BIGINT REFERENCES "post"("id") ON DELETE SET NULL
);

-- Création des tags permettant une recherche sur la thèmatique des posts.
DROP TABLE IF EXISTS "tag" CASCADE;
CREATE TABLE "tag" (
    "name" VARCHAR(32) PRIMARY KEY,
    "post" BIGINT NOT NULL REFERENCES "post"("id") ON DELETE CASCADE
);

-- Création de la table de follow
-- Un utilisateur peux suivre un autre utilisateur.
-- Si l'un des 2 utilisateurs n'existe plus, il n'y a aucune raison de garder l'occurence dans la table.
DROP TABLE IF EXISTS "follow" CASCADE;
CREATE TABLE "follow" (
    "follower" BIGINT NOT NULL REFERENCES "user"("id") ON DELETE CASCADE,
    "followed" BIGINT NOT NULL REFERENCES "user"("id") ON DELETE CASCADE,
    PRIMARY KEY("follower", "followed")
);

-- Table contenant les likes d'un post fait par un utilisateur.
DROP TABLE IF EXISTS "like" CASCADE;
CREATE TABLE "like" (
    "user" BIGINT NOT NULL REFERENCES "user"("id") ON DELETE CASCADE,
    "post" INTEGER NOT NULL REFERENCES "user"("id") ON DELETE CASCADE,
    PRIMARY KEY("user", "post")
);

-- Vue permettant d'afficher les posts publique avec leurs nombre de partage et de like.
DROP VIEW IF EXISTS "view_post";
CREATE VIEW "view_post" AS
SELECT
    DISTINCT p.*,
    g.is_public,
    count(l.user) as "like",
    (SELECT count(a.id) FROM "attachment" a WHERE p.id = a.post) as share
FROM "post" p
    LEFT JOIN "like" l ON p.id = l.post
    LEFT JOIN "group" g ON p.group = g.id
WHERE p.is_draft = false
GROUP BY p.id, g.id;

-- Création du fil d'actualité des plus populaire
DROP VIEW IF EXISTS "view_post_trending";
CREATE VIEW "view_post_trending" AS
SELECT *
FROM "view_post"
ORDER BY "like" DESC;

-- Création du fil d'actualité des plus récents
DROP VIEW IF EXISTS "view_post_newest";
CREATE VIEW "view_post_newest" AS
SELECT * FROM "view_post"
ORDER BY "created_at" DESC;

-- Fonction permettant de récupérer tous les commentaires et sous-commentaires d'un post.
-- Ce n'était pas demandé je le met là pour votre simple curiosité
-- Il prends la forme d'une fonction qui prend en entrée un id de post et ressort en premier ce post et ensuite les commentaires.
DROP FUNCTION IF EXISTS GetPostComments;
CREATE FUNCTION GetPostComments(post_id BIGINT)
RETURNS TABLE (
    id BIGINT,
    owner BIGINT,
    parent BIGINT,
    "group" BIGINT,
    content TEXT,
    level INTEGER,
    like INTEGER,
    share INTEGER,
    created_at TIMESTAMP,
    is_draft BOOLEAN
) AS $$
BEGIN
    RETURN QUERY
    WITH RECURSIVE CommentTree AS ( -- Liste les posts (pas les commentaires)
        SELECT p.id::BIGINT, p.parent::BIGINT, p.owner::BIGINT, p."group"::BIGINT, p.content::TEXT, (1)::INTEGER AS level, p.like::INTEGER, p.share::INTEGER, p.created_at::TIMESTAMP, p.is_draft::BOOLEAN
        FROM "view_post" p
        WHERE p.id = post_id -- Filter for the specified post ID
      UNION ALL -- Liste les commentaire d'un post
        SELECT p.id::BIGINT, p.parent::BIGINT, p.owner::BIGINT, p."group"::BIGINT, p.content::TEXT, (ct.level + 1)::INTEGER, p.like::INTEGER, p.share::INTEGER, p.created_at::TIMESTAMP, p.is_draft::BOOLEAN
        FROM "view_post" p
        INNER JOIN CommentTree ct ON p.parent = ct.id
    ) -- Extraction des valeurs finales
    SELECT c.*
    FROM CommentTree c;
END;
$$ LANGUAGE plpgsql;



-- """""""""""""""""""""""""" --
-- Insertion du jeu de donnée --
-- """""""""""""""""""""""""" --

-- Création des utilisateurs

INSERT INTO "user" ("id", "username") VALUES
(1, 'Jean Marc'),
(2, 'Pascal'),
(3, 'Maxime'),
(4, 'Paul'),
(5, 'Basil');

SELECT setval('user_id_seq', (SELECT MAX(id) FROM "user"));

-- Changement des settings pour l'utilisateurs JM
INSERT INTO "settings" ("user", "notification_enabled") VALUES
(1, FALSE);

-- Créations des posts sans groupes
INSERT INTO "post" ("id", "owner", "content", "is_draft") VALUES
(1, 1, 'From the makers of `ruff` comes [`uv`](https://astral.sh/blog/uv)\n\n&gt; TL;DR: `uv` is an extremely fast Python package installer and resolver, written in Rust, and designed as a drop-in replacement for `pip` and `pip-tools` workflows.\n\nIt is also capable of replacing `virtualenv`.\n\nWith this announcement, the [`rye`](https://github.com/mitsuhiko/rye) project and package management solution created by u/mitsuhiko (creator of Flask, minijinja, and so much more) in Rust, will be maintained by the [astral](https://github.com/astral-sh/) team.\n\nThis \"merger\" and announcement is all working toward the goal of a `Cargo`-type project and package management experience, but for Python.\n\nFor those of you who have big problems with the state of Python''s package and project management, this is a great set of announcements...\n\nFor everyone else, there is https://xkcd.com/927/.\n\n- [Twitter Announcement](https://twitter.com/charliermarsh/status/1758216803275149389)\n- [PyPI](https://pypi.org/project/uv/)\n- [GitHub](https://github.com/astral-sh/uv)\n\nInstall it today:\n\n```\npip install uv\n# or\npipx install uv\n# or\ncurl -LsSf https://astral.sh/uv/install.sh | sh\n```', FALSE),
(2, 3, 'Exciting to see, after many years, serious work in enabling multithreading that takes advantage of multiple CPUs in a more effective way in Python. One step at a time: https://github.com/python/cpython/pull/116338', FALSE),
(3, 5, 'I have been using python to code for almost 2 years and wanted to know what all IDEs people use ? So I can make a wise choice. TIA', FALSE),
(4, 2, 'Found a cool resource which explains the CLI tools hidden in the Python Standard Library.\n\nLink : [https://til.simonwillison.net/python/stdlib-cli-tools](https://til.simonwillison.net/python/stdlib-cli-tools)', FALSE),
(5, 1, '```py\nclass Movable:\n    def __init__(self, x, y, dx, dy, worldwidth, worldheight):\n        #automatically sets the given arguments. Can be reused with any class that has an order of named args.\n        \n        nonmembers = [] #populate with names that should not become members and will be used later. In many simple classes, this can be left empty.\n        \n        for key, value in list(locals().items())[1:]: #exclude ''self'', which is the first entry.\n            if not key in nonmembers:\n                setattr(self, key, value)\n    \n        #handle all nonmembers and assign other members:\n    \n        return\n```\nI always hate how redundant and bothersome it is to type "self.member = member" 10+ times, and this code does work the way I want it to. It''s pretty readable in my opinion, especially with the documentation. That aside, is it considered acceptable practice in python? Will other developers get annoyed if I use it?\n\nEdit:  Thanks for the very fast replies. Data classes it is! I meant for this to be a discussion of code conventions, but since I learned about a completely new feature to me, I guess this post belongs in r/learpython.', TRUE),
(6, 1, 'Exciting to see, after many years, serious work in enabling multithreading that takes advantage of multiple CPUs in a more effective way in Python. One step at a time: https://github.com/python/cpython/pull/116338', FALSE);

-- Création des commentaires de posts
INSERT INTO "post" ("id", "owner", "parent", "content", "is_draft") VALUES
(7, 1, 6, 'Maybe use dataclasses?', FALSE),
(8, 5, 7, 'Oh wow those are useful, good to know about them. But this implementation does allow me to have other parameters in the __init__ method that aren''t directly members themselves and rather contribute to the calculation of other members. Do data classes allow this in some way?', FALSE),
(9, 2, 5, 'Thanks', FALSE);

-- Création des groupes utilisateurs
INSERT INTO "group" ("id", "name", "owner", "is_public") VALUES
(1, 'Linux', 1, TRUE),
(2, 'Python', 1, TRUE),
(3, 'SQL', 1, TRUE),
(4, 'Bash', 1, TRUE),
(5, 'SecOps', 1, FALSE);

SELECT setval('group_id_seq', (SELECT MAX(id) FROM "group"));

-- Ajout des utilisateurs dans les groupes avec les bons roles
-- (admin pour JM et visiteurs pour les autres)
INSERT INTO "group_users" ("user", "group", "role") VALUES
(1, 1, 1),
(1, 2, 1),
(1, 3, 1),
(1, 4, 1),
(2, 2, NULL),
(2, 1, NULL),
(1, 5, 1);

-- Créations des posts avec groupes
INSERT INTO "post" ("id", "owner", "group", "content") VALUES
(10, 1, 1, 'Radion, an internet radio CLI client, written in Bash.'),
(11, 1, 4, NULL),
(12, 1, 5, 'Grosse faille de sécurité chez GCR cinéma !! Les utilisateurs ont fuités sur le web hier.');

SELECT setval('post_id_seq', (SELECT MAX(id) FROM "post"));

-- Ajout de la pièce jointe image dans le post "Radion" du groupe "Linux"
INSERT INTO "attachment" ("id", "owner", "type", "image") VALUES
(1, 10, 'image', 'https://preview.redd.it/radion-an-internet-radio-cli-client-written-in-bash-v0-bf28wvpwjync1.png?auto=webp&s=5ea01b080358157f0d8b19962b541b742a89d9b0');

-- Création du lien de partage du post "Radion" dans le groupe "Bash"
INSERT INTO "attachment" ("id", "owner", "type", "post") VALUES
(2, 11, 'post', 10);

SELECT setval('attachment_id_seq', (SELECT MAX(id) FROM "attachment"));

-- Créations des likes
INSERT INTO "like" ("post", "user") VALUES
(1, 1),
(2, 1),
(3, 1),
(1, 2),
(3, 2),
(1, 3);


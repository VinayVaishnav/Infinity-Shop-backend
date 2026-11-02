-- Product catalog from myProj/products/*.png (run scripts/sync-product-images.sh first)
-- Category ids: 1=T-Shirts, 2=Shirts, 3=Shoes, 4=Hoodies, 5=Accessories, 6=Caps
-- Image URLs are served from /products/<filename> (Vite public + Spring static)
-- version=0 required for JPA @Version optimistic locking on mp_products

DELETE FROM mp_user_cart;
DELETE FROM mp_order_products;
DELETE FROM mp_orders;
TRUNCATE TABLE mp_products RESTART IDENTITY CASCADE;

-- ─── T-Shirts (category 1) ───
INSERT INTO mp_products (name, description, category_id, price, image, is_available, stock, size, version)
SELECT v.name, v.description, 1, v.price, v.image, true,
       CASE s.size WHEN 'S' THEN v.stock_s WHEN 'M' THEN v.stock_m WHEN 'L' THEN v.stock_l ELSE v.stock_xl END,
       s.size,
       0
FROM (VALUES ('S'), ('M'), ('L'), ('XL')) AS s(size)
CROSS JOIN (VALUES
  ('Iron Man Arc Reactor Tee', 'Arc reactor print on premium cotton.', '29.99', '/products/Iron_Man_Arc_Reactor_Tee.png', 12, 24, 20, 8),
  ('Spider-Man Web Swing Tee', 'Web-swing graphic tee for daily wear.', '27.99', '/products/Spider-Man_Web_Swing_Tee.png', 15, 28, 22, 10),
  ('Captain America Shield Tee', 'Shield emblem front print.', '28.99', '/products/Captain_America_Shield_Tee.png', 14, 26, 18, 9),
  ('Thor Mjolnir Heavy Tee', 'Mjolnir artwork on heavyweight cotton.', '30.99', '/products/Thor_Mjolnir_Heavy_Tee.png', 10, 22, 16, 6),
  ('Black Panther Wakanda Forever Tee', 'Wakanda Forever tribute tee.', '29.99', '/products/Black_Panther_Wakanda_Forever_Tee.png', 11, 20, 15, 7),
  ('Wolverine Adamantium Claw Tee', 'Claw slash graphic.', '28.99', '/products/Wolverine_Adamantium_Claw_Tee.png', 13, 25, 19, 8),
  ('Scarlet Witch Chaos Magic Tee', 'Chaos magic motif.', '27.99', '/products/Scarlet_Witch_Chaos_Magic_Tee.png', 12, 23, 17, 7),
  ('Venom Symbiote Black Tee', 'All-black symbiote style print.', '27.99', '/products/Venom_Symbiote_Black_Tee.png', 16, 30, 24, 11),
  ('X-Men Mutant Academy Tee', 'Mutant academy crest.', '26.99', '/products/X-Men_Mutant_Academy_Tee.png', 14, 27, 21, 9),
  ('Fantastic Four Flame On Tee', 'Flame On! team graphic.', '26.99', '/products/Fantastic_Four_Flame_On_Tee.png', 9, 18, 14, 5)
) AS v(name, description, price, image, stock_s, stock_m, stock_l, stock_xl);

-- ─── Shirts (category 2) ───
INSERT INTO mp_products (name, description, category_id, price, image, is_available, stock, size, version)
SELECT v.name, v.description, 2, v.price, v.image, true,
       CASE s.size WHEN 'S' THEN v.stock_s WHEN 'M' THEN v.stock_m WHEN 'L' THEN v.stock_l ELSE v.stock_xl END,
       s.size,
       0
FROM (VALUES ('S'), ('M'), ('L'), ('XL')) AS s(size)
CROSS JOIN (VALUES
  ('Punisher Shirt', 'Skull emblem button-down style shirt.', '44.99', '/products/punisher_shirt.png', 8, 16, 12, 4),
  ('Black Panther Shirt', 'Wakandan pattern casual shirt.', '45.99', '/products/black_panther_shirt.png', 10, 18, 14, 5),
  ('Captain America Shirt', 'Star-spangled casual shirt.', '45.99', '/products/captain_america_shirt.png', 9, 17, 13, 4),
  ('Deadpool Logo Shirt', 'Merc with a mouth logo shirt.', '43.99', '/products/deadpool_log_shirt.png', 11, 20, 15, 6),
  ('Marvel Zombies Shirt', 'Horror-variant zombie graphic shirt.', '46.99', '/products/marvel_zombies_shirt.png', 7, 14, 10, 3)
) AS v(name, description, price, image, stock_s, stock_m, stock_l, stock_xl);

-- ─── Shoes (category 3) — sizes 8–11 ───
INSERT INTO mp_products (name, description, category_id, price, image, is_available, stock, size, version)
SELECT v.name, v.description, 3, v.price, v.image, true,
       CASE s.size WHEN '8' THEN v.s8 WHEN '9' THEN v.s9 WHEN '10' THEN v.s10 ELSE v.s11 END,
       s.size,
       0
FROM (VALUES ('8'), ('9'), ('10'), ('11')) AS s(size)
CROSS JOIN (VALUES
  ('Loki Studs Shoes', 'Green and gold studded sneakers.', '89.99', '/products/loki_studs_shoes.png', 4, 8, 6, 3),
  ('Daredevil Shoes', 'Red horned-accent runners.', '92.99', '/products/daredevil_shoes.png', 5, 9, 7, 3),
  ('X-Men Shoes', 'Blue and gold mutant trainers.', '88.99', '/products/x-men_shoes.png', 4, 7, 6, 2),
  ('Black Panther Shoes', 'Vibranium-inspired black trainers.', '94.99', '/products/black_panther_shoes.png', 3, 6, 5, 2),
  ('Spider-Man Shoes', 'Red and blue web-pattern sneakers.', '87.99', '/products/spider_man_shoes.png', 6, 10, 8, 4)
) AS v(name, description, price, image, s8, s9, s10, s11);

-- ─── Hoodies (category 4) ───
INSERT INTO mp_products (name, description, category_id, price, image, is_available, stock, size, version)
SELECT v.name, v.description, 4, v.price, v.image, true,
       CASE s.size WHEN 'S' THEN v.stock_s WHEN 'M' THEN v.stock_m WHEN 'L' THEN v.stock_l ELSE v.stock_xl END,
       s.size,
       0
FROM (VALUES ('S'), ('M'), ('L'), ('XL')) AS s(size)
CROSS JOIN (VALUES
  ('Moon Knight Hoodie', 'Crescent moon hooded sweatshirt.', '64.99', '/products/Moon_knight_hoodie.png', 6, 14, 11, 4),
  ('Ms. Marvel Hoodie', 'Lightning bolt hero hoodie.', '62.99', '/products/ms_marvel_hoodie.png', 7, 15, 12, 5),
  ('Deadpool Hoodie', 'Red merc hoodie.', '63.99', '/products/deadpool_hoodie.png', 8, 16, 13, 5),
  ('I Am Groot Hoodie', 'Groot face print hoodie.', '61.99', '/products/IamGroot_hoodie.png', 5, 12, 10, 3),
  ('Hulk Smash Hoodie', 'Smash graphic fleece hoodie.', '65.99', '/products/hulk_smach_hoodie.png', 6, 13, 11, 4)
) AS v(name, description, price, image, stock_s, stock_m, stock_l, stock_xl);

-- ─── Accessories (category 5) — one size ───
INSERT INTO mp_products (name, description, category_id, price, image, is_available, stock, size, version) VALUES
  ('Pym Particle Lab Kit', 'Ant-Man collectible lab kit.', 5, '36.99', '/products/Pym_Particle_Lab_Kit.png', true, 12, 'OS', 0),
  ('Eye of Agamotto Pendant', 'Time Stone pendant replica.', 5, '49.99', '/products/Eye_of_Agamotto_Pendant.png', true, 15, 'OS', 0),
  ('Spider-Man Web-Shooter Band', 'Cosplay web-shooter wrist band.', 5, '29.99', '/products/Spider-Man_Web-Shooter_Band.png', true, 22, 'OS', 0),
  ('Mjolnir Desk Paperweight', 'Thor hammer desk paperweight.', 5, '44.99', '/products/Mjolnir_Desk_Paperweight.png', true, 18, 'OS', 0),
  ('Captain Marvel Starforce Belt Bag', 'Starforce crossbody belt bag.', 5, '34.99', '/products/Captain_Marvel_Starforce_Belt_Bag.png', true, 30, 'OS', 0),
  ('Infinity Gauntlet Replica Glove', 'Display-grade gauntlet replica.', 5, '39.99', '/products/Infinity_Gauntlet_Replica_Glove.png', true, 25, 'OS', 0);

-- ─── Caps (category 6) — one size ───
INSERT INTO mp_products (name, description, category_id, price, image, is_available, stock, size, version) VALUES
  ('Guardians Star-Lord Snapback', 'Star-Lord style snapback cap.', 6, '25.99', '/products/Guardians_Star-Lord_Snapback.png', true, 28, 'OS', 0),
  ('Captain America Shield Cap', 'Shield panel structured cap.', 6, '26.99', '/products/Captain_America_Shield_Cap.png', true, 35, 'OS', 0),
  ('Avengers A-Logo Dad Cap', 'Navy dad cap with Avengers A.', 6, '22.99', '/products/Avengers_A-Logo_Dad–Cap.png', true, 45, 'OS', 0),
  ('Loki Horned Snapback', 'Green Loki horn snapback.', 6, '24.99', '/products/Loki_Horned_Snapback.png', true, 40, 'OS', 0);

-- Demo: one variant sold out
UPDATE mp_products SET stock = 0, is_available = false
WHERE name = 'Fantastic Four Flame On Tee' AND size = 'XL';

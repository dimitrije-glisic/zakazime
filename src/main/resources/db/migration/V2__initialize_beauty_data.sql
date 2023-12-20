-- Šišanje
INSERT INTO service (title, description, price, avg_duration, subcategory_id, template)
VALUES ('Muško šišanje', 'Klasično muško šišanje.', 800, 30, 1, true),
       ('Žensko šišanje', 'Stilizovano žensko šišanje.', 1200, 45, 1, true),
       ('Šišanje dece', 'Šišanje za decu do 12 godina.', 600, 30, 1, true),
       ('Bob šišanje', 'Moderan bob stil šišanja.', 1000, 45, 1, true),
       ('Layered šišanje', 'Šišanje sa slojevima.', 1100, 60, 1, true),
       ('Pixie šišanje', 'Kratko pixie šišanje.', 950, 40, 1, true),
       ('Undercut šišanje', 'Undercut stil šišanja.', 1200, 50, 1, true),
       ('Šišanje sa figarom', 'Stilizovanje kose figarom.', 1300, 60, 1, true),
       ('Balayage šišanje', 'Balayage tehnika bojenja.', 1500, 70, 1, true),
       ('Ombre šišanje', 'Ombre tehnika bojenja kose.', 1400, 70, 1, true);

-- Farbanje
INSERT INTO service (title, description, price, avg_duration, subcategory_id, template)
VALUES ('Farbanje korena', 'Farbanje korena kose.', 1500, 60, 2, true),
       ('Proradivanje boje', 'Proradivanje boje kroz kosu.', 2000, 90, 2, true),
       ('Proradivanje pramenova', 'Farbanje pramenova u različitim nijansama.', 1800, 75, 2, true),
       ('Kompletno farbanje', 'Kompletno farbanje kose u jednoj boji.', 2500, 120, 2, true),
       ('Sombre tehnika', 'Farbanje kose sombre tehnikom.', 2200, 90, 2, true),
       ('Highlights', 'Izrada svetlijih pramenova.', 1600, 85, 2, true),
       ('Lowlight', 'Izrada tamnijih pramenova.', 1600, 85, 2, true),
       ('Color melt', 'Tehnika farbanja za prirodni prelaz.', 2300, 95, 2, true),
       ('Pastelne boje', 'Farbanje kose u pastelnim bojama.', 2100, 100, 2, true),
       ('Plavo farbanje', 'Farbanje kose u različite nijanse plave.', 2200, 120, 2, true);

-- Tretmani lica
INSERT INTO service (title, description, price, avg_duration, subcategory_id, template)
VALUES ('Hidratacijski tretman', 'Tretman za hidrataciju kože lica.', 1500, 60, 3, true),
       ('Tretman protiv bora', 'Tretman protiv bora na licu.', 2000, 60, 3, true),
       ('Čišćenje lica', 'Dubinsko čišćenje kože lica.', 1300, 50, 3, true),
       ('Anti-age tretman', 'Tretman za zatezanje kože i smanjenje bora.', 2200, 70, 3, true),
       ('Tretman za akne', 'Specijalizovani tretman za kožu sklonu aknama.', 1700, 55, 3, true),
       ('Peeling lica', 'Hemijski peeling za obnavljanje kože.', 1800, 60, 3, true),
       ('Mikrodermoabrazija', 'Tretman za uklanjanje mrtvih ćelija kože.', 1600, 45, 3, true),
       ('Tretman zlatom', 'Luksuzni tretman lica sa zlatom.', 2500, 75, 3, true),
       ('Vitaminski tretman', 'Tretman lica sa visokim sadržajem vitamina.', 1900, 65, 3, true),
       ('Hidrofacial tretman', 'Hidrofacial tretman za dubinsku hidrataciju.', 2100, 70, 3, true);

-- Masaže
INSERT INTO service (title, description, price, avg_duration, subcategory_id, template)
VALUES ('Relaksaciona masaža', 'Masaža za opuštanje i smanjenje stresa.', 1200, 60, 4, true),
       ('Terapeutska masaža', 'Masaža za ublažavanje bolova u mišićima.', 1400, 60, 4, true),
       ('Aromaterapijska masaža', 'Masaža sa eteričnim uljima za relaksaciju.', 1300, 70, 4, true),
       ('Švedska masaža', 'Tradicionalna švedska masaža za opuštanje.', 1500, 60, 4, true),
       ('Hot stone masaža', 'Masaža toplim kamenjem.', 1600, 75, 4, true),
       ('Anticelulit masaža', 'Masaža za smanjenje celulita.', 1100, 45, 4, true),
       ('Refleksologija', 'Masaža refleksnih tačaka na stopalima.', 1000, 50, 4, true),
       ('Sportska masaža', 'Masaža za sportiste i aktivne osobe.', 1700, 65, 4, true),
       ('Masaža lica', 'Opustajuća masaža lica.', 900, 30, 4, true),
       ('Masaža glave', 'Masaža glave za smanjenje napetosti.', 800, 30, 4, true);

-- Manikir
INSERT INTO service (title, description, price, avg_duration, subcategory_id, template)
VALUES ('Klasični manikir', 'Klasični tretman i nega noktiju.', 1000, 45, 5, true),
       ('Gel manikir', 'Manikir sa gel lakom.', 1200, 60, 5, true),
       ('Japanski manikir', 'Nega noktiju japanskom tehnikom.', 1500, 70, 5, true),
       ('Francuski manikir', 'Francuski stil manikira.', 1100, 50, 5, true),
       ('Spa manikir', 'Luksuzni spa tretman za ruke.', 1300, 60, 5, true),
       ('Nail art', 'Umetničko oslikavanje noktiju.', 1400, 75, 5, true),
       ('Parafinski tretman', 'Parafinski tretman za ruke.', 900, 40, 5, true),
       ('Manikir sa šljokicama', 'Dekorativni manikir sa šljokicama.', 1250, 65, 5, true),
       ('Izgradnja noktiju', 'Izgradnja i oblikovanje noktiju.', 1600, 90, 5, true),
       ('Popravka noktiju', 'Popravka polomljenih noktiju.', 700, 30, 5, true);

-- Pedikir
INSERT INTO service (title, description, price, avg_duration, subcategory_id, template)
VALUES ('Klasični pedikir', 'Nega stopala i noktiju.', 1100, 60, 6, true),
       ('Spa pedikir', 'Luksuzni spa tretman za stopala.', 1300, 70, 6, true),
       ('Medicinski pedikir', 'Pedikir sa medicinskim tretmanima.', 1400, 75, 6, true),
       ('Gel pedikir', 'Pedikir sa gel lakom.', 1200, 60, 6, true),
       ('Parafinski tretman za stopala', 'Parafinski tretman za stopala.', 1000, 50, 6, true),
       ('Pedikir za dijabetičare', 'Pedikir prilagođen potrebama dijabetičara.', 1500, 65, 6, true),
       ('Peeling stopala', 'Tretman pilinga za stopala.', 950, 45, 6, true),
       ('Pedikir sa masažom', 'Pedikir sa opuštajućom masažom stopala.', 1350, 75, 6, true),
       ('Nail art na noktima stopala', 'Umetničko oslikavanje noktiju na stopalima.', 1250, 65, 6, true),
       ('French pedikir', 'Francuski stil pedikira.', 1150, 60, 6, true);

-- Dnevna šminka
INSERT INTO service (title, description, price, avg_duration, subcategory_id, template)
VALUES ('Prirodna dnevna šminka', 'Lagana šminka za svakodnevne prilike.', 1000, 45, 7, true),
       ('Poslovna šminka', 'Diskretna šminka za poslovne prilike.', 1200, 50, 7, true),
       ('Šminka za sastanke', 'Profesionalna šminka za važne sastanke.', 1300, 55, 7, true),
       ('Šminka za video pozive', 'Šminka prilagođena video pozivima.', 1100, 40, 7, true),
       ('BB krem šminka', 'Lagana šminka sa BB kremom.', 950, 35, 7, true),
       ('Teen šminka', 'Šminka prilagođena tinejdžerima.', 800, 30, 7, true),
       ('Brza dnevna šminka', 'Brz i efikasan dnevni make-up.', 900, 30, 7, true),
       ('Šminka sa naglaskom na oči', 'Dnevna šminka sa fokusom na oči.', 1150, 45, 7, true),
       ('Šminka sa naglaskom na usne', 'Dnevna šminka sa fokusom na usne.', 1150, 45, 7, true),
       ('Minimalistička šminka', 'Minimalistički stil šminkanja.', 850, 30, 7, true);

-- Večernja šminka
INSERT INTO service (title, description, price, avg_duration, subcategory_id, template)
VALUES ('Glamurozna večernja šminka', 'Svečana šminka za posebne prilike.', 1800, 60, 8, true),
       ('Šminka za venčanja', 'Profesionalna šminka za venčanja.', 2000, 75, 8, true),
       ('Šminka za maturu', 'Stilizovana šminka za maturske večeri.', 1500, 60, 8, true),
       ('Smoky eye šminka', 'Intenzivna smoky eye šminka.', 1600, 50, 8, true),
       ('Šminka sa lažnim trepavicama', 'Šminka sa aplikacijom lažnih trepavica.', 1700, 70, 8, true),
       ('Šminka sa šljokicama', 'Sjajna šminka sa šljokicama za večernje izlaske.', 1900, 65, 8, true),
       ('Retro šminka', 'Šminka u retro stilu.', 1800, 60, 8, true),
       ('Bold lip šminka', 'Šminka sa naglašenim usnama.', 1600, 55, 8, true),
       ('Šminka za tematske zabave', 'Šminka prilagođena tematskim zabavama.', 2000, 75, 8, true),
       ('Festival šminka', 'Kreativna šminka za festivale.', 1700, 60, 8, true);

-- Voskom
INSERT INTO service (title, description, price, avg_duration, subcategory_id, template)
VALUES ('Depilacija nogu', 'Depilacija nogu voskom.', 1500, 30, 9, true),
       ('Depilacija ruku', 'Depilacija ruku voskom.', 1200, 25, 9, true),
       ('Depilacija pazuha', 'Depilacija pazuha voskom.', 800, 20, 9, true),
       ('Brazilian depilacija', 'Brazilian depilacija voskom.', 1800, 40, 9, true),
       ('Depilacija lica', 'Depilacija voskom na licu.', 900, 20, 9, true),
       ('Depilacija leđa', 'Depilacija leđa voskom.', 1600, 35, 9, true),
       ('Depilacija stomaka', 'Depilacija stomaka voskom.', 1300, 30, 9, true),
       ('Depilacija bikini zone', 'Depilacija bikini zone voskom.', 1400, 30, 9, true),
       ('Depilacija celih nogu', 'Depilacija celih nogu voskom.', 2000, 45, 9, true),
       ('Depilacija celih ruku', 'Depilacija celih ruku voskom.', 1500, 40, 9, true);

-- Depilacija Laserom
INSERT INTO service (title, description, price, avg_duration, subcategory_id, template)
VALUES ('Laser depilacija nogu', 'Laser depilacija nogu za trajno uklanjanje dlaka.', 3000, 60, 10, true),
       ('Laser depilacija ruku', 'Laser depilacija ruku.', 2500, 45, 10, true),
       ('Laser depilacija pazuha', 'Laser depilacija pazuha za dugotrajne rezultate.', 2000, 30, 10, true),
       ('Laser depilacija bikini zone', 'Laser depilacija bikini zone.', 2800, 40, 10, true),
       ('Laser depilacija lica', 'Precizna laser depilacija za lice.', 2200, 30, 10, true),
       ('Laser depilacija leđa', 'Laser depilacija leđa za muškarce i žene.', 3200, 50, 10, true),
       ('Laser depilacija stomaka', 'Laser depilacija stomaka.', 2700, 40, 10, true),
       ('Celo telo laser depilacija', 'Kompletna laser depilacija celog tela.', 5000, 120, 10, true),
       ('Laser depilacija nadusnica', 'Laser depilacija nadusnica za žene.', 1500, 20, 10, true),
       ('Laser depilacija brade', 'Laser depilacija brade za muškarce.', 1800, 25, 10, true);

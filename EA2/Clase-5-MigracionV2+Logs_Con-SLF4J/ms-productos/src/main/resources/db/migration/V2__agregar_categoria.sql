-- Agregamos una nueva columna a la tabla existente
ALTER TABLE producto ADD COLUMN categoria VARCHAR(50);

-- Actualizamos datos existentes
UPDATE producto SET categoria = 'Perifericos' WHERE nombre LIKE '%Botella%';
UPDATE producto SET categoria = 'Perifericos' WHERE nombre LIKE '%Telefono%';
UPDATE producto SET categoria = 'Audio' WHERE nombre LIKE '%Mani%';
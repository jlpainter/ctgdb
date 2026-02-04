-- Connect to our database 
use ctgdb;

--
-- Import records into database (order matters)
--
LOAD DATA LOCAL INFILE './meddra.csv'  INTO TABLE meddra FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS;
LOAD DATA LOCAL INFILE './meddra_hierarchy.csv'  INTO TABLE meddra_hierarchy FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS;
LOAD DATA LOCAL INFILE './adverse_event.csv'  INTO TABLE adverse_event FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS
(id, adverse_event, @vmeddra_id, exact_match) SET meddra_id = NULLIF(@vmeddra_id,'');

LOAD DATA LOCAL INFILE './clinical_trial.csv'  INTO TABLE clinical_trial FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS;
LOAD DATA LOCAL INFILE './ct_arms.csv'  INTO TABLE ct_arms FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS;

LOAD DATA LOCAL INFILE './countries.csv'  INTO TABLE countries FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS;
LOAD DATA LOCAL INFILE './ct_country.csv'  INTO TABLE ct_country FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS;

LOAD DATA LOCAL INFILE './conditions.csv'  INTO TABLE conditions FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS
(id, term, @vmeddra_id, exact_match) SET meddra_id = NULLIF(@vmeddra_id,'');

LOAD DATA LOCAL INFILE './ct_conditions.csv'  INTO TABLE ct_conditions FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS;

LOAD DATA LOCAL INFILE './interventions.csv'  INTO TABLE interventions FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS;
LOAD DATA LOCAL INFILE './intervention_other_names.csv'  INTO TABLE intervention_other_names FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS;
LOAD DATA LOCAL INFILE './ct_intervention.csv'  INTO TABLE ct_intervention FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS;
LOAD DATA LOCAL INFILE './ct_adverse_events.csv'  INTO TABLE ct_adverse_events FIELDS TERMINATED BY ','  ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS;

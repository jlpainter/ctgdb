--
-- Import records into database (order matters)
--
COPY ctgdb.meddra FROM '/tmp/pg/meddra.csv' CSV HEADER;
COPY ctgdb.meddra_hierarchy FROM '/tmp/pg/meddra_hierarchy.csv' CSV HEADER;
COPY ctgdb.adverse_event FROM '/tmp/pg/adverse_event.csv' CSV HEADER QUOTE '"';
COPY ctgdb.clinical_trial FROM '/tmp/pg/clinical_trial.csv' CSV HEADER;
COPY ctgdb.ct_arms FROM '/tmp/pg/ct_arms.csv' CSV HEADER;
COPY ctgdb.countries FROM '/tmp/pg/countries.csv' CSV HEADER;
COPY ctgdb.ct_country FROM '/tmp/pg/ct_country.csv' CSV HEADER;
COPY ctgdb.conditions FROM '/tmp/pg/conditions.csv' CSV HEADER;
COPY ctgdb.ct_conditions FROM '/tmp/pg/ct_conditions.csv' CSV HEADER;
COPY ctgdb.interventions FROM '/tmp/pg/interventions.csv' CSV HEADER;
COPY ctgdb.intervention_other_names FROM '/tmp/pg/intervention_other_names.csv' CSV HEADER;
COPY ctgdb.ct_intervention FROM '/tmp/pg/ct_intervention.csv' CSV HEADER;
COPY ctgdb.ct_adverse_events FROM '/tmp/pg/ct_adverse_events.csv' CSV HEADER;

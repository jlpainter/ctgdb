--
-- Import records into database for Azure PostgreSQL data base (order matters)
--
\copy meddra FROM '/tmp/pg/meddra.csv' CSV HEADER;
\copy meddra_hierarchy FROM '/tmp/pg/meddra_hierarchy.csv' CSV HEADER;
\copy adverse_event FROM '/tmp/pg/adverse_event.csv' CSV HEADER QUOTE '"';

\copy clinical_trial FROM '/tmp/pg/clinical_trial.csv' CSV HEADER;
\copy ct_arms FROM '/tmp/pg/ct_arms.csv' CSV HEADER;
\copy countries FROM '/tmp/pg/countries.csv' CSV HEADER;
\copy ct_country FROM '/tmp/pg/ct_country.csv' CSV HEADER;
\copy conditions FROM '/tmp/pg/conditions.csv' CSV HEADER;
\copy ct_conditions FROM '/tmp/pg/ct_conditions.csv' CSV HEADER;
\copy interventions FROM '/tmp/pg/interventions.csv' CSV HEADER;
\copy intervention_other_names FROM '/tmp/pg/intervention_other_names.csv' CSV HEADER;
\copy ct_intervention FROM '/tmp/pg/ct_intervention.csv' CSV HEADER;
\copy ct_adverse_events FROM '/tmp/pg/ct_adverse_events.csv' CSV HEADER;

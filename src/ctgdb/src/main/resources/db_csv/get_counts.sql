-- Connect to our database 
use ctgdb;

--
-- Select all counts for quick comparison
--
select count(*) from meddra;
select count(*) from meddra_hierarchy;
select count(*) from adverse_event;
select count(*) from clinical_trial;
select count(*) from countries;
select count(*) from ct_country;
select count(*) from conditions;
select count(*) from ct_conditions;
select count(*) from interventions;
select count(*) from intervention_other_names;
select count(*) from ct_intervention;
select count(*) from ct_adverse_events;

--
-- CS6242: Data Analytics
--
-- Database structure to store our CT data
-- for dashboard analytics and analysis
--
-- @author: Jeffery Painter <jeff@jivecast.com>
-- @modified: 2021-Oct-14
--

-- DROP DATABASE 
CREATE DATABASE ctgdb;
USE ctgdb;

-- Clean the database
DROP TABLE IF EXISTS meddra;
DROP TABLE IF EXISTS meddra_hierarchy;

-- Interventions are what is being studied
DROP TABLE IF EXISTS interventions;
DROP TABLE IF EXISTS intervention_other_name;

DROP TABLE IF EXISTS ct_adverse_events;
DROP TABLE IF EXISTS ct_intervention;
DROP TABLE IF EXISTS ct_arms;
DROP TABLE IF EXISTS ct_country;
DROP TABLE IF EXISTS ct_conditions;

DROP TABLE IF EXISTS clinical_trial;
DROP TABLE IF EXISTS ct_status;
DROP TABLE IF EXISTS ct_phase;
DROP TABLE IF EXISTS ct_type;

DROP TABLE IF EXISTS adverse_event;
DROP TABLE IF EXISTS conditions;
DROP TABLE IF EXISTS country;

--
-- Status of trial
--
CREATE TABLE IF NOT EXISTS ct_status (
    id            integer not null AUTO_INCREMENT,
    name          varchar(50),
    PRIMARY KEY(id)
);

-- possible status of a clinical trial
INSERT INTO ct_status (id,name) VALUES (1, 'Not yet recruiting');
INSERT INTO ct_status (id,name) VALUES (2, 'Recruiting');
INSERT INTO ct_status (id,name) VALUES (3, 'Enrolling by invitation');
INSERT INTO ct_status (id,name) VALUES (4, 'Active, not recruiting');
INSERT INTO ct_status (id,name) VALUES (5, 'Suspended');
INSERT INTO ct_status (id,name) VALUES (6, 'Terminated');
INSERT INTO ct_status (id,name) VALUES (7, 'Completed');
INSERT INTO ct_status (id,name) VALUES (8, 'Withdrawn');
INSERT INTO ct_status (id,name) VALUES (9, 'Available');
INSERT INTO ct_status (id,name) VALUES (10, 'Unknown');

--
-- Study types
--
CREATE TABLE IF NOT EXISTS ct_type (
    id            integer not null AUTO_INCREMENT,
    name          varchar(50),
    PRIMARY KEY(id)
);

-- possible status of a clinical trial
INSERT INTO ct_type (id,name) VALUES (1, 'Interventional');
INSERT INTO ct_type (id,name) VALUES (2, 'Observational');
INSERT INTO ct_type (id,name) VALUES (3, 'Patient Registries');
INSERT INTO ct_type (id,name) VALUES (4, 'Expanded Access');
INSERT INTO ct_type (id,name) VALUES (5, 'Unknown');

--
-- Study phase
--
CREATE TABLE IF NOT EXISTS ct_phase (
    id            integer not null AUTO_INCREMENT,
    name          varchar(50),
    PRIMARY KEY(id)
);

-- possible status of a clinical trial
INSERT INTO ct_phase (id,name) VALUES (1, 'Early Phase 1');
INSERT INTO ct_phase (id,name) VALUES (2, 'Phase 1');
INSERT INTO ct_phase (id,name) VALUES (3, 'Phase 1/Phase 2');
INSERT INTO ct_phase (id,name) VALUES (4, 'Phase 2');
INSERT INTO ct_phase (id,name) VALUES (5, 'Phase 2/Phase 3');
INSERT INTO ct_phase (id,name) VALUES (6, 'Phase 3');
INSERT INTO ct_phase (id,name) VALUES (7, 'Phase 3/Phase 4');
INSERT INTO ct_phase (id,name) VALUES (8, 'Phase 4');
INSERT INTO ct_phase (id,name) VALUES (9, 'NA');


-- treatments
CREATE TABLE IF NOT EXISTS interventions (
    id            integer not null AUTO_INCREMENT,
    name          varchar(250),
    int_type      char(50),
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS intervention_other_names (
    intervention_id integer not null,
    other_name    varchar(250)
);

-- link to intervention table
ALTER TABLE intervention_other_names
    ADD CONSTRAINT intervention_other_names_fk_1
    FOREIGN KEY (intervention_id)
    REFERENCES interventions (id);

-- meddra codes from umls
CREATE TABLE IF NOT EXISTS meddra (
    
    id            integer not null AUTO_INCREMENT,
    aui           char(20),
    cui           char(20),
    tty           char(5),
    code          varchar(50),
    term          varchar(500),
    
    PRIMARY KEY(id)
);

-- meddra hierarachy from umls
CREATE TABLE IF NOT EXISTS meddra_hierarchy (
    
    parent_id     integer not null,
    child_id      integer not null
);

ALTER TABLE meddra_hierarchy
    ADD CONSTRAINT meddra_hier_fk_1
    FOREIGN KEY (parent_id)
    REFERENCES meddra (id);

ALTER TABLE meddra_hierarchy
    ADD CONSTRAINT meddra_hier_fk_2
    FOREIGN KEY (child_id)
    REFERENCES meddra (id);    

--
-- Top level table to store study info
--
CREATE TABLE IF NOT EXISTS clinical_trial (
    
    -- trial identifiers, id is a field we will generate
    id            integer not null AUTO_INCREMENT,
    
    -- link to tables above
    status_id     integer,
    type_id       integer,
    phase_id      integer,
    
    -- study descriptions
    brief_title   varchar(300),
    full_title    varchar(1500),
    
    -- who is running the study?
    study_sponsor varchar(200),
    study_collab  varchar(200),
    
    -- web lookup identifiers
    url           varchar(250),
    nct_id        varchar(50),
    study_id      varchar(50),

    -- date info    
    start_date    datetime,
    end_date      datetime,
    
    -- compute total trial length (if completed)
    trial_length_days integer,
    
    -- total number of patients enrolled
    pats_enrolled integer,
    
    -- These numbers reflect who is eligible for 
    -- inclusion in the study 
    min_age       integer,
    max_age       integer,
    
    -- 0 = No, 1 = Yes
    has_female_pats  integer,
    has_male_pats    integer,
    has_healthy_pats integer,
    
    -- quick flag to search for completed trials
    is_complete      integer,
    
    PRIMARY KEY(id)
);


ALTER TABLE clinical_trial
    ADD CONSTRAINT clinical_trial_fk_1
    FOREIGN KEY (status_id)
    REFERENCES ct_status (id);

ALTER TABLE clinical_trial
    ADD CONSTRAINT clinical_trial_fk_2
    FOREIGN KEY (type_id)
    REFERENCES ct_type (id);

ALTER TABLE clinical_trial
    ADD CONSTRAINT clinical_trial_fk_3
    FOREIGN KEY (phase_id)
    REFERENCES ct_phase (id);

-- Countries the trial is being run in (can be more than one)
CREATE TABLE IF NOT EXISTS countries (
    id            integer not null,
    country       varchar(200),
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS ct_country (
    trial_id            integer not null,
    country_id          integer not null
);

-- link to clinical trial
ALTER TABLE ct_country
    ADD CONSTRAINT ct_country_fk_1
    FOREIGN KEY (trial_id)
    REFERENCES clinical_trial (id);
    
-- link to country
ALTER TABLE ct_country
    ADD CONSTRAINT ct_country_fk_2
    FOREIGN KEY (country_id)
    REFERENCES countries (id);    

    
-- all unique conditions    
CREATE TABLE IF NOT EXISTS conditions (
    id            integer not null,
    term          varchar(500),
    -- best match to Meddra
    meddra_id     integer DEFAULT null,
    
    -- did we match to Meddra exactly?
    -- this would have a higher confidence
    -- in returning results
    exact_match   integer,
    
    PRIMARY KEY(id)
);

ALTER TABLE conditions
    ADD CONSTRAINT condition_fk_1
    FOREIGN KEY (meddra_id)
    REFERENCES meddra (id);

    
-- what drugs are being studied (can be more than one)    
CREATE TABLE IF NOT EXISTS ct_intervention (
    trial_id      integer not null,
    intervention_id       integer not null
);

ALTER TABLE ct_intervention
    ADD CONSTRAINT ct_intervention_fk_1
    FOREIGN KEY (trial_id)
    REFERENCES clinical_trial (id);

ALTER TABLE ct_intervention
    ADD CONSTRAINT ct_intervention_fk_2
    FOREIGN KEY (intervention_id)
    REFERENCES interventions (id);
    
-- what conditions are being studied (can be more than one)    
CREATE TABLE IF NOT EXISTS ct_conditions (
    trial_id      integer not null,
    condition_id  integer not null
);

ALTER TABLE ct_conditions
    ADD CONSTRAINT ct_conditions_fk_1
    FOREIGN KEY (trial_id)
    REFERENCES clinical_trial (id);

ALTER TABLE ct_conditions
    ADD CONSTRAINT ct_conditions_fk_2
    FOREIGN KEY (condition_id)
    REFERENCES conditions (id);
    
-- Link trials to their study arm
-- this can be a test or placebo group
CREATE TABLE IF NOT EXISTS ct_arms (

    -- we need to create unique arm IDs to link
    -- to safety events below
    arm_id        integer not null AUTO_INCREMENT,

    -- foreign key to primary table
    trial_id      integer not null,
    
    -- these come from the XML file to identify patient groups
    group_id      varchar(20),
    group_title   varchar(150),
    group_desc    varchar(350),
    
    -- count by gender
    female        integer DEFAULT 0,
    male          integer DEFAULT 0,
    
    -- ethnicity (storing in table for now)
    
    -- I have mapped all possible reported ethnicities (n=944)
    -- into one of the following categories
    -- not perfect, but captures most to a high degree
	asian         integer DEFAULT 0,
	black         integer DEFAULT 0,
	caucasian     integer DEFAULT 0,
	hispanic      integer DEFAULT 0,
	indian        integer DEFAULT 0,
	middle_east   integer DEFAULT 0,	
	multiple_eth  integer DEFAULT 0,
	native_american integer DEFAULT 0,
	native_hawaiian integer DEFAULT 0,
	other_eth     integer DEFAULT 0,
        
    -- ages provided as stats
    mean_age      decimal,
    std_dev       decimal,
    
    -- has the trial completed? 0 = no, 1 = yes
    -- we will have to search for 'placebo'
    --   in the group title to set this flag (not provided by the XML)
    is_placebo    integer,
    
    -- healthy volunteers
    is_healthy    integer,

    -- count our patients in this group, not all fields will have counts
    pats_started  integer,

    -- count completed patients only if trial is completed
    pats_complete integer,
    
    -- patients can be lost (drop out and no contact)
    pats_lost     integer,
    
    -- officially withdrew from the study
    pats_withdraw integer,
    
    -- deceased
    pats_died     integer,
    
    PRIMARY KEY(arm_id)
);

ALTER TABLE ct_arms
    ADD CONSTRAINT ct_arms_fk_1
    FOREIGN KEY (trial_id)
    REFERENCES clinical_trial (id);

-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS adverse_event (
    -- unique ID for table entry
    id            integer not null AUTO_INCREMENT,
    
    -- exact terms found in CT data
    adverse_event varchar(500),

    -- best match to Meddra
	meddra_id     integer DEFAULT null,

	-- did we match to Meddra exactly?
    -- this would have a higher confidence
    -- in returning results
    exact_match   integer,
	
    PRIMARY KEY (id)
);
    
ALTER TABLE adverse_event
    ADD CONSTRAINT adverse_event_fk_1
    FOREIGN KEY (meddra_id)
    REFERENCES meddra (id);

-- What adverse events were found?
CREATE TABLE IF NOT EXISTS ct_adverse_events (

    -- unique ID for table entry
    id            integer not null AUTO_INCREMENT,

    -- foreign key to primary table
    trial_id      integer not null,
    
    -- foreign key to patient group
    arm_id        integer not null,

    ae_id         integer not null,
    
    -- 0 = no, 1 = yes
    is_serious    integer,
    
    pats_affected integer,
    
    PRIMARY KEY(id)
    
);

ALTER TABLE ct_adverse_events
    ADD CONSTRAINT ct_adverse_events_fk_1
    FOREIGN KEY (trial_id)
    REFERENCES clinical_trial (id);

ALTER TABLE ct_adverse_events
    ADD CONSTRAINT ct_adverse_events_fk_2
    FOREIGN KEY (arm_id)
    REFERENCES ct_arms (arm_id);

-- link to unique adverse event terms
ALTER TABLE ct_adverse_events
    ADD CONSTRAINT ct_adverse_events_fk_3
    FOREIGN KEY (ae_id)
    REFERENCES adverse_event (id);

--
-- This will generate the CTDB schema in our Postgres database
--
--
-- I had to copy/paste this into a psql session connected to the
-- azure postgres instance
--

CREATE TABLE meddra (
    id integer NOT NULL,
    aui character(20),
    cui character(20),
    tty character(10),
    code character varying(50),
    term character varying(500)
);


ALTER TABLE meddra OWNER TO ctdb;


CREATE TABLE meddra_hierarchy (
    parent_id integer NOT NULL,
    child_id integer NOT NULL
);


ALTER TABLE meddra_hierarchy OWNER TO ctdb;

CREATE SEQUENCE meddra_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE meddra_id_seq OWNER TO ctdb;


ALTER SEQUENCE meddra_id_seq OWNED BY meddra.id;


CREATE TABLE adverse_event (
    id integer NOT NULL,
    adverse_event character varying(500),
    meddra_id integer,
    exact_match integer
);


ALTER TABLE adverse_event OWNER TO ctdb;

CREATE SEQUENCE adverse_event_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE adverse_event_id_seq OWNER TO ctdb;

ALTER SEQUENCE adverse_event_id_seq OWNED BY adverse_event.id;



CREATE TABLE clinical_trial (
    id integer NOT NULL,
    status_id integer,
    type_id integer,
    phase_id integer,
    brief_title character varying(300),
    full_title character varying(1500),
    study_sponsor character varying(500),
    study_collab text,
    url character varying(250),
    nct_id character varying(50),
    study_id character varying(50),
    start_date timestamp with time zone,
    end_date timestamp with time zone,
    trial_length_days integer,
    pats_enrolled integer,
    min_age integer,
    max_age integer,
    has_female_pats integer,
    has_male_pats integer,
    has_healthy_pats integer,
    is_complete integer
);


ALTER TABLE clinical_trial OWNER TO ctdb;

CREATE SEQUENCE clinical_trial_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE clinical_trial_id_seq OWNER TO ctdb;

ALTER SEQUENCE clinical_trial_id_seq OWNED BY clinical_trial.id;

CREATE TABLE conditions (
    id integer NOT NULL,
    term character varying(500),
    meddra_id integer,
    exact_match integer
);


ALTER TABLE conditions OWNER TO ctdb;

CREATE TABLE countries (
    id integer NOT NULL,
    country character varying(200)
);


ALTER TABLE countries OWNER TO ctdb;

CREATE TABLE ct_adverse_events (
    id integer NOT NULL,
    trial_id integer NOT NULL,
    arm_id integer NOT NULL,
    ae_id integer NOT NULL,
    is_serious integer,
    pats_affected integer
);


ALTER TABLE ct_adverse_events OWNER TO ctdb;

CREATE SEQUENCE ct_adverse_events_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ct_adverse_events_id_seq OWNER TO ctdb;

ALTER SEQUENCE ct_adverse_events_id_seq OWNED BY ct_adverse_events.id;


CREATE TABLE ct_arms (
    arm_id integer NOT NULL,
    trial_id integer NOT NULL,
    group_id character varying(20),
    group_title character varying(150),
    group_desc text,
    female integer DEFAULT 0,
    male integer DEFAULT 0,
    asian integer DEFAULT 0,
    black integer DEFAULT 0,
    caucasian integer DEFAULT 0,
    hispanic integer DEFAULT 0,
    indian integer DEFAULT 0,
    middle_east integer DEFAULT 0,
    multiple_eth integer DEFAULT 0,
    native_american integer DEFAULT 0,
    native_hawaiian integer DEFAULT 0,
    other_eth integer DEFAULT 0,
    mean_age numeric(10,0),
    std_dev numeric(10,0),
    is_placebo integer,
    is_healthy integer,
    pats_started integer,
    pats_complete integer,
    pats_lost integer,
    pats_withdraw integer,
    pats_died integer
);


ALTER TABLE ct_arms OWNER TO ctdb;

CREATE SEQUENCE ct_arms_arm_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ct_arms_arm_id_seq OWNER TO ctdb;

ALTER SEQUENCE ct_arms_arm_id_seq OWNED BY ct_arms.arm_id;


CREATE TABLE ct_conditions (
    trial_id integer NOT NULL,
    condition_id integer NOT NULL
);


ALTER TABLE ct_conditions OWNER TO ctdb;

CREATE TABLE ct_country (
    trial_id integer NOT NULL,
    country_id integer NOT NULL
);


ALTER TABLE ct_country OWNER TO ctdb;

CREATE TABLE ct_intervention (
    trial_id integer NOT NULL,
    intervention_id integer NOT NULL
);


ALTER TABLE ct_intervention OWNER TO ctdb;

CREATE TABLE ct_phase (
    id integer NOT NULL,
    name character varying(50)
);


ALTER TABLE ct_phase OWNER TO ctdb;


CREATE SEQUENCE ct_phase_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ct_phase_id_seq OWNER TO ctdb;

ALTER SEQUENCE ct_phase_id_seq OWNED BY ct_phase.id;



CREATE TABLE ct_status (
    id integer NOT NULL,
    name character varying(50)
);


ALTER TABLE ct_status OWNER TO ctdb;

CREATE SEQUENCE ct_status_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ct_status_id_seq OWNER TO ctdb;

ALTER SEQUENCE ct_status_id_seq OWNED BY ct_status.id;


CREATE TABLE ct_type (
    id integer NOT NULL,
    name character varying(50)
);


ALTER TABLE ct_type OWNER TO ctdb;


CREATE SEQUENCE ct_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ct_type_id_seq OWNER TO ctdb;

ALTER SEQUENCE ct_type_id_seq OWNED BY ct_type.id;

CREATE TABLE intervention_other_names (
    intervention_id integer NOT NULL,
    other_name character varying(250)
);


ALTER TABLE intervention_other_names OWNER TO ctdb;
CREATE TABLE interventions (
    id integer NOT NULL,
    name character varying(250),
    int_type character(50)
);


ALTER TABLE interventions OWNER TO ctdb;


CREATE SEQUENCE interventions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE interventions_id_seq OWNER TO ctdb;


ALTER SEQUENCE interventions_id_seq OWNED BY interventions.id;




ALTER TABLE ONLY adverse_event ALTER COLUMN id SET DEFAULT nextval( 'adverse_event_id_seq'::regclass);

ALTER TABLE ONLY clinical_trial ALTER COLUMN id SET DEFAULT nextval( 'clinical_trial_id_seq'::regclass);

ALTER TABLE ONLY ct_adverse_events ALTER COLUMN id SET DEFAULT nextval( 'ct_adverse_events_id_seq'::regclass);

ALTER TABLE ONLY ct_arms ALTER COLUMN arm_id SET DEFAULT nextval( 'ct_arms_arm_id_seq'::regclass);

ALTER TABLE ONLY ct_phase ALTER COLUMN id SET DEFAULT nextval( 'ct_phase_id_seq'::regclass);

ALTER TABLE ONLY ct_status ALTER COLUMN id SET DEFAULT nextval( 'ct_status_id_seq'::regclass);

ALTER TABLE ONLY ct_type ALTER COLUMN id SET DEFAULT nextval( 'ct_type_id_seq'::regclass);

ALTER TABLE ONLY interventions ALTER COLUMN id SET DEFAULT nextval( 'interventions_id_seq'::regclass);

ALTER TABLE ONLY meddra ALTER COLUMN id SET DEFAULT nextval( 'meddra_id_seq'::regclass);

CREATE INDEX idx_19864_condition_fk_1 ON conditions USING btree (meddra_id);

CREATE UNIQUE INDEX idx_19864_primary ON conditions USING btree (id);



CREATE INDEX idx_19873_ct_adverse_events_fk_1 ON ct_adverse_events USING btree (trial_id);



CREATE INDEX idx_19873_ct_adverse_events_fk_2 ON ct_adverse_events USING btree (arm_id);




CREATE INDEX idx_19873_ct_adverse_events_fk_3 ON ct_adverse_events USING btree (ae_id);



CREATE UNIQUE INDEX idx_19873_primary ON ct_adverse_events USING btree (id);


CREATE INDEX idx_19896_ct_conditions_fk_1 ON ct_conditions USING btree (trial_id);



CREATE INDEX idx_19896_ct_conditions_fk_2 ON ct_conditions USING btree (condition_id);


CREATE INDEX idx_19899_ct_country_fk_1 ON ct_country USING btree (trial_id);


CREATE INDEX idx_19899_ct_country_fk_2 ON ct_country USING btree (country_id);

CREATE INDEX idx_19902_ct_intervention_fk_1 ON ct_intervention USING btree (trial_id);

CREATE INDEX idx_19902_ct_intervention_fk_2 ON ct_intervention USING btree (intervention_id);


CREATE UNIQUE INDEX idx_19911_primary ON ct_status USING btree (id);

CREATE UNIQUE INDEX idx_19916_primary ON ct_type USING btree (id);



CREATE UNIQUE INDEX idx_19924_primary ON interventions USING btree (id);


CREATE UNIQUE INDEX idx_19929_primary ON meddra USING btree (id);



CREATE INDEX idx_19935_meddra_hier_fk_1 ON meddra_hierarchy USING btree (parent_id);


CREATE INDEX idx_19935_meddra_hier_fk_2 ON meddra_hierarchy USING btree (child_id);


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


-- possible status of a clinical trial
INSERT INTO ct_type (id,name) VALUES (1, 'Interventional');
INSERT INTO ct_type (id,name) VALUES (2, 'Observational');
INSERT INTO ct_type (id,name) VALUES (3, 'Patient Registries');
INSERT INTO ct_type (id,name) VALUES (4, 'Expanded Access');
INSERT INTO ct_type (id,name) VALUES (5, 'Unknown');


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


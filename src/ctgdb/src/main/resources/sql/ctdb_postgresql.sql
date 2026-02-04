--
-- PostgreSQL database dump
--

-- Dumped from database version 14.7 (Ubuntu 14.7-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.7 (Ubuntu 14.7-0ubuntu0.22.04.1)

-- Started on 2023-04-19 11:37:15 EDT

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4 (class 2615 OID 20480)
-- Name: ctdb; Type: SCHEMA; Schema: -; Owner: painter
--

DROP SCHEMA ctdb;
CREATE SCHEMA ctdb;
ALTER SCHEMA ctdb OWNER TO painter;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 210 (class 1259 OID 20481)
-- Name: adverse_event; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.adverse_event (
    id integer NOT NULL,
    adverse_event character varying(500),
    meddra_id integer,
    exact_match integer
);


ALTER TABLE ctdb.adverse_event OWNER TO painter;

--
-- TOC entry 211 (class 1259 OID 20486)
-- Name: adverse_event_id_seq; Type: SEQUENCE; Schema: ctdb; Owner: painter
--

CREATE SEQUENCE ctdb.adverse_event_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctdb.adverse_event_id_seq OWNER TO painter;

--
-- TOC entry 3457 (class 0 OID 0)
-- Dependencies: 211
-- Name: adverse_event_id_seq; Type: SEQUENCE OWNED BY; Schema: ctdb; Owner: painter
--

ALTER SEQUENCE ctdb.adverse_event_id_seq OWNED BY ctdb.adverse_event.id;


--
-- TOC entry 212 (class 1259 OID 20487)
-- Name: clinical_trial; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.clinical_trial (
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


ALTER TABLE ctdb.clinical_trial OWNER TO painter;

--
-- TOC entry 213 (class 1259 OID 20492)
-- Name: clinical_trial_id_seq; Type: SEQUENCE; Schema: ctdb; Owner: painter
--

CREATE SEQUENCE ctdb.clinical_trial_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctdb.clinical_trial_id_seq OWNER TO painter;

--
-- TOC entry 3458 (class 0 OID 0)
-- Dependencies: 213
-- Name: clinical_trial_id_seq; Type: SEQUENCE OWNED BY; Schema: ctdb; Owner: painter
--

ALTER SEQUENCE ctdb.clinical_trial_id_seq OWNED BY ctdb.clinical_trial.id;


--
-- TOC entry 214 (class 1259 OID 20493)
-- Name: conditions; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.conditions (
    id integer NOT NULL,
    term character varying(500),
    meddra_id integer,
    exact_match integer
);


ALTER TABLE ctdb.conditions OWNER TO painter;

--
-- TOC entry 215 (class 1259 OID 20498)
-- Name: countries; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.countries (
    id integer NOT NULL,
    country character varying(200)
);


ALTER TABLE ctdb.countries OWNER TO painter;

--
-- TOC entry 216 (class 1259 OID 20501)
-- Name: ct_adverse_events; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.ct_adverse_events (
    id integer NOT NULL,
    trial_id integer NOT NULL,
    arm_id integer NOT NULL,
    ae_id integer NOT NULL,
    is_serious integer,
    pats_affected integer
);


ALTER TABLE ctdb.ct_adverse_events OWNER TO painter;

--
-- TOC entry 217 (class 1259 OID 20504)
-- Name: ct_adverse_events_id_seq; Type: SEQUENCE; Schema: ctdb; Owner: painter
--

CREATE SEQUENCE ctdb.ct_adverse_events_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctdb.ct_adverse_events_id_seq OWNER TO painter;

--
-- TOC entry 3459 (class 0 OID 0)
-- Dependencies: 217
-- Name: ct_adverse_events_id_seq; Type: SEQUENCE OWNED BY; Schema: ctdb; Owner: painter
--

ALTER SEQUENCE ctdb.ct_adverse_events_id_seq OWNED BY ctdb.ct_adverse_events.id;


--
-- TOC entry 218 (class 1259 OID 20505)
-- Name: ct_arms; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.ct_arms (
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


ALTER TABLE ctdb.ct_arms OWNER TO painter;

--
-- TOC entry 219 (class 1259 OID 20522)
-- Name: ct_arms_arm_id_seq; Type: SEQUENCE; Schema: ctdb; Owner: painter
--

CREATE SEQUENCE ctdb.ct_arms_arm_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctdb.ct_arms_arm_id_seq OWNER TO painter;

--
-- TOC entry 3460 (class 0 OID 0)
-- Dependencies: 219
-- Name: ct_arms_arm_id_seq; Type: SEQUENCE OWNED BY; Schema: ctdb; Owner: painter
--

ALTER SEQUENCE ctdb.ct_arms_arm_id_seq OWNED BY ctdb.ct_arms.arm_id;


--
-- TOC entry 220 (class 1259 OID 20523)
-- Name: ct_conditions; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.ct_conditions (
    trial_id integer NOT NULL,
    condition_id integer NOT NULL
);


ALTER TABLE ctdb.ct_conditions OWNER TO painter;

--
-- TOC entry 221 (class 1259 OID 20526)
-- Name: ct_country; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.ct_country (
    trial_id integer NOT NULL,
    country_id integer NOT NULL
);


ALTER TABLE ctdb.ct_country OWNER TO painter;

--
-- TOC entry 222 (class 1259 OID 20529)
-- Name: ct_intervention; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.ct_intervention (
    trial_id integer NOT NULL,
    intervention_id integer NOT NULL
);


ALTER TABLE ctdb.ct_intervention OWNER TO painter;

--
-- TOC entry 223 (class 1259 OID 20532)
-- Name: ct_phase; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.ct_phase (
    id integer NOT NULL,
    name character varying(50)
);


ALTER TABLE ctdb.ct_phase OWNER TO painter;

--
-- TOC entry 224 (class 1259 OID 20535)
-- Name: ct_phase_id_seq; Type: SEQUENCE; Schema: ctdb; Owner: painter
--

CREATE SEQUENCE ctdb.ct_phase_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctdb.ct_phase_id_seq OWNER TO painter;

--
-- TOC entry 3461 (class 0 OID 0)
-- Dependencies: 224
-- Name: ct_phase_id_seq; Type: SEQUENCE OWNED BY; Schema: ctdb; Owner: painter
--

ALTER SEQUENCE ctdb.ct_phase_id_seq OWNED BY ctdb.ct_phase.id;


--
-- TOC entry 225 (class 1259 OID 20536)
-- Name: ct_status; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.ct_status (
    id integer NOT NULL,
    name character varying(50)
);


ALTER TABLE ctdb.ct_status OWNER TO painter;

--
-- TOC entry 226 (class 1259 OID 20539)
-- Name: ct_status_id_seq; Type: SEQUENCE; Schema: ctdb; Owner: painter
--

CREATE SEQUENCE ctdb.ct_status_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctdb.ct_status_id_seq OWNER TO painter;

--
-- TOC entry 3462 (class 0 OID 0)
-- Dependencies: 226
-- Name: ct_status_id_seq; Type: SEQUENCE OWNED BY; Schema: ctdb; Owner: painter
--

ALTER SEQUENCE ctdb.ct_status_id_seq OWNED BY ctdb.ct_status.id;


--
-- TOC entry 227 (class 1259 OID 20540)
-- Name: ct_type; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.ct_type (
    id integer NOT NULL,
    name character varying(50)
);


ALTER TABLE ctdb.ct_type OWNER TO painter;

--
-- TOC entry 228 (class 1259 OID 20543)
-- Name: ct_type_id_seq; Type: SEQUENCE; Schema: ctdb; Owner: painter
--

CREATE SEQUENCE ctdb.ct_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctdb.ct_type_id_seq OWNER TO painter;

--
-- TOC entry 3463 (class 0 OID 0)
-- Dependencies: 228
-- Name: ct_type_id_seq; Type: SEQUENCE OWNED BY; Schema: ctdb; Owner: painter
--

ALTER SEQUENCE ctdb.ct_type_id_seq OWNED BY ctdb.ct_type.id;


--
-- TOC entry 229 (class 1259 OID 20544)
-- Name: intervention_other_names; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.intervention_other_names (
    intervention_id integer NOT NULL,
    other_name character varying(250)
);


ALTER TABLE ctdb.intervention_other_names OWNER TO painter;

--
-- TOC entry 230 (class 1259 OID 20547)
-- Name: interventions; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.interventions (
    id integer NOT NULL,
    name character varying(250),
    int_type character(50)
);


ALTER TABLE ctdb.interventions OWNER TO painter;

--
-- TOC entry 231 (class 1259 OID 20550)
-- Name: interventions_id_seq; Type: SEQUENCE; Schema: ctdb; Owner: painter
--

CREATE SEQUENCE ctdb.interventions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctdb.interventions_id_seq OWNER TO painter;

--
-- TOC entry 3464 (class 0 OID 0)
-- Dependencies: 231
-- Name: interventions_id_seq; Type: SEQUENCE OWNED BY; Schema: ctdb; Owner: painter
--

ALTER SEQUENCE ctdb.interventions_id_seq OWNED BY ctdb.interventions.id;


--
-- TOC entry 232 (class 1259 OID 20551)
-- Name: meddra; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.meddra (
    id integer NOT NULL,
    aui character(20),
    cui character(20),
    tty character(10),
    code character varying(50),
    term character varying(500)
);


ALTER TABLE ctdb.meddra OWNER TO painter;

--
-- TOC entry 233 (class 1259 OID 20556)
-- Name: meddra_hierarchy; Type: TABLE; Schema: ctdb; Owner: painter
--

CREATE TABLE ctdb.meddra_hierarchy (
    parent_id integer NOT NULL,
    child_id integer NOT NULL
);


ALTER TABLE ctdb.meddra_hierarchy OWNER TO painter;

--
-- TOC entry 234 (class 1259 OID 20559)
-- Name: meddra_id_seq; Type: SEQUENCE; Schema: ctdb; Owner: painter
--

CREATE SEQUENCE ctdb.meddra_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctdb.meddra_id_seq OWNER TO painter;

--
-- TOC entry 3465 (class 0 OID 0)
-- Dependencies: 234
-- Name: meddra_id_seq; Type: SEQUENCE OWNED BY; Schema: ctdb; Owner: painter
--

ALTER SEQUENCE ctdb.meddra_id_seq OWNED BY ctdb.meddra.id;


--
-- TOC entry 3274 (class 2604 OID 20560)
-- Name: adverse_event id; Type: DEFAULT; Schema: ctdb; Owner: painter
--

ALTER TABLE ONLY ctdb.adverse_event ALTER COLUMN id SET DEFAULT nextval('ctdb.adverse_event_id_seq'::regclass);


--
-- TOC entry 3275 (class 2604 OID 20561)
-- Name: clinical_trial id; Type: DEFAULT; Schema: ctdb; Owner: painter
--

ALTER TABLE ONLY ctdb.clinical_trial ALTER COLUMN id SET DEFAULT nextval('ctdb.clinical_trial_id_seq'::regclass);


--
-- TOC entry 3276 (class 2604 OID 20562)
-- Name: ct_adverse_events id; Type: DEFAULT; Schema: ctdb; Owner: painter
--

ALTER TABLE ONLY ctdb.ct_adverse_events ALTER COLUMN id SET DEFAULT nextval('ctdb.ct_adverse_events_id_seq'::regclass);


--
-- TOC entry 3289 (class 2604 OID 20563)
-- Name: ct_arms arm_id; Type: DEFAULT; Schema: ctdb; Owner: painter
--

ALTER TABLE ONLY ctdb.ct_arms ALTER COLUMN arm_id SET DEFAULT nextval('ctdb.ct_arms_arm_id_seq'::regclass);


--
-- TOC entry 3290 (class 2604 OID 20564)
-- Name: ct_phase id; Type: DEFAULT; Schema: ctdb; Owner: painter
--

ALTER TABLE ONLY ctdb.ct_phase ALTER COLUMN id SET DEFAULT nextval('ctdb.ct_phase_id_seq'::regclass);


--
-- TOC entry 3291 (class 2604 OID 20565)
-- Name: ct_status id; Type: DEFAULT; Schema: ctdb; Owner: painter
--

ALTER TABLE ONLY ctdb.ct_status ALTER COLUMN id SET DEFAULT nextval('ctdb.ct_status_id_seq'::regclass);


--
-- TOC entry 3292 (class 2604 OID 20566)
-- Name: ct_type id; Type: DEFAULT; Schema: ctdb; Owner: painter
--

ALTER TABLE ONLY ctdb.ct_type ALTER COLUMN id SET DEFAULT nextval('ctdb.ct_type_id_seq'::regclass);


--
-- TOC entry 3293 (class 2604 OID 20567)
-- Name: interventions id; Type: DEFAULT; Schema: ctdb; Owner: painter
--

ALTER TABLE ONLY ctdb.interventions ALTER COLUMN id SET DEFAULT nextval('ctdb.interventions_id_seq'::regclass);


--
-- TOC entry 3294 (class 2604 OID 20568)
-- Name: meddra id; Type: DEFAULT; Schema: ctdb; Owner: painter
--

ALTER TABLE ONLY ctdb.meddra ALTER COLUMN id SET DEFAULT nextval('ctdb.meddra_id_seq'::regclass);


--
-- TOC entry 3295 (class 1259 OID 20569)
-- Name: idx_19864_condition_fk_1; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE INDEX idx_19864_condition_fk_1 ON ctdb.conditions USING btree (meddra_id);


--
-- TOC entry 3296 (class 1259 OID 20570)
-- Name: idx_19864_primary; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE UNIQUE INDEX idx_19864_primary ON ctdb.conditions USING btree (id);


--
-- TOC entry 3297 (class 1259 OID 20571)
-- Name: idx_19873_ct_adverse_events_fk_1; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE INDEX idx_19873_ct_adverse_events_fk_1 ON ctdb.ct_adverse_events USING btree (trial_id);


--
-- TOC entry 3298 (class 1259 OID 20572)
-- Name: idx_19873_ct_adverse_events_fk_2; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE INDEX idx_19873_ct_adverse_events_fk_2 ON ctdb.ct_adverse_events USING btree (arm_id);


--
-- TOC entry 3299 (class 1259 OID 20573)
-- Name: idx_19873_ct_adverse_events_fk_3; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE INDEX idx_19873_ct_adverse_events_fk_3 ON ctdb.ct_adverse_events USING btree (ae_id);


--
-- TOC entry 3300 (class 1259 OID 20574)
-- Name: idx_19873_primary; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE UNIQUE INDEX idx_19873_primary ON ctdb.ct_adverse_events USING btree (id);


--
-- TOC entry 3301 (class 1259 OID 20575)
-- Name: idx_19896_ct_conditions_fk_1; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE INDEX idx_19896_ct_conditions_fk_1 ON ctdb.ct_conditions USING btree (trial_id);


--
-- TOC entry 3302 (class 1259 OID 20576)
-- Name: idx_19896_ct_conditions_fk_2; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE INDEX idx_19896_ct_conditions_fk_2 ON ctdb.ct_conditions USING btree (condition_id);


--
-- TOC entry 3303 (class 1259 OID 20577)
-- Name: idx_19899_ct_country_fk_1; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE INDEX idx_19899_ct_country_fk_1 ON ctdb.ct_country USING btree (trial_id);


--
-- TOC entry 3304 (class 1259 OID 20578)
-- Name: idx_19899_ct_country_fk_2; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE INDEX idx_19899_ct_country_fk_2 ON ctdb.ct_country USING btree (country_id);


--
-- TOC entry 3305 (class 1259 OID 20579)
-- Name: idx_19902_ct_intervention_fk_1; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE INDEX idx_19902_ct_intervention_fk_1 ON ctdb.ct_intervention USING btree (trial_id);


--
-- TOC entry 3306 (class 1259 OID 20580)
-- Name: idx_19902_ct_intervention_fk_2; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE INDEX idx_19902_ct_intervention_fk_2 ON ctdb.ct_intervention USING btree (intervention_id);


--
-- TOC entry 3307 (class 1259 OID 20581)
-- Name: idx_19911_primary; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE UNIQUE INDEX idx_19911_primary ON ctdb.ct_status USING btree (id);


--
-- TOC entry 3308 (class 1259 OID 20582)
-- Name: idx_19916_primary; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE UNIQUE INDEX idx_19916_primary ON ctdb.ct_type USING btree (id);


--
-- TOC entry 3309 (class 1259 OID 20583)
-- Name: idx_19924_primary; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE UNIQUE INDEX idx_19924_primary ON ctdb.interventions USING btree (id);


--
-- TOC entry 3310 (class 1259 OID 20584)
-- Name: idx_19929_primary; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE UNIQUE INDEX idx_19929_primary ON ctdb.meddra USING btree (id);


--
-- TOC entry 3311 (class 1259 OID 20585)
-- Name: idx_19935_meddra_hier_fk_1; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE INDEX idx_19935_meddra_hier_fk_1 ON ctdb.meddra_hierarchy USING btree (parent_id);


--
-- TOC entry 3312 (class 1259 OID 20586)
-- Name: idx_19935_meddra_hier_fk_2; Type: INDEX; Schema: ctdb; Owner: painter
--

CREATE INDEX idx_19935_meddra_hier_fk_2 ON ctdb.meddra_hierarchy USING btree (child_id);


-- possible status of a clinical trial
INSERT INTO ctdb.ct_status (id,name) VALUES (1, 'Not yet recruiting');
INSERT INTO ctdb.ct_status (id,name) VALUES (2, 'Recruiting');
INSERT INTO ctdb.ct_status (id,name) VALUES (3, 'Enrolling by invitation');
INSERT INTO ctdb.ct_status (id,name) VALUES (4, 'Active, not recruiting');
INSERT INTO ctdb.ct_status (id,name) VALUES (5, 'Suspended');
INSERT INTO ctdb.ct_status (id,name) VALUES (6, 'Terminated');
INSERT INTO ctdb.ct_status (id,name) VALUES (7, 'Completed');
INSERT INTO ctdb.ct_status (id,name) VALUES (8, 'Withdrawn');
INSERT INTO ctdb.ct_status (id,name) VALUES (9, 'Available');
INSERT INTO ctdb.ct_status (id,name) VALUES (10, 'Unknown');


-- possible status of a clinical trial
INSERT INTO ctdb.ct_type (id,name) VALUES (1, 'Interventional');
INSERT INTO ctdb.ct_type (id,name) VALUES (2, 'Observational');
INSERT INTO ctdb.ct_type (id,name) VALUES (3, 'Patient Registries');
INSERT INTO ctdb.ct_type (id,name) VALUES (4, 'Expanded Access');
INSERT INTO ctdb.ct_type (id,name) VALUES (5, 'Unknown');


-- possible status of a clinical trial
INSERT INTO ctdb.ct_phase (id,name) VALUES (1, 'Early Phase 1');
INSERT INTO ctdb.ct_phase (id,name) VALUES (2, 'Phase 1');
INSERT INTO ctdb.ct_phase (id,name) VALUES (3, 'Phase 1/Phase 2');
INSERT INTO ctdb.ct_phase (id,name) VALUES (4, 'Phase 2');
INSERT INTO ctdb.ct_phase (id,name) VALUES (5, 'Phase 2/Phase 3');
INSERT INTO ctdb.ct_phase (id,name) VALUES (6, 'Phase 3');
INSERT INTO ctdb.ct_phase (id,name) VALUES (7, 'Phase 3/Phase 4');
INSERT INTO ctdb.ct_phase (id,name) VALUES (8, 'Phase 4');
INSERT INTO ctdb.ct_phase (id,name) VALUES (9, 'NA');


-- Completed on 2023-04-19 11:37:15 EDT

--
-- PostgreSQL database dump complete
--



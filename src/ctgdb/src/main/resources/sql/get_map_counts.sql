use quaranteam_ctdb;

-- Condition mapping results

SELECT count(*) FROM quaranteam_ctdb.conditions;
-- 92733

SELECT count(*) FROM quaranteam_ctdb.conditions where meddra_id is not null;
-- 52374
-- 56.48 % mapped

SELECT count(*) FROM quaranteam_ctdb.conditions where meddra_id is not null and exact_match = 1;
-- 10690
-- 11.53 % exact match

SELECT count(*) FROM quaranteam_ctdb.conditions where meddra_id is not null and exact_match = 0;
-- 41684
-- 44.95 %

-- Adverse Event mapping results

SELECT count(*) FROM quaranteam_ctdb.adverse_event;
-- 102868

SELECT count(*) FROM quaranteam_ctdb.adverse_event where meddra_id is not null;

-- 63304
-- 61.54 % mapped

SELECT count(*) FROM quaranteam_ctdb.adverse_event where meddra_id is not null and exact_match = 1;
-- 22098
-- 21.48 % exact match

SELECT count(*) FROM quaranteam_ctdb.adverse_event where meddra_id is not null and exact_match = 0;
-- 41206
-- 40.06 %


-- example query to link non-exact matches to compare terms
SELECT ae.adverse_event, m.term 
FROM quaranteam_ctdb.adverse_event ae, quaranteam_ctdb.meddra m
WHERE
    ae.meddra_id IS NOT NULL AND
    ae.meddra_id = m.id AND
    ae.exact_match = 0;


# CTGDB: ClinicalTrials.gov Drug Safety Database Example

This repository contains work completed as part of my **Master’s project** for  
**Course 6424 – Data Visualization** at **Georgia Institute of Technology**.

The project explores methods for extracting, structuring, and enriching publicly
available **ClinicalTrials.gov (CT.gov)** data for use in **drug safety and
pharmacovigilance** contexts.

---

## Project Background

As part of the course project, a complete data pipeline was developed to:

- Ingest publicly available **ClinicalTrials.gov** data
- Create a **MySQL database** representation of trials and outcomes
- Map adverse events (AEs) to **MedDRA** terminology
- Explicitly extract and preserve **demographic data** (e.g., age, sex), which is
  often underrepresented or difficult to access in other downstream datasets

While this implementation is intentionally simpler than large-scale efforts such as
the **AACT database**  
https://aact.ctti-clinicaltrials.org/  

we believe it provides a useful and transparent starting point for researchers and
practitioners interested in working directly with CT.gov data in drug safety settings.

---

## Documentation and Reports

The written materials produced during the project include:

- **Proposal** – initial project scope and design
- **Progress Report** – mid-project status and refinements
- **Final Report and Poster** – presented at the conclusion of the course

These documents describe the motivation, data processing approach, design decisions,
and results of the project.

---

## Source Code

All source code for the project is located in the **`java/`** directory.

The code demonstrates:
- Parsing and normalization of CT.gov data
- Construction of a relational MySQL schema
- Mapping of adverse event terminology to MedDRA
- Explicit handling of demographic fields to support downstream safety analyses

---

## License

This work is released under the **Apache License, Version 2.0 (ASL 2.0)**.

It is provided as an **educational and research example** of how publicly available
ClinicalTrials.gov data can be structured and reused for drug safety and
pharmacovigilance applications.

---

## Disclaimer

This project was developed for academic purposes as part of a graduate course.

but rather as a complementary, lightweight example emphasizing transparency,
accessibility, and methodological clarity.

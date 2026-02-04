#!/usr/bin/env bash
#
# download_ctdata.sh
#
# Downloads the latest publicly available ClinicalTrials.gov XML dataset
# and extracts it into src/main/data/.
#

set -euo pipefail

# -----------------------------
# Configuration
# -----------------------------
CT_URL="https://clinicaltrials.gov/AllPublicXML.zip"
DATA_DIR="src/main/data"
ZIP_FILE="AllPublicXML.zip"

# -----------------------------
# Download latest CT.gov data
# -----------------------------
echo "Downloading ClinicalTrials.gov data..."
wget -O "${ZIP_FILE}" "${CT_URL}"

# -----------------------------
# Prepare data directory
# -----------------------------
echo "Resetting data directory: ${DATA_DIR}"
rm -rf "${DATA_DIR}"
mkdir -p "${DATA_DIR}"

# -----------------------------
# Move and extract data
# -----------------------------
echo "Extracting data..."
mv "${ZIP_FILE}" "${DATA_DIR}/"
cd "${DATA_DIR}"

unzip "${ZIP_FILE}"

echo "Done. ClinicalTrials.gov data is ready in ${DATA_DIR}"

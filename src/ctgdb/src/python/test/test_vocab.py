#
# Test vocab for overlap
#
import csv
import pandas as pd

input_dir = "/home/painter/umls/mdr2025AA/2025AA/META/"
mrconso = input_dir + "MRCONSO.RRF"

# Correct list of 18 column names from UMLS documentation
column_names = [
    'CUI', 'LAT', 'TS', 'LUI', 'STT', 'SUI', 'ISPREF', 'AUI',
    'SAUI', 'SCUI', 'SDUI', 'SAB', 'TTY', 'CODE', 'STR',
    'SRL', 'SUPPRESS', 'CVF'
]

# Read the MRCONSO.RRF file with proper handling
df = pd.read_csv(
    mrconso,
    sep='|',
    header=None,
    names=column_names,
    dtype=str,
    index_col=False,            # <-- This is the key fix to prevent auto-indexing
    engine='python',
    quoting=csv.QUOTE_NONE,
    on_bad_lines='warn'
)

# Display key fields to verify alignment
#print(df[['CUI', 'SAB', 'TTY', 'CODE', 'STR']].head())
  
# Step 1: Filter sources (and make explicit copies to avoid SettingWithCopyWarning)
who_cst = df[df['SAB'].isin(['WHO', 'CST']) & (df['SUPPRESS'] == 'N')].copy()
mdr = df[df['SAB'] == 'MDR'].copy()

# Step 2: Normalize and collect MDR strings
mdr_strs = set(mdr['STR'].str.upper())

# Step 3: Add normalized STR column in WHO/CST
who_cst['STR_UPPER'] = who_cst['STR'].str.upper()

# Step 4: Find WHO/CST strings not in MDR
not_in_mdr = who_cst[~who_cst['STR_UPPER'].isin(mdr_strs)].drop(columns=['STR_UPPER'])

# Step 6: Get set of all CUIs that appear in MedDRA
mdr_cuis = set(mdr['CUI'])

# Step 7: Mark which unmatched WHO/CST terms have CUIs in MedDRA
not_in_mdr['CUI_in_MDR'] = not_in_mdr['CUI'].isin(mdr_cuis)

# Step 8: Optional – separate into two groups
cui_found = not_in_mdr[not_in_mdr['CUI_in_MDR']].copy()
cui_missing = not_in_mdr[~not_in_mdr['CUI_in_MDR']].copy()

# View results
print("Total unmatched WHO/CST terms:", len(not_in_mdr))
print("Unmatched terms whose CUI exists in MedDRA:", len(cui_found))
print("Unmatched terms whose CUI does NOT exist in MedDRA:", len(cui_missing))

  
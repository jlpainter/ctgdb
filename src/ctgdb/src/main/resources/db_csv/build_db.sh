#
# Create database
#
export DB_USER=username
export DB_PASS=password

mysql -u ${DB_USER} -p${DB_PASS} ctgdb < ct_db.sql

# Import data
mysql -u ${DB_USER} -p${DB_PASS} ctgdb < import-csv-tables.sql


Notes from Jeff 2023-11-13

To create a new database:

1. Connect to the server using the pgdb script in this folder

2. Create the database
    postgres=> CREATE DATABASE ctdb2023nov;

3. Connect to the new database
    \c ctdb2023nov

4. Run the create table script interactively

5. Load the data using the import file
    \copy works but COPY does not with the remote server.

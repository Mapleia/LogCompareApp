# LOGCOMPARE - GW2 APP

## OVERVIEW

Uses Guild Wars 2 Elite Insight Parser to parse ArcDPS files for comparison. 
This tool creates .json files of percentiles, based on data already inputted by the user (local only).
Requires a database and Gw2 Elite Insight Parser installation.

## SETUP

1. Go to release section, then download the provided installer and install LogCompare.
2. Go to https://mariadb.org/download/ and setup as instructed (below).

### SETUP MARIADB
A) Click next. 

![A](./data/assets/step1.png)

B) Set your password, be sure to remember it to set up LogCompare later. Click next. 

![B](./data/assets/step2.png)

C) Keep as default, click next. 

![C](./data/assets/step3.png)

D) Click install. 

![D](./data/assets/step4.png)

3. Open sample.properties file found in "/data/assets/sample.properties" and change password to the one you set up with 
your database. Save the file.

4. Launch LogCompare!

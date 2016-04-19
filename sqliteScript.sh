#!/bin/bash

DB_NAME=moviescript
SQL_SCRIPT_FILE=./ddlScript.sql

sqlite3 $DB_NAME.db < $SQL_SCRIPT_FILE

#!/bin/bash
cd "$(dirname "$0")"
java -cp "bin:lib/mysql-connector-j-8.4.0.jar" app.Main

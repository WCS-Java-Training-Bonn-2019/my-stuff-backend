#! /bin/bash

/root/mysql_startup.sh&

sleep 10

java -jar /root/app.jar

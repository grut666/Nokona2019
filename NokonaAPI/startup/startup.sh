#!/bin/bash
cd /t/Development/apache-tomee-plume-7.1.4/logs
rm catalina.out
cd /t/Development/apache-tomee-plume-7.1.4/bin 
echo Starting Shutdown
./shutdown.sh
sleep 5
echo Starting Startup
./startup.sh
echo Finished

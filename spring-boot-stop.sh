#!/bin/bash
PID_FILE=application.pid

if [ -f "$PID_FILE" ]
then
    echo "Application Shutting down ..."
    kill -SIGTERM `cat ${PID_FILE}`
else
    echo "Application is already stopped"
fi
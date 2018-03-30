#!/bin/bash
echo "Starting at" `date`

if [ "$#" -ne 1 ]
then
    echo "Wrong arguments! Requires 1 argument [APPLICATION_NAME]" >&2
    exit 1
fi

APPLICATION_NAME=$1
echo "APPLICATION_NAME: $APPLICATION_NAME"

nohup java -jar ${APPLICATION_NAME} --logging.path=./logs > ./nohup.out 2>&1 &
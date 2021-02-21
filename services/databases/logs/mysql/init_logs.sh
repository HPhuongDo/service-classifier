#!/bin/bash

if [ ! -f /var/log/mysql/mysql.log ]; then
    touch /var/log/mysql/mysql.log
fi

chmod 777 /var/log/mysql/mysql.log
# chmod 0700 /var/log/postgres/postgres.log

#! /bin/bash

case "$1" in 
start)
    # only run databases first
    log_path=$(dirname $0)/logs/simple/
    docker_path=$2
    time=$(date +%y%m%d-%H%M)

    #docker-compose -f $docker_path up -d
    docker-compose -f $docker_path --env-file ./config/.env up -d

    # create logs for all the containers
    containers=$(docker ps --format "{{.Names}}")
    for container in $containers
    do
        # echo "Logging $container in ${container}_$time.log"
        mkdir -p $log_path
        # docker-compose -f $docker_path/docker-compose.yml logs -f $container > $docker_path/logs/${container}/${container}_$time.log &
        docker logs -f $container > $log_path/${container}.log &
    done
    ;;
stop)
   docker stop $(docker ps -q)
   ;;
*)
    echo "Usage: $0 {start|stop} relative-path-to-docker-compose.yml"
esac

exit 0 
#! /bin/bash

case "$1" in 
start)
    # only run databases first
    # log_path=$(dirname $0)/classifier/ServiceClassifier/src/main/resources    # later log in resources folder
    docker_path=$(dirname $0)/services/$2
    time=$(date +%y%m%d-%H%M)

    docker-compose -f $docker_path/docker-compose.yml up -d

    # create logs for the containers
    containers=$(docker ps --format "{{.Names}}")
    for container in $containers
    do
        # echo "Logging $container in ${container}_$time.log"
        mkdir -p $docker_path/logs/$container
        # docker-compose -f $docker_path/docker-compose.yml logs -f $container > $docker_path/logs/${container}/${container}_$time.log &
        docker-compose -f $docker_path/docker-compose.yml logs -f $container > $docker_path/logs/${container}/${container}.log &
    done
    ;;
stop)
   docker stop $(docker ps -q)
   ;;
*)
   echo "Usage: $0 {start|stop} {databases|webapi}"
esac

exit 0 
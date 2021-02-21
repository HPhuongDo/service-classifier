Start all:
docker-compose --env-file ./config/.env up -d (to run in background)

https://github.com/google/cadvisor
Start cAdvisor:
VERSION=v0.36.0 # use the latest release version from https://github.com/google/cadvisor/releases
sudo docker run \
  --volume=/:/rootfs:ro \
  --volume=/var/run:/var/run:ro \
  --volume=/sys:/sys:ro \
  --volume=/var/lib/docker/:/var/lib/docker:ro \
  --volume=/dev/disk/:/dev/disk:ro \
  --publish=8080:8080 \
  --detach=true \
  --name=cadvisor \
  --privileged \
  --device=/dev/kmsg \
  gcr.io/cadvisor/cadvisor:latest


List all running containers:
docker ps

List all (-a) containers (-q only IDs):
docker ps -aq

Stop all running containers:
docker stop $(docker ps -aq)

Remove all containers:
docker rm $(docker ps -aq)

Remove all images:
docker rmi $(docker images -q)


--------------
docker pull ...
docker run ...
Run commands inside a docker container:
docker exec -it (container name) (bash)

To redirect the current logs to a file, use a redirection operator.
$ docker logs (container name) > output.log

To send the current logs and then any updates that follow, use –follow with the redirection operator.
$ docker logs -f (container name) > output.log

# Riak

Riak KV is a dynamo-style distributed database. Dynamo-style means it makes use of **leaderless replication** across nodes. It is a key-value NoSQL data system. 

## Setting up and connecting to a single node

[Docker Setup](https://riak.com/posts/technical/running-riak-in-docker/index.html?p=12629.html)

Set up a single node: `docker run --name=riak -d -p 8087:8087 -p 8098:8098 basho/riak-kv`

    Note: the first port is for database client connection, the second is for Riak control web connection.

### [Client Libraries](https://docs.riak.com/riak/kv/2.2.3/developing/client-libraries/index.html)

My first choice of language is Python, but unfortunetely Riak is not compatible with Python >3.5 and version that old are no longer available to officially install anymore :'(

For server-side and non-web work my next choice is Java, so let's give it a go.

Following the [quick setup guide](https://github.com/basho/riak-java-client/tree/1.4.4#quick-start) I was able to immediately starting inserting, querying, and deleting data from my node (see [quickstart.java](/riak/src/main/java/com/will/java_riak/quickstart.java) for the code)

### Control Panel - Riak Control

https://docs.riak.com/riak/kv/2.2.3/using/admin/riak-control/

Riak control is disabled by default but can be enabled by adjusting the config file on the node (`etc/riak/riak.conf`).

Once enabled, the default link to the admin panel is http://127.0.0.1:8098/admin

## Setting up additional nodes

Riak may be leaderless, but it still makes use of [coordinator nodes](https://riak.com/posts/technical/riak-core-the-coordinator/index.html?p=6091.html). The first step in starting your own Riak cluster is to spin up a coordinator node. Other nodes will join the cluster through the coordinator.

You can start a coordinator node with the collowing command: `docker run --name=riak -d -p 8087:8087 -p 8098:8098 --label cluster.name=adhoc basho/riak-kv`

The coordinator's IP Address can be discovered with `docker inspect -f '{{.NetworkSettings.IPAddress}}' riak`

Other containers can now be started: `docker run -d -P -e COORDINATOR_NODE=172.17.0.2 --label cluster.name=adhoc basho/riak-kv` (don't forget to change the IP, or make it dynamic using the previous command).

Now if we go to the admin panel for the coordinator we can see all of the nodes in the cluster!

These ad-hoc docker commands have been rolled into a [docker-compose file](docker-compose.yml).
Bring up the Docker container and scale to any number of member nodes using `docker-compose scale coordinator=1 member=4`.

Run `docker-compose exec coordinator riak-admin cluster status` to check the membership status of the cluster.

## Proxy and Load Balancing (HAProxy)

So now we have a cluster of nodes that replicate data with each-other. This is cool, but in a production system, you should have a single endpoint, not a list of possible nodes to hit. This can be handled with a load balancer to split traffic across our nodes.

In the haproxy folder in this directory, there is a Dockerfile and a haproxy config file. These will be used automatically by the Riak docker-compose file, however the IPs and ports may need to be updated.

The config file used is the one provided as an example on the riak docs: https://docs.riak.com/riak/kv/latest/configuring/load-balancing-proxy/index.html and edited following the setup docs for haproxy: https://www.haproxy.com/documentation/hapee/latest/configuration/config-sections/backend/

Debug any errors inthe haproxy config by building the docker container with `docker build -t my-haproxy .` and test the config file with `docker run -it --rm --name haproxy-syntax-check my-haproxy haproxy -c -f /usr/local/etc/haproxy/haproxy.cfg`.



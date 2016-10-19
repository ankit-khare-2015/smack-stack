##Prerequisites

Docker and docker-compose are used for running code samples:

      *docker version 1.12.2
      *docker-compose 1.8.1
      
      *For Windows machine please install docker tool box https://www.docker.com/products/docker-toolbox

For building the app, MAVEN is used      
      
      *MAVEN 3.3.9 Embeded Eclipse version



##Setting Dockerized Environment
### Creating docker-machine and launching containers

Depending on one's needs virtual machine memory could be adjusted to different value, but memory should be more than 5GB. Steps to create new 
docker-machine and launch docker images:  

      docker-machine create -d virtualbox --virtualbox-memory "8000" --virtualbox-cpu-count "4" smack-demo
      eval "$(docker-machine env workshop)"
      
      docker-machine start workshop : to start default machine
	  
	  docker-machine env workshop   

      docker-compose up -d :This command will bring up the images also it might take a little while since it downloads all the images  
      
      docker ps            : This Command will will display the currently running container
      
      winpty docker  exec -ti dataenggsmack_hadoop_1 bash : For windows user they can using this command for connecting to container all linux user can run this minus winpty 
      
      winpty docker  exec -ti dataenggsmack_cassandra_1 bash

After that dockerized Cassandra, MongoDB and single-node Hadoop cluster will launched. `docker ps` 
could be used for verification and getting ids of containers. After a short delay all UI components should be accessible via browser (except SparkUI).

Project build directory is linked to hadoop container and available at `/target` `/dataset` folder. Every time fatjar is rebuilt it is visible inside the container.

For shutting down with deletion of all the data use `docker-compose down`
      

Logging in to Docker container
      
      #container name could be used to login
      docker exec -ti smackstack_hadoop_1 bash (in linux env)
       
      winpty docker  exec -ti  smackstack_hadoop_1 bash (in windows)

##Initial Setup
Download the yelp data tar file https://www.yelp.com/dataset_challenge/dataset
untar the file and copy all the json file and store it 'data-set' folder in project root directory since it has been mounted in docker-compose-yml     

###Loading data to HDFS
*winpty docker  exec -ti  smackstack_hadoop_1 bash

*hadoop fs -put data-set/* .
      
###Create keyspace in cassandra database
*winpty docker  exec -ti  smackstack_cassandra_1 bash

open cassandra shell by executing  'cqlsh' cmd

create keyspace by running below command

*CREATE KEYSPACE yelp_data WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};

      
## To run and submit Spark Applications from this project the fatjar should be assembled via `mvn clean install` from the root of project dir.

            
      spark-shell \
      --master yarn \
      --deploy-mode client \
      --executor-cores 1 \
      --num-executors 2 \
      --jars /target/data-engg-smack-0.0.1-SNAPSHOT-shaded.jar \
      --conf spark.cassandra.connection.host=cassandra
	  --class packagename.classname	
      

##List of applications

This application parse and load data from yelp json file through spark and load the data into cassandra keyspace and performs 
some simple SparkQL query check on business , categories and tip tables created in yelp_data keyspace using spark  

* __YelpBusinessDataLoader__ - This file loads yelp business and categories data in cassandra: `classname.method` 
* __YelpTipDataLoader__ - This file loads yelp tip data in cassandra keyspace
* __YelpTipQueryLoader__ - SparkSQL based implementation using DataFrame API and Cassandra as a data source

## Yelp Data Load steps 
__Note __ - Load Yelp business and categories  and Load Yelp tip data  should be executed first else there would be come exception while running YelpTipQueryLoader

### Load Yelp business and categories 
spark-shell \
  --master yarn \
  --class com.newyorker.data_engg.data_engg_smack.YelpBusinessDataLoader \
  --deploy-mode client \
  --executor-cores 1 \
  --num-executors 2 \
  --jars target/data-engg-smack-0.0.1-SNAPSHOT.jar \
  --conf spark.cassandra.connection.host=cassandra

###Load Yelp tip data 

spark-shell \
  --master yarn \
  --class com.newyorker.data_engg.data_engg_smack.YelpTipDataLoader \
  --deploy-mode client \
  --executor-cores 1 \
  --num-executors 2 \
  --jars target/data-engg-smack-0.0.1-SNAPSHOT.jar \
  --conf spark.cassandra.connection.host=cassandra
  
  
###Query Yelp Data

spark-shell \
  --master yarn \
  --class com.newyorker.data_engg.data_engg_smack.YelpTipQueryLoader \
  --deploy-mode client \
  --executor-cores 1 \
  --num-executors 2 \
  --jars target/data-engg-smack-0.0.1-SNAPSHOT.jar \
  --conf spark.cassandra.connection.host=cassandra

  
  
## Data cleanup
      
      #HDFS files
      docker exec -ti smackstack_hadoop_1  bash 
      hadoop fs -rm -r /*.json
      
      #Cassandra tables
      docker exec -ti smackstack_cassandra_1 cqlsh
      cqlsh> drop keyspace yelp_data;
      
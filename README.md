##Prerequisites

Docker and docker-compose are used for running code samples:

      docker version 1.12.2
      docker-compose 1.8.1
      
      For Windows machine please install docker tool box https://www.docker.com/products/docker-toolbox

For building the app, MAVEN is used      
      
      MAVEN 3.3.9 Embeded Eclipse version



##Setting Dockerized Environment
### Creating docker-machine and launching containers

Depending on one's needs virtual machine memory could be adjusted to different value, but memory should be more than 2GB. Steps to create new 
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
      
##Running spark-shell with samples
To run and submit Spark Applications from this project the fatjar should be assembled via `mvn clean install` from the root of project dir.

Logging in to Docker container
      
      #container name could be used to login
      docker exec -ti smackstack_hadoop_1 bash (in linux env)
       
      winpty docker  exec -ti  smackstack_hadoop_1 bash (in windows)

##Initial Setup
Download the yelp data tar file https://www.yelp.com/dataset_challenge/dataset
untar the file and copy all the json file and store it 'data-set' folder in project root directory since it has been mounted in docker-compose-yml     
#Loading data to HDFS
winpty docker  exec -ti  smackstack_hadoop_1 bash

hadoop fs -put data-set/* .
      
#Create keyspace in cassandra database
winpty docker  exec -ti  smackstack_cassandra_1 bash

open cassandra shell by executing  'cqlsh' cmd

create keyspace by running below command

CREATE KEYSPACE yelp_data WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};

      
Running Spark shell with fatjar in classpath will allow to execute applications right from spark-shell 
            
      spark-shell \
      --master yarn \
      --deploy-mode client \
      --executor-cores 1 \
      --num-executors 2 \
      --jars /target/data-engg-smack-0.0.1-SNAPSHOT-shaded.jar \
      --conf spark.cassandra.connection.host=cassandra
	  --class packagename.classname	
      

##List of applications

API examples provide implementation for the "naive lambda" application which reads aggregated data from campaigns data source and groups it with 
"raw" event data from events data source. After sample data is generated it is available in Json, Parquet and Bson files in HDFS (/workshop dir)
and in Cassandra and MongoDB. 

* __YelpBusinessDataLoader__ - SOME text: `classname.method` 
* __YelpTipDataLoader__ - SOME TEXT  `classname.method` 
* __YelpTipQueryLoader__ - SparkSQL based implementation using DataFrame API and Cassandra as a data source

## Yelp Data Load steps 


# Load Yelp business and categories 
spark-shell \
  --master yarn \
  --class com.newyorker.data_engg.data_engg_smack.YelpBusinessDataLoader \
  --deploy-mode client \
  --executor-cores 1 \
  --num-executors 2 \
  --jars target/data-engg-smack-0.0.1-SNAPSHOT.jar \
  --conf spark.cassandra.connection.host=cassandra

#Load Yelp tip data 

spark-shell \
  --master yarn \
  --class com.newyorker.data_engg.data_engg_smack.YelpTipDataLoader \
  --deploy-mode client \
  --executor-cores 1 \
  --num-executors 2 \
  --jars target/data-engg-smack-0.0.1-SNAPSHOT.jar \
  --conf spark.cassandra.connection.host=cassandra
  
  
##Query Yelp Data

spark-shell \
  --master yarn \
  --class com.newyorker.data_engg.data_engg_smack.YelpTipQueryLoader \
  --deploy-mode client \
  --executor-cores 1 \
  --num-executors 2 \
  --jars target/data-engg-smack-0.0.1-SNAPSHOT.jar \
  --conf spark.cassandra.connection.host=cassandra

  
  
      
## Data model

Through all the code samples the same Cassandra data model is used:



## Data cleanup
      
      #HDFS files
      docker exec -ti sparkworkshop_hadoop_1  bash 
      hadoop fs -rm -r /workshop/*
      
      #Cassandra tables
      docker exec -ti sparkworkshop_cassandra_1 cqlsh
      cqlsh> drop keyspace demo;
      
      #Mongo DB
      docker exec -ti sparkworkshop_mongo_1 mongo
      >use demo
      >db.dropDatabase()
# Yelp Data Engineering Project (SMACK Stack)

This project uses Docker and the SMACK stack (Spark, Mesos, Akka, Cassandra, Kafka) to load Yelp dataset files into a Cassandra database and run SparkSQL queries. It’s a great starting point for data engineering practice.

---

## 🔧 Prerequisites

### Tools
- **Docker** (recommended version: ≥ 20.x)
- **Docker Compose** (recommended version: ≥ 1.29)
- **Maven** (≥ 3.6, embedded in many IDEs like Eclipse or IntelliJ)

For **Windows users**, install Docker Desktop: [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop)

---

## ⚙️ Setting Up the Docker Environment

### 1. Create and Start Docker Machine

> Optional: For VirtualBox setups where `docker-machine` is still used:

```bash
docker-machine create -d virtualbox --virtualbox-memory "8000" --virtualbox-cpu-count "4" smack-demo
eval "$(docker-machine env smack-demo)"
docker-machine start smack-demo
```

### 2. Launch Containers

```bash
docker-compose up --build -d
```

To stop and remove containers + volumes:

```bash
docker-compose down -v
```

### 3. Check Running Containers

```bash
docker ps
```

### 4. Access Running Containers

- **Hadoop container:**

```bash
docker exec -ti smackstack_hadoop_1 bash
```

- **Cassandra container:**

```bash
docker exec -ti smackstack_cassandra_1 bash
```

---

## 📦 Initial Setup

1. Download Yelp data from:  
   https://www.yelp.com/dataset

2. Extract all `.json` files and place them in a folder called `data-set` in the root of this project.

3. Confirm volume mounts in your `docker-compose.yml` file.

---

## ⛓️ Load Data to HDFS

```bash
docker exec -ti smackstack_hadoop_1 bash
hadoop fs -mkdir -p /yelp
hadoop fs -put data-set/*.json /yelp/
```

---

## 🗃️ Setup Cassandra

```bash
docker exec -ti smackstack_cassandra_1 bash
cqlsh
```

Inside the Cassandra shell:

```sql
CREATE KEYSPACE yelp_data WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};
```

---

## 🚀 Build and Run Spark Applications

Build the fat JAR:

```bash
mvn clean install
```

Now you can run Spark jobs using:

```bash
spark-shell \
  --master yarn \
  --deploy-mode client \
  --executor-cores 1 \
  --num-executors 2 \
  --jars target/data-engg-smack-0.0.1-SNAPSHOT.jar \
  --conf spark.cassandra.connection.host=cassandra \
  --class <fully.qualified.ClassName>
```

---

## 📌 Available Spark Applications

| Application            | Description                                                             |
|------------------------|-------------------------------------------------------------------------|
| `YelpBusinessDataLoader` | Loads Yelp business + category data into Cassandra                     |
| `YelpTipDataLoader`      | Loads tip data into Cassandra                                           |
| `YelpTipQueryLoader`     | Runs SparkSQL queries over loaded data using DataFrame API             |

> ⚠️ Run loaders **before** query jobs, or you'll encounter missing table errors.

---

## 📊 Load & Query Yelp Data

### Load Business Data

```bash
spark-submit \
  --class com.newyorker.data_engg.data_engg_smack.YelpBusinessDataLoader \
  ...
```

### Load Tip Data

```bash
spark-submit \
  --class com.newyorker.data_engg.data_engg_smack.YelpTipDataLoader \
  ...
```

### Query Tip Data

```bash
spark-submit \
  --class com.newyorker.data_engg.data_engg_smack.YelpTipQueryLoader \
  ...
```

---

## 🧹 Data Cleanup

### HDFS Cleanup

```bash
docker exec -ti smackstack_hadoop_1 bash
hadoop fs -rm -r /yelp/*.json
```

### Cassandra Cleanup

```bash
docker exec -ti smackstack_cassandra_1 cqlsh
DROP KEYSPACE yelp_data;
```

---

## 📝 Notes & Improvements

- Default credentials mentioned (`postgres/postgres`) may differ. Update `.env` if using `admin/admin123`.
- Architecture diagrams should ideally be modeled in tools like **Lucidchart**, **draw.io**, or **Diagrams.net**.
- Improve `.gitignore` to exclude:
  ```gitignore
  pddata_clean/
  logs/
  housing_dbt/
  ```

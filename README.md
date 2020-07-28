### Using MySQL and the Avro message format

Demo app catch message from "schema-changes.inventory" topic

Run docker
```
docker-compose -f docker-compose-mysql-avro-connector.yaml up
```

Start Mysql Connector
```
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @register-mysql.json
```

Start app
```
mvn clean install
java -jar target/DebeziumDemo-1.0-SNAPSHOT-jar-with-dependencies.jar
```
import io.debezium.config.Configuration;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;

import java.io.File;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@lombok.extern.slf4j.Slf4j
public class Main {
    public static UploadFileTask uploadFileTask = new UploadFileTask("");
    public static int record_count = 0;
    public static void main(String[] args) {
        Configuration config = Configuration.create().build();
        final Properties props = config.asProperties();
        props.setProperty("name", "inventory-connector");
        props.setProperty("connector.class", "io.debezium.connector.mysql.MySqlConnector");
        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        props.setProperty("offset.storage.file.filename", "/home/ntn/IdeaProjects/DebeziumDemo/offsets.dat");
        props.setProperty("offset.flush.interval.ms", "60000");
        props.setProperty("key.converter.schemas.enable","false");
        props.setProperty("value.converter.schemas.enable","false");

        /* begin connector properties */
        props.setProperty("database.hostname", "localhost");
        props.setProperty("database.port", "3306");
        props.setProperty("database.user", "root");
        props.setProperty("database.password", "debezium");
        props.setProperty("database.server.id", "184054");
        props.setProperty("database.server.name", "dbserver1");
        props.setProperty("databbase.whitelist", "inventory");
        props.setProperty("database.history.kafka.bootstrap.servers", "localhost:9092");
        props.setProperty("database.history.kafka.topic", "schema-changes.inventory");
//        props.setProperty("database.history",
//                "io.debezium.relational.history.FileDatabaseHistory");
//        props.setProperty("database.history.file.filename",
//                "/home/ntn/IdeaProjects/DebeziumDemo/dbhistory.dat");

//        create upload dir
        File file = new File("./uploadFile");
        boolean create = !file.exists() && file.mkdir();
        if (create) {
            log.info("UploadFile Directory created");
        }

// Create the engine with this configuration ...
        try (DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
                .using(props)
                .notifying(record -> {
                    concatLog(record.toString());
                }).build()
        ) {
//            Executors.newSingleThreadExecutor().execute(engine);
            // Run the engine asynchronously ...
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(engine);
//            engine.run();
//            try {
//                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//            } catch (InterruptedException e) {
//                System.out.println(e.toString());
//            }
            // Do something else or wait for a signal or an event
        } catch (Exception e) {
//            e.printStackTrace();
            log.error(e.toString());
        }
        // Engine is stopped when the main code is finished

        Timer timer = new Timer();
        timer.schedule(uploadFileTask,0,5000);
    }

    public static void concatLog(String notify){
        record_count += 1;
        uploadFileTask.setMysqlLog(uploadFileTask.getMysqlLog() + notify);
    }
}

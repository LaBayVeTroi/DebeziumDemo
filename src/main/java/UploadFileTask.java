import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

@lombok.extern.slf4j.Slf4j
@Getter
@Setter
public class UploadFileTask extends TimerTask {
    public static MinioClient minioClient = null;

    private String mysqlLog;

    static {
        //create minio client and bucket "test"
        minioClient =
                MinioClient.builder()
                        .endpoint("http://localhost:9000")
                        .credentials("minioadmin", "minioadmin")
                        .build();
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket("test").build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("test").build());
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    UploadFileTask(String mysqlLog) {
        this.mysqlLog = mysqlLog;
    }

    @Override
    public void run() {
        uploadFileToMinIO(this.mysqlLog,minioClient);
        this.mysqlLog = "";
    }

    public static void uploadFileToMinIO(String notify, MinioClient minioClient) {
        if(notify == null || notify.equals("")){
            log.info("Database don't change !!!");
            return;
        }
        log.info(notify);
        Date date = new Date();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        String fileName = "mysql-log-" + dateStr;
        File file = new File("./uploadFile/" + fileName);
        try {
            boolean created = file.createNewFile();
            if (created) {
                System.out.println("File is created");
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.append(notify);
        }
        catch (Exception e){
            log.error(e.toString());
        }
        try {
            minioClient.uploadObject(UploadObjectArgs.builder().bucket("test").object(fileName).filename("./uploadFile/" + fileName).build());
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}

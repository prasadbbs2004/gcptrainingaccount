import com.google.cloud.storage.transfermanager.ParallelUploadConfig;
import com.google.cloud.storage.transfermanager.TransferManager;
import com.google.cloud.storage.transfermanager.TransferManagerConfig;
import com.google.cloud.storage.transfermanager.UploadResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class UploadDirectory {

  public static void uploadDirectoryContents(String bucketName, Path sourceDirectory)
      throws IOException {
    TransferManager transferManager = TransferManagerConfig.newBuilder().build().getService();
    ParallelUploadConfig parallelUploadConfig =
        ParallelUploadConfig.newBuilder().setBucketName(bucketName).build();

    // Create a list to store the file paths
    List<Path> filePaths = new ArrayList<>();
    // Get all files in the directory
    // try-with-resource to ensure pathStream is closed
    try (Stream<Path> pathStream = Files.walk(sourceDirectory)) {
      pathStream.filter(Files::isRegularFile).forEach(filePaths::add);
    }
    List<UploadResult> results =
        transferManager.uploadFiles(filePaths, parallelUploadConfig).getUploadResults();
    for (UploadResult result : results) {
      System.out.println(
          "Upload for "
              + result.getInput().getName()
              + " completed with status "
              + result.getStatus());
    }
  }
}
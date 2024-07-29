package com.heredata.swift.operation;

import com.heredata.event.ProgressEventType;
import com.heredata.event.ProgressListener;
import com.heredata.event.ProgressPublisher;
import com.heredata.swift.model.*;
import com.heredata.utils.IOUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static com.heredata.comm.HttpConstants.DEFAULT_BUFFER_SIZE;
import static com.heredata.comm.HttpConstants.KB;
import static com.heredata.swift.utils.SwiftUtils.*;
import static com.heredata.utils.CodingUtils.assertParameterNotNull;
import static com.heredata.utils.LogUtils.logException;

/**
 * <p>Title: SwiftDownloadOperation</p>
 * <p>Description: 对象下载操作 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 18:02
 */
public class SwiftDownloadOperation {

    protected SwiftObject getObjectWrap(GetObjectRequest getObjectRequest) {
        return objectOperation.getObject(getObjectRequest);
    }

    protected Long getInputStreamCRCWrap(InputStream inputStream) {
        return IOUtils.getCRCValue(inputStream);
    }

    /**
     * 修复漏洞3.1.1.2    漏洞来源代码扫描报告-cmstoreos-sdk-java-1215-0b57751a.pdf
     */
    static class DownloadCheckPoint extends ObjectInputStream implements Serializable {

        private static final long serialVersionUID = 4682293344365787077L;

        private static final String DOWNLOAD_MAGIC = "92611BED-89E2-46B6-89E5-72F273D4B0A3";

        public DownloadCheckPoint() throws IOException {
            super();
        }

        /**
         * 在内部设置白名单机制，只允许序列化已知的类。
         */
        @Override
        protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
            if (!osc.getName().equals(DownloadCheckPoint.class.getName())) {
                throw new InvalidClassException("Unauthorized deserialization", osc.getName());
            }
            return super.resolveClass(osc);
        }

        /**
         * 修复漏洞3.2.2.1  资源没有安全释放  漏洞来源代码扫描报告-cmstoreos-sdk-java-1215-0b57751a.pdf
         * Loads the checkpoint data from the checkpoint file.
         */
        public synchronized void load(String cpFile) throws IOException, ClassNotFoundException {
            FileInputStream fileIn = null;
            ObjectInputStream in = null;
            try {
                fileIn = new FileInputStream(cpFile);
                in = new ObjectInputStream(fileIn);
                DownloadCheckPoint dcp = (DownloadCheckPoint) in.readObject();
                assign(dcp);
            } finally {
                in.close();
                fileIn.close();
            }
        }

        /**
         * Writes the checkpoint data to the checkpoint file.
         */
        public synchronized void dump(String cpFile) throws IOException {
            this.md5 = hashCode();
            FileOutputStream fileOut = new FileOutputStream(cpFile);
            ObjectOutputStream outStream = new ObjectOutputStream(fileOut);
            outStream.writeObject(this);
            outStream.close();
            fileOut.close();
        }

        /**
         * Updates the part's download status.
         *
         * @throws IOException
         */
        public synchronized void update(int index, boolean completed) throws IOException {
            downloadParts.get(index).isCompleted = completed;
        }

        /**
         * Check if the object matches the checkpoint information.
         */
        public synchronized boolean isValid(SwiftObjectOperation objectOperation, DownloadFileRequest downloadFileRequest) {
            // 比较checkpoint的magic和md5
            if (this.magic == null || !this.magic.equals(DOWNLOAD_MAGIC) || this.md5 != hashCode()) {
                return false;
            }

            GenericRequest genericRequest = new GenericRequest(bucketName, objectKey);

            SimplifiedObjectMeta meta = objectOperation.getSimplifiedObjectMeta(genericRequest);

            // Object's size, last modified time or ETAG are not same as the one
            // in the checkpoint.
            if (this.objectStat.size != meta.getSize() || !this.objectStat.lastModified.equals(meta.getLastModified())
                    || !this.objectStat.digest.equals(meta.getETag())) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((bucketName == null) ? 0 : bucketName.hashCode());
            result = prime * result + ((downloadFile == null) ? 0 : downloadFile.hashCode());
            result = prime * result + ((magic == null) ? 0 : magic.hashCode());
            result = prime * result + ((objectKey == null) ? 0 : objectKey.hashCode());
            result = prime * result + ((objectStat == null) ? 0 : objectStat.hashCode());
            result = prime * result + ((downloadParts == null) ? 0 : downloadParts.hashCode());
            return result;
        }

        private void assign(DownloadCheckPoint dcp) {
            this.magic = dcp.magic;
            this.md5 = dcp.md5;
            this.downloadFile = dcp.downloadFile;
            this.bucketName = dcp.bucketName;
            this.objectKey = dcp.objectKey;
            this.objectStat = dcp.objectStat;
            this.downloadParts = dcp.downloadParts;
        }

        public String magic; // magic
        public int md5; // the md5 of checkpoint data.
        public String downloadFile; // local path for the download.
        public String bucketName; // bucket name
        public String objectKey; // object key
        public ObjectStat objectStat; // object state
        public ArrayList<DownloadPart> downloadParts; // download parts list.

    }

    static class ObjectStat implements Serializable {

        private static final long serialVersionUID = -2883494783412999919L;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((digest == null) ? 0 : digest.hashCode());
            result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
            result = prime * result + (int) (size ^ (size >>> 32));
            return result;
        }

        public static ObjectStat getFileStat(SwiftObjectOperation objectOperation, DownloadFileRequest downloadFileRequest) {
            String bucketName = downloadFileRequest.getBucketName();
            String key = downloadFileRequest.getKey();

            GenericRequest genericRequest = new GenericRequest(bucketName, key);

            SimplifiedObjectMeta meta = objectOperation.getSimplifiedObjectMeta(genericRequest);

            ObjectStat objStat = new ObjectStat();
            objStat.size = meta.getSize();
            objStat.lastModified = meta.getLastModified();
            objStat.digest = meta.getETag();

            return objStat;
        }

        public long size; // file size
        public Date lastModified; // file's last modified time.
        public String digest; // The file's ETag.
    }

    static class DownloadPart implements Serializable {

        private static final long serialVersionUID = -3655925846487976207L;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + index;
            result = prime * result + (isCompleted ? 1231 : 1237);
            result = prime * result + (int) (end ^ (end >>> 32));
            result = prime * result + (int) (start ^ (start >>> 32));
            result = prime * result + (int) (crc ^ (crc >>> 32));
            result = prime * result + (int) (fileStart ^ (fileStart >>> 32));
            return result;
        }

        public int index; // part index (starting from 0).
        public long start; // start index;
        public long end; // end index;
        public boolean isCompleted; // flag of part download finished or not;
        public long length; // length of part
        public long crc; // part crc.
        public long fileStart;  // start index in file, for range get
    }

    static class PartResult {

        public PartResult(int number, long start, long end) {
            this.number = number;
            this.start = start;
            this.end = end;
        }

        public PartResult(int number, long start, long end, long length, long clientCRC) {
            this.number = number;
            this.start = start;
            this.end = end;
            this.length = length;
            this.clientCRC = clientCRC;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        public int getNumber() {
            return number;
        }

        public boolean isFailed() {
            return failed;
        }

        public void setFailed(boolean failed) {
            this.failed = failed;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }

        public Long getClientCRC() {
            return clientCRC;
        }

        public void setClientCRC(Long clientCRC) {
            this.clientCRC = clientCRC;
        }

        public Long getServerCRC() {
            return serverCRC;
        }

        public void setServerCRC(Long serverCRC) {
            this.serverCRC = serverCRC;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        private int number; // part number, starting from 1.
        private long start; // start index in the part.
        private long end; // end index in the part.
        private boolean failed; // flag of part upload failure.
        private Exception exception; // Exception during part upload.
        private Long clientCRC; // client crc of this part
        private Long serverCRC; // server crc of this file

        private long length;
    }

    static class DownloadResult {

        public List<PartResult> getPartResults() {
            return partResults;
        }

        public void setPartResults(List<PartResult> partResults) {
            this.partResults = partResults;
        }

        public ObjectMetadata getObjectMetadata() {
            return objectMetadata;
        }

        public void setObjectMetadata(ObjectMetadata objectMetadata) {
            this.objectMetadata = objectMetadata;
        }

        private List<PartResult> partResults;
        private ObjectMetadata objectMetadata;
    }

    public SwiftDownloadOperation(SwiftObjectOperation objectOperation) {
        this.objectOperation = objectOperation;
    }

    public DownloadFileResult downloadObject(DownloadFileRequest downloadFileRequest) throws Throwable {
        assertParameterNotNull(downloadFileRequest, "downloadFileRequest");

        String bucketName = downloadFileRequest.getBucketName();
        String key = downloadFileRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameCreationValid(bucketName);
        ensureObjectKeyValid(key);

        // the download file name is not specified, using the key as the local
        // file name.
        if (downloadFileRequest.getDownloadFile() == null) {
            downloadFileRequest.setDownloadFile(downloadFileRequest.getKey());
        }

        // the checkpoint is enabled, but no checkpoint file, using the default
        // checkpoint file name.
        /**
         * 如果开启断点下载的话，首先检查断点文件是否存在，如果不存在就赋予属性值
         */
        if (downloadFileRequest.isEnableCheckpoint()) {
            if (downloadFileRequest.getCheckpointFile() == null || downloadFileRequest.getCheckpointFile().isEmpty()) {
                downloadFileRequest.setCheckpointFile(downloadFileRequest.getDownloadFile() + ".dcp");
            }
        }

        return downloadFileWithCheckpoint(downloadFileRequest);
    }

    private DownloadFileResult downloadFileWithCheckpoint(DownloadFileRequest downloadFileRequest) throws Throwable {
        DownloadFileResult downloadFileResult = new DownloadFileResult();
        DownloadCheckPoint downloadCheckPoint = new DownloadCheckPoint();

        // The checkpoint is enabled, downloads the parts download results from
        // checkpoint file.
        if (downloadFileRequest.isEnableCheckpoint()) {
            // read the last download result. If checkpoint file dosx not exist,
            // or the file is updated/corrupted,
            // re-download again.
            try {
                downloadCheckPoint.load(downloadFileRequest.getCheckpointFile());
            } catch (Exception e) {
                remove(downloadFileRequest.getCheckpointFile());
            }

            // The download checkpoint is corrupted, download again.
            if (!downloadCheckPoint.isValid(objectOperation, downloadFileRequest)) {
                prepare(downloadCheckPoint, downloadFileRequest);
                remove(downloadFileRequest.getCheckpointFile());
            }
        } else {
            // The checkpoint is not enabled, download the file again.
            prepare(downloadCheckPoint, downloadFileRequest);
        }

        // Progress listen starts tracking the progress.
        ProgressListener listener = downloadFileRequest.getProgressListener();
        ProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);

        // Concurrently download parts.
        DownloadResult downloadResult = download(downloadCheckPoint, downloadFileRequest);
        Long serverCRC = null;
        for (PartResult partResult : downloadResult.getPartResults()) {
            if (partResult.getServerCRC() != null) {
                serverCRC = partResult.getServerCRC();
            }
            if (partResult.isFailed()) {
                ProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_PART_FAILED_EVENT);
                throw partResult.getException();
            }
        }

        // Publish the complete status.
        ProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_COMPLETED_EVENT);

        // rename the temp file.
        renameTo(downloadFileRequest.getTempDownloadFile(), downloadFileRequest.getDownloadFile());

        // The checkpoint is enabled, delete the checkpoint file after a
        // successful download.
        if (downloadFileRequest.isEnableCheckpoint()) {
            remove(downloadFileRequest.getCheckpointFile());
        }

        downloadFileResult.setObjectMetadata(downloadResult.getObjectMetadata());
        return downloadFileResult;
    }

    private void prepare(DownloadCheckPoint downloadCheckPoint, DownloadFileRequest downloadFileRequest)
            throws IOException {
        downloadCheckPoint.magic = DownloadCheckPoint.DOWNLOAD_MAGIC;
        downloadCheckPoint.downloadFile = downloadFileRequest.getDownloadFile();
        downloadCheckPoint.bucketName = downloadFileRequest.getBucketName();
        downloadCheckPoint.objectKey = downloadFileRequest.getKey();
        downloadCheckPoint.objectStat = ObjectStat.getFileStat(objectOperation, downloadFileRequest);
        long downloadSize;
        if (downloadCheckPoint.objectStat.size > 0) {
            long[] slice = getSlice(downloadFileRequest.getRange(), downloadCheckPoint.objectStat.size);
            downloadCheckPoint.downloadParts = splitFile(slice[0], slice[1], downloadFileRequest.getPartSize());
            downloadSize = slice[1];
        } else {
            //download whole file
            downloadSize = 0;
            downloadCheckPoint.downloadParts = splitOneFile();
        }
        createFixedFile(downloadFileRequest.getTempDownloadFile(), downloadSize);
    }

    public static void createFixedFile(String filePath, long length) throws IOException {
        File file = new File(filePath);
        RandomAccessFile rf = null;

        try {
            rf = new RandomAccessFile(file, "rw");
            rf.setLength(length);
        } finally {
            if (rf != null) {
                rf.close();
            }
        }
    }

    private DownloadResult download(DownloadCheckPoint downloadCheckPoint, DownloadFileRequest downloadFileRequest)
            throws Throwable {
        DownloadResult downloadResult = new DownloadResult();
        ArrayList<PartResult> taskResults = new ArrayList<PartResult>();
        ExecutorService service = Executors.newFixedThreadPool(downloadFileRequest.getTaskNum());
        ArrayList<Future<PartResult>> futures = new ArrayList<Future<PartResult>>();
        List<Task> tasks = new ArrayList<Task>();
        ProgressListener listener = downloadFileRequest.getProgressListener();

        // Compute the size of data pending download.
        long completedLength = 0;
        long contentLength = 0;
        for (int i = 0; i < downloadCheckPoint.downloadParts.size(); i++) {
            long partSize = downloadCheckPoint.downloadParts.get(i).end -
                    downloadCheckPoint.downloadParts.get(i).start + 1;
            contentLength += partSize;
            if (downloadCheckPoint.downloadParts.get(i).isCompleted) {
                completedLength += partSize;
            }
        }

        ProgressPublisher.publishResponseContentLength(listener, contentLength);
        ProgressPublisher.publishResponseBytesTransferred(listener, completedLength);
        downloadFileRequest.setProgressListener(null);

        // Concurrently download parts.
        for (int i = 0; i < downloadCheckPoint.downloadParts.size(); i++) {
            if (!downloadCheckPoint.downloadParts.get(i).isCompleted) {
                Task task = new Task(i, "download-" + i, downloadCheckPoint, i, downloadFileRequest, objectOperation,
                        listener);
                futures.add(service.submit(task));
                tasks.add(task);
            } else {
                taskResults.add(new PartResult(i + 1, downloadCheckPoint.downloadParts.get(i).start,
                        downloadCheckPoint.downloadParts.get(i).end, downloadCheckPoint.downloadParts.get(i).length,
                        downloadCheckPoint.downloadParts.get(i).crc));
            }
        }
        service.shutdown();

        // Waiting for all parts download,
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        for (Future<PartResult> future : futures) {
            try {
                PartResult tr = future.get();
                taskResults.add(tr);
            } catch (ExecutionException e) {
                downloadFileRequest.setProgressListener(listener);
                throw e.getCause();
            }
        }

        // Sorts the download result by the part number.
        Collections.sort(taskResults, new Comparator<PartResult>() {
            @Override
            public int compare(PartResult p1, PartResult p2) {
                return p1.getNumber() - p2.getNumber();
            }
        });

        // sets the return value.
        downloadResult.setPartResults(taskResults);
        if (tasks.size() > 0) {
            downloadResult.setObjectMetadata(tasks.get(0).GetobjectMetadata());
        }
        downloadFileRequest.setProgressListener(listener);

        return downloadResult;
    }

    class Task implements Callable<PartResult> {

        public Task(int id, String name, DownloadCheckPoint downloadCheckPoint, int partIndex,
                    DownloadFileRequest downloadFileRequest, SwiftObjectOperation objectOperation,
                    ProgressListener progressListener) {
            this.id = id;
            this.name = name;
            this.downloadCheckPoint = downloadCheckPoint;
            this.partIndex = partIndex;
            this.downloadFileRequest = downloadFileRequest;
            this.objectOperation = objectOperation;
            this.progressListener = progressListener;
        }

        @Override
        public PartResult call() throws Exception {
            PartResult tr = null;
            RandomAccessFile output = null;
            InputStream content = null;

            try {
                DownloadPart downloadPart = downloadCheckPoint.downloadParts.get(partIndex);
                tr = new PartResult(partIndex + 1, downloadPart.start, downloadPart.end);

                output = new RandomAccessFile(downloadFileRequest.getTempDownloadFile(), "rw");
                output.seek(downloadPart.fileStart);

                GetObjectRequest getObjectRequest = new GetObjectRequest(downloadFileRequest.getBucketName(),
                        downloadFileRequest.getKey());
                getObjectRequest.setRange(downloadPart.start, downloadPart.end);

                SwiftObject obj = getObjectWrap(getObjectRequest);
                objectMetadata = obj.getMetadata();
                content = obj.getObjectContent();

                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int bytesRead = 0;
                while ((bytesRead = IOUtils.readNBytes(content, buffer, 0, buffer.length)) > 0) {
                    output.write(buffer, 0, bytesRead);
                }

                if (objectOperation.getInnerClient().getClientConfiguration().isCrcCheckEnabled()) {
                    Long clientCRC = getInputStreamCRCWrap(content);
                    tr.setClientCRC(clientCRC);
                    tr.setServerCRC(objectMetadata.getServerCRC());
                    tr.setLength(objectMetadata.getContentLength());
                    downloadPart.length = objectMetadata.getContentLength();
                    downloadPart.crc = clientCRC;
                }
                downloadCheckPoint.update(partIndex, true);
                if (downloadFileRequest.isEnableCheckpoint()) {
                    downloadCheckPoint.dump(downloadFileRequest.getCheckpointFile());
                }
                ProgressPublisher.publishResponseBytesTransferred(progressListener,
                        (downloadPart.end - downloadPart.start + 1));
            } catch (Exception e) {
                tr.setFailed(true);
                tr.setException(e);
                logException(String.format("Task %d:%s upload part %d failed: ", id, name, partIndex), e);
            } finally {
                if (output != null) {
                    output.close();
                }

                if (content != null) {
                    content.close();
                }
            }

            return tr;
        }

        public ObjectMetadata GetobjectMetadata() {
            return objectMetadata;
        }

        private int id;
        private String name;
        private DownloadCheckPoint downloadCheckPoint;
        private int partIndex;
        private DownloadFileRequest downloadFileRequest;
        private SwiftObjectOperation objectOperation;
        private ObjectMetadata objectMetadata;
        private ProgressListener progressListener;
    }

    private ArrayList<DownloadPart> splitFile(long start, long objectSize, long partSize) {
        ArrayList<DownloadPart> parts = new ArrayList<DownloadPart>();

        long partNum = objectSize / partSize;
        long alignSize = 4 * KB;
        if (partNum >= 10000) {
            partSize = objectSize / (10000 - 1);
            partSize = (((partSize + alignSize - 1) / alignSize) * alignSize);
        }

        long offset = 0L;
        for (int i = 0; offset < objectSize; offset += partSize, i++) {
            DownloadPart part = new DownloadPart();
            part.index = i;
            part.start = offset + start;
            part.end = getPartEnd(offset, objectSize, partSize) + start;
            part.fileStart = offset;
            parts.add(part);
        }

        return parts;
    }

    private long getPartEnd(long begin, long total, long per) {
        if (begin + per > total) {
            return total - 1;
        }
        return begin + per - 1;
    }

    private ArrayList<DownloadPart> splitOneFile() {
        ArrayList<DownloadPart> parts = new ArrayList<DownloadPart>();
        DownloadPart part = new DownloadPart();
        part.index = 0;
        part.start = 0;
        part.end = -1;
        part.fileStart = 0;
        parts.add(part);
        return parts;
    }

    private long[] getSlice(long[] range, long totalSize) {
        long start = 0;
        long size = totalSize;

        if ((range == null) ||
                (range.length != 2) ||
                (totalSize < 1) ||
                (range[0] < 0 && range[1] < 0) ||
                (range[0] > 0 && range[1] > 0 && range[0] > range[1]) ||
                (range[0] >= totalSize)) {
            //download all
        } else {
            //dwonload part by range & total size
            long begin = range[0];
            long end = range[1];
            if (range[0] < 0) {
                begin = 0;
            }
            if (range[1] < 0 || range[1] >= totalSize) {
                end = totalSize - 1;
            }
            start = begin;
            size = end - begin + 1;
        }

        return new long[]{start, size};
    }

    private boolean remove(String filePath) {
        boolean flag = false;
        File file = new File(filePath);

        if (file.isFile() && file.exists()) {
            flag = file.delete();
        }

        return flag;
    }

    private static void renameTo(String srcFilePath, String destFilePath) throws IOException {
        File srcfile = new File(srcFilePath);
        File destfile = new File(destFilePath);
        moveFile(srcfile, destfile);
    }

    private static void moveFile(final File srcFile, final File destFile) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' is a directory");
        }
        if (destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' is a directory");
        }
        if (destFile.exists()) {
            if (!destFile.delete()) {
                throw new IOException("Failed to delete original file '" + srcFile + "'");
            }
        }

        final boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile, destFile);
            if (!srcFile.delete()) {
                throw new IOException(
                        "Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
            }
        }
    }

    private static void copyFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    private SwiftObjectOperation objectOperation;
}

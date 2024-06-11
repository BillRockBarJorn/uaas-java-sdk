package com.heredata.hos.operation;

import com.heredata.event.ProgressEventType;
import com.heredata.event.ProgressListener;
import com.heredata.event.ProgressPublisher;
import com.heredata.exception.ClientException;
import com.heredata.exception.InconsistentException;
import com.heredata.exception.ServiceException;
import com.heredata.hos.comm.HOSHeaders;
import com.heredata.hos.internal.Mimetypes;
import com.heredata.hos.model.*;
import com.heredata.hos.utils.HOSUtils;
import com.heredata.utils.CRC64;
import com.heredata.utils.IOUtils;
import lombok.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

import static com.heredata.hos.utils.HOSUtils.ensureBucketNameValid;
import static com.heredata.hos.utils.HOSUtils.ensureObjectKeyValid;
import static com.heredata.utils.CodingUtils.assertParameterNotNull;
import static com.heredata.utils.LogUtils.logException;

/**
 * <p>Title: HOSUploadOperation</p>
 * <p>Description: hos上传操作类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:13
 */
public class HOSUploadOperation {

    protected UploadCheckPoint createUploadCheckPointWrap() throws IOException {
        return new UploadCheckPoint();
    }

    protected void loadUploadCheckPointWrap(UploadCheckPoint uploadCheckPoint, String checkpointFile) throws Throwable {
        uploadCheckPoint.load(checkpointFile);
    }

    protected InitiateMultipartUploadResult initiateMultipartUploadWrap(UploadCheckPoint uploadCheckPoint,
                                                                        InitiateMultipartUploadRequest initiateMultipartUploadRequest) throws ServiceException, ClientException {
        return multipartOperation.initiateMultipartUpload(initiateMultipartUploadRequest);
    }

    protected UploadPartResult uploadPartWrap(UploadCheckPoint uploadCheckPoint, UploadPartRequest request) throws ServiceException, ClientException {
        return multipartOperation.uploadPart(request);
    }

    protected CompleteMultipartUploadResult completeMultipartUploadWrap(UploadCheckPoint uploadCheckPoint, CompleteMultipartUploadRequest request)
            throws ServiceException, ClientException {
        return multipartOperation.completeMultipartUpload(request);
    }

    /**
     * 修复漏洞3.1.1.1    漏洞来源代码扫描报告-cmstoreos-sdk-java-1215-0b57751a.pdf
     * 断点续传的实体类，包含断点续传的所有属性信息用来完成此功能
     */
    static class UploadCheckPoint extends ObjectInputStream implements Serializable {

        private static final long serialVersionUID = 5424904565837227164L;

        private static final String UPLOAD_MAGIC = "FE8BB4EA-B593-4FAC-AD7A-2459A36E2E62";

        protected UploadCheckPoint() throws IOException, SecurityException {
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
            if (!osc.getName().equals(UploadCheckPoint.class.getName())) {
                throw new InvalidClassException("Unauthorized deserialization", osc.getName());
            }
            return super.resolveClass(osc);
        }

        /**
         * 修复漏洞3.2.2.1  资源没有安全释放
         * Gets the checkpoint data from the checkpoint file.
         */
        public synchronized void load(String cpFile) throws IOException, ClassNotFoundException {
            FileInputStream fileIn = null;
            ObjectInputStream in = null;
            try {
                fileIn = new FileInputStream(cpFile);
                in = new ObjectInputStream(fileIn);
                UploadCheckPoint ucp = (UploadCheckPoint) in.readObject();
                assign(ucp);
            } finally {
                IOUtils.safeCloseStream(fileIn);
                IOUtils.safeCloseStream(in);
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
         * The part upload complete, update the status.
         *
         * @throws IOException
         */
        public synchronized void update(int partIndex, PartETag partETag, boolean completed) throws IOException {
            partETags.add(partETag);
            uploadParts.get(partIndex).isCompleted = completed;
        }

        /**
         * Check if the local file matches the checkpoint.
         */
        public synchronized boolean isValid(String uploadFile) {
            // 比较checkpoint的magic和md5
            // Compares the magic field in checkpoint and the file's md5.
            if (this.magic == null || !this.magic.equals(UPLOAD_MAGIC) || this.md5 != hashCode()) {
                return false;
            }

            // Checks if the file exists.
            File upload = new File(uploadFile);
            if (!upload.exists()) {
                return false;
            }

            // The file name, size and last modified time must be same as the
            // checkpoint.
            // If any item is changed, return false (re-upload the file).
            if (!this.uploadFile.equals(uploadFile) || this.uploadFileStat.size != upload.length()
                    || this.uploadFileStat.lastModified != upload.lastModified()) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((magic == null) ? 0 : magic.hashCode());
            result = prime * result + ((partETags == null) ? 0 : partETags.hashCode());
            result = prime * result + ((uploadFile == null) ? 0 : uploadFile.hashCode());
            result = prime * result + ((uploadFileStat == null) ? 0 : uploadFileStat.hashCode());
            result = prime * result + ((uploadID == null) ? 0 : uploadID.hashCode());
            result = prime * result + ((uploadParts == null) ? 0 : uploadParts.hashCode());
            result = prime * result + (int) originPartSize;
            return result;
        }

        public void assign(UploadCheckPoint ucp) {
            this.magic = ucp.magic;
            this.md5 = ucp.md5;
            this.uploadFile = ucp.uploadFile;
            this.uploadFileStat = ucp.uploadFileStat;
            this.key = ucp.key;
            this.uploadID = ucp.uploadID;
            this.uploadParts = ucp.uploadParts;
            this.partETags = ucp.partETags;
            this.originPartSize = ucp.originPartSize;
        }

        public String magic;
        public int md5;
        public String uploadFile;
        public FileStat uploadFileStat;
        public String key;
        public String uploadID;
        public ArrayList<UploadPart> uploadParts;
        public ArrayList<PartETag> partETags;
        public long originPartSize;
    }

    static class FileStat implements Serializable {
        private static final long serialVersionUID = -1223810339796425415L;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((digest == null) ? 0 : digest.hashCode());
            result = prime * result + (int) (lastModified ^ (lastModified >>> 32));
            result = prime * result + (int) (size ^ (size >>> 32));
            return result;
        }

        public static FileStat getFileStat(String uploadFile) {
            FileStat fileStat = new FileStat();
            File file = new File(uploadFile);
            fileStat.size = file.length();
            fileStat.lastModified = file.lastModified();
            return fileStat;
        }

        public long size; // file size
        public long lastModified; // file last modified time.
        public String digest; // file content's digest (signature).
    }

    static class UploadPart implements Serializable {
        private static final long serialVersionUID = 6692863980224332199L;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (isCompleted ? 1231 : 1237);
            result = prime * result + number;
            result = prime * result + (int) (offset ^ (offset >>> 32));
            result = prime * result + (int) (size ^ (size >>> 32));
            result = prime * result + (int) (crc ^ (crc >>> 32));
            return result;
        }

        public int number; // part number
        public long offset; // the offset in the file
        public long size; // part size
        public boolean isCompleted; // upload completeness flag.
        public long crc; //part crc
    }

    @Data
    static class PartResult {

        public PartResult(int number, long offset, long length) {
            this.number = number;
            this.offset = offset;
            this.length = length;
        }

        public PartResult(int number, long offset, long length, long partCRC) {
            this.number = number;
            this.offset = offset;
            this.length = length;
            this.partCRC = partCRC;
        }

        private int number; // part number
        private long offset; // offset in the file
        private long length; // part size
        private boolean failed; // part upload failure flag
        private Exception exception; // part upload exception
        private Long partCRC;
    }

    public HOSUploadOperation(HOSMultipartOperation multipartOperation) {
        this.multipartOperation = multipartOperation;
    }

    public CompleteMultipartUploadResult uploadFile(UploadObjectRequest uploadObjectRequest) throws Throwable {
        assertParameterNotNull(uploadObjectRequest, "uploadFileRequest");

        String bucketName = uploadObjectRequest.getBucketName();
        String key = uploadObjectRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);

        assertParameterNotNull(uploadObjectRequest.getUploadFile(), "uploadFile");

        // The checkpoint is enabled without specifying the checkpoint file,
        // using the default one.
        if (uploadObjectRequest.isEnableCheckpoint()) {
            if (uploadObjectRequest.getCheckpointFile() == null || uploadObjectRequest.getCheckpointFile().isEmpty()) {
                uploadObjectRequest.setCheckpointFile(uploadObjectRequest.getUploadFile() + ".ucp");
            }
        }

        return uploadFileWithCheckpoint(uploadObjectRequest);
    }

    private CompleteMultipartUploadResult uploadFileWithCheckpoint(UploadObjectRequest uploadObjectRequest) throws Throwable {
        UploadCheckPoint uploadCheckPoint = createUploadCheckPointWrap();

        // The checkpoint is enabled, reading the checkpoint data from the
        // checkpoint file.
        if (uploadObjectRequest.isEnableCheckpoint()) {
            // The checkpoint file either does not exist, or is corrupted, the
            // whole file needs the re-upload.
            try {
                loadUploadCheckPointWrap(uploadCheckPoint, uploadObjectRequest.getCheckpointFile());
            } catch (Exception e) {
                remove(uploadObjectRequest.getCheckpointFile());
            }

            // The file uploaded is updated, re-upload.
            if (!uploadCheckPoint.isValid(uploadObjectRequest.getUploadFile())) {
                prepare(uploadCheckPoint, uploadObjectRequest);
                remove(uploadObjectRequest.getCheckpointFile());
            }
        } else {
            // The checkpoint is not enabled, re-upload.
            prepare(uploadCheckPoint, uploadObjectRequest);
        }

        // The progress tracker starts
        ProgressListener listener = uploadObjectRequest.getProgressListener();
        ProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);

        // Concurrently upload parts.
        List<PartResult> partResults = upload(uploadCheckPoint, uploadObjectRequest);
        for (PartResult partResult : partResults) {
            if (partResult.isFailed()) {
                ProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_PART_FAILED_EVENT);
                throw partResult.getException();
            }
        }

        // The progress tracker publishes the data.
        ProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_COMPLETED_EVENT);

        // Complete parts.
        CompleteMultipartUploadResult multipartUploadResult = complete(uploadCheckPoint, uploadObjectRequest);

        // check crc64
        if (multipartOperation.getInnerClient().getClientConfiguration().isCrcCheckEnabled()) {
            Long clientCRC = calcObjectCRCFromParts(partResults);
            multipartUploadResult.setClientCRC(clientCRC);
            try {
                HOSUtils.checkChecksum(clientCRC, multipartUploadResult.getServerCRC(), multipartUploadResult.getRequestId());
            } catch (Exception e) {
                ProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
                throw new InconsistentException(clientCRC, multipartUploadResult.getServerCRC(), multipartUploadResult.getRequestId());
            }
        }

        // The checkpoint is enabled and upload the checkpoint file.
        if (uploadObjectRequest.isEnableCheckpoint()) {
            remove(uploadObjectRequest.getCheckpointFile());
        }

        return multipartUploadResult;
    }

    private static Long calcObjectCRCFromParts(List<PartResult> partResults) {
        long crc = 0;

        for (PartResult partResult : partResults) {
            if (partResult.getPartCRC() == null || partResult.getLength() <= 0) {
                return null;
            }
            crc = CRC64.combine(crc, partResult.getPartCRC(), partResult.getLength());
        }
        return new Long(crc);
    }

    private void prepare(UploadCheckPoint uploadCheckPoint, UploadObjectRequest uploadObjectRequest) {
        uploadCheckPoint.magic = UploadCheckPoint.UPLOAD_MAGIC;
        uploadCheckPoint.uploadFile = uploadObjectRequest.getUploadFile();
        uploadCheckPoint.key = uploadObjectRequest.getKey();
        uploadCheckPoint.uploadFileStat = FileStat.getFileStat(uploadCheckPoint.uploadFile);
        uploadCheckPoint.uploadParts = splitFile(uploadCheckPoint.uploadFileStat.size, uploadObjectRequest.getPartSize());
        uploadCheckPoint.partETags = new ArrayList<>();
        uploadCheckPoint.originPartSize = uploadObjectRequest.getPartSize();

        ObjectMetadata metadata = uploadObjectRequest.getObjectMetadata();
        if (metadata == null) {
            metadata = new ObjectMetadata();
        }

        if (metadata.getContentType() == null) {
            metadata.setContentType(
                    Mimetypes.getInstance().getMimetype(uploadCheckPoint.uploadFile, uploadCheckPoint.key));
        }

        InitiateMultipartUploadRequest initiateUploadRequest = new InitiateMultipartUploadRequest(
                uploadObjectRequest.getBucketName(), uploadObjectRequest.getKey(), metadata);

        InitiateMultipartUploadResult initiateUploadResult = initiateMultipartUploadWrap(uploadCheckPoint, initiateUploadRequest);
        uploadCheckPoint.uploadID = initiateUploadResult.getUploadId();
    }

    private ArrayList<PartResult> upload(UploadCheckPoint uploadCheckPoint, UploadObjectRequest uploadObjectRequest)
            throws Throwable {
        ArrayList<PartResult> taskResults = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(uploadObjectRequest.getTaskNum());
        ArrayList<Future<PartResult>> futures = new ArrayList<>();
        ProgressListener listener = uploadObjectRequest.getProgressListener();

        // Compute the size of the data pending upload.
        long contentLength = 0;
        long completedLength = 0;
        for (int i = 0; i < uploadCheckPoint.uploadParts.size(); i++) {
            long partSize = uploadCheckPoint.uploadParts.get(i).size;
            contentLength += partSize;
            if (uploadCheckPoint.uploadParts.get(i).isCompleted) {
                completedLength += partSize;
            }
        }

        ProgressPublisher.publishRequestContentLength(listener, contentLength);
        ProgressPublisher.publishRequestBytesTransferred(listener, completedLength);
        uploadObjectRequest.setProgressListener(null);

        // Upload parts.
        for (int i = 0; i < uploadCheckPoint.uploadParts.size(); i++) {
            if (!uploadCheckPoint.uploadParts.get(i).isCompleted) {
                futures.add(service.submit(new Task(i, "upload-" + i, uploadCheckPoint, i, uploadObjectRequest,
                        multipartOperation, listener)));
            } else {
                taskResults.add(new PartResult(i + 1, uploadCheckPoint.uploadParts.get(i).offset,
                        uploadCheckPoint.uploadParts.get(i).size, uploadCheckPoint.uploadParts.get(i).crc));
            }
        }
        service.shutdown();

        // Waiting for parts upload complete.
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        for (Future<PartResult> future : futures) {
            try {
                PartResult tr = future.get();
                taskResults.add(tr);
            } catch (ExecutionException e) {
                uploadObjectRequest.setProgressListener(listener);
                throw e.getCause();
            }
        }

        // Sorts PartResult by the part numnber.
        Collections.sort(taskResults, new Comparator<PartResult>() {
            @Override
            public int compare(PartResult p1, PartResult p2) {
                return p1.getNumber() - p2.getNumber();
            }
        });
        uploadObjectRequest.setProgressListener(listener);

        return taskResults;
    }

    class Task implements Callable<PartResult> {

        public Task(int id, String name, UploadCheckPoint uploadCheckPoint, int partIndex,
                    UploadObjectRequest uploadObjectRequest, HOSMultipartOperation multipartOperation,
                    ProgressListener progressListener) {
            this.id = id;
            this.name = name;
            this.uploadCheckPoint = uploadCheckPoint;
            this.partIndex = partIndex;
            this.uploadObjectRequest = uploadObjectRequest;
            this.multipartOperation = multipartOperation;
            this.progressListener = progressListener;
        }

        @Override
        public PartResult call() throws Exception {
            PartResult tr = null;
            InputStream instream = null;

            try {
                UploadPart uploadPart = uploadCheckPoint.uploadParts.get(partIndex);
                tr = new PartResult(partIndex + 1, uploadPart.offset, uploadPart.size);

                instream = new FileInputStream(uploadCheckPoint.uploadFile);
                instream.skip(uploadPart.offset);

                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(uploadObjectRequest.getBucketName());
                uploadPartRequest.setKey(uploadObjectRequest.getKey());
                uploadPartRequest.setUploadId(uploadCheckPoint.uploadID);
                uploadPartRequest.setPartNumber(uploadPart.number);
                uploadPartRequest.setInputStream(instream);
                uploadPartRequest.setPartSize(uploadPart.size);

                UploadPartResult uploadPartResult = uploadPartWrap(uploadCheckPoint, uploadPartRequest);

                if (multipartOperation.getInnerClient().getClientConfiguration().isCrcCheckEnabled()) {
                    HOSUtils.checkChecksum(uploadPartResult.getClientCRC(), uploadPartResult.getServerCRC(), uploadPartResult.getRequestId());
                    tr.setPartCRC(uploadPartResult.getClientCRC());
                    tr.setLength(uploadPartResult.getPartSize());
                    uploadPart.crc = uploadPartResult.getClientCRC();
                }
                PartETag partETag = new PartETag(uploadPartResult.getPartNumber(), uploadPartResult.getETag());
                uploadCheckPoint.update(partIndex, partETag, true);
                if (uploadObjectRequest.isEnableCheckpoint()) {
                    uploadCheckPoint.dump(uploadObjectRequest.getCheckpointFile());
                }
                ProgressPublisher.publishRequestBytesTransferred(progressListener, uploadPart.size);
            } catch (Exception e) {
                tr.setFailed(true);
                tr.setException(e);
                logException(String.format("Task %d:%s upload part %d failed: ", id, name, partIndex + 1), e);
            } finally {
                IOUtils.safeCloseStream(instream);
            }

            return tr;
        }

        private int id;
        private String name;
        private UploadCheckPoint uploadCheckPoint;
        private int partIndex;
        private UploadObjectRequest uploadObjectRequest;
        private HOSMultipartOperation multipartOperation;
        private ProgressListener progressListener;
    }

    private CompleteMultipartUploadResult complete(UploadCheckPoint uploadCheckPoint,
                                                   UploadObjectRequest uploadObjectRequest) {
        Collections.sort(uploadCheckPoint.partETags, new Comparator<PartETag>() {
            @Override
            public int compare(PartETag p1, PartETag p2) {
                return p1.getPartNumber() - p2.getPartNumber();
            }
        });
        CompleteMultipartUploadRequest completeUploadRequest = new CompleteMultipartUploadRequest(
                uploadObjectRequest.getBucketName(), uploadObjectRequest.getKey(), uploadCheckPoint.uploadID,
                uploadCheckPoint.partETags);

        ObjectMetadata metadata = uploadObjectRequest.getObjectMetadata();
        if (metadata != null) {
            String acl = (String) metadata.getRawMetadata().get(HOSHeaders.HOS_OBJECT_ACL);
            if (acl != null && !acl.equals("")) {
//                CannedAccessControlList accessControlList = CannedAccessControlList.parse(acl);
//                completeUploadRequest.setObjectACL(accessControlList);
            }
        }

//        completeUploadRequest.setCallback(uploadFileRequest.getCallback());

        return completeMultipartUploadWrap(uploadCheckPoint, completeUploadRequest);
    }

    private ArrayList<UploadPart> splitFile(long fileSize, long partSize) {
        ArrayList<UploadPart> parts = new ArrayList<UploadPart>();

        long partNum = fileSize / partSize;
        if (partNum >= 10000) {
            partSize = fileSize / (10000 - 1);
            partNum = fileSize / partSize;
        }

        for (long i = 0; i < partNum; i++) {
            UploadPart part = new UploadPart();
            part.number = (int) (i + 1);
            part.offset = i * partSize;
            part.size = partSize;
            part.isCompleted = false;
            parts.add(part);
        }

        if (fileSize % partSize > 0) {
            UploadPart part = new UploadPart();
            part.number = parts.size() + 1;
            part.offset = parts.size() * partSize;
            part.size = fileSize % partSize;
            part.isCompleted = false;
            parts.add(part);
        }

        return parts;
    }

    private boolean remove(String filePath) {
        boolean flag = false;
        File file = new File(filePath);

        if (file.isFile() && file.exists()) {
            flag = file.delete();
        }

        return flag;
    }

    protected HOSMultipartOperation multipartOperation;
}

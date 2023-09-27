package com.heredata;

import com.heredata.hos.HOS;
import com.heredata.hos.HOSClientBuilder;
import com.heredata.hos.model.DownloadFileRequest;
import com.heredata.hos.model.KeyInformation;
import com.heredata.hos.model.ObjectListing;
import com.heredata.hos.model.UploadObjectRequest;
import com.heredata.hos.model.bucket.Bucket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * TODO
 * @author wuzz
 * @since 2023/5/19
 */
public class HosMain {

    public static void main(String[] args) throws Throwable {

//        args = new String[]{"put", "bucket", "2023/07/b.txt", "E:\\比洛巴乔\\Desktop\\b.txt"};
        args = new String[]{"list"};

//        String accountId = "1d4c0e02004b11ee9c2da7f517747f46";
//        String endpoint = "http://172.18.232.192:8089/v1";
//        String ak = "EHPWXMEHQHVYS44XBP2X";
//        String sk = "AovTa03S0gWeD5EhJUqf0WNMaNPNl31zagq8pPAg";

        KeyInformation keyInformation = new KeyInformation();
//        keyInformation.setEndPoint("http://172.18.232.37:8089/v1/");
        keyInformation.setEndPoint("http://172.18.232.37:8089/HOSv1/");
        try {
            keyInformation.setKeyInformation("test_user1", "TEST#ps@857"
                    , "test_pro1"
                    , "http://172.18.232.192:6020/v3/auth/tokens");
//            System.out.println("AccessKey=" + keyInformation.getAccessKey());
//            System.out.println("SecretKey=" + keyInformation.getSecretKey());
//            System.out.println("Token=" + keyInformation.getXSubjectToken());
//            keyInformation.setSecretId(StringUtils.getPointLengthUUID(32), new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24), "http://172.18.232.37:6069/v1/secrets");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HOS hos = new HOSClientBuilder().build(keyInformation.getEndPoint()
                , keyInformation.getAccessKey(), keyInformation.getSecretKey());

        Arrays.stream(args).forEach(System.out::println);

        if ("list".equals(args[0])) {
            String bucketName = "";
            String prefix = "";
            String startAfter = "";
            // 列表请求
            if (args.length >= 2 && args[1] != null && args[1].trim().length() != 0) {
                bucketName = args[1];
                ObjectListing objectListing = hos.listObjects(bucketName, args.length == 3 ? args[2] : "");

                objectListing.getObjectSummaries().forEach(System.out::println);
            } else {
                List<Bucket> buckets = hos.listBuckets();
                buckets.forEach(System.out::println);
            }
        } else if ("download".equals(args[0])) {
            String bucketName = args[1];
            String keyName = args[2];
            String localPath = args[3];
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, keyName);
            downloadFileRequest.setDownloadFile(localPath);
            hos.downloadObject(downloadFileRequest);
            System.out.println("下载成功");
        } else if ("put".equals(args[0])) {
            String bucketName = args[1];
            String keyName = args[2];
            String localPath = args[3];
            File file = new File(localPath);
            if (!file.exists()) throw new FileNotFoundException(localPath);
            if (file.length() < 1024 * 1024 * 64) {
                // 如果文件小于64MB，走直接上传
                hos.putObject(bucketName, keyName, file);
            } else {
                UploadObjectRequest uploadObjectRequest = new UploadObjectRequest(bucketName, keyName);
                hos.uploadFile(uploadObjectRequest);
            }
            System.out.println("上传成功");
        }
    }

}

package com.heredata.utils;

import com.heredata.comm.io.BoundedInputStream;
import com.heredata.comm.io.RepeatableBoundedFileInputStream;
import com.heredata.comm.io.RepeatableFileInputStream;
import lombok.NonNull;

import java.io.*;
import java.util.zip.CheckedInputStream;

import static com.heredata.comm.HttpConstants.DEFAULT_STREAM_BUFFER_SIZE;

/**
 * <p>Title: IOUtils</p>
 * <p>Description: io操作工具类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:36
 */
public class IOUtils {

    public static String readStreamAsString(InputStream in, String charset) throws IOException {
        if (in == null) {
            return "";
        }

        Reader reader = null;
        Writer writer = new StringWriter();
        String result;

        char[] buffer = new char[1024];
        try {
            int n = -1;
            reader = new BufferedReader(new InputStreamReader(in, charset));
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }

            result = writer.toString();
        } finally {
            in.close();
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        }

        return result;
    }

    public static byte[] readStreamAsByteArray(InputStream in) throws IOException {

        if (in == null) {
            return new byte[0];
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            output.write(buffer, 0, len);
        }
        output.flush();
        return output.toByteArray();
    }

    public static void safeClose(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public static void safeClose(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public static boolean checkFile(File file) {
        if (file == null) {
            return false;
        }

        boolean exists = false;
        boolean isFile = false;
        boolean canRead = false;
        try {
            exists = file.exists();
            isFile = file.isFile();
            canRead = file.canRead();
        } catch (SecurityException se) {
            // Swallow the exception and return false directly.
            return false;
        }

        return (exists && isFile && canRead);
    }

    public static InputStream newRepeatableInputStream(final InputStream original) throws IOException {
        InputStream repeatable = null;
        if (!original.markSupported()) {
            if (original instanceof FileInputStream) {
                repeatable = new RepeatableFileInputStream((FileInputStream) original);
            } else {
                repeatable = new BufferedInputStream(original, DEFAULT_STREAM_BUFFER_SIZE);
            }
        } else {
            repeatable = original;
        }
        return repeatable;
    }

    public static InputStream newRepeatableInputStream(final BoundedInputStream original) throws IOException {
        InputStream repeatable = null;
        if (!original.markSupported()) {
            if (original.getWrappedInputStream() instanceof FileInputStream) {
                repeatable = new RepeatableBoundedFileInputStream(original);
            } else {
                repeatable = new BufferedInputStream(original, DEFAULT_STREAM_BUFFER_SIZE);
            }
        } else {
            repeatable = original;
        }
        return repeatable;
    }

    public static Long getCRCValue(InputStream inputStream) {
        if (inputStream instanceof CheckedInputStream) {
            return ((CheckedInputStream) inputStream).getChecksum().getValue();
        }
        return null;
    }

    public static int readNBytes(InputStream inputStream, byte[] b, int off, int len) throws IOException {
        int n;
        int count;
        for (n = 0; n < len; n += count) {
            count = inputStream.read(b, off + n, len - n);
            if (count < 0) {
                break;
            }
        }

        return n;
    }

    /**
     * 修复漏洞3.2.2.1  资源没有安全释放  漏洞来源代码扫描报告-cmstoreos-sdk-java-1215-0b57751a.pdf
     */
    public static boolean writeOutFile(InputStream is, String destinationFile) throws IOException {
        File file = new File(destinationFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("创建" + destinationFile + "文件失败");
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            int len = -1;
            byte[] arr = new byte[1024];
            while ((len = is.read(arr)) != -1) {
                fileOutputStream.write(arr, 0, len);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            safeCloseStream(fileOutputStream);
            safeCloseStream(is);
        }
        return true;
    }


    public static void safeCloseStream(@NonNull final Closeable os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

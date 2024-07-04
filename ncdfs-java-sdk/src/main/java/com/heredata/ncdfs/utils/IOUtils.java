package com.heredata.ncdfs.utils;


import com.heredata.ncdfs.comm.io.RepeatableFileInputStream;
import com.heredata.ncdfs.internal.NCDFSConstants;

import java.io.*;
import java.util.zip.CheckedInputStream;

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
                repeatable = new BufferedInputStream(original, NCDFSConstants.DEFAULT_STREAM_BUFFER_SIZE);
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

    public static boolean writeOutFile(InputStream is, String destinationFile) throws IOException {
        File file = new File(destinationFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("创建" + destinationFile + "文件失败");
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        int len = -1;
        byte[] arr = new byte[1024];
        while ((len = is.read(arr)) != -1) {
            fileOutputStream.write(arr, 0, len);
        }
        fileOutputStream.close();
        is.close();
        return true;
    }

}

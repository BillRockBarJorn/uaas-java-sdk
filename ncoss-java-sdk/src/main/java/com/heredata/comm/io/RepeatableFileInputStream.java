package com.heredata.comm.io;

import com.heredata.exception.ClientException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import static com.heredata.utils.LogUtils.getLog;


public class RepeatableFileInputStream extends InputStream {

    private File file = null;
    private FileInputStream fis = null;
    private FileChannel fileChannel = null;
    private long markPos = 0;

    public RepeatableFileInputStream(File file) throws IOException {
        this(new FileInputStream(file), file);
    }

    public RepeatableFileInputStream(FileInputStream fis) throws IOException {
        this(fis, null);
    }

    public RepeatableFileInputStream(FileInputStream fis, File file) throws IOException {
        this.file = file;
        this.fis = fis;
        this.fileChannel = fis.getChannel();
        this.markPos = fileChannel.position();
    }

    @Override
    public void reset() throws IOException {
        fileChannel.position(markPos);
        getLog().trace("Reset to position " + markPos);
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readlimit) {
        try {
            markPos = fileChannel.position();
        } catch (IOException e) {
            throw new ClientException("Failed to mark file position", e);
        }
        getLog().trace("File input stream marked at position " + markPos);
    }

    @Override
    public int available() throws IOException {
        return fis.available();
    }

    @Override
    public void close() throws IOException {
        fis.close();
    }

    @Override
    public int read() throws IOException {
        return fis.read();
    }

    @Override
    public long skip(long n) throws IOException {
        return fis.skip(n);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return fis.read(b, off, len);
    }

    public InputStream getWrappedInputStream() {
        return this.fis;
    }

    public File getFile() {
        return this.file;
    }
}

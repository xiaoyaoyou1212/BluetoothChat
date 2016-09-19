package com.vise.basebluetooth.mode;

import java.io.File;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-19 15:10
 */
public class FileMessage extends BaseMessage {

    private File file;
    private String fileName;
    private long fileLength;

    public FileMessage() {
    }

    public File getFile() {
        return file;
    }

    public FileMessage setFile(File file) {
        this.file = file;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public FileMessage setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public long getFileLength() {
        return fileLength;
    }

    public FileMessage setFileLength(long fileLength) {
        this.fileLength = fileLength;
        return this;
    }
}

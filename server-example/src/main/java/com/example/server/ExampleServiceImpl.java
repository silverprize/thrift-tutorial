package com.example.server;

import com.example.ExampleService;
import com.example.UploadInfo;
import org.apache.thrift.TException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by silver on 14. 6. 2.
 */
public class ExampleServiceImpl implements ExampleService.Iface {

    private UploadFile uploadFile;

    @Override
    public String echo(String input) throws TException {
        try {
            return "from " + InetAddress.getLocalHost().getHostAddress() + " : " + input;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean upload(UploadInfo info) throws TException {
        switch (info.msg) {
            case BEGIN_UPLOAD:
                return createSkeleton(info);
            case PROGRESS_UPLOAD:
                return progressUpload(info);
            case END_UPLOAD:
                return endUpload();
            default:
                throw new TException();
        }
    }

    private boolean createSkeleton(UploadInfo info) {
        try {
            File dest = new File("downloads");
            if (!dest.exists()) {
                dest.mkdirs();
            }

            String name = info.fileName.substring(0, info.fileName.lastIndexOf('.'));
            String ext = info.fileName.substring(name.length() + 1);

            uploadFile = new UploadFile();
            uploadFile.file = File.createTempFile(name, ext, dest);
            uploadFile.raf = new RandomAccessFile(uploadFile.file, "rw");
            uploadFile.length = info.length;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean progressUpload(UploadInfo info) {
        try {
            uploadFile.raf.getChannel().write(info.data, uploadFile.raf.length());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean endUpload() {
        boolean ret = true;
        if (uploadFile.raf != null) {
            try {
                uploadFile.raf.close();
                if (uploadFile.file.length() != uploadFile.length) {
                    uploadFile.file.delete();
                    ret = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private static class UploadFile {
        private File file;

        private RandomAccessFile raf;

        private long length;
    }
}

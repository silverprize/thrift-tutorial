package com.example.server;

import com.example.ExampleService;
import com.example.TransferInfo;
import org.apache.thrift.TException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by silver on 14. 6. 2.
 */
public class ExampleServiceImpl implements ExampleService.Iface {

    private static final File DOWNLOADS_DIRECTORY = new File("downloads");

    private ByteBuffer ioBuffer = ByteBuffer.allocate(1024 * 10);

    private Context context;

    @Override
    public String echo(String input) throws TException {
        try {
            return "from " + InetAddress.getLocalHost().getHostAddress() + " : " + input;
        } catch (UnknownHostException e) {
            throw new TException(e);
        }
    }

    @Override
    public void upload(TransferInfo info) throws TException {
        switch (info.type) {
            case REQUEST:
                beginUpload(info);
                break;

            case PROGRESS:
                progressUpload(info);
                break;

            default:
                throw new TException();
        }
    }

    @Override
    public TransferInfo download(TransferInfo info) throws TException {
        switch (info.type) {
            case REQUEST:
                return beginDownload(info);

            case PROGRESS:
                return progressDownload();

            default:
                throw new TException();
        }
    }

    @Override
    public List<String> getFileList() throws TException {
        String[] arr = DOWNLOADS_DIRECTORY.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File each = new File(dir, name);
                return each.isFile() && !each.isHidden();
            }
        });
        ArrayList<String> list = new ArrayList<String>(arr.length);
        for (String file : arr) {
            list.add(file);
        }
        return list;
    }

    private void beginUpload(TransferInfo info) throws TException {
        try {
            if (!DOWNLOADS_DIRECTORY.exists()) {
                DOWNLOADS_DIRECTORY.mkdirs();
            }
            int dot = info.fileName.lastIndexOf('.');
            String ext = dot != -1 ? info.fileName.substring(dot, info.fileName.length()) : ".unknown";
            String name = dot != -1 ? info.fileName.substring(0, info.fileName.length() - ext.length()) : info.fileName;

            context = new Context();
            context.file = File.createTempFile(name, ext, DOWNLOADS_DIRECTORY);
            context.raf = new RandomAccessFile(context.file, "rw");
            context.length = info.length;
        } catch (IOException e) {
            try {
                context.raf.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw new TException(e);
        }
    }

    private void progressUpload(TransferInfo info) throws TException {
        try {
            context.raf.getChannel().write(info.data, context.raf.length());
            if (context.file.length() == context.length) {
                context.raf.close();
            }
        } catch (IOException e) {
            try {
                context.raf.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            context.file.delete();
            throw new TException(e);
        }
    }

    private TransferInfo beginDownload(TransferInfo reqInfo) throws TException {
        TransferInfo recvInfo = new TransferInfo();
        File src = new File(DOWNLOADS_DIRECTORY, reqInfo.fileName);
        if (src.exists()) {
            try {
                context = new Context();
                context.raf = new RandomAccessFile(src, "r");
                context.file = src;
                recvInfo.length = src.length();
                return recvInfo;
            } catch (FileNotFoundException e) {
                try {
                    context.raf.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                throw new TException(e);
            }
        }
        return recvInfo;
    }

    private TransferInfo progressDownload() throws TException {
        try {
            TransferInfo recvInfo = new TransferInfo();
            recvInfo.data = ioBuffer;
            ioBuffer.clear();
            recvInfo.length = context.raf.getChannel().read(ioBuffer);
            ioBuffer.flip();
            return recvInfo;
        } catch (IOException e) {
            try {
                context.raf.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw new TException(e);
        }
    }

    private static class Context {
        private File file;

        private RandomAccessFile raf;

        private long length;
    }
}

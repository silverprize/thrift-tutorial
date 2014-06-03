package com.example.client;

import com.example.ExampleService;
import com.example.UploadInfo;
import com.example.UploadMessage;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by silver on 14. 6. 2.
 */
public class ExampleClient {

    public static void main(String[] args) throws TException, IOException, URISyntaxException {
        if (args.length != 2) {
            throw new TException("invalid arguments");
        }

        String address = "localhost";
        int port = 10004;
        if (System.getProperty("address") != null) {
            address = System.getProperty("address");
        }
        if (Integer.getInteger("port") != null) {
            port = Integer.getInteger("port");
        }

        TTransport transport = new TSocket(address, port);
        TProtocol protocol = new TBinaryProtocol(transport);
        ExampleService.Client client = new ExampleService.Client(protocol);
        transport.open();

        if ("echo".equals(args[0])) {
            echo(client, args[1]);
        } else if ("upload".equals(args[0])) {
            upload(client, args[1]);
        }

        transport.close();
    }

    private static void echo(ExampleService.Client client, String msg) throws TException {
        String rev = client.echo(msg);
        System.out.println(rev);
    }

    private static void upload(ExampleService.Client client, String path) throws URISyntaxException, TException, IOException {
        File file = new File(path);
        UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.msg = UploadMessage.BEGIN_UPLOAD;
        uploadInfo.fileName = file.getName();
        uploadInfo.length = file.length();

        boolean success = true;
        if (client.upload(uploadInfo)) {
            FileInputStream fis = new FileInputStream(file);
            FileChannel fileChannel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 10);

            uploadInfo.msg = UploadMessage.PROGRESS_UPLOAD;
            uploadInfo.data = buffer;
            while ((uploadInfo.length = fileChannel.read(buffer)) > 0) {
                buffer.flip();
                if (!client.upload(uploadInfo)) {
                    success = false;
                    break;
                }
                buffer.clear();
            }
            fis.close();

            uploadInfo.msg = UploadMessage.END_UPLOAD;
            uploadInfo.data = null;
            if (!client.upload(uploadInfo)) {
                success = false;
            }
        }

        String alertMsg = success ? "Success to upload." : "Failure to upload.";
        System.out.println(alertMsg);
    }
}

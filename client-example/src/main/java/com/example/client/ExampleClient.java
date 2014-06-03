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
        TTransport transport = new TSocket("localhost", 8877);
        TProtocol protocol = new TBinaryProtocol(transport);
        ExampleService.Client client = new ExampleService.Client(protocol);
        transport.open();
        String rev = client.echo("Ah lololololo.");
        System.out.println(rev);
        transport.close();

        File file = new File(ExampleClient.class.getResource("/holololo.jpg").toURI());
        UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.msg = UploadMessage.BEGIN_UPLOAD;
        uploadInfo.fileName = file.getName();
        uploadInfo.length = file.length();

        boolean success = true;
        transport.open();
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
        transport.close();

        String alertMsg = success ? "Success to upload." : "Failure to upload.";
        System.out.println(alertMsg);
    }
}

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
        String rev = client.echo("Ah asdljasldj");
        System.out.println(rev);
        transport.close();

        transport.open();
        File file = new File(ExampleClient.class.getResource("/holololo.jpg").toURI());
        UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.msg = UploadMessage.BEGIN_UPLOAD;
        uploadInfo.fileName = file.getName();
        uploadInfo.length = file.length();

        if (client.upload(uploadInfo)) {
            FileInputStream f = new FileInputStream(file);
            FileChannel fileChannel = f.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 10);
            boolean sendAll = true;
            uploadInfo.msg = UploadMessage.PROGRESS_UPLOAD;
            while ((uploadInfo.length = fileChannel.read(buffer)) > 0) {
                buffer.flip();
                uploadInfo.data = buffer;
                if (!client.upload(uploadInfo)) {
                    sendAll = false;
                    break;
                }
                buffer.clear();
            }
            uploadInfo.msg = UploadMessage.END_UPLOAD;
            if (!client.upload(uploadInfo)) {
                sendAll = false;
            }
            f.close();
        }
        transport.close();
    }
}

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

/**
 * Created by silver on 14. 6. 2.
 */
public class ExampleClient {

    public static void main(String[] args) throws TException, IOException, URISyntaxException {
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

        Scanner prompt = new Scanner(System.in);
        do {
            printMenus();
            String arg = prompt.nextLine();
            if ("1".equals(arg)) {
                System.out.print("Input message: ");
                String msg = prompt.nextLine();
                echo(client, msg);
            } else if ("2".equals(arg)) {
                System.out.print("Input file path: ");
                String path = prompt.nextLine();
                upload(client, path);
            } else if ("3".equals(arg)) {
                System.out.println("Bye~");
                break;
            }
        } while (true);
    }

    private static void printMenus() {
        System.out.print("Select a number\n1) Echo\n2) File upload\n3) Exit\nSelect: ");
    }

    private static void echo(ExampleService.Client client, String msg) throws TException {
        client.getOutputProtocol().getTransport().open();
        String rev = client.echo(msg);
        System.out.println(rev);
        client.getOutputProtocol().getTransport().close();
    }

    private static void upload(ExampleService.Client client, String path) throws URISyntaxException, TException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException(path);
        }

        UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.msg = UploadMessage.BEGIN_UPLOAD;
        uploadInfo.fileName = file.getName();
        uploadInfo.length = file.length();

        client.getOutputProtocol().getTransport().open();
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
        client.getOutputProtocol().getTransport().close();

        String alertMsg = success ? "Success to upload." : "Failure to upload.";
        System.out.println(alertMsg);
    }
}

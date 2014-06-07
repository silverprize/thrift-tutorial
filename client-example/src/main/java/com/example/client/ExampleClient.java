package com.example.client;

import com.example.ExampleService;
import com.example.TransferInfo;
import com.example.TransferType;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Scanner;

/**
 * Created by silver on 14. 6. 2.
 */
public class ExampleClient {

    public static void main(String[] args) throws IOException, URISyntaxException {
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
                List<String> files = getAvailableFileList(client);
                if (files.isEmpty()) {
                    System.out.println("No files are available.");
                } else {
                    System.out.println("List of files");
                    for (int i = 0; i < files.size(); i++) {
                        System.out.println("[" + i + "] " + files.get(i));
                    }
                    System.out.print("Select file: ");
                    String fileNumber = prompt.nextLine();
                    int selected = Integer.parseInt(fileNumber);
                    if (selected >= 0 && selected < files.size()) {
                        download(client, files.get(selected));
                    } else {
                        System.out.println(selected + " is wrong number.");
                    }
                }
            } else if ("4".equals(arg)) {
                System.out.println("Bye~");
                break;
            }
        } while (true);
    }

    private static void printMenus() {
        System.out.print("Select a number\n" +
                "1) Echo\n" +
                "2) File upload\n" +
                "3) File download\n" +
                "4) Exit\n" +
                "Select: ");
    }

    private static void echo(ExampleService.Client client, String msg) {
        try {
            client.getOutputProtocol().getTransport().open();
            String rev = client.echo(msg);
            System.out.println(rev);
            client.getOutputProtocol().getTransport().close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private static void upload(ExampleService.Client client, String path) throws URISyntaxException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("File not found \"" + file.getAbsolutePath() + "\"");
            return;
        }

        FileInputStream fis = new FileInputStream(file);
        try {
            TransferInfo reqInfo = new TransferInfo();
            reqInfo.type = TransferType.REQUEST;
            reqInfo.fileName = file.getName();
            reqInfo.length = file.length();

            client.getOutputProtocol().getTransport().open();
            client.upload(reqInfo);

            reqInfo.type = TransferType.PROGRESS;
            reqInfo.data = ByteBuffer.allocate(1024 * 10);
            FileChannel fileChannel = fis.getChannel();
            while ((reqInfo.length = fileChannel.read(reqInfo.data)) > 0) {
                reqInfo.data.flip();
                client.upload(reqInfo);
                reqInfo.data.clear();
            }
            System.out.println("Success to upload.");
        } catch (TException e) {
            e.printStackTrace();
            fis.close();
            fis = null;
            file.delete();
        } finally {
            client.getOutputProtocol().getTransport().close();
            if (fis != null) {
                fis.close();
            }
        }
    }

    private static void download(ExampleService.Client client, String fileName)
            throws IOException {
        int dot = fileName.lastIndexOf('.');
        String name = dot != -1 ? fileName.substring(0, fileName.indexOf('.')) : fileName;
        String ext = dot != -1 ? fileName.substring(name.length()) : ".unknown";
        File destinationDir = new File("downloads");
        File destination = File.createTempFile(name, ext, destinationDir);
        FileOutputStream fos = new FileOutputStream(destination);

        try {
            TransferInfo reqInfo = new TransferInfo();
            reqInfo.type = TransferType.REQUEST;
            reqInfo.fileName = fileName;

            client.getInputProtocol().getTransport().open();
            TransferInfo recvInfo = client.download(reqInfo);

            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }

            long total = recvInfo.length;
            long offset = 0;
            reqInfo.type = TransferType.PROGRESS;
            do {
                recvInfo = client.download(reqInfo);
                offset += recvInfo.length;
                fos.getChannel().write(recvInfo.data);
            } while (total > offset);
            System.out.println("Success to download.");
        } catch (TException e) {
            e.printStackTrace();
            fos.close();
            fos = null;
            destination.delete();
        } finally {
            client.getInputProtocol().getTransport().close();
            if (fos != null) {
                fos.close();
            }
        }
    }

    private static List<String> getAvailableFileList(ExampleService.Client client) {
        try {
            client.getOutputProtocol().getTransport().open();
            List<String> list = client.getFileList();
            client.getOutputProtocol().getTransport().close();
            return list;
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }
}

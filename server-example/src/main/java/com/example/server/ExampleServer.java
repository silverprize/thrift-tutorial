package com.example.server;

import com.example.DownloadService;
import com.example.EchoService;
import com.example.UploadService;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by silver on 14. 6. 2.
 */
public class ExampleServer {
    public static void main(String[] args) {
        final int echoPort = Integer.getInteger("port.echo", 10004);
        final int downloadPort = Integer.getInteger("port.download", 10040);
        final int uploadPort = Integer.getInteger("port.upload", 10400);

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(3);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    startEchoServer(echoPort);
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    startDownloadServer(downloadPort);
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    startUploadServer(uploadPort);
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void startEchoServer(int port) throws TTransportException {
        TServerTransport transport = new TServerSocket(port);
        TSimpleServer.Args simpleServerArgs = new TSimpleServer.Args(transport);
        EchoServiceImpl serviceImpl = new EchoServiceImpl();
        EchoService.Processor<EchoService.Iface> processor = new EchoService.Processor<EchoService.Iface>(serviceImpl);
        simpleServerArgs.processor(processor);
        TSimpleServer simpleServer = new TSimpleServer(simpleServerArgs);
        simpleServer.serve();
    }

    private static void startDownloadServer(int port) throws TTransportException {
        TServerTransport transport = new TServerSocket(port);
        TSimpleServer.Args simpleServerArgs = new TSimpleServer.Args(transport);
        DownloadServiceImpl serviceImpl = new DownloadServiceImpl();
        DownloadService.Processor<DownloadService.Iface> processor = new DownloadService.Processor<DownloadService.Iface>(serviceImpl);
        simpleServerArgs.processor(processor);
        TSimpleServer simpleServer = new TSimpleServer(simpleServerArgs);
        simpleServer.serve();
    }

    private static void startUploadServer(int port) throws TTransportException {
        TServerTransport transport = new TServerSocket(port);
        TSimpleServer.Args simpleServerArgs = new TSimpleServer.Args(transport);
        UploadServiceImpl serviceImpl = new UploadServiceImpl();
        UploadService.Processor<UploadService.Iface> processor = new UploadService.Processor<UploadService.Iface>(serviceImpl);
        simpleServerArgs.processor(processor);
        TSimpleServer simpleServer = new TSimpleServer(simpleServerArgs);
        simpleServer.serve();
    }
}

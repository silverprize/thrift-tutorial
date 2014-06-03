package com.example.server;

import com.example.ExampleService;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by silver on 14. 6. 2.
 */
public class ExampleServer {
    public static void main(String[] args) throws TTransportException {
        int port = 10004;
        if (Integer.getInteger("port") != null) {
            port = Integer.getInteger("port");
        }
        TServerTransport transport = new TServerSocket(port);
        TSimpleServer.Args simpleServerArgs = new TSimpleServer.Args(transport);
        ExampleServiceImpl serviceImpl = new ExampleServiceImpl();
        ExampleService.Processor<ExampleService.Iface> processor = new ExampleService.Processor<ExampleService.Iface>(serviceImpl);
        simpleServerArgs.processor(processor);
        TSimpleServer simpleServer = new TSimpleServer(simpleServerArgs);
        simpleServer.serve();
    }
}

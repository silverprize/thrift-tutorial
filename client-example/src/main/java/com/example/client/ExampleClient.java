package com.example.client;

import com.example.ExampleService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * Created by silver on 14. 6. 2.
 */
public class ExampleClient {
    public static void main(String[] args) throws TException {
        TTransport transport = new TSocket("localhost", 8877);
        TProtocol protocol = new TBinaryProtocol(transport);
        ExampleService.Client client = new ExampleService.Client(protocol);
        transport.open();
        String rev = client.echo("Ah asdljasldj");
        System.out.print(rev);
        transport.close();
    }
}

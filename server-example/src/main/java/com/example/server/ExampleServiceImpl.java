package com.example.server;

import com.example.ExampleService;
import org.apache.thrift.TException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by silver on 14. 6. 2.
 */
public class ExampleServiceImpl implements ExampleService.Iface {
    @Override
    public String echo(String input) throws TException {
        try {
            return "from " + InetAddress.getLocalHost().toString() + " : " + input;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "null";
    }
}

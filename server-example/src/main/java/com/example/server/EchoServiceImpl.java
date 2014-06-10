package com.example.server;

import com.example.EchoService;
import org.apache.thrift.TException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by silver on 14. 6. 10.
 */
public class EchoServiceImpl implements EchoService.Iface {
    @Override
    public String echo(String input) throws TException {
        try {
            return "from " + InetAddress.getLocalHost().getHostAddress() + " : " + input;
        } catch (UnknownHostException e) {
            throw new TException(e);
        }
    }
}

package com.example.filesync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("example.com", 80);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("GET / HTTP/1.1");
            out.println("Host: example.com");
            out.println("");

            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

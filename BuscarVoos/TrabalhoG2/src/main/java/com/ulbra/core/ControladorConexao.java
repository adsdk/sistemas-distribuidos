package com.ulbra.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

/**
 * @author adrisson.silva
 */
public class ControladorConexao implements Serializable {

    private final Socket socket;
    private final OutputStream output;
    private final InputStream input;
    private boolean conectionOpen;
    private final BufferedReader br;
    private final PrintWriter pr;

    public ControladorConexao(String destino, int porta) throws IOException {
        this(new Socket(destino, porta));
    }

    public ControladorConexao(Socket socket) throws IOException {
        this.socket = socket;
        this.output = this.socket.getOutputStream();
        this.input = this.socket.getInputStream();
        this.conectionOpen = true;
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.pr = new PrintWriter(socket.getOutputStream(), true);
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getInput() {
        return input;
    }

    public OutputStream getOutput() {
        return output;
    }

    public boolean enviar(String msg) throws IOException {
        if (isConectionOpen()) {
            pr.println(msg);
        }
        return true;
    }

    public String receber() throws IOException, ClassNotFoundException {
        if (isConectionOpen()) {
            return br.readLine();
        }
        return null;
    }

    public void close() throws IOException {
        this.conectionOpen = false;
        this.output.close();
        this.input.close();
        this.pr.close();
        this.br.close();
        this.socket.close();
    }
    
    public void limparBuffer() throws InterruptedException, IOException {
        Thread.sleep(500);
        
        while (br.ready()) {
            br.readLine();
        }
    }

    public boolean isConectionOpen() {
        return conectionOpen;
    }
}

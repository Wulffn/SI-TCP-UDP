package udp;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;


public class UDPC {
    // Client needs to know server identification, <IP:port>
    private static final int serverPort = 7777;

    // buffers for the messages
    public static String message;
    private final static int maxSize = 65507;
    private static byte[] dataIn = new byte[maxSize];
    private static byte[] dataOut = new byte[maxSize];


    // In UDP messages are encapsulated in packages and sent over sockets
    private static DatagramPacket requestPacket;
    private static DatagramPacket responsePacket;
    private static DatagramSocket clientSocket;

    public static void main(String[] args) throws IOException {

        clientSocket = new DatagramSocket();
        InetAddress serverIP = InetAddress.getByName(args[0]);
        System.out.println(serverIP);


        Scanner scan = new Scanner(System.in);
        System.out.println("Type message: ");

        while ((message = scan.nextLine()) != null) {
            sendImg(serverIP);
            receiveImg();
        }
        clientSocket.close();

    }

    public static void sendRequest(InetAddress serverIP) throws IOException {
        //clientSocket = new DatagramSocket();
        dataOut = message.getBytes();
        requestPacket = new DatagramPacket(dataOut, dataOut.length, serverIP, serverPort);
        clientSocket.send(requestPacket);
    }

    public static void receiveResponse() throws IOException {
        //clientSocket = new DatagramSocket();
        responsePacket = new DatagramPacket(dataIn, dataIn.length);
        clientSocket.receive(responsePacket);
        String message = new String(responsePacket.getData(), 0, responsePacket.getLength());
        System.out.println("Response from Server: " + message);
    }


    public static void sendImg(InetAddress serverIP) throws IOException {
        dataOut = getBytesFromImage("img1.jpg");
        requestPacket = new DatagramPacket(dataOut, dataOut.length, serverIP, serverPort);
        clientSocket.send(requestPacket);
    }

    public static void receiveImg() throws IOException {
        responsePacket = new DatagramPacket(dataIn, dataIn.length);
        clientSocket.receive(responsePacket);
        saveImageFromBytes(responsePacket.getData());
    }

    public static byte[] getBytesFromImage(String fileName) throws IOException {
        File file = new File("src/main/resources/" + fileName);
        return Files.readAllBytes(file.toPath());
    }

    public static void saveImageFromBytes(byte[] image) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(image);
        BufferedImage bImage2 = ImageIO.read(bis);
        String filename = "imgFromServer" + System.currentTimeMillis() + ".jpg" ;
        ImageIO.write(bImage2, "jpg", new File("src/main/resources/" + filename) );
    }

}
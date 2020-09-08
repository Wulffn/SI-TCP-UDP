package udp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;

public class UDPS
{
    private static final int serverPort = 7777;

    // buffers for the messages
    private final static int maxSize = 65507;
    private static byte[] dataIn = new byte[maxSize];
    private static byte[] dataOut = new byte[maxSize];

    // In UDP messages are encapsulated in packages and sent over sockets
    private static DatagramPacket requestPacket;
    private static DatagramPacket responsePacket;
    private static DatagramSocket serverSocket;


    public static void main(String[] args) throws Exception
    {
        DatagramPacket messageIn, messageOut;
        try
        {
            String serverIP = InetAddress.getLocalHost().getHostAddress();
            // Opens socket for accepting requests
            serverSocket = new DatagramSocket(serverPort);
            while(true)
            {
                System.out.println("Server " + serverIP + " running ...");
                messageOut = receiveRequest();
                sendResponse(messageOut);
            }
        }
        catch(Exception e)
        {
            System.out.println(" Connection fails: " + e);
        }
        finally
        {
            serverSocket.close();
            System.out.println("Server port closed");
        }
    }

    public static DatagramPacket receiveRequest() throws IOException
    {
        requestPacket = new DatagramPacket(dataIn, dataIn.length);
        serverSocket.receive(requestPacket);
        System.out.println("msg length: " + requestPacket.getLength());

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(requestPacket.getData());
            BufferedImage bImage2 = ImageIO.read(bis);
            String filename = "serverCreatedImage" + System.currentTimeMillis() + ".jpg" ;
            ImageIO.write(bImage2, "jpg", new File("src/main/resources/" + filename) );
        } catch (Exception e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }

        return requestPacket;
    }

    public static void sendResponse(DatagramPacket message) throws IOException
    {
        InetAddress clientIP;
        int clientPort;

        clientIP = message.getAddress();
        clientPort = message.getPort();
        System.out.println("Client port: " + clientPort);

        responsePacket = new DatagramPacket(message.getData(), message.getLength(), clientIP, clientPort);
        System.out.println("response length: " + responsePacket.getLength());
        serverSocket.send(responsePacket);
    }
}
/*
 * Author: Nate Sutton
 * 2017
 * 
 * References:
 * https://stackoverflow.com/questions/4231458/moving-the-cursor-in-java
 */

package mouse_controller;

import java.awt.AWTException;
import java.awt.Robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class mouse_controller {	

	public static void main(String[] args) {
		  ServerSocket serverSocket = null;
		  Socket socket = null;
		  DataInputStream dataInputStream = null;
		  DataOutputStream dataOutputStream = null;
		
		// TODO Auto-generated method stub
		System.out.print("hello world\n\n");
		
		try {
		    // These coordinates are screen coordinates
		    int xCoord = 500;
		    int yCoord = 500;

		    // Move the cursor
		    Robot robot = new Robot();
		    robot.mouseMove(xCoord, yCoord);
		} catch (AWTException e) {
		}
		
		try {
		   serverSocket = new ServerSocket(8888);
		   System.out.println("Listening :8888");
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }
		  
		  while(true){
		   try {
		    socket = serverSocket.accept();
		    dataInputStream = new DataInputStream(socket.getInputStream());
		    dataOutputStream = new DataOutputStream(socket.getOutputStream());
		    System.out.println("ip: " + socket.getInetAddress());
		    System.out.println("message: " + dataInputStream.readUTF());
		    dataOutputStream.writeUTF("Hello!");
		   } catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		   }
		   finally{
		    if( socket!= null){
		     try {
		      socket.close();
		     } catch (IOException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		     }
		    }
		    
		    if( dataInputStream!= null){
		     try {
		      dataInputStream.close();
		     } catch (IOException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		     }
		    }
		    
		    if( dataOutputStream!= null){
		     try {
		      dataOutputStream.close();
		     } catch (IOException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		     }
		    }
		   }
		  }
	}

}

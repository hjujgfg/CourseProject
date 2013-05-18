package com.lapidus.android.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.threed.jpct.SimpleVector;

import android.util.Log;



public class ClientThreadRan implements Runnable {
	Socket socket;
	public static boolean connected;
	public static SimpleVector ss;
	public static SimpleVector ss1;
	public void run() {
		// TODO Auto-generated method stub
		
		ObjectInputStream ois;
		ObjectOutputStream oos;   
		connected = true;
		SimpleVector tmp;
		while(connected) {
			try {
    			//creates a new Socket object and names it socket.
    			//Establishes the socket connection between the client & server
    			//name of the machine & the port number to which we want to connect
    			socket = new Socket(ConnectionEstablisher.serverIpAddress, ConnectionEstablisher.SERVERPORT);
    			//if (printOutput) {
    				System.out.print("Establishing connection.");
    			//}
    			//opens a PrintWriter on the socket input autoflush mode
    			ois = new ObjectInputStream(socket.getInputStream());
    			oos = new ObjectOutputStream(socket.getOutputStream());
    			//ss.field2 ++;
    			oos.writeObject(ss);
    			Log.i("ClientActivity", "sent " + ss.toString());
    			tmp = (SimpleVector)ois.readObject();
    			ss1.x = tmp.x;
    			ss1.y = tmp.y;
    			ss1.z = tmp.z;
    			Log.i("ClientActivity", "got " + ss.toString());   
    			
    		}
    		catch (UnknownHostException e) {
    			System.err.println("Unknown host: " + ConnectionEstablisher.serverIpAddress);
    			//System.exit(1);
    		}
    		catch (ConnectException e) {
    			System.err.println("Connection refused by host: " + ConnectionEstablisher.serverIpAddress);
    			//System.exit(1);
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		// finally, close the socket and decrement runningThreads
    		finally {
    			System.out.println("closing");
    			
    			try {
    				socket.close();
    				//runningThreads.decrementAndGet();
    				System.out.flush();
    			}
    			catch (IOException e ) {
    				System.out.println("Couldn't close socket");
    			}
    		}
		}    		
	}    	
}

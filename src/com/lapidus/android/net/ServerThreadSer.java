package com.lapidus.android.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.sax.StartElementListener;

import com.lapidus.android.engine.Engine;
import com.lapidus.android.primitives.Point;

public class ServerThreadSer implements Runnable {
	boolean connection;
	ObjectOutputStream oos; 
	public static ArrayList<Point> arr;
	Socket client; 
	public static Context context;
	Handler handler = new Handler();
	public void run() {
		// TODO Auto-generated method stub
		System.out.print("Accepted connection. ");
    	connection = true;
    	
		System.out.println("Server listening on port 15432");
		while (connection){
    		try {
    			ServerSocket socket = new ServerSocket(ConnectionEstablisher.SERVERPORT);
    			client = socket.accept();
    			// open a new PrintWriter and BufferedReader on the socket
    			
    			oos = new ObjectOutputStream(client.getOutputStream());
    			
    			System.out.print("Reader and writer created. ");

    			System.out.println("Writing");
    			oos.writeObject(arr);
    		    oos.flush();
    		    oos.close();
    		    connection = false;
    		    System.out.println("ready");
    			// run the command using CommandExecutor and get its output
    			handler.post(new Runnable() {
					
					public void run() {
						// TODO Auto-generated method stub
						Engine.path = (ArrayList<Point>)arr.clone();
						Intent i = new Intent(context, Engine.class);
						context.startActivity(i);
					}
				});
    			
    			//output.println(arr);
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		} 
    		finally {
    			// close the connection to the client
    			try {
    				client.close();
    			}
    			catch (IOException e) {
    				e.printStackTrace();	
    			}			
    			System.out.println("Output closed.");
    		}
    	}
	}

}

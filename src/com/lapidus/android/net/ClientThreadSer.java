package com.lapidus.android.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.lapidus.android.engine.Engine;
import com.lapidus.android.primitives.Point;





public class ClientThreadSer implements Runnable {
	public static ArrayList<Point> receivedPoints;
	Socket socket;	
	public static boolean connection;
	Handler handler = new Handler();
	public static Context context;
	public void run() {
		// TODO Auto-generated method stub
		connection = true;
		ObjectInputStream ois;
		while (connection) {
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
				/*ObjectInputStream ois = (ObjectInputStream) socket.getInputStream();
				*/
				try {
					receivedPoints = (ArrayList<Point>) ois.readObject();
					for (Point x : receivedPoints) {
						System.out.print(" " + x.toString());
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				handler.post(new Runnable() {
					
					public void run() {
						// TODO Auto-generated method stub
						Toast toast = Toast.makeText(context, "Got points " + receivedPoints.size() , Toast.LENGTH_SHORT);
						toast.show();
						Engine.path = new ArrayList<Point>();
						for (Point x : receivedPoints) {
							try {
								Engine.path.add(x.clone());
							} catch (CloneNotSupportedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						Intent i = new Intent(context, Engine.class);
						context.startActivity(i);
						
					}
				});
				connection = false;
	
			}
			catch (UnknownHostException e) {
				System.err.println("Unknown host: " + ConnectionEstablisher.serverIpAddress);
				
			}
			catch (ConnectException e) {
				System.err.println("Connection refused by host: " + ConnectionEstablisher.serverIpAddress);
				
			}
			catch (IOException e) {
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

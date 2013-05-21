package com.lapidus.android.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.threed.jpct.SimpleVector;

import android.os.Handler;
import android.util.Log;

/**класс-сервер для непрерывного обмена данными о положении моделей игроков*/
public class ServerThreadRan implements Runnable{
	/**выходной поток*/
	ObjectOutputStream oos;
	/**входной поток*/
	ObjectInputStream ois;
	/**вектор напрвления основной модели*/
	public static SimpleVector ss;
	/**вектор напрвления модели соперника*/
	public static SimpleVector ss1; 
	/**индикатор соединения*/
	public static boolean connection;
	/**сокет клиента*/
	Socket client;
	/**
	 * наследуемый метод
	 * @see java.lang.Runnable#run() 
	 */	
	public void run() {
		// TODO Auto-generated method stub
		System.out.print("Accepted connection. ");
    	connection = true;
		System.out.println("Server listening on port " +ConnectionEstablisher.SERVERPORT);
		SimpleVector tmp;
		while (connection){
    		try {
    			ServerSocket socket = new ServerSocket(ConnectionEstablisher.SERVERPORT);
    			client = socket.accept();
    			// open a new PrintWriter and BufferedReader on the socket
    			
    			oos = new ObjectOutputStream(client.getOutputStream());
    			ois = new ObjectInputStream(client.getInputStream());
    			System.out.print("Reader and writer created. ");
    			tmp = (SimpleVector)ois.readObject();
    			Log.i("ServerActivity", "got " + tmp.toString());
    			ss1.x = tmp.x;
    			ss1.y = tmp.y;
    			ss1.z = tmp.z;
    			Log.i("ServerActivity", "ss1 " + ss1.toString());     
    			
    			System.out.println("Writing");
    			oos.writeObject(ss);    			    			
    			Log.i("ServerActivity", "sent " + ss.toString());
    			
    		    oos.flush();
    		    oos.close();		    		    
    		    System.out.println("ready");
    			// run the command using CommandExecutor and get its output
    			
    			
    			//output.println(arr);
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
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

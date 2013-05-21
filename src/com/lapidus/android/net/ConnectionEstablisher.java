package com.lapidus.android.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.lapidus.android.R;
import com.lapidus.android.painter.Painter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
/**класс активности установки соединения*/
public class ConnectionEstablisher extends Activity {
	/**индикатор сервера*/
	public static boolean isServer;
	/**обработчик для главного потока*/
	Handler handler; 
	/**индикатор соединения*/
	boolean connection;
	/**входная строка*/
	String inString;
	/**выходная строка*/
	String outString;
	/**контекст*/
	Context context;
	/**сокет клиента*/
	Socket client;
	/**скоет клиента на сервере*/
	Socket socket;
	/**клиентсткий тред*/
	Thread cthread; 
	/**серверный тред*/
	Thread sThread;
	/** порт */
	static int SERVERPORT = 8090;
	/** адрес по умолчанию */
	static String serverIpAddress = "192.168.1.103";
	/**
	 * наследуемый метод, инициализирует поля
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection_establisher_layout);
		handler = new Handler();
		connection = true;
		TextView client = (TextView) findViewById(R.id.client_button);
		context = this;
		Painter.isMulti = true;
		client.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				AlertDialog.Builder alert = new AlertDialog.Builder(context);

				alert.setTitle("Server IP");
				alert.setMessage("Enter server IP");

				// Set an EditText view to get user input 
				final EditText input = new EditText(context);
				alert.setView(input);
				input.setText(serverIpAddress);

				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				  String value = input.getText().toString();
				  // Do something with value!
				  	serverIpAddress = value;
					cthread = new Thread(new ClientThread());									
					cthread.start();
					isServer = false;
					dialog.dismiss();
				  }
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
					  	dialog.dismiss();
				  }
				});

				alert.show();
			}
		});
		TextView server = (TextView)findViewById(R.id.server_button);
		server.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				AlertDialog.Builder alert = new AlertDialog.Builder(context);

				alert.setTitle("Run Server");
				alert.setMessage("Enter this IP address on client");

				// Set an EditText view to get user input 
				final TextView input = new TextView(context);
				input.setText(getLocalIpAddress());
				alert.setView(input);

				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				  String value = input.getText().toString();
				  // Do something with value!
				  	sThread = new Thread(new ServerThread());
					sThread.start();	
					isServer = true;
					dialog.dismiss();
				  }
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
					  dialog.dismiss();
				  }
				});

				alert.show();
			}
		});		
	}
	/**
	 * обработчик нажатия кнопки
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		return super.onKeyDown(keyCode, event);		
	}
	/**
	 * получить IP-адрес
	 * @return IP-адрес
	 */
	private String getLocalIpAddress() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		return ip;
    }
	/**
	 * преобразовать адрес к читаемому виду
	 * @param i IP адрес
	 * @return строковое отображение 
	 */
	private String intToIp(int i) {
		   return ( i & 0xFF) + "." +
		   			((i >> 8 ) & 0xFF) + "." +
		   			((i >> 16 ) & 0xFF) + "." +
		   			((i >> 24 ) & 0xFF );
		}
	/**
	 * класс сервера для первоначальной установки соединения
	 * @author Егор
	 *
	 */
	public class ServerThread implements Runnable {
		/**
		 * @see java.lang.Runnable#run()
		 */
        public void run() {
        	System.out.print("Accepted connection. ");
        	
        	PrintWriter output;
        	BufferedReader input;
			System.out.println("Server listening on port 15432");
			while (connection){
	    		try {
	    			ServerSocket socket = new ServerSocket(SERVERPORT);
	    			client = socket.accept();
	    			// open a new PrintWriter and BufferedReader on the socket
	    			
	    			output = new PrintWriter(client.getOutputStream(), true);
	    			input = new BufferedReader(new InputStreamReader(client.getInputStream()));
	    			System.out.print("Reader and writer created. ");

	    			
	    			// read the command from the client
	    		        while  ((inString = input.readLine()) == null);
	    			System.out.println("Read command " + inString);
	    			handler.post(new Runnable() {
						
						public void run() {
							// TODO Auto-generated method stub
							Toast t = Toast.makeText(context, "got^ " + inString, Toast.LENGTH_SHORT);
							t.show();
						}
					});
	    		        
	    			// run the command using CommandExecutor and get its output
	    			
	    			outString = "END_MESSAGE";
	    			
	    			System.out.println("Server sending result to client");
	    			// send the result of the command to the client
	    			
	    			output.println(outString);
	    			handler.post(new Runnable() {
						
						public void run() {
							// TODO Auto-generated method stub
							Toast t = Toast.makeText(context, "Success" + outString, Toast.LENGTH_SHORT);
							t.show();
							Intent i = new Intent(context, Painter.class);
							startActivity(i);
						}
					});
	    			connection = false;
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
	/**
	 * класс клиента для установки первоначального соединения 
	 * @author Егор
	 *
	 */
	public class ClientThread implements Runnable {
		 /**
		  * @see java.lang.Runnable#run()
		  */
        public void run() {
        	PrintWriter out = null;
    		BufferedReader input = null;
    		client = new Socket();
    		try {
    			//creates a new Socket object and names it socket.
    			//Establishes the socket connection between the client & server
    			//name of the machine & the port number to which we want to connect
    			socket = new Socket(serverIpAddress, SERVERPORT);
    			
    			System.out.print("Establishing connection.");
    			
    			//opens a PrintWriter on the socket input autoflush mode
    			out = new PrintWriter(socket.getOutputStream(), true);

    			//opens a BufferedReader on the socket
    			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    			 System.out.println("\nRequesting output for the '" +"' command from " + serverIpAddress);
    			 
    			// get the current time (before sending the request to the server)
    			

    			// send the command to the server
    			out.println("establishing connection");
    			System.out.println("Sent output");

    			// read the output from the server
    			outString= input.readLine();
    			Log.i("ClientActivity", outString);
    			//if (outString == "END_MESSAGE") {
    				handler.post(new Runnable() {
						
						public void run() {
							// TODO Auto-generated method stub
							Toast t = Toast.makeText(context, outString + " connected", Toast.LENGTH_SHORT);
							t.show();
							ClientThreadSer.context = context;
							Thread tr = new Thread(new ClientThreadSer());
							tr.start();
						}
					});
    			//}
    			while ((outString != null) && (!outString.equals("END_MESSAGE"))) {
    				System.out.println(outString);
    				handler.post(new Runnable() {
						
						public void run() {
							// TODO Auto-generated method stub
							Toast t = Toast.makeText(context, outString + " connected", Toast.LENGTH_SHORT);
							t.show();
							Thread tr = new Thread(new ClientThreadSer());
							tr.start();
						}
					});
    			}    			

    		}
    		catch (UnknownHostException e) {
    			System.err.println("Unknown host: " + serverIpAddress);
    			handler.post(new Runnable() {
					
					public void run() {
						// TODO Auto-generated method stub
						Toast t = Toast.makeText(context, "Unknown host", Toast.LENGTH_SHORT);
						t.show();
					}
				});
    		}
    		catch (ConnectException e) {
    			System.err.println("Connection refused by host: " + serverIpAddress);
    			handler.post(new Runnable() {
					
					public void run() {
						// TODO Auto-generated method stub
						Toast t = Toast.makeText(context, "Connection refused by host: " + serverIpAddress, Toast.LENGTH_SHORT);
						t.show();
					}
				});
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    			handler.post(new Runnable() {
					
					public void run() {
						// TODO Auto-generated method stub
						Toast t = Toast.makeText(context, "connection corrupt", Toast.LENGTH_SHORT);
						t.show();
					}
				});
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

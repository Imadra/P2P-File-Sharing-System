

import java.io.*;
import java.net.*;
import java.util.*;

public class SThread extends Thread{
	private BufferedReader br;
	private PrintWriter pw;
	private Socket connectToClient;
	private method serverfunction;

	public SThread(Socket soc,method serverfunction)throws IOException{
		super();
		connectToClient = soc;
		this.serverfunction = serverfunction;
		br = getReader(connectToClient);
		pw = getWriter(connectToClient);
		start();
	}

	public PrintWriter getWriter(Socket socket)throws IOException{
		OutputStream socketOut = socket.getOutputStream();
		return new PrintWriter(socketOut, true);

	}

	public BufferedReader getReader(Socket socket)throws IOException{
		InputStream socketIn = socket.getInputStream();
		return new BufferedReader(new InputStreamReader(socketIn));

	}

	/*
	 * Deal with messages sent from client
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		try{

			String msg = null;

			while((msg = br.readLine())!=null){
                System.out.println(msg);
				String[] st = msg.split(" ", 4);
				String command = st[0];
                System.out.println(command);
				String ID = st[1];
                System.out.println(ID);
                String fileName = st[2];
                System.out.println(fileName);


				if("register".equals(command)){
                    String full = st[3];
                    serverfunction.registery(ID, fileName, full);

				}
                    else if("unregister".endsWith(command)){
					serverfunction.unregistery(ID, fileName);

				}
                  else if("search".equals(command)){
					ArrayList<String> peerList = new ArrayList<String>();

					//find the peer list that have that file
					peerList = serverfunction.search(fileName);
					//pw.println(peerList);
					if(peerList.size() != 0){
                        for(int i =0; i<peerList.size();i++){
                                pw.println(peerList.get(i));
                        }
					}

					pw.println("bye");

				}

			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

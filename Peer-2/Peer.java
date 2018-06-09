


import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Peer {
	
	/* 
	 *  and register all files in the local file list.
	 */
	public static void Monitor_file(String path, procedure peerfunction){
		File file = new File(path);
                File[] listOfFiles = file.listFiles();
                if(listOfFiles.length != 0) {
                    for (int i = 0; i < listOfFiles.length; i++) {
                        if(listOfFiles[i].isFile()) {
                            String name = listOfFiles[i].getName();
                            int dot = name.lastIndexOf('.');
                            String base = (dot == -1) ? name : name.substring(0, dot);
                            String extension = (dot == -1) ? "" : name.substring(dot+1);
                            long sz = listOfFiles[i].length();
                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yy");
                            String lastMod = sdf2.format(listOfFiles[i].lastModified());
                            String curFile = base + " " + extension + " " + sz + " "
                                    + lastMod+" ";
                            Thread_for_register(name, peerfunction, curFile);
                            //System.out.println(curFile);
                        }
                    }
                }
		//String test[];
		//test = file.list();
		//if(test.length!=0){
		//	for(int i = 0; i<test.length; i++){
		//	Thread_for_register(test[i],peerfunction);
		//	}
		//}
                else {
                    System.out.println("You are not sharing any files!");
                    System.exit(0);
                }
		// Use WrThread to auto update register file
		new WrThread(path,peerfunction);
	}
	
	/*
	 *  Register the file to the index server
	 */ 
	public static void Thread_for_register(String fileName, procedure peerfunction, String full){
		Socket socket = null;
		StringBuffer sb = new StringBuffer("register ");
		try{
			peerSocket peersocket = new peerSocket();
			socket = peersocket.socket;
			BufferedReader br = peersocket.getReader(socket);
			PrintWriter pw = peersocket.getWriter(socket);
			// register to the local file
			full = full + Info_Peer.local.ID;
			peerfunction.Do_register(Info_Peer.local.ID, fileName, full);
			
			// register to the server end
			// Send register message
			sb.append(Info_Peer.local.ID);
			sb.append(" "+fileName);
                        sb.append(" "+full);
			pw.println(sb.toString());

		}catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally{
			try{
				if(socket!=null){
					socket.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	


	public void do_it(procedure peerfunction)throws IOException{
		
		boolean exit = false;
		// Store file name
		String fileName = null;			
		
		BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
		String cmd = localReader.readLine();
                while(!cmd.equals("HELLO")) {
                    System.out.println("You've entered the wrong starting command.");
                    System.out.println("Enter HELLO to share your files!");
                    cmd = localReader.readLine();
                }
                System.out.println("HI");
                Monitor_file(Info_Peer.local.path,peerfunction);
		// Usage Interface
		while(!exit)
		{
                        System.out.println("\nEnter command: ");
                        cmd = localReader.readLine();
                        if(cmd.equals("BYE")) {
                            exit = true;
                            System.exit(0);
                        }
                        ArrayList<String> nword;
                        nword = new ArrayList<>();
                        String curWord = new String();
                        for (int i = 0; i < cmd.length(); i++){
                            char c = cmd.charAt(i); 
                            if(c != ' ') {
                                curWord = curWord + c;
                            }
                            else {
                                nword.add(curWord);
                                curWord = "";
                            }
                            //Process char
                        }
                        nword.add(curWord);
                        nword.add("");
                        if("SCORE".equals(nword.get(0)) && nword.get(1).equals("")) {
                            int x = Info_Peer.local.uploads*100, y = Info_Peer.local.requests;
                            int z = 0;
                            if(y!=0)
                                z = x/y;
                            System.out.println("SCORE OF CURRENT PEER: "+z);
                        }
                        if(nword.get(0).equals("SEARCH") && !nword.get(1).equals("") && nword.get(2).equals("")) {
                            boolean find;
                            fileName = nword.get(1);
                            // Search file through index server
                            find = searchThread(fileName, peerfunction);
                            if(find) {
                                System.out.println("\n Choose which peer you want to connect?\n");
                                int x = Integer.parseInt(localReader.readLine());
                                System.out.println("You are connected to peer of index: " + x);
                                while(true) {
                                    System.out.println("Write a message to this peer: ");
                                    cmd = localReader.readLine();
                                    ArrayList<String> n;
                                    n = new ArrayList<>();
                                    String cur = new String();
                                    for (int i = 0; i < cmd.length(); i++){
                                        char c = cmd.charAt(i); 
                                        if(c != ' ') {
                                            cur = cur + c;
                                        }
                                        else {
                                            n.add(cur);
                                            cur = "";
                                        }
                                        //Process char
                                    }
                                    n.add(cur);
                                    n.add("");
                                    if(n.get(0).equals("DISCONNECT") && n.get(1).equals("")) {
                                        break;
                                    }
                                    if(!n.get(0).equals("DOWNLOAD:") || n.get(1).equals("")
                                             || n.get(2).equals("")  || n.get(3).equals("")  || !n.get(4).equals("")) {
                                        System.out.println("Peer does not understand your message command");
                                        System.out.println("Please type the message in the format of:");
                                        System.out.println("<DOWNLOAD: FileName, Type, Size> without paratheses to download the file from peer");
                                        System.out.println("Or type DISCONNECT to cancel and go back");
                                        continue;
                                    }
                                    String nn = n.get(1);
                                    if (nn != null && nn.length() > 0) {
                                        nn = nn.substring(0, nn.length() - 1);
                                    }
                                    String nn2 = n.get(2);
                                    if (nn2 != null && nn2.length() > 0) {
                                        nn2 = nn2.substring(0, nn2.length() - 1);
                                    }
                                    download(nn, peerfunction, x, nn2, n.get(3));
                                    try {
                                        TimeUnit.SECONDS.sleep(3);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    String IP = null;
                                    int port = 0;
                                    int serverPort = 0;

                                    String address = Info_Peer.dest.destPath.get(x-1);

                                    String[] info = address.split("\\:");
                                    IP = info[0];
                                    port = Integer.parseInt(info[1]);
                                    System.out.print("Score of "+IP+":"+port+": ");
                                    cmd = localReader.readLine();
                                    int yyy = Integer.parseInt(cmd);
                                    
                                    //peerfunction.addReq();
                                    Info_Peer.local.requests++;
                                    if(yyy==0)
                                    {
                                        System.out.println("NOT DOWNLOADED");
                                    }
                                    else {
                                        System.out.println("DOWNLOADED");
                                        Info_Peer.local.uploads++;
                                        //peerfunction.addUpl();
                                    }
                                    break;
                                }
                                //download(nword.get(0), peerfunction, x);
                            }
                        }
		}	
	}
	


	public static void Thread_for_unregister(String fileName, procedure peerfunction){
		Socket socket = null;
		
		System.out.println("in");


		StringBuffer sb = new StringBuffer("unregister ");
		try{			
			peerSocket peersocket = new peerSocket();
			socket = peersocket.socket;
			BufferedReader br = peersocket.getReader(socket);
			PrintWriter pw = peersocket.getWriter(socket);
			// Unregister to the local file
			
			peerfunction.unDo_register(Info_Peer.local.ID, fileName);
			
			// Unregister to the server end
			// Send unregister message
			sb.append(Info_Peer.local.ID);
			sb.append(" "+fileName);
			
			pw.println(sb.toString());

		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				if(socket!=null){
					socket.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	
	public boolean searchThread(String fileName, procedure peerfunction){
		
		Info_Peer peerinfo = new Info_Peer();
		peerinfo.initial();
		Socket socket = null;
		StringBuffer sb = new StringBuffer("search ");
		boolean find = false;
		// Store file ID
		String findID = null;
		
		try{			
			peerSocket peersocket = new peerSocket();
			socket = peersocket.socket;
			BufferedReader br = peersocket.getReader(socket);
			PrintWriter pw = peersocket.getWriter(socket);
			
			// Send search message
			sb.append(Info_Peer.local.ID);
			sb.append(" "+fileName);
			pw.println(sb.toString());
			
			// Get peer list
                        int i = 1;
                        System.out.println("FOUND:");
			while(!("bye".equals(findID = br.readLine()))){
                                System.out.println(i+") "+findID);
                                String[] getID = findID.split(" ", 4);
                                
				Info_Peer.dest.destList.add(getID[3]);
                                i++;
			}
			// If find file in some peers, output their address
                        find = peerfunction.search(fileName);
			/*if((find = peerfunction.search(fileName))== true){
				for(int i=0; i<Info_Peer.dest.destPath.size(); i++){
					System.out.println(fileName+" is found on "+Info_Peer.dest.destPath.get(i));
				}
			}*/

		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				if(socket!=null){
					socket.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		return find;
		
	}
	
	/*
	 *  Used to download file from other clients
	 */
	public void download(String fileName, procedure peerfunction, int peerNum, String type, String sz){
		
		String IP = null;
		String folder = null;
		int port = 0;
		int serverPort = 0;
		
		String address = Info_Peer.dest.destPath.get(peerNum-1);
		
		String[] info = address.split("\\:");
		IP = info[0];
		port = Integer.parseInt(info[1]);
		folder = info[2];
		/*
		 *  Set up serverPort
		 */
		serverPort = Info_Peer.local.downloadPort;
		// Set up a server socket to receive file
                fileName = fileName + "."+type;
		DThread x = new DThread(serverPort,fileName);
		
		// Set up a socket connection to the peer destination
		Socket socket = null;
		
		StringBuffer sb = new StringBuffer("download ");
		try{
			peerSocket peersocket = new peerSocket(IP, port);
			socket = peersocket.socket;
			
			BufferedReader br = peersocket.getReader(socket);
			PrintWriter pw = peersocket.getWriter(socket);
						
			// Send download message
			sb.append(fileName);
			sb.append(" " + serverPort);
			sb.append(" " + Info_Peer.local.IP);
                        sb.append(" " + sz);
			pw.println(sb.toString());
   
		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				if(socket!=null){
					socket.close();
                                /*Socket sockett = null;
                                peerSocket peersockett = new peerSocket();
                                sockett = peersockett.socket;
                                PrintWriter pww = peersockett.getWriter(sockett);
                                if(x.getP() == false)
                                {
                                    System.out.println("NO!");
                                    pww.println("NOTHING");
                                }
                                else {
                                    //TODO
                                    System.out.println("YES!");
                                    pww.println("SOMETHING");
                                }*/
			}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	
	}
	
	
	
	public static void main(String args[])throws IOException{
	    procedure peerfunction = new procedure();
	    peerfunction.intialize();
	 //   Monitor_file(Info_Peer.local.path,peerfunction);
	    ServerSocket server = null;
            
	    try{
	    	server = new ServerSocket(Info_Peer.local.serverPort);
	    	System.out.println("\n Peer  started!");
                //System.out.println(server);
	    	new PThread(server);
	    }catch(IOException e){
	    	e.printStackTrace();
	    }
		new Peer().do_it(peerfunction);

	}
}

/*
 *   Used to receive file from file client
 *   Step 1. Set up a server socket
 *   Step 2. Waiting for input data 
 */
class DThread extends Thread{
	int port;
	String fileName;
	public DThread(int port,String fileName){
		this.port = port;
		this.fileName = fileName;
		start();
	}
        
	public void run(){
		try {
			ServerSocket server = new ServerSocket(port);
			//while(true){
				Socket socket = server.accept();
                                receiveFile(socket,fileName);
                                socket.close();
                                server.close();
			//}
		} 
                catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void receiveFile(Socket socket, String fileName) throws IOException{
        byte[] inputByte = null;  
        int length = 0;
        DataInputStream dis = null;  
        FileOutputStream fos = null;
        String filePath = "./Look/" + fileName;  
        try {
            try {
                dis = new DataInputStream(socket.getInputStream());  
                File f = new File("./Look");  
                if(!f.exists()){
                    f.mkdir();
                }  

                fos = new FileOutputStream(new File(filePath));      
                inputByte = new byte[1024];
                System.out.println("\nStart receiving..."); 
                System.out.println("display file " + fileName);
                while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {  
                    fos.write(inputByte, 0, length);  
                    fos.flush();      
                }  
                System.out.println("Finish receive:"+filePath);  
            } finally {  
                if (fos != null)  
                    fos.close();  
                if (dis != null)  
                    dis.close();  
                if (socket != null)  
                    socket.close();   
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
}

/*
 *  Watch file
 *  Listening to the local file folder
 *  When there is a change, register or 
 *  unregister file in the local list.
 */
class WrThread extends Thread {
	String path = null;
	procedure peerfunction = null;

	public WrThread(String path,procedure peerfunction){
		this.path = path;
		this.peerfunction = peerfunction;
		
		start();
	}
	
	public void run(){
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(Info_Peer.local.fileList.size()!=0){
					for(int i = 0; i < Info_Peer.local.fileList.size(); i++){
						File file = new File(path + File.separator +
								Info_Peer.local.fileList.get(i));
						if(!file.exists()){
							System.out.println(Info_Peer.local.fileList.get(i)+" was removed!");
							Peer.Thread_for_unregister(Info_Peer.local.fileList.get(i),peerfunction);
							
						}
					}
				}
			}
			
		}, 1000, 100);
		     
	}
}
class peerServer{
	public ServerSocket serversocket;
	public int port;
	
	public peerServer()throws IOException{
		port = Info_Peer.local.serverPort;
		serversocket = new ServerSocket(port);
	}
	
	public peerServer(int port)throws IOException{
		this.port = port;
		serversocket = new ServerSocket(port);
	}
	
	public PrintWriter getWriter(Socket socket)throws IOException{
		OutputStream socketOut = socket.getOutputStream();
		return new PrintWriter(socketOut, true);
		
	}
	
	public BufferedReader getReader(Socket socket)throws IOException{
		InputStream socketIn = socket.getInputStream();
		return new BufferedReader(new InputStreamReader(socketIn));
		
	}
}

class peerSocket{
    
	public Socket socket=null;
	private procedure pf = new procedure();
	
	public peerSocket()throws IOException{
		pf.intialize();
		socket = new Socket(Info_Peer.local.IP,Info_Peer.local.clientPort);
	}
	
	public peerSocket(String IP, int port)throws IOException{
		pf.intialize();
		Info_Peer.local.clientPort = port;
		socket = new Socket(IP,Info_Peer.local.clientPort);
	}
	
	public PrintWriter getWriter(Socket socket)throws IOException{
		OutputStream socketOut = socket.getOutputStream();
		return new PrintWriter(socketOut, true);
		
	}
	
	public BufferedReader getReader(Socket socket)throws IOException{
		InputStream socketIn = socket.getInputStream();
		return new BufferedReader(new InputStreamReader(socketIn));
		
	}
}


import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.StringTokenizer;

/*
 *  Receiving file from other client
 */
class ReThread extends Thread {
	
	int port = 0;
	String fileName = null;
	String IP = null;
        int sz = 0;
	public ReThread(String fileName, String IP, int port, int sz){
		this.fileName = fileName;
		this.IP = IP;
		this.port = port;
                this.sz = sz;
		start();
	}
	
	public void run(){
		Socket socket = null;  
                DataOutputStream dos = null;
		int length = 0;
            double sumL = 0 ;
            byte[] sendBytes = null;  

            FileInputStream fis = null;  
            boolean bool = false;
            if(sz != -1) {

                try {
                    File file = new File("./Look/" + fileName);
                    long l = sz;
                    socket = new Socket(IP,port);                
                    dos = new DataOutputStream(socket.getOutputStream());  
                    fis = new FileInputStream(file);        
                    sendBytes = new byte[1024];   

                    while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {  
                        sumL=sumL+ length;               
                        System.out.println("Sent:"+((sumL/l)*100)+"%");
                        dos.write(sendBytes, 0, length);  
                        dos.flush();
                    }   
                    //
                    if(sumL==l){
                        bool = true;  
                    }  

                }catch (Exception e) {  
                    System.out.println("error");  
                    bool = false;  
                    e.printStackTrace();    
                }finally{    
                    if (dos != null)
                                        try {
                                                dos.close();
                                        } catch (IOException e) {
                                                e.printStackTrace();
                                        }  
                    if (fis != null)
                                        try {
                                                fis.close();
                                        } catch (IOException e) {

                                                e.printStackTrace();
                                        }     
                    if (socket != null)
                                        try {
                                                socket.close();
                                        } catch (IOException e) {

                                                e.printStackTrace();
                                        }      
                }  
            }
            System.out.println(bool?"YES!":"NO!");
        
	}
}




public class PThread extends Thread{
	private ServerSocket serversocket;
	
	public PThread(ServerSocket serversocket)throws IOException{
		super();
		this.serversocket = serversocket;
		
		start();
	}
	
	public void run(){  
	    Socket socket = null;  
          
		try{
			while(true){
	
				socket = serversocket.accept();
				new clientThread(socket);		
				
			}
		}catch(Exception e){
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
}

class clientThread extends Thread{
	
	private PrintWriter pw;
	private Socket connectToClient;
	private BufferedReader br;
	
	public clientThread(Socket soc)throws IOException{
		super();
		connectToClient = soc;
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
	
	
	public void run(){
		try{			
			String msg = null;
			
			while((msg = br.readLine())!=null){
                                String w[] = msg.split(" ", 5);
				String command = w[0];
				String fileName = w[1];
				int port = Integer.parseInt(w[2]);
				String IP = w[3];
                                int size = Integer.parseInt(w[4]);

                                if("download".equals(command)){
                                    // Create a receive file thread 
                                    Random rand = new Random();

                                    int  n = rand.nextInt(100) + 1;
                                    System.out.println(n);
                                    if(n < 50) {
                                        new ReThread(fileName, IP, port, size);
                                    }
                                    else {
                                        new ReThread("NO", IP, port, -1);
                                    }
	            	
				}
				
			}		
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}




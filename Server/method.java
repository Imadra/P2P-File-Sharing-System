

import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class method {

	private ArrayList<Info_File> registryList = new ArrayList<Info_File>();
    private Map<String, ArrayList<String>> fileSearch  = new HashMap<String, ArrayList<String>>();
    private Map<String, Integer> data = new HashMap<String,Integer> ();

//	FileWriter writer = null;
	/*
	 *  Register file on server side
	 */
	public void registery(String peerID, String fileName, String full){
		// TODO Auto-generated method stub
		registerThread register = new registerThread(peerID, fileName, full);
		Thread thread = new Thread(register);
		thread.start();
		thread = null;
	}

	/*
	 *  registerThread
	 *  Used to implement multiusers to register files at the same time
	 */
	class registerThread implements Runnable{
		private String peerID;
		private String fileName;
        private String full;

		public registerThread(String peerID, String fileName, String full){
			this.peerID = peerID;
			this.fileName = fileName;
            this.full = full;
		}
		@Override
		public void run() {
			if(registryList.size()==0){
				try{
					FileWriter writer = new FileWriter("./serverLog.txt",true);
                    registryList.add(new Info_File(peerID,fileName));
                    data.put(peerID, 0);
                    String w[] = full.split(" ", 2);
                    //System.out.println("HAHAH: " + w[0]+"+"+w[1]);
                    ArrayList<String> cur = fileSearch.get(w[0]);
                    if(cur == null)
                        cur = new ArrayList<String> ();
                    cur.add(w[1]);
                    fileSearch.put(w[0], cur);
					System.out.println("File:"+fileName+" from "+"Client:"+peerID+" is registried!");
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = df.format(new Date());
					writer.write(time + "\t\tFile "+fileName + " is registered on the index server!\r\n");
					writer.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}

			}
			else{
				try{
					//if(fileNotExist(peerID,fileName)){
						FileWriter writer = new FileWriter("./serverLog.txt",true);
						registryList.add(new Info_File(peerID,fileName));
                        data.put(peerID, 0);
                        String w[] = full.split(" ", 2);
                        //System.out.println("HAHAH: " + w[0]+"+"+w[1]);
                        ArrayList<String> cur = fileSearch.get(w[0]);
                        if(cur == null)
                            cur = new ArrayList<String> ();
                        cur.add(w[1]);
                        fileSearch.put(w[0], cur);
						System.out.println("File:"+fileName+" from "+"Client:"+peerID+" is registried!");
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String time = df.format(new Date());
						writer.write(time + "\t\tFile "+fileName + " is registered on the index server!\r\n");
						writer.close();
					//}
				}
                                catch(Exception e){
					e.printStackTrace();
				}
			}
		}

	}


	/*
	 *  Unregister file on server side
	 */
	public void unregistery(String peerID, String fileName){
		// TODO Auto-generated method stub
		unregisteryThread unregister = new unregisteryThread(peerID, fileName);
		Thread thread = new Thread(unregister);
		thread.start();
		thread = null;

	}

	/*
	 *  unregisterThread
	 *  Used to implement multiusers to unregister files at the same time
	 */
	class unregisteryThread implements Runnable{
		private String peerID;
		private String fileName;

		public unregisteryThread(String peerID, String fileName){
			this.peerID = peerID;
			this.fileName = fileName;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			for(int i=0;i<registryList.size();i++){
				try{

					if(registryList.get(i).getName().equals(fileName)&&
							registryList.get(i).getID().equals(peerID)){
						FileWriter writer = new FileWriter("./serverLog.txt",true);
						registryList.remove(i);
						System.out.println("File:"+fileName+" from "+"Client:"+peerID+" is removed!");
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String time = df.format(new Date());
						writer.write(time + "\t\tFile "+fileName + " is unregistered on the index server!\r\n");
						writer.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}

	}

	public ArrayList<String> search(String fileName) {
		//search file ID: IP + port
		ArrayList<String> peerList = new ArrayList<String>();
        int dot = fileName.lastIndexOf('.');
        String base = (dot == -1) ? fileName : fileName.substring(0, dot);
        ArrayList<String> value = fileSearch.get(base);
        ArrayList<String> res = new ArrayList<String>();
        if(value == null)
            value = new ArrayList<String> ();
        for(int i=0;i<value.size();i++) {
            String cur = value.get(i);
            res.add(cur);
        }

		return res;
	}

	class searchThread implements Callable<ArrayList<String>>{
		private ArrayList<String> peerList = new ArrayList<String>();
		private String fileName;

		public searchThread(String fileName){
			this.fileName = fileName;
		}
		@Override
		public ArrayList<String> call() throws Exception {
			// TODO Auto-generated method stub
			for(int i=0;i<registryList.size();i++){
				if(registryList.get(i).getName().equals(fileName)){
					peerList.add(registryList.get(i).getID());

				}
			}
			return peerList;
		}

	}

}





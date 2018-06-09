

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Info_Peer {
        public int NumOfRequests=0;
        public int NumOfUploads=0;
	// Store local infomation
	public static class local{
		
		public static int serverPort = 9210;
		public static int clientPort = 8010;
		public static int downloadPort = 10210;
		public static String IP = "";	
		public static String name = "";
		public static String ID = "";
                public static int requests = 0;
                public static int uploads = 0;
                public static int score = 0;
		public static String path = "./Look";
		public static ArrayList<String> fileList = new ArrayList<String>();
	}
	
	// Store destination information
	public static class dest{
		public static ArrayList<String> destList = new ArrayList<String>();
		public static String destination = "127.0.0.1:8010";
		public static ArrayList<String> destPath = new ArrayList<String>();
		public static String path = "./Look";
	}
	
	// Initialization
	public void initial(){
		Info_Peer.dest.destination = "";
		Info_Peer.dest.destList = new ArrayList<String>();
		Info_Peer.dest.destPath = new ArrayList<String>();
	}
        public int getRequests() {
            return NumOfRequests;
        }
        
        public int getUploads() {
            return NumOfUploads;
        }
        public int getScore() {
            return NumOfUploads*100/NumOfRequests;
        }
        public void addRequests(){
		this.NumOfRequests++;
	}
        public void addUploads(){
		this.NumOfUploads++;
	}
	
}

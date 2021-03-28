package Network;

import java.net.InetAddress;

public class Player {
	private InetAddress IPAddress;
	private int port;
	private int lastSeen = 0;
	private boolean justJoined = true;
	
	public Player(InetAddress IPAddress, int port){
		this.IPAddress = IPAddress;
		this.port = port;
	}
	
	public InetAddress getAddress(){
		return IPAddress;
	}
	
	public int getPort(){
		return port;
	}
	
	public void notSeen(){
		lastSeen++;
	}
	
	public int getLastSeen(){
		return lastSeen;
	}
	
	public boolean check(InetAddress a){
		if(IPAddress.equals(a)){
			return true;
		}
		return false;
	}
	
	public boolean getJustJoined(){
		return justJoined;
	}
	
	public void setJustJoined(boolean b){
		justJoined = b;
	}
}

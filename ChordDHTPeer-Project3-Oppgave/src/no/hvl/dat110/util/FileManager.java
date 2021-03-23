package no.hvl.dat110.util;

/**
 * @author tdoy
 * dat110 - project 3
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import no.hvl.dat110.middleware.Message;
import no.hvl.dat110.rpc.interfaces.NodeInterface;
import no.hvl.dat110.util.Hash;

/**
 * 
 * @author vilde
 *
 */
public class FileManager {

	private BigInteger[] replicafiles; // array stores replicated files for distribution to matching nodes
	private int numReplicas; // let's assume each node manages nfiles (5 for now) - can be changed from the
								// constructor
	private NodeInterface chordnode;
	private String filepath; // absolute filepath
	private String filename; // only filename without path and extension
	private BigInteger hash;
	private byte[] bytesOfFile;
	private String sizeOfByte;
	private Set<Message> activeNodesforFile = null;

	public FileManager(NodeInterface chordnode) throws RemoteException {
		this.chordnode = chordnode;
	}

	public FileManager(NodeInterface chordnode, int N) throws RemoteException {
		this.numReplicas = N;
		replicafiles = new BigInteger[N];
		this.chordnode = chordnode;
	}

	public FileManager(NodeInterface chordnode, String filepath, int N) throws RemoteException {
		this.filepath = filepath;
		this.numReplicas = N;
		replicafiles = new BigInteger[N];
		this.chordnode = chordnode;
	}

	public void createReplicaFiles() {

		// implement

		// set a loop where size = numReplicas
		for (int i = 0; i < numReplicas; i++) {
			// replicate by adding the index to filename
			filename = filename+String.valueOf(i);
			
			// hash the replica
			Hash hashFile = new Hash();
			hash = hashFile.hashOf(filename);
			
			// store the hash in the replicafiles array.
			replicafiles[i] = hash;
			
	}

	}

	/**
	 * 
	 * @param bytesOfFile
	 * @throws RemoteException
	 */
	public int distributeReplicastoPeers() throws RemoteException {
		int counter = 0;

		// Task1: Given a filename, make replicas and distribute them to all active
		// peers such that: pred < replica <= peer
		
		// Task2: assign a replica as the primary for this file. Hint, see the slide
		// (project 3) on Canvas
		Random rnd = new Random();
		int index = rnd.nextInt(Util.numReplicas-1);
		
		//Key(file) = hash(file), der hash står sto egentlig fileId
		hash = Hash.hashOf(filename);
		
		if (counter == index)
			chordnode.saveFileContent(filename, hash, bytesOfFile, true); //save the file in the memory of the peer
		else 
			chordnode.saveFileContent(filename, hash, bytesOfFile, false);

		// create replicas of the filename
		BigInteger replica 
		
		// iterate over the replicas

		
		// for each replica, find its successor by performing findSuccessor(replica)
		for (int i = 0; i < replicafiles.length; i++) {
		chordnode = chordnode.findSuccessor(replicafiles[i]);
		// call the addKey on the successor and add the replica
		chordnode.addKey(id);
		// call the saveFileContent() on the successor
		
		}
		
		

		// increment counter
		counter++;

		return counter;
	}

	/**
	 * 
	 * @param filename
	 * @return list of active nodes having the replicas of this file
	 * @throws RemoteException
	 */
	public Set<Message> requestActiveNodesForFile(String filename) throws RemoteException {

		this.filename = filename;
		Set<Message> succinfo = new HashSet<Message>();
		// Task: Given a filename, find all the peers that hold a copy of this file

		// generate the N replicas from the filename by calling createReplicaFiles()

		// it means, iterate over the replicas of the file

		// for each replica, do findSuccessor(replica) that returns successor s.

		// get the metadata (Message) of the replica from the successor, s (i.e. active
		// peer) of the file

		// save the metadata in the set succinfo.

		this.activeNodesforFile = succinfo;

		return succinfo;
	}

	/**
	 * Find the primary server - Remote-Write Protocol
	 * 
	 * @return
	 */
	public NodeInterface findPrimaryOfItem() {

		// Task: Given all the active peers of a file (activeNodesforFile()), find which
		// is holding the primary copy

		// iterate over the activeNodesforFile

		// for each active peer (saved as Message)

		// use the primaryServer boolean variable contained in the Message class to
		// check if it is the primary or not

		// return the primary

		return null;
	}

	/**
	 * Read the content of a file and return the bytes
	 * 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public void readFile() throws IOException, NoSuchAlgorithmException {

		File f = new File(filepath);

		byte[] bytesOfFile = new byte[(int) f.length()];

		FileInputStream fis = new FileInputStream(f);

		fis.read(bytesOfFile);
		fis.close();

		// set the values
		filename = f.getName().replace(".txt", "");
		hash = Hash.hashOf(filename);
		this.bytesOfFile = bytesOfFile;
		double size = (double) bytesOfFile.length / 1000;
		NumberFormat nf = new DecimalFormat();
		nf.setMaximumFractionDigits(3);
		sizeOfByte = nf.format(size);

		System.out.println("filename=" + filename + " size=" + sizeOfByte);

	}

	public void printActivePeers() {

		activeNodesforFile.forEach(m -> {
			String peer = m.getNodeIP();
			String id = m.getNodeID().toString();
			String name = m.getNameOfFile();
			String hash = m.getHashOfFile().toString();
			int size = m.getBytesOfFile().length;

			System.out.println(
					peer + ": ID = " + id + " | filename = " + name + " | HashOfFile = " + hash + " | size =" + size);

		});
	}

	/**
	 * @return the numReplicas
	 */
	public int getNumReplicas() {
		return numReplicas;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the hash
	 */
	public BigInteger getHash() {
		return hash;
	}

	/**
	 * @param hash the hash to set
	 */
	public void setHash(BigInteger hash) {
		this.hash = hash;
	}

	/**
	 * @return the bytesOfFile
	 */
	public byte[] getBytesOfFile() {
		return bytesOfFile;
	}

	/**
	 * @param bytesOfFile the bytesOfFile to set
	 */
	public void setBytesOfFile(byte[] bytesOfFile) {
		this.bytesOfFile = bytesOfFile;
	}

	/**
	 * @return the size
	 */
	public String getSizeOfByte() {
		return sizeOfByte;
	}

	/**
	 * @param size the size to set
	 */
	public void setSizeOfByte(String sizeOfByte) {
		this.sizeOfByte = sizeOfByte;
	}

	/**
	 * @return the chordnode
	 */
	public NodeInterface getChordnode() {
		return chordnode;
	}

	/**
	 * @return the activeNodesforFile
	 */
	public Set<Message> getActiveNodesforFile() {
		return activeNodesforFile;
	}

	/**
	 * @return the replicafiles
	 */
	public BigInteger[] getReplicafiles() {
		return replicafiles;
	}

	/**
	 * @param filepath the filepath to set
	 */
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
}

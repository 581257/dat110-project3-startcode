/**
 * 
 */
package no.hvl.dat110.middleware;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.hvl.dat110.rpc.interfaces.NodeInterface;
import no.hvl.dat110.util.Hash;
import no.hvl.dat110.util.Util;

/**
 * @author tdoy
 *
 */
public class ChordLookup {

	private Node node;

	public ChordLookup(Node node) {
		this.node = node;
	}

	/**
	 * 
	 * @param key
	 * @return
	 * @throws RemoteException
	 * @author ehell
	 */
	public NodeInterface findSuccessor(BigInteger key) throws RemoteException {

		NodeInterface highest_pred;

		// ask this node to find the successor of key
		// Gjøres i Node-klassen.

		// get the successor of the node

		NodeInterface successor = this.node.getSuccessor();

		// get the stub for this successor (Util.getProcessStub())

		NodeInterface stub = Util.getProcessStub(successor.getNodeName(), successor.getPort());

		// check that key is a member of the set {nodeid+1,...,succID} i.e. (nodeid+1 <=
		// key <= succID) using the ComputeLogic
		
		BigInteger one = BigInteger.valueOf(1);

		if (Util.computeLogic(key, this.node.getNodeID().add(one), successor.getNodeID())) {
			// if logic returns true, then return the successor
			return stub;
		} else {
			// if logic returns false; call findHighestPredecessor(key)
			highest_pred = findHighestPredecessor(key);
		}

		// do return highest_pred.findSuccessor(key) - This is a recursive call until
		// logic returns true

		return highest_pred.findSuccessor(key);
	}

	/**
	 * This method makes a remote call. Invoked from a local client
	 * 
	 * @param ID BigInteger
	 * @return
	 * @throws RemoteException
	 */
	private NodeInterface findHighestPredecessor(BigInteger key) throws RemoteException {

		// collect the entries in the finger table for this node

		List<NodeInterface> fingerTable = this.node.getFingerTable();

		// starting from the last entry, iterate over the finger table

		for (int i = fingerTable.size() - 1; i >= 0; i--) {

			// for each finger, obtain a stub from the registry
			NodeInterface stub = Util.getProcessStub(fingerTable.get(i).getNodeName(), fingerTable.get(i).getPort());

			BigInteger one = BigInteger.valueOf(1);
			// check that finger is a member of the set {nodeID+1,...,ID-1} i.e. (nodeID+1
			// <= finger <= key-1) using the ComputeLogic
			if (Util.computeLogic(stub.getNodeID(), this.node.getNodeID().add(one),
					key.subtract(one))) {
				// if logic returns true, then return the finger (means finger is the closest to
				// key)
				return stub;
			}
		}

		return (NodeInterface) node;
	}

	public void copyKeysFromSuccessor(NodeInterface succ) {

		Set<BigInteger> filekeys;
		try {
			// if this node and succ are the same, don't do anything
			if (succ.getNodeName().equals(node.getNodeName()))
				return;

			System.out.println("copy file keys that are <= " + node.getNodeName() + " from successor "
					+ succ.getNodeName() + " to " + node.getNodeName());

			filekeys = new HashSet<>(succ.getNodeKeys());
			BigInteger nodeID = node.getNodeID();
			BigInteger succID = succ.getNodeID();

			for (BigInteger fileID : filekeys) {
				// a small modification here if node > succ. We need to make sure the keys
				// copied are only lower than succ
				if (succ.getNodeID().compareTo(nodeID) == -1) {
					if (fileID.compareTo(succID) == -1 || fileID.compareTo(succID) == 0) {
						BigInteger addresssize = Hash.addressSize();
						fileID = fileID.add(addresssize);
					}
				}
				// if fileID <= nodeID, copy the file to the newly joined node.
				if (fileID.compareTo(nodeID) == -1 || fileID.compareTo(nodeID) == 0) {
					System.out.println("fileID=" + fileID + " | nodeID= " + nodeID);
					node.addKey(fileID); // re-assign file to this successor node
					Message msg = succ.getFilesMetadata().get(fileID);
					node.saveFileContent(msg.getNameOfFile(), fileID, msg.getBytesOfFile(), msg.isPrimaryServer()); // save
																													// the
																													// file
																													// in
																													// memory
																													// of
																													// the
																													// newly
																													// joined
																													// node
					succ.removeKey(fileID); // remove the file key from the successor
					succ.getFilesMetadata().remove(fileID); // also remove the saved file from memory
				}
			}

			System.out.println(
					"Finished copying file keys from successor " + succ.getNodeName() + " to " + node.getNodeName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void notify(NodeInterface pred_new) throws RemoteException {

		NodeInterface pred_old = node.getPredecessor();

		// if the predecessor is null accept the new predecessor
		if (pred_old == null) {
			node.setPredecessor(pred_new); // accept the new predecessor
			return;
		}

		else if (pred_new.getNodeName().equals(node.getNodeName())) {
			node.setPredecessor(null);
			return;
		} else {
			BigInteger nodeID = node.getNodeID();
			BigInteger pred_oldID = pred_old.getNodeID();

			BigInteger pred_newID = pred_new.getNodeID();

			// check that pred_new is between pred_old and this node, accept pred_new as the
			// new predecessor
			// check that ftsuccID is a member of the set {nodeID+1,...,ID-1}
			boolean cond = Util.computeLogic(pred_newID, pred_oldID.add(new BigInteger("1")),
					nodeID.add(new BigInteger("1")));
			if (cond) {
				node.setPredecessor(pred_new); // accept the new predecessor
			}
		}
	}

	public void leaveRing() throws RemoteException {

		System.out.println("Attempting to update successor and predecessor before leaving the ring...");

		try {

			NodeInterface prednode = node.getPredecessor(); // get the predecessor
			NodeInterface succnode = node.getSuccessor(); // get the successor
			NodeInterface prednodestub = Util.getProcessStub(prednode.getNodeName(), prednode.getPort()); // get the
																											// prednode
																											// stub
			NodeInterface succnodestub = Util.getProcessStub(succnode.getNodeName(), succnode.getPort()); // get the
																											// succnode
																											// stub
			Set<BigInteger> keyids = node.getNodeKeys(); // get the keys for chordnode

			if (succnodestub != null) { // add chordnode's keys to its successor
				keyids.forEach(fileID -> {
					try {
						System.out.println("Adding fileID = " + fileID + " to " + succnodestub.getNodeName());
						succnodestub.addKey(fileID);
						Message msg = node.getFilesMetadata().get(fileID);
						succnodestub.saveFileContent(msg.getNameOfFile(), fileID, msg.getBytesOfFile(),
								msg.isPrimaryServer()); // save the file in memory of the newly joined node
					} catch (RemoteException e) {
						// e.printStackTrace();
					}
				});

				succnodestub.setPredecessor(prednodestub); // set prednode as the predecessor of succnode
			}
			if (prednodestub != null) {
				prednodestub.setSuccessor(succnodestub); // set succnode as the successor of prednode
			}
		} catch (Exception e) {
			//
			System.out.println("some errors while updating succ/pred/keys...");
		}
		System.out.println("Update of successor and predecessor completed...bye!");
	}

}

package com.grimco.loadout.capability;

import java.util.Set;

public interface ILoadout
{
	/**
	 * Returns True if the given Loadout Key has previously been received.
	 * @param loadout
	 * 			Loadout Key as defined by the loadout.json
	 * @return If the loadout has been received
	 */
	boolean receivedLoadout(String loadout);
	
	/**
	 * Flag's the given Loadout Key as being received
	 *
	 * @param loadout
	 * 			Loadout Key, as defined by the loadout.json
	 */
	void setLoadout(String loadout);
	
	/**
	 * Returns all Loadouts received by the Entity
	 * @return returns a Set of received Loadouts
	 */
	Set<String> getLoadouts();
	
	/**
	 * Resets all given Loadouts on the player
	 */
	void resetLoadouts();
}

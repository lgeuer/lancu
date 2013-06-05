/**
 * File: IdentifyAdapter.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.util.id;

public abstract class IdentifyAdapter implements Identifiable {

	private int id = IdFactory.VOID;

	public void setId(int anId) {

		id = anId;
	}

	@Override
	public int getId() {

		return id;
	}
}

/**
 * File: IdFactory.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.util.id;

import java.io.Serializable;

public class IdFactory implements Serializable {

	private static final long serialVersionUID = 7081989892187555080L;

	public static final int VOID = Integer.MIN_VALUE;

	private int start;

	private int nextId;

	public IdFactory() {

		start = 0;
		nextId = 0;
	}

	public IdFactory(int startId) {

		start = startId;
		nextId = startId;
	}

	public int getId() {

		return nextId++;
	}

	public void reset() {

		nextId = start;
	}

	public int getStartId() {

		return start;
	}

	public int getLastId() {

		return nextId - 1;
	}
}
/*
 * Copyright 2011 Kazuyuki Shudo, and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ow.routing.dlg.message;

import ow.id.IDAddressPair;
import ow.messaging.Message;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class RepSuccAndPredMessage extends Message {
	public final static String NAME = "REP_SUCC_AND_PRD";
	public final static boolean TO_BE_REPORTED = true;
	public final static Color COLOR = null;

	// message members
	public IDAddressPair[] successors;
	public IDAddressPair[] predecessors;
	public String newId;

	public RepSuccAndPredMessage() { super(); }	// for Class#newInstance()

	public RepSuccAndPredMessage(
			IDAddressPair[] successors, IDAddressPair[] predecessors, String newId) {
		this.successors = successors;
		this.predecessors = predecessors;
		this.newId = newId;
	}

	public void encodeContents(ObjectOutputStream oos) throws IOException {
		oos.writeObject(this.successors);
		oos.writeObject(this.predecessors);
		oos.writeObject(this.newId);
	}

	public void decodeContents(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		this.successors = (IDAddressPair[])ois.readObject();
		this.predecessors = (IDAddressPair[])ois.readObject();
		this.newId = (String)ois.readObject();
	}
}

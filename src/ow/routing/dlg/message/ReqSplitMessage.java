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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class ReqSplitMessage extends Message {
	public final static String NAME = "REQ_SPLIT";
	public final static boolean TO_BE_REPORTED = true;
	public final static Color COLOR = null;
	// message members
	public IDAddressPair[] successors;
	public IDAddressPair[] predecessors;
	public String newID;

	public void encodeContents(ObjectOutputStream oos) {}

	public void decodeContents(ObjectInputStream ois) {}
}

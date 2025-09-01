package io.github.steaf23.bingoreloaded.api.network.packets;

import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface DataWriter<WriteFrom> {

	void write(WriteFrom from, DataOutputStream to) throws IOException;

	class Empty implements DataWriter<Object> {

		@Override
		public void write(Object o, DataOutputStream to) {
		}
	}
}

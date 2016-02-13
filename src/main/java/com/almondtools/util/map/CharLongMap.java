package com.almondtools.util.map;

import static com.almondtools.util.map.HashFunction.NULL;

import java.util.Map;

public class CharLongMap {

	private HashFunction h;
	private char[] keys;
	private long[] values;
	private long defaultValue;

	public CharLongMap(HashFunction h, Map<Character, Long> map, Long defaultValue) {
		this.h = h;
		this.defaultValue = defaultValue;
		computeKeysAndValues(map);
	}

	private void computeKeysAndValues(Map<Character, Long> map) {
		int len = map.size();
		if (h == NULL) {
			keys = new char[len];
			Character[] objectkeys = map.keySet().toArray(new Character[0]);
			for (int i = 0; i < objectkeys.length; i++) {
				keys[i] = objectkeys[i];
			}
		} else if (len == 0) {
			keys = new char[1];
			values = new long[1];
			values[0] = defaultValue;
		} else {
			keys = new char[len];
			values = new long[len];
			for (Map.Entry<Character, Long> entry : map.entrySet()) {
				char key = entry.getKey();
				long value = entry.getValue();

				int i = h.hash(key);

				keys[i] = key;
				values[i] = value;
			}
		}
	}

	public char[] keys() {
		return keys;
	}

	public long get(char value) {
		int i = h.hash(value);
		if (i >= keys.length) {
			return defaultValue;
		} else if (keys[i] == value) {
			return values[i];
		} else {
			return defaultValue;
		}
	}

	public long getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("{\n");
		if (keys.length > 0) {
			char key = keys[0];
			long value = get(key);
			buffer.append(key).append(": ").append(value);
			
		}
		for (int i = 1; i < keys.length; i++) {
			char key = keys[i];
			long value = get(key);
			buffer.append(",\n").append(key).append(": ").append(value);
		}
		buffer.append("\n}");
		return buffer.toString();
	}

	public static class Builder extends MinimalPerfectMapBuilder<Character, Long, CharLongMap> {

		public Builder(Long defaultValue) {
			super(defaultValue);
		}

		public CharLongMap perfectMinimal() {
			try {
				computeFunctions(100, 1.15);
				return new CharLongMap(getH(), getEntries(), getDefaultValue());
			} catch (HashBuildException e) {
				return new Fallback(getEntries(), getDefaultValue());
			}
		}

	}

	private static class Fallback extends CharLongMap {

		private Map<Character, Long> map;

		public Fallback(Map<Character, Long> map, Long defaultValue) {
			super(NULL, map, defaultValue);
			this.map = map;
		}

		@Override
		public long get(char key) {
			Long value = map.get(key);
			if (value == null) {
				return getDefaultValue();
			} else {
				return value;
			}
		}

	}
}

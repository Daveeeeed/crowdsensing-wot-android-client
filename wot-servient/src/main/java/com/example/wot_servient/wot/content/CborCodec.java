package com.example.wot_servient.wot.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

/**
 * (De)serializes data in CBOR format.
 */
public class CborCodec extends JsonCodec {

	private final ObjectMapper mapper = new ObjectMapper(new CBORFactory());

	@Override
	public String getMediaType() {
		return "application/cbor";
	}

	@Override
	protected ObjectMapper getMapper() {
		return mapper;
	}
}

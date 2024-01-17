package com.jwell.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.jwell.suite.model.ScannMessage;

import java.io.IOException;

public class MessageAdapter extends TypeAdapter<ScannMessage> {

    @Override
    public void write(JsonWriter out, ScannMessage value) throws IOException {
        // TODO: Writer implementation
    }

    @Override
    public ScannMessage read(JsonReader in) throws IOException {
        if(in.peek() != JsonToken.NULL) {
            return fromJson(in.nextString());
        } else {
            in.nextNull();
            return null;
        }
    }

}

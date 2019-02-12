package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;


/**
 * not thread-safe
 */
public class Match {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Boolean bool;
    private final String data;
    private final Encoder encoder;
    public final int leftover;
    public final int abend;
    public final int ttotal;
    public final int tmatch;

    private JsonNode rootNode;
    private JsonMatchResult matchResult;


    Match(Boolean bool, int leftover, int abend, int ttotal, int tmatch) {
        this.encoder = Encoder.BOOL;
        this.bool = bool;
        this.data = null;
        this.leftover = leftover;
        this.abend = abend;
        this.ttotal = ttotal;
        this.tmatch = tmatch;
    }

    Match(String encoder, String data, int leftover, int abend, int ttotal, int tmatch) {
        this.encoder = Encoder.valueOf(encoder.toUpperCase());
        this.bool = null;
        this.data = data;
        this.leftover = leftover;
        this.abend = abend;
        this.ttotal = ttotal;
        this.tmatch = tmatch;
    }


    public boolean matches() {
        if (encoder == Encoder.BOOL) {
            return bool;
        } else if (encoder == Encoder.JSON) {
            return jsonMatchResult() != null;
        }
        return data != null;
    }

    public String match() {
        if (encoder == Encoder.JSON) {
            return jsonMatchResult() != null ? jsonMatchResult().match() : null;
        }
        return data;
    }

    public MatchResult jsonMatchResult() {
        if (matchResult == null && rootNode() != null) {
            matchResult = new JsonMatchResult(rootNode());
        }
        return matchResult;
    }


    private JsonNode rootNode() {
        if (encoder != Encoder.JSON) {
            throw new RosieException("Encoder is not JSON");
        }
        if (rootNode == null && data != null) {
            try {
                rootNode = objectMapper.readTree(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return rootNode;
    }


    private static class JsonMatchResult implements MatchResult {
        private JsonNode jsonNode;
        private List<MatchResult> subs;

        private JsonMatchResult(JsonNode jsonNode) {
            Objects.requireNonNull(jsonNode);
            this.jsonNode = jsonNode;
        }

        @Override
        public String match() {
            return jsonNode.get("data").textValue();
        }

        @Override
        public int start() {
            return jsonNode.get("s").asInt(0) - 1;
        }

        @Override
        public int end() {
            return jsonNode.get("e").asInt(0) - 1;
        }

        @Override
        public String type() {
            return jsonNode.get("type").textValue();
        }

        @Override
        public List<MatchResult> subs() {
            if (subs == null && jsonNode.get("subs") != null) {
                List<MatchResult> list = new ArrayList<>();
                Iterator<JsonNode> subs = jsonNode.get("subs").elements();
                while (subs.hasNext()) {
                    JsonNode node = subs.next();
                    MatchResult sub = new JsonMatchResult(node);
                    list.add(sub);
                }
                this.subs = Collections.unmodifiableList(list);
            }
            return subs;
        }
    }


    private enum Encoder {
        BOOL, LINE, COLOR, JSON
    }
}

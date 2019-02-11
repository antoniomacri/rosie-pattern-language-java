package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


/**
 * not thread-safe
 */
public class Match implements MatchResult {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Boolean bool;
    private final String data;
    private final Encoder encoder;
    public final int leftover;
    public final int abend;
    public final int ttotal;
    public final int tmatch;

    private JsonNode rootNode;
    private PartialMatchResult matchResult;


    private Match(Boolean bool, int leftover, int abend, int ttotal, int tmatch) {
        this.encoder = Encoder.BOOL;
        this.bool = bool;
        this.data = null;
        this.leftover = leftover;
        this.abend = abend;
        this.ttotal = ttotal;
        this.tmatch = tmatch;
    }

    private Match(String encoder, String data, int leftover, int abend, int ttotal, int tmatch) {
        this.encoder = Encoder.valueOf(encoder.toUpperCase());
        this.bool = null;
        this.data = data;
        this.leftover = leftover;
        this.abend = abend;
        this.ttotal = ttotal;
        this.tmatch = tmatch;
    }

    static Match bool(boolean value, int leftover, int abend, int ttotal, int tmatch) {
        return new Match(value, leftover, abend, ttotal, tmatch);
    }

    static Match text(String encoder, String data, int leftover, int abend, int ttotal, int tmatch) {
        return new Match(encoder, data, leftover, abend, ttotal, tmatch);
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

    private PartialMatchResult matchResult() {
        if (matchResult == null && rootNode() != null) {
            matchResult = new PartialMatchResult(rootNode());
        }
        return matchResult;
    }


    public boolean matches() {
        if (encoder == Encoder.BOOL) {
            return bool;
        } else if (encoder == Encoder.JSON) {
            return matchResult() != null;
        }
        return data != null;
    }

    @Override
    public String match() {
        if (encoder == Encoder.JSON) {
            return matchResult() != null ? matchResult().match() : null;
        }
        return data;
    }

    @Override
    public int start() {
        return matchResult() != null ? matchResult().start() : -1;
    }

    @Override
    public int end() {
        return matchResult() != null ? matchResult().end() : -1;
    }

    @Override
    public String type() {
        return matchResult() != null ? matchResult().type() : null;
    }

    @Override
    public List<MatchResult> subs() {
        return matchResult() != null ? matchResult().subs() : null;
    }


    private static class PartialMatchResult implements MatchResult {
        private JsonNode jsonNode;
        private List<MatchResult> subs;

        private PartialMatchResult(JsonNode jsonNode) {
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
                    MatchResult sub = new PartialMatchResult(node);
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

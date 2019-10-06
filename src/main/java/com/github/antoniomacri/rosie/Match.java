package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.IOException;
import java.util.*;


/**
 * An object describing a match result.
 * <p>
 * Note: this class is not thread-safe.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Match {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Encoder encoder;

    private final Boolean bool;
    private final String data;
    private final int leftover;
    private final int abend;
    private final int ttotal;
    private final int tmatch;

    private JsonNode rootNode;
    private JsonMatchResult matchResult;


    static Match failed(int leftover, int abend, int ttotal, int tmatch) {
        return builder()
                .encoder(Encoder.BOOL).bool(false)
                .leftover(leftover).abend(abend).ttotal(ttotal).tmatch(tmatch)
                .build();
    }

    static Match noData(int leftover, int abend, int ttotal, int tmatch) {
        return builder()
                .encoder(Encoder.BOOL).bool(true)
                .leftover(leftover).abend(abend).ttotal(ttotal).tmatch(tmatch)
                .build();
    }

    static Match withData(String encoder, String data, int leftover, int abend, int ttotal, int tmatch) {
        return builder()
                .encoder(Encoder.valueOf(encoder.toUpperCase())).data(data)
                .leftover(leftover).abend(abend).ttotal(ttotal).tmatch(tmatch)
                .build();
    }


    /**
     * Indicates whether the match ended abnormally by encountering an RPL error pattern.
     */
    public boolean isAborted() {
        return abend != 0;
    }

    /**
     * When the match succeeded, indicates the number of bytes left unmatched.
     */
    public int getRemainingBytes() {
        return leftover;
    }

    /**
     * The number of microseconds spent in the call.
     * <p>
     * Notice this is subject to the platform's clock resolution.
     */
    public int getTotalMillis() {
        return ttotal;
    }

    /**
     * The number of microseconds spent actually doing the matching.
     * <p>
     * The value returned by {@link #getTotalMillis()} includes also time spent encoding the results to produce data.
     */
    public int getMatchMillis() {
        return tmatch;
    }


    public boolean matches() {
        if (encoder == Encoder.BOOL) {
            return bool;
        } else if (encoder == Encoder.JSON) {
            return jsonMatchResult() != null;
        }
        return data != null;
    }

    /**
     * A string encoding of the results.
     */
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
                rootNode = OBJECT_MAPPER.readTree(data);
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

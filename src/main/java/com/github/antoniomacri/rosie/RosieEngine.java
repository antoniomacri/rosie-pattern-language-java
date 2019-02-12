package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.antoniomacri.rosie.lib.RosieLib;
import com.github.antoniomacri.rosie.lib.RosieMatch;
import com.github.antoniomacri.rosie.lib.RosieString;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


/**
 * Create a Rosie pattern matching engine.
 * <p>
 * A Rosie pattern matching engine is used to load/import RPL code
 * (patterns) and to do matching. Create as many engines as you need.
 */
public class RosieEngine implements Closeable {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /*
    # Problem: Can't make use of RosieLibd-level defaults because we create a new RosieLibd instance for every engine.
    #
    # Observation: The librosie implementation currently allows just one
    #   ROSIE_HOME (globally).  This fixes the values of ROSIE_LIBDIR,
    #   ROSIE_VERSION, and RPL_VERSION.  So the only default that can
    #   matter on a per-engine basis is ROSIE_LIBPATH.
    #
    # Solution:
    # - We could store that default here in Python, and set it each time we create a new engine.

    # TODO:
    # - Create a rosie class
    # - Move config() to the rosie class
    # - Define setlibpath for this class
    # - When creating a new engine, set the engine's libpath (needed functionality) and the rosie
    #   libpath (so that the config() method will show the right values).
    #
    # TO BE CONTINUED.
   */


    private Pointer engine;


    public RosieEngine() {
        try (RosieString errors = RosieString.create()) {
            engine = RosieLib.rosie_new(errors);
            if (engine == Pointer.NULL) {
                throw new RuntimeException(errors.toString());
            }
        }
    }

    public Pattern compile(String exp) throws RosieException {
        try (RosieString errors = RosieString.create(); RosieString rosieExpression = RosieString.create(exp)) {
            IntByReference pat = new IntByReference();
            int ok = RosieLib.rosie_compile(engine, rosieExpression, pat, errors);
            if (ok != 0) {
                throw new RuntimeException("compile() failed (please report this as a bug)");
            }
            if (pat.getValue() == 0 || hasErrors(errors)) {
                throw new RosieException("Errors reported", errors.toString());
            }
            return new Pattern(pat.getValue());
        }
    }


    //region Functions for loading statements/blocks/packages into an engine

    public String load(String src) {
        try (RosieString rosieSrc = RosieString.create(src); RosieString rosiePkgname = RosieString.create(); RosieString errors = RosieString.create()) {
            IntByReference ok = new IntByReference();
            int result = RosieLib.rosie_load(engine, ok, rosieSrc, rosiePkgname, errors);
            if (result != 0) {
                throw new RuntimeException("load() failed (please report this as a bug)");
            }
            if (ok.getValue() == 0 || hasErrors(errors)) {
                throw new RosieException("Errors reported", errors.toString());
            }
            return rosiePkgname.toString();
        }
    }

    public String loadfile(String fn) {
        try (RosieString Cerrs = RosieString.create(); RosieString Cfn = RosieString.create(fn); RosieString Cpkgname = RosieString.create()) {
            IntByReference Csuccess = new IntByReference();
            int ok = RosieLib.rosie_loadfile(engine, Csuccess, Cfn, Cpkgname, Cerrs);
            if (ok != 0) {
                throw new RuntimeException("loadfile() failed (please report this as a bug)");
            }
            if (Csuccess.getValue() == 0 || hasErrors(Cerrs)) {
                throw new RosieException("Errors reported", Cerrs.toString());
            }
            return Cpkgname.toString();
        }
    }

    public String importPackage(String pkgname) {
        return importPackage(pkgname, null);
    }

    public String importPackage(String pkgname, String as_name) {
        try (RosieString Cerrs = RosieString.create(); RosieString Cas_name = as_name != null ? RosieString.create(as_name) : null;
             RosieString Cpkgname = RosieString.create(pkgname); RosieString Cactual_pkgname = RosieString.create()) {
            IntByReference Csuccess = new IntByReference();
            int ok = RosieLib.rosie_import(engine, Csuccess, Cpkgname, Cas_name, Cactual_pkgname, Cerrs);
            if (ok != 0) {
                throw new RuntimeException("import() failed (please report this as a bug)");
            }
            if (Csuccess.getValue() == 0 || hasErrors(Cerrs)) {
                throw new RosieException("Errors reported", Cerrs.toString());
            }
            return Cactual_pkgname.toString();
        }
    }

    //endregion

    //region Functions for matching and tracing (debugging)

    /**
     * @param pattern
     * @param input
     * @param start   0-based index
     * @param encoder
     * @return
     */
    public Match match(Pattern pattern, String input, int start, String encoder) {
        try (RosieString Cinput = RosieString.create(input)) {
            RosieMatch Cmatch = new RosieMatch();
            int ok = RosieLib.rosie_match(engine, pattern.getPat(), start + 1, encoder, Cinput, Cmatch);
            if (ok != 0) {
                throw new RuntimeException("match() failed (please report this as a bug)");
            }

            int left = Cmatch.leftover;
            int abend = Cmatch.abend;
            int ttotal = Cmatch.ttotal;
            int tmatch = Cmatch.tmatch;
            if (Cmatch.data.ptr == null) {
                if (Cmatch.data.len.intValue() == 0) {
                    return new Match(false, left, abend, ttotal, tmatch);
                } else if (Cmatch.data.len.intValue() == 1) {
                    return new Match(true, left, abend, ttotal, tmatch);
                } else if (Cmatch.data.len.intValue() == 2) {
                    throw new IllegalArgumentException("invalid output encoder");
                } else if (Cmatch.data.len.intValue() == 4) {
                    throw new IllegalStateException("invalid compiled pattern");
                }
            }
            return new Match(encoder, Cmatch.data.toString(), left, abend, ttotal, tmatch);
        }
    }

    public TraceResult trace(Pattern pattern, String input, int start, String style) {
        try (RosieString Cinput = RosieString.create(input); RosieString Ctrace = RosieString.create()) {
            IntByReference Cmatched = new IntByReference();
            int ok = RosieLib.rosie_trace(engine, pattern.getPat(), start, style, Cinput, Cmatched, Ctrace);
            if (ok != 0) {
                throw new RuntimeException("trace() failed (please report this as a bug): " + Ctrace.toString());
            }

            if (Ctrace.ptr == null) {
                if (Ctrace.len.intValue() == 2) {
                    throw new IllegalArgumentException("invalid trace style");
                } else if (Ctrace.len.intValue() == 1) {
                    throw new IllegalStateException("invalid compiled pattern");
                }
            }
            boolean matched = Cmatched.getValue() != 0;
            String trace = Ctrace.toString();
            return new TraceResult(matched, trace);
        }
    }

    public MatchFileResult matchfile(Pattern pattern, String encoder, String infile, String outfile, String errfile) {
        return matchfile(pattern.getPat(), encoder, infile, outfile, errfile, false);
    }

    public MatchFileResult matchfile(Pattern pattern, String encoder, String infile, String outfile, String errfile, boolean wholefile) {
        return matchfile(pattern.getPat(), encoder, infile, outfile, errfile, wholefile);
    }

    private MatchFileResult matchfile(int Cpat, String encoder, String infile, String outfile, String errfile, boolean wholefile) {
        if (Cpat == 0) {
            throw new IllegalArgumentException("invalid compiled pattern");
        }

        try (RosieString Cerrmsg = RosieString.create()) {
            IntByReference Ccin = new IntByReference();
            IntByReference Ccout = new IntByReference();
            IntByReference Ccerr = new IntByReference();
            int wff = wholefile ? 1 : 0;

            int ok = RosieLib.rosie_matchfile(engine, Cpat, encoder, wff,
                    infile != null ? infile : "",
                    outfile != null ? outfile : "",
                    errfile != null ? errfile : "",
                    Ccin, Ccout, Ccerr, Cerrmsg);

            if (ok != 0) {
                throw new RuntimeException("matchfile() failed: " + Cerrmsg.toString());
            }
            if (Ccin.getValue() == -1) { // Error occurred
                if (Ccout.getValue() == 2) {
                    throw new IllegalArgumentException("invalid encoder");
                } else if (Ccout.getValue() == 3) {
                    throw new RuntimeException(Cerrmsg.toString()); // file i/o error
                } else if (Ccout.getValue() == 4) {
                    throw new IllegalStateException("invalid compiled pattern (already freed?)");
                } else {
                    throw new RuntimeException("unknown error caused matchfile to fail");
                }
            }

            return new MatchFileResult(Ccin.getValue(), Ccout.getValue(), Ccerr.getValue());
        }
    }

    //endregion

    //region Functions for reading and processing rcfile (init file) contents

    public ReadRcFileResult readRcFile(String filename) throws IOException {
        IntByReference Cfile_exists = new IntByReference();
        RosieString filenameArg = filename == null ? RosieString.create() : RosieString.create(filename);
        RosieString Coptions = RosieString.create();
        RosieString Cmessages = RosieString.create();
        int ok = RosieLib.rosie_read_rcfile(engine, filenameArg, Cfile_exists, Coptions, Cmessages);
        if (ok != 0) {
            throw new RuntimeException("read_rcfile() failed (please report this as a bug)");
        }
        String messages = Cmessages.toString();
        List<String> messagesList;
        if (messages != null && !messages.isEmpty()) {
            messagesList = OBJECT_MAPPER.readValue(messages, new TypeReference<List<String>>() {
            });
        } else {
            messagesList = Collections.emptyList();
        }
        if (Cfile_exists.getValue() == 0) {
            return new ReadRcFileResult(false, null, messagesList);
        }
        // else: file existed and was read
        String options = Coptions.toString();
        if (options != null && !options.isEmpty()) {
            List<KeyValue> optionsList = OBJECT_MAPPER.readValue(options, new TypeReference<List<KeyValue>>() {
            });
            return new ReadRcFileResult(true, optionsList, messagesList);
        }
        // else: file existed, but some problems processing it
        return new ReadRcFileResult(true, null, messagesList);
    }

    public ExecuteRcFileResult executeRcFile(String filename) throws IOException {
        IntByReference Cfile_exists = new IntByReference();
        IntByReference Cno_errors = new IntByReference();
        RosieString filenameArg = filename == null ? RosieString.create() : RosieString.create(filename);
        RosieString Cmessages = RosieString.create();
        int ok = RosieLib.rosie_execute_rcfile(engine, filenameArg, Cfile_exists, Cno_errors, Cmessages);
        if (ok != 0) {
            throw new RuntimeException("execute_rcfile() failed (please report this as a bug)");
        }
        String messages = Cmessages.toString();
        List<String> messagesList;
        if (messages != null && !messages.isEmpty()) {
            messagesList = OBJECT_MAPPER.readValue(messages, new TypeReference<List<String>>() {
            });
        } else {
            messagesList = Collections.emptyList();
        }
        if (Cfile_exists.getValue() == 0) {
            return new ExecuteRcFileResult(false, false, messagesList);
        }
        // else: file existed
        if (Cno_errors.getValue() == 1) {
            return new ExecuteRcFileResult(true, true, messagesList);
        }
        // else: some problems processing it
        return new ExecuteRcFileResult(false, true, messagesList);
    }

    //endregion


    //region Functions that return a parse tree or fragments of one

    public ParseResult parse_expression(String exp) {
        try (RosieString rosieExpression = RosieString.create(exp); RosieString rosieParseTree = RosieString.create(); RosieString rosieMessages = RosieString.create()) {
            int ok = RosieLib.rosie_parse_expression(engine, rosieExpression, rosieParseTree, rosieMessages);
            if (ok != 0) {
                throw new RuntimeException("parse_expression failed (please report this as a bug)");
            }
            return new ParseResult(rosieParseTree.toString(), rosieMessages.toString());
        }
    }

    public ParseResult parse_block(String block) {
        try (RosieString rosieExpression = RosieString.create(block); RosieString rosieParseTree = RosieString.create(); RosieString rosieMessages = RosieString.create()) {
            int ok = RosieLib.rosie_parse_block(engine, rosieExpression, rosieParseTree, rosieMessages);
            if (ok != 0) {
                throw new RuntimeException("parse_block failed (please report this as a bug)");
            }
            return new ParseResult(rosieParseTree.toString(), rosieMessages.toString());
        }
    }

    public ParseResult expression_refs(String exp) {
        try (RosieString rosieExpression = RosieString.create(exp); RosieString rosieRefs = RosieString.create(); RosieString rosieMessages = RosieString.create()) {
            int ok = RosieLib.rosie_expression_refs(engine, rosieExpression, rosieRefs, rosieMessages);
            if (ok != 0) {
                throw new RuntimeException("expression_refs failed (please report this as a bug)");
            }
            return new ParseResult(rosieRefs.toString(), rosieMessages.toString());
        }
    }

    public ParseResult block_refs(String block) {
        try (RosieString rosieExpression = RosieString.create(block); RosieString rosieRefs = RosieString.create(); RosieString rosieMessages = RosieString.create()) {
            int ok = RosieLib.rosie_block_refs(engine, rosieExpression, rosieRefs, rosieMessages);
            if (ok != 0) {
                throw new RuntimeException("block_refs failed (please report this as a bug)");
            }
            return new ParseResult(rosieRefs.toString(), rosieMessages.toString());
        }
    }

    public ParseResult expression_deps(String exp) {
        try (RosieString rosieExpression = RosieString.create(exp); RosieString rosieDeps = RosieString.create(); RosieString rosieMessages = RosieString.create()) {
            int ok = RosieLib.rosie_expression_deps(engine, rosieExpression, rosieDeps, rosieMessages);
            if (ok != 0) {
                throw new RuntimeException("expression_deps failed (please report this as a bug)");
            }
            return new ParseResult(rosieDeps.toString(), rosieMessages.toString());
        }
    }

    public ParseResult block_deps(String block) {
        try (RosieString rosieExpression = RosieString.create(block); RosieString rosieDeps = RosieString.create(); RosieString rosieMessages = RosieString.create()) {
            int ok = RosieLib.rosie_block_deps(engine, rosieExpression, rosieDeps, rosieMessages);
            if (ok != 0) {
                throw new RuntimeException("block_deps failed (please report this as a bug)");
            }
            return new ParseResult(rosieDeps.toString(), rosieMessages.toString());
        }
    }

    //endregion


    //region Functions for reading and modifying various engine settings

    public String config() {
        try (RosieString retvals = RosieString.create()) {
            int ok = RosieLib.rosie_config(engine, retvals);
            if (ok != 0) {
                throw new RuntimeException("config() failed (please report this as a bug)");
            }
            return retvals.toString();
        }
    }

    public String getLibpath() {
        RosieString libpathArg = RosieString.create();
        int ok = RosieLib.rosie_libpath(engine, libpathArg);
        if (ok != 0) {
            throw new RuntimeException("libpath() failed (please report this as a bug)");
        }
        return libpathArg.toString();
    }


    public void setLibpath(String libpath) {
        RosieString libpathArg = RosieString.create(libpath);
        int ok = RosieLib.rosie_libpath(engine, libpathArg);
        if (ok != 0) {
            throw new RuntimeException("libpath() failed (please report this as a bug)");
        }
    }

    public AllocLimitResult getAllocLimit() {
        IntByReference limit_arg = new IntByReference();
        IntByReference usage_arg = new IntByReference();
        limit_arg.setValue(-1); // query
        int ok = RosieLib.rosie_alloc_limit(engine, limit_arg, usage_arg);
        if (ok != 0) {
            throw new RuntimeException("alloc_limit() failed (please report this as a bug)");
        }
        return new AllocLimitResult(limit_arg.getValue(), usage_arg.getValue());
    }

    public AllocLimitResult setAllocLimit(Integer newLimit) {
        IntByReference limit_arg = new IntByReference();
        IntByReference usage_arg = new IntByReference();
        if (newLimit != 0 && newLimit < 8192) {
            throw new IllegalArgumentException("new allocation limit must be 8192 KB or higher (or zero for unlimited)");
        }
        limit_arg.setValue(newLimit);
        int ok = RosieLib.rosie_alloc_limit(engine, limit_arg, usage_arg);
        if (ok != 0) {
            throw new RuntimeException("alloc_limit() failed (please report this as a bug)");
        }
        return new AllocLimitResult(limit_arg.getValue(), usage_arg.getValue());
    }

    //endregion


    @Override
    public void close() {
        if (engine != Pointer.NULL) {
            RosieLib.rosie_finalize(engine);
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
            // TODO: null quando chiuso
        }
    }

    private static boolean hasErrors(RosieString errors) {
        return errors.len.intValue() > 0 || errors.ptr != Pointer.NULL;
    }
}

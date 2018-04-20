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
            engine = RosieLib.INSTANCE.rosie_new(errors);
            if (engine == Pointer.NULL) {
                throw new RuntimeException(errors.toString());
            }
        }
    }

    public String config() {
        try (RosieString retvals = RosieString.create()) {
            int ok = RosieLib.INSTANCE.rosie_config(engine, retvals);
            if (ok != 0) {
                throw new RuntimeException("config() failed (please report this as a bug)");
            }
            return retvals.toString();
        }
    }

    public CompilationResult compile(String exp) {
        try (RosieString errors = RosieString.create(); RosieString rosieExpression = RosieString.create(exp)) {
            IntByReference pat = new IntByReference();
            int ok = RosieLib.INSTANCE.rosie_compile(engine, rosieExpression, pat, errors);
            if (ok != 0) {
                throw new RuntimeException("compile() failed (please report this as a bug)");
            }
            return new CompilationResult(pat.getValue() != 0 ? pat.getValue() : null, errors.toString());
        }
    }

    public LoadResult load(String src) {
        try (RosieString rosieSrc = RosieString.create(src); RosieString rosiePkgname = RosieString.create(); RosieString errors = RosieString.create()) {
            IntByReference ok = new IntByReference();
            int result = RosieLib.INSTANCE.rosie_load(engine, ok, rosieSrc, rosiePkgname, errors);
            if (result != 0) {
                throw new RuntimeException("load() failed (please report this as a bug)");
            }
            String errorsString = errors.toString();
            return new LoadResult(ok.getValue(), rosiePkgname.toString(), errorsString);
        }
    }

    public ImportResult importPackage(String pkgname) {
        return importPackage(pkgname, null);
    }

    public ImportResult importPackage(String pkgname, String as_name) {
        try (RosieString Cerrs = RosieString.create(); RosieString Cas_name = as_name != null ? RosieString.create(as_name) : null;
             RosieString Cpkgname = RosieString.create(pkgname); RosieString Cactual_pkgname = RosieString.create()) {
            IntByReference Csuccess = new IntByReference();
            int ok = RosieLib.INSTANCE.rosie_import(engine, Csuccess, Cpkgname, Cas_name, Cactual_pkgname, Cerrs);
            if (ok != 0) {
                throw new RuntimeException("import() failed (please report this as a bug)");
            }
            String actual_pkgname = Cactual_pkgname.toString();
            String errs = Cerrs.toString();
            return new ImportResult(Csuccess.getValue(), actual_pkgname, errs);
        }
    }

    public LoadResult loadfile(String fn) {
        try (RosieString Cerrs = RosieString.create(); RosieString Cfn = RosieString.create(fn); RosieString Cpkgname = RosieString.create()) {
            IntByReference Csuccess = new IntByReference();
            int ok = RosieLib.INSTANCE.rosie_loadfile(engine, Csuccess, Cfn, Cpkgname, Cerrs);
            if (ok != 0) {
                throw new RuntimeException("loadfile() failed (please report this as a bug)");
            }
            String errs = Cerrs.toString();
            String pkgname = Cpkgname.toString();
            return new LoadResult(Csuccess.getValue(), pkgname, errs);
        }
    }

    public MatchResult match(int Cpat, String input, int start, String encoder) {
        if (Cpat == 0) {
            throw new IllegalArgumentException("invalid compiled pattern");
        }

        try (RosieString Cinput = RosieString.create(input)) {
            RosieMatch Cmatch = new RosieMatch();
            int ok = RosieLib.INSTANCE.rosie_match(engine, Cpat, start, encoder, Cinput, Cmatch);
            if (ok != 0) {
                throw new RuntimeException("match() failed (please report this as a bug)");
            }

            int left = Cmatch.leftover;
            int abend = Cmatch.abend;
            int ttotal = Cmatch.ttotal;
            int tmatch = Cmatch.tmatch;
            if (Cmatch.data.ptr == null) {
                if (Cmatch.data.len.intValue() == 0) {
                    return new MatchResult(null, left, abend, ttotal, tmatch);
                } else if (Cmatch.data.len.intValue() == 1) {
                    return new MatchResult(/* FIXME: what does True mean? */"", left, abend, ttotal, tmatch);
                } else if (Cmatch.data.len.intValue() == 2) {
                    throw new IllegalArgumentException("invalid output encoder");
                } else if (Cmatch.data.len.intValue() == 4) {
                    throw new IllegalStateException("invalid compiled pattern (already freed?)");
                }
            }
            return new MatchResult(Cmatch.data.toString(), left, abend, ttotal, tmatch);
        }
    }

    public TraceResult trace(int Cpat, String input, int start, String style) {
        if (Cpat == 0) {
            throw new RuntimeException("invalid compiled pattern");
        }

        try (RosieString Cinput = RosieString.create(input); RosieString Ctrace = RosieString.create()) {
            IntByReference Cmatched = new IntByReference();
            int ok = RosieLib.INSTANCE.rosie_trace(engine, Cpat, start, style, Cinput, Cmatched, Ctrace);
            if (ok != 0) {
                throw new RuntimeException("trace() failed (please report this as a bug)");
            }

            if (Ctrace.ptr == null) {
                if (Ctrace.len.intValue() == 2) {
                    throw new IllegalArgumentException("invalid trace style");
                } else if (Ctrace.len.intValue() == 1) {
                    throw new IllegalStateException("invalid compiled pattern (already freed?)");
                }
            }
            boolean matched = Cmatched.getValue() != 0;
            String trace = Ctrace.toString();
            return new TraceResult(matched, trace);
        }
    }

    public MatchFileResult matchfile(int Cpat, String encoder) {
        return matchfile(Cpat, encoder, null, null, null, false);
    }

    public MatchFileResult matchfile(int Cpat, String encoder, String infile, String outfile, String errfile) {
        return matchfile(Cpat, encoder, infile, outfile, errfile, false);
    }

    public MatchFileResult matchfile(int Cpat, String encoder, String infile, String outfile, String errfile, boolean wholefile) {
        if (Cpat == 0) {
            throw new IllegalArgumentException("invalid compiled pattern");
        }

        try (RosieString Cerrmsg = RosieString.create()) {
            IntByReference Ccin = new IntByReference();
            IntByReference Ccout = new IntByReference();
            IntByReference Ccerr = new IntByReference();
            int wff = wholefile ? 1 : 0;

            int ok = RosieLib.INSTANCE.rosie_matchfile(engine, Cpat, encoder, wff,
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


    public ReadRcFileResult readRcFile(String filename) throws IOException {
        IntByReference Cfile_exists = new IntByReference();
        RosieString filenameArg = filename == null ? RosieString.create() : RosieString.create(filename);
        RosieString Coptions = RosieString.create();
        RosieString Cmessages = RosieString.create();
        int ok = RosieLib.INSTANCE.rosie_read_rcfile(engine, filenameArg, Cfile_exists, Coptions, Cmessages);
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
        int ok = RosieLib.INSTANCE.rosie_execute_rcfile(engine, filenameArg, Cfile_exists, Cno_errors, Cmessages);
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


    public String getLibpath() {
        RosieString libpathArg = RosieString.create();
        int ok = RosieLib.INSTANCE.rosie_libpath(engine, libpathArg);
        if (ok != 0) {
            throw new RuntimeException("libpath() failed (please report this as a bug)");
        }
        return libpathArg.toString();
    }


    public void setLibpath(String libpath) {
        RosieString libpathArg = RosieString.create(libpath);
        int ok = RosieLib.INSTANCE.rosie_libpath(engine, libpathArg);
        if (ok != 0) {
            throw new RuntimeException("libpath() failed (please report this as a bug)");
        }
    }

    public AllocLimitResult getAllocLimit() {
        IntByReference limit_arg = new IntByReference();
        IntByReference usage_arg = new IntByReference();
        limit_arg.setValue(-1); // query
        int ok = RosieLib.INSTANCE.rosie_alloc_limit(engine, limit_arg, usage_arg);
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
        int ok = RosieLib.INSTANCE.rosie_alloc_limit(engine, limit_arg, usage_arg);
        if (ok != 0) {
            throw new RuntimeException("alloc_limit() failed (please report this as a bug)");
        }
        return new AllocLimitResult(limit_arg.getValue(), usage_arg.getValue());
    }


    @Override
    public void close() {
        if (engine != Pointer.NULL) {
            RosieLib.INSTANCE.rosie_finalize(engine);
        }
    }
}

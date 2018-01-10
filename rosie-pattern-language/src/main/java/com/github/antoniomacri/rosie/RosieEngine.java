package com.github.antoniomacri.rosie;

import com.github.antoniomacri.rosie.lib.RosieLib;
import com.github.antoniomacri.rosie.lib.RosieMatch;
import com.github.antoniomacri.rosie.lib.RosieString;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.io.Closeable;


/**
 * Create a Rosie pattern matching engine.
 */
public class RosieEngine implements Closeable {

    private RosieString new_cstr() {
        RosieString str = RosieLib.INSTANCE.rosie_new_string_ptr("", 0);
        return str;
    }

    private RosieString new_cstr(String expression) {
        RosieString str = RosieLib.INSTANCE.rosie_new_string_ptr(expression, expression.length());
        return str;
    }


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
        try (RosieString errors = new_cstr()) {
            engine = RosieLib.INSTANCE.rosie_new(errors);
            if (engine == Pointer.NULL) {
                throw new RuntimeException(errors.toString());
            }
        }
    }

    public String config() {
        try (RosieString retvals = new_cstr()) {
            int ok = RosieLib.INSTANCE.rosie_config(engine, retvals);
            if (ok != 0) {
                throw new RuntimeException("config() failed (please report this as a bug)");
            }
            return retvals.toString();
        }
    }

    public CompilationResult compile(String exp) {
        try (RosieString errors = new_cstr(); RosieString rosieExpression = new_cstr(exp)) {
            IntByReference pat = new IntByReference();
            int ok = RosieLib.INSTANCE.rosie_compile(engine, rosieExpression, pat, errors);
            if (ok != 0) {
                throw new RuntimeException("compile() failed (please report this as a bug)");
            }
            if (pat.getValue() == 0) {
                return new CompilationResult(null, errors.toString());
            } else {
                return new CompilationResult(pat.getValue(), null);
            }
        }
    }

    public LoadResult load(String src) {
        try (RosieString rosieSrc = new_cstr(src); RosieString rosiePkgname = new_cstr(); RosieString errors = new_cstr()) {
            IntByReference ok = new IntByReference();
            int result = RosieLib.INSTANCE.rosie_load(engine, ok, rosieSrc, rosiePkgname, errors);
            if (result != 0) {
                throw new RuntimeException("load() failed (please report this as a bug)");
            }
            String errorsString = errors.toString();
            return new LoadResult(ok.getValue(), rosiePkgname.toString(), errorsString.equals("{}") ? null : errorsString);
        }
    }

    public ImportResult importPackage(String pkgname) {
        return importPackage(pkgname, null);
    }

    public ImportResult importPackage(String pkgname, String as_name) {
        try (RosieString Cerrs = new_cstr(); RosieString Cas_name = as_name != null ? new_cstr(as_name) : null; RosieString Cpkgname = new_cstr(pkgname)) {
            IntByReference Csuccess = new IntByReference();
            int ok = RosieLib.INSTANCE.rosie_import(engine, Csuccess, Cpkgname, Cas_name, Cerrs);
            if (ok != 0) {
                throw new RuntimeException("import() failed (please report this as a bug)");
            }
            String actual_pkgname = Cpkgname.toString();
            String errs = Cerrs.toString();
            if ("{}".equals(errs)) {
                errs = null;
            }
            return new ImportResult(Csuccess.getValue(), actual_pkgname, errs);
        }
    }

    public MatchResult match(int Cpat, String input, int start, String encoder) {
        if (Cpat == 0) {
            throw new IllegalArgumentException("invalid compiled pattern");
        }

        try (RosieString Cinput = new_cstr(input)) {
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

        try (RosieString Cinput = new_cstr(input); RosieString Ctrace = new_cstr()) {
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

        try (RosieString Cerrmsg = new_cstr()) {
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
                if (Ccout.getValue() == 1) {
                    throw new IllegalStateException("invalid compiled pattern (already freed?)");
                } else if (Ccout.getValue() == 2) {
                    throw new IllegalArgumentException("invalid encoder");
                } else if (Ccout.getValue() == 3) {
                    throw new RuntimeException(Cerrmsg.toString()); // file i/o error
                } else {
                    throw new RuntimeException("unknown error caused matchfile to fail");
                }
            }

            return new MatchFileResult(Ccin.getValue(), Ccout.getValue(), Ccerr.getValue());
        }
    }

    @Override
    public void close() {
        if (engine != Pointer.NULL) {
            RosieLib.INSTANCE.rosie_finalize(engine);
        }
    }

    /*
    def setlibpath(self, libpath):
        ok = lib.rosie_setlibpath_engine(self.engine, libpath)
        if ok != 0:
            raise RuntimeError("setpath() failed (please report this as a bug)")
     *
    public void setlibpath(self, libpath) {
        ok = lib.rosie_setlibpath_engine(self.engine, libpath)
        if ok != 0:
        raise RuntimeError ("setpath() failed (please report this as a bug)")
    }

    /*
    def set_alloc_limit(self, newlimit):
        if (newlimit != 0) and (newlimit < 10):
            raise ValueError("new allocation limit must be 10 MB or higher (or zero for unlimited)")
        ok = lib.rosie_set_alloc_limit(self.engine, newlimit)
        if ok != 0:
            raise RuntimeError("set_alloc_limit() failed (please report this as a bug)")
     *
    public void set_alloc_limit(self, newlimit) {
        if (newlimit != 0) and(newlimit < 10):
        raise ValueError ("new allocation limit must be 10 MB or higher (or zero for unlimited)")
        ok = lib.rosie_set_alloc_limit(self.engine, newlimit)
        if ok != 0:
        raise RuntimeError ("set_alloc_limit() failed (please report this as a bug)");
    }
    */
}

package com.github.antoniomacri.rosie;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


/**
 * Create a RosieEngine pattern matching engine.  The first call to engine()
 * will load librosie from one of the standard shared library
 * directories for your system, or from a custom path provided as an
 * argument.
 */
public class RosieEngine {
    private RosieLib LIB = Native.loadLibrary("rosie", RosieLib.class);


    private RosieString new_cstr() {
        RosieString str = LIB.rosie_new_string_ptr("", 0);
        return str;
    }

    private RosieString new_cstr(String expression) {
        RosieString str = LIB.rosie_new_string_ptr(expression, expression.length());
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


    /*
     def __init__(self, custom_libpath=None):
        global lib, libname
        if not lib:
            if custom_libpath:
                libpath = path.join(custom_libpath, libname)
                if not path.isfile(libpath):
                    raise RuntimeError("Cannot find librosie at " + libpath)
            else:
                libpath = find_library(libname)
                if not libpath:
                    raise RuntimeError("Cannot find librosie using ctypes.util.find_library()")
            lib = ffi.dlopen(libpath, ffi.RTLD_GLOBAL)
        Cerrs = new_cstr()
        self.engine = lib.rosie_new(Cerrs)
        if self.engine == ffi.NULL:
            raise RuntimeError("librosie: " + read_cstr(Cerrs))
        return
     */
    public RosieEngine() {
        RosieString errors = new_cstr();
        engine = LIB.rosie_new(errors);
        if (engine == Pointer.NULL) {
            throw new RuntimeException(errors.toString());
        }
    }

    public String config() {
        RosieString retvals = new_cstr();
        int ok = LIB.rosie_config(engine, retvals);
        if (ok != 0) {
            throw new RuntimeException("config() failed (please report this as a bug)");
        }
        return retvals.toString();
    }

    public RosieCompiled compile(String exp) {
        RosieString errors = new_cstr();
        RosieString rosieExpression = new_cstr(exp);
        IntByReference pat = new IntByReference();
        int ok = LIB.rosie_compile(engine, rosieExpression, pat, errors);
        if (ok != 0) {
            throw new RuntimeException("compile() failed (please report this as a bug)");
        }
        if (pat.getValue() == 0) {
            return new RosieCompiled(null, errors.toString());
        } else {
            return new RosieCompiled(pat, null);
        }
    }

    public LoadResult load(String src) {
        RosieString rosieSrc = new_cstr(src);
        IntByReference ok = new IntByReference();
        RosieString rosiePkgname = new_cstr();
        RosieString errors = new_cstr();
        int result = LIB.rosie_load(engine, ok, rosieSrc, rosiePkgname, errors);
        if (result != 0) {
            throw new RuntimeException("load() failed (please report this as a bug)");
        }
        String errorsString = errors.toString();
        return new LoadResult(ok.getValue(), rosiePkgname.toString(), errorsString.equals("{}") ? null : errorsString);
    }

    /*
    def loadfile(self, fn):
        f = open(fn, 'r')
        rplsource = f.read()
        return self.load(rplsource)
     *
    public void loadfile(fn) {
        f = open(fn, 'r')
        rplsource = f.read()
        return load(rplsource);
    }
    */

    public ImportResult import_pkg(String pkgname) {
        return import_pkg(pkgname, null);
    }

    public ImportResult import_pkg(String pkgname, String as_name) {
        RosieString Cerrs = new_cstr();
        RosieString Cas_name = as_name != null ? new_cstr(as_name) : null;
        RosieString Cpkgname = new_cstr(pkgname);
        IntByReference Csuccess = new IntByReference();
        int ok = LIB.rosie_import(engine, Csuccess, Cpkgname, Cas_name, Cerrs);
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

    public MatchResult match(IntByReference Cpat, String input, int start, String encoder) {
        if (Cpat.getValue() == 0) {
            throw new IllegalArgumentException("invalid compiled pattern");
        }
        RosieMatch Cmatch = new RosieMatch();
        RosieString Cinput = new_cstr(input);
        int ok = LIB.rosie_match(engine, Cpat.getValue(), start, encoder, Cinput, Cmatch);
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

    public TraceResult trace(IntByReference Cpat, String input, int start, String style) {
        if (Cpat.getValue() == 0) {
            throw new RuntimeException("invalid compiled pattern");
        }
        IntByReference Cmatched = new IntByReference();
        RosieString Cinput = new_cstr(input);
        RosieString Ctrace = new_cstr();
        int ok = LIB.rosie_trace(engine, Cpat.getValue(), start, style, Cinput, Cmatched, Ctrace);
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

    public MatchFileResult matchfile(IntByReference Cpat, String encoder) {
        return matchfile(Cpat, encoder, null, null, null, false);
    }

    public MatchFileResult matchfile(IntByReference Cpat, String encoder, String infile, String outfile, String errfile) {
        return matchfile(Cpat, encoder, infile, outfile, errfile, false);
    }

    public MatchFileResult matchfile(IntByReference Cpat, String encoder, String infile, String outfile, String errfile, boolean wholefile) {
        if (Cpat.getValue() == 0) {
            throw new IllegalArgumentException("invalid compiled pattern");
        }
        IntByReference Ccin = new IntByReference();
        IntByReference Ccout = new IntByReference();
        IntByReference Ccerr = new IntByReference();
        int wff = wholefile ? 1 : 0;
        RosieString Cerrmsg = new_cstr();
        int ok = LIB.rosie_matchfile(engine, Cpat.getValue(), encoder, wff,
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

    /*
    def __del__(self):
        if hasattr(self, 'engine') and (self.engine != ffi.NULL):
            lib.rosie_finalize(self.engine)
     *
    public void __del__() {
        if hasattr(self, 'engine') and(self.engine != ffi.NULL):
        lib.rosie_finalize(self.engine)
    }
*/
}

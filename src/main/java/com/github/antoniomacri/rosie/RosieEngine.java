package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.antoniomacri.rosie.lib.RosieLib;
import com.github.antoniomacri.rosie.lib.RosieString;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.io.Closeable;


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


    /**
     * Creates a new Rosie Matching Engine.
     */
    public RosieEngine() {
        try (RosieString errors = RosieString.create()) {
            engine = RosieLib.rosie_new(errors);
            if (engine == Pointer.NULL) {
                throw new RuntimeException(errors.toString());
            }
        }
    }

    /**
     * Compiles the given RPL expression in the context of this engine.
     * <p>
     * The returned {@link Pattern} will be available until closed or until the engine is closed.
     *
     * @param exp the RPL expression
     * @return an RPL pattern that can be matched against input strings
     */
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
            return new Pattern(engine, pat.getValue());
        }
    }


    //region Functions for loading statements/blocks/packages into an engine

    /**
     * Loads and compiles RPL code, storing the resulting bindings in the engine's environment.
     *
     * @param src the RPL code
     * @return the package name, if the RPL code contained a package declaration, or {@code null}.
     */
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

    /**
     * Loads and compiles RPL code from a file, storing the resulting bindings in the engine's environment.
     *
     * @param fn the RPL source file
     * @return the package name, if the RPL file contained a package declaration, or {@code null}.
     */
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

    /**
     * Imports the given package.
     * <p>
     * Calling {@link #importPackage} is equivalent to calling {@link #load(String)} with the string "{@code import
     * <packageName>}", except that {@link #importPackage} will always find and load the RPL package in the
     * filesystem, whereas {@link #load(String)} does nothing if the package has already been loaded into the engine.
     *
     * @param pkgname the name of the package to load
     * @return the name of the package loaded
     */
    public String importPackage(String pkgname) {
        return importPackage(pkgname, null);
    }

    /**
     * Imports the given package and binds it to an alias.
     * <p>
     * Calling {@link #importPackage} is equivalent to calling {@link #load(String)} with the string "{@code import
     * <packageName> as <asName>}", except that {@link #importPackage} will always find and load the RPL package in the
     * filesystem, whereas {@link #load(String)} does nothing if the package has already been loaded into the engine.
     * <p>
     * See {@link #getAllocLimit()}.
     *
     * @param pkgname the name of the package to load
     * @param as_name the alias to bind the package to
     * @return the actual name of the package loaded (not the alias)
     */
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


    //region Functions for reading and modifying various engine settings

    /**
     * Retrieves the configuration of an engine and of the Rosie installation that created it.
     */
    public String config() {
        try (RosieString retvals = RosieString.create()) {
            int ok = RosieLib.rosie_config(engine, retvals);
            if (ok != 0) {
                throw new RuntimeException("config() failed (please report this as a bug)");
            }
            return retvals.toString();
        }
    }

    /**
     * Gets the current libpath.
     */
    public String getLibpath() {
        RosieString libpathArg = RosieString.create();
        int ok = RosieLib.rosie_libpath(engine, libpathArg);
        if (ok != 0) {
            throw new RuntimeException("libpath() failed (please report this as a bug)");
        }
        return libpathArg.toString();
    }

    /**
     * Sets the libpath.
     */
    public void setLibpath(String libpath) {
        RosieString libpathArg = RosieString.create(libpath);
        int ok = RosieLib.rosie_libpath(engine, libpathArg);
        if (ok != 0) {
            throw new RuntimeException("libpath() failed (please report this as a bug)");
        }
    }

    /**
     * Gets the "soft limit" on the size of the Lua heap.
     * <p>
     * The front-end of the RPL compiler, the CLI, and some of the output encoders (such as color and jsonpp)
     * are written in Lua, a language that has garbage collection. Various Rosie functions ({@link #match}{@link #trace} rosie_matchfile)
     * check to see if the Lua heap has grown beyond the current limit, and if so, invoke the garbage collector.
     *
     * @return the current allocation values (size limit and heap usage).
     */
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

    /**
     * Sets the "soft limit" on the size of the Lua heap.
     * <p>
     * See {@link #getAllocLimit()}.
     *
     * @param newLimit the heap size limit, or 0 to remove the limit (it will default to Lua's garbage collection settings)
     */
    public AllocLimitResult setAllocLimit(int newLimit) {
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


    /**
     * Destroys a Rosie Matching Engine and all associated data, freeing their memory.
     */
    @Override
    public void close() {
        if (engine != Pointer.NULL) {
            RosieLib.rosie_finalize(engine);
            engine = Pointer.NULL;
        }
    }

    private static boolean hasErrors(RosieString errors) {
        return errors.len.intValue() > 0 || errors.ptr != Pointer.NULL;
    }
}

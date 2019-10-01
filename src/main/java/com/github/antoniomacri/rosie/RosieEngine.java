package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.antoniomacri.rosie.lib.RosieLib;
import com.github.antoniomacri.rosie.lib.RosieString;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;


/**
 * Create a Rosie pattern matching engine.
 * <p>
 * A Rosie pattern matching engine is used to load/import RPL code (patterns) and to do matching.
 */
public class RosieEngine implements Closeable {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


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
     * An RPL expression must be compiled before it can be used to match (or trace) with an input string.
     * The returned {@link Pattern} will be available until closed or until the engine is closed.
     *
     * @param expression the RPL expression
     * @return an RPL pattern that can be matched against input strings
     */
    public Pattern compile(String expression) throws RosieException {
        try (RosieString rsErrors = RosieString.create(); RosieString rsExpression = RosieString.create(expression)) {
            IntByReference pat = new IntByReference();
            int result = RosieLib.rosie_compile(engine, rsExpression, pat, rsErrors);
            if (result != 0) {
                throw new RuntimeException("compile() failed (please report this as a bug)");
            }
            if (pat.getValue() == 0 || hasErrors(rsErrors)) {
                throw new RosieException("Errors reported", rsErrors.toString());
            }
            return new Pattern(engine, pat.getValue());
        }
    }


    /**
     * Loads and compiles RPL code, storing the resulting bindings in the engine's environment.
     *
     * @param rplCode the RPL code
     * @return the package name, if the RPL code contained a package declaration, or {@code null}.
     */
    public String load(String rplCode) {
        try (RosieString rsCode = RosieString.create(rplCode); RosieString rsPackageName = RosieString.create(); RosieString rsErrors = RosieString.create()) {
            IntByReference ok = new IntByReference();
            int result = RosieLib.rosie_load(engine, ok, rsCode, rsPackageName, rsErrors);
            if (result != 0) {
                throw new RuntimeException("load() failed (please report this as a bug)");
            }
            if (ok.getValue() == 0 || hasErrors(rsErrors)) {
                throw new RosieException("Errors reported", rsErrors.toString());
            }
            return rsPackageName.toString();
        }
    }

    /**
     * Loads and compiles RPL code from a file, storing the resulting bindings in the engine's environment.
     *
     * @param rplFile the RPL source file
     * @return the package name, if the RPL file contained a package declaration, or {@code null}.
     */
    public String loadFile(String rplFile) {
        try (RosieString rsFile = RosieString.create(rplFile); RosieString rsPackageName = RosieString.create(); RosieString rsErrors = RosieString.create()) {
            IntByReference ok = new IntByReference();
            int result = RosieLib.rosie_loadfile(engine, ok, rsFile, rsPackageName, rsErrors);
            if (result != 0) {
                throw new RuntimeException("loadFile() failed (please report this as a bug)");
            }
            if (ok.getValue() == 0 || hasErrors(rsErrors)) {
                throw new RosieException("Errors reported", rsErrors.toString());
            }
            return rsPackageName.toString();
        }
    }

    /**
     * Imports the given package.
     * <p>
     * Calling {@link #importPackage} is equivalent to calling {@link #load(String)} with the string "{@code import
     * <packageName>}", except that {@link #importPackage} will always find and load the RPL package in the
     * filesystem, whereas {@link #load(String)} does nothing if the package has already been loaded into the engine.
     *
     * @param packageName the name of the package to load
     * @return the name of the package loaded
     */
    public String importPackage(String packageName) {
        return importPackage(packageName, null);
    }

    /**
     * Imports the given package and binds it to an alias.
     * <p>
     * Calling {@link #importPackage} is equivalent to calling {@link #load(String)} with the string "{@code import
     * <packageName> as <asName>}", except that {@link #importPackage} will always find and load the RPL package in the
     * filesystem, whereas {@link #load(String)} does nothing if the package has already been loaded into the engine.
     *
     * @param packageName the name of the package to load
     * @param asName      the alias to bind the package to
     * @return the actual name of the package loaded (not the alias)
     */
    public String importPackage(String packageName, String asName) {
        try (RosieString rsAsName = asName != null ? RosieString.create(asName) : null; RosieString rsPackageName = RosieString.create(packageName);
             RosieString rsActualPackageName = RosieString.create(); RosieString rsErrors = RosieString.create()) {
            IntByReference ok = new IntByReference();
            int result = RosieLib.rosie_import(engine, ok, rsPackageName, rsAsName, rsActualPackageName, rsErrors);
            if (result != 0) {
                throw new RuntimeException("import() failed (please report this as a bug)");
            }
            if (ok.getValue() == 0 || hasErrors(rsErrors)) {
                throw new RosieException("Errors reported", rsErrors.toString());
            }
            return rsActualPackageName.toString();
        }
    }


    /**
     * Retrieves the configuration of an engine and of the Rosie installation that created it.
     */
    public Configuration config() {
        try (RosieString rsJsonResult = RosieString.create()) {
            int result = RosieLib.rosie_config(engine, rsJsonResult);
            if (result != 0) {
                throw new RuntimeException("config() failed (please report this as a bug)");
            }

            try {
                List<List<ConfigProperty>> configs = OBJECT_MAPPER.readValue(rsJsonResult.toString(), new TypeReference<List<List<ConfigProperty>>>() {
                });
                return new Configuration(configs.get(0), configs.get(1), configs.get(2));
            } catch (IOException e) {
                throw new RuntimeException("Cannot parse configuration JSON.", e);
            }
        }
    }

    /**
     * Gets the current libpath.
     */
    public String getLibpath() {
        RosieString rsNewPath = RosieString.create();
        int result = RosieLib.rosie_libpath(engine, rsNewPath);
        if (result != 0) {
            throw new RuntimeException("libpath() failed (please report this as a bug)");
        }
        return rsNewPath.toString();
    }

    /**
     * Sets the libpath.
     *
     * @param libpath the new desired libpath
     */
    public void setLibpath(String libpath) {
        RosieString rsLibPath = RosieString.create(libpath);
        int result = RosieLib.rosie_libpath(engine, rsLibPath);
        if (result != 0) {
            throw new RuntimeException("libpath() failed (please report this as a bug)");
        }
    }

    /**
     * Gets the "soft limit" on the size of the Lua heap.
     * <p>
     * The front-end of the RPL compiler, the CLI, and some of the output encoders (such as color and jsonpp)
     * are written in Lua, a language that has garbage collection. Various Rosie functions ({@link Pattern#match},
     * {@link Pattern#trace}) check to see if the Lua heap has grown beyond the current limit, and if so, invoke
     * the garbage collector.
     *
     * @return the current allocation values (size limit and heap usage).
     */
    public AllocLimitResult getAllocLimit() {
        IntByReference limit_arg = new IntByReference();
        IntByReference usage_arg = new IntByReference();
        limit_arg.setValue(-1); // query
        int result = RosieLib.rosie_alloc_limit(engine, limit_arg, usage_arg);
        if (result != 0) {
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
        int result = RosieLib.rosie_alloc_limit(engine, limit_arg, usage_arg);
        if (result != 0) {
            throw new RuntimeException("alloc_limit() failed (please report this as a bug)");
        }
        return new AllocLimitResult(limit_arg.getValue(), usage_arg.getValue());
    }


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

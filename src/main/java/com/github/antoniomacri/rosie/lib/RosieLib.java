package com.github.antoniomacri.rosie.lib;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;



// Documentation taken from:
// https://gitlab.com/rosie-pattern-language/rosie/blob/master/doc/librosie.md
public class RosieLib {
    static {
        Native.register(RosieLib.class, "rosie");
    }


    /**
     * Copies len bytes at the pointer msg into newly allocated space.  Returns
     * a new string structure initialized such that it refers to the copy.
     */
    // str rosie_new_string(byte_ptr msg, size_t len);
    public static native RosieString.ByValue rosie_new_string(String msg, int len);

    /**
     * Copies len bytes at the pointer msg into newly allocated space, and
     * allocates a new string structure initialized such that it refers to the copy.
     * Returns a pointer to the structure.
     */
    // str *rosie_new_string_ptr(byte_ptr msg, size_t len);
    public static native RosieString rosie_new_string_ptr(String msg, int len);

    // str rosie_string_from(byte_ptr msg, size_t len);
    public static native RosieString.ByValue rosie_string_from(String msg, int len);

    // str *rosie_string_ptr_from(byte_ptr msg, size_t len);
    public static native RosieString rosie_string_ptr_from(String msg, int len);

    // void rosie_free_string(str s);
    public static native void rosie_free_string(RosieString.ByValue s);

    /**
     * The data field of the structure str is freed (by Unix free).  Then the
     * string structure itself is freed.
     */
    // void rosie_free_string_ptr(str *s);
    public static native void rosie_free_string_ptr(RosieString s);

    /**
     * Create a new Rosie Matching Engine.
     * <p>
     * If there are any warnings or errors, they will be returned in messages, independently of whether the call
     * succeeded.  If the call fails, the pointer returned will be null.
     * <p>
     * The client is responsible for freeing messages with rosie_free_string_ptr.
     */
    // Engine *rosie_new(str *messages);
    public static native Pointer rosie_new(RosieString messages);

    /**
     * Destroys a Rosie Matching Engine and all associated data, freeing their memory.
     */
    // void rosie_finalize(Engine *e);
    public static native void rosie_finalize(Pointer e);

    /**
     * If the data pointer in the newpath argument is NULL, the call invokes a query for the current libpath,
     * which is returned in newpath.
     * <p>
     * The client is responsible for freeing newpath with rosie_free_string_ptr.
     */
    // int rosie_libpath(Engine *e, str *newpath);
    public static native int rosie_libpath(Pointer e, RosieString newpath);

    /**
     * The front-end of the RPL compiler, the CLI, and some of the output encoders
     * (such as color and jsonpp) are written in Lua, a language that has garbage
     * collection. The rosie_alloc_limit API allows the client program to set and
     * query a "soft limit" on the size of the Lua heap.
     * <p>
     * The functions rosie_match, rosie_trace, and rosie_matchfile check to
     * see if the Lua heap has grown beyond the current limit, and if so, invokes the
     * garbage collector.
     * <p>
     * When called with newlimit of 0, the limit is removed, and will default to
     * Lua's default garbage collection settings.
     * <p>
     * When called with newlimit of -1, the call is a query.  On return,
     * newlimit will be set to the current limit, and usage to the current Lua
     * heap usage.
     * <p>
     * The units of newlimit and usage are Kb (1024 bytes).
     */
    // int rosie_alloc_limit(Engine *e, int *newlimit, int *usage);
    public static native int rosie_alloc_limit(Pointer e, IntByReference newlimit, IntByReference usage);

    /**
     * The rosie_config API provides a way to read the configuration of an engine
     * and of the Rosie installation that created it.  The string returned in
     * retvals is a JSON-encoded list of 3 configuration tables:
     * <ol>
     * <li>The first table describes the engine-independent Rosie installation
     * configuration.</li>
     * <li>The second table describes the engine configuration.</li>
     * <li>The third table is a set of configuration parameters that is passed to every
     * output encoder.  (An encoder may use any, all, or none of these.)</li>
     * </ol>
     * Each of the tables is a list of items.  Each item has the following structure,
     * where all JSON values are strings:
     * <p>
     * <ul>
     * <li>name: a unique name for this item of the configuration</li>
     * <li>set_by: distribution if this aspect of the configuration is set by the
     * Rosie distribution that was installed; build if set at build-time;
     * default if it is a run-time default that can be customized; rcfile if
     * set in the Rosie init file .rosierc; CLI if set on the command line
     * (CLI only); other values, including the empty string, are possible</li>
     * <li>value: the current value for this item</li>
     * <li>description: a human-readable description of the item</li>
     * </ul>
     * Additional (undocumented) keys may be present.
     */
    // int rosie_config(Engine *e, str *retvals);
    public static native int rosie_config(Pointer e, RosieString retvals);

    /**
     * The string expression is compiled into an rplx object and an integer
     * handle to that object is returned.  The object will be available until
     * explicitly freed, or until the engine e is freed with rosie_finalize.
     * <p>
     * If pat is non-zero upon return, it is the rplx handle, which behaves
     * somewhat like a Unix file descriptor in that (1) it remains valid until
     * explicitly freed (with rosie_free_rplx) and (2) the same integer value may
     * be reused by the engine afterwards.
     * <p>
     * Regardless of error status, messages may contain errors, warnings, or other
     * information.
     * <p>
     * The client is responsible for freeing messages with rosie_free_string_ptr.
     */
    // int rosie_compile(Engine *e, str *expression, int *pat, str *messages);
    public static native int rosie_compile(Pointer e, RosieString expression, IntByReference pat, RosieString messages);

    /**
     * Call rosie_free_rplx to allow the engine to reclaim the compiled pattern pat.
     */
    // int rosie_free_rplx(Engine *e, int pat);
    public static native int rosie_free_rplx(Pointer e, int pat);

    /**
     * Using engine e and its pattern pat, match the pattern against input
     * and produce match data (a string) using output encoder encoder.  Note that
     * encoder is a null-terminated C-style string.
     * <p>
     * The match argument is a pointer to a rosie_matchresult structure that is
     * allocated by the client program, into which the match results will be written.
     * A single struct may be used across repeated calls to rosie_match, and indeed
     * this is recommended.
     * <p>
     * As noted in the earlier section on <a
     * href="https://gitlab.com/rosie-pattern-language/rosie/blob/master/doc/librosie.md#types">
     * librosie types</a>, a rosie_matchresult contains one dynamically allocated object, its data
     * field.  The client program does not need to and should not manage the storage
     * for data because librosie will automatically reuse it, making it larger as
     * needed (using realloc).
     * <p>
     * IMPORTANT: Because librosie reuses the match results data field (a string),
     * the client program must make a copy of that string, if necessary, before calling
     * rosie_match again.
     */
    // int rosie_match(Engine *e, int pat, int start, char *encoder, str *input, match *match);
    public static native int rosie_match(Pointer e, int pat, int start, String encoder, RosieString input, RosieMatch match);

    // int rosie_matchfile(Engine *e, int pat, char *encoder, int wholefileflag,
    //                char *infilename, char *outfilename, char *errfilename,
    //                int *cin, int *cout, int *cerr,
    //                str *err);
    public static native int rosie_matchfile(Pointer e, int pat, String encoder, int wholefileflag,
                                             String infilename, String outfilename, String errfilename,
                                             IntByReference cin, IntByReference cout, IntByReference cerr,
                                             RosieString err);

    /**
     * Like rosie_match, but executes the trace operation where trace_style is
     * a null-terminated C string argument analogous to encoder.
     * <p>
     * Return values are the boolean matched (0 for false, 1 for true) and the
     * string trace (which holds the trace output as a string).  As with the
     * data field in a match result, a null pointer field in trace requires
     * checking the length field to determine whether an error occurred.
     * <p>
     * When the trace pointer is null and its length is also null, then no trace
     * data was returned.  (Currently, all trace styles produce some data, so this
     * outcome is not possible.)  A non-zero length with a null pointer indicates one
     * of the errors listed above in the <a
     * href="https://gitlab.com/rosie-pattern-language/rosie/blob/master/doc/librosie.md#interpreting-match-results">
     * interpreting match results</a> section.
     * <p>
     * The client is responsible for freeing trace with rosie_free_string_ptr.
     */
    // int rosie_trace(Engine *e, int pat, int start, char *trace_style, str *input, int *matched, str *trace);
    public static native int rosie_trace(Pointer e, int pat, int start, String trace_style, RosieString input,
                                         IntByReference matched, RosieString trace);

    /**
     * The string src is read, compiled, and the resulting bindings are stored in
     * the engine's environment.  If ok is 0 on return, no errors occurred.  There
     * may still be messages (e.g. warnings).
     * <p>
     * If ok is non-zero, an error occurred, and messages will contain a
     * <p>
     * JSON-encoded error structure.
     * <p>
     * TODO: Document the JSON violation structure.
     * <p>
     * The client is responsible for freeing messages with rosie_free_string_ptr.
     * <p>
     * If src contained a package declaration, the package name will be returned in
     * pkgname.
     * <p>
     * The client is responsible for freeing pkgname with rosie_free_string_ptr.
     */
    // int rosie_load(Engine *e, int *ok, str *src, str *pkgname, str *messages);
    public static native int rosie_load(Pointer e, IntByReference ok, RosieString src, RosieString pkgname, RosieString messages);

    /**
     * Same functionality as rosie_load, except fn is a filename and librosie
     * reads and processes the contents of that file.
     */
    // int rosie_loadfile(Engine *e, int *ok, str *fn, str *pkgname, str *messages);
    public static native int rosie_loadfile(Pointer e, IntByReference ok, RosieString fn, RosieString pkgname, RosieString messages);

    /**
     * Calling rosie_import with package  causes the same actions as
     * calling rosie_load with the string import &lt;pkgname&gt;, with one exception:
     * rosie_import will always find and load the RPL package &lt;pkgname&gt; in the
     * filesystem.  By contrast, when rosie_load encounters import &lt;pkgname&gt;, the
     * package may have already been loaded into the engine.
     * <p>
     * Including a (string) value for the as parameter behaves like import &lt;pkgname&gt; as &lt;as&gt; with the same caveats.
     */
    // int rosie_import(Engine *e, int *ok, str *pkgname, str *as, str *actual_pkgname, str *messages);
    public static native int rosie_import(Pointer e, IntByReference ok, RosieString pkgname, RosieString as, RosieString actual_pkgname, RosieString messages);

    // int rosie_read_rcfile(Engine *e, str *filename, int *file_exists, str *options, str *messages);
    public static native int rosie_read_rcfile(Pointer e, RosieString filename, IntByReference file_exists, RosieString options, RosieString messages);

    // int rosie_execute_rcfile(Engine *e, str *filename, int *file_exists, int *no_errors, str *messages);
    public static native int rosie_execute_rcfile(Pointer e, RosieString filename, IntByReference file_exists, IntByReference no_errors, RosieString messages);

    // int rosie_expression_refs(void *e, str *input, str *refs, str *messages);
    public static native int rosie_expression_refs(Pointer e, RosieString input, RosieString refs, RosieString messages);

    // int rosie_block_refs(void *e, str *input, str *refs, str *messages);
    public static native int rosie_block_refs(Pointer e, RosieString input, RosieString refs, RosieString messages);

    // int rosie_expression_deps(void *e, str *input, str *deps, str *messages);
    public static native int rosie_expression_deps(Pointer e, RosieString input, RosieString deps, RosieString messages);

    // int rosie_block_deps(void *e, str *input, str *deps, str *messages);
    public static native int rosie_block_deps(Pointer e, RosieString input, RosieString deps, RosieString messages);

    // int rosie_parse_expression(void *e, str *input, str *parsetree, str *messages);
    public static native int rosie_parse_expression(Pointer e, RosieString input, RosieString parsetree, RosieString messages);

    // int rosie_parse_block(void *e, str *input, str *parsetree, str *messages);
    public static native int rosie_parse_block(Pointer e, RosieString input, RosieString parsetree, RosieString messages);
}

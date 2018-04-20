package com.github.antoniomacri.rosie.lib;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


public interface RosieLib extends Library {
    // TODO: liblua.5.3.so: impossibile aprire il file oggetto condiviso: File o directory non esistente
    RosieLib INSTANCE = Native.loadLibrary("rosie", RosieLib.class);


    // str rosie_new_string(byte_ptr msg, size_t len);
    RosieString.ByValue rosie_new_string(String msg, int len);

    // str *rosie_new_string_ptr(byte_ptr msg, size_t len);
    RosieString rosie_new_string_ptr(String msg, int len);

    // str rosie_string_from(byte_ptr msg, size_t len);
    RosieString.ByValue rosie_string_from(String msg, int len);

    // str *rosie_string_ptr_from(byte_ptr msg, size_t len);
    RosieString rosie_string_ptr_from(String msg, int len);

    // void rosie_free_string(str s);
    void rosie_free_string(RosieString.ByValue s);

    // void rosie_free_string_ptr(str *s);
    void rosie_free_string_ptr(RosieString s);

    // Engine *rosie_new(str *messages);
    Pointer rosie_new(RosieString messages);

    // void rosie_finalize(Engine *e);
    void rosie_finalize(Pointer e);

    // int rosie_libpath(Engine *e, str *newpath);
    int rosie_libpath(Pointer e, RosieString newpath);

    // int rosie_alloc_limit(Engine *e, int *newlimit, int *usage);
    int rosie_alloc_limit(Pointer e, IntByReference newlimit, IntByReference usage);

    // int rosie_config(Engine *e, str *retvals);
    int rosie_config(Pointer e, RosieString retvals);

    // int rosie_compile(Engine *e, str *expression, int *pat, str *messages);
    int rosie_compile(Pointer e, RosieString expression, IntByReference pat, RosieString messages);

    // int rosie_free_rplx(Engine *e, int pat);
    int rosie_free_rplx(Pointer e, int pat);

    // int rosie_match(Engine *e, int pat, int start, char *encoder, str *input, match *match);
    int rosie_match(Pointer e, int pat, int start, String encoder, RosieString input, RosieMatch match);

    // int rosie_matchfile(Engine *e, int pat, char *encoder, int wholefileflag,
    //                char *infilename, char *outfilename, char *errfilename,
    //                int *cin, int *cout, int *cerr,
    //                str *err);
    int rosie_matchfile(Pointer e, int pat, String encoder, int wholefileflag,
                        String infilename, String outfilename, String errfilename,
                        IntByReference cin, IntByReference cout, IntByReference cerr,
                        RosieString err);

    // int rosie_trace(Engine *e, int pat, int start, char *trace_style, str *input, int *matched, str *trace);
    int rosie_trace(Pointer e, int pat, int start, String trace_style, RosieString input,
                    IntByReference matched, RosieString trace);

    // int rosie_load(Engine *e, int *ok, str *src, str *pkgname, str *messages);
    int rosie_load(Pointer e, IntByReference ok, RosieString src, RosieString pkgname, RosieString messages);

    // int rosie_loadfile(Engine *e, int *ok, str *fn, str *pkgname, str *messages);
    int rosie_loadfile(Pointer e, IntByReference ok, RosieString fn, RosieString pkgname, RosieString messages);

    // int rosie_import(Engine *e, int *ok, str *pkgname, str *as, str *actual_pkgname, str *messages);
    int rosie_import(Pointer e, IntByReference ok, RosieString pkgname, RosieString as, RosieString actual_pkgname, RosieString messages);

    // int rosie_read_rcfile(Engine *e, str *filename, int *file_exists, str *options, str *messages);
    int rosie_read_rcfile(Pointer e, RosieString filename, IntByReference file_exists, RosieString options, RosieString messages);

    // int rosie_execute_rcfile(Engine *e, str *filename, int *file_exists, int *no_errors, str *messages);
    int rosie_execute_rcfile(Pointer e, RosieString filename, IntByReference file_exists, IntByReference no_errors, RosieString messages);
}

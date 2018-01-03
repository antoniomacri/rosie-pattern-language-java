package com.github.antoniomacri.rosie;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


public interface RosieLib extends Library {
    // str rosie_new_string(byte_ptr msg, size_t len);
    RosieString.ByValue rosie_new_string(String msg, int len);

    // str *rosie_new_string_ptr(byte_ptr msg, size_t len);
    RosieString rosie_new_string_ptr(String msg, int len);

    // void rosie_free_string(str s);
    void rosie_free_string(RosieString.ByValue s);

    // void rosie_free_string_ptr(str *s);
    void rosie_free_string_ptr(RosieString s);

    // void *rosie_new(str *errors);
    Pointer rosie_new(RosieString errors);

    // void rosie_finalize(void *L);
    void rosie_finalize(Pointer L);

    // int rosie_setlibpath(void *L, char *newpath);
    int rosie_setlibpath(Pointer L, String newpath);

    // int rosie_set_alloc_limit(void *L, int newlimit);
    int rosie_set_alloc_limit(Pointer L, int newlimit);

    // int rosie_config(void *L, str *retvals);
    int rosie_config(Pointer L, RosieString retvals);

    // int rosie_compile(void *L, str *expression, int *pat, str *errors);
    int rosie_compile(Pointer L, RosieString expression, IntByReference pat, RosieString errors);

    // int rosie_free_rplx(void *L, int pat);
    int rosie_free_rplx(Pointer L, int pat);

    // int rosie_match(void *L, int pat, int start, char *encoder, str *input, match *match);
    int rosie_match(Pointer L, int pat, int start, String encoder, RosieString input, RosieMatch match);

    // int rosie_matchfile(void *L, int pat, char *encoder, int wholefileflag,
    //                char *infilename, char *outfilename, char *errfilename,
    //                int *cin, int *cout, int *cerr,
    //                str *err);
    int rosie_matchfile(Pointer L, int pat, String encoder, int wholefileflag,
                        String infilename, String outfilename, String errfilename,
                        IntByReference cin, IntByReference cout, IntByReference cerr,
                        RosieString err);

    // int rosie_trace(void *L, int pat, int start, char *trace_style, str *input, int *matched, str *trace);
    int rosie_trace(Pointer L, int pat, int start, String trace_style, RosieString input,
                    IntByReference matched, RosieString trace);

    // int rosie_load(void *L, int *ok, str *src, str *pkgname, str *errors);
    int rosie_load(Pointer L, IntByReference ok, RosieString src, RosieString pkgname, RosieString errors);

    // int rosie_import(void *L, int *ok, str *pkgname, str *as, str *errors);
    int rosie_import(Pointer L, IntByReference ok, RosieString pkgname, RosieString as, RosieString errors);
}

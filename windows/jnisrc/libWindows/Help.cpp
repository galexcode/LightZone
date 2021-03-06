/* Copyright (C) 2005-2011 Fabio Riccardi */

// standard
#include <windows.h>

// local
#include "LC_JNIUtils.h"
#include "LC_WinUtils.h"
#ifndef AUTO_DEP
#include "javah/com_lightcrafts_platform_windows_WindowsHelp.h"
#endif
#include "htmlhelp.h"

using namespace std;
using namespace LightCrafts;

////////// JNI ////////////////////////////////////////////////////////////////

#define WindowsHelp_METHOD(method) \
        name4(Java_,com_lightcrafts_platform_windows_WindowsHelp,_,method)

/**
 * Shows the HTML help window.
 */
JNIEXPORT void JNICALL WindowsHelp_METHOD(showHelpForLanguage)
    ( JNIEnv *env, jclass, jstring jHTMLFile, jstring jISO639LangCode )
{
    jstring_to_w const wISO639LangCode( env, jISO639LangCode );
    WCHAR wBuf[ 80 ];
    ::wcscpy( wBuf, LC_getAppName() );
    ::wcscat( wBuf, TEXT("-") );
    ::wcscat( wBuf, wISO639LangCode );
    ::wcscat( wBuf, TEXT(".chm") );
    if ( jHTMLFile ) {
        ::wcscat( wBuf, TEXT("::/LightZone_Help/") );
        jstring_to_w const wHTMLFile( env, jHTMLFile );
        ::wcscat( wBuf, wHTMLFile );
        ::wcscat( wBuf, TEXT(".html") );
    }
    ::wcscat( wBuf, TEXT(">Main") );
    HtmlHelp( ::GetDesktopWindow(), wBuf, HH_DISPLAY_TOC, 0 );
}
/* vim:set et sw=4 ts=4: */

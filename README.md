GLPKAndroid
===========

A port of the GNU Linear Programming Kit (GLPK) for use on Android systems.

Audience
========
This project is a complete Android app that includes GLPK as an NDK library.  The app itself isn't the most exciting thing in the world, but it should demonstrate the capabilities of the GLPK library.  The app does allow for the entry of arbitrary linear programs using the CPLEX LP format, so some simple LP's should be solvable using the app.

Given all of this, the audience is likely those that might be interested in using linear programming capability within their own app and want the power of a full-featured LP library like GLPK.

Goals
=====
I hope that any developers that take a look at this feel free to contribute improvements to the project.  I'd be happy to add any interested and active developers to this repository to push changes.

Building
========
This repo is basically just my entire Eclipse project pushed to Github.  That may or may not make it easy to incorporate to your environment.  If anyone has any suggestions on how to make ti cleaner and easier for folks to import this project, just let me know.  I'm not the most adept at git.

Notes
=====
Some hand editing of the source files and configuration files was required to get all of this to build properly.  I'll try and document those changes here along with the steps I used to get NDK to build GLPK, but I can't guarantee this is a complete list.  If anyone tries to duplicate the steps necessary to build the GLPK library with NDK and finds issues, please let me know.

## Code fixes
NDK doesn't like the assignment of a variable to iteself.  So in *glpios01.c*, the line:
```
for (node = node; node != NULL; node = node->up)
```
needed to be changed to:
```
for (; node != NULL; node = node->up)
```

For some reason, I needed to add a header to *glpapi01.c*:
```
#include "stdlib.h"
```

The edit I am least satisfied with and would appreciate help from the community to fix, is the addition of the following definitions in *glpk.h*.  I could not the NDK compiler to find the right headers.  So I had to add definitions for floating point math:
```
/* Added since NDK having trouble finding headers. This is not a good idea. */
#ifndef DBL_MAX
#define DBL_MAX	1.7976931348623157E+308
#define DBL_MIN	2.2250738585072014E-308
#endif
```
Again, I want to emphasize that I am not happy about having to do that.  Something about compiler settings in Eclipse for NDK such that I could get all the headers found.

## SWIG
I used the wonderful SWIG tool to generate the jni files.  To do that I used my *glpk_android.i* which you will find in the scripts directory.  It is simple and just looks like:
```
%module glpk
 %{
 /* Includes the header in the wrapper code */
 #include "glpk.h"
 %}
 
%include carrays.i
%array_functions( double, double_array )
%array_functions( int, int_array )

 /* Parse the header file to generate wrappers */
 %include "glpk.h"
```
Note that I had to include help for array functions.  That was painful to learn.

To run this with SWIG, I issued the following command in the glpk C source directory:
```
swig -java -package com.littlepancake.glpk.jni glpk_android.i
```
Then copy the resulting Java files to the Android project (<project_dir>/src/com/littlepancake/glpk/jni).

## Configure NDK build
I've included a script that I wrote to create the Android.mk file.  It's called *generate_android_mk.sh* and should be self-explanatory.


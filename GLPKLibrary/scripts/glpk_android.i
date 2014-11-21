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


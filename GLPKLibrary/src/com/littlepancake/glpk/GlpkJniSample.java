package com.littlepancake.glpk;

import java.text.DecimalFormat;

import com.littlepancake.glpk.jni.GLPK;
import com.littlepancake.glpk.jni.GlpkConstants;
import com.littlepancake.glpk.jni.SWIGTYPE_p_double;
import com.littlepancake.glpk.jni.SWIGTYPE_p_glp_prob;
import com.littlepancake.glpk.jni.SWIGTYPE_p_int;

public class GlpkJniSample {
	
	/* This example problem is exactly the same as the one presented
	 * in the GLPK manual.  
	 */
	public static void sample() {
		DecimalFormat df = new DecimalFormat("0.00");
		
		/*
		Maximize z = 10x1 + 6x2 + 4x3
		subject to:
			p=   x1 + x2 + x3 
			q= 10x1 +4x2 +5x3 
			r=  2x1 +2x2 +6x3
		and bounds of variables
		−∞<p≤100 −∞<q≤600 −∞<r≤300
		0≤x1 <+∞ 0≤x2 <+∞ 0≤x3 <+∞
		*/

		final int ARRAY_SIZE = 10;
		double z, x1, x2, x3; /* Results will be saved in these variables. */
		
		/* Create our arrays. */
		SWIGTYPE_p_int ia = 
				GLPK.new_int_array(ARRAY_SIZE);
		SWIGTYPE_p_int ja = 
				GLPK.new_int_array(ARRAY_SIZE);
		SWIGTYPE_p_double ar = 
				GLPK.new_double_array(ARRAY_SIZE);
		
		/* Create the LP instance. */
		SWIGTYPE_p_glp_prob lp;		
		lp = GLPK.glp_create_prob();
		GLPK.glp_set_prob_name(lp, "sample");
		GLPK.glp_set_obj_dir(lp, GLP_MAX);
		
		/* Add some rows. */
		GLPK.glp_add_rows(lp, 3);
		GLPK.glp_set_row_name(lp, 1, "p");
		GLPK.glp_set_row_bnds(lp, 1, GLP_UP, 0.0, 100.0);
		GLPK.glp_set_row_name(lp, 2, "q");
		GLPK.glp_set_row_bnds(lp, 2, GLP_UP, 0.0, 600.0);
		GLPK.glp_set_row_name(lp, 3, "r");
		GLPK.glp_set_row_bnds(lp, 3, GLP_UP, 0.0, 300.0);
		
		/* Add some columns. */
		GLPK.glp_add_cols(lp, 3);
		GLPK.glp_set_col_name(lp, 1, "x1");
		GLPK.glp_set_col_bnds(lp, 1, GLP_LO, 0.0, 0.0);
		GLPK.glp_set_obj_coef(lp, 1, 10.0);
		GLPK.glp_set_col_name(lp, 2, "x2");
		GLPK.glp_set_col_bnds(lp, 2, GLP_LO, 0.0, 0.0);
		GLPK.glp_set_obj_coef(lp, 2, 6.0);
		GLPK.glp_set_col_name(lp, 3, "x3");
		GLPK.glp_set_col_bnds(lp, 3, GLP_LO, 0.0, 0.0);
		GLPK.glp_set_obj_coef(lp, 3, 4.0);
		
		/* Set the constraint matrix (A) values. */

		/*    A[1,1] = 1.0 */
		GLPK.int_array_setitem(ia, 1, 1); 
		GLPK.int_array_setitem(ja, 1, 1); 
		GLPK.double_array_setitem(ar, 1, 1.0);
		
		/*    A[1,2] = 1.0 */
		GLPK.int_array_setitem(ia, 2, 1); 
		GLPK.int_array_setitem(ja, 2, 2); 
		GLPK.double_array_setitem(ar, 2, 1.0);
		
		/*    A[1,3] = 1.0 */
		GLPK.int_array_setitem(ia, 3, 1); 
		GLPK.int_array_setitem(ja, 3, 3); 
		GLPK.double_array_setitem(ar, 3, 1.0);
		
		/*    A[2,1] = 10.0 */
		GLPK.int_array_setitem(ia, 4, 2); 
		GLPK.int_array_setitem(ja, 4, 1); 
		GLPK.double_array_setitem(ar, 4, 10.0);
		
		/*    A[2,2] = 4.0 */
		GLPK.int_array_setitem(ia, 6, 2); 
		GLPK.int_array_setitem(ja, 6, 2); 
		GLPK.double_array_setitem(ar, 6, 4.0);
		
		/*    A[2,3] = 5.0 */
		GLPK.int_array_setitem(ia, 8, 2); 
		GLPK.int_array_setitem(ja, 8, 3); 
		GLPK.double_array_setitem(ar, 8, 5.0);
		
		/*    A[3,1] = 2.0 */
		GLPK.int_array_setitem(ia, 5, 3); 
		GLPK.int_array_setitem(ja, 5, 1); 
		GLPK.double_array_setitem(ar, 5, 2.0);
		
		/*    A[3,2] = 2.0 */
		GLPK.int_array_setitem(ia, 7, 3); 
		GLPK.int_array_setitem(ja, 7, 2); 
		GLPK.double_array_setitem(ar, 7, 2.0);
		
		/*    A[3,3] = 6.0 */
		GLPK.int_array_setitem(ia, 9, 3); 
		GLPK.int_array_setitem(ja, 9, 3); 
		GLPK.double_array_setitem(ar, 9, 6.0);
		
		GLPK.glp_load_matrix(lp, 9, ia, ja, ar);
		
		/* Solve the LP and retrieve values. */
		GLPK.glp_simplex(lp, null);		
		z  = GLPK.glp_get_obj_val(lp);
		x1 = GLPK.glp_get_col_prim(lp, 1);
		x2 = GLPK.glp_get_col_prim(lp, 2);
		x3 = GLPK.glp_get_col_prim(lp, 3);
		
		/* Save result string for other classes to access. */
		solutionString = 
				"z = "+df.format(z)+
				"; x1 = "+df.format(x1)+
				"; x2 = "+df.format(x2)+
				"; x3 = "+df.format(x3);

		/* Tidy up. */
		GLPK.glp_delete_prob(lp);
	}
	
	/* Shorten the names of some constants. */
	static int GLP_MAX = GlpkConstants.GLP_MAX;
	static int GLP_LO = GlpkConstants.GLP_LO;
	static int GLP_UP = GlpkConstants.GLP_UP;
		
	public static String solutionString = "NOT YET SET";
}




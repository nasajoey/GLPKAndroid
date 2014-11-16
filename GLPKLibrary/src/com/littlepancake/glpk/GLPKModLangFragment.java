/*
	Copyright 2014, Joseph Rios, Littlepancake Software, info@littlepancake.com

	This file is part of the GLPK on Android.

    GLPK on Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GLPK on Android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GLPK on Android.  If not, see <http://www.gnu.org/licenses/>.

*/
package com.littlepancake.glpk;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.littlepancake.glpk.jni.GLPK;
import com.littlepancake.glpk.jni.GlpkConstants;
import com.littlepancake.glpk.jni.SWIGTYPE_p_glp_prob;
import com.littlepancake.glpk.jni.glp_cpxcp;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class GLPKModLangFragment extends Fragment implements OnClickListener {

	private final String tag = getClass().getSimpleName();
	private final String outputFileName = "output.lp";

	private EditText problemEntryEditText;
	private Button buttonSolve;
	private Button buttonClear;
	private Button buttonComments;
	
	@Override
	public void onResume() {
		super.onResume();
		if(problemEntryEditText.getText().toString().length() <= 0 ) {
			appendInitialProblem();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_glpktest, container, false);
		problemEntryEditText = (EditText) v.findViewById(R.id.problemEntryEditText);
		buttonSolve = (Button) v.findViewById(R.id.buttonSolve);
		buttonClear = (Button) v.findViewById(R.id.buttonClear);
		buttonComments = (Button) v.findViewById(R.id.buttonComments);

		buttonSolve.setOnClickListener(this);
		buttonClear.setOnClickListener(this);
		buttonComments.setOnClickListener(this);

		Resources res = getResources();
		AssetManager am = res.getAssets();

		String fileList[] = null;
		try {
			fileList = am.list("");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (fileList != null) {   
			for ( int i = 0;i<fileList.length;i++) {
				Log.d(tag,fileList[i]); 
			}
		}
		return v;
	}

	private class SolveModel extends AsyncTask<String, Void, Integer> {

		SWIGTYPE_p_glp_prob lp;
		private ProgressDialog pdia;

		@Override
		protected Integer doInBackground(String... params) {

			try {
				writeOptFile(outputFileName, problemEntryEditText.getText().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			lp = GLPK.glp_create_prob();
			double z, x1, x2;
			glp_cpxcp cpxcp = new glp_cpxcp();
			GLPK.glp_init_cpxcp(cpxcp);
			int ret = GLPK.glp_read_lp(lp, cpxcp, getActivity().getFilesDir()+"/"+outputFileName);
			if( ret != 0 ) {
				Log.d(tag,  "Returning -1.");
				return -1;
			}
			GLPK.glp_simplex(lp, null);
			
			z = GLPK.glp_get_obj_val(lp);
			x1 = GLPK.glp_get_col_prim(lp, 1);
			x2 = GLPK.glp_get_col_prim(lp, 2);
			Log.d(tag, "Solution is  : "+z);
			Log.d(tag, "With col vals: "+x1+", "+x2);

			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if( result >= 0 ) {
				int status = GLPK.glp_get_status(lp);

				if( status == GlpkConstants.GLP_OPT )
					appendSolution(lp);
				else if( status == GlpkConstants.GLP_INFEAS ) {
					appendMessage("\\* The problem is infeasible. *\\\n");
				}
				else if( status == GlpkConstants.GLP_UNBND ){
					appendMessage("\\* The problem has an unbounded solution. *\\\n");
				}
				else if( status == GlpkConstants.GLP_UNDEF ){
					appendMessage("\\* The solution is undefined. *\\\n");
				}
				else {
					appendMessage("\\* No error, but no optimal solution. *\\\n");
				}				
			}
			else 
				appendError("Could not read the problem correctly");
			pdia.dismiss();
		}

		@Override
		protected void onPreExecute() {
			pdia = new ProgressDialog(getActivity());
			pdia.setMessage("Solving... please wait.");
			pdia.show(); 
		}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();

		if( id == buttonSolve.getId()) {
			Log.d(tag, "Solve Button pressed.");
			new SolveModel().execute("");
		}
		else if( id == buttonClear.getId() ) {
			problemEntryEditText.setText("");
		}
		else if( id == buttonComments.getId() ) {
			removeComments();
		}

	}
	
	private void removeComments() {
		String s = problemEntryEditText.getText().toString();
		String[] lines = s.split("\n");
		StringBuilder sb = new StringBuilder();
		for( String line : lines ) {
			if( !line.startsWith("\\") ) {
				sb.append(line+"\n");
			}
		}
		problemEntryEditText.setText(sb.toString());
	}

	private void appendInitialProblem() {
		StringBuffer sb = new StringBuffer();

		sb.append("Max\n");
		sb.append(" 2x\n");
		sb.append("Subject to\n");
		sb.append(" x + y <= 5\n");
		sb.append(" x <= 3\n");
		sb.append(" y >= 3\n");
		sb.append("End\n");

		problemEntryEditText.append(sb.toString());
	}

	private void appendSolution(SWIGTYPE_p_glp_prob lp) {
		StringBuffer sb = new StringBuffer();
		sb.append("\\* Solution : *\\\n");
		sb.append("\\\\ Objective = "+GLPK.glp_get_obj_val(lp)+"\n");
		sb.append("\\* Non-zero vars : *\\\n");

		for( int i = 1; i <= GLPK.glp_get_num_cols(lp); i++ ) {
			if( GLPK.glp_get_col_prim(lp, i) != 0.0 ) {
				sb.append("\\\\ "+GLPK.glp_get_col_name(lp, i)+"  = "+GLPK.glp_get_col_prim(lp, i)+"\n");
			}
		}
		sb.append("\\* End solution *\\\n");
		problemEntryEditText.append(sb.toString());
	}
	private void appendError(String errorString) {

		StringBuffer sb = new StringBuffer();
		sb.append("\\* Error. *\\\n");
		sb.append("\\* "+errorString+" *\\\n");
		problemEntryEditText.append(sb.toString());
	}
	private void appendMessage(String message) {
		problemEntryEditText.append(message);
	}

	public void writeOptFile(String filename, String lines) throws IOException {
		if( filename == null ) filename = "out.lp";
		String fullFileName = getActivity().getFilesDir()+"/"+filename;
		PrintWriter pw = new PrintWriter(new FileWriter(fullFileName)); 
		pw.write(lines);
		pw.flush();
		pw.close();
	}

}

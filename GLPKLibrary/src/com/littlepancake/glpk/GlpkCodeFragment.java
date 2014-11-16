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

import java.io.IOException;
import java.io.InputStream;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class GlpkCodeFragment extends Fragment implements OnClickListener{

	private final String tag = getClass().getSimpleName();
	private Button solveButton;
	private TextView textViewSolution;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.code_sample_layout, container, false);
		TextView tv = (TextView) v.findViewById(R.id.textViewCode);
		solveButton = (Button) v.findViewById(R.id.buttonSolve);
		solveButton.setOnClickListener(this);
		textViewSolution = (TextView) v.findViewById(R.id.textViewSolution);
		
		InputStream is;
		byte[] buffer = null;
		try {
			is = getActivity().getAssets().open("test.html");

			int size = is.available();

			buffer = new byte[size];
			is.read(buffer);
			is.close();
		} catch (IOException e) {
			tv.setMovementMethod(new ScrollingMovementMethod());			
			tv.setText(e.getMessage());
			e.printStackTrace();
		}

		if( buffer != null ) {
			String str = new String(buffer);
			Spanned result = Html.fromHtml(str);
			tv.setMovementMethod(new ScrollingMovementMethod());

			tv.setText(result);
		}
		
		
		return v;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if( id == solveButton.getId() ) {
			new SolveProblem().execute("");
		}

	}
	
	private class SolveProblem extends AsyncTask<String, Void, String> {

		private ProgressDialog pdia;
		
        @Override
        protected String doInBackground(String... params) {
        	GlpkJniSample.sample();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        	textViewSolution.setTextAppearance(getActivity(), android.R.style.TextAppearance_Small);
        	textViewSolution.setText(GlpkJniSample.solutionString);
        	pdia.dismiss();
        }

        @Override
        protected void onPreExecute() {
        	pdia = new ProgressDialog(getActivity());
            pdia.setMessage("Solving... please wait.");
            pdia.show(); 
        }

    }
}

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressLint("WorldReadableFiles")
@SuppressWarnings("deprecation")
public class HelpFragment extends Fragment implements OnClickListener {
	
	private final String glpkPdfName = "glpk.pdf";
	private final String gplv3FileName = "gplv3.txt";
	private Button manualButton, gplButton;
	private final int buff_size = 1024;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.help_layout, container, false);
		manualButton = (Button) v.findViewById(R.id.readButton);
		manualButton.setOnClickListener(this);
		gplButton = (Button) v.findViewById(R.id.readGplv3);
		gplButton.setOnClickListener(this);
		return v;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	/* Taken from here:
	 * http://stackoverflow.com/questions/17085574/read-a-pdf-file-from-assets-folder
	 * with a few small changes.
	 */
	private void openGlpkPdf()
	{
		AssetManager assetManager = getActivity().getAssets();

		File file = new File(getActivity().getFilesDir(), glpkPdfName);

		if( !file.exists() ) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(glpkPdfName);
				/* Note that "MODE_WORLD_READABLE" is "dangerous". It seems the alternative is
				 * for me to write a few hundred more lines of code or something. Guess I'll
				 * remain dangerous for this app.
				 * See:
				 *  https://github.com/commonsguy/cw-omnibus/tree/master/ContentProvider/Files
				 * for the proper, code-intensive way to handle this.
				 */
				out = getActivity().openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);
				byte[] buffer = new byte[buff_size];
				int read;
				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
				}
				in.close();
				out.flush();
				out.close();
				out = null;
				in = null;
			} catch (IOException e) {
				Log.e("tag", e.getMessage());
			}
		}
		else {
			//Log.d("tag", glpkPdfName+" already exists.");
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(
				Uri.parse("file://" + getActivity().getFilesDir() + "/"+glpkPdfName),
				"application/pdf");
		startActivity(intent);
	}
	
	private void openGplTxt()
	{
		AssetManager assetManager = getActivity().getAssets();

		File file = new File(getActivity().getFilesDir(), gplv3FileName);

		if( !file.exists() ) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(gplv3FileName);
				/* Note that "MODE_WORLD_READABLE" is "dangerous". It seems the alternative is
				 * for me to write a few hundred more lines of code or something. Guess I'll
				 * remain dangerous for this app.
				 * See:
				 *  https://github.com/commonsguy/cw-omnibus/tree/master/ContentProvider/Files
				 * for the proper, code-intensive way to handle this.
				 */
				out = getActivity().openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);
				byte[] buffer = new byte[buff_size];
				int read;
				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
					Log.d("tag", "Read and wrote "+read+" bytes.");
				}
				in.close();
				out.flush();
				out.close();
			} catch (IOException e) {
				Log.e("tag", e.getMessage());
			}
		}
		else {
			//Log.d("tag", gplv3FileName+" already exists.");
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(
				Uri.parse("file://" + getActivity().getFilesDir() + "/"+gplv3FileName),
				"text/plain");
		startActivity(intent);
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		if( id == manualButton.getId() ) {
			openGlpkPdf();
		}
		else if( id == gplButton.getId() ) {
			openGplTxt();
		}
	}
}

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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

public class GlpkTabActivity extends FragmentActivity {
	public static final String TAB_LABEL_MODL = "Model Language";
	public static final String TAB_LABEL_PROG = "API Example";
	public static final String TAB_LABEL_HELP = "Help";
	public static final String LIB_NAME_GLPK  = "glpk";
	
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tab_host);
        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec(TAB_LABEL_MODL).setIndicator(TAB_LABEL_MODL,
                        getResources().getDrawable(android.R.drawable.star_on)),
                        GLPKModLangFragment.class, null);
        
        mTabHost.addTab(
                mTabHost.newTabSpec(TAB_LABEL_PROG).setIndicator(TAB_LABEL_PROG,
                        getResources().getDrawable(android.R.drawable.star_on)),
                GlpkCodeFragment.class, null);
        
        mTabHost.addTab(
                mTabHost.newTabSpec(TAB_LABEL_HELP).setIndicator(TAB_LABEL_HELP,
                        getResources().getDrawable(android.R.drawable.star_on)),
                HelpFragment.class, null);
    }
    
	static {
		System.loadLibrary(LIB_NAME_GLPK);
	}
}
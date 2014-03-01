/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid.Helpers;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Klasse zum wechseln der Tabs
 * @author Osiris
 */
public class TabListener<T extends SherlockFragment> implements ActionBar.TabListener 
{
    private SherlockFragment mFragment;
    private final SherlockFragmentActivity mActivity;
    private final Class<T> mClass;

    /** Constructor used each time a new tab is created.
      * @param activity  The host Activity, used to instantiate the fragment
      * @param tag  The identifier tag for the fragment
      * @param clz  The fragment's Class, used to instantiate the fragment
      */
    public TabListener(SherlockFragmentActivity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mClass = clz;
    }

    /* The following are each of the ActionBar.TabListener callbacks */

    public void onTabSelected(Tab tab, FragmentTransaction ftignore) {
        FragmentManager fragMgr = mActivity.getSupportFragmentManager();
        FragmentTransaction ft = fragMgr.beginTransaction();
        
        // Check if the fragment is already initialized
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = (SherlockFragment) SherlockFragment.instantiate(mActivity, mClass.getName());
            
        } 
        ft.replace(android.R.id.content, mFragment);
        
        ft.commit();
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }
}

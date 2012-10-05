package com.nix.loadeer;

import static com.nix.loadeer.ApiConstant.REPLACER;
import static com.nix.loadeer.ApiConstant.SEARCH_API;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Nixit
 * Date: 10/4/12
 * Time: 2:51 PM
 */
public class SearchResultLoader extends AsyncTaskLoader<ArrayList<SearchResult>> implements ArgsChangeObserver {

    private String url;
    @SuppressWarnings("unused")
	private String msg;
    private String args;
    private ArrayList<SearchResult> mResult;

    final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();
    private ArgsChangeListener argsChangeListener;
    private static final String TAG = SearchResultLoader.class.getSimpleName();

    public SearchResultLoader(Context context, ArgsChangeListener argsChangeListener) {
        super(context);
        this.argsChangeListener = argsChangeListener;
//        this.url = SEARCH_API.replace(REPLACER, args);
    }

    /**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    
    
    @SuppressWarnings("finally")
	@Override
    public ArrayList<SearchResult> loadInBackground() {
        if (!Utils.isNullOrEmpty(args))
            if (Utils.isOnline(getContext())) {
                String response = Utils.readJson(url);
                if (!Utils.isNullOrEmpty(response)) {
                    ArrayList<SearchResult> resultList = null;
                    try {
                        resultList = new ArrayList<SearchResult>();
                        JSONArray jArray = new JSONObject(response).getJSONObject("ResultSet").getJSONArray("Result");
                        for (int i = 0; i < jArray.length(); i++) {
                            SearchResult searchResult = new SearchResult();
                            JSONObject jObject = jArray.getJSONObject(i);
                            if(jObject.has("symbol"))
                            	searchResult.setSymbole(jObject.getString("symbol"));
                            if(jObject.has("name"))
                            	searchResult.setName(jObject.getString("name"));
                            if(jObject.has("exch"))
                            	searchResult.setExch(jObject.getString("exch"));
                            if(jObject.has("type"))
                            	searchResult.setType(jObject.getString("type"));
                            if(jObject.has("exchDisp"))
                            	searchResult.setExchDisp(jObject.getString("exchDisp"));
                            if(jObject.has("typeDisp"))
                            	searchResult.setTypeDisp(jObject.getString("typeDisp"));
                            resultList.add(searchResult);
                        }
                        msg = getContext().getString(R.string.success_msg);
                    } catch (JSONException e) {
                        msg = getContext().getString(R.string.our_mistake);
                        e.printStackTrace();
                    } finally {
                        Collections.sort(resultList, ALPHA_COMPARATOR);
                        return resultList;
                    }
                } else {
                    msg = getContext().getString(R.string.internal_server_error);
                }
            } else {
                msg = getContext().getString(R.string.no_iternet);
            }
        return null;
    }

    /**
     * Called when there is new data to deliver to the client. The super class
     * will take care of delivering it; the implementation here just adds a
     * little more logic.
     */
    @Override
    public void deliverResult(ArrayList<SearchResult> result) {
        if (isReset()) {
            // An async query came in while the loader is stopped. We
            // don't need the result.
            if (result != null) {
                onReleaseResources(result);
            }
        }

        ArrayList<SearchResult> oldResult = result;

        mResult = result;
        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(result);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldResult != null) {
            onReleaseResources(oldResult);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {

        if (mResult != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mResult);
        }
        // Has something interesting in the configuration changed since we
        // last built the app list?

        argsChangeListener.addObserver(this);

        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources(), args);

        if (takeContentChanged() || mResult == null || configChange) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
        super.onStartLoading();
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(ArrayList<SearchResult> resultList) {
        super.onCanceled(resultList);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(resultList);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mResult != null) {
            onReleaseResources(mResult);
            mResult = null;
        }

        argsChangeListener.removeObserver(this);
//        if (mPackageObserver != null) {
//            getContext().unregisterReceiver(mPackageObserver);
//            mPackageObserver = null;
//        }
    }


    /**
     * Helper function to take care of releasing resources associated with an
     * actively loaded data set.
     */
    private void onReleaseResources(ArrayList<SearchResult> result) {
        // For a simple List<> there is nothing to do. For something
        // like a Cursor, we would close it here.
    }

    @Override
    public void updateResultList(String args) {
        this.args = args;
        this.url = SEARCH_API.replace(REPLACER, args);
        Log.i(TAG, this.url);
        onContentChanged();
    }

    public static class InterestingConfigChanges {
        final Configuration mLastConfiguration = new Configuration();
        int mLastDensity;

        /**
         * Helper for determining if the configuration has changed in an interesting
         * way so we need to rebuild the app list.
         */
        boolean applyNewConfig(Resources res, String currentString) {
            int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
            boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;
            if (densityChanged || (configChanges & (ActivityInfo.CONFIG_LOCALE
                    | ActivityInfo.CONFIG_UI_MODE | ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
                mLastDensity = res.getDisplayMetrics().densityDpi;
                return true;
            }
            return false;
        }
    }

    /**
     * Perform alphabetical comparison of application entry objects.
     */
    public static final Comparator<SearchResult> ALPHA_COMPARATOR = new Comparator<SearchResult>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(SearchResult object1, SearchResult object2) {
            return sCollator.compare(object1.getSymbole(), object2.getSymbole());
        }
    };


}

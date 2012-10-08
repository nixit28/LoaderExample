package com.nix.loadeer;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ResultListFragment extends ListFragment implements TextWatcher,
		LoaderManager.LoaderCallbacks<ArrayList<SearchResult>> {

	private static final String TAG = ResultListFragment.class.getSimpleName();
	private ArrayAdapter<SearchResult> mAdapter;
	private ArgsChangListnerImpl argsChangeListener;
	private ProgressBar progressBar;
	private EditText editSearch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main, container, false);

		// Set up EditText
		editSearch = (EditText) view.findViewById(R.id.edtSearchBox);
		editSearch.addTextChangedListener(this);
		
		progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);

//		ListView listView = (ListView) view.findViewById(android.R.id.list);
		mAdapter = new ArrayAdapter<SearchResult>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<SearchResult>());
		setListAdapter(mAdapter);
		argsChangeListener = new ArgsChangListnerImpl();

		getLoaderManager().initLoader(0, null, this);
		return view;
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1,
			int i2) {
	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		if(!(getListView().getCount()>0)){
			progressBar.setVisibility(View.VISIBLE);
		}
		argsChangeListener.notifyObserver(charSequence.toString());
	}

	@Override
	public void afterTextChanged(Editable editable) {
	}

	@Override
	public Loader<ArrayList<SearchResult>> onCreateLoader(int i, Bundle bundle) {
		Log.i(TAG, "I am @ onCreatLoader");
		return new SearchResultLoader(getActivity(), argsChangeListener);
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<SearchResult>> arrayListLoader,
			ArrayList<SearchResult> searchResults) {
		mAdapter.clear();
		
		progressBar.setVisibility(View.GONE);
		
		Log.i(TAG, "I am @ onCreatLoader");
		if (searchResults != null){
			for (SearchResult result : searchResults)
				mAdapter.add(result);
		}else{
			if((editSearch.getText().length()>0))
				Toast.makeText(getActivity(), "Error Loading data", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<SearchResult>> arrayListLoader) {
	}

}

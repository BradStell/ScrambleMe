package com.elitetek.scrambleme;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.elitetek.scrambleme.database.DataManager;


public class MainFragment extends Fragment {

	
	ListView root;
    DataManager dataManager;
	ArrayList<ImagePairs> picturesList;
	private OnFragmentInteractionListener mListener;

	public MainFragment() {
		// Required empty public constructor
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FFF_Tusj.ttf");

        root = (ListView) getActivity().findViewById(R.id.listViewRoot);
        dataManager = new DataManager(getActivity());

        new GetPicturesAsyncTask().execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_main, container, false);
	}	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	public interface OnFragmentInteractionListener {
		public void fromMainFragment();
	}

    public class GetPicturesAsyncTask extends AsyncTask<Void, Void, ArrayList<ImagePairs>> {

        ProgressDialog progressDialog;

        @Override
        protected ArrayList<ImagePairs> doInBackground(Void... params) {
            // TODO code to retrieve pictures from database
            return dataManager.getAllSavedImages();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            picturesList = new ArrayList<ImagePairs>();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Fetching Pictures");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<ImagePairs> result) {
            super.onPostExecute(result);

            if (result != null) {
                GalleryListAdapter adapter = new GalleryListAdapter(getActivity(), result);
                adapter.setNotifyOnChange(true);
                root.setAdapter(adapter);
            } else {
                Toast.makeText(getActivity(), "No Scrambled Images Yet", Toast.LENGTH_SHORT).show();
            }

            progressDialog.dismiss();
        }
    }
}

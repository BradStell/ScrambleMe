package com.elitetek.scrambleme;

import com.elitetek.scrambleme.database.DataManager;
import com.parse.ParseUser;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


public class MainFragment extends Fragment {

	
	ListView root;
    DataManager dataManager;
    ArrayList<ImagePairs> cachedList;
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

        if (cachedList != null) {
            Log.d("demo", "in != null");

            cachedList = new ArrayList<ImagePairs>();

            for (int i = 0; i < MainActivity.picturesList.size(); i++) {
                if (MainActivity.picturesList.get(i).getOwnerName().compareTo(ParseUser.getCurrentUser().getString("username")) == 0) {
                    cachedList.add(MainActivity.picturesList.get(i));
                }
            }

            GalleryListAdapter adapter = new GalleryListAdapter(getActivity(), cachedList);
            adapter.setNotifyOnChange(true);
            root.setAdapter(adapter);
        } else {
            new GetPicturesAsyncTask().execute();
        }

        root.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int imagePairId;

                if (cachedList != null) {
                    ImagePairs clickedPair = cachedList.get(position);
                    imagePairId = clickedPair.getId();
                } else {
                    ImagePairs clickedPair = MainActivity.picturesList.get(position);
                    imagePairId = clickedPair.getId();
                }

                Log.d("clicked", imagePairId + "");

                /*ImageView normal = (ImageView) view.findViewById(R.id.linearLayoutNormalRoot).findViewById(R.id.imageViewNormal);
                ImageView scrambled = (ImageView) view.findViewById(R.id.linearLayoutScrambleRoot).findViewById(R.id.imageViewScrambled);

                Bitmap bitmapNormal = ((BitmapDrawable)normal.getDrawable()).getBitmap();
                Bitmap bitmapScrambled = ((BitmapDrawable)scrambled.getDrawable()).getBitmap();*/

                String pathToScrambledImage = dataManager.getScrambledImagePath(imagePairId);

                mListener.fromMainFragment(imagePairId, pathToScrambledImage);
            }
        });
	}

    public void removeItemFromArrayLists(int id) {

        for (int i = 0; i < cachedList.size(); i++) {
            if (cachedList.get(i).getId() == id)
                cachedList.remove(i);
        }
        for (int i = 0; i < MainActivity.picturesList.size(); i++) {
            if (MainActivity.picturesList.get(i).getId() == id)
                MainActivity.picturesList.remove(i);
        }
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
		public void fromMainFragment(int id, String imagePath);
	}

    /**
     * Inner AsynchTask for querying sql database for images
     */
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
            MainActivity.picturesList = new ArrayList<ImagePairs>();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Fetching Pictures");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<ImagePairs> result) {
            super.onPostExecute(result);

            if (result != null) {
                MainActivity.picturesList = result;
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

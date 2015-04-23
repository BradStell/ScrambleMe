package com.elitetek.scrambleme;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class MainFragment extends Fragment {

	
	ListView root;
	ArrayList<Image> picturesList;
	private OnFragmentInteractionListener mListener;

	public MainFragment() {
		// Required empty public constructor
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		
		//TextView title = (TextView) getActivity().findViewById(R.id.textViewGalleryTitle);
		Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FFF_Tusj.ttf");
		//title.setTypeface(titleFont);
		//title.setTextSize(getActivity().getResources().getDimension(R.dimen.main_title_text_size));
		//title.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
		
		
		/***********  Dummy Pictures for testing purposes *********************************/
		
		Image i1 = new Image();
		Bitmap map = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.me);
		i1.setOriginalPic(map);
		i1.setScrambledPic(map);
		
		Image i2 = new Image();
		map = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.plant);
		i2.setOriginalPic(map);
		i2.setScrambledPic(map);
		
		Image i3 = new Image();
		map = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.me);
		i3.setOriginalPic(map);
		i3.setScrambledPic(map);
		
		Image i4 = new Image();
		map = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.plant);
		i4.setOriginalPic(map);
		i4.setScrambledPic(map);
		
		Image i5 = new Image();
		map = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.me);
		i5.setOriginalPic(map);
		i5.setScrambledPic(map);
		
		Image i6 = new Image();
		map = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.plant);
		i6.setOriginalPic(map);
		i6.setScrambledPic(map);
		
		picturesList = new ArrayList<Image>();
		picturesList.add(i1);
		picturesList.add(i2);
		picturesList.add(i3);
		picturesList.add(i4);
		picturesList.add(i5);
		picturesList.add(i6);
		
		
		/***********  Dummy Pictures for testing purposes *********************************/
		
		root = (ListView) getActivity().findViewById(R.id.listViewRoot);
		GalleryListAdapter adapter = new GalleryListAdapter(getActivity(), picturesList);
		adapter.setNotifyOnChange(true);
		root.setAdapter(adapter);
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
}

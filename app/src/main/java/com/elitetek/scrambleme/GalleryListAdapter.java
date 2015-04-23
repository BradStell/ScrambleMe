package com.elitetek.scrambleme;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryListAdapter extends ArrayAdapter<Image> {
	
	Context context;
	ArrayList<Image> objects;
	
	public GalleryListAdapter(Context context, ArrayList<Image> objects) {
		super(context, R.layout.gallery_custom_view, objects);
		this.context = context;
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.gallery_custom_view, parent, false);

			holder = new ViewHolder();
			convertView.setTag(holder);
			holder.normalPic = (ImageView) convertView.findViewById(R.id.imageViewNormal);
			holder.scrambledPic = (ImageView) convertView.findViewById(R.id.imageViewScrambled);
		}

		holder = (ViewHolder) convertView.getTag();
		Image image = objects.get(position);
		holder.normalPic.setImageBitmap(image.getOriginalPic());
		holder.scrambledPic.setImageBitmap(image.getScrambledPic());

		return convertView;
	}

	static class ViewHolder {		
		ImageView normalPic;
		ImageView scrambledPic;
		
	}
}

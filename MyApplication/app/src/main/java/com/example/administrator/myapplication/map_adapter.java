package com.example.administrator.myapplication;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.CircleOptionsCreator;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2015/10/12.
 */
public class map_adapter extends BaseAdapter {
	public Context context;
	public List<Map<String,Object>> data ;
	public LayoutInflater layoutInflater ;
	public ImageLoader imageLoader ;


	public  map_adapter(Context context ,List<Map<String,Object>> listData){

		this.data = listData ;
		this.context =  context;
		this.layoutInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();




	}


	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}



	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {

		container contain = null;
		if(contain==null){
			contain = new container();
			view = layoutInflater.inflate(R.layout.map_listitem,null);

			contain.circleImageView = (CircleImageView)view.findViewById(R.id.circleImage);
			contain.user_name = (TextView)view.findViewById(R.id.user_name);
			contain.time = (TextView)view.findViewById(R.id.time);
			contain.content = (TextView)view.findViewById(R.id.content);
			contain.distance = (TextView)view.findViewById(R.id.distance);
           view.setTag(contain);

		}else {
			contain = (container)view.getTag();

		}

		imageLoader.displayImage(data.get(position).get("ImageUrl").toString(), contain.circleImageView);
		contain.user_name.setText(data.get(position).get("name").toString());
		contain.time.setText(data.get(position).get("time").toString());
		contain.content.setText(data.get(position).get("content").toString());
		contain.distance.setText(data.get(position).get("distance").toString());


		return view;
	}



	public final  class  container{
		public CircleImageView circleImageView ;
		public TextView distance ,user_name,time,content ;


	}
}

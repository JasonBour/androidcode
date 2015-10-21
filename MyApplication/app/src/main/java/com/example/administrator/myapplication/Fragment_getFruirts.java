package com.example.administrator.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/10/14.
 */
public class Fragment_getFruirts extends Fragment {
      public ImageView apple ,apricot,banana,cheery,kiwi,lemon,mango,orange,peach,pear,strawberry,tomato ;
	public View view ;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragement_getgruits, container, false);
             initView();

		return view;
	}

	private void initView() {
		//apple =(ImageView)view.findViewById(R.id.) ,apricot,banana,cheery,kiwi,lemon,mango,orange,peach,pear,strawberry,tomato

	}
}

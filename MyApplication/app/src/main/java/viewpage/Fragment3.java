package viewpage;




import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.login;

public class Fragment3 extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);



		View view = inflater.inflate(R.layout.fragment_4, container, false);
		
	
		TextView text = (TextView)view.findViewById(R.id.tvInNew);
		text.setOnClickListener( this );
		
		
		return view;
	}

	@Override
	public void onClick(View v) {


		Intent intent = new Intent();
		intent.setClass(getActivity(), login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

		
	}

}

package utils;

import android.content.Context;

import java.io.File;

/**
 * Created by Administrator on 2015/10/9.
 */
public class Logout {
	public static void clean_sharedPrefernces(Context context){
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/shared_prefs"));


	}

	public static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}
}

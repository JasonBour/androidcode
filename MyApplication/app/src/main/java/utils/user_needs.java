package utils;

/**
 * Created by Administrator on 2015/10/12.
 * 这个是每一个用户需求的相关信息
 */
public class user_needs  {

	private String latitude;  //用户的纬度
	private String longitude ;//用户的经度
    private String name ;  //用户名字
	private String content; //需求内容
    private String ImageUrl ;
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}


	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	 public  String getName(){
		 return  name;
	 }

	public   String getImageUrl(){
		return  ImageUrl ;
	}





	public String getPassword() {
		return content;
	}




	public void setPassword(String content) {
		this.content = content;
	}
}

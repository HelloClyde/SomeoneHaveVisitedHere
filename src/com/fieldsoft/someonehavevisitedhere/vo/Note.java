package com.fieldsoft.someonehavevisitedhere.vo;

public class Note {
	int Note_Id;
	int User_Id;
	String Content;
	String Image_File_Path;
	String Date;
	double Location_Latitude;
	double Location_Longitude;
	
	public Note(){
		
	}
	
	public Note(int id,int uid,String c,String im,String d,double la,double lo){
		this.Note_Id = id;
		this.User_Id = uid;
		this.Content = c;
		this.Image_File_Path = im;
		this.Date = d;
		this.Location_Latitude = la;
		this.Location_Longitude = lo;
	}
	
	public int getNote_Id() {
		return Note_Id;
	}
	public void setNote_Id(int note_Id) {
		Note_Id = note_Id;
	}
	public int getUser_Id() {
		return User_Id;
	}
	public void setUser_Id(int user_Id) {
		User_Id = user_Id;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public String getImage_File_Path() {
		return Image_File_Path;
	}
	public void setImage_File_Path(String image_File_Path) {
		Image_File_Path = image_File_Path;
	}
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		Date = date;
	}
	public double getLocation_Latitude() {
		return Location_Latitude;
	}
	public void setLocation_Latitude(double location_Latitude) {
		Location_Latitude = location_Latitude;
	}
	public double getLocation_Longitude() {
		return Location_Longitude;
	}
	public void setLocation_Longitude(double location_Longitude) {
		Location_Longitude = location_Longitude;
	}
	
	
}

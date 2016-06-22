package com.fieldsoft.someonehavevisitedhere.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AccountInfo {
	Context context;
	String UserEmail;
	String UserPassword;
	boolean IsValue;
	
	public AccountInfo(Context context){
		this.context = context;
		this.IsValue = false;
	}
	
	public AccountInfo(Context context,String userEmail,String password){
		this.context = context;
		this.UserEmail = userEmail;
		this.UserPassword = password;
		if (UserEmail == null || UserPassword == null)
			this.IsValue = false;
		else
			this.IsValue = true;
	}
	
	public boolean IsValue(){
		return this.IsValue;
	}
	
	public void Read(){
		SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.UserEmail = mSharedPreferences.getString("UserEmail", null);
		this.UserPassword = mSharedPreferences.getString("UserPassword", null);
		if (UserEmail == null || UserPassword == null)
			this.IsValue = false;
		else
			this.IsValue = true;
	}
	
	public void Save(){
		SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = mSharedPreferences.edit();
		editor.putString("UserEmail", this.UserEmail);
		editor.putString("UserPassword", this.UserPassword);
		editor.commit();
	}
	
	public void Delete(){
		SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = mSharedPreferences.edit();
		editor.remove("UserEmail");
		editor.remove("UserPassword");
		editor.commit();
	}

	/**
	 * @return context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @return userEmail
	 */
	public String getUserEmail() {
		return UserEmail;
	}

	/**
	 * @return userPassword
	 */
	public String getUserPassword() {
		return UserPassword;
	}

}

/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.example.ebuspass.ebuspassapp.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class   SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "android_api9";

	// Login table name
	private static final String TABLE_USER = "user";
	private static final String TABLE_BUSPASS = "BusPass";
	// Login Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_FNAME = "first_name";
	private static final String KEY_LNAME = "last_name";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_UID = "unique_id";
	private static final String KEY_UNAME = "username";
	private static final String KEY_CREATED_AT = "date_joined";

	private static final String BUSPASS ="monthlyPass";
	private static final String RIDES = "rides";
	private static final String RIDES_TAKEN = "rides_taken";
	private static final String SECURE_KEY = "secure_key";

	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
			+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_FNAME + " TEXT,"
			+ KEY_LNAME + " TEXT," + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
			+ KEY_UNAME + " TEXT," + KEY_CREATED_AT + " TEXT" + ")";

	String CREATE_PASS_TABLE = "CREATE TABLE " + TABLE_BUSPASS + "(" + KEY_ID + " INTEGER PRIMARY KEY," +
			BUSPASS + " TEXT, " + RIDES + " TEXT, " + RIDES_TAKEN + " TEXT, " + SECURE_KEY + " TEXT, " +
			KEY_UNAME + " TEXT)";

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(CREATE_LOGIN_TABLE);
		db.execSQL(CREATE_PASS_TABLE);

		Log.d(TAG, "Database tables created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUSPASS);
		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String first_name, String last_name, String email, String uid, String
			username, String date_joined) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FNAME, first_name); // Name
		values.put(KEY_LNAME, last_name);
		values.put(KEY_EMAIL, email); // Email
		values.put(KEY_UID, uid); // Email
		values.put(KEY_UNAME, username);
		values.put(KEY_CREATED_AT, date_joined); // Created At

		if(getProfilesCount()==0){
			long id = db.insert(TABLE_USER, null, values);
			Log.d(TAG, "New user inserted into sqlite: " + id);
		}else
		{
			long id = db.update(TABLE_USER, values,  KEY_UNAME + " = '" + username + "'", null);
			Log.d(TAG, "Used updated into sqlite: " + id);
		}
		Log.d("Rows in user table", Long.toString(getProfilesCount()));
		db.close();

	}

	public void addPass(String monthlyPass, String rides, String key, String username){
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BUSPASS, monthlyPass);
		values.put(RIDES, rides);
		values.put(RIDES_TAKEN, getRidesTaken(username));
		values.put(SECURE_KEY, key);
		values.put(KEY_UNAME, username);

		if(getPassCount(username)==0) {
			long id = db.insert(TABLE_BUSPASS, null, values);
			Log.d(TAG, "New Pass inserted into sqlite: " + id);
		}
		else
		{
			long id = db.update(TABLE_BUSPASS, values, KEY_UNAME + " = '" + username + "'", null);
			Log.d(TAG, "Update old pass: " + id);
		}
		db.close();
	}

	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM " + TABLE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put("first_name", cursor.getString(1));
			user.put("last_name", cursor.getString(2));
			user.put("email", cursor.getString(3));
			user.put("uid", cursor.getString(4));
			user.put("username", cursor.getString(5));
			user.put("date_joined", cursor.getString(6));
		}
		cursor.close();
		db.close();
		// return user
		Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

		return user;
	}

	// Getting pass details from database
	public HashMap<String, String> getPassDetails(String username){
		HashMap<String, String > pass = new HashMap<String, String>();
		String selectQuery = "SELECT * FROM " + TABLE_BUSPASS + " WHERE " + KEY_UNAME
				+ " = '" + username + "'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		cursor.moveToFirst();
		if (cursor.getCount() > 0){
			pass.put("monthlyPass", cursor.getString(1));
			pass.put("rides", cursor.getString(2));
			pass.put("ridesTaken", cursor.getString(3));
			pass.put("key", cursor.getString(4));
			pass.put("username", cursor.getString(5));
		}
		cursor.close();
		
		Log.d(TAG, "Getting pass: " + pass.toString());

		return pass;
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void deleteUsers() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from sqlite");
	}

	public long getProfilesCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		long cnt = DatabaseUtils.queryNumEntries(db, TABLE_USER);
		return cnt;
	}

	public int getPassCount(String username){
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT * FROM " + TABLE_BUSPASS + " WHERE " + KEY_UNAME + " = '" +
				username + "'";

		Cursor cursor = db.rawQuery(query, null);

		Log.d("PassCount", Integer.toString(cursor.getCount()));

		return cursor.getCount();
	}

	public String getRidesTaken(String username)
	{

		HashMap<String, String> passInfo = getPassDetails(username);
		String ridesTaken = passInfo.get("ridesTaken");
		if( ridesTaken==null)
		{
			return "0";
		}
		else
			return ridesTaken;
	}

	public void resetRidesTaken(String username) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "UPDATE " + TABLE_BUSPASS + " SET " + RIDES_TAKEN + " = '0' WHERE "
				+ KEY_UNAME + " = '" + username + "'";
		db.execSQL(query);
	}

	public void increaseRidesTaken(String username) {
		SQLiteDatabase db = this.getWritableDatabase();
		String updateQuery = "UPDATE " + TABLE_BUSPASS + " SET " + RIDES_TAKEN + " = "
				+ RIDES_TAKEN + " + 1 WHERE " + KEY_UNAME + " = '" + username + "'";
		db.execSQL(updateQuery);
	}
}

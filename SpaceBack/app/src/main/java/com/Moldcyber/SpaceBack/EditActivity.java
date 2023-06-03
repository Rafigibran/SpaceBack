package com.Moldcyber.SpaceBack;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.AlertDialog;
import android.content.*;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import cjh.waveprogressbarlibrary.*;
import com.google.android.material.button.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.text.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;
import java.io.*;
import java.util.zip.*;
import androidx.documentfile.provider.DocumentFile;
import android.provider.DocumentsContract;
import android.database.*;
import android.provider.DocumentsContract.Document;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {
	
	public final int REQ_CD_FP = 101;
	
	private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
	
	private HashMap<String, Object> map1 = new HashMap<>();
	private HashMap<String, Object> map2 = new HashMap<>();
	private String json_data = "";
	private String replace1 = "";
	private String replace2 = "";
	private HashMap<String, Object> json_map = new HashMap<>();
	private String base64 = "";
	private String segment = "";
	private String ApiKey = "";
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private LinearLayout linear4;
	private LinearLayout linear5;
	private ImageView imageview2;
	private TextView textview5;
	private ImageView imageview3;
	private ImageView imageview1;
	private MaterialButton Proses;
	private MaterialButton hilang;
	
	private Calendar cal = Calendar.getInstance();
	private ProgressDialog prog;
	private RequestNetwork Request;
	private RequestNetwork.RequestListener _Request_request_listener;
	private Intent fp = new Intent(Intent.ACTION_GET_CONTENT);
	private Intent intent = new Intent();
	private DatabaseReference Key = _firebase.getReference("Key");
	private ChildEventListener _Key_child_listener;
	private AlertDialog.Builder dialog;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.edit);
		initialize(_savedInstanceState);
		FirebaseApp.initializeApp(this);
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		linear2 = findViewById(R.id.linear2);
		linear4 = findViewById(R.id.linear4);
		linear5 = findViewById(R.id.linear5);
		imageview2 = findViewById(R.id.imageview2);
		textview5 = findViewById(R.id.textview5);
		imageview3 = findViewById(R.id.imageview3);
		imageview1 = findViewById(R.id.imageview1);
		Proses = findViewById(R.id.Proses);
		hilang = findViewById(R.id.hilang);
		Request = new RequestNetwork(this);
		fp.setType("image/*");
		fp.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		dialog = new AlertDialog.Builder(this);
		
		imageview2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				intent.setClass(getApplicationContext(), HomeActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		imageview3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				SketchwareUtil.showMessage(getApplicationContext(), "Pilih Gambar Terlebih Dahulu");
			}
		});
		
		imageview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				startActivityForResult(fp, REQ_CD_FP);
			}
		});
		
		Proses.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				SketchwareUtil.showMessage(getApplicationContext(), "Pilih Gambar Terlebih Dahulu");
			}
		});
		
		_Request_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				map1.clear();
				map2.clear();
				replace1 = _response.replace("{\"data\":{\"result_b64\":\"", "{\"result\":\"");
				replace2 = replace1.replace("}}", "}");
				json_map = new Gson().fromJson(replace2, new TypeToken<HashMap<String, Object>>(){}.getType());
				_base64Toimg(imageview1, json_map.get("result").toString());
				prog.dismiss();
				Proses.setVisibility(View.GONE);
				hilang.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				dialog.setTitle("Terjadi Kesalahan!");
				dialog.setMessage("Tidak ada respon dari AI");
				dialog.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
						
					}
				});
				dialog.create().show();
			}
		};
		
		_Key_child_listener = new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				ApiKey = _childValue.get("ApiKey").toString();
			}
			
			@Override
			public void onChildChanged(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				ApiKey = _childValue.get("ApiKey").toString();
			}
			
			@Override
			public void onChildMoved(DataSnapshot _param1, String _param2) {
				
			}
			
			@Override
			public void onChildRemoved(DataSnapshot _param1) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				
			}
			
			@Override
			public void onCancelled(DatabaseError _param1) {
				final int _errorCode = _param1.getCode();
				final String _errorMessage = _param1.getMessage();
				
			}
		};
		Key.addChildEventListener(_Key_child_listener);
	}
	
	private void initializeLogic() {
		
		int[] colorsCRNVO = { Color.parseColor("#ff424242"), Color.parseColor("#ff424242") }; android.graphics.drawable.GradientDrawable CRNVO = new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM, colorsCRNVO);
		CRNVO.setCornerRadii(new float[]{(int)0,(int)0,(int)0,(int)0,(int)0,(int)0,(int)0,(int)0});
		CRNVO.setStroke((int) 0, Color.parseColor("#000000"));
		linear2.setElevation((float) 10);
		linear2.setBackground(CRNVO);
		Proses.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)25, 0xFF424242));
		hilang.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)25, 0xFF424242));
		hilang.setVisibility(View.GONE);
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			case REQ_CD_FP:
			if (_resultCode == Activity.RESULT_OK) {
				ArrayList<String> _filePath = new ArrayList<>();
				if (_data != null) {
					if (_data.getClipData() != null) {
						for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
							ClipData.Item _item = _data.getClipData().getItemAt(_index);
							_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
						}
					}
					else {
						_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
					}
				}
				base64 = _convartFileToBase64(_filePath.get((int)(0)));
				textview5.setText(Uri.parse(_filePath.get((int)(0))).getLastPathSegment());
				segment = Uri.parse(_filePath.get((int)(0))).getLastPathSegment();
				imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath.get((int)(0)), 1024, 1024));
				Proses.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View _view) {
						_removeBg_Set_Image_path(base64);
						prog = new ProgressDialog(EditActivity.this);
						prog.setTitle("Memproses...");
						prog.setMessage("AI sedang menghilangkan latar belakang");
						prog.setCancelable(false);
						prog.show();
					}
				});
				imageview3.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View _view) {
						cal = Calendar.getInstance();
						_saveImage(imageview1, FileUtil.getPublicDir(Environment.DIRECTORY_PICTURES), new SimpleDateFormat("H:m:s").format(cal.getTime()));
					}
				});
			}
			else {
				
			}
			break;
			default:
			break;
		}
	}
	
	
	@Override
	public void onBackPressed() {
		intent.setClass(getApplicationContext(), HomeActivity.class);
		startActivity(intent);
		finish();
	}
	public String _convartFileToBase64(final String _path) {
		String filePath = _path;
		String outData = "";
		   
		    java.io.File file = new java.io.File(filePath);
		    try (java.io.FileInputStream InFile = new java.io.FileInputStream(file)) {
			    // Reading a file from file system
			    byte fileData[] = new byte[(int) file.length()];
			    InFile.read(fileData);
			    byte[] encodedBytesFile = org.apache.commons.codec.binary.Base64.encodeBase64(fileData);
			    
			    outData = new String(encodedBytesFile);
			    } catch (java.io.FileNotFoundException e) {
			 SketchwareUtil.showMessage(getApplicationContext(), "File not found" + e);
			    } catch (java.io.IOException ioe) {
			SketchwareUtil.showMessage(getApplicationContext(),"Exception while reading the File " + ioe);
			    }
		return (outData);
	}
	
	
	public void _removeBg_Set_Image_path(final String _imgPath) {
		map1 = new HashMap<>();
		map2 = new HashMap<>();
		map1.put("accept", "application/json");
		map1.put("X-Api-Key", ApiKey);
		map1.put("Content-Type", "application/json");
		json_data = "{\n  \"image_file_b64\": \"".concat(_imgPath.concat("\",\n \"image_url\":\"\",\n  \"size\": \"preview\",\n  \"type\": \"auto\",\n  \"type_level\": \"2\",\n  \"format\": \"png\",\n  \"roi\": \"0% 0% 100% 100%\",\n  \"crop\": false,\n  \"crop_margin\": \"0\",\n  \"scale\": \"original\",\n  \"position\": \"original\",\n  \"channels\": \"rgba\",\n  \"add_shadow\": false,\n  \"semitransparency\": true,\n  \"bg_color\": \"\",\n  \"bg_image_url\": \"\"\n}"));
		map2 = new Gson().fromJson(json_data, new TypeToken<HashMap<String, Object>>(){}.getType());
		Request.setHeaders(map1);
		Request.setParams(map2, RequestNetworkController.REQUEST_BODY);
		Request.startRequestNetwork(RequestNetworkController.POST, "https://api.remove.bg/v1.0/removebg", "", _Request_request_listener);
	}
	
	
	public void _base64Toimg(final ImageView _imageView, final String _base64) {
		byte[] decoded= android.util.Base64.decode(_base64, android.util.Base64.DEFAULT); 		android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeByteArray(decoded, 0, decoded.length); 		android.graphics.drawable.BitmapDrawable bd = new android.graphics.drawable.BitmapDrawable(bmp); 		_imageView.setImageDrawable(bd);
	}
	
	
	public void _saveImage(final View _view, final String _path, final String _name) {
		Bitmap returnedBitmap = Bitmap.createBitmap(_view.getWidth(), _view.getHeight(),Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap); 
		android.graphics.drawable.Drawable bgDrawable =_view.getBackground(); 
		if (bgDrawable!=null) { 
				bgDrawable.draw(canvas); 
			} else { 
					canvas.drawColor(Color.TRANSPARENT); 
			}
		_view.draw(canvas); 
		java.io.File pictureFile = new java.io.File(Environment.getExternalStorageDirectory() + _path + _name +".png"); 
		if (pictureFile == null) {
				showMessage("Error creating media file, check storage permissions: "); 
				return; 
			}
		try { 
				java.io.FileOutputStream fos = new java.io.FileOutputStream(pictureFile); 
				returnedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				showMessage(" Gambar Tersimpan!!");
				fos.close();
			} catch (java.io.FileNotFoundException e) { 
					showMessage("File not found: " + e.getMessage()); 
				} catch (java.io.IOException e) { 
						showMessage("Error accessing file: " + e.getMessage()); 
				}
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}
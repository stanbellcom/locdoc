package ee.cs.ut.locdoc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.sync.android.DbxAccountManager;

import ee.cs.ut.locdoc.utils.DBUtils;

public class LoginActivity extends Activity {
	private static final int REQUEST_LINK_TO_DBX = 0;

	private DbxAccountManager dbAccManager;
	private Button btnLogin;
	private Button btnLogout;
	private TextView txvNoInternet;
	private Button btnCheckConnection;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogout = (Button) findViewById(R.id.btn_logout);
		txvNoInternet = (TextView) findViewById(R.id.lbl_no_internet);
		btnCheckConnection = (Button) findViewById(R.id.btn_check_connection);
		
		DBUtils dbUtils = new DBUtils();
		dbAccManager = dbUtils.getAccManager(getApplicationContext());
		
		checkConnection();
		
		if (!dbAccManager.hasLinkedAccount())
			btnLogout.setEnabled(false);

		// click listeners
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dbAccManager.hasLinkedAccount()) {
					Intent i = new Intent(getBaseContext(), MainScreenActivity.class);
					startActivity(i);
				} else {
					dbAccManager.startLink(LoginActivity.this, REQUEST_LINK_TO_DBX);
				}
			}
		});

		btnLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dbAccManager.hasLinkedAccount()) {
					dbAccManager.unlink();
					btnLogout.setEnabled(false);
				}
			}
		});
		
		btnCheckConnection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checkConnection();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_LINK_TO_DBX) {
			Intent i = new Intent(getBaseContext(), MainScreenActivity.class);
			startActivity(i);
		}
	}
	
	public void checkConnection(){
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
			btnLogin.setVisibility(View.INVISIBLE);
			btnLogout.setVisibility(View.INVISIBLE);
			txvNoInternet.setVisibility(View.VISIBLE);
			btnCheckConnection.setVisibility(View.VISIBLE);
		} else {
			btnLogin.setVisibility(View.VISIBLE);
			btnLogout.setVisibility(View.VISIBLE);
			txvNoInternet.setVisibility(View.INVISIBLE);
			btnCheckConnection.setVisibility(View.INVISIBLE);
		}
	}
}

package ee.cs.ut.locdoc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import br.com.thinkti.android.filechooser.FileChooser;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxPath;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ee.cs.ut.locdoc.utils.DBUtils;
import ee.cs.ut.locdoc.utils.GoogleUtils;

public class GMapActivity extends Activity {

	private GoogleMap googleMap;
	private MarkerOptions uploadMarkerOptions;
	private Marker uploadMarker;

	private DBUtils dbUtils;

	private final int FILEADD_REQUESTCODE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);

		dbUtils = new DBUtils();

		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null)
				Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
		}

		if (googleMap != null) {
			googleMap.setMyLocationEnabled(true);
			googleMap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng latLng) {
					if (uploadMarker != null)
						uploadMarker.remove();

					uploadMarkerOptions = new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title(
							"Upload my file here");
					uploadMarker = googleMap.addMarker(uploadMarkerOptions);

					AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(GMapActivity.this);
					confirmationDialog.setTitle("Uploading file");
					confirmationDialog.setMessage("Do you want to upload file here?");
					confirmationDialog.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							startFileChooser();
						}
					});
					confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//do nothing
						}
					});
					confirmationDialog.show();
				}
			});
			
			googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				
				@Override
				public void onInfoWindowClick(Marker marker) {
					startFileChooser();
				}
			});
		}
	}
	
	public void startFileChooser(){
		final Intent intent = new Intent(GMapActivity.this, FileChooser.class);
		ArrayList<String> extensions = new ArrayList<String>();
		extensions.add(".pdf");
		extensions.add(".xls");
		extensions.add(".xlsx");
		intent.putStringArrayListExtra("filterFileExtension", extensions);
		startActivityForResult(intent, FILEADD_REQUESTCODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == FILEADD_REQUESTCODE) && (resultCode == -1)) {
			String filePath = data.getStringExtra("fileSelected");

			File file = new File(filePath);
			String fileName = "[" + uploadMarker.getPosition().latitude + "," + uploadMarker.getPosition().longitude + "] "
					+ file.getName();

			String currentLocationName;
			AsyncTask<Double, Void, String> task = new GoogleUtils().execute(uploadMarker.getPosition().latitude,
					uploadMarker.getPosition().longitude);
			currentLocationName = "";
			try {
				currentLocationName = task.get().toString();
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			} catch (ExecutionException e2) {
				e2.printStackTrace();
			}

			if (currentLocationName != null) {
				DbxPath locationPath = dbUtils.createPathIfMissing(currentLocationName);
				DbxPath newFilePath = new DbxPath(locationPath, fileName);

				try {
					DbxFile newFile = dbUtils.getFileSystem().create(newFilePath);
					newFile.writeFromExistingFile(file, false);
					newFile.close();
					Toast.makeText(GMapActivity.this, "File has been sucessfully added to \"" + currentLocationName + "\" directory",
							Toast.LENGTH_LONG).show();
					GMapActivity.this.finish();
				} catch (DbxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

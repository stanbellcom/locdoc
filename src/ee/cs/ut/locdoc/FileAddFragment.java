package ee.cs.ut.locdoc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import br.com.thinkti.android.filechooser.FileChooser;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxPath;

import ee.cs.ut.locdoc.utils.DBUtils;
import ee.cs.ut.locdoc.utils.GPSTracker;
import ee.cs.ut.locdoc.utils.GoogleUtils;

public class FileAddFragment extends Fragment {
	private Button btnAddToCurrent;
	private Button btnAddPickLoc;
	private Button btnRefresh;
	private TextView lblCoordinates;
	private TextView lblLocationName;
	private String currentLocationName;
	private final int FILEADD_REQUESTCODE = 0;
	private GPSTracker gps;
	private DBUtils dbUtils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fileadd_fragment, container, false);

		// initialization
		dbUtils = new DBUtils();
		gps = new GPSTracker(getActivity());
		btnAddToCurrent = (Button) rootView.findViewById(R.id.btn_add_file_current_loc);
		btnAddPickLoc = (Button) rootView.findViewById(R.id.btn_add_file_pick_loc);
		btnRefresh = (Button) rootView.findViewById(R.id.btn_current_locaiton_refresh);
		lblCoordinates = (TextView) rootView.findViewById(R.id.lbl_current_location_coordinates);
		lblLocationName = (TextView) rootView.findViewById(R.id.lbl_current_location_name);

		// button listeners
		btnRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setCurrentLocation();
			}
		});

		final Intent intent = new Intent(getActivity(), FileChooser.class);
		ArrayList<String> extensions = new ArrayList<String>();
		extensions.add(".pdf");
		extensions.add(".xls");
		extensions.add(".xlsx");
		intent.putStringArrayListExtra("filterFileExtension", extensions);
		btnAddToCurrent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setCurrentLocation();
				startActivityForResult(intent, FILEADD_REQUESTCODE);
			}
		});
		
		btnAddPickLoc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), GMapActivity.class);
				startActivity(intent);
			}
		});
		
		

		return rootView;
	}

	public void setCurrentLocation() {
		if (gps.canGetLocation()) {
			gps.getLocation();
			lblCoordinates.setText("[" + gps.getLatitude() + "," + gps.getLongitude() + "]");
			AsyncTask<Double, Void, String> task = new GoogleUtils().execute(gps.getLatitude(), gps.getLongitude());
			currentLocationName = "";
			try {
				currentLocationName = task.get().toString();
				lblLocationName.setText(currentLocationName);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			} catch (ExecutionException e2) {
				e2.printStackTrace();
			}
		} else {
			btnAddToCurrent.setEnabled(false);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == FILEADD_REQUESTCODE) && (resultCode == -1)) {
			String filePath = data.getStringExtra("fileSelected");

			File file = new File(filePath);
			String fileName = "[" + gps.getLatitude() + "," + gps.getLongitude() + "] " + file.getName();

			if (currentLocationName != null) {
				DbxPath locationPath = dbUtils.createPathIfMissing(currentLocationName);
				DbxPath newFilePath = new DbxPath(locationPath, fileName);

				try {
					DbxFile newFile = dbUtils.getFileSystem().create(newFilePath);
					newFile.writeFromExistingFile(file, false);
					newFile.close();
					Toast.makeText(getActivity(), "File has been sucessfully added to \"" + currentLocationName + "\" directory", Toast.LENGTH_LONG).show();
				} catch (DbxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

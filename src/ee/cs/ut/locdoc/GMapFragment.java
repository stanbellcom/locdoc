package ee.cs.ut.locdoc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.dropbox.sync.android.DbxPath;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ee.cs.ut.locdoc.utils.DBUtils;

public class GMapFragment extends Fragment {

	private DBUtils dbUtils = new DBUtils();
	private List<DbxPath> getAllFiles;
	private HashMap<String, DbxPath> fileNameMap = new HashMap<String, DbxPath>();

	public GoogleMap googleMap;
	public Set<Marker> allMarkers = new HashSet<Marker>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.map_fragment, container, false);

		if (googleMap == null) {
			Object o = (Fragment) getFragmentManager().findFragmentById(R.id.map);
			SupportMapFragment k = (SupportMapFragment) o;

			googleMap = k.getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getActivity(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
			}
		}

		if (googleMap != null) {
			googleMap.setMyLocationEnabled(true);
			populateMap();

			googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

				@Override
				public void onInfoWindowClick(Marker myMarker) {
					File file = dbUtils.saveFileLocally(fileNameMap.get(myMarker.getTitle()));
					MimeTypeMap map = MimeTypeMap.getSingleton();
					String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
					String type = map.getMimeTypeFromExtension(ext);

					if (type == null)
						type = "*/*";

					Intent intent = new Intent(Intent.ACTION_VIEW);
					Uri data = Uri.fromFile(file);
					intent.setDataAndType(data, type);
					startActivity(intent);

				}
			});
		}

		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		try {
			SupportMapFragment fragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map));
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			ft.remove(fragment);
			ft.commit();
			googleMap = null;
		} catch (Exception e) {
		}
	}
	
	public void populateMap(){
		deletePreviousMarkers();
		
		getAllFiles = new ArrayList<DbxPath>();
		dbUtils.getAllFiles(DbxPath.ROOT, getAllFiles);

		for (int i = 0; i < getAllFiles.size(); i++) {
			DbxPath file = getAllFiles.get(i);
			String filename = file.getName();

			int fstindex = filename.indexOf("[") + 1;
			int scdindex = filename.indexOf("]");

			if (fstindex > 0 && scdindex > -1) {
				String[] cordinates = filename.substring(fstindex, scdindex).split(",");
				filename = filename.substring(scdindex + 2, filename.length());

				double latitude = Double.parseDouble(cordinates[0]);
				double longitude = Double.parseDouble(cordinates[1]);

				MarkerOptions myMarker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(filename);
				fileNameMap.put(filename, file);
				allMarkers.add(googleMap.addMarker(myMarker));
			}
		}
	}
	
	public void deletePreviousMarkers(){
		for (Marker m : allMarkers){
			m.remove();
		}
	}
}
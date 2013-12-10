package ee.cs.ut.locdoc;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxPath;

import ee.cs.ut.locdoc.adapters.FileBrowserListViewAdapter;
import ee.cs.ut.locdoc.utils.DBUtils;

public class FileBrowserFragment extends Fragment {

	private DBUtils dbUtils;
	private List<DbxPath> fileList;

	private DbxPath currentPath;

	private ListView lstvFiles;
	private TextView txvHeader;
	private Button btnBack;
	private ImageButton btnRefresh;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.filebrowser_fragment, container, false);

		dbUtils = new DBUtils();
		lstvFiles = (ListView) rootView.findViewById(R.id.lstv_files);

		View header = (View) inflater.inflate(R.layout.header_row, null);
		lstvFiles.addHeaderView(header);
		txvHeader = (TextView) rootView.findViewById(R.id.txv_header);
		btnBack = (Button) rootView.findViewById(R.id.btn_filebrowser_back);
		btnRefresh = (ImageButton) rootView.findViewById(R.id.btn_refresh_filelist);

		currentPath = DbxPath.ROOT;
		updateFileList(currentPath);

		lstvFiles.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				if (position != 0) {
					currentPath = fileList.get(position - 1);
					try {
						if (dbUtils.getFileSystem().isFolder(currentPath)) {
							updateFileList(currentPath);
						} else {
							File file = dbUtils.saveFileLocally(currentPath);
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
					} catch (DbxException e) {
						e.printStackTrace();
					}
				}
			}
		});

		btnRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateFileList(currentPath);
			}
		});

		return rootView;
	}

	public void updateFileList(final DbxPath path) {
		if (path == DbxPath.ROOT) {
			txvHeader.setText(R.string.lbl_basedir);
			btnBack.setVisibility(View.GONE);
		} else {
			txvHeader.setText(path.getName());

			btnBack.setVisibility(View.VISIBLE);
			btnBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPath = path.getParent();
					updateFileList(path.getParent());
				}
			});

		}

		fileList = dbUtils.getFileList(path);
		FileBrowserListViewAdapter adapter = new FileBrowserListViewAdapter(getActivity(), R.layout.fileentry_row, fileList);

		lstvFiles.setAdapter(adapter);
	}
}

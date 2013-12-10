package ee.cs.ut.locdoc.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxPath;

import ee.cs.ut.locdoc.R;
import ee.cs.ut.locdoc.utils.DBUtils;

public class FileBrowserListViewAdapter extends ArrayAdapter<DbxPath> {
	private Context context;
	private int layoutResourceId;
	private List<DbxPath> list = null;
	private DBUtils dbUtils;

	public FileBrowserListViewAdapter(Context context, int layoutResourceId, List<DbxPath> list) {
		super(context, layoutResourceId, list);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.list = list;
		dbUtils = new DBUtils();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		FileHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new FileHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.imv_fileicon);
			holder.txtFilename = (TextView) row.findViewById(R.id.txv_filename);

			row.setTag(holder);
		} else {
			holder = (FileHolder) row.getTag();
		}

		DbxPath path = list.get(position);
		try {
			String name = path.getName();

			if (dbUtils.getFileSystem().isFolder(path)) {
				holder.imgIcon.setBackgroundResource(R.drawable.icon_folder);
			} else {
				holder.imgIcon.setBackgroundResource(R.drawable.icon_file);
				int scdindex = name.indexOf("]");
				if (scdindex > -1)
					name = name.substring(scdindex + 2, name.length());
			}

			holder.txtFilename.setText(name);
		} catch (DbxException e) {
			e.printStackTrace();
		}

		return row;
	}

	static class FileHolder {
		ImageView imgIcon;
		TextView txtFilename;
	}

}

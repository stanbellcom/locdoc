package ee.cs.ut.locdoc.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

public class DBUtils {
	private static final String APP_KEY = "7k22u3soa771zc1";
	private static final String APP_SECRET = "1b7m5srwzscjvvq";

	private static DbxAccountManager dbAccManager;
	private DbxFileSystem dbFileSystem;

	public DbxAccountManager getAccManager(Context context) {
		if (dbAccManager == null)
			dbAccManager = DbxAccountManager.getInstance(context, APP_KEY,
					APP_SECRET);

		return dbAccManager;
	}

	public List<DbxPath> getFileList(DbxPath path) {
		List<DbxPath> items = new ArrayList<DbxPath>();

		try {
			dbFileSystem = getFileSystem();
			dbFileSystem.awaitFirstSync();
			List<DbxFileInfo> files = dbFileSystem.listFolder(path);

			for (DbxFileInfo file : files) {
				items.add(file.path);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return items;
	}

	public DbxFileSystem getFileSystem() {
		try {
			return DbxFileSystem.forAccount(dbAccManager.getLinkedAccount());
		} catch (Unauthorized e) {
			e.printStackTrace();
		}

		return null;
	}

	public File saveFileLocally(DbxPath path) {
		InputStream in = null;
		OutputStream out = null;
		DbxFile readFile = null;
		try {
			readFile = getFileSystem().open(path);
			in = readFile.getReadStream();
			out = new FileOutputStream(
					Environment.getExternalStorageDirectory() + "/"
							+ path.getName());
			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();

			out.flush();
			out.close();
			readFile.close();

			return new File(Environment.getExternalStorageDirectory() + "/"
					+ path.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public DbxPath createPathIfMissing(String pathStr) {
		DbxPath pathToCheck = new DbxPath(pathStr);
		try {
			if (!getFileSystem().exists(pathToCheck)) {
				getFileSystem().createFolder(pathToCheck);
			}
			return pathToCheck;
		} catch (DbxException e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<DbxPath> getAllFiles(DbxPath path, List<DbxPath> files) {
		dbFileSystem = getFileSystem();
		try {
			dbFileSystem.awaitFirstSync();
		} catch (DbxException e) {
			e.printStackTrace();
		}
		return getAllFilesHelper(path, files);
	}
	
	private List<DbxPath> getAllFilesHelper(DbxPath path, List<DbxPath> files){
		List<DbxPath> tmpList = getFileList(path);

		for (DbxPath file_or_dir : tmpList) {
			try {
				if (getFileSystem().isFolder(file_or_dir)) {
					getAllFilesHelper(file_or_dir, files);
				} else {
					files.add(file_or_dir);
				}
			} catch (DbxException e) {
				e.printStackTrace();
			}
		}

		return files;
	}
}
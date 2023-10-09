package de.marx_software.cms.headless.modules.manager;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author marx
 */
public class ModulePacker {

	/**
	 * Delets a dir recursively deleting anything inside it.
	 *
	 * @param dir The dir to delete
	 * @return true if the dir was successfully deleted
	 */
	protected static boolean deleteDirectory(File dir) {
		if (!dir.exists() || !dir.isDirectory()) {
			return false;
		}

		String[] files = dir.list();
		for (int i = 0, len = files.length; i < len; i++) {
			File f = new File(dir, files[i]);
			if (f.isDirectory()) {
				deleteDirectory(f);
			} else {
				f.delete();
			}
		}
		return dir.delete();
	}
	
	protected static boolean moveDirectoy (final File src, final File dest) {
		return src.renameTo(dest);
	}

	/**
	 * Unpack a zip file
	 *
	 * @param theFile
	 * @param targetDir
	 * @return the file
	 * @throws IOException
	 */
	protected static File unpackArchive(File theFile, File targetDir) throws IOException {
		if (!theFile.exists()) {
			throw new IOException(theFile.getAbsolutePath() + " does not exist");
		}
		if (!buildDirectory(targetDir)) {
			throw new IOException("Could not create directory: " + targetDir);
		}
		boolean found = false;
		String moduleid = null;
		try (ZipFile zipFile = new ZipFile(theFile)) {
			for (Enumeration entries = zipFile.entries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				File file = new File(targetDir, File.separator + entry.getName());
				if (entry.isDirectory() && !found) {
					moduleid = file.getName();
					found = true;
				}
				if (!buildDirectory(file.getParentFile())) {
					throw new IOException("Could not create directory: " + file.getParentFile());
				}
				if (!entry.isDirectory()) {
					copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file)));
				} else if (!buildDirectory(file)) {
					throw new IOException("Could not create directory: " + file);
				}
			}
		}
		return new File(targetDir, moduleid);
	}

	private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len = in.read(buffer);
		while (len >= 0) {
			out.write(buffer, 0, len);
			len = in.read(buffer);
		}
		in.close();
		out.close();
	}

	private static boolean buildDirectory(File file) {
		return file.exists() || file.mkdirs();
	}
}

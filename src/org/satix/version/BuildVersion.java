package org.satix.version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class BuildVersion extends Task {
	public void execute() throws BuildException {
		String buildDate = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
		PrintStream ps = null;
		try {
			ps = new PrintStream(new FileOutputStream(new File("VERSION.txt")));
			ps.println("satix.version=1.0 pre build");
			ps.println("satix.revision=" + buildDate);
		} catch (Exception ex) {
			throw new BuildException(ex);
		} finally {
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (Exception ex) {
					throw new BuildException(ex);
				}
			}
		}
	}
}

package readers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import classes.SysdigRecordObject;
import exceptions.HighFieldNumberException;
import exceptions.LowFieldNumberException;
import helpers.Configurations;

public class CSVReader extends SysdigObjectDAL {

	public CSVReader() throws NoSuchFieldException, SecurityException {

		Class<?> c = new SysdigRecordObject().getClass();
		ArrayList<Field> temp = new ArrayList<Field>();
		for (String pick : Configurations.getShortFieldList())
			temp.add(c.getField(pick));

		ClassFields = temp.toArray(new Field[temp.size()]);

		// finilize class creation 
		init();
	}

	public SysdigRecordObject GetObjectFromTextLine(String inp)
			throws LowFieldNumberException, HighFieldNumberException, IllegalArgumentException, IllegalAccessException {

		SysdigRecordObject ret = new SysdigRecordObject();

		String fields[] = { "evt_num", "evt_datetime", "evt_type", "evt_res", "evt_args", "thread_tid", "thread_unitid",
				"proc_pid", "proc_ppid", "proc_name", "proc_exepath", "user_uid", "user_euid", "user_gid", "fd_num",
				"fd_type", "fd_filename", "fd_name", "fd_inode", "fd_ip", "fd_port", "fd_1_num", "fd_1_type",
				"fd_1_filename", "fd_1_name", "fd_1_inode", "fd_1_ip", "fd_1_port", "proc_cwd", "proc_args",
				"proc_name", "proc_inode", "dep_tid", "dep_unitid" };

		String indexes[] = { "evt_datetime", "evt_type", "thread_tid", "proc_name", "proc_args", "proc_cwd",
				"proc_cmdline", "proc_pname", "proc_pid", "proc_ppid", "fd_cip", "fd_cport", "fd_directory",
				"fd_filename", "fd_ip", "fd_name", "fd_num", "fd_type", "fd_typechar", "user_name", "user_uid",
				"evt_num", "evt_args", "user_shell" };

		List<String> indexes_list = Arrays.asList(indexes);

		String tokens[] = inp.split(Configurations.getInstance().getSetting(Configurations.LINE_SEPERATOR));

		if (tokens.length < ClassFields.length) {
			throw new LowFieldNumberException("Error! number of fields do not match!" + tokens.length + " instead of "
					+ ClassFields.length + " : " + inp);

		} else if (tokens.length > ClassFields.length) {
			throw new HighFieldNumberException("Error! number of fields do not match!" + tokens.length + " instead of "
					+ ClassFields.length + " : " + inp);
		}
		for (int index = 0; index < tokens.length; index++) {
			int i = indexes_list.indexOf(fields[index]);

			ClassFields[i].set(ret, tokens[index].trim());
		}

		ret.fd_typechar = getFDTypeChar(ret.fd_type);

		return ret;
	}

	private String getFDTypeChar(String str) {
		switch (str) {
		case "ipv4":
			return "4";
		case "ipv6":
			return "6";
		case "file":
			return "f";
		case "signal":
			return "s";
		case "unix":
			return "u";
		case "event":
			return "e";
		case "inotify":
			return "i";
		case "timer":
			return "t";

		}
		return "<NA>";
	}
}

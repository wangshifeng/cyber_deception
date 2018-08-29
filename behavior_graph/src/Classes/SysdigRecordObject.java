package Classes;

public class SysdigRecordObject {
	public String fd_num;
	public String fd_type;
	public String fd_typechar;
	public String fd_name;
	public String fd_directory;
	public String fd_filename;
	public String fd_ip;
	public String fd_cip;
	public String fd_sip;
	public String fd_port;
	public String fd_cport;
	public String fd_sport;
	public String fd_l4proto;
	public String fd_sockfamily;
	public String fd_is_server;
	public String proc_pid;
	public String proc_exe;
	public String proc_name;
	public String proc_args;
	public String proc_cmdline;
	public String proc_cwd;
	public String proc_nchilds;
	public String proc_ppid;
	public String proc_pname;
	public String proc_apid;
	public String proc_aname;
	public String proc_loginshellid;
	public String proc_duration;
	public String proc_fdopencount;
	public String proc_fdlimit;
	public String proc_fdusage;
	public String proc_vmsize;
	public String proc_vmrss;
	public String proc_vmswap;
	public String thread_pfmajor;
	public String thread_pfminor;
	public String thread_tid;
	public String thread_ismain;
	public String thread_exectime;
	public String thread_totexectime;
	public String evt_num;
	public String evt_time;
	public String evt_time_s;
	public String evt_datetime;
	public String evt_rawtime;
	public String evt_rawtime_s;
	public String evt_rawtime_ns;
	public String evt_reltime;
	public String evt_reltime_s;
	public String evt_reltime_ns;
	public String evt_latency;
	public String evt_latency_s;
	public String evt_latency_ns;
	public String evt_deltatime;
	public String evt_deltatime_s;
	public String evt_deltatime_ns;
	public String evt_dir;
	public String evt_type;
	public String evt_cpu;
	public String evt_args;
	public String evt_info;
	public String evt_buffer;
	public String evt_res;
	public String evt_rawres;
	public String evt_failed;
	public String evt_is_io;
	public String evt_is_io_read;
	public String evt_is_io_write;
	public String evt_io_dir;
	public String evt_is_wait;
	public String evt_is_syslog;
	public String evt_count;
	public String user_uid;
	public String user_name;
	public String user_homedir;
	public String user_shell;
	public String group_gid;
	public String group_name;
	public String syslog_facility_str;
	public String syslog_facility;
	public String syslog_severity_str;
	public String syslog_severity;
	public String syslog_message;
	// public String arg1;
	// public String arg2;
	// public String returnValue;

	public String getProcPID() {
		return proc_pid + "|" + proc_name;
	}

	public String getParentProcID() {
		return proc_ppid + "|" + proc_pname;
	}

	public String getFD_ID() {
		return fd_num + "|" + fd_name;
	}

}

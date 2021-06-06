package il.ac.telhai.os.software;

public class Signal {
	public static final int SIGHUP	=	 1;
	public static final int SIGINT	=	 2;
	public static final int SIGQUIT	=	 3;
	public static final int SIGILL	=	 4;
	public static final int SIGTRAP	=	 5;
	public static final int SIGABRT	=	 6;
	public static final int SIGIOT	=	 6;
	public static final int SIGBUS	=	 7;
	public static final int SIGFPE	=	 8;
	public static final int SIGKILL	=	 9;
	public static final int SIGUSR1	=	10;
	public static final int SIGSEGV	=	11;
	public static final int SIGUSR2	=	12;
	public static final int SIGPIPE	=	13;
	public static final int SIGALRM	=	14;
	public static final int SIGTERM	=	15;
	public static final int SIGSTKFLT=	16;
	public static final int SIGCHLD	=	17;
	public static final int SIGCONT	=	18;
	public static final int SIGSTOP	=	19;
	public static final int SIGTSTP	=	20;
	public static final int SIGTTIN	=	21;
	public static final int SIGTTOU	=	22;
	public static final int SIGURG	=	23;
	public static final int SIGXCPU	=	24;
	public static final int SIGXFSZ	=	25;
	public static final int SIGVTALRM=	26;
	public static final int SIGPROF	=	27;
	public static final int SIGWINCH=	28;
	public static final int SIGIO	=	29;
	public static final int SIGPOLL	=	SIGIO;
	public static final int SIGPWR	=	30;
	public static final int SIGSYS	=	31;
	public static final int _NSIG	=	32;

	public enum Behaviour {
		IGNORE, TERMINATE, HANDLE;
	}

	private static Behaviour[] defaultBehaviour = new Behaviour[_NSIG];
	static {
		defaultBehaviour[SIGHUP]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGINT]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGQUIT]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGILL]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGTRAP]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGABRT]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGIOT]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGBUS]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGFPE]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGKILL]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGUSR1]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGSEGV]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGUSR2]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGPIPE]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGALRM]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGTERM]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGSTKFLT] =	Behaviour.IGNORE;
		defaultBehaviour[SIGCHLD]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGCONT]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGSTOP]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGTSTP]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGTTIN]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGTTOU]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGURG]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGXCPU]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGXFSZ]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGVTALRM] =	Behaviour.IGNORE;
		defaultBehaviour[SIGPROF]	=	Behaviour.IGNORE;
		defaultBehaviour[SIGWINCH]  =	Behaviour.IGNORE;
		defaultBehaviour[SIGIO]	    =	Behaviour.IGNORE;
		defaultBehaviour[SIGPWR]	=	Behaviour.TERMINATE;
		defaultBehaviour[SIGSYS]	=	Behaviour.TERMINATE;
	}
	
	private int signo;

	public Signal(int signo) {
		this.signo = signo;
	}

	public int getSigno() {
		return signo;
	}
	
	public Behaviour getDefaultBehaviour() {
		return defaultBehaviour[signo];
	}

	@Override
	public String toString() {
		return "Signal [" + signo + "]";
	}
}

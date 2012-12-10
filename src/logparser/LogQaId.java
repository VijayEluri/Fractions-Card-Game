package logparser;

public class LogQaId {
	private static int count;
	private int myCount;
	private String qid;
	
	static {
		count = 0;
	}
	
	public LogQaId(int qid, int start) {
		count = start;
		setUp();
	}
	
	public LogQaId(String qid) {
		this.qid = qid;
		setUp();
	}
	
	private void setUp() {
		count++;
		myCount = count;
	}
	
	public int getQaId() {
		return myCount;
	}
	
	public String getQId() {
		return qid;
	}
}
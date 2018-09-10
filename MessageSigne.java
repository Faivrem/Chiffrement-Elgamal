import java.math.BigInteger;

public class MessageSigne {
	String m;
	BigInteger s;
	BigInteger r;
	
	
	public MessageSigne(String m, BigInteger s, BigInteger r) {
		this.m = m;
		this.s = s;
		this.r = r;
	}


	@Override
	public String toString() {
		return "MessageSigne (m=" + m + ", s=" + s + ", r=" + r + ")";
	}
	
}

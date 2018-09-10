import java.math.BigInteger;

public class MessageCrypte {
	String c;
	BigInteger gbmodp;
	
	
	public MessageCrypte(String c, BigInteger gbmodp) {
		this.c = c;
		this.gbmodp = gbmodp;
	}


	@Override
	public String toString() {
		return "MessageCrypte (c=" + c + ", gbmodp=" + gbmodp + ")";
	}
}

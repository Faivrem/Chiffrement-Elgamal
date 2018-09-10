import java.math.BigInteger;

public class Cle {
	BigInteger a; //Cle prive
	BigInteger g; //Generateur
	BigInteger gAmodP; //Cle publique
	BigInteger p;
	
	public Cle(BigInteger g, BigInteger gAmodP, BigInteger p) {
		this.g = g;
		this.gAmodP = gAmodP;
		this.p = p;
	}
	
	public Cle(BigInteger a, BigInteger g, BigInteger gAmodP, BigInteger p) {
		this.a = a;
		this.g = g;
		this.gAmodP = gAmodP;
		this.p = p;
	}

	@Override
	public String toString() {
		return "Cle (a=" + a + ", g=" + g + ", gAmodP=" + gAmodP + ", p=" + p + ")";
	}
	
}

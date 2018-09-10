import java.math.BigInteger;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;


public class Elgamal {
	Cle K;
	MessageCrypte messageCrypte;
	MessageSigne messageSigne;
	/* 
	 * 
	 * 
	 * 
	 */
	public Cle GenererClesElgamal(int b) {
		Cle c;
		BigInteger q =BigInteger.ZERO;
		BigInteger a=new BigInteger(String.valueOf(b));
		BigInteger p=BigInteger.ZERO;
		int flag=0;
		int point=0;
		if (b<=65500){
			while (flag==0){
				while (point==0){
					q = new BigInteger(16,20,new Random());
					if ((q).add(q).compareTo(a)==1){
						point=1;
					}
				}
				p=q.multiply(new BigInteger("2"));
				p=p.add(BigInteger.ONE);
				if (p.isProbablePrime(20)){
					flag=1;
				}
				else {
					point=0;
				}

			}

			BigInteger g=new BigInteger("2");
			boolean trouverGenerateur=false;

			while (trouverGenerateur==false && g.compareTo(p)==-1){
				if (g.modPow(q,p).compareTo(BigInteger.ONE)==0){
					trouverGenerateur=true;
				}
				else {
					g=g.add(BigInteger.ONE);
				}
			}

			BigInteger gamodp=g.modPow(a,p);
			c=new Cle(a,g,gamodp,p);
			System.out.println(c);
			this.K=c;
		}

		else {
			System.out.println("Veuillez choisir un a plus petit");
			c=null;
		}
		return c;
	}
	public MessageCrypte EncryperElgamal(String m,Cle k) {
		String resultat="";
		String bloc="";
		m=m.toUpperCase();
		Random rn = new Random();
		BigInteger b = new BigInteger(16,new Random());
		int decoupage=	k.p.toString().length()-1; // taille d'un block
		int i=k.p.toString().length();
		String messageAsc="";
		for (int j=0;j<m.length();j++){
			messageAsc=messageAsc.concat(Integer.toString((int)m.charAt(j)));
		}
		int resteModulo=messageAsc.length()%decoupage;
		int iteration=messageAsc.length()/decoupage;

		for (int d=0;d<iteration*decoupage;d=d+decoupage){
			String test=messageAsc.substring(d,d+decoupage); // Crypter en boucle
			BigInteger momo=new BigInteger(test);
			BigInteger rez=(k.gAmodP.modPow(b,k.p)).multiply(momo).mod(k.p);;

			bloc=rez.toString();
			while (bloc.length()<i){
				bloc="0"+bloc;
			}
			resultat=resultat.concat(bloc);
		}
		if(resteModulo!=0){
			String test=messageAsc.substring(messageAsc.length()-resteModulo,messageAsc.length());
			BigInteger momo=new BigInteger(test);
			BigInteger rez=(k.gAmodP.modPow(b,k.p)).multiply(momo).mod(k.p);
			bloc=rez.toString();
			while (bloc.length()<i){
				bloc="0"+bloc;
			}
			resultat=resultat.concat(bloc);
		}
		System.out.println("Message crypté="+resultat);
		BigInteger gbmodp=k.g.modPow(b,k.p);
		MessageCrypte lui=new MessageCrypte(resultat,gbmodp);
		return lui;

	}
	public String DecrypterElgamal(MessageCrypte c,Cle k) {
		String message="";
		String messageAsc="";
		try {
			int i=k.p.toString().length();
			int tailleCrypte=c.c.length();
			int iteration=tailleCrypte/i;
			BigInteger B=c.gbmodp;
			BigInteger res=BigInteger.ZERO;
			for (int j=0;j<i*iteration;j=j+i){

				BigInteger C=new BigInteger(c.c.substring(j,j+i));

				BigInteger inverseA=k.p.subtract(BigInteger.ONE).subtract(k.a);

				res=B.modPow(inverseA,k.p);
				res=res.multiply(C).mod(k.p);
				message=message.concat(res.toString());
			}
			try{
				for (int z=0;z<message.length();z=z+2){
					String number=message.substring(z,z+2);
					char carac=(char)Integer.parseInt(number);
					messageAsc=messageAsc.concat(Character.toString(carac));
				}
				System.out.println("message="+messageAsc);
			}
			catch (StringIndexOutOfBoundsException e){
				System.out.println("Impossible1");
			}
		}
		catch(ArithmeticException e){
			System.out.println("Impossiblemm");
		}
		catch(NumberFormatException e){
			System.out.println("Un message crypté doit être une chaine de chiffre");
		}
		catch(NullPointerException e){
			System.out.println("Impossiblexx");
		}
		return messageAsc;
	}
	public MessageSigne SignerElgamal(String m,Cle c) {
		m=m.toUpperCase();
		String messageAsc="";
		for (int j=0;j<m.length();j++){
			messageAsc=messageAsc.concat(Integer.toString((int)m.charAt(j)));
		}
		//System.out.println("asci "+messageAsc);
		BigInteger r;
		BigInteger H=new BigInteger(messageAsc);
		H=H.multiply(H);
		BigInteger k=BigInteger.ZERO;
		BigInteger s;
		boolean cond=false;
		while (cond==false){
			k = new BigInteger(16 ,new Random());
			if ((k.compareTo(c.p.subtract(BigInteger.ONE))==-1) && (k.compareTo(BigInteger.ONE)==1) 
					&& (k.gcd(c.p.subtract(BigInteger.ONE)).compareTo(BigInteger.ONE)==0)){
				//System.out.println(k);
				cond=true;
			}
		}
		r=c.g.modPow(k,c.p);
		s=H.subtract(c.a.multiply(r));
		s=s.multiply(k.modInverse(c.p.subtract(BigInteger.ONE))).mod(c.p.subtract(BigInteger.ONE));
		MessageSigne x=new MessageSigne(m,s,r);
		System.out.println(x);
		return x;
	}

	public boolean VerifierSignatureElgamal(String m,BigInteger s,BigInteger r,Cle k) {
		m=m.toUpperCase();
		String messageAsc="";
		for (int j=0;j<m.length();j++){
			messageAsc=messageAsc.concat(Integer.toString((int)m.charAt(j)));
		}
		BigInteger H=new BigInteger(messageAsc);
		H=H.multiply(H);
		BigInteger gauche=k.gAmodP.modPow(r,k.p).multiply(r.modPow(s,k.p)).mod(k.p);
		BigInteger droite=k.g.modPow(H,k.p);

		if (droite.compareTo(gauche)==0){
			System.out.println("Bonne signature");
			return true;
		}
		else{
			System.out.println("Mauvaise signature");
			return false;
		}
	}

	public Cle rentrerCle(){

		Cle r=null;
		try {
			BigInteger g=null;
			BigInteger a=null;
			BigInteger p=null;

			Scanner sc = new Scanner(System.in);
			System.out.println("Rentrer des paramètres valides de clé:");
			System.out.print("g=");
			g=sc.nextBigInteger();
			sc.nextLine();
			System.out.print("a=");
			a=sc.nextBigInteger();
			sc.nextLine();
			System.out.print("p=");
			p=sc.nextBigInteger();
			sc.nextLine();
			r=new Cle(a,g,null,p);
		}
		catch(InputMismatchException e){
			System.out.println("Vos paramètres ne sont pas valides");
		}
		return r;
	}

	public Cle rentrerCleSignature(){

		Cle r=null;
		try {
			BigInteger g=null;
			BigInteger gamodp=null;
			BigInteger p=null;

			Scanner sc = new Scanner(System.in);
			System.out.println("Rentrer des paramètres valides de clé:");
			System.out.print("g=");
			g=sc.nextBigInteger();
			sc.nextLine();
			System.out.print("g^a mod p=");
			gamodp=sc.nextBigInteger();
			sc.nextLine();
			System.out.print("p=");
			p=sc.nextBigInteger();
			sc.nextLine();
			r=new Cle(g,gamodp,p);
		}
		catch(InputMismatchException e){
			System.out.println("Vos paramètres ne sont pas valides");
		}
		return r;
	}
	public MessageCrypte rentrerMessageCrypter(){
		MessageCrypte m=null;
		try {
			String message;
			BigInteger gbmodp=null;

			Scanner sc = new Scanner(System.in);
			System.out.println("Rentrer des paramètres valides pour un message crypté");
			System.out.print("message crypté=");
			message=sc.nextLine();
			//sc.nextLine();
			System.out.print("gbmodp=");
			gbmodp=sc.nextBigInteger();
			sc.nextLine();
			m=new MessageCrypte(message,gbmodp);
		}
		catch (InputMismatchException e){
			System.out.println("Vos paramètres ne sont pas valides");
		}
		return m;
	}

	public MessageSigne rentrerMessageSigner(){
		MessageSigne m=null;
		try {
			String message;
			BigInteger s=null;
			BigInteger r=null;

			Scanner sc = new Scanner(System.in);
			System.out.println("Rentrer des paramètres valides pour un message signé");
			System.out.print("message signé=");
			message=sc.nextLine().toUpperCase();
			//System.out.println("message c"+message);
			//sc.nextLine();
			System.out.print("s=");
			s=sc.nextBigInteger();
			sc.nextLine();
			System.out.print("r=");
			r=sc.nextBigInteger();
			sc.nextLine();
			m=new MessageSigne(message,s,r);
		}
		catch (InputMismatchException e){
			System.out.println("Vos paramètres ne sont pas valides");
		}
		return m;
	}

	public static void main(String[] args) {
		Elgamal chiffrement=new Elgamal();
		Scanner sc = new Scanner(System.in);
		int choix;
		while (true){
			//choix=sc.nextInt();
			System.out.println("Menu:");
			System.out.println("1) Generer Cles Elgamal");
			System.out.println("2) Encrypter Elgamal");
			System.out.println("3) Décrypter Elgamal");
			System.out.println("4) Signer un message Elgamal");
			System.out.println("5) Verifier une signature Elgamal");
			System.out.println("Choix?");
			try {
				choix=sc.nextInt();
				sc.nextLine();
				if (choix==1){
					int a;
					System.out.println("Choisir en entier a(inférieur à 65500):");
					a=sc.nextInt();
					sc.nextLine();
					chiffrement.K=chiffrement.GenererClesElgamal(a);
				}
				if (choix==2){
					try {
						String message="";
						System.out.println("Entrez votre message :");
						message=sc.nextLine();
						//sc.nextLine();
						if(chiffrement.K!=null){
							System.out.print("Voulez-vous utiliser la clé generée dernierement?(y/n)   ");
							System.out.println(chiffrement.K);
							String reponse="";
							reponse=sc.next();
							sc.nextLine();
							if (reponse.equals(new String("y"))){
								chiffrement.messageCrypte=chiffrement.EncryperElgamal(message,chiffrement.K);
							}
							else {

								BigInteger g;
								BigInteger gAmodP;
								BigInteger p;
								System.out.println("Rentrer vos paramètres de clé:");
								System.out.print("g=");
								g=sc.nextBigInteger();
								sc.nextLine();
								System.out.print("g^a mod p=");
								gAmodP=sc.nextBigInteger();
								sc.nextLine();
								System.out.print("p=");
								p=sc.nextBigInteger();
								sc.nextLine();
								chiffrement.messageCrypte=chiffrement.EncryperElgamal(message,new Cle(g,gAmodP,p));


							}
						}
						else {
							System.out.println("Vous n'avez pas encore défini de clé.");
							BigInteger g;
							BigInteger gAmodP;
							BigInteger p;
							System.out.println("Rentrer des paramètres valides de clé:");
							System.out.print("g=");
							g=sc.nextBigInteger();
							sc.nextLine();
							System.out.print("g^a mod p=");
							gAmodP=sc.nextBigInteger();
							sc.nextLine();
							System.out.print("p=");
							p=sc.nextBigInteger();
							sc.nextLine();
							chiffrement.messageCrypte=chiffrement.EncryperElgamal(message,new Cle(g,gAmodP,p));
						}
					}
					catch(InputMismatchException e){
							System.out.println(e);
					}
				}
				if (choix==3){
					boolean decision=false;

					while (decision==false){
						if (chiffrement.K!=null){
							if(chiffrement.messageCrypte!=null){
								String reponse="";
								System.out.println("Vous disposez d'une clé K et d'un message crypté, voulez-vous les utiliser?(y/n)");
								System.out.println(chiffrement.messageCrypte);
								System.out.println("La clef (g="+chiffrement.K.g+" a="+chiffrement.K.a+"et p="+chiffrement.K.p+")");
								reponse=sc.next();
								sc.nextLine();
								if (reponse.equals(new String("y"))){
									decision=true;
									chiffrement.DecrypterElgamal(chiffrement.messageCrypte,chiffrement.K);
								}
								else{
									decision=false;
									chiffrement.K=null;
									chiffrement.messageCrypte=null;
								}
							}

							else { // Cle k mais pas de message
								String reponse;
								System.out.println("Vous disposez d'une clé K, voulez-vous l'utiliser?(y/n)");
								reponse=sc.next();
								sc.nextLine();
								if (reponse.equals(new String("y"))){
									chiffrement.DecrypterElgamal(chiffrement.rentrerMessageCrypter(),chiffrement.K);
									decision=true;
								}
								else{
									chiffrement.DecrypterElgamal(chiffrement.rentrerMessageCrypter(),chiffrement.rentrerCle());
									decision=true;
								}
							}
						}
						else {
							if (chiffrement.messageCrypte!=null){
								String reponse;
								System.out.println("Vous disposez d'un message signé, voulez-vous l'utiliser?(y/n)");
								reponse=sc.next();
								sc.nextLine();
								if (reponse.equals(new String("y"))){
									MessageCrypte q=chiffrement.messageCrypte;
									chiffrement.DecrypterElgamal(q,chiffrement.rentrerCleSignature());
									decision=true;
								}
								else{
									MessageCrypte q=chiffrement.rentrerMessageCrypter();
									chiffrement.DecrypterElgamal(q,chiffrement.rentrerCleSignature());
									decision=true;
								}
							}
							else {
								System.out.println("Vous ne disposez ni de message crypté, ni de clé K");
								chiffrement.DecrypterElgamal(chiffrement.rentrerMessageCrypter(),chiffrement.rentrerCle());
								decision=true;
							}
						}

					}
				}
				if (choix==4){
					System.out.println("Veuillez entrer votre message à signer");
					String message=sc.nextLine();
					String reponse;
					//sc.nextLine();
					try {
						if (chiffrement.K!=null){
							System.out.println("Vous disposez d'une clé K, voulez-vous l'utiliser?(y/n)");
							reponse=sc.next();
							sc.nextLine();
							if (reponse.equals(new String("y"))){
								chiffrement.messageSigne=chiffrement.SignerElgamal(message,chiffrement.K);
							}
							else {
								Cle x=chiffrement.rentrerCle();
								x.gAmodP=x.g.modPow(x.a,x.p);
								chiffrement.messageSigne=chiffrement.SignerElgamal(message,x);
							}
						}
						else{
							Cle x=chiffrement.rentrerCle();
							x.gAmodP=x.g.modPow(x.a,x.p);
							chiffrement.messageSigne=chiffrement.SignerElgamal(message,x);
						}
					}
					catch(NullPointerException e){

					}
				}
				if (choix==5){
					boolean decision=false;
					while (decision==false){
						if (chiffrement.K!=null){
							if(chiffrement.messageSigne!=null){
								String reponse="";
								System.out.println("Vous disposez d'une clé K et d'un message signé, voulez-vous les utiliser?(y/n)");
								System.out.println(chiffrement.messageSigne);
								System.out.println("La clef (g="+chiffrement.K.g+" a="+chiffrement.K.a+"et p="+chiffrement.K.p+")");
								reponse=sc.next();
								sc.nextLine();
								if (reponse.equals(new String("y"))){
									decision=true;
									MessageSigne q=chiffrement.messageSigne;
									chiffrement.VerifierSignatureElgamal(q.m,q.s,q.r, chiffrement.K);
								}
								else{
									decision=false;
									chiffrement.K=null;
									chiffrement.messageSigne=null;
								}
							}

							else { // Cle k mais pas de message
								String reponse;
								System.out.println("Vous disposez d'une clé K, voulez-vous l'utiliser?(y/n)");
								reponse=sc.next();
								sc.nextLine();
								if (reponse.equals(new String("y"))){
									MessageSigne q=chiffrement.rentrerMessageSigner();
									chiffrement.VerifierSignatureElgamal(q.m,q.s,q.r,chiffrement.K);
									decision=true;
								}
								else{
									MessageSigne q=chiffrement.rentrerMessageSigner();
									chiffrement.VerifierSignatureElgamal(q.m,q.s,q.r,chiffrement.rentrerCleSignature());
									decision=true;
								}
							}
						}
						else {
							if (chiffrement.messageCrypte!=null){
								String reponse;
								System.out.println("Vous disposez d'un message signé, voulez-vous l'utiliser?(y/n)");
								reponse=sc.next();
								sc.nextLine();
								if (reponse.equals(new String("y"))){
									MessageSigne q=chiffrement.messageSigne;
									chiffrement.VerifierSignatureElgamal(q.m,q.s,q.r,chiffrement.rentrerCleSignature());
									decision=true;
								}
								else{
									MessageSigne q=chiffrement.rentrerMessageSigner();
									chiffrement.VerifierSignatureElgamal(q.m,q.s,q.r,chiffrement.rentrerCleSignature());
									decision=true;
								}
							}
							else {
								System.out.println("Vous ne disposez ni de message signé, ni de clé K");
								MessageSigne q=chiffrement.rentrerMessageSigner();
								System.out.println(q);
								chiffrement.VerifierSignatureElgamal(q.m,q.s,q.r,chiffrement.rentrerCleSignature());
								decision=true;
							}
						}

					}
				}
			}
			/*Elgamal l=new Elgamal();
		Cle test=l.GenererClesElgamal(30);
		MessageCrypte m=l.EncryperElgamal("testma clique motherfucker je peux ecrire de long textes tu sais meme pas comment clikm",test);
		l.DecrypterElgamal(m,test);
		MessageSigne t=l.SignerElgamal("test",test);
		l.VerifierSignatureElgamal("test",t.s,t.r,test);*/
			catch(InputMismatchException e){
				System.out.println("Vous avez mal rentré vos paramètres");
				choix=1;
				sc.nextLine();
			}

		}
	}
}
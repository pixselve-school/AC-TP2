/**** on va ici implémenter la transformée de Fourier rapide 1D ****/

public class FFT_1D {

  //"combine" c1 et c2 selon la formule vue en TD
  // c1 et c2 sont de même taille
  // la taille du résultat est le double de la taille de c1
  public static CpxTab combine(CpxTab c1, CpxTab c2) {
    assert (c1.taille() == c2.taille()) : "combine: c1 et c2 ne sont pas de même taille, taille c1=" + c1.taille() + " taille c2=" + c2.taille();
    int n = c1.taille() + c2.taille();
    CpxTab res = new CpxTab(n);
    for (int k = 0; k < c1.taille(); k++) {
      double a = c1.get_p_imag(k);
      double b = c1.get_p_reel(k);

      double c = c2.get_p_imag(k);
      double d = c2.get_p_reel(k);

      double theta = 2 * Math.PI * k / n;

      res.set_p_imag(k, a + c * Math.cos(theta) + d * Math.sin(theta));
      res.set_p_reel(k, b + d * Math.cos(theta) - c * Math.sin(theta));

      res.set_p_imag(k + c1.taille(), a - c * Math.cos(theta) - d * Math.sin(theta));
      res.set_p_reel(k + c1.taille(), b - d * Math.cos(theta) + c * Math.sin(theta));
    }
    return res;
  }

  //renvoie la TFD d'un tableau de complexes
  //la taille de x doit être une puissance de 2
  public static CpxTab FFT(CpxTab x) {
    //A FAIRE : Test d'arrêt
    if (x.taille() == 1) {
      return x;
    }

    assert (x.taille() > 0) : "FFT: la taille de x doit être strictement positive";

    //A FAIRE : Décomposition en "pair" et "impair" et appel récursif
    CpxTab pair = new CpxTab(x.taille() / 2);
    CpxTab impair = new CpxTab(x.taille() / 2);

    for (int k = 0; k < x.taille(); k++) {
      if (k % 2 == 0) {
        pair.set_p_reel(k / 2, x.get_p_reel(k));
        pair.set_p_imag(k / 2, x.get_p_imag(k));
      } else {
        impair.set_p_reel(k / 2, x.get_p_reel(k));
        impair.set_p_imag(k / 2, x.get_p_imag(k));
      }
    }

    return combine(FFT(pair), FFT(impair));
  }

  //renvoie la TFD d'un tableau de réels
  //la taille de x doit être une puissance de 2
  public static CpxTab FFT(double[] x) {
    return FFT(new CpxTab(x));
  }

  //renvoie la transformée de Fourier inverse de y
  public static CpxTab FFT_inverse(CpxTab y) {
    CpxTab res = FFT(y.conjugue()).conjugue();
    for (int k = 0; k < res.taille(); k++) {
      res.set_p_reel(k, res.get_p_reel(k) / y.taille());
      res.set_p_imag(k, res.get_p_imag(k) / y.taille());
    }
    return res;
  }

  //calcule le produit de deux polynômes en utilisant la FFT
  //tab1 et tab2, sont les coefficients de ces polynômes
  // CpxTab sera le tableau des coefficients du polynôme produit (purement réel)
  public static CpxTab multiplication_polynome_viaFFT(double[] tab1, double[] tab2) {

    //on commence par doubler la taille des vecteurs en rajoutant des zéros à la fin (cf TD)
    double[] t1 = new double[2 * tab1.length], t2 = new double[2 * tab2.length];
    for (int i = 0; i < tab1.length; i++) {
      t1[i] = tab1[i];
      t2[i] = tab2[i];
    }

    //on calcule la FFT de t1 et t2
    CpxTab x1 = FFT(t1), x2 = FFT(t2);

    //on calcule le produit terme à terme
    for (int k = 0; k < x1.taille(); k++) {
      x1.set_p_reel(k, x1.get_p_reel(k) * x2.get_p_reel(k) - x1.get_p_imag(k) * x2.get_p_imag(k));
      x1.set_p_imag(k, x1.get_p_reel(k) * x2.get_p_imag(k) + x1.get_p_imag(k) * x2.get_p_reel(k));
    }

    //on renvoie la FFT inverse de x1
    return FFT_inverse(x1);
  }


  //renvoie un tableau de réels aléatoires
  //utile pour tester la multiplication de polynômes
  public static double[] random(int n) {
    double[] t = new double[n];

    for (int i = 0; i < n; i++)
      t[i] = Math.random();
    return t;
  }

  //effectue la multiplication de polynômes représentés par coefficients
  // p1, p2 les coefficients des deux polynômes P1 et P2
  // renvoie les coefficients du polynôme P1*P2
  private static double[] multiplication_polynome_viaCoeff(double[] p1, double[] p2) {

    int n = p1.length + p2.length - 1;
    double a, b;
    double[] out = new double[n];
    for (int k = 0; k < n; k++) {
      for (int i = 0; i <= k; i++) {
        a = (i < p1.length) ? p1[i] : 0;
        b = (k - i < p2.length) ? p2[k - i] : 0;
        out[k] += a * b;
      }
    }
    return out;
  }


  //affiche un tableau de réels
  private static void afficher(double[] t) {
    System.out.print("[");
    for (int k = 0; k < t.length; k++) {
      System.out.print(t[k]);
      if (k < (t.length - 1))
        System.out.print(" ");
    }
    System.out.println("]");
  }

  public static void main(String[] args) {
    double[] t5 = {1, 2, 3, 4};

    /* Exo 2: calculez et affichez TFD(1,2,3,4) */
    //A FAIRE
    CpxTab t = FFT(t5);
    System.out.println("TFD(1,2,3,4) = " + t);

    /* Exo 3: calculez et affichez TFD_inverse(TFD(1,2,3,4)) */
    CpxTab t_2 = FFT_inverse(t);
    System.out.println("TFD_inverse(TFD(1,2,3,4)) = " + t_2);


    /* Exo 4: multiplication polynomiale, vérification*/
    /* A(X) = 2 et B(X)=-3 */
    //A FAIRE
    double[] a = {2};
    double[] b = {-3};

    CpxTab c = multiplication_polynome_viaFFT(a, b);
    System.out.println("A(X) = 2 et B(X)=-3");
    System.out.println("A(X)*B(X) = " + c);

    /* A(X) = 2+X et B(X)= -3+2X */
    //A FAIRE
    double[] a2 = {2, 1};
    double[] b2 = {-3, 2};

    CpxTab c2 = multiplication_polynome_viaFFT(a2, b2);
    System.out.println("A(X) = 2+X et B(X)= -3+2X");
    System.out.println("A(X)*B(X) = " + c2);


    /* A(X) = 1 + 2X + 3X^2 + 4X^3 et B(X) = -3 + 2X - 5 X^2*/
	/*
		System.out.println("-----------------------------------------------------");
		System.out.println("   Comparaison des 2 méthodes de multiplications polynomiales");
		double[] t6 = {-3,2,-5,0};
		System.out.println("mult via FFT  --> " + multiplication_polynome_viaFFT(t5, t6));
		System.out.print(  "mult via coeff -> ");
		afficher(multiplication_polynome_viaCoeff(t5, t6));
	*/

    /* Exo 5: comparaison des temps de calculs */
	/*
		// Pour étude du temps de calcul 
		int n = 256;  // taille des polynômes à multiplier (testez différentes valeurs en gardant des puissances de 2)
			
		System.out.println("Temps de calcul pour n="+n);
		double[] tab1 =random(n),tab2 = random(n);
		long date1, date2;
		date1 = System.currentTimeMillis();
		multiplication_polynome_viaCoeff(tab1, tab2);
		date2 = System.currentTimeMillis();
		System.out.println("   via Coeff: " + (date2 - date1));

		date1 = System.currentTimeMillis();
		multiplication_polynome_viaFFT(tab1, tab2);
		date2 = System.currentTimeMillis();
		System.out.println("   via FFT  : " + (date2 - date1));
	*/

  }

}

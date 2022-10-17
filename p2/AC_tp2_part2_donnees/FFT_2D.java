import java.io.IOException;

public class FFT_2D {

  //renvoie la TFD d'une image de complexes
  public static CpxImg FFT(CpxImg I) {

    CpxImg out = new CpxImg(I.taille());

    // FFT 1D sur les lignes
    for (int k = 0; k < I.taille(); k++)
      out.set_line(k, FFT_1D.FFT(I.get_line(k)));

    // transposition
    out.transpose();

    // FFT 1D sur les "nouvelles" lignes de out (anciennes colonnes)
    for (int k = 0; k < I.taille(); k++)
      out.set_line(k, FFT_1D.FFT(out.get_line(k)));

    //on re transpose pour revenir dans le sens de d�part
    out.transpose();

    //on divise par la taille de I
    out.multiply(1. / I.taille());
    return out.recentrage();
  }

  //renvoie la TFD inverse d'une images de complexes
  public static CpxImg FFT_inverse(CpxImg I) {
    I = I.recentrage();
    CpxImg out = new CpxImg(I.taille());
    for (int k = 0; k < I.taille(); k++)
      out.set_line(k, I.get_line(k).conjugue());

    out = FFT(out).recentrage();
    for (int k = 0; k < I.taille(); k++)
      out.set_line(k, out.get_line(k).conjugue());
    return out;
  }

  // compression par mise � z�ro des coefficients de fr�quence
  // FI contient la TDF de I
  // Dans FI on met � z�ros tous les coefficients correspondant � des fr�quences inf�rieures � k
  public static void compression(CpxImg FI, int k) {
    // A COMPLETER
  }

  // compression par seuillage des coefficients faibles
  // FI contient la TDF de I
  // Dans FI on met � z�ros tous les coefficients dont le module est inf�rieur � seuil
  // on renvoie le nombre de coefficients conserv�s
  public static int compression_seuil(CpxImg FI, double seuil) {
    //A COMPLETER
    return 0;
  }


  public static void main(String[] args) {

//    try {
    // Pour n = 16
    // vecteur constant a = (a, . . . , a). Testez diff�rentes valeurs de a ? R.

    int n = (int) Math.pow(2, 4);
    int a = 10;

    // On cr�e un vecteur constant
    CpxTab vecteurConstant = new CpxTab(n);
    for (int i = 0; i < n; i++) {
      vecteurConstant.set_p_reel(i, a);
    }
    // On effectue la FFT (pic a 0, n*a)
    CpxTab fftVecteurConstant = FFT_1D.FFT(vecteurConstant);
    System.out.println("Vecteur constant : " + vecteurConstant);
    System.out.println("FFT du vecteur constant : " + fftVecteurConstant);


    // sinuso�de pure
    int k = 1;
    CpxTab synusoide = new CpxTab(n);
    for (int i = 0; i < n; i++) {
      synusoide.set_p_reel(i, Math.cos(2 * Math.PI * k * i / n));
    }
    CpxTab fftSinusoide = FFT_1D.FFT(synusoide);
    System.out.println("Sinuso�de : " + synusoide);
    System.out.println("FFT de la sinuso�de : " + fftSinusoide);

    // somme de 2 sinuso�des
    CpxTab sommeSinusoide = new CpxTab(n);
    for (int i = 0; i < n; i++) {
      sommeSinusoide.set_p_reel(i, Math.cos(2 * Math.PI * i / n) + 1 / 2.0 * Math.cos(2 * Math.PI * 3 * i / n));
    }

    // FFT
    CpxTab fftSommeSinusoide = FFT_1D.FFT(sommeSinusoide);
    System.out.println("Somme de 2 sinuso�des : " + sommeSinusoide);
    System.out.println("FFT de la somme de 2 sinuso�des : " + fftSommeSinusoide);


    // la fonction a
    CpxTab fonction_a = new CpxTab(n);
    for (int i = 0; i < n; i++) {
      fonction_a.set_p_reel(i, 4 + 2 * Math.sin(2 * Math.PI * 2) / n * i + Math.cos(2 * Math.PI * 7 / n * i));
    }
    CpxTab fft_fonction_a = FFT_1D.FFT(fonction_a);
    System.out.println("Fonction a : " + fonction_a);
    System.out.println("FFT de Fonction a : " + fft_fonction_a);
    try {
//      Exemple, lecture
      BytePixmap BP = new BytePixmap("p2/AC_tp2_part2_donnees/barbara_512.pgm");
      CpxImg I = new CpxImg(BP);

      // Do the FFT
      CpxImg FI = FFT(I);

      //Exemple, �criture
      BP = FI.convert_to_BytePixmap();
      BP.write("p2/results/exercice2/barbara_512_fft.pgm");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

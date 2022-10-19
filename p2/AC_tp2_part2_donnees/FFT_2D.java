import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

    //on re transpose pour revenir dans le sens de départ
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

  // compression par mise à zéro des coefficients de fréquence
  // FI contient la TDF de I
  // Dans FI on met à zéros tous les coefficients correspondant à des fréquences inférieures à k
  public static void compression(CpxImg FI, int k) {
    // Cette
    //fonction doit annuler tous les coefficients de FI (FI sera la transformée de Fourier
    //de l’image I à compresser) qui sont en dehors du carré de coté 2k centré sur le
    //centre du tableau (i.e, n/2, n/2).

    int n = FI.taille();
    int centre = n / 2;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (centre - k > i || i > centre + k || centre - k > j || j > centre + k) {
          FI.set_p_reel(i, j, 0);
          FI.set_p_imag(i, j, 0);
        }
      }
    }

  }

  // compression par seuillage des coefficients faibles
  // FI contient la TDF de I
  // Dans FI on met à zéros tous les coefficients dont le module est inférieur à seuil
  // on renvoie le nombre de coefficients conservés
  public static int compression_seuil(CpxImg FI, double seuil) {
    int n = FI.taille();
    int nb_coeff = 0;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        double reel = FI.get_p_reel(i, j);
        double imag = FI.get_p_imag(i, j);
        double module = Math.sqrt(reel * reel + imag * imag);
        if (module < seuil) {
          FI.set_p_reel(i, j, 0);
          FI.set_p_imag(i, j, 0);
        } else {
          nb_coeff++;
        }
      }
    }
    return nb_coeff;
  }


  public static void exercice1() {
//    try {
    // Pour n = 16
    // vecteur constant a = (a, . . . , a). Testez différentes valeurs de a ? R.

    int n = (int) Math.pow(2, 4);
    int a = 10;

    // On crée un vecteur constant
    CpxTab vecteurConstant = new CpxTab(n);
    for (int i = 0; i < n; i++) {
      vecteurConstant.set_p_reel(i, a);
    }
    // On effectue la FFT (pic a 0, n*a)
    CpxTab fftVecteurConstant = FFT_1D.FFT(vecteurConstant);
    System.out.println("Vecteur constant : " + vecteurConstant);
    System.out.println("FFT du vecteur constant : " + fftVecteurConstant);


    // sinusoïde pure
    int k = 1;
    CpxTab synusoide = new CpxTab(n);
    for (int i = 0; i < n; i++) {
      synusoide.set_p_reel(i, Math.cos(2 * Math.PI * k * i / n));
    }
    CpxTab fftSinusoide = FFT_1D.FFT(synusoide);
    System.out.println("Sinusoïde : " + synusoide);
    System.out.println("FFT de la sinusoïde : " + fftSinusoide);

    // somme de 2 sinusoïdes
    CpxTab sommeSinusoide = new CpxTab(n);
    for (int i = 0; i < n; i++) {
      sommeSinusoide.set_p_reel(i, Math.cos(2 * Math.PI * i / n) + 1 / 2.0 * Math.cos(2 * Math.PI * 3 * i / n));
    }

    // FFT
    CpxTab fftSommeSinusoide = FFT_1D.FFT(sommeSinusoide);
    System.out.println("Somme de 2 sinusoïdes : " + sommeSinusoide);
    System.out.println("FFT de la somme de 2 sinusoïdes : " + fftSommeSinusoide);


    // la fonction a
    CpxTab fonction_a = new CpxTab(n);
    for (int i = 0; i < n; i++) {
      fonction_a.set_p_reel(i, 4 + 2 * Math.sin(2 * Math.PI * 2) / n * i + Math.cos(2 * Math.PI * 7 / n * i));
    }
    CpxTab fft_fonction_a = FFT_1D.FFT(fonction_a);
    System.out.println("Fonction a : " + fonction_a);
    System.out.println("FFT de Fonction a : " + fft_fonction_a);
    try {

      List<String> images = new ArrayList<>();
      images.add("fingerprint");
      images.add("mire2");
      images.add("barbara_512");
      images.add("tigre_512");
      images.add("lena_512");
      images.add("mire3");
      images.add("mire1");
      for (String image : images) {
        //      Exemple, lecture
        BytePixmap BP = new BytePixmap("p2/AC_tp2_part2_donnees/" + image + ".pgm");
        CpxImg I = new CpxImg(BP);

        // Do the FFT
        CpxImg FI = FFT(I);

        //Exemple, écriture
        BP = FI.convert_to_BytePixmap();
        BP.write("p2/results/exercice2/fft/" + image + "_fft.pgm");

        // Do the iFFT
        CpxImg IFI = FFT_inverse(FI);
        BP = IFI.convert_to_BytePixmap();
        BP.write("p2/results/exercice2/ifft/" + image + "_fft.pgm");
      }


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void exercice4_1() {
    try {

      List<String> images = new ArrayList<>();
      images.add("fingerprint");
      images.add("mire2");
      images.add("barbara_512");
      images.add("tigre_512");
      images.add("lena_512");
      images.add("mire3");
      images.add("mire1");
      for (String image : images) {
        //      Exemple, lecture
        BytePixmap BP = new BytePixmap("p2/AC_tp2_part2_donnees/" + image + ".pgm");
        CpxImg I = new CpxImg(BP);

        // Do the FFT
        CpxImg FI = FFT(I);
        FI.set_p_reel(FI.taille() / 2, FI.taille() / 2, 0);
        FI.set_p_imag(FI.taille() / 2, FI.taille() / 2, 0);
        //Exemple, écriture
        BP = FI.convert_to_BytePixmap();
        BP.write("p2/results/exercice4/" + image + "_fft.pgm");


        // Do the iFFT
        CpxImg IFI = FFT_inverse(FI);
        BP = IFI.convert_to_BytePixmap();
        BP.write("p2/results/exercice4/" + image + "_ifft.pgm");
      }


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void exercice4_2() {
    try {

      List<String> images = new ArrayList<>();
      images.add("fingerprint");
      images.add("mire2");
      images.add("barbara_512");
      images.add("tigre_512");
      images.add("lena_512");
      images.add("mire3");
      images.add("mire1");
      for (String image : images) {
        //      Exemple, lecture
        BytePixmap BP = new BytePixmap("p2/AC_tp2_part2_donnees/" + image + ".pgm");
        CpxImg I = new CpxImg(BP);

        // Do the FFT
        CpxImg FI = FFT(I);
        FI.set_p_reel(FI.taille() / 2 + 1, FI.taille() / 2, 0);
        FI.set_p_imag(FI.taille() / 2 + 1, FI.taille() / 2, 0);
        //Exemple, écriture
        BP = FI.convert_to_BytePixmap();
        BP.write("p2/results/exercice4_2/" + image + "_fft.pgm");


        // Do the iFFT
        CpxImg IFI = FFT_inverse(FI);
        BP = IFI.convert_to_BytePixmap();
        BP.write("p2/results/exercice4_2/" + image + "_ifft.pgm");
      }


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void exercice5() {
    try {
      List<String> images = new ArrayList<>();
      images.add("fingerprint");
      images.add("mire2");
      images.add("barbara_512");
      images.add("tigre_512");
      images.add("lena_512");
      images.add("mire3");
      images.add("mire1");
      for (int k = 1; k < 100; k *= 2) {
        for (String image : images) {
          //      Exemple, lecture
          BytePixmap BP = new BytePixmap("p2/AC_tp2_part2_donnees/" + image + ".pgm");
          CpxImg I = new CpxImg(BP);


          // Do the FFT
          CpxImg FI = FFT(I);
          compression(FI, k);
          // Do the iFFT
          CpxImg IFI = FFT_inverse(FI);
          BP = IFI.convert_to_BytePixmap();
          //Exemple, écriture
          BP.write("p2/results/exercice5/" + image + "_compressed_k=" + k + ".pgm");


        }
      }


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void exercice6() {
    try {
      List<String> images = new ArrayList<>();
      images.add("fingerprint");
      images.add("mire2");
      images.add("barbara_512");
      images.add("tigre_512");
      images.add("lena_512");
      images.add("mire3");
      images.add("mire1");
      for (int k = 1; k < 100; k *= 2) {
        for (String image : images) {
          //      Exemple, lecture
          BytePixmap BP = new BytePixmap("p2/AC_tp2_part2_donnees/" + image + ".pgm");
          CpxImg I = new CpxImg(BP);


          // Do the FFT
          CpxImg FI = FFT(I);
          compression_seuil(FI, k);
          // Do the iFFT
          CpxImg IFI = FFT_inverse(FI);
          BP = IFI.convert_to_BytePixmap();
          //Exemple, écriture
          BP.write("p2/results/exercice6/" + image + "_compressed_k=" + k + ".pgm");


        }
      }


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static BytePixmap compression_seuil_10_percent(CpxImg I) {
    // Do the FFT
    CpxImg FI = FFT(I);
    // list all the modules values and count
    HashMap<Double, Integer> modules = new HashMap<>();
    for (int i = 0; i < FI.taille(); i++) {
      for (int j = 0; j < FI.taille(); j++) {
        double module = Math.sqrt(Math.pow(FI.get_p_reel(i, j), 2) + Math.pow(FI.get_p_imag(i, j), 2));
        if (modules.containsKey(module)) {
          modules.put(module, modules.get(module) + 1);
        } else {
          modules.put(module, 1);
        }
      }
    }
    // sort the list (descending order)
    List<Double> modules_sorted = new ArrayList<>(modules.keySet());
    Collections.sort(modules_sorted, Collections.reverseOrder());
    // get the 10% of the modules
    int count = 0;
    int count_10_percent = (int) (FI.taille() * FI.taille() * 0.1);
    int index = 0;
    while (count < count_10_percent && index < modules_sorted.size()) {
      count += modules.get(modules_sorted.get(index));
      index++;
    }
    double seuil = modules_sorted.get(index);
    compression_seuil(FI, seuil);
    // Do the iFFT
    CpxImg IFI = FFT_inverse(FI);
    return IFI.convert_to_BytePixmap();


  }

  public static BytePixmap compression_10_percent(CpxImg I) {
    // Do the FFT
    CpxImg FI = FFT(I);
    // compute the k value for 10%
    int k = (int) (FI.taille() * FI.taille() * 0.1);
    compression(FI, k);
    // Do the iFFT
    CpxImg IFI = FFT_inverse(FI);
    return IFI.convert_to_BytePixmap();
  }

  public static void exercice7() {
    try {
      List<String> images = new ArrayList<>();
      images.add("barbara_512");
      images.add("tigre_512");
      for (String image : images) {
        //      Exemple, lecture
        BytePixmap BP = new BytePixmap("p2/AC_tp2_part2_donnees/" + image + ".pgm");
        CpxImg I = new CpxImg(BP);

        compression_seuil_10_percent(I).write("p2/results/exercice7/" + image + "_compressed_10_percent_seuil.pgm");
        compression_10_percent(I).write("p2/results/exercice7/" + image + "_compressed_10_percent.pgm");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static void main(String[] args) {
    exercice7();
  }

}


package javafiles;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafiles.ModularAlgebra.Polynomials;
import javax.crypto.KeyGenerator;

public class EncryptController {

    private static EncryptController ec = new EncryptController();

    public static EncryptController getInstance() {
        return ec;
    }

    private EncryptController() {

    }

    public static final int MIN_KEY_SIZE = 64;
    public static final int MAX_KEY_SIZE = 1024;

    private byte[] key;
    private String[] keyInHex;

    /*       Stopwatch timer = new Stopwatch();*/
    /*      System.Windows.Forms.OpenFileDialog ofdForIn = new OpenFileDialog();*/
    /*       System.Windows.Forms.OpenFileDialog ofdForOut = new OpenFileDialog();*/
    Random random;

//    SecureRandom generator;
    private String[][] permTable;

    private Object sfd;

//    public EncryptController() throws NoSuchAlgorithmException {
//
////                    InitializeComponent();
////            genMode.IsChecked = true;
////            for (int i = 8; i < 64; i *= 2)
////            {
////                permSize.Items.add(i);
////            }
////            for (int i = 4; i < 32; i *= 2)
////            {
////                powPoli.Items.add(i);
////                powMatrix.Items.add(i);
////            }
////            keySize.SelectedIndex = permSize.SelectedIndex = 0;
////            powPoli.SelectedIndex = powMatrix.SelectedIndex = 1;
////            subbut.IsEnabled = permbut.IsEnabled = false;
//    }
////
    public static List<Integer> getKeySizes() {
        List<Integer> sizes = new ArrayList<>();
        for (int i = 64; i < 1024; i *= 2) {
            sizes.add(i);
        }
        return sizes;
    }

    public static List<Integer> getPoliAndMatrixItems() {
        List<Integer> values = new ArrayList<>();
        for (int i = 4; i < 32; i *= 2) {
            values.add(i);
        }
        return values;
    }

    public static byte[] hexToBin(String[] hexArray) {
        byte[] result = new byte[hexArray.length];
        for (int i = 0; i < hexArray.length; i++) {
            result[i] = new BigInteger(hexArray[i], 16).toString(2).getBytes()[0];
        }
        return result;
    }

    public String[][] getSubstitutionMatrix(Substitution subs) {
        return Polynomials.getTablePlusAdditiveComponent(
                Polynomials.productTableMatrix(
                        Polynomials.getTableOfMultiplicativelyInverseSums(
                                Polynomials.getTableOfMultiplicativelyInverse(
                                        Polynomials.fromBinToDec(subs.getPolinomial().getOmega()),
                                        Polynomials.fromBinToDec(subs.getPolinomial().getFi())),
                                subs.getAdditive().getAlpha()),
                        Polynomials.generateMatrixGalois(subs.getMatrix().getOmega(), subs.getMatrix().getFi())),
                subs.getAdditive().getBeta());
    }

    /**
     * @param ks - znachenie poley s kluchami
     * @param subs - znachenie poley s paneli Substitution
     */
    public boolean substitutionExecute(KeysSet ks, Substitution subs) {
        TimerMonitor.getInstance().start();

        String[][] result = getSubstitutionMatrix(subs);

        String keyHex = ks.getKeys().get(0) + ks.getKeys().get(1) + ks.getKeys().get(2) + ks.getKeys().get(3);
        
        String[] s = new String[keyHex.length()];
        for (int i = 0; i < s.length; i++) {
            s[i] = keyHex.charAt(i) + "";
        }
        key = hexToBin(s);
       
        keyInHex = new String[key.length / 4];
         System.out.println(key.length / 4);
        int maxNum = 0;
        List<String> table = new ArrayList<>();
        while ((int)Math.pow(2, maxNum) != (int)Math.sqrt(result.length)) {
            maxNum++;
        }
        for (int i = 0; i < key.length / (maxNum * 2); i++) {
            String x = "", y = "";
            for (int j = i * (2 * maxNum); j < i * (2 * maxNum) + maxNum; j++) {
                x += key[j];
            }
            for (int j = i * (2 * maxNum) + maxNum; j < i * (2 * maxNum) + 2 * maxNum; j++) {
                y += key[j];
            }
            table.add(String.format("%01X",
                    Polynomials.fromBinToDec(
                            result[Polynomials.fromBinToDec(x)][Polynomials.fromBinToDec(y)]) & 0xFFFFF));
        }
        Polynomials.addZeros(table);
        String newKey = "";
        for (int i = 0; i < table.size(); i++) {
            newKey += table.get(i);
        }
        for (int i = 0; i < newKey.length(); i++) {
            keyInHex[i] = newKey.charAt(i) + "";
        }
        key = hexToBin(keyInHex);
        TimerMonitor.getInstance().stop();

        return true;
    }
//
//        private void Mode_Checked(object sender, RoutedEventArgs e)
//        {
//            if (((System.Windows.Controls.RadioButton)sender).Name == "genMode")
//            {
//                genBox.IsEnabled = true;
//                loadBox.IsEnabled = false;
//            }
//            else
//            {
//                genBox.IsEnabled = false;
//                loadBox.IsEnabled = true;
//            }
//        }

    private static byte[] generateRNGCryptoSHA1PRNG(int byteSize) {
        byte[] bytes = new byte[byteSize];
        try {
            SecureRandom generator = SecureRandom.getInstance("SHA1PRNG");

            synchronized (generator) {
                generator.nextBytes(bytes);
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncryptController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bytes;
    }

    public KeysSet generateKeys(int keySize) {

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            keys.add("");
        }
        key = generateRNGCryptoSHA1PRNG(keySize);

        Random rand = new Random(new Random(4).nextInt(7));

        for (int i = 0; i < key.length; i++) {
            int temp = rand.nextInt(8);
            if ((key[i] & (byte) Math.pow(2, temp)) == (byte) Math.pow(2, temp)) {
                key[i] = 1;
            } else {
                key[i] = 0;
            }
        }

        String keyFragment = "";
        keyInHex = new String[key.length / 4];
        for (int i = 1; i <= key.length; i++) {
            keyFragment += key[i - 1];
            if (i % 4 == 0) {
                keyInHex[i / 4 - 1] = String.format("%01X", Polynomials.fromBinToDec(keyFragment) & 0xFFFFF);
                keyFragment = "";
            }
        }

        int counter = 0;
        keyFragment = "";
        for (int i = 1; i <= keyInHex.length; i++) {
            keyFragment += keyInHex[i - 1];
            if (keyFragment.length() == 16) {

                keys.set(counter / 2, keys.get(counter / 2) + keyFragment);
                keyFragment = "";
                counter++;
            }
        }

        key = hexToBin(keyInHex);

        return new KeysSet(keys.toArray(new String[keys.size()]));
    }

//    public static String generateKey(int size) {
//        if (size < MIN_KEY_SIZE || size > MAX_KEY_SIZE) {
//            throw new IllegalArgumentException("Size should be [size<MIN_KEY_SIZE||size>MAX_KEY_SIZE]");
//        }
//        StringBuffer retString = new StringBuffer();
//        try {
//            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//            keyGen.initialize(1024);
//            byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
//
//            for (int i = 0; i < publicKey.length; ++i) {
//                retString.append(Integer.toHexString(0x0100 + (publicKey[i] & 0x00FF)).substring(1));
//            }
//            //System.out.println(retString);
//        } catch (NoSuchAlgorithmException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return retString.toString();
//    }
    public Path saveKey(String path, KeysSet ks) throws IOException {
        String temp = "";
        for (int i = 0; i < 4; i++) {
            String currentKey = ks.getKeys().get(i);
            if (currentKey.isEmpty()) {
                break;
            } else {
                temp += currentKey;
            }
        }
        return Files.write(new File(path).toPath(), Arrays.asList(new String[]{temp}), StandardOpenOption.CREATE);
    }

    public KeysSet loadKey(String path) throws IOException {
        List<String> ll = new ArrayList<>();
        List<String> keys = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            keys.add("");
        }

        String temp = Files.readAllLines(new File(path).toPath()).get(0);

        for (int i = 1; i <= temp.length(); i++) {
            if (i == 129) {
                break;
            }
            keys.set((i - 1) / 32, keys.get((i - 1) / 32) + temp.charAt(i - 1) + "");
        }
        key = new byte[temp.length() * 4];

        ll.add("1111");
        for (int i = 0; i < temp.length(); i++) {
            ll.add(temp.charAt(i) + "");
        }
        Polynomials.addZeros(ll);
        ll.remove(0);
        for (int i = 0; i < ll.size(); i++) {
            for (int j = 0; j < 4; j++) {
                if ((ll.get(i).charAt(j) + "").contentEquals("1")) {
                    key[i * 4 + j] = 1;
                } else {
                    key[i * 4 + j] = 0;
                }
            }
        }

        return new KeysSet(keys.toArray(new String[keys.size()]));
    }

//        private void TabItem_GotFocus(object sender, RoutedEventArgs e)
//        {
//            this.SizeToContent = System.Windows.SizeToContent.WidthAndHeight;
//        }
    public static List<String> getFiPoliSelection(int polinomValue) {
        List<String> values = new ArrayList<>();
        String[] poli = Polynomials.getIrreduciblePolinomialsArray(
                Byte.valueOf(String.valueOf(polinomValue)), true);
        for (int i = 0; i < poli.length; i++) {
            values.add(poli[i]);
        }
        return values;
    }

    public static List<String> getOmegaPoliSelection(String polinomFiValue) {
        List<String> values = new ArrayList<>();
        int a = (int) Math.pow(2, Polynomials.countOfDigits(
                new UnsignedInteger(Polynomials.fromBinToDec(String.valueOf(polinomFiValue))))) / 2;

        String poli = polinomFiValue;
        for (int i = 2; i < (int) Math.pow(2,
                Polynomials.countOfDigits(new UnsignedInteger(Polynomials.fromBinToDec(poli)))) / 2; i++) {
            if (Polynomials.isElementPrimitive(i, Polynomials.fromBinToDec(poli))) {
                values.add(Polynomials.fromDecToBin(new UnsignedInteger(i)));
            }
        }
        return values;

    }

    public static List<String> getMatrixOmegaPoliSelection(String polinomFiValue) {
        List<String> text_3 = new ArrayList<>();
        for (int i = 2; i < (int) Math.pow(2,
                Polynomials.countOfDigits(new UnsignedInteger(Polynomials.fromBinToDec(polinomFiValue)))) / 2; i++) {
            text_3.add(Polynomials.fromDecToBin(new UnsignedInteger(i)));
        }
        return text_3;

    }

    public String[][] substitutionReportMatrix(Substitution sub) {
        String[][] result = getSubstitutionMatrix(sub);

        String[][] report = new String[(int) Math.sqrt(result.length)][(int) Math.sqrt(result.length)];

        for (int i = 0; i < (int) Math.sqrt(result.length); i++) {
            for (int j = 0; j < (int) Math.sqrt(result.length); j++) {
                String temper = String.format("%01X", Polynomials.fromBinToDec(result[i][j]) & 0xFFFFF);
                report[i][j] = temper;

            }
        }
        return report;
    }

    private void premutationReportMatrix(int premSize) {
        int segmetCount = premSize;

        String[][] report = new String[(int) Math.sqrt(permTable.length)][(int) Math.sqrt(permTable.length)];

        for (int i = 0; i < (int) Math.sqrt(permTable.length); i++) {
            for (int j = 0; j < (int) Math.sqrt(permTable.length); j++) {
                report[i][j] = permTable[i][j];
            }
        }
    }

//        private void premutationExecute(int premSize)
//        {
//            TimerMonitor.getInstance().start();
//            random = new Random(Integer.valueOf(keyInHex[3]));
//            int segmetCount = premSize;
//            DataGridView gridPerm = new DataGridView();
//            gridPerm.RowCount = gridPerm.ColumnCount = segmetCount;
//            gridPerm.Height = gridPerm.Width = 3;
//            for (int i = 0; i < segmetCount; i++)
//            {
//                gridPerm[0, i].Value = String.format("{0:X}", random.next() % segmetCount);
//                for (int j = 1; j < segmetCount; j++)
//                {
//                    Boolean m = true;
//                    do
//                    {
//                        int temper = random.next() % segmetCount;
//                        int d = 0;
//                        for (; d < j; d++)
//                        {
//                            if (String.format("{0:X}", temper) == gridPerm[d, i].Value.ToString())
//                            {
//                                break;
//                            }
//                        }
//                        if (d == j)
//                        {
//                            m = false;
//                            gridPerm[j, i].Value = String.format("{0:X}", temper);
//                        }
//                    } while (m);
//                }
//                gridPerm.Rows[i].HeaderCell.Value = gridPerm.Columns[i].HeaderCell.Value =
//                    String.format("{0:X}", i);
//            }
//            permbut.IsEnabled = true;
//            permTable = new String[gridPerm.RowCount, gridPerm.RowCount];
//            for (int i = 0; i < gridPerm.RowCount; i++)
//            {
//                for (int j = 0; j < gridPerm.RowCount; j++)
//                {
//                    permTable[i, j] = gridPerm[j, i].Value.ToString();
//                }
//            }
//            int countPow = Polynomials.countOfDigits(uint.Parse(permSize.SelectedItem.ToString())) - 1;
//            int blockSize = key.length / int.Parse(permSize.SelectedItem.ToString());
//            String lastKey = "", tkey = "";
//            for (int i = 0; i < countPow; i++)
//            {
//                tkey += key[key.length - 1 - i].ToString();
//            }
//            for (int i = 0; i < tkey.length; i++)
//            {
//                lastKey += tkey[tkey.length - 1 - i].ToString();
//            }
//            byte[] temperKey = new byte[key.length];
//            for (int i = 0; i < temperKey.length; i++)
//            {
//                temperKey[i] = key[i];
//            }
//            for (int i = 0; i < int.Parse(permSize.SelectedItem.ToString()); i++)
//            {
//                for (int j = 0; j < blockSize; j++)
//                {
//                    key[i * blockSize + j] = temperKey[Integer.valueOf(gridPerm[i, Polynomials.fromBinToDec(
//                        lastKey)].Value.ToString(), 16) * blockSize + j];
//                }
//            }
//            int summBytes = 0;
//            for (int i = 0; i < key.length / 8; i++)
//            {
//                String tempByte = "";
//                for (int j = 0; j < 8; j++)
//                {
//                    tempByte += key[8 * i + j];
//                }
//                summBytes = (summBytes ^ int.Parse(tempByte));
//            }
//            TimerMonitor.getInstance().stop();
//
//        }
    private void shift() {
        TimerMonitor.getInstance().start();
        int blockSize = Polynomials.countOfDigits(new UnsignedInteger(key.length / 2)) - 1;
        String lastKey = "", tkey = "";
        for (int i = 0; i < blockSize; i++) {
            tkey += key[key.length - 1 - i] + "";
        }

        for (int i = 0; i < tkey.length(); i++) {
            lastKey += tkey.charAt(tkey.length() - 1 - i) + "";
        }
        if ((lastKey.charAt(lastKey.length() - 1) + "").contentEquals("0")) {
            String s = lastKey.substring(0, lastKey.length() - 1);
            lastKey = s.substring(lastKey.length() - 2) + "1" + s.substring(lastKey.length() - 1);
        }
        int offset = Polynomials.fromBinToDec(lastKey);
        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < key.length; i++) {
            keyList.add(key[i] + "");
        }
        for (int i = 0; i < offset; i++) {
            String character = keyList.get(0);
            keyList.remove(0);
            keyList.add(character);
        }
        for (int i = 0; i < key.length; i++) {
            key[i] = Byte.valueOf(keyList.get(0));
        }
        TimerMonitor.getInstance().stop();

    }

//        private void InputFile_Click(object sender, RoutedEventArgs e)
//        {
//            if (ofdForIn.ShowDialog() == System.Windows.Forms.DialogResult.OK)
//            {
//                inFile.Text = ofdForIn.FileName;
//            }
//        }
//        private void OutputFile_Click(object sender, RoutedEventArgs e)
//        {
//            if (ofdForOut.ShowDialog() == System.Windows.Forms.DialogResult.OK)
//            {
//                outFile.Text = ofdForOut.FileName;
//            }
//        }
    private void finish(String inputPath, String outputPath) throws IOException {
        TimerMonitor.getInstance().start();
        byte[] text = Files.readAllBytes(new File(inputPath).toPath());
        List<Byte> l = new ArrayList<>(), textInBit = new ArrayList<Byte>();
        List<Character> res = new ArrayList<>();
        for (int i = 0; i < text.length; i++) {
            l.add(text[i]);
        }
        while ((l.size() % key.length) != 0) {
            l.add(new Integer(32).byteValue());
        }
        for (int i = 0; i < l.size(); i++) {
            String tempp = Polynomials.fromDecToBin(new UnsignedInteger(l.get(i)));
            for (int j = tempp.length(); j < 8; j++) {
                tempp = "0" + tempp;
            }
            for (int j = 0; j < 8; j++) {
                if ((tempp.charAt(i) + "").contentEquals("1")) {
                    textInBit.add(new Integer(1).byteValue());
                } else {
                    textInBit.add(new Integer(0).byteValue());
                }
            }
        }
        for (int i = 0; i < textInBit.size(); i++) {
            if (textInBit.get(i) == key[i % key.length]) {
                textInBit.set(i, new Integer(0).byteValue());
            } else {
                textInBit.set(i, new Integer(1).byteValue());
            }
        }
        l.clear();
        for (int i = 0; i < textInBit.size() / 8; i++) {
            String a = "";
            for (int j = 0; j < 8; j++) {
                a += textInBit.get(i * 8 + j);
            }
            res.add(new String(new byte[]{new Integer(Polynomials.fromBinToDec(a)).byteValue()}).charAt(0));
        }
        Byte[] b = new Byte[res.size()];
        for (int i = 0; i < b.length; i++) {
            b[i] = (res.get(i) + "").getBytes()[0];
        }

        List<String> out = new ArrayList<>();
        for (Byte b1 : b) {
            out.add(b + "");
        }

        Files.write(new File(outputPath).toPath(), out, StandardOpenOption.CREATE);
        TimerMonitor.getInstance().stop();

    }

    public static byte[] stringToBytesASCII(String str) {
        char[] buffer = str.toCharArray();
        byte[] b = new byte[buffer.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) buffer[i];
        }
        return b;
    }

}

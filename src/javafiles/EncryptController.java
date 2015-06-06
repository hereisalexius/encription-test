package javafiles;

import java.util.*;
import javafiles.ModularAlgebra.Polynomials;

public class EncryptController {

    /*       Stopwatch timer = new Stopwatch();*/
    /*      System.Windows.Forms.OpenFileDialog ofdForIn = new OpenFileDialog();*/
    /*       System.Windows.Forms.OpenFileDialog ofdForOut = new OpenFileDialog();*/
    // Random random;
       /* RNGCryptoServiceProvider generator = new RNGCryptoServiceProvider();*/
   private String[] keyInHex;
   private String[][] permTable;
   private byte[] key;

    public EncryptController() {
//                    InitializeComponent();
//            genMode.IsChecked = true;
//            for (int i = 64; i < 1024; i *= 2)
//            {
//                keySize.Items.add(i);
//            }
//            for (int i = 8; i < 64; i *= 2)
//            {
//                permSize.Items.add(i);
//            }
//            for (int i = 4; i < 32; i *= 2)
//            {
//                powPoli.Items.add(i);
//                powMatrix.Items.add(i);
//            }
//            keySize.SelectedIndex = permSize.SelectedIndex = 0;
//            powPoli.SelectedIndex = powMatrix.SelectedIndex = 1;
//            subbut.IsEnabled = permbut.IsEnabled = false;
        
    }
    
    
    private byte[] fromHexToBin(String[] hexArray)
        {
            List<String> inBin = new ArrayList<>();
            byte[] result = new byte[hexArray.length * 4];
            inBin.add("1111");
            for (int l = 0; l < hexArray.length; l++)
            {
                inBin.add(String.valueOf(hexArray[l]));
            }
            Polynomials.addZeros(inBin);
            inBin.remove(0);
            for (int i = 0; i < key.length; i++)
            {
                result[i] = Byte.valueOf(""+inBin.get(i / 4).charAt(i % 4));
            }
            return result;
        }
    
    
        /**
         * @param KeysSet - znachenie poley s kluchami
         * @param Substitution - znachenie poley s paneli Substitution
         */
        private boolean substitutionExecute(KeysSet ks,Substitution subs)
        {
            TimerMonitor.getInstance().start();
            String[][] result = Polynomials.getTablePlusAdditiveComponent(
                Polynomials.productTableMatrix(
                    Polynomials.getTableOfMultiplicativelyInverseSums(
                        Polynomials.getTableOfMultiplicativelyInverse(
                            Polynomials.fromBinToDec(subs.getPolinomial().getOmega()),
                            Polynomials.fromBinToDec(subs.getPolinomial().getFi())),
                        subs.getAdditive().getAlpha()),
                    Polynomials.generateMatrixGalois(subs.getMatrix().getOmega(), subs.getMatrix().getFi())),
                subs.getAdditive().getBeta());
            String keyHex = ks.getKeys()[0] + ks.getKeys()[1] + ks.getKeys()[2] + ks.getKeys()[3];
            String[] s = new String[keyHex.length()];
            for (int i = 0; i < s.length; i++)
            {
                s[i] = keyHex.charAt(i)+"";
            }
            key = fromHexToBin(s);
            keyInHex = new String[key.length / 4];
            int maxNum = 0;
            List<String> table = new ArrayList<>();
            for (int i = 0; ; i++)
            {
                if ((int)Math.pow(2, maxNum) == (int)Math.sqrt(result.length))
                {
                    break;
                }
                maxNum++;
            }
            for (int i = 0; i < key.length / (maxNum * 2); i++)
            {
                String x = "", y = "";
                for (int j = i * (2 * maxNum); j < i * (2 * maxNum) + maxNum; j++)
                {
                    x += key[j];
                }
                for (int j = i * (2 * maxNum) + maxNum; j < i * (2 * maxNum) + 2 * maxNum; j++)
                {
                    y += key[j];
                }
                table.add(String.format("%05X",
                    Polynomials.fromBinToDec(
                    result[Polynomials.fromBinToDec(x)][Polynomials.fromBinToDec(y)])& 0xFFFFF));
            }
            Polynomials.addZeros(table);
            String newKey = "";
            for (int i = 0; i < table.size(); i++)
            {
                newKey += table.get(i);
            }
            for (int i = 0; i < newKey.length(); i++)
            {
                keyInHex[i] = newKey.charAt(i)+"";
            }
            key = fromHexToBin(keyInHex);
            TimerMonitor.getInstance().stop();
            
            return true;
        }

        private void Mode_Checked(object sender, RoutedEventArgs e)
        {
            if (((System.Windows.Controls.RadioButton)sender).Name == "genMode")
            {
                genBox.IsEnabled = true;
                loadBox.IsEnabled = false;
            }
            else
            {
                genBox.IsEnabled = false;
                loadBox.IsEnabled = true;
            }
        }

        private void Button_Click_1(object sender, RoutedEventArgs e)
        {
            for (int i = 0; i < 4; i++)
            {
                System.Windows.Controls.TextBox tbs = (System.Windows.Controls.TextBox)
                         LogicalTreeHelper.FindLogicalNode(keyBox, "t" + i.ToString());
                tbs.Text = "";
            }
            key = new byte[int.Parse(keySize.SelectedItem.ToString())];
            generator.GetNonZeroBytes(key);
            Random rand = new Random(new Random(4).Next(0, 7));

            for (int i = 0; i < key.length; i++)
            {
                int temp = rand.Next(0, 8);
                if ((key[i] & (byte)Math.pow(2, temp)) == (byte)Math.pow(2, temp))
                {
                    key[i] = 1;
                }
                else
                {
                    key[i] = 0;
                }
            }
            String keyFragment = "";
            keyInHex = new String[key.length / 4];
            for (int i = 1; i <= key.length; i++)
            {
                keyFragment += key[i - 1];
                if (i % 4 == 0)
                {
                    keyInHex[i / 4 - 1] = String.format("{0:X}", Polynomials.fromBinToDec(keyFragment));
                    keyFragment = "";
                }
            }
            int counter = 0;
            keyFragment = "";
            for (int i = 1; i <= keyInHex.length; i++)
            {
                keyFragment += keyInHex[i - 1];
                if (keyFragment.length == 16)
                {

                    System.Windows.Controls.TextBox tbs = (System.Windows.Controls.TextBox)
                         LogicalTreeHelper.FindLogicalNode(keyBox, "t" + ((counter / 2).ToString()));
                    tbs.Text += keyFragment;
                    keyFragment = "";
                    counter++;
                }
            }
            key = fromHexToBin(keyInHex);
        }

        private void Button_Click_2(object sender, RoutedEventArgs e)
        {
            System.Windows.Forms.SaveFileDialog sfd = new SaveFileDialog();
            sfd.Filter = "txt files (*.txt)|*.txt";
            if (sfd.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                String temp = "";
                for (int i = 0; i < 4; i++)
                {
                    System.Windows.Controls.TextBox tbs = (System.Windows.Controls.TextBox)
                         LogicalTreeHelper.FindLogicalNode(keyBox, "t" + i.ToString());
                    if (tbs.Text == "")
                    {
                        break;
                    }
                    else
                    {
                        temp += tbs.Text;
                    }
                }
                System.IO.File.Create(sfd.FileName).Close();
                System.IO.File.WriteAllText(sfd.FileName, temp);
            }
        }

        private void Button_Click_3(object sender, RoutedEventArgs e)
        {
            List<String> ll = new ArrayList<>();
            System.Windows.Forms.OpenFileDialog ofd = new OpenFileDialog();
            ofd.Filter = "txt files (*.txt)|*.txt";
            if (ofd.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                for (int i = 0; i < 4; i++)
                {
                    System.Windows.Controls.TextBox tbs = (System.Windows.Controls.TextBox)
                             LogicalTreeHelper.FindLogicalNode(keyBox, "t" + i.ToString());
                    tbs.Text = "";
                }
                String temp = System.IO.File.ReadAllText(ofd.FileName);
                for (int i = 1; i <= temp.length; i++)
                {
                    if (i == 129)
                    {
                        break;
                    }
                    System.Windows.Controls.TextBox tbs = (System.Windows.Controls.TextBox)
                         LogicalTreeHelper.FindLogicalNode(keyBox, "t" + (((i - 1) / 32).ToString()));
                    tbs.Text += temp[i - 1];
                }
                key = new byte[temp.length * 4];
                keyPath.Text = ofd.FileName;
                ll.add("1111");
                for (int i = 0; i < temp.length; i++)
                {
                    ll.add(String.valueOf(Integer.valueOf(temp[i].ToString(), 16), 2));
                }
                Polynomials.addZeros(ll);
                ll.remove(0);
                for (int i = 0; i < ll.size(); i++)
                {
                    for (int j = 0; j < 4; j++)
                    {
                        if (ll[i][j] == '1')
                        {
                            key[i * 4 + j] = 1;
                        }
                        else
                        {
                            key[i * 4 + j] = 0;
                        }
                    }
                }
            }
        }

        private void TabItem_GotFocus(object sender, RoutedEventArgs e)
        {
            this.SizeToContent = System.Windows.SizeToContent.WidthAndHeight;
        }

        private void powPoli_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            text_0.Items.Clear();
            String[] poli = Polynomials.GetIrreduciblePolinomialsArray(
                byte.Parse(powPoli.SelectedItem.ToString()), true);
            for (int i = 0; i < poli.length; i++)
            {
                text_0.Items.add(poli[i]);
            }
            text_0.SelectedIndex = 0;
        }

        private void text_0_SizeChanged(object sender, SelectionChangedEventArgs e)
        {
            if (text_0.SelectedItem != null)
            {
                int a = (int)Math.pow(2, Polynomials.CountOfDigits(
                  (uint)Polynomials.fromBinToDec(text_0.SelectedItem.ToString()))) / 2;
                text_1.Items.Clear();
                if (text_0.SelectedItem != null)
                {
                    String poli = text_0.SelectedItem.ToString();
                    for (int i = 2; i < (int)Math.pow(2,
                        Polynomials.CountOfDigits((uint)Polynomials.fromBinToDec(poli))) / 2; i++)
                    {
                        if (Polynomials.IsElementPrimitive(i, Polynomials.fromBinToDec(poli)))
                        {
                            text_1.Items.add(Polynomials.FromDecToBin((uint)i));
                        }
                    }
                    text_1.SelectedIndex = 0;
                }  
            }
            
        }

        private void powMatrix_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            text_2.Items.Clear();
            String[] poli = Polynomials.GetIrreduciblePolinomialsArray(
                byte.Parse(powMatrix.SelectedItem.ToString()), true);
            for (int i = 0; i < poli.length; i++)
            {
                text_2.Items.add(poli[i]);
            }
            text_2.SelectedIndex = 0;
        }

        private void text_2_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            text_3.Items.Clear();
            if (text_2.SelectedItem != null)
            {
                String poli = text_2.SelectedItem.ToString();
                for (int i = 2; i < (int)Math.pow(2,
                    Polynomials.CountOfDigits((uint)Polynomials.fromBinToDec(text_2.SelectedItem.ToString())))/2; i++)
                {
                    text_3.Items.add(Polynomials.FromDecToBin((uint)i));
                }
                text_3.SelectedIndex = 0;
            }

        }

        private void Button_Click_5(object sender, RoutedEventArgs e)
        {
            String[,] result = Polynomials.getTablePlusAdditiveComponent(
                Polynomials.productTableMatrix(
                    Polynomials.getTableOfMultiplicativelyInverseSums(
                        Polynomials.getTableOfMultiplicativelyInverse(
                            Polynomials.fromBinToDec(text_1.SelectedItem.ToString()),
                            Polynomials.fromBinToDec(text_0.SelectedItem.ToString())),
                        text_4.Text),
                    Polynomials.generateMatrixGalois(text_3.SelectedItem.ToString(), text_2.SelectedItem.ToString())),
                text_5.Text);

            Form f = new Form();
            DataGridView dgv = new DataGridView();
            f.Controls.Clear();
            f.Show();
            f.Controls.add(dgv);
            dgv.RowCount = (int)Math.sqrt(result.length);
            dgv.ColumnCount = (int)Math.sqrt(result.length);
            f.Show();
            int s = 0;
            for (int i = 0; i < (int)Math.sqrt(result.length); i++)
            {
                for (int j = 0; j < (int)Math.sqrt(result.length); j++)
                {
                    String temper = String.format(" {0:X}", Polynomials.fromBinToDec(result[i, j]));
                    dgv[j, i].Value = temper;
                    s = i % 2;
                    if (s == 0)
                    {
                        dgv.Rows[i].Cells[j].Style.BackColor = System.Drawing.Color.LightGreen;
                    }
                }
                dgv.Rows[i].HeaderCell.Value = dgv.Columns[i].HeaderCell.Value = String.format("{0:X}", i);
            }
            dgv.AutoResizeColumns();
            dgv.AutoResizeRows();
            dgv.AutoSize = true;
            f.AutoSize = true;

            f.AutoScroll = true;
            f.MaximumSize = new System.Drawing.Size(1000, 700);
            f.Size = new System.Drawing.Size(dgv.Width, dgv.Height);
        }

        private void Button_Click_4(object sender, RoutedEventArgs e)
        {
            int segmetCount = int.Parse(permSize.SelectedItem.ToString());
            Form f = new Form();
            DataGridView gridPerm = new DataGridView();
            f.Controls.Clear();
            f.Show();
            f.Controls.add(gridPerm);
            gridPerm.ColumnCount = gridPerm.RowCount = (int)Math.sqrt(permTable.length);
            int s = 0;
            for (int i = 0; i < (int)Math.sqrt(permTable.length); i++)
            {
                if ((i % 2) == 0)
                {
                    gridPerm.Rows[i].Cells[0].Style.BackColor = System.Drawing.Color.LightGreen;
                }
                for (int j = 0; j < (int)Math.sqrt(permTable.length); j++)
                {
                    gridPerm[j, i].Value = permTable[i, j];
                    s = i % 2;
                    if (s == 0)
                    {
                        gridPerm.Rows[i].Cells[j].Style.BackColor = System.Drawing.Color.LightGreen;
                    }
                }

                gridPerm.Rows[i].HeaderCell.Value = gridPerm.Columns[i].HeaderCell.Value = String.format("{0:X}", i);
            }
            gridPerm.AutoResizeColumns();
            gridPerm.AutoResizeRows();
            gridPerm.AutoSize = true;
            f.AutoSize = true;
            f.AutoScroll = true;
            f.MaximumSize = new System.Drawing.Size(1000, 700);
            f.Size = new System.Drawing.Size(gridPerm.Width, gridPerm.Height);
        }

        private void Button_Click_6(object sender, RoutedEventArgs e)
        {
            timer.Start();
            random = new Random(int.Parse((String.valueOf(Integer.valueOf(keyInHex[3], 16), 2))));
            int segmetCount = int.Parse(permSize.SelectedItem.ToString());
            DataGridView gridPerm = new DataGridView();
            gridPerm.RowCount = gridPerm.ColumnCount = segmetCount;
            gridPerm.Height = gridPerm.Width = 3;
            for (int i = 0; i < segmetCount; i++)
            {
                gridPerm[0, i].Value = String.format("{0:X}", random.Next() % segmetCount);
                for (int j = 1; j < segmetCount; j++)
                {
                    Boolean m = true;
                    do
                    {
                        int temper = random.Next() % segmetCount;
                        int d = 0;
                        for (; d < j; d++)
                        {
                            if (String.format("{0:X}", temper) == gridPerm[d, i].Value.ToString())
                            {
                                break;
                            }
                        }
                        if (d == j)
                        {
                            m = false;
                            gridPerm[j, i].Value = String.format("{0:X}", temper);
                        }
                    } while (m);
                }
                gridPerm.Rows[i].HeaderCell.Value = gridPerm.Columns[i].HeaderCell.Value =
                    String.format("{0:X}", i);
            }
            permbut.IsEnabled = true;
            permTable = new String[gridPerm.RowCount, gridPerm.RowCount];
            for (int i = 0; i < gridPerm.RowCount; i++)
            {
                for (int j = 0; j < gridPerm.RowCount; j++)
                {
                    permTable[i, j] = gridPerm[j, i].Value.ToString();
                }
            }
            int countPow = Polynomials.CountOfDigits(uint.Parse(permSize.SelectedItem.ToString())) - 1;
            int blockSize = key.length / int.Parse(permSize.SelectedItem.ToString());
            String lastKey = "", tkey = "";
            for (int i = 0; i < countPow; i++)
            {
                tkey += key[key.length - 1 - i].ToString();
            }
            for (int i = 0; i < tkey.length; i++)
            {
                lastKey += tkey[tkey.length - 1 - i].ToString();
            }
            byte[] temperKey = new byte[key.length];
            for (int i = 0; i < temperKey.length; i++)
            {
                temperKey[i] = key[i];
            }
            for (int i = 0; i < int.Parse(permSize.SelectedItem.ToString()); i++)
            {
                for (int j = 0; j < blockSize; j++)
                {
                    key[i * blockSize + j] = temperKey[Integer.valueOf(gridPerm[i, Polynomials.fromBinToDec(
                        lastKey)].Value.ToString(), 16) * blockSize + j];
                }
            }
            int summBytes = 0;
            for (int i = 0; i < key.length / 8; i++)
            {
                String tempByte = "";
                for (int j = 0; j < 8; j++)
                {
                    tempByte += key[8 * i + j];
                }
                summBytes = (summBytes ^ int.Parse(tempByte));
            }
            timer.Stop();
            this.Title = timer.ElapsedMilliseconds.ToString();
            timer.Reset();
        }

        private void Shift_Click(object sender, RoutedEventArgs e)
        {
            timer.Start();
            int blockSize = Polynomials.CountOfDigits((uint)(key.length / 2)) - 1;
            String lastKey = "", tkey = "";
            for (int i = 0; i < blockSize; i++)
            {
                tkey += key[key.length - 1 - i].ToString();
            }

            for (int i = 0; i < tkey.length; i++)
            {
                lastKey += tkey[tkey.length - 1 - i].ToString();
            }
            if (lastKey[lastKey.length - 1] == '0')
            {
                lastKey = (lastKey.Remove(lastKey.length - 1).Insert(lastKey.length - 2, "1"));
            }
            int offset = Polynomials.fromBinToDec(lastKey);
            List<String> keyList = new ArrayList<>();
            for (int i = 0; i < key.length; i++)
            {
                keyList.add(key[i].ToString());
            }
            for (int i = 0; i < offset; i++)
            {
                String character = keyList[0];
                keyList.remove(0);
                keyList.add(character);
            }
            for (int i = 0; i < key.length; i++)
            {
                key[i] = byte.Parse(keyList[i]);
            }
            timer.Stop();
            this.Title = timer.ElapsedMilliseconds.ToString();
            timer.Reset();
        }
        
        private void InputFile_Click(object sender, RoutedEventArgs e)
        {
            if (ofdForIn.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                inFile.Text = ofdForIn.FileName;
            }
        }
        private void OutputFile_Click(object sender, RoutedEventArgs e)
        {
            if (ofdForOut.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                outFile.Text = ofdForOut.FileName;
            }
        }
        private void Finish(object sender, RoutedEventArgs e)
        {
            timer.Start();
            byte[] text = System.IO.File.ReadAllBytes(ofdForIn.FileName);
            List<byte> l = new List<byte>(), textInBit = new List<byte>();
            List<char> res = new List<char>();
            for (int i = 0; i < text.length; i++)
            {
                l.add(text[i]);
            }
            while ((l.size() % key.length) != 0)
            {
                l.add(32);
            }
            for (int i = 0; i < l.size(); i++)
            {
                String tempp = Polynomials.FromDecToBin(l[i]);
                for (int j = tempp.length; j < 8; j++)
                {
                    tempp = tempp.Insert(0, "0");
                }
                for (int j = 0; j < 8; j++)
                {
                    if (tempp[j] == '1')
                    {
                        textInBit.add(1);
                    }
                    else
                    {
                        textInBit.add(0);
                    }
                }
            }
            for (int i = 0; i < textInBit.size(); i++)
            {
                if (textInBit[i] == key[i % key.length])
                {
                    textInBit[i] = 0;
                }
                else
                {
                    textInBit[i] = 1;
                }
            }
            l.Clear();
            for (int i = 0; i < textInBit.size() / 8; i++)
            {
                String a = "";
                for (int j = 0; j < 8; j++)
                {
                    a += textInBit[i * 8 + j];
                }
                res.add(Convert.ToChar((byte)Polynomials.fromBinToDec(a)));
            }
            byte[] b = new byte[res.size()];
            for (int i = 0; i < b.length; i++)
            {
                b[i] = Convert.ToByte(res[i]);
            }
            System.IO.File.WriteAllBytes(ofdForOut.FileName, b);
            timer.Stop();
            this.Title = timer.ElapsedMilliseconds.ToString();
            timer.Reset();
        }
   
   

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafiles;

import java.util.*;

public class ModularAlgebra {

    public static class Polynomials {
        /// <summary>
        /// Всі строки в массиві будуть доповнені спереду нулями і матимуть довжину найдовшої строки в колекції
        /// </summary>
        /// <param name="list">Список зі строками, які треба доповнити нулями</param>
        /// <returns>Список зі строками одинакової довжини</returns>
        /// <exception cref="System.IllegalArgumentException">Значення змінної list дорівнює null</exception>
        public static List<String> addZeros(List<String> list) {

            int length = 0;
            for (String str : list) {
                if (str.length() > length) {
                    length = str.length();
                }
            }
            for (int i = 0; i < list.size(); i++) {
                String add = "";
                for (int temp = 0; temp < length - list.get(i).length(); temp++) {
                    add += "0";
                }
                list.set(i, add + list.get(i));
            }
            return list;
        }

        /// <summary>
        /// Знаходить кількість розрядів натурального десяткового числа
        /// </summary>
        /// <param name="numeric">Задає натуральне десяткове число, кількість розрядів якого слід знайти</param>
        /// <returns>Число, що вказує кількість розрядів натурального десяткового числа</returns>
        public static int countOfDigits(UnsignedInteger numeric) {
            int limit = 0;
            while (numeric.getUnsignedValue() >> limit != 0) {
                limit++;
            }
            return limit;
        }

        /// <summary>
        /// Переводить строку, що представляє собою число у двійковій системі числення, в десяткове число
        /// </summary>
        /// <param name="numeric">Задає десяткове число, яке треба перевести у десяткову систему числення</param>
        /// <returns>Натуральне десяткове число, отримане із заданого двійкового</returns>
        /// <exception cref="System.IllegalArgumentException">Параметр numeric не вдалось розпізнати як послідовність бітів</exception>
        public static int fromBinToDec(String numeric) {
            int temp = 0;
            for (int i = numeric.length() - 1; i >= 0; i--) {
                if (numeric.charAt(numeric.length() - i - 1) == '1' || numeric.charAt(numeric.length() - i - 1) == '0') {
                    if (numeric.charAt(numeric.length() - i - 1) == '1') {
                        temp += (int) Math.pow(2, i);
                    }
                } else {
                    throw new IllegalArgumentException("Параметр numeric не вдалось розпізнати як послідовність бітів");
                }
            }
            return temp;
        }

        /// <summary>
        /// Переводить натуральне число із десяткової системи числення в двійкову
        /// </summary>
        /// <param name="numeric">Задає десяткове число, яке треба перевести у двійкову систему числення</param>
        /// <returns>Строку, що представляє собою послідовність бітів заданого десяткового числа</returns>
        public static String fromDecToBin(UnsignedInteger numeric) {
            String polinomialInString = "";
            int limit = countOfDigits(numeric);
            for (int powerResult = (int) Math.pow(2, limit - 1); powerResult > 0; powerResult /= 2) {
                if ((numeric.getUnsignedValue() & powerResult) == powerResult) {
                    polinomialInString += '1';
                } else {
                    polinomialInString += '0';
                }
            }
            return polinomialInString;
        }

        /// <summary>
        /// Генерує матрицю Галуа
        /// </summary>
        /// <param name="element">Двійкове число, на основі якого формується матриця Галуа</param>
        /// <param name="polinomial">Незвідний поліном у двійковій формі, що є модулем, на основі якого будується матриця</param>
        /// <returns>Матрицю Галуа</returns>
        public static String[] generateMatrixGalois(String element, String polinomial) {
            List<String> temper = new ArrayList<>();
            temper.add(element);
            temper.add(polinomial.substring(1));

            addZeros(temper);

            List<String> listOfPolinomials = new ArrayList<>();
            listOfPolinomials.add(String.valueOf(fromBinToDec(temper.get(0))));
            int elem = 2, poli = fromBinToDec(polinomial);
            int polinomialLimit = countOfDigits(new UnsignedInteger(poli)),
                    elementLimit = countOfDigits(new UnsignedInteger(elem)), elementNow = fromBinToDec(element);

            for (int i = 0; i < polinomial.length() - 1; i++) {
                int temp = 0;

                for (int k = 0; k < elementLimit; k++) {
                    if ((elem & (int) Math.pow(2, k)) == (int) Math.pow(2, k)) {
                        temp = (temp ^ (elementNow << k));
                    }
                }
                elementNow = temp;
                int elementOver = countOfDigits(new UnsignedInteger(elementNow));

                if (elementOver > polinomialLimit - 1) {
                    for (;;) {
                        int over = countOfDigits(new UnsignedInteger(elementNow));

                        if (over >= polinomialLimit) {
                            elementNow = (elementNow ^ (poli << (over - polinomialLimit)));
                        } else {
                            break;
                        }
                    }
                }
                listOfPolinomials.add(String.valueOf(elementNow));
            }

            for (int i = 0; i < listOfPolinomials.size(); i++) {
                listOfPolinomials.set(i, fromDecToBin(new UnsignedInteger(Integer.valueOf(listOfPolinomials.get(i)))));
            }
            listOfPolinomials = addZeros(listOfPolinomials);
            String[] matrix = new String[polinomial.length() - 1];
            for (int i = 0; i < polinomial.length() - 1; i++) {
                matrix[matrix.length - 1 - i] = listOfPolinomials.get(i);
            }
            return matrix;
        }

        /// <summary>
        /// Знаходить усі незвідні поліноми заданого степеня та повертає їх у вигляді масива
        /// </summary>
        /// <param name="power">Задає степінь незвідних поліномів, які треба знайти</param>        
        /// <param name="inBitVector">Якщо передати true, то поліноми будуть представлені послідовністю бітів. Інакше - числами в десятковій системі числення</param>
        /// <returns>Масив строк, що представляють собою незвідні поліноми</returns>
        public static String[] getIrreduciblePolinomialsArray(byte power, boolean inBitVector) {
            List<String> polinoms = new ArrayList<>();
            int maxPolinom = (int) Math.pow(2, power + 1) - 1;

            for (int i = (maxPolinom + 1) / 2; i <= maxPolinom; i++) {
                if (isPolinomialIrreducible(i)) {
                    polinoms.add(i + "");
                }
            }
            if (inBitVector == true) {
                for (int i = 0; i < polinoms.size(); i++) {
                    polinoms.set(i, fromDecToBin(new UnsignedInteger(Integer.valueOf(polinoms.get(i)))));
                }
            }
            return polinoms.toArray(new String[polinoms.size()]);
        }

        /// <summary>
        /// Знаходить послідовність степенів заданого формуючого елемента по заданому модулю
        /// </summary>
        /// <param name="element">Формуючий елемент у десятковому вигляді</param>
        /// <param name="polinomial">Поліном у десятковому вигляді</param>
        /// <param name="withAddition">Якщо передати true, то всі строки в массиві будуть доповнені спереду нулями і матимуть довжину найдовшої строки в масиві</param>
        /// <param name="inBitVector">Якщо передати true, то степені формуючого елемента будуть представлені послідовністю бітів. Інакше - числами в десятковій системі числення</param>
        /// <returns>Масив, елементами якого є степені формуючого елемента, взяті по модулю заданого полінома</returns>
        /// <exception cref="System.IllegalArgumentException">Введений поліном не є незвідним</exception>
        public static String[] getSequenceOfPowers(int element, int polinomial, boolean withAddition, boolean inBitVector) {
            if (!isPolinomialIrreducible(polinomial)) {
                throw new IllegalArgumentException("Заданий поліном не є незвідним");
            } else {
                List<String> listOfPolinomials = new ArrayList<>();
                listOfPolinomials.add("1");
                int polinomialLimit = countOfDigits(new UnsignedInteger(polinomial)),
                        elementLimit = countOfDigits(new UnsignedInteger(element)), elementNow = 1;

                for (;;) {
                    int temp = 0;

                    for (int k = 0; k < elementLimit; k++) {
                        if ((element & (int) Math.pow(2, k)) == (int) Math.pow(2, k)) {
                            temp = (temp ^ (elementNow << k));
                        }
                    }

                    elementNow = temp;
                    int elementOver = countOfDigits(new UnsignedInteger(elementNow));

                    if (elementOver > polinomialLimit - 1) {
                        for (;;) {
                            int over = countOfDigits(new UnsignedInteger(elementNow));

                            if (over >= polinomialLimit) {
                                elementNow = (elementNow ^ (polinomial << (over - polinomialLimit)));
                            } else {
                                break;
                            }
                        }
                    }
                    listOfPolinomials.add(elementNow + "");
                    if (elementNow == 1) {
                        break;
                    }
                }

                if (inBitVector == true) {
                    for (int i = 0; i < listOfPolinomials.size(); i++) {
                        listOfPolinomials.set(i, fromDecToBin(new UnsignedInteger(Integer.valueOf(listOfPolinomials.get(i)))));
                    }
                }
                if (withAddition == true) {
                    listOfPolinomials = addZeros(listOfPolinomials);
                }
                return listOfPolinomials.toArray(new String[listOfPolinomials.size()]);
            }
        }

        public static String[] getSequenceOfPowers(int element, int polinomial) {
            return getSequenceOfPowers(element, polinomial, false, false);
        }

        /// <summary>
        /// Знаходить таблицю взаємопов'язаних мультиплікативно зворотніх для заданого незвідного полінома і примітивного формуючого елемента
        /// </summary>
        /// <param name="element">Формуючий елемент у десятковому вигляді</param>
        /// <param name="polinomial">Поліном у десятковому вигляді</param>        
        /// <returns>Двовимірний масив, елементами якого є мультиплікативно зворотні елементи до елементів послідовності максимальної довжини, представлені у двійковій системі числення. Кожена строка масиву доповнена спереду нулями, щоб мати довжину найдовшої строки</returns>
        /// <exception cref="System.IllegalArgumentException">Введений поліном не є незвідним</exception>
        /// <exception cref="System.IllegalArgumentException">Заданий формуючий елемент не є примітивним для заданого незвідного полінома</exception>
        public static String[][] getTableOfMultiplicativelyInverse(int element, int polinomial) {
            if (!isPolinomialIrreducible(polinomial)) {
                throw new IllegalArgumentException("Заданий поліном не є незвідним");
            }
            if (!isElementPrimitive(element, polinomial)) {
                throw new IllegalArgumentException("Заданий формуючий елемент не є примітивним для заданого полінома");
            } else {
                String[] mSequence = getSequenceOfPowers(element, polinomial, true, true);
                int size = (int) Math.pow(2, (fromDecToBin(new UnsignedInteger(polinomial)).length() - 1) / 2);
                String maxHalf = fromDecToBin(new UnsignedInteger(size - 1));
                List<String> temp = new ArrayList<>();
                temp.add(maxHalf);
                temp.add("");
                temp.add("");

                String[][] reverse = new String[size][size];
                for (int i = 0; i < maxHalf.length() * 2; i++) {
                    reverse[0][0] += '0';
                }
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (i == 0 && j == 0) {
                            continue;
                        }
                        int fix = 0;
                        String x1 = fromDecToBin(new UnsignedInteger(j)), x2 = fromDecToBin(new UnsignedInteger(i));
                        temp.set(1, x1);
                        temp.set(2, x2);
                        temp = addZeros(temp);
                        do {
                            if ((temp.get(2) + temp.get(1)).contentEquals(mSequence[fix])) {
                                reverse[i][j] = mSequence[mSequence.length - 1 - fix];
                                break;
                            }
                            fix++;
                        } while (true);
                    }
                }
                return reverse;
            }
        }

        /// <summary>
        /// Знаходить двовимірний масив, елементами якого є мультиплікативно зворотні суми.
        /// </summary>
        /// <param name="tableOfMultiplicativelyInverse">Квадратна таблиця елементів</param>
        /// <param name="alpha">Двійкове число, котре вказує на величину зсуву</param>        
        /// <returns>Двовимірний масив, елементами якого є мультиплікативно зворотні суми. Кожна строка масиву доповнена спереду нулями, щоб мати довжину найдовшої строки</returns>
        public static String[][] getTableOfMultiplicativelyInverseSums(String[][] tableOfMultiplicativelyInverse, String alpha) {
            String x1 = "", x2 = "";
            List<String> tempList = new ArrayList<>();
            for (int i = 0; i < alpha.length() / 2; i++) {
                x2 += alpha.charAt(i);
                x1 += alpha.charAt(i + alpha.length() / 2);
            }
            String[][] result = tableOfMultiplicativelyInverse;
            for (int i = 0; i < (int) Math.sqrt(result.length); i++) {
                for (int j = 0; j < (int) Math.sqrt(result.length); j++) {
                    tempList.add(result[i][j]);
                }
            }
            String inverse = result[fromBinToDec(x2)][fromBinToDec(x1)];
            while (true) {
                if (tempList.get(0).contentEquals(inverse)) {
                    for (int i = 0; i < (int) Math.sqrt(result.length); i++) {
                        for (int j = 0; j < (int) Math.sqrt(result.length); j++) {
                            result[i][j] = tempList.get(0);
                            tempList.remove(0);
                        }
                    }
                    return result;
                }
                tempList.add(tempList.get(0));
                tempList.remove(0);
            }
        }

        /// <summary>
        /// Знаходить суму елементів таблиці і адитивної компоненти
        /// </summary>
        /// <param name="table">Таблиця, елементами якої є двійкові числа, котрі слід скласти з адитивною компонентою</param>
        /// <param name="component">Адитивна компонента</param>
        /// <returns>Таблицю, елементами якої є суми елементів заданої таблиці та адивиної компонети</returns>
        public static String[][] getTablePlusAdditiveComponent(String[][] table, String component) {
            List<String> temper = new ArrayList<>();
            String[][] result = new String[(int) Math.sqrt(table.length)][(int) Math.sqrt(table.length)];
            for (int i = 0; i < (int) Math.sqrt(table.length); i++) {
                for (int j = 0; j < (int) Math.sqrt(table.length); j++) {
                    temper.add(fromDecToBin(new UnsignedInteger(fromBinToDec(table[i][j]) ^ fromBinToDec(component))));
                }
            }
            addZeros(temper);
            for (int i = 0; i < (int) Math.sqrt(table.length); i++) {
                for (int j = 0; j < (int) Math.sqrt(table.length); j++) {
                    result[i][j] = temper.get(i * (int) Math.sqrt(result.length) + j);
                }
            }
            return result;
        }

        /// <summary>
        /// Визначає, чи є даний формуючий елемент примітивним для заданого незвідного полінома
        /// </summary>
        /// <param name="element">Формуючий елемент у десятковому вигляді</param>
        /// <param name="polinomial">Поліном у десятковому вигляді</param>
        /// <returns>Значення, яке показує, чи є даний формуючий елемент примітивним для заданого полінома. Якщо значення рівне true, то формуючий елемент примітивний</returns>
        /// <exception cref="System.IllegalArgumentException">Введений поліном не є незвідним</exception>
        public static boolean isElementPrimitive(int element, int polinomial) {
            if (!isPolinomialIrreducible(polinomial)) {
                throw new IllegalArgumentException("Заданий поліном не є незвідним");
            } else {
                int polinomialLimit = countOfDigits(new UnsignedInteger(polinomial));
                return (int) Math.pow(2, polinomialLimit - 1) == getSequenceOfPowers(element, polinomial).length;
            }

        }

        /// <summary>
        /// Визначає, чи є заданий поліном незвідним
        /// </summary>
        /// <param name="polinomial">Задає поліном</param>
        /// <returns>Значення, котре вказує, чи є заданий поліном незвідним. Якщо значення рівне true, то поліном незвідний</returns>
        public static boolean isPolinomialIrreducible(int polinomial) {
            int limit = countOfDigits(new UnsignedInteger(polinomial));
            if ((polinomial & 1) == 0) {
                return false;
            } else {
                int isIEven = 0;
                for (int y = (int) Math.pow(2, limit - 1); y > 0; y /= 2) {
                    if ((polinomial & y) == y) {
                        isIEven++;
                    }
                }
                if (isIEven % 2 == 0) {
                    return false;
                }
            }
            for (int first = 2; first < polinomial; first++) {
                int lengthFirst = countOfDigits(new UnsignedInteger(first));

                for (int second = (int) Math.pow(2, limit - lengthFirst);
                        second <= (int) Math.pow(2, limit - lengthFirst + 1) - 1; second++) {
                    int multiplication = 0;

                    for (int summ = 1, offset = 0; summ <= second; summ *= 2, offset++) {
                        if ((second & summ) == summ) {
                            multiplication = (multiplication ^ (first << offset));
                        }
                    }

                    if (multiplication == polinomial) {
                        return false;
                    }
                }
            }
            return true;
        }

        /// <summary>
        /// Визначає, чи є незвідним поліном примітивним
        /// </summary>
        /// <param name="polinomial">Поліном у десятковому вигляді</param>
        /// <returns>Значення, яке показує, чи є поліном примітивним. Якщо значення рівне true, то поліном примітивний</returns>
        /// <exception cref="System.IllegalArgumentException">Введений поліном не є незвідним</exception>
        public static boolean isPolinomialPrimitive(int polinomial) {
            if (!isPolinomialIrreducible(polinomial)) {
                throw new IllegalArgumentException("Заданий поліном не є незвідним");
            } else {
                int limit = countOfDigits(new UnsignedInteger(polinomial)), element = 1;
                for (int index = 0;; index++) {
                    element = element << 1;
                    if ((element & (int) Math.pow(2, limit - 1)) == (int) Math.pow(2, limit - 1)) {
                        element = (element ^ polinomial);
                    }
                    if (element == 1) {
                        return index == (int) Math.pow(2, limit - 1) - 2;
                    }
                }
            }
        }

        /// <summary>
        /// Знаходить добуток елементів таблиці на матрицю Галуа
        /// </summary>
        /// <param name="table">Таблиця, елементами якої є двійкові числа, котрі слід помножити на матрицю Галуа</param>
        /// <param name="matrix">Матриця Галуа</param>
        /// <returns>Таблицю, елементами якої є добуток елементів вихідної таблиці на матрицю Галуа. Числа предсатвлені у двійковій формі</returns>
        public static String[][] productTableMatrix(String[][] table, String[] matrix) {
            List<String> temper = new ArrayList<>();
            String[][] result = new String[(int) Math.sqrt(table.length)][(int) Math.sqrt(table.length)];
            for (int i = 0; i < (int) Math.sqrt(table.length); i++) {
                for (int j = 0; j < (int) Math.sqrt(table.length); j++) {
                    int temp = 0;
                    for (int prod = 0; prod < matrix.length; prod++) {
                        if (table[i][j].charAt(prod) == '1') {
                            temp = temp ^ fromBinToDec(matrix[prod]);
                        } else {
                        }
                    }
                    temper.add(fromDecToBin(new UnsignedInteger(temp)));
                }
            }
            addZeros(temper);
            for (int i = 0; i < (int) Math.sqrt(table.length); i++) {
                for (int j = 0; j < (int) Math.sqrt(table.length); j++) {
                    result[i][j] = temper.get(i * (int) Math.sqrt(result.length) + j);
                }
            }
            return result;
        }

    }

}

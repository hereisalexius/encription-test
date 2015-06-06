/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafiles;

/**
 *
 * @author hereisalexius
 */
public class Substitution {

    private final Polinomial polinomial;
    private final Matrix matrix;
    private final Additive additive;

    public Substitution(Polinomial polinomial, Matrix matrix, Additive additive) {
        this.polinomial = polinomial;
        this.matrix = matrix;
        this.additive = additive;
    }

    public Polinomial getPolinomial() {
        return polinomial;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public Additive getAdditive() {
        return additive;
    }

    public static class Polinomial {

        private final String power; //text-0
        private final String fi;  //text-1
        private final String omega; //text-2 

        public Polinomial(String power, String fi, String omega) {
            this.power = power;
            this.fi = fi;
            this.omega = omega;
        }

        public String getPower() {
            return power;
        }

        public String getFi() {
            return fi;
        }

        public String getOmega() {
            return omega;
        }

    }

    public static class Matrix extends Polinomial {

        public Matrix(String power, String fi, String omega) {
            super(power, fi, omega);
        }

    }

    public static class Additive {

        private final String alpha;
        private final String beta;

        public Additive(String alpha, String beta) {
            this.alpha = alpha;
            this.beta = beta;
        }

        public String getAlpha() {
            return alpha;
        }

        public String getBeta() {
            return beta;
        }
    }

}

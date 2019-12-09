package generator;

public final class Gauss {

    private Gauss() {
    }

    public strictfp static double[] method(double[][] A, double[] B, double[] X) {
        double koef;
        for (int i = 0; i <= B.length - 2; i++) {
            for (int j = i + 1; j <= B.length - 1; j++) {
                koef = -1 * (A[j][i] / A[i][i]);
                for (int k = i; k <= B.length - 1; k++) {
                    A[j][k] += A[i][k] * koef;
                }
                B[j] += B[i] * koef;
            }
        }
        X[B.length - 1] = B[B.length - 1] / A[B.length - 1][B.length - 1];
        for (int j = B.length - 2; j >= 0; j--) {
            double sum = 0;
            for (int k = B.length - 1; k >= j; k--) {
                sum += A[j][k] * X[k];
            }
            X[j] = (B[j] - sum) / A[j][j];
        }
        return X;
    }

    public static double[] jacobi(double[][] A, double[] B, double[] X) {
        double eps = 0.001;
        int N = A.length;
        double[] tempX = new double[N];
        double norm;
        do {
            for (int i = 0; i < N; i++) {
                tempX[i] = B[i];
                for (int g = 0; g < N; g++) {
                    if (i != g) {
                        tempX[i] -= A[i][g] * X[g];
                    }
                }
                tempX[i] /= A[i][i];
            }
            norm = Math.abs(X[0] - tempX[0]);
            for (int h = 0; h < N; h++) {
                if (Math.abs(X[h] - tempX[h]) > norm) {
                    norm = Math.abs(X[h] - tempX[h]);
                }
                X[h] = tempX[h];
            }
        } while (norm > eps);
        return X;
    }

}

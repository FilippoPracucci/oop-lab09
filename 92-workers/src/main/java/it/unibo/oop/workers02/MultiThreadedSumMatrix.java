package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the calculation.
 * 
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    /**
     * 
     * @param nthread
     *             no. of thread performing the sum.
     */
    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startcolumn;
        private final int ncolumn;
        private double res;

        /**
         * Build a new worker.
         *
         * @param matrix
         *          the matrix to sum
         * @param startcolumn
         *          the initial column for this worker
         * @param ncolumn
         *          the no. of columns to sum up for this worker
         */
        Worker(final double[][] matrix, final int startcolumn, final int ncolumn) {
            super();
            this.matrix = matrix.clone();
            this.startcolumn = startcolumn;
            this.ncolumn = ncolumn;
        }

        /**
         * Convert matrix to list.
         * 
         * @return the list of double converted from the matrix
         */
        private List<Double> toList() {
            /*
             * Build a list of double
             */
            final List<Double> matrixlist = new ArrayList<>();
            for (int i = this.startcolumn; i < (this.startcolumn + this.ncolumn - 1); i++)  {
                for (int j = 0; j < this.matrix.length; j++) {
                    matrixlist.add(this.matrix[i][j]);
                }
            }
            return matrixlist;
        }

        @Override
        public void run() {
            final List<Double> matrixlist = toList();
            System.out.println(//NOPMD: println allowed for the exercise
                "Working from column  " + this.startcolumn + "to column " + (this.startcolumn + this.ncolumn - 1)
            ); 
            for (final Double elem: matrixlist) {
                this.res += elem;
            }
        }

        /**
         * Returns the result of summing up a part of the matrix, after converted it into a list of double.
         * 
         * @return the sum of every element of the list
         */
        public double getResult() {
            return this.res;
        }
    }

    /**
     * @param matrix is the matrix to sum
     * 
     * @return the sum
     */
    @Override
    public double sum(final double[][] matrix) {
        final int size = getSize(matrix) % this.nthread + getSize(matrix) / this.nthread;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(this.nthread);
        for (int start = 0; start < getSize(matrix); start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        /*
         * Start them
         */
        for (final Worker w: workers) {
            w.start();
        }

        double sum = 0.0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        return sum;
    }

    /**
     * 
     * @param matrix the matrix to get the size
     * 
     * @return the size of the matrix
     */
    private int getSize(final double[][] matrix) {
        /*
         * Assume that the matrix is square
         */
        return matrix.length * matrix[0].length;
    }
}

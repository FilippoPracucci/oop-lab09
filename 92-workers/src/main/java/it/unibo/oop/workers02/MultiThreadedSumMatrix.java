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

    private static final class Worker extends Thread {
        private final double[][] matrix;
        private final int startrow;
        private final int nrow;
        private double res;

        /**
         * Build a new worker.
         *
         * @param matrix
         *          the matrix to sum
         * @param startrow
         *          the initial row for this worker
         * @param nrow
         *          the no. of rows to sum up for this worker
         */
        private Worker(final double[][] matrix, final int startrow, final int nrow) {
            super();
            this.matrix = matrix;
            this.startrow = startrow;
            this.nrow = nrow;
        }

        @Override
        public void run() {
            System.out.println(//NOPMD: println allowed for the exercise
                "Working from column  " + this.startrow + " to column " + (this.startrow + this.nrow - 1)
            );
            for (int i = this.startrow; i < this.matrix.length && i < (this.startrow + this.nrow); i++) {
                for (final double num: this.matrix[i]) {
                    this.res += num;
                }
            }
        }

        /**
         * Returns the result of summing up a part of the matrix.
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
        final int size = getRows(matrix) % this.nthread + getRows(matrix) / this.nthread;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(this.nthread);
        for (int start = 0; start < getRows(matrix); start += size) {
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
     * @return the number of rows of the matrix
     */
    private static int getRows(final double[][] matrix) {
        return matrix.length;
    }
}

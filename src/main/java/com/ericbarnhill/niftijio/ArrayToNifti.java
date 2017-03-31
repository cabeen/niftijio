package com.ericbarnhill.niftijio;

import com.ericbarnhill.arrayMath.ArrayMath;

public class ArrayToNifti {

    public static  NiftiVolume ArrayToNifti(double[][] f) {
        final int fi = f.length;
        final int fj = f[0].length;
        double[][][][] g = ArrayMath.convertTo4d(f);
        NiftiVolume v = new NiftiVolume(fi, fj, 1, 1);
        v.data = new FourDimensionalArray(g);
        return v;
    }

    public static  NiftiVolume ArrayToNifti(double[][][] f) {
        final int fi = f.length;
        final int fj = f[0].length;
        final int fk = f[0][0].length;
        double[][][][] g = ArrayMath.convertTo4d(f);
        NiftiVolume v = new NiftiVolume(fi, fj, fk, 1);
        v.data = new FourDimensionalArray(g);
        return v;
    }

    public static  NiftiVolume ArrayToNifti(double[][][][] f) {
        final int fi = f.length;
        final int fj = f[0].length;
        final int fk = f[0][0].length;
        final int fl = f[0][0][0].length;
        NiftiVolume v = new NiftiVolume(fi, fj, fk, 1);
        v.data = new FourDimensionalArray(f);
        return v;
    }

}

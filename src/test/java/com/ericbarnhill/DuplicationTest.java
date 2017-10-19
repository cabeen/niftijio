package com.ericbarnhill.niftijio;
import org.apache.commons.io.FilenameUtils;
import java.io.IOException;
import java.util.Arrays;

public class DuplicationTest {

    public static void main(String[] args) {
        try {
            NiftiVolume v = NiftiVolume.read(args[0]);
            int[] dims = getDims(v);
            System.out.println(Arrays.toString(dims));
            String filename = FilenameUtils.removeExtension(args[0]);
            String extension = FilenameUtils.getExtension(args[0]);
            String newFilename = filename + "_duplicate" + extension;
            v.write(newFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[] getDims(NiftiVolume v) {
        int[] dims = new int[4];
        dims[0] = v.header.dim[0];
        dims[1] = v.header.dim[1];
        dims[2] = v.header.dim[2];
        dims[3] = v.header.dim[3];
        return dims; 
    }
}
        

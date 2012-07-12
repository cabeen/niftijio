package niftijio;

import java.io.IOException;
import java.io.PrintWriter;

public class Example
{
    public static void main(String[] args)
    {
        try
        {
            if (args.length == 0 || args.length > 2)
            {
                System.out.println("Usage: niftijio input.nii.gz [output]");
                System.out.println("Description: read a volume and optionally write it out again");
                return;
            }

            NiftiVolume volume = NiftiVolume.read(args[0]);

            int nx = volume.header.dim[1];
            int ny = volume.header.dim[2];
            int nz = volume.header.dim[3];
            int dim = volume.header.dim[4];

            if (dim == 0)
                dim = 1;

            if (args.length == 1)
            {
                System.out.println("dims: " + nx + " " + ny + " " + nz + " " + dim);
                System.out.println("datatype: " + NiftiHeader.decodeDatatype(volume.header.datatype));
            }
            else if (args[1].endsWith("txt"))
            {
                PrintWriter out = new PrintWriter(args[1]);

                out.println("volume ");
                out.println("dimensions:");
                out.println(nx + " " + ny + " " + nz + " " + dim);
                out.println("data:");
                for (int d = 0; d < dim; d++)
                    for (int k = 0; k < nz; k++)
                        for (int j = 0; j < ny; j++)
                            for (int i = 0; i < nx; i++)
                                out.println(volume.data[i][j][k][d]);

                out.println();
                out.close();
            }
            else
            {
                volume.write(args[1]);
            }
        }
        catch (IOException e)
        {
            System.err.println("error: " + e.getMessage());
        }

    }
}

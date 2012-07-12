package niftijio;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class NiftiHeader
{
    /* derived from http://niftilib.sourceforge.net/ */
    public static final int HEADER_SIZE = 348;
    public static final int EXT_KEY_SIZE = 8;
    public static final String MAGIC_STRING = "n+1";

    public static final short INTENT_NONE = 0;
    public static final short INTENT_CORREL = 2;
    public static final short INTENT_TTEST = 3;
    public static final short INTENT_FTEST = 4;
    public static final short INTENT_ZSCORE = 5;
    public static final short INTENT_CHISQ = 6;
    public static final short INTENT_BETA = 7;
    public static final short INTENT_BINOM = 8;
    public static final short INTENT_GAMMA = 9;
    public static final short INTENT_POISSON = 10;
    public static final short INTENT_NORMAL = 11;
    public static final short INTENT_FTEST_NONC = 12;
    public static final short INTENT_CHISQ_NONC = 13;
    public static final short INTENT_LOGISTIC = 14;
    public static final short INTENT_LAPLACE = 15;
    public static final short INTENT_UNIFORM = 16;
    public static final short INTENT_TTEST_NONC = 17;
    public static final short INTENT_WEIBULL = 18;
    public static final short INTENT_CHI = 19;
    public static final short INTENT_INVGAUSS = 20;
    public static final short INTENT_EXTVAL = 21;
    public static final short INTENT_PVAL = 22;
    public static final short INTENT_ESTIMATE = 1001;
    public static final short INTENT_LABEL = 1002;
    public static final short INTENT_NEURONAME = 1003;
    public static final short INTENT_GENMATRIX = 1004;
    public static final short INTENT_SYMMATRIX = 1005;
    public static final short INTENT_DISPVECT = 1006;
    public static final short INTENT_List = 1007;
    public static final short INTENT_POINTSET = 1008;
    public static final short INTENT_TRIANGLE = 1009;
    public static final short INTENT_QUATERNION = 1010;

    public static final short DT_NONE = 0;
    public static final short DT_BINARY = 1;
    public static final short TYPE_UINT8 = 2;
    public static final short TYPE_INT16 = 4;
    public static final short TYPE_INT32 = 8;
    public static final short TYPE_FLOAT32 = 16;
    public static final short TYPE_COMPLEX64 = 32;
    public static final short TYPE_FLOAT64 = 64;
    public static final short TYPE_RGB24 = 128;
    public static final short DT_ALL = 255;
    public static final short TYPE_INT8 = 256;
    public static final short TYPE_UINT16 = 512;
    public static final short TYPE_UINT32 = 768;
    public static final short TYPE_INT64 = 1024;
    public static final short TYPE_UINT64 = 1280;
    public static final short TYPE_FLOAT128 = 1536;
    public static final short TYPE_COMPLEX128 = 1792;
    public static final short TYPE_COMPLEX256 = 2048;

    public static final short UNITS_UNKNOWN = 0;
    public static final short UNITS_METER = 1;
    public static final short UNITS_MM = 2;
    public static final short UNITS_MICRON = 3;
    public static final short UNITS_SEC = 8;
    public static final short UNITS_MSEC = 16;
    public static final short UNITS_USEC = 24;
    public static final short UNITS_HZ = 32;
    public static final short UNITS_PPM = 40;

    public static final short SLICE_SEQ_INC = 1;
    public static final short SLICE_SEQ_DEC = 2;
    public static final short SLICE_ALT_INC = 3;
    public static final short SLICE_ALT_DEC = 4;

    public static final short XFORM_UNKNOWN = 0;
    public static final short XFORM_SCANNER_ANAT = 1;
    public static final short XFORM_ALIGNED_ANAT = 2;
    public static final short XFORM_TALAIRACH = 3;
    public static final short XFORM_MNI_152 = 4;

    public String filename;
    public boolean little_endian;
    public short freq_dim, phase_dim, slice_dim;
    public short xyz_unit_code, t_unit_code;
    public short qfac;
    public List<int[]> extensions_list;
    public List<byte[]> extension_blobs;
    
    public int sizeof_hdr;
    public StringBuffer data_type_string;
    public StringBuffer db_name;
    public int extents;
    public short session_error;
    public StringBuffer regular;
    public StringBuffer dim_info;
    public short dim[];
    public float intent[];
    public short intent_code;
    public short datatype;
    public short bitpix;
    public short slice_start;
    public float pixdim[];
    public float vox_offset;
    public float scl_slope;
    public float scl_inter;
    public short slice_end;
    public byte slice_code;
    public byte xyzt_units;
    public float cal_max;
    public float cal_min;
    public float slice_duration;
    public float toffset;
    public int glmax;
    public int glmin;
    public StringBuffer descrip;
    public StringBuffer aux_file;
    public short qform_code;
    public short sform_code;
    public float quatern[];
    public float qoffset[];
    public float srow_x[];
    public float srow_y[];
    public float srow_z[];
    public StringBuffer intent_name;
    public StringBuffer magic;
    public byte extension[];

    public NiftiHeader()
    {
        setDefaults();
        return;
    }

    public NiftiHeader(int nx, int ny, int nz, int dim)
    {
        this.filename = "";
        this.pixdim[0] = 1.0f;
        this.pixdim[1] = 1.0f;
        this.pixdim[2] = 1.0f;
        this.srow_x[0] = 1.0f;
        this.srow_y[1] = 1.0f;
        this.srow_z[2] = 1.0f;
        this.descrip = new StringBuffer("Created: " + new Date().toString());
        this.setDatatype(TYPE_FLOAT32);
        this.dim[0] = (short) (dim > 1 ? 4 : 3);
        this.dim[1] = (short) nx;
        this.dim[2] = (short) ny;
        this.dim[3] = (short) nz;
        this.dim[4] = (short) (dim > 1 ? dim : 0);
    }

    public void setDatatype(short code)
    {
        datatype = code;
        bitpix = (short) (bytesPerVoxel(code) * 8);
        return;
    }

    public String decodeIntent(short icode)
    {
        switch (icode)
        {
        case NiftiHeader.INTENT_NONE:
            return ("INTENT_NONE");
        case NiftiHeader.INTENT_CORREL:
            return ("INTENT_CORREL");
        case NiftiHeader.INTENT_TTEST:
            return ("INTENT_TTEST");
        case NiftiHeader.INTENT_FTEST:
            return ("INTENT_FTEST");
        case NiftiHeader.INTENT_ZSCORE:
            return ("INTENT_ZSCORE");
        case NiftiHeader.INTENT_CHISQ:
            return ("INTENT_CHISQ");
        case NiftiHeader.INTENT_BETA:
            return ("INTENT_BETA");
        case NiftiHeader.INTENT_BINOM:
            return ("INTENT_BINOM");
        case NiftiHeader.INTENT_GAMMA:
            return ("INTENT_GAMMA");
        case NiftiHeader.INTENT_POISSON:
            return ("INTENT_POISSON");
        case NiftiHeader.INTENT_NORMAL:
            return ("INTENT_NORMAL");
        case NiftiHeader.INTENT_FTEST_NONC:
            return ("INTENT_FTEST_NONC");
        case NiftiHeader.INTENT_CHISQ_NONC:
            return ("INTENT_CHISQ_NONC");
        case NiftiHeader.INTENT_LOGISTIC:
            return ("INTENT_LOGISTIC");
        case NiftiHeader.INTENT_LAPLACE:
            return ("INTENT_LAPLACE");
        case NiftiHeader.INTENT_UNIFORM:
            return ("INTENT_UNIFORM");
        case NiftiHeader.INTENT_TTEST_NONC:
            return ("INTENT_TTEST_NONC");
        case NiftiHeader.INTENT_WEIBULL:
            return ("INTENT_WEIBULL");
        case NiftiHeader.INTENT_CHI:
            return ("INTENT_CHI");
        case NiftiHeader.INTENT_INVGAUSS:
            return ("INTENT_INVGAUSS");
        case NiftiHeader.INTENT_EXTVAL:
            return ("INTENT_EXTVAL");
        case NiftiHeader.INTENT_PVAL:
            return ("INTENT_PVAL");
        case NiftiHeader.INTENT_ESTIMATE:
            return ("INTENT_ESTIMATE");
        case NiftiHeader.INTENT_LABEL:
            return ("INTENT_LABEL");
        case NiftiHeader.INTENT_NEURONAME:
            return ("INTENT_NEURONAME");
        case NiftiHeader.INTENT_GENMATRIX:
            return ("INTENT_GENMATRIX");
        case NiftiHeader.INTENT_SYMMATRIX:
            return ("INTENT_SYMMATRIX");
        case NiftiHeader.INTENT_DISPVECT:
            return ("INTENT_DISPVECT");
        case NiftiHeader.INTENT_List:
            return ("INTENT_List");
        case NiftiHeader.INTENT_POINTSET:
            return ("INTENT_POINTSET");
        case NiftiHeader.INTENT_TRIANGLE:
            return ("INTENT_TRIANGLE");
        case NiftiHeader.INTENT_QUATERNION:
            return ("INTENT_QUATERNION");
        default:
            return ("INVALID_INTENT_CODE");
        }
    }

    public static String decodeDatatype(short dcode)
    {
        switch (dcode)
        {
        case DT_NONE:
            return ("DT_NONE");
        case DT_BINARY:
            return ("DT_BINARY");
        case NiftiHeader.TYPE_UINT8:
            return ("TYPE_UINT8");
        case NiftiHeader.TYPE_INT16:
            return ("TYPE_INT16");
        case NiftiHeader.TYPE_INT32:
            return ("TYPE_INT32");
        case NiftiHeader.TYPE_FLOAT32:
            return ("TYPE_FLOAT32");
        case NiftiHeader.TYPE_COMPLEX64:
            return ("TYPE_COMPLEX64");
        case NiftiHeader.TYPE_FLOAT64:
            return ("TYPE_FLOAT64");
        case NiftiHeader.TYPE_RGB24:
            return ("TYPE_RGB24");
        case DT_ALL:
            return ("DT_ALL");
        case NiftiHeader.TYPE_INT8:
            return ("TYPE_INT8");
        case NiftiHeader.TYPE_UINT16:
            return ("TYPE_UINT16");
        case NiftiHeader.TYPE_UINT32:
            return ("TYPE_UINT32");
        case NiftiHeader.TYPE_INT64:
            return ("TYPE_INT64");
        case NiftiHeader.TYPE_UINT64:
            return ("TYPE_UINT64");
        case NiftiHeader.TYPE_FLOAT128:
            return ("TYPE_FLOAT128");
        case NiftiHeader.TYPE_COMPLEX128:
            return ("TYPE_COMPLEX128");
        case NiftiHeader.TYPE_COMPLEX256:
            return ("TYPE_COMPLEX256");
        default:
            return ("INVALID_DATATYPE_CODE");
        }
    }

    public static short bytesPerVoxel(short dcode)
    {
        switch (dcode)
        {
        case DT_NONE:
            return (0);
        case DT_BINARY:
            return (-1);
        case NiftiHeader.TYPE_UINT8:
            return (1);
        case NiftiHeader.TYPE_INT16:
            return (2);
        case NiftiHeader.TYPE_INT32:
            return (4);
        case NiftiHeader.TYPE_FLOAT32:
            return (4);
        case NiftiHeader.TYPE_COMPLEX64:
            return (8);
        case NiftiHeader.TYPE_FLOAT64:
            return (8);
        case NiftiHeader.TYPE_RGB24:
            return (3);
        case DT_ALL:
            return (0);
        case NiftiHeader.TYPE_INT8:
            return (1);
        case NiftiHeader.TYPE_UINT16:
            return (2);
        case NiftiHeader.TYPE_UINT32:
            return (4);
        case NiftiHeader.TYPE_INT64:
            return (8);
        case NiftiHeader.TYPE_UINT64:
            return (8);
        case NiftiHeader.TYPE_FLOAT128:
            return (16);
        case NiftiHeader.TYPE_COMPLEX128:
            return (16);
        case NiftiHeader.TYPE_COMPLEX256:
            return (32);
        default:
            return (0);
        }
    }

    public String decodeSliceOrder(short code)
    {
        switch (code)
        {
        case NiftiHeader.SLICE_SEQ_INC:
            return ("SLICE_SEQ_INC");
        case NiftiHeader.SLICE_SEQ_DEC:
            return ("SLICE_SEQ_DEC");
        case NiftiHeader.SLICE_ALT_INC:
            return ("SLICE_ALT_INC");
        case NiftiHeader.SLICE_ALT_DEC:
            return ("SLICE_ALT_DEC");
        default:
            return ("INVALID_SLICE_SEQ_CODE");
        }
    }

    public String decodeXform(short code)
    {
        switch (code)
        {
        case NiftiHeader.XFORM_UNKNOWN:
            return ("XFORM_UNKNOWN");
        case NiftiHeader.XFORM_SCANNER_ANAT:
            return ("XFORM_SCANNER_ANAT");
        case NiftiHeader.XFORM_ALIGNED_ANAT:
            return ("XFORM_ALIGNED_ANAT");
        case NiftiHeader.XFORM_TALAIRACH:
            return ("XFORM_TALAIRACH");
        case NiftiHeader.XFORM_MNI_152:
            return ("XFORM_MNI_152");
        default:
            return ("INVALID_XFORM_CODE");
        }
    }

    public String decodeUnits(short code)
    {
        switch (code)
        {
        case NiftiHeader.UNITS_UNKNOWN:
            return ("UNITS_UNKNOWN");
        case NiftiHeader.UNITS_METER:
            return ("UNITS_METER");
        case NiftiHeader.UNITS_MM:
            return ("UNITS_MM");
        case NiftiHeader.UNITS_MICRON:
            return ("UNITS_MICRON");
        case NiftiHeader.UNITS_SEC:
            return ("UNITS_SEC");
        case NiftiHeader.UNITS_MSEC:
            return ("UNITS_MSEC");
        case NiftiHeader.UNITS_USEC:
            return ("UNITS_USEC");
        case NiftiHeader.UNITS_HZ:
            return ("UNITS_HZ");
        case NiftiHeader.UNITS_PPM:
            return ("UNITS_PPM");
        default:
            return ("INVALID_UNITS_CODE");
        }
    }

    private void setDefaults()
    {
        little_endian = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
        sizeof_hdr = HEADER_SIZE;
        data_type_string = new StringBuffer();
        for (int i = 0; i < 10; i++)
            data_type_string.append("\0");
        db_name = new StringBuffer();
        for (int i = 0; i < 18; i++)
            db_name.append("\0");
        extents = 0;
        session_error = 0;
        regular = new StringBuffer("\0");
        dim_info = new StringBuffer("\0");
        freq_dim = 0;
        phase_dim = 0;
        slice_dim = 0;
        dim = new short[8];
        for (int i = 0; i < 8; i++)
            dim[i] = 0;
        dim[1] = 0;
        dim[2] = 0;
        dim[3] = 0;
        dim[4] = 0;
        intent = new float[3];
        for (int i = 0; i < 3; i++)
            intent[i] = (float) 0.0;
        intent_code = NiftiHeader.INTENT_NONE;
        datatype = DT_NONE;
        bitpix = 0;
        slice_start = 0;
        pixdim = new float[8];
        pixdim[0] = 1;
        qfac = 1;
        for (int i = 1; i < 8; i++)
            pixdim[i] = (float) 0.0;

        vox_offset = (float) 352;
        scl_slope = (float) 0.0;
        scl_inter = (float) 0.0;
        slice_end = 0;
        slice_code = (byte) 0;
        xyzt_units = (byte) 0;
        xyz_unit_code = NiftiHeader.UNITS_UNKNOWN;
        t_unit_code = NiftiHeader.UNITS_UNKNOWN;

        cal_max = (float) 0.0;
        cal_min = (float) 0.0;
        slice_duration = (float) 0.0;
        toffset = (float) 0.0;
        glmax = 0;
        glmin = 0;
        
        descrip = new StringBuffer();
        
        for (int i = 0; i < 80; i++)
            descrip.append("\0");
        aux_file = new StringBuffer();
        for (int i = 0; i < 24; i++)
            aux_file.append("\0");

        qform_code = NiftiHeader.XFORM_UNKNOWN;
        sform_code = NiftiHeader.XFORM_UNKNOWN;

        quatern = new float[3];
        qoffset = new float[3];
        for (int i = 0; i < 3; i++)
        {
            quatern[i] = (float) 0.0;
            qoffset[i] = (float) 0.0;
        }

        srow_x = new float[4];
        srow_y = new float[4];
        srow_z = new float[4];
        for (int i = 0; i < 4; i++)
        {
            srow_x[i] = (float) 0.0;
            srow_y[i] = (float) 0.0;
            srow_z[i] = (float) 0.0;
        }

        intent_name = new StringBuffer();
        
        for (int i = 0; i < 16; i++)
            intent_name.append("\0");

        magic = new StringBuffer(MAGIC_STRING);
        extension = new byte[4];
        for (int i = 0; i < 4; i++)
            extension[i] = (byte) 0;

        extensions_list = new ArrayList<int[]>(5);
        extension_blobs = new ArrayList<byte[]>(5);

        return;
    }

    public Map<String, Object> info()
    {
        Map<String, Object> info = new HashMap<String, Object>();

        info.put("size", String.valueOf(sizeof_hdr));
        info.put("data_offset", String.valueOf(vox_offset));
        info.put("magic_string", String.valueOf(magic));
        info.put("datatype_code", String.valueOf(datatype));
        info.put("datatype_name", decodeDatatype(datatype));
        info.put("bit_per_vox", String.valueOf(bitpix));
        info.put("scaling_offset", String.valueOf(scl_inter));
        info.put("scaling_slope", String.valueOf(scl_slope));

        for (int i = 0; i <= dim[0]; i++)
            info.put("dim" + i, String.valueOf(dim[i]));

        for (int i = 0; i <= dim[0]; i++)
            info.put("space" + i, String.valueOf(pixdim[i]));

        info.put("xyz_units_code", String.valueOf(xyz_unit_code));
        info.put("xyz_units_name", decodeUnits(xyz_unit_code));
        info.put("t_units_code", String.valueOf(t_unit_code));
        info.put("t_units_name", decodeUnits(t_unit_code));
        info.put("t_offset", String.valueOf(toffset));

        for (int i = 0; i < 3; i++)
            info.put("intent" + i, String.valueOf(intent[i]));

        info.put("intent_code", String.valueOf(intent_code));
        info.put("intent_name", decodeIntent(intent_code));
        info.put("cal_max", String.valueOf(cal_max));
        info.put("cal_min", String.valueOf(cal_min));

        info.put("slice_code", String.valueOf(slice_code));
        info.put("slice_name", decodeSliceOrder((short) slice_code));
        info.put("slice_freq", String.valueOf(freq_dim));
        info.put("slice_phase", String.valueOf(phase_dim));
        info.put("slice_index", String.valueOf(slice_dim));
        info.put("slice_start", String.valueOf(slice_start));
        info.put("slice_end", String.valueOf(slice_end));
        info.put("slice_dur", String.valueOf(slice_duration));
        info.put("qfac", String.valueOf(qfac));
        info.put("qform_code", String.valueOf(qform_code));
        info.put("qform_name", decodeXform(qform_code));
        info.put("quat_b", String.valueOf(quatern[0]));
        info.put("quat_c", String.valueOf(quatern[1]));
        info.put("quat_d", String.valueOf(quatern[2]));
        info.put("quat_x", String.valueOf(qoffset[0]));
        info.put("quat_y", String.valueOf(qoffset[1]));
        info.put("quat_z", String.valueOf(qoffset[2]));

        info.put("sform_code", String.valueOf(sform_code));
        info.put("sform_name", decodeXform(sform_code));
        for (int i = 0; i < 4; i++)
            info.put("sform0" + i, String.valueOf(srow_x[i]));
        for (int i = 0; i < 4; i++)
            info.put("sform1" + i, String.valueOf(srow_y[i]));
        for (int i = 0; i < 4; i++)
            info.put("sform2" + i, String.valueOf(srow_z[i]));

        return info;
    }

    private static byte[] setStringSize(StringBuffer s, int n)
    {
        byte b[];
        int slen;

        slen = s.length();

        if (slen >= n)
            return (s.toString().substring(0, n).getBytes());

        b = new byte[n];
        for (int i = 0; i < slen; i++)
            b[i] = (byte) s.charAt(i);
        for (int i = slen; i < n; i++)
            b[i] = 0;

        return (b);
    }

    private static boolean littleEndian(String fn) throws IOException
    {
        InputStream is = new FileInputStream(fn);
        if (fn.endsWith(".gz"))
            is = new GZIPInputStream(is);
        DataInput di = new DataInputStream(is);

        di.skipBytes(40);
        short s = di.readShort();

        return (s < 1) || (s > 7);
    }

    public static NiftiHeader read(String fn) throws IOException
    {
        DataInput di;

        boolean le = littleEndian(fn);

        InputStream is = new FileInputStream(fn);
        if (fn.endsWith(".gz"))
            is = new GZIPInputStream(is);

        if (le)
            di = new LEDataInputStream(is);
        else
            di = new DataInputStream(is);

        NiftiHeader ds = new NiftiHeader();

        ds.filename = fn;
        ds.little_endian = le;
        ds.sizeof_hdr = di.readInt();

        byte[] bb = new byte[10];
        di.readFully(bb, 0, 10);
        ds.data_type_string = new StringBuffer(new String(bb));

        bb = new byte[18];
        di.readFully(bb, 0, 18);
        ds.db_name = new StringBuffer(new String(bb));
        ds.extents = di.readInt();
        ds.session_error = di.readShort();
        ds.regular = new StringBuffer();
        ds.regular.append((char) (di.readUnsignedByte()));
        ds.dim_info = new StringBuffer();
        ds.dim_info.append((char) (di.readUnsignedByte()));

        int fps_dim = (int) ds.dim_info.charAt(0);
        ds.freq_dim = (short) (fps_dim & 3);
        ds.phase_dim = (short) ((fps_dim >>> 2) & 3);
        ds.slice_dim = (short) ((fps_dim >>> 4) & 3);

        for (int i = 0; i < 8; i++)
            ds.dim[i] = di.readShort();
        if (ds.dim[0] > 0)
            ds.dim[1] = ds.dim[1];
        if (ds.dim[0] > 1)
            ds.dim[2] = ds.dim[2];
        if (ds.dim[0] > 2)
            ds.dim[3] = ds.dim[3];
        if (ds.dim[0] > 3)
            ds.dim[4] = ds.dim[4];

        for (int i = 0; i < 3; i++)
            ds.intent[i] = di.readFloat();

        ds.intent_code = di.readShort();
        ds.datatype = di.readShort();
        ds.bitpix = di.readShort();
        ds.slice_start = di.readShort();

        for (int i = 0; i < 8; i++)
            ds.pixdim[i] = di.readFloat();

        ds.qfac = (short) Math.floor((double) (ds.pixdim[0]));
        ds.vox_offset = di.readFloat();
        ds.scl_slope = di.readFloat();
        ds.scl_inter = di.readFloat();
        ds.slice_end = di.readShort();
        ds.slice_code = (byte) di.readUnsignedByte();

        ds.xyzt_units = (byte) di.readUnsignedByte();

        int unit_codes = (int) ds.xyzt_units;
        ds.xyz_unit_code = (short) (unit_codes & 007);
        ds.t_unit_code = (short) (unit_codes & 070);

        ds.cal_max = di.readFloat();
        ds.cal_min = di.readFloat();
        ds.slice_duration = di.readFloat();
        ds.toffset = di.readFloat();
        ds.glmax = di.readInt();
        ds.glmin = di.readInt();

        bb = new byte[80];
        di.readFully(bb, 0, 80);
        ds.descrip = new StringBuffer(new String(bb));

        bb = new byte[24];
        di.readFully(bb, 0, 24);
        ds.aux_file = new StringBuffer(new String(bb));

        ds.qform_code = di.readShort();
        ds.sform_code = di.readShort();

        for (int i = 0; i < 3; i++)
            ds.quatern[i] = di.readFloat();
        for (int i = 0; i < 3; i++)
            ds.qoffset[i] = di.readFloat();

        for (int i = 0; i < 4; i++)
            ds.srow_x[i] = di.readFloat();
        for (int i = 0; i < 4; i++)
            ds.srow_y[i] = di.readFloat();
        for (int i = 0; i < 4; i++)
            ds.srow_z[i] = di.readFloat();

        bb = new byte[16];
        di.readFully(bb, 0, 16);
        ds.intent_name = new StringBuffer(new String(bb));

        bb = new byte[4];
        di.readFully(bb, 0, 4);
        ds.magic = new StringBuffer(new String(bb));
        
        di.readFully(ds.extension, 0, 4);

        if (ds.extension[0] != (byte) 0)
        {
            int start_addr = NiftiHeader.HEADER_SIZE + 4;

            while (start_addr < (int) ds.vox_offset)
            {
                int[] size_code = new int[2];
                size_code[0] = di.readInt();
                size_code[1] = di.readInt();

                int nb = size_code[0] - NiftiHeader.EXT_KEY_SIZE;
                byte[] eblob = new byte[nb];
                di.readFully(eblob, 0, nb);
                ds.extension_blobs.add(eblob);
                ds.extensions_list.add(size_code);
                start_addr += (size_code[0]);

                if (start_addr > (int) ds.vox_offset)
                    throw new IOException("error: overrun in extension " + ds.extensions_list.size());
            }
        }

        return ds;
    }

    public byte[] encodeHeader() throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataOutput dout = (this.little_endian) ? new LEDataOutputStream(os) : new DataOutputStream(os);

        dout.writeInt(this.sizeof_hdr);

        if (this.data_type_string.length() >= 10)
        {
            dout.writeBytes(this.data_type_string.substring(0, 10));
        }
        else
        {
            dout.writeBytes(this.data_type_string.toString());
            for (int i = 0; i < (10 - this.data_type_string.length()); i++)
                dout.writeByte(0);
        }

        if (this.db_name.length() >= 18)
        {
            dout.writeBytes(this.db_name.substring(0, 18));
        }
        else
        {
            dout.writeBytes(this.db_name.toString());
            for (int i = 0; i < (18 - this.db_name.length()); i++)
                dout.writeByte(0);
        }

        dout.writeInt(this.extents);
        dout.writeShort(this.session_error);
        dout.writeByte((int) this.regular.charAt(0));

        int spf_dims = 0;
        spf_dims = (spf_dims & ((int) (this.slice_dim) & 3)) << 2;
        spf_dims = (spf_dims & ((int) (this.phase_dim) & 3)) << 2;
        spf_dims = (spf_dims & ((int) (this.freq_dim) & 3));
        byte b = (byte) spf_dims;
        dout.writeByte((int) b);

        for (int i = 0; i < 8; i++)
            dout.writeShort(this.dim[i]);

        for (int i = 0; i < 3; i++)
            dout.writeFloat(this.intent[i]);

        dout.writeShort(this.intent_code);
        dout.writeShort(this.datatype);
        dout.writeShort(this.bitpix);
        dout.writeShort(this.slice_start);

        for (int i = 0; i < 8; i++)
            dout.writeFloat(this.pixdim[i]);

        dout.writeFloat(this.vox_offset);
        dout.writeFloat(this.scl_slope);
        dout.writeFloat(this.scl_inter);
        dout.writeShort(this.slice_end);
        dout.writeByte((int) this.slice_code);

        int units = ((byte) (((int) (this.xyz_unit_code) & 007) | ((int) (this.t_unit_code) & 070)));
        dout.writeByte(units);
        dout.writeFloat(this.cal_max);
        dout.writeFloat(this.cal_min);
        dout.writeFloat(this.slice_duration);
        dout.writeFloat(this.toffset);
        dout.writeInt(this.glmax);
        dout.writeInt(this.glmin);
        dout.write(NiftiHeader.setStringSize(this.descrip, 80), 0, 80);
        dout.write(NiftiHeader.setStringSize(this.aux_file, 24), 0, 24);
        dout.writeShort(this.qform_code);
        dout.writeShort(this.sform_code);

        for (int i = 0; i < 3; i++)
            dout.writeFloat(this.quatern[i]);
        for (int i = 0; i < 3; i++)
            dout.writeFloat(this.qoffset[i]);
        for (int i = 0; i < 4; i++)
            dout.writeFloat(this.srow_x[i]);
        for (int i = 0; i < 4; i++)
            dout.writeFloat(this.srow_y[i]);
        for (int i = 0; i < 4; i++)
            dout.writeFloat(this.srow_z[i]);

        dout.write(NiftiHeader.setStringSize(this.intent_name, 16), 0, 16);
        dout.write(NiftiHeader.setStringSize(this.magic, 4), 0, 4);

        if (this.extension[0] != 0)
        {
            for (int i = 0; i < 4; i++)
                dout.writeByte((int) this.extension[i]);

            for (int i = 0; i < this.extensions_list.size(); i++)
            {
                int[] size_code = this.extensions_list.get(i);
                dout.writeInt(size_code[0]);
                dout.writeInt(size_code[1]);

                byte[] eblob = this.extension_blobs.get(i);
                dout.write(eblob);
            }
        }

        if (this.little_endian)
            ((LEDataOutputStream) dout).close();
        else
            ((DataOutputStream) dout).close();

        return os.toByteArray();
    }
}
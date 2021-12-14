package com.example.photoplants.utils;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

public class ConvertUtil {
    public static byte[] bitmap2bytearray(Bitmap source){
        int bytes = source.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(bytes);
        source.copyPixelsToBuffer(buf);
        return buf.array();
    }
}

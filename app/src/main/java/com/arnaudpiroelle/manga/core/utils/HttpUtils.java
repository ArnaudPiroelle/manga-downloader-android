package com.arnaudpiroelle.manga.core.utils;

import retrofit.client.Response;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class HttpUtils {
    public static String convertFrom(Response response) {
        BufferedReader reader;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static InputStream readFrom(Response response) {
        try {
            return response.getBody().in();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void writeFile(InputStream inputStream, File pageFile) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(pageFile);

        ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
        WritableByteChannel outputChannel = Channels.newChannel(outputStream);

        fastChannelCopy(inputChannel, outputChannel);

        inputChannel.close();
        outputChannel.close();
    }

    private static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }

        buffer.flip();

        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }
}

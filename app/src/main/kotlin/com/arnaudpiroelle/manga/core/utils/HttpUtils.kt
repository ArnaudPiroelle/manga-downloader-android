package com.arnaudpiroelle.manga.core.utils

import retrofit.client.Response
import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel

object HttpUtils {
    fun convertFrom(response: Response): String {
        val reader: BufferedReader
        val sb = StringBuilder()
        try {
            reader = BufferedReader(InputStreamReader(response.body.`in`()))

            var line: String? = reader.readLine()

            while (line != null) {
                sb.append(line)
                line = reader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return sb.toString()
    }

    fun readFrom(response: Response): InputStream {
        return response.body.`in`()
    }

    @Throws(IOException::class)
    fun writeFile(inputStream: InputStream, pageFile: File) {
        val outputStream = FileOutputStream(pageFile)

        val inputChannel = Channels.newChannel(inputStream)
        val outputChannel = Channels.newChannel(outputStream)

        fastChannelCopy(inputChannel, outputChannel)

        inputChannel.close()
        outputChannel.close()
    }

    @Throws(IOException::class)
    private fun fastChannelCopy(src: ReadableByteChannel, dest: WritableByteChannel) {
        val buffer = ByteBuffer.allocateDirect(16 * 1024)
        while (src.read(buffer) != -1) {
            buffer.flip()
            dest.write(buffer)
            buffer.compact()
        }

        buffer.flip()

        while (buffer.hasRemaining()) {
            dest.write(buffer)
        }
    }
}

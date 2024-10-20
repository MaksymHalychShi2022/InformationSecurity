package com.example.informationsecurity.utils

class MD5 {

    private var a = INIT_A
    private var b = INIT_B
    private var c = INIT_C
    private var d = INIT_D
    private var buffer = ByteArray(64)
    private var bufferLength = 0
    private var totalLength = 0L

    companion object {
        private const val INIT_A = 0x67452301
        private const val INIT_B = -0x10325477
        private const val INIT_C = -0x67452302
        private const val INIT_D = 0x10325476

        private val SHIFT_AMTS = intArrayOf(
            7, 12, 17, 22,
            5, 9, 14, 20,
            4, 11, 16, 23,
            6, 10, 15, 21
        )

        private val TABLE_T = IntArray(64) {
            ((1L shl 32) * Math.abs(Math.sin(it + 1.0))).toLong().toInt()
        }
    }

    fun update(chunk: ByteArray, offset: Int = 0, length: Int = chunk.size) {
        var inputOffset = offset

        totalLength += length.toLong()

        if (bufferLength > 0) {
            val bufferRemaining = 64 - bufferLength
            if (length < bufferRemaining) {
                System.arraycopy(chunk, inputOffset, buffer, bufferLength, length)
                bufferLength += length
                return
            } else {
                System.arraycopy(chunk, inputOffset, buffer, bufferLength, bufferRemaining)
                processChunk(buffer)
                bufferLength = 0
                inputOffset += bufferRemaining
            }
        }

        while (inputOffset + 63 < offset + length) {
            val block = chunk.copyOfRange(inputOffset, inputOffset + 64)
            processChunk(block)
            inputOffset += 64
        }

        if (inputOffset < offset + length) {
            bufferLength = (offset + length) - inputOffset
            System.arraycopy(chunk, inputOffset, buffer, 0, bufferLength)
        }
    }

    fun digest(input: ByteArray, offset: Int = 0, length: Int = input.size): ByteArray {
        reset()
        update(input, offset, length)
        return digest()
    }


    fun digest(): ByteArray {
        val padding = ByteArray(64)
        padding[0] = 0x80.toByte()

        val messageLengthBits = totalLength * 8
        val paddingLength = if (bufferLength < 56) 56 - bufferLength else 120 - bufferLength

        update(padding.copyOfRange(0, paddingLength))
        update(ByteArray(8).apply {
            var len = messageLengthBits
            for (i in 0 until 8) {
                this[i] = (len and 0xFF).toByte()
                len = len ushr 8
            }
        })

        return getResult()
    }


    private fun processChunk(chunk: ByteArray) {
        val x = IntArray(16) {
            val index = it * 4
            ((chunk[index].toInt() and 0xFF) or
                    ((chunk[index + 1].toInt() and 0xFF) shl 8) or
                    ((chunk[index + 2].toInt() and 0xFF) shl 16) or
                    ((chunk[index + 3].toInt() and 0xFF) shl 24))
        }

        var aa = a
        var bb = b
        var cc = c
        var dd = d

        for (j in 0 until 64) {
            val div16 = j / 16
            val f: Int
            val bufferIndex: Int
            when (div16) {
                0 -> {
                    f = (bb and cc) or (bb.inv() and dd)
                    bufferIndex = j
                }

                1 -> {
                    f = (bb and dd) or (cc and dd.inv())
                    bufferIndex = (5 * j + 1) % 16
                }

                2 -> {
                    f = bb xor cc xor dd
                    bufferIndex = (3 * j + 5) % 16
                }

                else -> {
                    f = cc xor (bb or dd.inv())
                    bufferIndex = (7 * j) % 16
                }
            }
            val temp = bb + Integer.rotateLeft(
                aa + f + x[bufferIndex] + TABLE_T[j],
                SHIFT_AMTS[(div16 * 4) + (j % 4)]
            )
            aa = dd
            dd = cc
            cc = bb
            bb = temp
        }

        a += aa
        b += bb
        c += cc
        d += dd
    }

    private fun getResult(): ByteArray {
        val result = ByteArray(16)
        var count = 0
        for (i in listOf(a, b, c, d)) {
            for (j in 0 until 4) {
                result[count++] = (i ushr (j * 8)).toByte()
            }
        }
        return result
    }

    private fun reset() {
        a = INIT_A
        b = INIT_B
        c = INIT_C
        d = INIT_D
        buffer = ByteArray(64)
        bufferLength = 0
        totalLength = 0L
    }
}

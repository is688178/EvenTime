package com.example.eventime.activities.utils

import android.graphics.Bitmap
import com.parse.ParseFile
import java.io.ByteArrayOutputStream

class ParseFileConvert {
    companion object {
        fun provideParseImageFile(bitmap: Bitmap): ParseFile {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            return ParseFile("profile.jpg", byteArray)
        }
    }
}
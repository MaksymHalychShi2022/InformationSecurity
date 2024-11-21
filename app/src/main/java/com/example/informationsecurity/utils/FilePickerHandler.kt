package com.example.informationsecurity.utils

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher

class FilePickerHandler(
    private val launcher: ActivityResultLauncher<Intent>
) {
    /**
     * The callback function to handle the selected file URI.
     * This can be dynamically updated before launching the picker.
     */
    var onFilePicked: ((Uri) -> Unit)? = null

    /**
     * Launches a file picker for reading a file.
     * @param mimeType The MIME type of the file to pick (default is all files).
     */
    fun pickFileToRead(mimeType: String = "*/*") {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType
        }
        launcher.launch(intent)
    }

    /**
     * Launches a file picker for writing a file.
     * @param mimeType The MIME type of the file to create (default is "application/octet-stream").
     * @param suggestedFileName Suggested name for the created file.
     */
    fun pickFileToWrite(
        mimeType: String = "application/octet-stream",
        suggestedFileName: String = "file"
    ) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, suggestedFileName)
        }
        launcher.launch(intent)
    }

    /**
     * Handles the result from the ActivityResultLauncher.
     * Invokes the `onFilePicked` callback with the selected file URI.
     * @param resultCode The result code from the file picker activity.
     * @param data The intent containing the file picker result.
     */
    fun handleResult(resultCode: Int, data: Intent?) {
        if (resultCode == android.app.Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            uri?.let { onFilePicked?.invoke(it) }
        }
    }
}

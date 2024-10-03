package com.example.informationsecurity.ui.lab2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.informationsecurity.databinding.FragmentLab2Binding
import java.io.InputStream
import java.io.OutputStream

class Lab2Fragment : Fragment() {

    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileSaveLauncher: ActivityResultLauncher<Intent>
    private lateinit var lab2ViewModel: Lab2ViewModel
    private var _binding: FragmentLab2Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        lab2ViewModel = ViewModelProvider(this).get(Lab2ViewModel::class.java)

        _binding = FragmentLab2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnHash.setOnClickListener {
            val inputString = binding.etInputString.text.toString()
            if (inputString.isEmpty()) {
                Toast.makeText(requireContext(), "Empty String!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lab2ViewModel.md5(inputString.toByteArray())
        }

        binding.btnChooseFile.setOnClickListener {
            openFilePicker()  // Open the file picker when button is clicked
        }

        binding.btnSaveOutputToFile.setOnClickListener {
            openFileSavePicker()
        }

        lab2ViewModel.output.observe(viewLifecycleOwner) {
            binding.tvOutput.text = it
        }

        return root
    }

    // Opens the file picker dialog
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"  // You can change this MIME type if you want to filter file types
        }
        filePickerLauncher.launch(intent)
    }

    // Reads the file content as ByteArray from Uri
    private fun readFileToByteArray(uri: Uri): ByteArray? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            inputStream?.use { it.readBytes() }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error reading file!", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            null
        }
    }

    // Register the ActivityResultLauncher to open the file picker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Launcher for opening a document (reading file)
        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Process the selected file's Uri for reading
                        processFile(it)
                    }
                }
            }

        // Launcher for creating a document (writing file)
        fileSaveLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Write data to the selected Uri
                        lab2ViewModel.output.value?.let { output ->
                            writeToFileUri(it, output)
                        }
                    }
                }
            }
    }

    // Opens the file picker dialog for writing (create new file)
    private fun openFileSavePicker() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"  // You can change this MIME type based on your needs
            putExtra(Intent.EXTRA_TITLE, "hash.md5")  // Suggested filename
        }
        fileSaveLauncher.launch(intent)
    }

    // Writes content to a selected Uri
    private fun writeToFileUri(uri: Uri, content: String) {
        try {
            val outputStream: OutputStream? = requireContext().contentResolver.openOutputStream(uri)
            outputStream?.use { it.write(content.toByteArray()) }

            // Notify the user
            Toast.makeText(requireContext(), "Saved to file!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error writing to file!", Toast.LENGTH_SHORT).show()
        }
    }


    // Process the file: read its content and calculate MD5 hash
    private fun processFile(uri: Uri) {
        val byteArray = readFileToByteArray(uri)
        if (byteArray != null) {
            lab2ViewModel.md5(byteArray)
        }
    }

    private fun notImplemented() {
        Toast.makeText(requireContext(), "Not implemented!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
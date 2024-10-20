package com.example.informationsecurity.ui.lab3

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.informationsecurity.MainViewModel
import com.example.informationsecurity.databinding.FragmentLab3Binding
import com.example.informationsecurity.utils.OperationState

class Lab3Fragment : Fragment() {

    private lateinit var encryptFilePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var decryptFilePickerLauncher: ActivityResultLauncher<Intent>
    private var _binding: FragmentLab3Binding? = null
    private lateinit var lab3ViewModel: Lab3ViewModel
    private val mainViewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        lab3ViewModel = ViewModelProvider(this).get(Lab3ViewModel::class.java)

        _binding = FragmentLab3Binding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnEncryptFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            encryptFilePickerLauncher.launch(intent)
        }


        binding.btnDecryptFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            decryptFilePickerLauncher.launch(intent)
        }
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        encryptFilePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Process the selected file's Uri for reading
                        lab3ViewModel.encryptFile(uri).observe(viewLifecycleOwner) { result ->
                            observeForProgressBar(result, "Encrypted!")
                        }
                    }
                }
            }

        decryptFilePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Process the selected file's Uri for reading
                        lab3ViewModel.decryptFile(uri).observe(viewLifecycleOwner) { result ->
                            observeForProgressBar(result, "Decrypted!")
                        }
                    }
                }
            }
    }


    private fun observeForProgressBar(result: OperationState<*>, successMassage: String? = null) {
        when (result) {
            is OperationState.Loading -> {
                mainViewModel.showProgressBar()
            }

            is OperationState.Success -> {
                successMassage?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
                mainViewModel.hideProgressBar()
            }

            is OperationState.Error -> {
                Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                mainViewModel.hideProgressBar()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
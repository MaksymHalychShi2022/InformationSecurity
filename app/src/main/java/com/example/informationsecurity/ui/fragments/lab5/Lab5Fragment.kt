package com.example.informationsecurity.ui.fragments.lab5

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
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.databinding.FragmentLab5Binding
import com.example.informationsecurity.utils.OperationState

class Lab5Fragment : Fragment() {

    private lateinit var loadPublicKeyLauncher: ActivityResultLauncher<Intent>
    private lateinit var loadPrivateKeyLauncher: ActivityResultLauncher<Intent>
    private lateinit var savePublicKeyLauncher: ActivityResultLauncher<Intent>
    private lateinit var savePrivateKeyLauncher: ActivityResultLauncher<Intent>

    private lateinit var signFilePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var verifyFilePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var saveSignatureOutputLauncher: ActivityResultLauncher<Intent>
    private lateinit var loadSignatureOutputLauncher: ActivityResultLauncher<Intent>


    private var _binding: FragmentLab5Binding? = null
    private lateinit var lab5ViewModel: Lab5ViewModel
    private val mainViewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        lab5ViewModel = ViewModelProvider(this).get(Lab5ViewModel::class.java)

        _binding = FragmentLab5Binding.inflate(inflater, container, false)
        val root: View = binding.root

        lab5ViewModel.signatureOutput.observe(viewLifecycleOwner) {
            binding.tvSignatureOutput.text = it
        }

        lab5ViewModel.privateKey.observe(viewLifecycleOwner) {
            binding.tvPrivateKey.text = it
        }

        lab5ViewModel.publicKey.observe(viewLifecycleOwner) {
            binding.tvPublicKey.text = it
        }

        binding.btnGenerateKeys.setOnClickListener {
            lab5ViewModel.generateKeys().observe(viewLifecycleOwner) {
                observeForProgressBar(it, "Keys generated!")
            }
        }

        binding.btnSavePublicKey.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type =
                    "application/octet-stream"  // You can change this MIME type based on your needs
                putExtra(Intent.EXTRA_TITLE, "id_rsa.pub")  // Suggested filename
            }
            savePublicKeyLauncher.launch(intent)
        }

        binding.btnLoadPublicKey.setOnClickListener {
            // Open the file picker when button is clicked
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            loadPublicKeyLauncher.launch(intent)
        }

        binding.btnSavePrivateKey.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type =
                    "application/octet-stream"  // You can change this MIME type based on your needs
                putExtra(Intent.EXTRA_TITLE, "id_rsa")  // Suggested filename
            }
            savePrivateKeyLauncher.launch(intent)
        }

        binding.btnLoadPrivateKey.setOnClickListener {
            // Open the file picker when button is clicked
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            loadPrivateKeyLauncher.launch(intent)
        }

        binding.btnSign.setOnClickListener {
            val inputString = binding.etInputString.text.toString()
            if (inputString.isEmpty()) {
                Toast.makeText(requireContext(), "Empty String!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lab5ViewModel.sign(inputString).observe(viewLifecycleOwner) {
                observeForProgressBar(it, "Signed!")
            }
        }

        binding.btnSignFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            signFilePickerLauncher.launch(intent)
        }

        binding.btnVerify.setOnClickListener {
            val inputString = binding.etInputString.text.toString()
            if (inputString.isEmpty()) {
                Toast.makeText(requireContext(), "Empty String!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lab5ViewModel.verifySignature(inputString).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is OperationState.Loading -> {
                        mainViewModel.showProgressBar()
                    }

                    is OperationState.Success -> {
                        mainViewModel.hideProgressBar()
                        result.data?.let { verified ->
                            if (verified) {
                                Toast.makeText(
                                    requireContext(), "Verified", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(), "Verification failed", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }

                    is OperationState.Error -> {
                        mainViewModel.hideProgressBar()
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnVerifyFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            verifyFilePickerLauncher.launch(intent)
        }

        binding.btnSaveSignature.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type =
                    "application/octet-stream"  // You can change this MIME type based on your needs
                putExtra(Intent.EXTRA_TITLE, "signature.pem")  // Suggested filename
            }
            saveSignatureOutputLauncher.launch(intent)
        }

        binding.btnLoadSignature.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            loadSignatureOutputLauncher.launch(intent)
        }

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadPublicKeyLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Process the selected file's Uri for reading
                        lab5ViewModel.loadPublicKey(it).observe(viewLifecycleOwner) { operation ->
                            observeForProgressBar(operation, "Public Key Loaded!")
                        }
                    }
                }
            }

        savePublicKeyLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Write data to the selected Uri
                        lab5ViewModel.savePublicKey(it).observe(viewLifecycleOwner) { operation ->
                            observeForProgressBar(operation, "Public Key Saved!")
                        }
                    }
                }
            }

        // Launchers for loading and saving private keys
        loadPrivateKeyLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Process the selected file's Uri for reading
                        lab5ViewModel.loadPrivateKey(it).observe(viewLifecycleOwner) { operation ->
                            observeForProgressBar(operation, "Private Key Loaded!")
                        }
                    }
                }
            }

        savePrivateKeyLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Write data to the selected Uri
                        lab5ViewModel.savePrivateKey(it).observe(viewLifecycleOwner) { operation ->
                            observeForProgressBar(operation, "Private Key Saved!")
                        }
                    }
                }
            }

        signFilePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data

                    uri?.let {
                        lab5ViewModel.sign(it).observe(viewLifecycleOwner) { operation ->
                            observeForProgressBar(operation, "Signed!")
                        }
                    }
                }
            }

        verifyFilePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data
                    uri?.let {
                        lab5ViewModel.verifyFileSignature(it)
                            .observe(viewLifecycleOwner) { result ->
                                when (result) {
                                    is OperationState.Loading -> {
                                        mainViewModel.showProgressBar()
                                    }

                                    is OperationState.Success -> {
                                        mainViewModel.hideProgressBar()
                                        result.data?.let { verified ->
                                            if (verified) {
                                                Toast.makeText(
                                                    requireContext(), "Verified", Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Verification failed",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                    }

                                    is OperationState.Error -> {
                                        mainViewModel.hideProgressBar()
                                        Toast.makeText(
                                            requireContext(), result.message, Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                    }
                }
            }

        saveSignatureOutputLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Write data to the selected Uri
                        lab5ViewModel.saveSignatureOutput(it)
                            .observe(viewLifecycleOwner) { operation ->
                                observeForProgressBar(operation, "Signature Saved!")
                            }
                    }
                }
            }

        loadSignatureOutputLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Process the selected file's Uri for reading
                        lab5ViewModel.loadSignature(it).observe(viewLifecycleOwner) { operation ->
                            observeForProgressBar(operation, "Signature Loaded!")
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
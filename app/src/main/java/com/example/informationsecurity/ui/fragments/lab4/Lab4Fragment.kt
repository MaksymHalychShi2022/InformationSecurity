package com.example.informationsecurity.ui.fragments.lab4

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.informationsecurity.R
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.databinding.FragmentLab4Binding
import com.example.informationsecurity.utils.OperationState

class Lab4Fragment : Fragment() {

    private lateinit var loadPublicKeyLauncher: ActivityResultLauncher<Intent>
    private lateinit var loadPrivateKeyLauncher: ActivityResultLauncher<Intent>
    private lateinit var savePublicKeyLauncher: ActivityResultLauncher<Intent>
    private lateinit var savePrivateKeyLauncher: ActivityResultLauncher<Intent>

    private lateinit var encryptFilePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var encryptOutputFilePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var decryptFilePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var decryptOutputFilePickerLauncher: ActivityResultLauncher<Intent>


    private var _binding: FragmentLab4Binding? = null
    private lateinit var lab4ViewModel: Lab4ViewModel
    private val mainViewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        lab4ViewModel = ViewModelProvider(this).get(Lab4ViewModel::class.java)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.lab4_option_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.generate_keys -> {
                        lab4ViewModel.generateKeys().observe(viewLifecycleOwner) {
                            observeForProgressBar(it, "Keys generated!")
                        }
                        true
                    }

                    R.id.encrypt_file -> {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type =
                                "*/*"  // You can change this MIME type if you want to filter file types
                        }
                        encryptFilePickerLauncher.launch(intent)
                        true
                    }

                    R.id.decrypt_file -> {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type =
                                "*/*"  // You can change this MIME type if you want to filter file types
                        }
                        decryptFilePickerLauncher.launch(intent)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        _binding = FragmentLab4Binding.inflate(inflater, container, false)

        // Public Key Output
        binding.outputPublicKey.tvLabel.text = requireContext().getString(R.string.public_key)
        lab4ViewModel.publicKey.observe(viewLifecycleOwner) {
            binding.outputPublicKey.tvScrollableText.text = it
        }

        binding.outputPublicKey.btnSave.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type =
                    "application/octet-stream"  // You can change this MIME type based on your needs
                putExtra(Intent.EXTRA_TITLE, "id_rsa.pub")  // Suggested filename
            }
            savePublicKeyLauncher.launch(intent)
        }

        binding.outputPublicKey.btnLoad.setOnClickListener {
            // Open the file picker when button is clicked
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            loadPublicKeyLauncher.launch(intent)
        }

        // Private Key Output
        binding.outputPrivateKey.tvLabel.text = requireContext().getString(R.string.private_key)
        lab4ViewModel.privateKey.observe(viewLifecycleOwner) {
            binding.outputPrivateKey.tvScrollableText.text = it
        }

        binding.outputPrivateKey.btnSave.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type =
                    "application/octet-stream"  // You can change this MIME type based on your needs
                putExtra(Intent.EXTRA_TITLE, "id_rsa")  // Suggested filename
            }
            savePrivateKeyLauncher.launch(intent)
        }

        binding.outputPrivateKey.btnLoad.setOnClickListener {
            // Open the file picker when button is clicked
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            loadPrivateKeyLauncher.launch(intent)
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadPublicKeyLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Process the selected file's Uri for reading
                        lab4ViewModel.loadPublicKey(it).observe(viewLifecycleOwner) { operation ->
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
                        lab4ViewModel.savePublicKey(it).observe(viewLifecycleOwner) { operation ->
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
                        lab4ViewModel.loadPrivateKey(it).observe(viewLifecycleOwner) { operation ->
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
                        lab4ViewModel.savePrivateKey(it).observe(viewLifecycleOwner) { operation ->
                            observeForProgressBar(operation, "Private Key Saved!")
                        }
                    }
                }
            }

        var inputUri: Uri? = null

        encryptFilePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    inputUri = result.data?.data

                    inputUri?.let {
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/octet-stream" // MIME type for binary files
                            putExtra(Intent.EXTRA_TITLE, "encrypted.txt") // Default file name
                        }
                        encryptOutputFilePickerLauncher.launch(intent)
                    }
                }
            }

        encryptOutputFilePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val outputUri: Uri? = result.data?.data
                    outputUri?.let {
                        // Process the selected file's Uri for reading
                        lab4ViewModel.encryptFile(inputUri!!, it)
                            .observe(viewLifecycleOwner) { result ->
                                observeForProgressBar(result, "Encrypted!")
                            }
                    }
                }
                inputUri = null // so it could not be used in later calls
            }


        decryptFilePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    inputUri = result.data?.data
                    inputUri?.let {
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/octet-stream" // MIME type for binary files
                            putExtra(Intent.EXTRA_TITLE, "decrypted.txt") // Default file name
                        }
                        decryptOutputFilePickerLauncher.launch(intent)
                    }
                }
            }

        decryptOutputFilePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        lab4ViewModel.decryptFile(inputUri!!, it)
                            .observe(viewLifecycleOwner) { result ->
                                observeForProgressBar(result, "Decrypted!")
                            }
                    }
                }
                inputUri = null
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
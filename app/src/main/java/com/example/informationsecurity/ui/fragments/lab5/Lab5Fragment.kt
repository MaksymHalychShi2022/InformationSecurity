package com.example.informationsecurity.ui.fragments.lab5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.informationsecurity.R
import com.example.informationsecurity.databinding.FragmentLab5Binding
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.utils.FilePickerHandler
import com.example.informationsecurity.utils.OperationState

class Lab5Fragment : Fragment() {

    private val lab5ViewModel: Lab5ViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentLab5Binding? = null

    private lateinit var filePickerHandler: FilePickerHandler

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filePickerHandler = FilePickerHandler(
            launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                filePickerHandler.handleResult(result.resultCode, result.data)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLab5Binding.inflate(inflater, container, false)

        // Signature Output
        binding.outputSignature.tvLabel.text = requireContext().getString(R.string.signature)
        lab5ViewModel.signatureOutput.observe(viewLifecycleOwner) {
            binding.outputSignature.tvScrollableText.text = it
        }

        binding.outputSignature.btnSave.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                lab5ViewModel.saveSignatureOutput(uri).observe(viewLifecycleOwner) { operation ->
                    observeForProgressBar(operation, "Signature Saved!")
                }
            }
            filePickerHandler.pickFileToWrite(suggestedFileName = "signature.pub")
        }

        binding.outputSignature.btnLoad.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                lab5ViewModel.loadSignature(uri).observe(viewLifecycleOwner) { operation ->
                    observeForProgressBar(operation, "Signature Loaded!")
                }
            }
            filePickerHandler.pickFileToRead()
        }

        // Public Key Output
        binding.outputPublicKey.tvLabel.text = requireContext().getString(R.string.public_key)
        lab5ViewModel.publicKey.observe(viewLifecycleOwner) {
            binding.outputPublicKey.tvScrollableText.text = it
        }

        binding.outputPublicKey.btnSave.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                lab5ViewModel.savePublicKey(uri).observe(viewLifecycleOwner) { operation ->
                    observeForProgressBar(operation, "Public Key Saved!")
                }
            }
            filePickerHandler.pickFileToWrite(suggestedFileName = "id_rsa.pub")
        }

        binding.outputPublicKey.btnLoad.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                lab5ViewModel.loadPublicKey(uri).observe(viewLifecycleOwner) { operation ->
                    observeForProgressBar(operation, "Public Key Loaded!")
                }
            }
            filePickerHandler.pickFileToRead()
        }

        // Private Key Output
        binding.outputPrivateKey.tvLabel.text = requireContext().getString(R.string.private_key)
        lab5ViewModel.privateKey.observe(viewLifecycleOwner) {
            binding.outputPrivateKey.tvScrollableText.text = it
        }

        binding.outputPrivateKey.btnSave.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                lab5ViewModel.savePrivateKey(uri).observe(viewLifecycleOwner) { operation ->
                    observeForProgressBar(operation, "Private Key Saved!")
                }
            }
            filePickerHandler.pickFileToWrite(suggestedFileName = "id_rsa")
        }

        binding.outputPrivateKey.btnLoad.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                lab5ViewModel.loadPrivateKey(uri).observe(viewLifecycleOwner) { operation ->
                    observeForProgressBar(operation, "Private Key Loaded!")
                }
            }
            filePickerHandler.pickFileToRead()
        }

        // Sign
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

        setupOptionMenu()

        return binding.root
    }

    private fun setupOptionMenu() = requireActivity().addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.lab5_option_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.generate_keys -> {
                    lab5ViewModel.generateKeys().observe(viewLifecycleOwner) {
                        observeForProgressBar(it, "Keys generated!")
                    }
                    true
                }

                R.id.verify_signature -> {
                    val inputString = binding.etInputString.text.toString()
                    if (inputString.isEmpty()) {
                        Toast.makeText(requireContext(), "Empty String!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        lab5ViewModel.verifySignature(inputString)
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
                                                    requireContext(),
                                                    "Verified",
                                                    Toast.LENGTH_SHORT
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
                    true
                }

                R.id.sign_file -> {
                    filePickerHandler.onFilePicked = { uri ->
                        lab5ViewModel.sign(uri).observe(viewLifecycleOwner) { operation ->
                            observeForProgressBar(operation, "Signed!")
                        }
                    }
                    filePickerHandler.pickFileToRead()
                    true
                }

                R.id.verify_file_signature -> {
                    filePickerHandler.onFilePicked = { uri ->
                        lab5ViewModel.verifyFileSignature(uri)
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
                                                    requireContext(),
                                                    "Verified",
                                                    Toast.LENGTH_SHORT
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
                    filePickerHandler.pickFileToRead()
                    true
                }

                else -> false
            }
        }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)


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
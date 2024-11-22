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
import androidx.lifecycle.Lifecycle
import com.example.informationsecurity.R
import com.example.informationsecurity.databinding.FragmentLab5Binding
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.utils.FilePickerHandler

class Lab5Fragment : Fragment() {

    private val lab5ViewModel: Lab5ViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentLab5Binding? = null

    private lateinit var filePickerHandler: FilePickerHandler

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filePickerHandler =
            FilePickerHandler(launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                filePickerHandler.handleResult(result.resultCode, result.data)
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLab5Binding.inflate(inflater, container, false)

        // Signature Output
        binding.outputSignature.tvLabel.text = requireContext().getString(R.string.signature)
        lab5ViewModel.signature.observe(viewLifecycleOwner) {
            binding.outputSignature.tvScrollableText.text = it
        }

        binding.outputSignature.btnSave.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                mainViewModel.runWithProgress(
                    task = { lab5ViewModel.saveSignature(uri) },
                    onSuccessMessage = "Signature Saved!"
                )
            }
            filePickerHandler.pickFileToWrite(suggestedFileName = "signature.pub")
        }

        binding.outputSignature.btnLoad.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                mainViewModel.runWithProgress(
                    task = { lab5ViewModel.loadSignature(uri) },
                    onSuccessMessage = "Signature Loaded!"
                )
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
                mainViewModel.runWithProgress(
                    task = { lab5ViewModel.savePublicKey(uri) },
                    onSuccessMessage = "Public Key Saved!"
                )
            }
            filePickerHandler.pickFileToWrite(suggestedFileName = "id_rsa.pub")
        }

        binding.outputPublicKey.btnLoad.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                mainViewModel.runWithProgress(
                    task = { lab5ViewModel.loadPublicKey(uri) },
                    onSuccessMessage = "Public Key Loaded!"
                )
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
                mainViewModel.runWithProgress(
                    task = { lab5ViewModel.savePrivateKey(uri) },
                    onSuccessMessage = "Private Key Saved!"
                )
            }
            filePickerHandler.pickFileToWrite(suggestedFileName = "id_rsa")
        }

        binding.outputPrivateKey.btnLoad.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                mainViewModel.runWithProgress(
                    task = { lab5ViewModel.loadPrivateKey(uri) },
                    onSuccessMessage = "Private Key Loaded!"
                )
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
            mainViewModel.runWithProgress(
                task = { lab5ViewModel.sign(inputString) },
                onSuccessMessage = "Signed!"
            )
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
                    mainViewModel.runWithProgress(
                        task = { lab5ViewModel.generateKeys("DSA") },
                        onSuccessMessage = "Keys generated!"
                    )
                    true
                }

                R.id.verify_signature -> {
                    val inputString = binding.etInputString.text.toString()
                    if (inputString.isEmpty()) {
                        Toast.makeText(requireContext(), "Empty String!", Toast.LENGTH_SHORT).show()
                    } else {
                        mainViewModel.runWithProgress(
                            task = { lab5ViewModel.verifySignature(inputString) },
                            onSuccess = { verified: Boolean ->
                                Toast.makeText(
                                    requireContext(), if (verified) {
                                        "Verified"
                                    } else {
                                        "Verification failed"
                                    }, Toast.LENGTH_SHORT
                                ).show()
                            })
                    }
                    true

                }

                R.id.sign_file -> {
                    filePickerHandler.onFilePicked = { uri ->
                        mainViewModel.runWithProgress(
                            task = { lab5ViewModel.sign(uri) },
                            onSuccessMessage = "Signed!"
                        )
                    }
                    filePickerHandler.pickFileToRead()
                    true
                }

                R.id.verify_file_signature -> {
                    filePickerHandler.onFilePicked = { uri ->
                        mainViewModel.runWithProgress(
                            task = { lab5ViewModel.verifySignature(uri) },
                            onSuccess = { verified: Boolean ->
                                Toast.makeText(
                                    requireContext(), if (verified) {
                                        "Verified"
                                    } else {
                                        "Verification failed"
                                    }, Toast.LENGTH_SHORT
                                ).show()
                            })
                    }
                    filePickerHandler.pickFileToRead()
                    true
                }

                else -> false
            }
        }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
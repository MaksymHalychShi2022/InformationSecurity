package com.example.informationsecurity.ui.fragments.lab2

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.informationsecurity.R
import com.example.informationsecurity.databinding.FragmentLab2Binding
import com.example.informationsecurity.utils.FilePickerHandler

class Lab2Fragment : Fragment() {

    private val lab2ViewModel: Lab2ViewModel by viewModels()
    private var _binding: FragmentLab2Binding? = null

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
        _binding = FragmentLab2Binding.inflate(inflater, container, false)


        binding.btnHash.setOnClickListener {
            val inputString = binding.etInputString.text.toString()
            if (inputString.isEmpty()) {
                Toast.makeText(requireContext(), "Empty String!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lab2ViewModel.runWithProgress(
                task = { lab2ViewModel.hash(inputString) }
            )
        }

        // Hash Output
        binding.outputHash.tvLabel.text = requireContext().getString(R.string.hash)
        lab2ViewModel.hash.observe(viewLifecycleOwner) {
            binding.outputHash.tvScrollableText.text = it
        }

        binding.outputHash.btnSave.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                lab2ViewModel.runWithProgress(
                    task = { lab2ViewModel.saveHash(uri) },
                    onSuccessMessage = "Hash Saved!"
                )
            }
            filePickerHandler.pickFileToWrite(suggestedFileName = "hash.md5")
        }

        binding.outputHash.btnLoad.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                lab2ViewModel.runWithProgress(
                    task = { lab2ViewModel.loadHash(uri) },
                    onSuccessMessage = "Hash Loaded!"
                )
            }
            filePickerHandler.pickFileToRead()
        }

        setupOptionMenu()

        return binding.root
    }

    private fun setupOptionMenu() = requireActivity().addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.lab2_option_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.hash_file -> {
                    // Open the file picker when button is clicked
                    filePickerHandler.onFilePicked = { uri ->
                        lab2ViewModel.runWithProgress(
                            task = { lab2ViewModel.hash(uri) }
                        )
                    }
                    filePickerHandler.pickFileToRead()
                    true
                }

                R.id.verify_file_hash -> {
                    Toast.makeText(requireContext(), "Not implemented", Toast.LENGTH_SHORT)
                        .show()
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
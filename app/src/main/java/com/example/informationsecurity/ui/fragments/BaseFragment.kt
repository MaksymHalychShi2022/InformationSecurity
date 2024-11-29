package com.example.informationsecurity.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.informationsecurity.databinding.WidgetOutputBinding
import com.example.informationsecurity.utils.FilePickerHandler
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected lateinit var filePickerHandler: FilePickerHandler

    protected val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize FilePickerHandler
        filePickerHandler = FilePickerHandler(
            launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                filePickerHandler.handleResult(result.resultCode, result.data)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // Utility method for setting up a reusable widget output section
    protected fun setupOutputSection(
        outputView: WidgetOutputBinding,
        labelResId: Int,
        data: LiveData<String>,
        saveTask: suspend (Uri) -> Unit,
        loadTask: suspend (Uri) -> Unit,
        suggestedFileName: String
    ) {
        outputView.tvLabel.text = getString(labelResId)
        data.observe(viewLifecycleOwner) {
            outputView.tvScrollableText.text = it
        }
        outputView.btnSave.setOnClickListener {
            handleFileAction(
                pickFile = { filePickerHandler.pickFileToWrite(suggestedFileName) },
                task = saveTask,
                successMessage = "Saved!"
            )
        }
        outputView.btnLoad.setOnClickListener {
            handleFileAction(
                pickFile = { filePickerHandler.pickFileToRead() },
                task = loadTask,
                successMessage = "Loaded!"
            )
        }
    }

    // Utility method for handling file actions
    private fun handleFileAction(
        pickFile: () -> Unit,
        task: suspend (uri: Uri) -> Unit,
        successMessage: String
    ) {
        filePickerHandler.onFilePicked = { uri ->
            onFilePicked(uri, task, successMessage)
        }
        pickFile()
    }

    // Default implementation for handling file picked actions
    protected open fun onFilePicked(uri: Uri, task: suspend (Uri) -> Unit, successMessage: String) {
        lifecycleScope.launch {
            task(uri)
            showToast(successMessage)
        }
    }

    protected fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    ): VB {
        _binding = bindingInflater.invoke(inflater, container, false)
        return _binding!!
    }

    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

package com.example.informationsecurity.ui.lab1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.informationsecurity.R
import com.example.informationsecurity.databinding.FragmentEstimatePiBinding
import com.example.informationsecurity.utils.LehmerRandomNumberGenerator
import com.example.informationsecurity.utils.RandomNumbersUtils

class EstimatePiFragment : Fragment() {

    private var _binding: FragmentEstimatePiBinding? = null
    private lateinit var viewModel: Lab1ViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(Lab1ViewModel::class.java)

        _binding = FragmentEstimatePiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnGenerate.setOnClickListener {
            val useStandardLib: Boolean = binding.cbUseStandardLib.isChecked
            val numberOfPairs: Long? =
                binding.etHowManyPairsToGenerate.text.toString().toLongOrNull()
            if (numberOfPairs != null && numberOfPairs > 0) {
                val estimatedPi = if (useStandardLib) RandomNumbersUtils.estimatePi(
                    { (1L..32767L).random() }, // Lambda for standard random number generation
                    totalPairs = numberOfPairs
                ) else RandomNumbersUtils.estimatePi(
                    LehmerRandomNumberGenerator()::next, // Function reference to the Lehmer RNG's next() method
                    totalPairs = numberOfPairs
                )
                viewModel.updateEstimatedPi(estimatedPi)
            } else {
                Toast.makeText(context, "Invalid input!", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.estimatedPi.observe(viewLifecycleOwner) {
            binding.tvOutput.text = it.toString()
        }

        binding.cbUseStandardLib.setOnCheckedChangeListener { _, isChecked ->
            binding.tvGeneratorUsedTitle.setText(
                if (isChecked) R.string.standard_lib_generator else R.string.lehmer_generator
            )
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
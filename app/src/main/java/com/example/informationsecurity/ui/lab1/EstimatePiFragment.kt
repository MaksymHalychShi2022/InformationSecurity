package com.example.informationsecurity.ui.lab1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
            val n: Long = binding.etHowManyPairsToGenerate.text.toString().toLong()
            val estimatedPi = RandomNumbersUtils.estimatePi(
                LehmerRandomNumberGenerator()::next,
                totalPairs = n
            )
            viewModel.updateEstimatedPi(estimatedPi)
        }

        viewModel.estimatedPi.observe(viewLifecycleOwner) {
            binding.tvOutput.text = it.toString()
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
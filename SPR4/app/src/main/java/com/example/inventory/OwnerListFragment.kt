package com.example.inventory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventory.databinding.FragmentOwnerListBinding

class OwnerListFragment : Fragment() {
    private var _binding: FragmentOwnerListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao(),
            (activity?.application as InventoryApplication).database.ownerDao()
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOwnerListBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = OwnerListAdapter {
            val action =    OwnerListFragmentDirections.actionOwnerListFragmentToOwnerDetailFragment(
                title = "Add Owner",
                ownerId = it.id
            )
            this.findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
        viewModel.allOwners.observe(this.viewLifecycleOwner) { owners ->
            owners?.let {
                adapter.submitList(it)
            }
        }
        binding.floatingActionButtonEditView.setOnClickListener {
            val action = OwnerListFragmentDirections.actionOwnerListFragmentToItemListFragment()
            this.findNavController().navigate(action)
        }
        binding.floatingActionButtonAddOwner.setOnClickListener {
            val action = OwnerListFragmentDirections.actionOwnerListFragmentToAddOwnerFragment(title = "Add Owner")
            this.findNavController().navigate(action)
        }
    }

}
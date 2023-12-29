package com.example.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventory.databinding.ItemListFragmentBinding

enum class SortType {
    ASCENDING_NAME,
    DESCENDING_NAME,
    BY_PRICE_ASC,
    BY_PRICE_DESC,
    BY_QUANTITY_ASC,
    BY_QUANTITY_DESC
}

class ItemListFragment : Fragment() {

    private var _binding: ItemListFragmentBinding? = null
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
        _binding = ItemListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.sortItems(SortType.ASCENDING_NAME)
        val adapter = ItemListAdapter {
            val action =    ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.floatingActionButton.setOnClickListener {
            val action = ItemListFragmentDirections.actionItemListFragmentToAddItemFragment(
                getString(R.string.add_fragment_title)
            )
            this.findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.floatingActionButtonEditView.setOnClickListener {
            val action = ItemListFragmentDirections.actionItemListFragmentToOwnerListFragment(
                title = "Owners List"
            )
            this.findNavController().navigate(action)
        }
        binding.floatingActionButtonEditSort.setOnClickListener {
            showSortPopupMenu(it)
        }
    }

    private fun showSortPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.sort_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.sort_az -> viewModel.sortItems(SortType.ASCENDING_NAME)
                R.id.sort_za -> viewModel.sortItems(SortType.DESCENDING_NAME)
                R.id.sort_price_up -> viewModel.sortItems(SortType.BY_PRICE_ASC)
                R.id.sort_price_down -> viewModel.sortItems(SortType.BY_PRICE_DESC)
                R.id.sort_quantity_up -> viewModel.sortItems(SortType.BY_QUANTITY_ASC)
                R.id.sort_quantity_down -> viewModel.sortItems(SortType.BY_QUANTITY_DESC)
            }
            true
        }

        popupMenu.show()
    }

}

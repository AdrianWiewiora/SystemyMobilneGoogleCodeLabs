/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.wordsapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsapp.databinding.ActivityMainBinding


/**
 * Main Activity and entry point for the app. Displays a RecyclerView of letters.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var isLinearLayoutManager = true
    private var isAscending = true
    private var lettersList = ('A').rangeTo('Z').toList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerView
        chooseLayout()
        chooseSort()
    }

    private fun chooseLayout() {
        if (isLinearLayoutManager) {
            recyclerView.layoutManager = LinearLayoutManager(this)
        } else {
            recyclerView.layoutManager = GridLayoutManager(this, 4)
        }
        recyclerView.adapter = LetterAdapter(lettersList)
    }

    private fun chooseSort() {
        recyclerView.adapter = LetterAdapter(lettersList)
    }


    private fun setIcon(menuItem: MenuItem?) {
        if (menuItem == null)
            return
        when (menuItem.itemId) {
            R.id.action_switch_layout -> {
                menuItem.icon = if (isLinearLayoutManager)
                    ContextCompat.getDrawable(this, R.drawable.ic_grid_layout)
                else ContextCompat.getDrawable(this, R.drawable.ic_linear_layout)
            }
            R.id.action_reverse_order -> {
                menuItem.icon = if (isAscending)
                    ContextCompat.getDrawable(this, R.drawable.ic_descending)
                else ContextCompat.getDrawable(this, R.drawable.ic_ascending)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.layout_menu, menu)

        val layoutButton = menu?.findItem(R.id.action_switch_layout)
        setIcon(layoutButton)

        val reverseOrderButton = menu?.findItem(R.id.action_reverse_order)
        setIcon(reverseOrderButton)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_layout -> {
                // Sets isLinearLayoutManager (a Boolean) to the opposite value
                isLinearLayoutManager = !isLinearLayoutManager
                // Sets layout and icon
                chooseLayout()
                setIcon(item)

                return true
            }
            R.id.action_reverse_order -> {
                isAscending = !isAscending
                lettersList  = lettersList.reversed()
                setIcon(item)
                chooseSort()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

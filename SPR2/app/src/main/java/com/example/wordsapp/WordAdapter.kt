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

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

/**
 * Adapter for the [RecyclerView] in [DetailActivity].
 */
class WordAdapter(private val letterId: String, context: Context) :
    RecyclerView.Adapter<WordAdapter.WordViewHolder>() {
    private var filteredWords: MutableList<String> = mutableListOf()

    init {
        CoroutineScope(Dispatchers.Main).launch {
            filteredWords = fetchWords(letterId).toMutableList()
            notifyDataSetChanged()
        }
    }

    suspend fun fetchWords(letterId: String): List<String> = withContext(Dispatchers.IO) {

        val url = URL("https://api.datamuse.com/words?sp=$letterId*")
        val urlConnection = url.openConnection() as HttpURLConnection

        urlConnection.requestMethod = "GET"
        val responseCode = urlConnection.responseCode

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = urlConnection.inputStream
            val response = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(response)

            (0 until jsonArray.length()).map { jsonArray.getJSONObject(it).getString("word") }
        } else {
            Log.e("WordAdapter", "Failed to retrieve words: $responseCode")
            emptyList()
        }
    }

    class WordViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val button = view.findViewById<Button>(R.id.button_item)
    }

    override fun getItemCount(): Int = filteredWords.size

    /**
     * Creates new views with R.layout.item_view as its template
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_view, parent, false)

        // Setup custom accessibility delegate to set the text read
        layout.accessibilityDelegate = Accessibility

        return WordViewHolder(layout)
    }

    /**
     * Replaces the content of an existing view with new data
     */
    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val item = filteredWords[position]
        val context = holder.view.context

        holder.button.text = item

        holder.button.setOnClickListener {
            // Tworzenie okna dialogowego
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Wybierz co chcesz zrobić z słowem")

            // Dodawanie przycisków do okna dialogowego
            builder.setPositiveButton("Otwórz w Google Chrome") { dialog, _ ->
                // Kod do otwarcia linku w Google Chrome
                val queryUrl: Uri = Uri.parse("${DetailActivity.SEARCH_PREFIX}${item}")
                val intent = Intent(Intent.ACTION_VIEW, queryUrl)
                context.startActivity(intent)
                dialog.dismiss()
            }

            builder.setNegativeButton("Przetłumacz w Tłumaczu Google") { dialog, _ ->
                // Kod do użycia Google Translate
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, item)
                intent.setPackage("com.google.android.apps.translate")
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Brak odpowiednich aplikacji", Toast.LENGTH_SHORT)
                        .show()
                }
                dialog.dismiss()
            }

            builder.show()
        }
    }

    // Setup custom accessibility delegate to set the text read with
    // an accessibility service
    companion object Accessibility : View.AccessibilityDelegate() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfo
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            // With `null` as the second argument to [AccessibilityAction], the
            // accessibility service announces "double tap to activate".
            // If a custom string is provided,
            // it announces "double tap to <custom string>".
            val customString = host.context?.getString(R.string.look_up_word)
            val customClick =
                AccessibilityNodeInfo.AccessibilityAction(
                    AccessibilityNodeInfo.ACTION_CLICK,
                    customString
                )
            info.addAction(customClick)
        }
    }
}
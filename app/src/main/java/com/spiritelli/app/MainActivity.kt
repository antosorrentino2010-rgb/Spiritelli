package com.spiritelli.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val collections = mutableListOf<String>()
    private val items = mutableMapOf<String, MutableList<String>>()

    private lateinit var collectionsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadData()
        showHome()
    }

    private fun showHome() {

        val root = createRoot()

        root.addView(createTitle("Collezioni"))

        val newCollection = Button(this)
        newCollection.text = "＋  Nuova collezione"
        newCollection.textSize = 18f
        newCollection.setOnClickListener {
            showCreateCollection()
        }

        root.addView(newCollection)

        collectionsContainer = LinearLayout(this)
        collectionsContainer.orientation = LinearLayout.VERTICAL
        collectionsContainer.setPadding(0, 25, 0, 0)

        root.addView(collectionsContainer)

        for (name in collections) {
            addCollectionButton(name)
        }

        setContentView(root)
    }

    private fun addCollectionButton(name: String) {

        val button = Button(this)
        button.text = "📁  $name"
        button.textSize = 18f

        button.setOnClickListener {
            showCollection(name)
        }

        button.setOnLongClickListener {
            showCollectionOptions(name)
            true
        }

        collectionsContainer.addView(button)
    }

    private fun showCollectionOptions(name: String) {

        val root = createRoot()

        root.addView(createTitle(name))

        val edit = Button(this)
        edit.text = "✏️  Modifica nome"

        edit.setOnClickListener {
            showEditCollection(name)
        }

        root.addView(edit)

        val delete = Button(this)
        delete.text = "🗑️  Elimina collezione"

        delete.setOnClickListener {

            collections.remove(name)
            items.remove(name)

            saveData()
            showHome()
        }

        root.addView(delete)

        val back = Button(this)
        back.text = "← Indietro"

        back.setOnClickListener {
            showHome()
        }

        root.addView(back)

        setContentView(root)
    }

    private fun showEditCollection(oldName: String) {

        val root = createRoot()

        root.addView(createTitle("Modifica collezione"))

        val input = EditText(this)
        input.setText(oldName)
        input.textSize = 18f

        root.addView(input)

        val save = Button(this)
        save.text = "Salva"

        save.setOnClickListener {

            val newName = input.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(
                    this,
                    "Inserisci un nome",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (newName != oldName && collections.contains(newName)) {
                Toast.makeText(
                    this,
                    "Questo nome esiste già",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val index = collections.indexOf(oldName)

            if (index >= 0) {
                collections[index] = newName
            }

            val oldItems = items.remove(oldName)
                ?: mutableListOf()

            items[newName] = oldItems

            saveData()
            showHome()
        }

        root.addView(save)

        val cancel = Button(this)
        cancel.text = "Annulla"

        cancel.setOnClickListener {
            showCollection(oldName)
        }

        root.addView(cancel)

        setContentView(root)
    }

    private fun showCreateCollection() {

        val root = createRoot()

        root.addView(createTitle("Nuova collezione"))

        val input = EditText(this)
        input.hint = "Nome della collezione"
        input.textSize = 18f

        root.addView(input)

        val save = Button(this)
        save.text = "Salva"

        save.setOnClickListener {

            val name = input.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(
                    this,
                    "Inserisci un nome",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (collections.contains(name)) {
                Toast.makeText(
                    this,
                    "Questa collezione esiste già",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            collections.add(name)
            items[name] = mutableListOf()

            saveData()
            showHome()
        }

        root.addView(save)

        val cancel = Button(this)
        cancel.text = "Annulla"

        cancel.setOnClickListener {
            showHome()
        }

        root.addView(cancel)

        setContentView(root)
    }

    private fun showCollection(name: String) {

        val root = createRoot()

        val back = Button(this)
        back.text = "← Indietro"
        back.setOnClickListener {
            showHome()
        }

        root.addView(back)
        root.addView(createTitle(name))

        val addItem = Button(this)
        addItem.text = "＋  Aggiungi elemento"
        addItem.textSize = 18f

        addItem.setOnClickListener {
            showAddItem(name)
        }

        root.addView(addItem)

        val list = items[name] ?: mutableListOf()

        for (item in list) {

            val itemButton = Button(this)
            itemButton.text = "• $item"
            itemButton.textSize = 18f

            itemButton.setOnLongClickListener {
                showItemOptions(name, item)
                true
            }

            root.addView(itemButton)
        }

        setContentView(root)
    }

    private fun showAddItem(collectionName: String) {

        val root = createRoot()

        root.addView(createTitle("Nuovo elemento"))

        val input = EditText(this)
        input.hint = "Nome dell'elemento"
        input.textSize = 18f

        root.addView(input)

        val save = Button(this)
        save.text = "Salva"

        save.setOnClickListener {

            val name = input.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(
                    this,
                    "Inserisci un nome",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val list = items[collectionName]
                ?: mutableListOf()

            list.add(name)
            items[collectionName] = list

            saveData()
            showCollection(collectionName)
        }

        root.addView(save)

        val cancel = Button(this)
        cancel.text = "Annulla"

        cancel.setOnClickListener {
            showCollection(collectionName)
        }

        root.addView(cancel)

        setContentView(root)
    }

    private fun showItemOptions(
        collectionName: String,
        item: String
    ) {

        val root = createRoot()

        root.addView(createTitle(item))

        val edit = Button(this)
        edit.text = "✏️  Modifica"

        edit.setOnClickListener {
            showEditItem(collectionName, item)
        }

        root.addView(edit)

        val delete = Button(this)
        delete.text = "🗑️  Elimina"

        delete.setOnClickListener {

            items[collectionName]?.remove(item)

            saveData()
            showCollection(collectionName)
        }

        root.addView(delete)

        val back = Button(this)
        back.text = "← Indietro"

        back.setOnClickListener {
            showCollection(collectionName)
        }

        root.addView(back)

        setContentView(root)
    }

    private fun showEditItem(
        collectionName: String,
        oldItem: String
    ) {

        val root = createRoot()

        root.addView(createTitle("Modifica elemento"))

        val input = EditText(this)
        input.setText(oldItem)
        input.textSize = 18f

        root.addView(input)

        val save = Button(this)
        save.text = "Salva"

        save.setOnClickListener {

            val newName = input.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(
                    this,
                    "Inserisci un nome",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val list = items[collectionName]

            if (list != null) {
                val index = list.indexOf(oldItem)

                if (index >= 0) {
                    list[index] = newName
                }
            }

            saveData()
            showCollection(collectionName)
        }

        root.addView(save)

        val cancel = Button(this)
        cancel.text = "Annulla"

        cancel.setOnClickListener {
            showCollection(collectionName)
        }

        root.addView(cancel)

        setContentView(root)
    }

    private fun createRoot(): LinearLayout {

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(40, 50, 40, 40)
        root.setBackgroundColor(Color.WHITE)

        return root
    }

    private fun createTitle(text: String): TextView {

        val title = TextView(this)
        title.text = text
        title.textSize = 30f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(Color.BLACK)
        title.gravity = Gravity.CENTER
        title.setPadding(0, 10, 0, 20)

        return title
    }

    private fun saveData() {

        val preferences = getSharedPreferences(
            "collezioni",
            MODE_PRIVATE
        )

        val editor = preferences.edit()

        editor.putStringSet(
            "collections",
            collections.toSet()
        )

        for (collection in collections) {

            val list = items[collection] ?: emptyList()

            editor.putStringSet(
                "items_$collection",
                list.toSet()
            )
        }

        editor.apply()
    }

    private fun loadData() {

        val preferences = getSharedPreferences(
            "collezioni",
            MODE_PRIVATE
        )

        val savedCollections =
            preferences.getStringSet(
                "collections",
                emptySet()
            )

        collections.clear()
        collections.addAll(
            savedCollections ?: emptySet()
        )

        items.clear()

        for (collection in collections) {

            val savedItems =
                preferences.getStringSet(
                    "items_$collection",
                    emptySet()
                )

            items[collection] =
                (savedItems ?: emptySet()).toMutableList()
        }
    }
}

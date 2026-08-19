package com.spiritelli.app

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {

    private val spiritelli = mutableListOf<String>()
    private lateinit var container: LinearLayout
    private lateinit var search: EditText

    private val developerCode = "131013"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadData()

        if (spiritelli.isEmpty()) {
            spiritelli.add("Spiritello esempio")
            saveData()
        }

        showHome()
    }

    // =========================
    // HOME
    // =========================

    private fun showHome() {

        val root = createRoot()

        // Header
        val header = LinearLayout(this)
        header.orientation = LinearLayout.VERTICAL
        header.setPadding(0, 10, 0, 20)

        val logo = TextView(this)
        logo.text = "👻"
        logo.textSize = 48f
        logo.gravity = Gravity.CENTER

        header.addView(logo)

        val title = TextView(this)
        title.text = "SPIRITELLI"
        title.textSize = 30f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(Color.rgb(35, 35, 45))
        title.gravity = Gravity.CENTER

        header.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "Scopri tutti gli Spiritelli"
        subtitle.textSize = 15f
        subtitle.setTextColor(Color.GRAY)
        subtitle.gravity = Gravity.CENTER

        header.addView(subtitle)

        root.addView(header)

        // Ricerca
        search = EditText(this)
        search.hint = "🔎  Cerca uno Spiritello..."
        search.textSize = 16f
        search.setSingleLine(true)
        search.setPadding(35, 0, 35, 0)
        search.background = roundedBackground(
            Color.rgb(245, 245, 250),
            30
        )

        root.addView(
            search,
            LinearLayout.LayoutParams(
                -1,
                60
            )
        )

        space(root, 15)

        // Contatore
        val counter = TextView(this)
        counter.text = "${spiritelli.size} Spiritelli"
        counter.textSize = 15f
        counter.setTextColor(Color.GRAY)

        root.addView(counter)

        space(root, 10)

        // Lista
        container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL

        root.addView(container)

        search.addTextChangedListener(
            object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    updateList(s.toString())
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {}
            }
        )

        updateList("")

        // Developer
        space(root, 20)

        val developer = TextView(this)
        developer.text = "⚙️  Modalità Developer"
        developer.textSize = 13f
        developer.gravity = Gravity.CENTER
        developer.setTextColor(Color.GRAY)
        developer.setPadding(0, 15, 0, 15)

        developer.setOnClickListener {
            askDeveloperCode()
        }

        root.addView(developer)

        setContentView(root)
    }

    // =========================
    // LISTA SPIRITELLI
    // =========================

    private fun updateList(query: String) {

        container.removeAllViews()

        val q = query.trim().lowercase(Locale.getDefault())

        val filtered = spiritelli.filter {
            q.isEmpty() ||
                    it.lowercase(Locale.getDefault()).contains(q)
        }

        if (filtered.isEmpty()) {

            val empty = TextView(this)
            empty.text = "👻\n\nNessuno Spiritello trovato"
            empty.textSize = 17f
            empty.gravity = Gravity.CENTER
            empty.setTextColor(Color.GRAY)
            empty.setPadding(0, 50, 0, 50)

            container.addView(empty)

            return
        }

        for (name in filtered) {
            addSpiritelloCard(name)
        }
    }

    private fun addSpiritelloCard(name: String) {

        val card = LinearLayout(this)
        card.orientation = LinearLayout.HORIZONTAL
        card.gravity = Gravity.CENTER_VERTICAL
        card.setPadding(25, 20, 20, 20)

        card.background = roundedBackground(
            Color.WHITE,
            22
        )

        card.elevation = 5f

        val icon = TextView(this)
        icon.text = "👻"
        icon.textSize = 30f
        icon.gravity = Gravity.CENTER

        card.addView(
            icon,
            LinearLayout.LayoutParams(
                65,
                65
            )
        )

        val textBox = LinearLayout(this)
        textBox.orientation = LinearLayout.VERTICAL
        textBox.setPadding(15, 0, 10, 0)

        val title = TextView(this)
        title.text = name
        title.textSize = 19f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(Color.rgb(30, 30, 40))

        textBox.addView(title)

        val description = TextView(this)
        description.text = "Tocca per visualizzare"
        description.textSize = 13f
        description.setTextColor(Color.GRAY)

        textBox.addView(description)

        card.addView(
            textBox,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        val arrow = TextView(this)
        arrow.text = "›"
        arrow.textSize = 30f
        arrow.setTextColor(Color.GRAY)

        card.addView(arrow)

        card.setOnClickListener {
            showSpiritello(name)
        }

        val params = LinearLayout.LayoutParams(
            -1,
            -2
        )

        params.setMargins(0, 0, 0, 15)

        container.addView(card, params)
    }

    // =========================
    // DETTAGLIO
    // =========================

    private fun showSpiritello(name: String) {

        val root = createRoot()

        val back = TextView(this)
        back.text = "‹  Indietro"
        back.textSize = 16f
        back.setTextColor(Color.rgb(80, 70, 180))
        back.setPadding(0, 10, 0, 20)

        back.setOnClickListener {
            showHome()
        }

        root.addView(back)

        val icon = TextView(this)
        icon.text = "👻"
        icon.textSize = 80f
        icon.gravity = Gravity.CENTER

        root.addView(icon)

        val title = createTitle(name)
        root.addView(title)

        val info = TextView(this)
        info.text = "Spiritello di Fortnite"
        info.textSize = 16f
        info.gravity = Gravity.CENTER
        info.setTextColor(Color.GRAY)

        root.addView(info)

        setContentView(root)
    }

    // =========================
    // DEVELOPER
    // =========================

    private fun askDeveloperCode() {

        val input = EditText(this)
        input.hint = "Codice Developer"
        input.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER
        input.setSingleLine(true)

        val dialog = AlertDialog.Builder(this)
            .setTitle("⚙️ Developer")
            .setMessage("Inserisci il codice per accedere alla modalità Developer")
            .setView(input)
            .setNegativeButton("Annulla", null)
            .setPositiveButton("Accedi", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    if (input.text.toString() == developerCode) {
                        dialog.dismiss()
                        showDeveloper()
                    } else {
                        Toast.makeText(
                            this,
                            "Codice errato",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        dialog.show()
    }

    private fun showDeveloper() {

        val root = createRoot()

        root.addView(
            createTitle("⚙️ Developer")
        )

        val subtitle = TextView(this)
        subtitle.text = "Gestisci gli Spiritelli"
        subtitle.textSize = 15f
        subtitle.setTextColor(Color.GRAY)
        subtitle.gravity = Gravity.CENTER

        root.addView(subtitle)

        space(root, 20)

        val add = createActionButton(
            "＋  Aggiungi Spiritello"
        )

        add.setOnClickListener {
            showAddSpiritello()
        }

        root.addView(add)

        space(root, 10)

        val manage = createActionButton(
            "✏️  Gestisci Spiritelli"
        )

        manage.setOnClickListener {
            showManageSpiritelli()
        }

        root.addView(manage)

        space(root, 10)

        val back = createActionButton(
            "←  Torna all'app"
        )

        back.setOnClickListener {
            showHome()
        }

        root.addView(back)

        setContentView(root)
    }

    // =========================
    // AGGIUNGI
    // =========================

    private fun showAddSpiritello() {

        val root = createRoot()

        root.addView(
            createTitle("Nuovo Spiritello")
        )

        val input = EditText(this)
        input.hint = "Nome dello Spiritello"
        input.textSize = 17f
        input.setSingleLine(true)

        root.addView(input)

        space(root, 15)

        val save = createActionButton("✓  Salva")

        save.setOnClickListener {

            val name =
                input.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(
                    this,
                    "Inserisci un nome",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (spiritelli.contains(name)) {
                Toast.makeText(
                    this,
                    "Questo Spiritello esiste già",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            spiritelli.add(name)
            saveData()

            Toast.makeText(
                this,
                "Spiritello aggiunto!",
                Toast.LENGTH_SHORT
            ).show()

            showDeveloper()
        }

        root.addView(save)

        space(root, 10)

        val cancel = createActionButton(
            "Annulla"
        )

        cancel.setOnClickListener {
            showDeveloper()
        }

        root.addView(cancel)

        setContentView(root)
    }

    // =========================
    // GESTIONE
    // =========================

    private fun showManageSpiritelli() {

        val root = createRoot()

        root.addView(
            createTitle("Gestisci Spiritelli")
        )

        for (name in spiritelli) {

            val button =
                createActionButton(
                    "👻  $name"
                )

            button.setOnClickListener {
                showEditSpiritello(name)
            }

            root.addView(button)

            space(root, 8)
        }

        val back =
            createActionButton("← Indietro")

        back.setOnClickListener {
            showDeveloper()
        }

        root.addView(back)

        setContentView(root)
    }

    // =========================
    // MODIFICA
    // =========================

    private fun showEditSpiritello(oldName: String) {

        val root = createRoot()

        root.addView(
            createTitle("Modifica Spiritello")
        )

        val input = EditText(this)
        input.setText(oldName)
        input.textSize = 17f
        input.setSingleLine(true)

        root.addView(input)

        space(root, 15)

        val save =
            createActionButton("✓  Salva modifiche")

        save.setOnClickListener {

            val newName =
                input.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(
                    this,
                    "Inserisci un nome",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (
                newName != oldName &&
                spiritelli.contains(newName)
            ) {
                Toast.makeText(
                    this,
                    "Questo nome esiste già",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val index =
                spiritelli.indexOf(oldName)

            if (index >= 0) {
                spiritelli[index] = newName
            }

            saveData()

            showManageSpiritelli()
        }

        root.addView(save)

        space(root, 10)

        val delete =
            createActionButton("🗑️  Elimina")

        delete.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Eliminare Spiritello?")
                .setMessage(
                    "Vuoi eliminare \"$oldName\"?"
                )
                .setNegativeButton(
                    "Annulla",
                    null
                )
                .setPositiveButton(
                    "Elimina"
                ) { _, _ ->

                    spiritelli.remove(oldName)

                    saveData()

                    showManageSpiritelli()
                }
                .show()
        }

        root.addView(delete)

        space(root, 10)

        val back =
            createActionButton("← Indietro")

        back.setOnClickListener {
            showManageSpiritelli()
        }

        root.addView(back)

        setContentView(root)
    }

    // =========================
    // UI
    // =========================

    private fun createRoot(): LinearLayout {

        val root = LinearLayout(this)

        root.orientation =
            LinearLayout.VERTICAL

        root.setPadding(
            35,
            35,
            35,
            30
        )

        root.setBackgroundColor(
            Color.rgb(248, 248, 252)
        )

        return root
    }

    private fun createTitle(
        text: String
    ): TextView {

        val title = TextView(this)

        title.text = text
        title.textSize = 28f
        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.setTextColor(
            Color.rgb(30, 30, 40)
        )

        title.gravity =
            Gravity.CENTER

        title.setPadding(
            0,
            10,
            0,
            20
        )

        return title
    }

    private fun createActionButton(
        text: String
    ): Button {

        val button = Button(this)

        button.text = text
        button.textSize = 16f
        button.isAllCaps = false

        button.setTextColor(
            Color.rgb(35, 35, 45)
        )

        button.background =
            roundedBackground(
                Color.WHITE,
                18
            )

        button.elevation = 3f

        button.setPadding(
            20,
            5,
            20,
            5
        )

        button.layoutParams =
            LinearLayout.LayoutParams(
                -1,
                60
            )

        return button
    }

    private fun roundedBackground(
        color: Int,
        radius: Int
    ): GradientDrawable {

        val drawable =
            GradientDrawable()

        drawable.setColor(color)

        drawable.cornerRadius =
            radius.toFloat()

        drawable.setStroke(
            1,
            Color.rgb(235, 235, 240)
        )

        return drawable
    }

    private fun space(
        root: LinearLayout,
        height: Int
    ) {

        val view = View(this)

        root.addView(
            view,
            LinearLayout.LayoutParams(
                1,
                height
            )
        )
    }

    // =========================
    // DATI
    // =========================

    private fun saveData() {

        val preferences =
            getSharedPreferences(
                "spiritelli",
                MODE_PRIVATE
            )

        preferences.edit()
            .putStringSet(
                "spiritelli",
                spiritelli.toSet()
            )
            .apply()
    }

    private fun loadData() {

        val preferences =
            getSharedPreferences(
                "spiritelli",
                MODE_PRIVATE
            )

        val saved =
            preferences.getStringSet(
                "spiritelli",
                emptySet()
            )

        spiritelli.clear()

        spiritelli.addAll(
            saved ?: emptySet()
        )
    }
}

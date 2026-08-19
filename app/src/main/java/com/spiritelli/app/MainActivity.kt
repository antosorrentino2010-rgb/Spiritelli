package com.spiritelli.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(40, 60, 40, 40)
        root.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "Collezioni"
        title.textSize = 32f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(Color.BLACK)
        title.gravity = Gravity.CENTER

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val subtitle = TextView(this)
        subtitle.text = "Le tue collezioni, tutto in un unico posto"
        subtitle.textSize = 17f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, 20, 0, 50)

        root.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val newCollection = Button(this)
        newCollection.text = "＋  Nuova collezione"
        newCollection.textSize = 18f

        root.addView(
            newCollection,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val emptyText = TextView(this)
        emptyText.text = "Non hai ancora creato nessuna collezione."
        emptyText.textSize = 16f
        emptyText.setTextColor(Color.GRAY)
        emptyText.gravity = Gravity.CENTER
        emptyText.setPadding(0, 80, 0, 0)

        root.addView(
            emptyText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(root)
    }
}

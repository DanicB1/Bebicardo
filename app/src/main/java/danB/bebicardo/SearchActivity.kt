package danB.bebicardo

import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.lang.Exception

class SearchActivity : AppCompatActivity(), View.OnClickListener {

    private var previousId: Int? = null
    private var selectedId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        addResultsToTable(intent.getStringArrayListExtra("recipes"))

        val infosButton: Button = findViewById(R.id.getInfosButton)
        infosButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                try {
                    var row: TableRow? = selectedId?.let { findViewById(it) }
                    var t = row?.getChildAt(0) as TextView

                    val intent = Intent(this@SearchActivity, RecipeActivity::class.java)
                    intent.putStringArrayListExtra("title", arrayListOf(t.text.toString()))
                    intent.putStringArrayListExtra("ingredients", MainActivity.db?.getIngredients(t.text.toString()))
                    intent.putStringArrayListExtra("instructions", MainActivity.db?.getInstructions(t.text.toString()))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this@SearchActivity,"Please select a recipe",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun addResultsToTable(text: ArrayList<String>) {
        var searchTable: TableLayout = findViewById(R.id.searchTable)
        var newRow: TableRow
        var newText: TextView

        var layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT
        )

        var id = 0
        for (t in text) {
            newRow = TableRow(this)
            newText = TextView(this)

            newText.textSize = 18.0F
            newText.text = t
            newRow.addView(newText, layoutParams)
            newRow.id = id++
            newRow.setOnClickListener(this)
            searchTable.addView(newRow)
        }
    }

    override fun onClick(v: View?) {
        if (selectedId != null) {
            previousId = selectedId
        }
        selectedId = v?.id!!

        var prev: TableRow? = previousId?.let { findViewById(it) }

        changeBackgroundColor(v as TableRow, prev)
    }

    private fun changeBackgroundColor(selectedRow: TableRow, previousRow: TableRow?) {
        if (previousId != null) {
            previousRow?.getChildAt(0)?.setBackgroundColor(Color.rgb(255,255,255))
        }
        selectedRow.getChildAt(0).setBackgroundColor(Color.rgb(193,225,236))
    }
}

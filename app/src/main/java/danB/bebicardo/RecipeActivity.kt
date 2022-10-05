package danB.bebicardo

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import org.w3c.dom.Text

class RecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        fillTables()
    }

    private fun fillTables() {
        setTitle()
        fillIngredientsTable()
        fillInstructionsTable()
    }

    private fun setTitle() {
        val tv: TextView = findViewById(R.id.recipeTitle) as TextView
        tv.text = intent.getStringArrayListExtra("title").get(0)
    }

    private fun fillIngredientsTable() {
        addRowsToTable(findViewById(R.id.ingredientsTable), intent.getStringArrayListExtra("ingredients"), "Ing")
    }

    private fun fillInstructionsTable() {
        addRowsToTable(findViewById(R.id.instructionsTable), intent.getStringArrayListExtra("instructions"), "Ins")
    }

    private fun addRowsToTable(table: TableLayout, text: ArrayList<String>, type: String) {
        var newRow: TableRow
        var newText: TextView
        var step: TextView

        var layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT
        )

        for (t in text) {
            var bottomMargin = layoutParams as ViewGroup.MarginLayoutParams
            bottomMargin.bottomMargin = 10

            newRow = TableRow(this)
            newRow.layoutParams = bottomMargin

            newText = TextView(this)

            var side = layoutParams as ViewGroup.MarginLayoutParams
            side.leftMargin = 5

            if (type == "Ins") {
                side.rightMargin = 5

                step = TextView(this)
                step.layoutParams = side
                step.text = t.substringBefore(' ')

                newText.text = t.substringAfter(' ')

                newRow.addView(step, layoutParams)
            } else {
                newText.layoutParams = side
                newText.text = t
            }

            newRow.addView(newText, layoutParams)
            table.addView(newRow)
        }
    }
}

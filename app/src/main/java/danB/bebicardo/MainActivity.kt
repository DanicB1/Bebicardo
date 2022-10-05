package danB.bebicardo

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    // Objet qui s'occupe des permissions
    private var permission: Permissions? = null

    // Objet pour envoyer des messages
    private var textMessage: TextMessage? = null

    // Objet pour parse les pages html
    private var parser: Parser? = null

    // Objet representant la connexion a la base de donnees
    // companion object = equivalent de static
    companion object {
        // Objet representant la connexion a la base de donnees
        private val DATABASE_NAME = "bebicardoDB"
        private val DATABASE_VERSION = 1
        var db: DatabaseHelper? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        /***************************************************************
         * Cutiness
         ***************************************************************/
        var bebicardo = findViewById(R.id.title) as TextView
        var typeFace = Typeface.createFromAsset(assets, "Fonts/BeautifulPeoplePersonalUse-dE0g.ttf")
        bebicardo.setTypeface(typeFace)

        /***************************************************************
         * Section cuisine / bebicardo
         ***************************************************************/
        // Lien vers la base de donnees
        db = DatabaseHelper(this, DATABASE_NAME, DATABASE_VERSION)
//        db!!.dropTables()
//        db!!.close()
//        this.deleteDatabase(DATABASE_NAME)

        // Objet pour parse
        parser = Parser()

        // Link bouton de recherche et dajout
        val searchButton: Button = findViewById(R.id.rechercheButton)
        val addButton: Button = findViewById(R.id.addButton)

        // Bouton pour chercher une recette
        searchButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var ing = findViewById(R.id.recherche) as EditText
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                val recipesFound = db?.searchRecipes(ing.text.toString())

                if (!recipesFound.isNullOrEmpty()) {
                    ing.setText("")
                    intent.putStringArrayListExtra("recipes", recipesFound)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@MainActivity, "No recipes were found", Toast.LENGTH_SHORT).show()
                }
            }
        })

        // Bouton pour ajouter une recette
        addButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                v?.let { urlDialog(it) }
            }
        })

        /***************************************************************
         * Section requetes / Messages
         ***************************************************************/
        permission = Permissions(this)
        textMessage = TextMessage(resources.getStringArray(R.array.requestSpinner),
                                  resources.getStringArray(R.array.requestAnswers))

        // Link les requetes et le bouton pour les envoyer
        val requestSpinner: Spinner = findViewById(R.id.requestSpinner)
        val requestButton: Button = findViewById(R.id.requestAtt)

        // Action d'envoyer une requete lorsquon appuie sur le bouton
        requestButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                textMessage!!.requestAttention(requestSpinner.selectedItem.toString())
            }
        })
    }

    // S'occupe des resultats des demandes de permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permission!!.handlePermissionsResult(requestCode, permissions, grantResults)
    }

    // AMOD: URL requiert un protocol (donc on ajoute https par defaut, ajouter dautres alternatives au besoin)
    private fun urlDialog(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog, null)

        builder.setCancelable(true)
        builder.setView(dialogView)

        var positiveButton = dialogView.findViewById(R.id.dialogPositive) as Button
        var negativeButton = dialogView.findViewById(R.id.dialogNegative) as Button
        val searchBar = dialogView.findViewById(R.id.dialogURL) as EditText

        val dialog = builder.create()

        positiveButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // AMOD: ajouter plus doptions de protocols
                val protocol = "https://"
                val url = searchBar.text.toString()

                var toastMessage: String
                var toast: Toast
                if (!parser?.sourceExists(url, protocol.length)!!) {
                    toastMessage = "Invalid URL"
                } else {
                    if (!db?.recipeExists(url)!!) {
                        db?.addNewRecipe(parser?.getRecipeInfos(url, protocol.length)!!)
                        toastMessage = "Recipe added"
                    } else {
                        toastMessage = "The recipe is already in the database"
                    }
                }

                toast = Toast.makeText(this@MainActivity, toastMessage, Toast.LENGTH_SHORT)
                toast.show()

                dialog.dismiss()
            }
        })

        negativeButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dialog.dismiss()
            }
        })

        dialog.show()
    }
}

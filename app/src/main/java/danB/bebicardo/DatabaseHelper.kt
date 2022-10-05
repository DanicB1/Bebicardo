package danB.bebicardo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import java.lang.Exception

class DatabaseHelper(context: Context, databaseName: String, databaseVersion: Int) : SQLiteOpenHelper(context, databaseName, null, databaseVersion) {
    private val TEXT = "tables done"
    private val DURATION = Toast.LENGTH_SHORT
    private val TOAST = Toast.makeText(context,TEXT,DURATION)

    private val DROP = "DROP TABLE IF EXISTS "

    private val COL_ID = "id"
    private val COL_NAME = "name"

    private val TABLE_RECIPES = "Recipes"
    private val CREATE_TABLE_RECIPES =
        "CREATE TABLE IF NOT EXISTS $TABLE_RECIPES (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_NAME TEXT" +
                ")"

    // AMOD: ajouter titre pour regrouper les ingredients sous des categories
    private val TABLE_INGREDIENTS = "Ingredients"
    private val COL_ID_RECIPE = "id_recipe"
    private val CREATE_TABLE_INGREDIENTS =
        "CREATE TABLE IF NOT EXISTS $TABLE_INGREDIENTS (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_ID_RECIPE INTEGER, "+
                "$COL_NAME TEXT, " +
                "FOREIGN KEY($COL_ID_RECIPE) REFERENCES $TABLE_RECIPES($COL_ID)"+
                ")"

    private val TABLE_INSTRUCTIONS = "Instructions"
    private val COL_INS_NUM = "instruction_number"
    private val COL_INSTRUCTION = "instruction"
    private val CREATE_TABLE_INSTRUCTIONS =
        "CREATE TABLE IF NOT EXISTS $TABLE_INSTRUCTIONS ("+
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "$COL_ID_RECIPE INTEGER, "+
                "$COL_INS_NUM INTEGER, "+
                "$COL_INSTRUCTION TEXT, "+
                "FOREIGN KEY($COL_ID_RECIPE) REFERENCES $TABLE_RECIPES($COL_ID)"+
                ")"

    private val TABLE_URLS = "Urls"
    private val COL_URL = "url"
    private val CREATE_TABLE_URLS =
        "CREATE TABLE IF NOT EXISTS $TABLE_URLS (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "$COL_URL TEXT" +
                ")"

    private var db: SQLiteDatabase? = null

    override fun onCreate(db: SQLiteDatabase?) {
        this.db = db
        createTables()
        TOAST.show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        dropTables()
        onCreate(db)
    }

    /**
     * POUR LES  RECHERCHES
     */
    fun searchRecipes(w: String): ArrayList<String> {
        val ingredientQuery = "SELECT DISTINCT "+COL_ID_RECIPE+
                " FROM "+ TABLE_INGREDIENTS +
                " WHERE "+COL_NAME+" like ?;"
        val recipeNameQuery = "SELECT "+COL_NAME+
                " FROM "+TABLE_RECIPES+
                " WHERE "+COL_ID+"=?;"
        var db = this.readableDatabase
        var cursor = db.rawQuery(ingredientQuery, arrayOf("%$w%"))

        var recipeIDs = ArrayList<Int>()
        if (cursor.moveToFirst()) {
            do {
                recipeIDs.add(cursor.getInt(cursor.getColumnIndex(COL_ID_RECIPE)))
            } while (cursor.moveToNext())
        }

        var recipeNames = ArrayList<String>()
        for (id in recipeIDs) {
            cursor = db.rawQuery(recipeNameQuery, arrayOf(id.toString()))
            if (cursor.moveToFirst()) {
                recipeNames.add(cursor.getString(cursor.getColumnIndex(COL_NAME)))
            }
        }
        cursor.close()

        return recipeNames
    }

    /**
     * POUR AJOUTER UNE RECETTE
     */
    fun recipeExists(url: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT *" +
                " FROM "+TABLE_URLS+
                " WHERE "+COL_URL+"=?"
        val c = db.rawQuery(query, arrayOf(url))

        if (c.count > 0) {
            return true
        }
        return false
    }

    fun addNewRecipe(recipeInfos: Array<Pair<ArrayList<String?>, ItemType>>) {
        var recipeID = -1
        for (info in recipeInfos) {
            when(info.second) {
                ItemType.TITLE -> recipeID = addTitle(info.first)
                ItemType.INGREDIENT -> addIngredients(info.first, recipeID)
                ItemType.INSTRUCTION -> addInstructions(info.first, recipeID)
            }
        }
    }

    private fun addTitle(title: ArrayList<String?>): Int {
        var db = this.writableDatabase
        var contentValues = ContentValues()
        val name = title.get(0)

        contentValues.put(COL_NAME, name)
        db.insert(TABLE_RECIPES, null, contentValues)

        return recipeID(name)
    }

    private fun recipeID(recipeName: String?): Int {
        val select = "SELECT " + COL_ID +
                " FROM "+ TABLE_RECIPES +
                " WHERE "+COL_NAME+"=?"
        var db = this.readableDatabase
        var cursor = db.rawQuery(select, arrayOf(recipeName))

        var result = -1
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex(COL_ID))
        }
        cursor.close()

        return result
    }

    private fun addIngredients(ingredients: ArrayList<String?>, recipeID: Int) {
        var db = this.writableDatabase
        var contentValues: ContentValues

        for (ing in ingredients) {
            contentValues = ContentValues()
            contentValues.put(COL_NAME, ing)
            contentValues.put(COL_ID_RECIPE, recipeID)
            db.insert(TABLE_INGREDIENTS, null, contentValues)
        }
    }

    private fun addInstructions(instructions: ArrayList<String?>, recipeID: Int) {
        var db = this.writableDatabase
        var contentValues: ContentValues
        var instructionNum = 1

        for (ins in instructions) {
            contentValues = ContentValues()
            contentValues.put(COL_ID_RECIPE, recipeID)
            contentValues.put(COL_INS_NUM, instructionNum)
            contentValues.put(COL_INSTRUCTION, ins)
            db.insert(TABLE_INSTRUCTIONS, null, contentValues)
            ++instructionNum
        }
    }

    fun getIngredients(t: String): ArrayList<String> {
        val ingQuery = "SELECT DISTINCT $COL_NAME" +
                " FROM $TABLE_INGREDIENTS" +
                " WHERE $COL_ID_RECIPE=?;"

        var db = this.readableDatabase
        var ing = ArrayList<String>()

        var cursor = db.rawQuery(ingQuery, arrayOf(recipeID(t).toString()))
        if (cursor.moveToFirst()) {
            do {
                ing.add(cursor.getString(cursor.getColumnIndex(COL_NAME)))
            } while (cursor.moveToNext())
        }

        return ing
    }

    fun getInstructions(t: String): ArrayList<String> {
        val insQuery = "SELECT DISTINCT $COL_INSTRUCTION" +
                " FROM $TABLE_INSTRUCTIONS" +
                " WHERE $COL_ID_RECIPE=?" +
                " ORDER BY $COL_INS_NUM;"

        var db = this.readableDatabase
        var ins = ArrayList<String>()

        var cursor = db.rawQuery(insQuery, arrayOf(recipeID(t).toString()))
        if (cursor.moveToFirst()) {
            do {
                ins.add(cursor.getString(cursor.getColumnIndex(COL_INSTRUCTION)))
            } while (cursor.moveToNext())
        }

        return ins
    }

    private fun createTables() {
        db?.execSQL(CREATE_TABLE_RECIPES)
        db?.execSQL(CREATE_TABLE_INGREDIENTS)
        db?.execSQL(CREATE_TABLE_URLS)
        db?.execSQL(CREATE_TABLE_INSTRUCTIONS)
    }

    fun dropTables() {
        try {
            db = this.readableDatabase
            db?.execSQL(DROP + TABLE_RECIPES)
            db?.execSQL(DROP + TABLE_INGREDIENTS)
            db?.execSQL(DROP + TABLE_URLS)
            db?.execSQL(DROP + CREATE_TABLE_INSTRUCTIONS)
        } catch (e: Exception) {
            Log.d("EXCEPTION", e.toString())
        }
    }
}
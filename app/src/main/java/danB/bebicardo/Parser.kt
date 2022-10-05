package danB.bebicardo

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Parser {
    // AMOD: ajouter plus de sources possibles
    val validSources = arrayOf("tastesbetterfromscratch",
                               "ricardo")

    fun getRecipeInfos(urlString: String, protocol: Int): Array<Pair<ArrayList<String?>, ItemType>>? {
        return readURL(urlString, getURL(urlString, protocol)!!)
    }

    fun sourceExists(url: String, protocol: Int): Boolean {
        if (getURL(url, protocol) in validSources) {
            return true
        }
        return false
    }

    private fun getURL(urlString: String, protocol: Int): String? {
        var subString = ""
        var stringIndex = protocol
        while (urlString[stringIndex] != '.') {
            subString += urlString[stringIndex]
            ++stringIndex
        }

        return subString
    }

    // AMOD: ajouter algo pour plusieurs sources possibles
    private fun readURL(url: String, source: String): Array<Pair<ArrayList<String?>, ItemType>>? {
        try {
            // AMOD: surement facon plus elegante avec une boucle
            if (source in validSources) {
                val htmlParser = Jsoup.connect(url).get()
                when (source) {
                    validSources[0] -> return readFromScratchURL(htmlParser)
                }
            }
        } finally {
//            connection.disconnect()
        }

        return null
    }

    private fun getTitleFromURL(doc: Document, selector: String): Pair<ArrayList<String?>, ItemType> {
        return Pair(arrayListOf(doc.select(selector).html()), ItemType.TITLE)
    }

    private fun getPairFromURL(list: ArrayList<String?>, type: ItemType): Pair<ArrayList<String?>, ItemType> {
        return Pair(list, type)
    }

    private fun readFromScratchURL(doc: Document): Array<Pair<ArrayList<String?>, ItemType>> {
        /**
         * Titre
         */
        // Selection du titre de la recette
        val titleSel = ".entry-header>h1"
        val title = getTitleFromURL(doc, titleSel)

        /**
         * Ingredients
         */
        // Preparation de la selection de chaque groupe dingredients
        var i = 1
        var ingr = doc.select(".wprm-recipe-ingredients:nth-of-type($i)>li")

        var ingList = ArrayList<String?>()
        while (ingr.size > 0) {
            for (ing in ingr) {
                ingList.add(ing.select("span:not(.wprm-checkbox-container)").text())
            }

            ++i
            ingr = doc.select(".wprm-recipe-ingredients:nth-of-type($i)>li")
        }
        val ingredients = getPairFromURL(ingList, ItemType.INGREDIENT)

        /**
         * Instructions
         */
        i = 1
        var insList = ArrayList<String?>()
        var inst = doc.select(".wprm-recipe-instructions>li>div>span")
        for (ins in inst) {
            insList.add("$i. ${ins.text()}")
            ++i
        }
        val instructions = getPairFromURL(insList, ItemType.INSTRUCTION)

        for (lul in insList) {
            Log.d("OY", lul)
        }


        return arrayOf(title, ingredients, instructions)
    }
}
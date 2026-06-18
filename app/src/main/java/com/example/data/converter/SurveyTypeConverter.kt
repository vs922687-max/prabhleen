package com.example.data.converter

import androidx.room.TypeConverter
import com.example.data.model.SurveyQuestion
import org.json.JSONArray
import org.json.JSONObject

class SurveyTypeConverter {
    @TypeConverter
    fun fromQuestionList(questions: List<SurveyQuestion>?): String {
        if (questions == null) return "[]"
        val array = JSONArray()
        for (q in questions) {
            val obj = JSONObject()
            obj.put("text", q.text)
            val optsArray = JSONArray()
            for (opt in q.options) {
                optsArray.put(opt)
            }
            obj.put("options", optsArray)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toQuestionList(jsonStr: String?): List<SurveyQuestion> {
        if (jsonStr.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<SurveyQuestion>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val text = obj.getString("text")
                val optsArray = obj.getJSONArray("options")
                val options = mutableListOf<String>()
                for (j in 0 until optsArray.length()) {
                    options.add(optsArray.getString(j))
                }
                list.add(SurveyQuestion(text, options))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}

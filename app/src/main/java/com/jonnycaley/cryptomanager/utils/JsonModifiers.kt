package com.jonnycaley.cryptomanager.utils

import android.util.Log

object JsonModifiers {

    fun jsonToCurrencies(fiats: String) : String {

        Log.i("jsonToCurrencies1", fiats)

        var ratesJson = fiats.substring(fiats.indexOf("\"rates"), fiats.indexOf("}") + 1)

        Log.i("jsonToCurrencies2", ratesJson)

        ratesJson = ratesJson.replace("\"rates\":{", "")

        Log.i("jsonToCurrencies3", ratesJson)

        ratesJson = ratesJson.replace(":", ",\"rate\":")

        Log.i("jsonToCurrencies4", ratesJson)

        ratesJson = ratesJson.replace(",\"rate\"" , "\"rate\"")

        Log.i("jsonToCurrencies5", ratesJson)

        ratesJson = ratesJson.replace("," , "},{\"fiat\":")

        Log.i("jsonToCurrencies6", ratesJson)

        ratesJson = ratesJson.replace("\"rate\"" , ",\"rate\"")

        Log.i("jsonToCurrencies7", ratesJson)

        ratesJson = "\"rates\":[{\"fiat\":$ratesJson"

        Log.i("jsonToCurrencies8", ratesJson)

        ratesJson = ratesJson.substring(0, ratesJson.length -1)

        Log.i("jsonToCurrencies9", ratesJson)

//        ratesJson = "{${ratesJson.substring(0, ratesJson.length - 9)}}]}"

        ratesJson = "{$ratesJson}]}"
        Log.i("jsonToCurrencies10", ratesJson)

        return ratesJson.trim()

    }


    fun jsonToExchanges(response: String): String {

        val replacedNulls = response.replace("{}", "{\"null\":[\"null\"]}")

        val responseStepOne = replacedNulls.replace(":[", ",\"converters\":[")
        val responseStepTwo = responseStepOne.replace("]","]}")
        val responseStepThree = responseStepTwo.replace(":{",",\"symbols\":[{\"symbol\":")
        val responseStepFour = responseStepThree.replace("]},","]},{\"symbol\":")
        val responseStepFive = responseStepFour.replace("]}},","]}},{\"name\":")
        val responseStepSix = responseStepFive.replace("]}}","]}]}")

        val startString = responseStepSix.substring(0,responseStepSix.length - 1)
        val endString = responseStepSix[responseStepSix.length - 1]

        val responseStepSeven = "$startString]$endString"
        val responseStepEight = "{\"exchanges\":[{\"name\":${responseStepSeven.substring(1)}"

        return responseStepEight.trim()

    }

    fun jsonToTimeStampPrice(response: String): String {

        val responseWithoutFirst = response.substring(1)

        val responseFromBracket = responseWithoutFirst.substring(responseWithoutFirst.indexOf("{"))

        val responseCutLastBracket = responseFromBracket.substring(0,responseFromBracket.length - 1)

        println(responseCutLastBracket)

        return responseCutLastBracket
    }

    fun jsonToCryptos(response: String): String {
        val responseNew = response.replace("Data\":{\"","Data\":[\"")

        val responseInArray = responseNew.replace("}},\"BaseImage","}],\"BaseImage")

        val responseFirstIds = responseInArray.replaceFirst(("\\[\""+".*?"+"\":\\{\"Id\"").toRegex(), "[{\"Id\"")

        val responseRemainderIds = responseFirstIds.replace((",\"IsTrading\":"+".*?"+"\\},\""+".*?"+"\":\\{\"Id\"").toRegex(), "\\},{\"Id\"")

        return responseRemainderIds
    }

    fun jsonToMultiPrices(json : String): String {

        val jsonToArrayStart = json.replaceFirst("{","{\"prices\":[{\"symbol\":")

        val jsonWithSymbol = jsonToArrayStart.replace("},","}},{\"symbol\":")

        val jsonWithPrices = jsonWithSymbol.replace(":{", ",\"prices\":{")

        val jsonAppendEnd = "$jsonWithPrices]}"

        return jsonAppendEnd

    }

    fun jsonToGeneral(json: String): String {

        val jsonFromDisplayOnwards = json.substring(0, json.indexOf("\"DISPLAY") -1 )

        println(jsonFromDisplayOnwards)

        val jsonFromUSDOnwards = jsonFromDisplayOnwards.substring(jsonFromDisplayOnwards.indexOf("\"USD\""))

        println(jsonFromUSDOnwards)

        val jsonSubEnd = jsonFromUSDOnwards.substring(0, jsonFromUSDOnwards.length - 2)

        println(jsonSubEnd)

        val jsonAddStart = "{$jsonSubEnd}"

        println(jsonAddStart)

        return jsonAddStart
    }

    fun jsonToSinglePrice(json : String) : String {

        return if(json.contains(":")){
            json.substring(json.indexOf(":") + 1, json.length - 1)
        } else {
            ""
        }
    }
}
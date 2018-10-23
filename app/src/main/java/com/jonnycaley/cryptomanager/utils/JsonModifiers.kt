package com.jonnycaley.cryptomanager.utils

object JsonModifiers {

    fun jsonToCurrencies(fiats: String) : String {

        var ratesJson = fiats.substring(fiats.indexOf("\"rates"), fiats.indexOf("}") + 1)

        ratesJson = ratesJson.replace("\"rates\":{", "")

        ratesJson = ratesJson.replace(":", ",\"rate\":")

        ratesJson = ratesJson.replace(",\"rate\"" , "\"rate\"")

        ratesJson = ratesJson.replace("," , "},{\"fiat\":")

        ratesJson = ratesJson.replace("\"rate\"" , ",\"rate\"")

        ratesJson = "\"rates\":[{\"fiat\":$ratesJson"

        ratesJson = ratesJson.substring(0, ratesJson.length -1)

        ratesJson = "{${ratesJson.substring(0, ratesJson.length - 9)}}]}"

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

        val jsonFromUSDOnwards = jsonFromDisplayOnwards.substring(jsonFromDisplayOnwards.indexOf("\"USD"))

        val jsonSubEnd = jsonFromUSDOnwards.substring(0, jsonFromUSDOnwards.length - 2)

        val jsonAddStart = "{$jsonSubEnd}"

        return jsonAddStart
    }
}
package com.jonnycaley.cryptomanager.utils

object JsonModifiers {

    fun jsonToCurrencies(fiats: String) : String {

        var ratesJson = fiats.substring(fiats.indexOf("\"rates"), fiats.indexOf("}") + 1)

        ratesJson = ratesJson.replace("\"rates\":{", "")

        println("1")

        ratesJson = ratesJson.replace(":", ",\"rate\":")

        println("2")

        ratesJson = ratesJson.replace(",\"rate\"" , "\"rate\"")

        println("3")

        ratesJson = ratesJson.replace("," , "},{\"fiat\":")

        println("4")

        ratesJson = ratesJson.replace("\"rate\"" , ",\"rate\"")

        println("5")

        ratesJson = "\"rates\":[{\"fiat\":$ratesJson"

        println("6")

        ratesJson = ratesJson.substring(0, ratesJson.length -1)

        println("7")

        ratesJson = "{${ratesJson.substring(0, ratesJson.length - 9)}}]}"

        println("8")

        println(ratesJson.trim())

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

        println(jsonToArrayStart)

        val jsonWithSymbol = jsonToArrayStart.replace("},","}},{\"symbol\":")

        println(jsonWithSymbol)


        val jsonWithPrices = jsonWithSymbol.replace(":{", ",\"prices\":{")

        println(jsonWithPrices)


        val jsonAppendEnd = "$jsonWithPrices]}"

        println(jsonAppendEnd)


        return jsonAppendEnd

    }

    fun jsonToGeneral(json: String): String {

        println(json)

        val jsonFromDisplayOnwards = json.substring(0, json.indexOf("\"DISPLAY") -1 )

        println(jsonFromDisplayOnwards)

        val jsonFromUSDOnwards = jsonFromDisplayOnwards.substring(jsonFromDisplayOnwards.indexOf("\"USD"))

        println(jsonFromUSDOnwards)

        val jsonSubEnd = jsonFromUSDOnwards.substring(0, jsonFromUSDOnwards.length - 2)

        println(jsonSubEnd)

        val jsonAddStart = "{$jsonSubEnd}"

        return jsonAddStart
    }
}
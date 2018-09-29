package com.jonnycaley.cryptomanager.utils

object JsonModifiers {

    fun jsonToCurrencies(fiats: String) : String {

        val startJson = fiats.substring(0, fiats.indexOf("\"rates"))

        var middleJson = fiats.substring(fiats.indexOf("\"rates"), fiats.indexOf("\"date"))

        val endJson = fiats.substring(fiats.indexOf("\"date"))

        middleJson = middleJson.replace("\"rates\":{", "")

        middleJson = middleJson.replace(":", ",\"rate\":")

        middleJson = middleJson.replace(",\"rate\"" , "\"rate\"")

        middleJson = middleJson.replace("," , "},{\"fiat\":")

        middleJson = middleJson.replace("\"rate\"" , ",\"rate\"")

        middleJson = "\"rates\":[{\"fiat\":$middleJson"

        middleJson = middleJson.substring(0, middleJson.length -1)

        middleJson = "${middleJson.substring(0, middleJson.length - 9)}],"

        println(startJson+middleJson+endJson.trim())

        return startJson+middleJson+endJson.trim()

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

}
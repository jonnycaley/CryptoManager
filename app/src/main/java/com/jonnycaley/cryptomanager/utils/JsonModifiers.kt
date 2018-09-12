package com.jonnycaley.cryptomanager.utils

object JsonModifiers {

    fun jsonToCurrencies(fiats: String) : String {

        val startJson = fiats.substring(0, fiats.indexOf("\"rates"))

        var endJson = fiats.substring(fiats.indexOf("\"rates"))


        endJson = endJson.replace("\"rates\":{", "")

        endJson = endJson.replace(":", ",\"rate\":")

        endJson = endJson.replace(",\"rate\"" , "\"rate\"")

        endJson = endJson.replace("," , "},{\"fiat\":")

        endJson = endJson.replace("\"rate\"" , ",\"rate\"")

        endJson = "\"rates\":[{\"fiat\":$endJson"

        endJson = endJson.substring(0, endJson.length -1)

        endJson = "$endJson]}"

        return startJson+endJson.trim()

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
package com.jonnycaley.cryptomanager.ui.article

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class ArticleArgs(val article : Article) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, ArticleActivity::class.java).apply {
        putExtra(ARTICLE_KEY, article)
    }

    companion object {
        fun deserializeFrom(intent: Intent): ArticleArgs{
            return ArticleArgs(
                    article = intent.getSerializableExtra(ARTICLE_KEY) as Article
            )
        }
    }
}
private const val ARTICLE_KEY = "article_key"



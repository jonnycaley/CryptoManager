package com.jonnycaley.cryptomanager.utils.mvp

interface BaseView<T> {

    fun setPresenter(presenter: T)
}
package com.jonnycaley.cryptomanager.ui.transactions.fiat

import com.jonnycaley.cryptomanager.data.model.DataBase.Variables
import io.reactivex.disposables.CompositeDisposable

class FiatTransactionPresenter (var dataManager: FiatTransactionDataManager, var view: FiatTransactionContract.View) : FiatTransactionContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

    }

    override fun saveFiatTransaction(isDeposit: Boolean, exchange: CharSequence, currency: CharSequence, quantity: CharSequence, date: CharSequence, notes: CharSequence) {

        var depositType = if(isDeposit){
            Variables.Transaction.Type.widthdrawl
        } else {
            Variables.Transaction.Type.deposit
        }

        dataManager.saveFiatTransaction(depositType, exchange, currency, quantity, date, notes)

    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "FiatTransactionPresenter"
    }

}
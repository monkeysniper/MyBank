package com.example.mybank.domain.contract

import com.example.mybank.data.model.Account

interface AccountContract {
    interface View{
        fun showAccounts(accounts:List<Account>)
        fun showError(message: String)
        fun showSuccess(message: String)
    }

    interface Presenter{
        fun loadAccounts()
        fun addAccount(name:String,balance:String,currency:String)
    }
}
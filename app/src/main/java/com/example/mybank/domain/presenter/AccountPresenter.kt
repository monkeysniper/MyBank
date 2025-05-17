package com.example.mybank.domain.presenter

import com.example.mybank.data.api.ApiClient
import com.example.mybank.data.model.Account
import com.example.mybank.domain.contract.AccountContract
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.acos

class AccountPresenter(private val view: AccountContract.View) : AccountContract.Presenter {
    override fun loadAccounts() {
        ApiClient.accountApi.getAccounts().enqueue(object : Callback<List<Account>> {
            override fun onResponse(call: Call<List<Account>>, response: Response<List<Account>>) {
                if (response.isSuccessful) {
                    view.showAccounts(response.body() ?: emptyList())
                } else {
                    view.showError("Ошибка загрузки")
                }
            }

            override fun onFailure(call: Call<List<Account>>, t: Throwable) {
                view.showError("Ошибка сети: ${t.message}")
            }
        })
    }

    override fun addAccount(name: String, balance: String, currency: String) {
        val account = Account(name = name, balance = balance, currency = currency, isActive = true)
        ApiClient.accountApi.createAccount(account).enqueue(object : Callback<Account> {
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    view.showSuccess("Аккаунт добавлен")
                    loadAccounts()
                } else {
                    view.showError("Ошибка добавление ")
                }
            }

            override fun onFailure(call: Call<Account>, t: Throwable) {
                view.showError("Ошибка сети: ${t.message}")
            }
        })
    }

    override fun deleteAccount(accountId: String) {
        ApiClient.accountApi.deleteAccount(accountId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    view.showSuccess("Удалено")
                    loadAccounts()
                } else {
                    view.showError("Ошибка удаления")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                view.showError("Ошибка сети: ${t.message}")

            }
        })
    }

    override fun updateAccount(accountId: String, account: Account) {
        ApiClient.accountApi.updateAccount(accountId, account).enqueue(object : Callback<Account> {
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    view.showSuccess("Успешно обновлен")
                    loadAccounts()
                } else {
                    view.showError("Ошибка")
                }
            }

            override fun onFailure(call: Call<Account>, t: Throwable) {
                view.showError("Ошибка сети: ${t.message}")
            }
        })
    }
}


package com.example.mybank.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybank.data.api.AccountApi
import com.example.mybank.data.model.Account
import com.example.mybank.data.model.PatchAccountStatusDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

//эта аннотация сообщает что зависимости будут внедряться в viewModel
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountApi: AccountApi
) : ViewModel() {
    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    //Загрузка списка счетов
    fun loadAccounts() {
        //Выполняем ассинхронный запрос к Api для получения списк счетов
        accountApi.getAccounts().enqueue(object : Callback<List<Account>> {
            //Обработка успешного ответа
            override fun onResponse(call: Call<List<Account>>, response: Response<List<Account>>) {
                if (response.isSuccessful) {
                    _accounts.value = response.body() ?: emptyList()
                } else {
                    //Сообщение об ошибке
                    _errorMessage.value = "Ошибка загрузки"
                }
            }

            //Обработка ошибка сети
            override fun onFailure(call: Call<List<Account>>, t: Throwable) {
                _errorMessage.value = ("Ошибка сети: ${t.message}")
            }
        })
    }

    //Добавление нового счета
    fun addAccount(name: String, balance: String, currency: String) {
        //Создаем обьект account с передаными данными
        val account = Account(name = name, balance = balance, currency = currency, isActive = true)
        //выполняем ассинхронный запрос к Api
        accountApi.createAccount(account).enqueue(object : Callback<Account> {
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    _successMessage.value = "Аккаунт добавлен"
                    loadAccounts()
                } else {
                    _errorMessage.value = "Ошибка добавление "
                }
            }

            override fun onFailure(call: Call<Account>, t: Throwable) {
                _errorMessage.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    //Удаление счета
    fun deleteAccount(accountId: String) {
        accountApi.deleteAccount(accountId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    _successMessage.value = "Удалено"
                    loadAccounts()
                } else {
                    _errorMessage.value = "Ошибка удаления"
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                _errorMessage.value = "Ошибка сети: ${t.message}"

            }
        })
    }

    //Обновление счета
    fun updateAccount(accountId: String, account: Account) {
        accountApi.updateAccount(accountId, account).enqueue(object : Callback<Account> {
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    _successMessage.value = "Успешно обновлен"
                    loadAccounts()
                } else {
                    _errorMessage.value = "Ошибка"
                }
            }

            override fun onFailure(call: Call<Account>, t: Throwable) {
                _errorMessage.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    //Обновление статус счета
    fun updateAccountStatus(accountId: String, isActive: Boolean) {
        accountApi.patchAccountsStatus(accountId, PatchAccountStatusDTO(isActive))
            .enqueue(object : Callback<Account> {
                override fun onResponse(call: Call<Account>, response: Response<Account>) {
                    if (response.isSuccessful) {
                        _successMessage.value = "Успешно cтатус счета"
                        loadAccounts()
                    } else {
                        _errorMessage.value = "Ошибка"
                    }
                }

                override fun onFailure(call: Call<Account>, t: Throwable) {
                    _errorMessage.value = "Ошибка сети: ${t.message}"
                }
            })
    }
}


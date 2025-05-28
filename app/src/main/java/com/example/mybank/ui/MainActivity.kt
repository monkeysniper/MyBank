package com.example.mybank.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybank.R
import com.example.mybank.data.model.Account
import com.example.mybank.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val viewModel: AccountViewModel by viewModels()
    private lateinit var adapter: AccountAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel.loadAccounts()

        recyclerView = binding.rv
        adapter = AccountAdapter(onDELETE = { id ->
            viewModel.deleteAccount(id)
        }, onChange = { account ->
            showEditDialog(account)
        },
            onStatusToggle = { id, isChecked ->
                viewModel.updateAccountStatus(id, isChecked)
            }
        )


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        binding.btnAdd.setOnClickListener({
            showAddDialog()
        })
        //загрузка списка счетов
        viewModel.loadAccounts()
        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {
        //подписка на LiveData при изменении списка аккаунтов обновляем ui
        viewModel.accounts.observe(this) {
            adapter.submitList(it)
        }
        viewModel.successMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()

        }
    }
    // Отображает диалог для добавления нового счёта
    // Получает данные от пользователя и вызывает метод ViewModel для добавления.
    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.et_name)
        val balanceInput = dialogView.findViewById<EditText>(R.id.et_balance)
        val currencyInput = dialogView.findViewById<EditText>(R.id.et_currency)

            //Cоздание и отображение диалог окна
        AlertDialog.Builder(this)
            .setTitle("Добавить счет ")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val name = nameInput.text.toString()
                val balance = balanceInput.text.toString()
                val currency = currencyInput.text.toString()
                viewModel.addAccount(name, balance, currency)
            }
            .setNeutralButton("Отмена", null)
            .show()
    }

    private fun showEditDialog(account: Account) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.et_name)
        val balanceInput = dialogView.findViewById<EditText>(R.id.et_balance)
        val currencyInput = dialogView.findViewById<EditText>(R.id.et_currency)
//Заполнение текущими данными
        nameInput.setText(account.name)
        balanceInput.setText(account.balance)
        currencyInput.setText(account.currency)

        AlertDialog.Builder(this)
            .setTitle("Редактировать счет ")
            .setView(dialogView)
            .setPositiveButton("Обновить") { _, _ ->
                val name = nameInput.text.toString()
                val balance = balanceInput.text.toString()
                val currency = currencyInput.text.toString()

                val updated = account.copy(
                    name = name,
                    balance = balance,
                    currency = currency
                )
                viewModel.updateAccount(updated.id!!, updated)
            }
            .setNeutralButton("Отмена", null)
            .show()
    }
}

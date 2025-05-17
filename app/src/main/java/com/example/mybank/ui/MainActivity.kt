package com.example.mybank.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.mybank.R
import com.example.mybank.data.model.Account
import com.example.mybank.databinding.ActivityMainBinding
import com.example.mybank.databinding.ItemBinding
import com.example.mybank.domain.contract.AccountContract
import com.example.mybank.domain.presenter.AccountPresenter

class MainActivity : AppCompatActivity(), AccountContract.View {
    lateinit var binding: ActivityMainBinding
    private lateinit var presenter: AccountContract.Presenter
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
        presenter = AccountPresenter(this)
        presenter.loadAccounts()

        recyclerView = binding.rv
        adapter = AccountAdapter(onDELETE = {id->
            presenter.deleteAccount(id)
        }, onChange = { account->
            showEditDialog(account)
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        binding.btnAdd.setOnClickListener({
            showAddDialog()
        })


    }

    override fun showAccounts(accounts: List<Account>) {
        adapter.submitList(accounts)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.et_name)
        val balanceInput = dialogView.findViewById<EditText>(R.id.et_balance)
        val currencyInput = dialogView.findViewById<EditText>(R.id.et_currency)

        AlertDialog.Builder(this)
            .setTitle("Добавить счет ")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val name = nameInput.text.toString()
                val balance = balanceInput.text.toString()
                val currency = currencyInput.text.toString()
                presenter.addAccount(name, balance, currency)
            }
            .setNeutralButton("Отмена", null)
            .show()
    }

    private fun showEditDialog(account: Account){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.et_name)
        val balanceInput = dialogView.findViewById<EditText>(R.id.et_balance)
        val currencyInput = dialogView.findViewById<EditText>(R.id.et_currency)

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

                val updated= account.copy(
                    name=name,
                    balance = balance,
                    currency = currency
                )
                presenter.updateAccount(updated.id!!,updated )
            }
            .setNeutralButton("Отмена", null)
            .show()
    }
}


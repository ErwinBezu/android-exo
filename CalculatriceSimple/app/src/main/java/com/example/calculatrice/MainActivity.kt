package com.example.calculatrice

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Variables pour stocker les nombres et l'opération
    private var firstNumber = 0.0
    private var secondNumber = 0.0
    private var currentOperation = ""
    private var isOperationClicked = false


    // Références vers les composants UI
    private lateinit var tvResult: TextView

    private lateinit var tvHistory: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisation des composants
        initializeViews()
        setupNumberButtons()
        setupOperationButtons()
    }

    private fun initializeViews() {
        tvResult = findViewById(R.id.tv_result)
        tvHistory = findViewById(R.id.tv_history)
    }

    private fun setupNumberButtons() {
        // Liste des boutons numériques avec leurs IDs
        val numberButtons = listOf(
            R.id.btn_0 to "0",
            R.id.btn_1 to "1",
            R.id.btn_2 to "2",
            R.id.btn_3 to "3",
            R.id.btn_4 to "4",
            R.id.btn_5 to "5",
            R.id.btn_6 to "6",
            R.id.btn_7 to "7",
            R.id.btn_8 to "8",
            R.id.btn_9 to "9",
            R.id.btn_dot to "."
        )

        // Configuration des listeners pour chaque bouton numérique
        numberButtons.forEach { (buttonId, number) ->
            findViewById<Button>(buttonId).setOnClickListener {
                onNumberClicked(number)
            }
        }
    }

    private fun setupOperationButtons() {
        // Boutons d'opération
        findViewById<Button>(R.id.btn_add).setOnClickListener {
            onOperationClicked("+")
        }

        findViewById<Button>(R.id.btn_subtract).setOnClickListener {
            onOperationClicked("-")
        }

        findViewById<Button>(R.id.btn_multiply).setOnClickListener {
            onOperationClicked("×")
        }

        findViewById<Button>(R.id.btn_divide).setOnClickListener {
            onOperationClicked("÷")
        }

        // Bouton égal
        findViewById<Button>(R.id.btn_equals).setOnClickListener {
            calculateResult()
        }

        // Bouton clear
        findViewById<Button>(R.id.btn_clear).setOnClickListener {
            clearCalculator()
        }
    }

    private fun onNumberClicked(number: String) {
        val currentText = tvResult.text.toString()

        when {
            // Si on vient de cliquer sur une opération, commencer un nouveau nombre
            isOperationClicked -> {
                tvResult.text = number
                isOperationClicked = false
            }
            // Si l'affichage montre "0", le remplacer par le nouveau chiffre
            currentText == "0" -> {
                tvResult.text = number
            }
            // Sinon, ajouter le chiffre à la fin
            else -> {
                tvResult.text = currentText + number
            }
        }
    }

    private fun onOperationClicked(operation: String) {
        // Sauvegarder le premier nombre
        firstNumber = tvResult.text.toString().toDoubleOrNull() ?: 0.0
        currentOperation = operation
        isOperationClicked = true

        // Afficher l'opération (optionnel, pour feedback utilisateur)
        tvResult.text = "${firstNumber.formatNumber()} $operation"
    }

    private fun calculateResult() {
        // Récupérer le second nombre
        val currentText = tvResult.text.toString()

        // Extraire le nombre après l'opération
        val parts = currentText.split(" ")
        secondNumber = if (parts.size > 2) {
            parts[2].toDoubleOrNull() ?: 0.0
        } else {
            currentText.toDoubleOrNull() ?: 0.0
        }

        // Calculer le résultat selon l'opération
        val result = when (currentOperation) {
            "+" -> firstNumber + secondNumber
            "-" -> firstNumber - secondNumber
            "×" -> firstNumber * secondNumber
            "÷" -> {
                if (secondNumber != 0.0) {
                    firstNumber / secondNumber
                } else {
                    showError("Division par zéro impossible")
                    return
                }
            }
            else -> {
                showError("Opération non reconnue")
                return
            }
        }

        // Afficher le résultat
        tvResult.text = result.formatNumber()

        // Réinitialiser pour la prochaine opération
        currentOperation = ""
        isOperationClicked = false
    }

    private fun clearCalculator() {
        tvResult.text = "0"
        firstNumber = 0.0
        secondNumber = 0.0
        currentOperation = ""
        isOperationClicked = false
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        clearCalculator()
    }

    // Extension pour formater les nombres (enlever les .0 inutiles)
    private fun Double.formatNumber(): String {
        return if (this % 1.0 == 0.0) {
            this.toInt().toString()
        } else {
            String.format("%.2f", this)
        }
    }
}
package com.example.myadder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView textExpression, textResult;
    String currentInput = "";
    String operator = "";
    double firstValue = 0;
    boolean isOperatorClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textExpression = findViewById(R.id.textExpression);
        textResult = findViewById(R.id.textResult);

        int[] numberBtnIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        };

        View.OnClickListener numberListener = v -> {
            Button btn = (Button) v;
            if (isOperatorClicked) {
                currentInput = "";
                isOperatorClicked = false;
            }
            currentInput += btn.getText().toString();
            textResult.setText(currentInput);
        };

        for (int id : numberBtnIds) {
            findViewById(id).setOnClickListener(numberListener);
        }

        setOperatorListener(R.id.btnPlus, "+");
        setOperatorListener(R.id.btnMinus, "-");
        setOperatorListener(R.id.btnMultiply, "×");
        setOperatorListener(R.id.btnDivide, "÷");

        // AC
        findViewById(R.id.btnAC).setOnClickListener(v -> {
            currentInput = "";
            operator = "";
            firstValue = 0;
            textExpression.setText("");
            textResult.setText("0");
        });

        // HOME (앱 종료)
        findViewById(R.id.btnHome).setOnClickListener(v -> finish());

        // BACKSPACE
        findViewById(R.id.btnBackspace).setOnClickListener(v -> {
            if (!currentInput.isEmpty()) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                textResult.setText(currentInput.isEmpty() ? "0" : currentInput);
            }
        });

        // 괄호
        findViewById(R.id.btnOpenParen).setOnClickListener(v -> {
            currentInput += "(";
            textResult.setText(currentInput);
        });

        findViewById(R.id.btnCloseParen).setOnClickListener(v -> {
            currentInput += ")";
            textResult.setText(currentInput);
        });

        // 계산
        findViewById(R.id.btnEqual).setOnClickListener(v -> {
            if (!currentInput.isEmpty() && !operator.isEmpty()) {
                try {
                    double secondValue = Double.parseDouble(currentInput);
                    double result = 0;

                    switch (operator) {
                        case "+":
                            result = firstValue + secondValue;
                            break;
                        case "-":
                            result = firstValue - secondValue;
                            break;
                        case "×":
                            result = firstValue * secondValue;
                            break;
                        case "÷":
                            result = (secondValue != 0) ? firstValue / secondValue : 0;
                            break;
                    }

                    textResult.setText(String.valueOf(result));
                    textExpression.setText(firstValue + " " + operator + " " + secondValue + " =");
                    currentInput = String.valueOf(result);
                    operator = "";
                    isOperatorClicked = false;

                } catch (Exception e) {
                    textResult.setText("Error");
                }
            }
        });

        // +/- 버튼
        findViewById(R.id.btnSign).setOnClickListener(v -> {
            if (!currentInput.isEmpty()) {
                if (currentInput.startsWith("-")) {
                    currentInput = currentInput.substring(1);
                } else {
                    currentInput = "-" + currentInput;
                }
                textResult.setText(currentInput);
            }
        });

        // % 버튼
        findViewById(R.id.btnPercent).setOnClickListener(v -> {
            if (!currentInput.isEmpty()) {
                try {
                    double value = Double.parseDouble(currentInput) / 100.0;
                    currentInput = String.valueOf(value);
                    textResult.setText(currentInput);
                } catch (Exception e) {
                    textResult.setText("Error");
                }
            }
        });
    }

    private void setOperatorListener(int buttonId, String operatorSymbol) {
        findViewById(buttonId).setOnClickListener(v -> {
            if (!currentInput.isEmpty()) {
                try {
                    firstValue = Double.parseDouble(currentInput);
                    operator = operatorSymbol;
                    textExpression.setText(currentInput + " " + operator);
                    isOperatorClicked = true;
                } catch (Exception e) {
                    textResult.setText("Error");
                }
            }
        });
    }
}
